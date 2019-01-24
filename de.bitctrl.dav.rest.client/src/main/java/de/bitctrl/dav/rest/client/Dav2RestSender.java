/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package de.bitctrl.dav.rest.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.reflections.Reflections;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import de.bitctrl.dav.rest.api.OnlineDatum;
import de.bitctrl.dav.rest.api.SystemObjekt;
import de.bitctrl.dav.rest.client.annotations.DavJsonDatensatzConverter;
import de.bitctrl.dav.rest.client.annotations.DavJsonObjektConverter;
import de.bitctrl.dav.rest.client.converter.DavJsonConverter;
import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.ClientReceiverInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.ReceiveOptions;
import de.bsvrz.dav.daf.main.ReceiverRole;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.config.Aspect;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.dav.daf.main.config.SystemObjectType;
import de.bsvrz.sys.funclib.dataIdentificationSettings.DataIdentification;
import de.bsvrz.sys.funclib.dataIdentificationSettings.EndOfSettingsListener;
import de.bsvrz.sys.funclib.dataIdentificationSettings.SettingsManager;
import de.bsvrz.sys.funclib.dataIdentificationSettings.UpdateListener;
import de.bsvrz.sys.funclib.debug.Debug;

/**
 * Controller Klasse für die Anmeldung am DAV und das Versenden via HTTP REST
 * API.
 * 
 * @author BitCtrl Systems GmbH, ChHoesel
 *
 */
public class Dav2RestSender implements ClientReceiverInterface {

	private final WebTarget target;
	private final SystemObject archivObjekt;
	private final ClientDavInterface connection;

	private static final Debug LOGGER = Debug.getLogger();

	/**
	 * Queue in der die zu persistierenden ResultDatas gehalten werden.
	 */
	private final LinkedBlockingDeque<ResultData> data2StoreList = new LinkedBlockingDeque<>();

	/**
	 * Queue in der die SystemObjecte enthalten sind, deren Mengen persistiert
	 * werden sollen.
	 */
	private final LinkedBlockingDeque<SystemObject> objects2StoreList = new LinkedBlockingDeque<>();

	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private Reflections reflections;

	public Dav2RestSender(WebTarget target, ClientDavInterface connection, SystemObject archivObjekt) {
		this.target = target;
		this.archivObjekt = archivObjekt;
		this.connection = connection;
	}

	/**
	 * Reagiert bei Änderung der Archivparameter und organisiert die An- und
	 * Abmeldungen für zu archivierende Datensätze.
	 *
	 * @author BitCtrl Systems GmbH, Christian Hösel
	 *
	 */
	private final class SettingsManagerUpdateListener implements UpdateListener, EndOfSettingsListener {

		private final List<DataIdentification> neueAnmeldungen = new ArrayList<>();
		private final List<DataIdentification> neueAbmeldungen = new ArrayList<>();

		@Override
		public synchronized void update(DataIdentification dataIdentification, Data oldSettings, Data newSettings) {
			if (oldSettings != null && newSettings == null) {
				archivParameterEntfernt(dataIdentification);
			} else {
				neueArchivParameter(dataIdentification, newSettings);
			}
		}

		private void archivParameterEntfernt(DataIdentification dataIdentification) {
			neueAbmeldungen.add(dataIdentification);
		}

		private void neueArchivParameter(DataIdentification dataIdentification, Data newSettings) {

			final boolean archivieren = newSettings.getUnscaledValue("Archivieren").intValue() > 0;

			if (archivieren) {
				neueAnmeldungen.add(dataIdentification);
			} else {
				neueAbmeldungen.add(dataIdentification);
			}
		}

		@Override
		public synchronized void inform() {
			LOGGER.info(
					"Neue Archivparameter eingelesen - Beginne mit Anmeldung/Ummeldung für Archivparameter. Es werden "
							+ neueAnmeldungen.size() + " Anmeldungen und " + neueAbmeldungen.size()
							+ " Abmeldungen vorgenommen.");
			final long start = System.currentTimeMillis();

			for (final DataIdentification id : neueAbmeldungen) {
				connection.unsubscribeReceiver(Dav2RestSender.this, id.getObject(), id.getDataDescription());
			}
			neueAbmeldungen.clear();

			neueAnmeldungen.parallelStream().forEach(id -> {
				try {
					connection.subscribeReceiver(Dav2RestSender.this, id.getObject(), id.getDataDescription(),
							ReceiveOptions.delayed(), ReceiverRole.receiver());
					objects2StoreList.add(id.getObject());
				} catch (final Exception e) {
					// ungültige Anmeldungen ignorieren wir
				}
			});
			neueAnmeldungen.clear();
			LOGGER.info("An- und Abmeldung für Archivdatensaetze abgeschlossen - "
					+ (System.currentTimeMillis() - start) + " ms");
			executor.execute(new RestSenderRunnable());
		}

	}

	/**
	 * {@link Runnable} zum asynchronen Versenden der SystemObjecte und Mengen.
	 *
	 * @author BitCtrl Systems GmbH, Christian Hösel
	 *
	 */
	private class RestSenderRunnable implements Runnable {

		@Override
		public void run() {
			// Injector für das ClientDavInterface
			final Injector injector = Guice.createInjector(new AbstractModule() {
				@Override
				protected void configure() {
					bind(ClientDavInterface.class).toInstance(connection);
				}
			});

			versendeSystemObjekte(injector);
			versendeDatensaetze(injector);

		}

