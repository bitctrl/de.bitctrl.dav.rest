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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.reflections.Reflections;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import de.bitctrl.dav.rest.api.model.Anzeige;
import de.bitctrl.dav.rest.api.model.AnzeigeEigenschaft;
import de.bitctrl.dav.rest.api.model.AnzeigeQuerschnitt;
import de.bitctrl.dav.rest.api.model.AnzeigeQuerschnittEigenschaft;
import de.bitctrl.dav.rest.api.model.AnzeigeQuerschnittHelligkeitsMeldung;
import de.bitctrl.dav.rest.api.model.FahrStreifen;
import de.bitctrl.dav.rest.api.model.Glaettemeldeanlage;
import de.bitctrl.dav.rest.api.model.GmaUmfelddaten;
import de.bitctrl.dav.rest.api.model.MessQuerschnitt;
import de.bitctrl.dav.rest.api.model.OnlineDatum;
import de.bitctrl.dav.rest.api.model.SystemObjekt;
import de.bitctrl.dav.rest.api.model.VerkehrsdatenKurzzeit;
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
	private final LinkedBlockingDeque<ResultData> data2Store = new LinkedBlockingDeque<>();

	/**
	 * Menge in der die {@link DataIdentification}s enthalten sind zu denen
	 * OnlineDaten versandt werden sollen.
	 */
	private final Set<DataIdentification> objects2Store = new ConcurrentSkipListSet<>();

	/**
	 * Queue für alle {@link OnlineDatum}, die nicht versand werden konnten und die
	 * später Nachversand werden sollen.
	 */
	private final LinkedBlockingDeque<OnlineDatum> data2redirect = new LinkedBlockingDeque<>();

	private final ExecutorService executor = Executors.newWorkStealingPool();
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
				objects2Store.remove(id);
			}
			neueAbmeldungen.clear();

			neueAnmeldungen.parallelStream().forEach(id -> {
				try {
					connection.subscribeReceiver(Dav2RestSender.this, id.getObject(), id.getDataDescription(),
							ReceiveOptions.delayed(), ReceiverRole.receiver());
					objects2Store.add(id);
				} catch (final Exception e) {
					// ungültige Anmeldungen ignorieren wir
				}
			});
			neueAnmeldungen.clear();
			LOGGER.info("An- und Abmeldung für Archivdatensaetze abgeschlossen - "
					+ (System.currentTimeMillis() - start) + " ms");

			executor.execute(new RestSystemObjektSender(objects2Store));
		}

	}

	private class RestSystemObjektSender implements Runnable {

		private final LinkedBlockingDeque<SystemObject> systemObjekte = new LinkedBlockingDeque<>();

		public RestSystemObjektSender(Collection<DataIdentification> objects2StoreQueue) {
			systemObjekte.addAll(objects2StoreQueue.stream().map(d -> d.getObject()).collect(Collectors.toSet()));
		}

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
		}

		private List<SystemObjekt> getObjekte(Injector injector) throws InterruptedException {
			final List<SystemObjekt> result = new ArrayList<>();
			int i = 0;

			final Set<Class<?>> annotatedWith = reflections.getTypesAnnotatedWith(DavJsonObjektConverter.class);

			SystemObject sysObj = systemObjekte.take();

			while (sysObj != null && i++ < 1000) {

				final SystemObjectType sysObjType = sysObj.getType();
				final List<Class<?>> konverterKlassen = annotatedWith.stream()
						.filter(c -> sysObjType.getPid().equals(c.getAnnotation(DavJsonObjektConverter.class).davTyp()))
						.collect(Collectors.toList());

				for (final Class<?> clazz : konverterKlassen) {
					try {
						final Object newInstance = injector.getProvider(clazz).get();

						if (newInstance instanceof DavJsonConverter) {
							result.addAll(((DavJsonConverter) newInstance).dav2Json(sysObj));
						}
					} catch (final Exception e) {
						LOGGER.error("Instanziierung der Klasse " + clazz + " fehlgeschlagen.", e);
					}
				}

				if (systemObjekte.isEmpty()) {
					break;
				}
				sysObj = systemObjekte.take();
			}
			return result;

		}

		private void versendeSystemObjekte(Injector injector) {
			while (!systemObjekte.isEmpty()) {
				try {
					final List<SystemObjekt> liste = getObjekte(injector);
					versendeFahrStreifen(liste);
					versendeMessQuerschnitte(liste);
					versendeAnzeigen(liste);
					versendeAnzeigeQuerschnitte(liste);
					versendeGlaetteMeldeAnlagen(liste);
				} catch (final InterruptedException e) {
					LOGGER.error("DAV Objekte konnten nicht versendet werden.", e);
				}
			}
		}

		private void versendeFahrStreifen(final List<SystemObjekt> liste) {
			try {
				final List<SystemObjekt> anzeigequerschnitte = liste.stream().filter(o -> o instanceof FahrStreifen)
						.collect(Collectors.toList());
				if (!anzeigequerschnitte.isEmpty()) {
					target.path("/systemobjekte/fahrstreifen").request()
							.post(Entity.entity(anzeigequerschnitte, MediaType.APPLICATION_JSON));
				}
			} catch (final Exception ex) {
				LOGGER.error("FahrStreifen konnten nicht versendet werden.", ex);
			}
		}

		private void versendeAnzeigeQuerschnitte(final List<SystemObjekt> liste) {
			try {
				final List<SystemObjekt> anzeigequerschnitte = liste.stream()
						.filter(o -> o instanceof AnzeigeQuerschnitt).collect(Collectors.toList());
				if (!anzeigequerschnitte.isEmpty()) {
					target.path("/systemobjekte/anzeigequerschnitt").request()
							.post(Entity.entity(anzeigequerschnitte, MediaType.APPLICATION_JSON));
				}
			} catch (final Exception ex) {
				LOGGER.error("AnzeigeQuerschnitte konnten nicht versendet werden.", ex);
			}
		}

		private void versendeAnzeigen(final List<SystemObjekt> liste) {
			try {
				final List<SystemObjekt> anzeigen = liste.stream().filter(o -> o instanceof Anzeige)
						.collect(Collectors.toList());
				if (!anzeigen.isEmpty()) {
					target.path("/systemobjekte/anzeige").request()
							.post(Entity.entity(anzeigen, MediaType.APPLICATION_JSON));
				}
			} catch (final Exception ex) {
				LOGGER.error("Anzeigen konnten nicht versendet werden.", ex);
			}
		}

		private void versendeMessQuerschnitte(final List<SystemObjekt> liste) {
			try {
				final List<SystemObjekt> messquerschnitte = liste.stream().filter(o -> o instanceof MessQuerschnitt)
						.collect(Collectors.toList());
				if (!messquerschnitte.isEmpty()) {
					target.path("/systemobjekte/messquerschnitt").request()
							.post(Entity.entity(messquerschnitte, MediaType.APPLICATION_JSON));
				}
			} catch (final Exception ex) {
				LOGGER.error("MessQuerschnitte konnten nicht versendet werden.", ex);
			}
		}
		
		private void versendeGlaetteMeldeAnlagen(final List<SystemObjekt> liste) {
			try {
				final List<SystemObjekt> gmas = liste.stream().filter(o -> o instanceof Glaettemeldeanlage)
						.collect(Collectors.toList());
				if (!gmas.isEmpty()) {
					target.path("/systemobjekte/glaettemeldeanlage").request()
							.post(Entity.entity(gmas, MediaType.APPLICATION_JSON));
				}
			} catch (final Exception ex) {
				LOGGER.error("GMA's konnten nicht versendet werden.", ex);
			}
		}

	}

	/**
	 * {@link Runnable} zum Versenden der {@link OnlineDatum}.
	 *
	 * @author BitCtrl Systems GmbH, Christian Hösel
	 *
	 */
	private class RestOnlineDatenSender implements Runnable {

		@Override
		public void run() {
			// Injector für das ClientDavInterface
			final Injector injector = Guice.createInjector(new AbstractModule() {
				@Override
				protected void configure() {
					bind(ClientDavInterface.class).toInstance(connection);
				}
			});
			versendeDatensaetze(injector);
		}

		private void versendeDatensaetze(Injector injector) {
			while (!data2Store.isEmpty()) {
				final List<OnlineDatum> liste = new ArrayList<>();
				try {
					liste.addAll(getOnlineDaten(injector));
					versendeMQVerkehrsDatenKurzzeit(liste);
					versendeAnzeigeEigenschaften(liste);
					versendeAQAnzeigeEigenschaften(liste);
					versendeAQHelligkeitsMeldungen(liste);
					versendeGMAUmfelddaten(liste);
				} catch (final Exception ex) {
					LOGGER.error("OnlineDaten konnten nicht versendet werden und werden später Nachversandt.", ex);
					data2redirect.addAll(liste);
				}
			}
		}

		private List<OnlineDatum> getOnlineDaten(Injector injector) throws InterruptedException {
			final List<OnlineDatum> result = new ArrayList<>();
			int i = 0;

			final Set<Class<?>> annotatedWith = reflections.getTypesAnnotatedWith(DavJsonDatensatzConverter.class);

			ResultData resultData = data2Store.take();

			while (resultData != null && i++ < 1000) {

				final AttributeGroup atg = resultData.getDataDescription().getAttributeGroup();
				final List<Class<?>> converterKlassen = annotatedWith.stream()
						.filter(c -> Arrays.asList(c.getAnnotation(DavJsonDatensatzConverter.class).davAttributGruppe())
								.contains(atg.getPid()))
						.collect(Collectors.toList());

				for (final Class<?> clazz : converterKlassen) {
					try {
						final Object newInstance = injector.getProvider(clazz).get();

						if (newInstance instanceof DavJsonConverter) {
							result.addAll(((DavJsonConverter) newInstance).dav2Json(resultData));
						}
					} catch (final Exception e) {
						LOGGER.error("Instanziierung und Konvertierung der Klasse " + clazz
								+ " fehlgeschlagen (ResultData: " + resultData + ").", e);
					}
				}

				if (data2Store.isEmpty()) {
					break;
				}
				resultData = data2Store.take();
			}
			return result;
		}

	}

	/**
	 * Sender zum nachsenden der {@link OnlineDatum}, deren Versand fehlgeschlagen
	 * ist.
	 *
	 * @author BitCtrl Systems GmbH, Christian Hoesel
	 *
	 */
	private class RestOnlineDatenNachSender implements Runnable {

		@Override
		public void run() {
			// Injector für das ClientDavInterface
			final Injector injector = Guice.createInjector(new AbstractModule() {
				@Override
				protected void configure() {
					bind(ClientDavInterface.class).toInstance(connection);
				}
			});
			versendeDatensaetze(injector);
		}

		private void versendeDatensaetze(Injector injector) {
			if(!data2redirect.isEmpty()) {
				LOGGER.info("Zyklisches Nachsenden von dynamischen Daten (OnlineDatum) gestartet - "
						+ data2redirect.size() + " Datensätze.");
			}
			
			while (!data2redirect.isEmpty()) {
				final List<OnlineDatum> liste = new ArrayList<>();
				try {
					data2redirect.drainTo(liste, 1000);
					versendeMQVerkehrsDatenKurzzeit(liste);
					versendeAnzeigeEigenschaften(liste);
					versendeAQAnzeigeEigenschaften(liste);
					versendeAQHelligkeitsMeldungen(liste);
					versendeGMAUmfelddaten(liste);
				} catch (final Exception ex) {
					LOGGER.error("OnlineDaten konnten nicht (nach)versendet werden.", ex);
					data2redirect.addAll(liste);
					break;
				}
			}
		}

	}

	public void anmelden() {
		reflections = new Reflections(Dav2RestSender.class.getPackage().getName());
		final Set<Class<?>> annotatedWith = reflections.getTypesAnnotatedWith(DavJsonObjektConverter.class);

		LOGGER.info("Folgende Objekt-Converter wurden via Reflection gefunden: " + annotatedWith);

		subscribeDavData();

		final LocalDateTime now = LocalDateTime.now();
		LocalDateTime twoOclock = now.withHour(2).withMinute(0).withSecond(0);

		long until = now.until(twoOclock, ChronoUnit.MINUTES);
		while (until < 0) {
			twoOclock = twoOclock.plusDays(1);
			until = now.until(twoOclock, ChronoUnit.MINUTES);
		}

		// täglich ca. um 2 werden die statischen Daten (SystemObjekte) neu übertragen.
		final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutor.scheduleAtFixedRate(() -> {
			LOGGER.info("Zyklisches versenden statischer Daten (SystemOjekte) gestartet.");
			executor.execute(new RestSystemObjektSender(objects2Store));
		}, until, 1440, TimeUnit.MINUTES);

		scheduledExecutor.scheduleAtFixedRate(new RestOnlineDatenNachSender(), 1, 1, TimeUnit.MINUTES);

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
					data2Store.add(rd);
				}
				executor.execute(new RestOnlineDatenSender());
			} catch (final Exception e) {
				LOGGER.error("Archiv kann Datensatz nicht der Warteschlange hinzufügen.", e);
			}
		}
	}

	private void versendeAQHelligkeitsMeldungen(final List<OnlineDatum> liste) {
		final List<OnlineDatum> aqHelligkeitsMeldungen = liste.stream()
				.filter(o -> o instanceof AnzeigeQuerschnittHelligkeitsMeldung).collect(Collectors.toList());
		if (!aqHelligkeitsMeldungen.isEmpty()) {
			final Response response = target.path("/onlinedaten/anzeigequerschnitthelligkeitsmeldung").request()
					.post(Entity.entity(aqHelligkeitsMeldungen, MediaType.APPLICATION_JSON));
			if (response.getStatus() < 200 || response.getStatus() >= 300) {
				LOGGER.error(
						"Der Versand von AnzeigeQuerschnittHelligkeitsMeldungen ist fehlgeschlagen und wird per Nachversand erneut versucht. ",
						response);
				data2redirect.addAll(aqHelligkeitsMeldungen);
			}
		}
	}

	private void versendeAQAnzeigeEigenschaften(final List<OnlineDatum> liste) {
		final List<OnlineDatum> anzeigeQuerschnittEigenschaften = liste.stream()
				.filter(o -> o instanceof AnzeigeQuerschnittEigenschaft).collect(Collectors.toList());
		if (!anzeigeQuerschnittEigenschaften.isEmpty()) {
			final Response response = target.path("/onlinedaten/anzeigequerschnitteigenschaft").request()
					.post(Entity.entity(anzeigeQuerschnittEigenschaften, MediaType.APPLICATION_JSON));
			if (response.getStatus() < 200 || response.getStatus() >= 300) {
				LOGGER.error(
						"Der Versand von AnzeigeQuerschnittEigenschaften ist fehlgeschlagen und wird per Nachversand erneut versucht. ",
						response);
				data2redirect.addAll(anzeigeQuerschnittEigenschaften);
			}
		}
	}

	private void versendeAnzeigeEigenschaften(final List<OnlineDatum> liste) {
		final List<OnlineDatum> anzeigeEigenschaften = liste.stream().filter(o -> o instanceof AnzeigeEigenschaft)
				.collect(Collectors.toList());
		if (!anzeigeEigenschaften.isEmpty()) {
			final Response response = target.path("/onlinedaten/anzeigeeigenschaft").request()
					.post(Entity.entity(anzeigeEigenschaften, MediaType.APPLICATION_JSON));
			if (response.getStatus() < 200 || response.getStatus() >= 300) {
				LOGGER.error(
						"Der Versand von AnzeigeEigenschaften ist fehlgeschlagen und wird per Nachversand erneut versucht. ",
						response);
				data2redirect.addAll(anzeigeEigenschaften);
			}
		}
	}

	private void versendeMQVerkehrsDatenKurzzeit(final List<OnlineDatum> liste) {
		final List<OnlineDatum> verkehrsdatenKurzzeit = liste.stream().filter(o -> o instanceof VerkehrsdatenKurzzeit)
				.collect(Collectors.toList());
		if (!verkehrsdatenKurzzeit.isEmpty()) {
			final Response response = target.path("/onlinedaten/verkehrsdatenkurzzeit").request()
					.post(Entity.entity(verkehrsdatenKurzzeit, MediaType.APPLICATION_JSON));
			if (response.getStatus() < 200 || response.getStatus() >= 300) {
				LOGGER.error(
						"Der Versand von kurzzeit Verkehrsdaten ist fehlgeschlagen und wird per Nachversand erneut versucht. ",
						response);
				data2redirect.addAll(verkehrsdatenKurzzeit);
			}
		}
	}
	
	private void versendeGMAUmfelddaten(final List<OnlineDatum> liste) {
		final List<OnlineDatum> umfelddaten = liste.stream().filter(o -> o instanceof GmaUmfelddaten)
				.collect(Collectors.toList());
		if (!umfelddaten.isEmpty()) {
			final Response response = target.path("/onlinedaten/gmaumfelddaten").request()
					.post(Entity.entity(umfelddaten, MediaType.APPLICATION_JSON));
			if (response.getStatus() < 200 || response.getStatus() >= 300) {
				LOGGER.error(
						"Der Versand von GMA Umfelddaten ist fehlgeschlagen und wird per Nachversand erneut versucht. ",
						response);
				data2redirect.addAll(umfelddaten);
			}
		}
	}
}