		private void versendeDatensaetze(Injector injector) {
			while (!data2StoreList.isEmpty()) {

				try {
					final List<OnlineDatum> liste = getOnlineDaten(injector);
					if (!liste.isEmpty()) {
						target.path("/onlinedaten").request().post(Entity.entity(liste, MediaType.APPLICATION_JSON));
					}
				} catch (final Exception ex) {
					LOGGER.error("OnlineDaten konnten nicht versendet werden.", ex);
				} finally {

				}

				// LOGGER.info("Warteschlange = " + data2StoreList.size());
			}
		}

		private List<OnlineDatum> getOnlineDaten(Injector injector) throws InterruptedException {
			final List<OnlineDatum> result = new ArrayList<>();
			int i = 0;

			final Set<Class<?>> annotatedWith = reflections.getTypesAnnotatedWith(DavJsonDatensatzConverter.class);

			ResultData resultData = data2StoreList.take();

			while (resultData != null && i++ < 1000) {

				final AttributeGroup atg = resultData.getDataDescription().getAttributeGroup();
				final Optional<Class<?>> findFirst = annotatedWith.stream().filter(
						c -> atg.getPid().equals(c.getAnnotation(DavJsonDatensatzConverter.class).davAttributGruppe()))
						.findFirst();

				if (findFirst.isPresent()) {
					final Class<?> clazz = findFirst.get();
					try {
						final Object newInstance = injector.getProvider(clazz).get();

						if (newInstance instanceof DavJsonConverter) {
							result.add((OnlineDatum) ((DavJsonConverter) newInstance).dav2Json(resultData));
						}
					} catch (final Exception e) {
						LOGGER.error("Instanziierung und Konvertierung der Klasse " + clazz
								+ " fehlgeschlagen (ResultData: " + resultData + ").", e);
					}
				}

				if (data2StoreList.isEmpty()) {
					break;
				}
				resultData = data2StoreList.take();
			}
			return result;
		}

		private void versendeSystemObjekte(Injector injector) {
			while (!objects2StoreList.isEmpty()) {

				try {
					final List<SystemObjekt> liste = getObjekte(injector);
					if (!liste.isEmpty()) {
						target.path("/systemobjekte").request().post(Entity.entity(liste, MediaType.APPLICATION_JSON));
					}
				} catch (final Exception ex) {
					LOGGER.error("Dav Objekte konnten nicht versendet werden.", ex);
				} finally {

				}

				// LOGGER.info("Warteschlange = " + data2StoreList.size());
			}
		}

		private List<SystemObjekt> getObjekte(Injector injector) throws InterruptedException {
			final List<SystemObjekt> result = new ArrayList<>();
			int i = 0;

			final Set<Class<?>> annotatedWith = reflections.getTypesAnnotatedWith(DavJsonObjektConverter.class);

			SystemObject sysObj = objects2StoreList.take();

			while (sysObj != null && i++ < 1000) {

				final SystemObjectType sysObjType = sysObj.getType();
				final Optional<Class<?>> findFirst = annotatedWith.stream()
						.filter(c -> sysObjType.getPid().equals(c.getAnnotation(DavJsonObjektConverter.class).davTyp()))
						.findFirst();

				if (findFirst.isPresent()) {
					final Class<?> clazz = findFirst.get();
					try {
						final Object newInstance = injector.getProvider(clazz).get();

						if (newInstance instanceof DavJsonConverter) {
							result.add((SystemObjekt) ((DavJsonConverter) newInstance).dav2Json(sysObj));
						}
					} catch (final Exception e) {
						LOGGER.error("Instanziierung der Klasse " + clazz + " fehlgeschlagen.", e);
					}
				} else {
//					SystemObjectJsonConverter convert = new SystemObjectJsonConverter();
//					result.add(convert.dav2Json(sysObj));
				}

				if (objects2StoreList.isEmpty()) {
					break;
				}
				sysObj = objects2StoreList.take();
			}
			return result;

		}

	}

	public void anmelden() {
		reflections = new Reflections(Dav2RestSender.class.getPackage().getName());
		final Set<Class<?>> annotatedWith = reflections.getTypesAnnotatedWith(DavJsonObjektConverter.class);

		LOGGER.info("Folgende Objekt-Converter wurden via Reflection gefunden: " + annotatedWith);

		subscribeDavData();
	}

	private void subscribeDavData() {

		final AttributeGroup atgArchiv = connection.getDataModel().getAttributeGroup("atg.archiv");
		final Aspect aspParameterSoll = connection.getDataModel().getAspect("asp.parameterSoll");
		final DataDescription desc = new DataDescription(atgArchiv, aspParameterSoll);
		final DataIdentification dataId = new DataIdentification(archivObjekt, desc);
		final SettingsManager settingsManager = new SettingsManager(connection, dataId);

		final SettingsManagerUpdateListener settingsManagerUpdateListener = new SettingsManagerUpdateListener();
		settingsManager.addUpdateListener(settingsManagerUpdateListener);
		settingsManager.addEndOfSettingsListener(settingsManagerUpdateListener);
		settingsManager.start();
		LOGGER.info("Anmeldung am Datenverteiler abgeschlossen, jetzt gehts los...");
	}

	@Override
	public void update(ResultData[] results) {
		if (results != null) {
			try {
				for (final ResultData rd : results) {
					data2StoreList.add(rd);
				}
				executor.execute(new RestSenderRunnable());
			} catch (final Exception e) {
				LOGGER.error("Archiv kann Datensatz nicht der Warteschlange hinzufügen.", e);
			}
		}

	}
}
