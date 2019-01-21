package de.bitctrl.dav.rest.client;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
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

import de.bitctrl.dav.rest.api.SystemObjekt;
import de.bitctrl.dav.rest.client.annotations.DavJsonObjektConverter;
import de.bitctrl.dav.rest.client.converter.DavJsonConverter;
import de.bitctrl.dav.rest.client.converter.SystemObjectJsonConverter;
import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.ClientReceiverInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.ReceiveOptions;
import de.bsvrz.dav.daf.main.ReceiverRole;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.config.Aspect;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.AttributeGroupUsage;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.dataIdentificationSettings.DataIdentification;
import de.bsvrz.sys.funclib.dataIdentificationSettings.EndOfSettingsListener;
import de.bsvrz.sys.funclib.dataIdentificationSettings.SettingsManager;
import de.bsvrz.sys.funclib.dataIdentificationSettings.UpdateListener;
import de.bsvrz.sys.funclib.debug.Debug;

public class Dav2RestSender implements ClientReceiverInterface {

	private WebTarget target;
	private SystemObject archivObjekt;
	private ClientDavInterface connection;

	private static final Debug LOGGER = Debug.getLogger();

	/**
	 * Queue in der die zu persistierenden ResultDatas gehalten werden.
	 */
	private LinkedBlockingDeque<ResultData> data2StoreList = new LinkedBlockingDeque<ResultData>();

	/**
	 * Queue in der die SystemObjecte enthalten sind, deren Mengen persistiert
	 * werden sollen.
	 */
	private LinkedBlockingDeque<SystemObject> objects2StoreList = new LinkedBlockingDeque<SystemObject>();

	private ExecutorService executor = Executors.newSingleThreadExecutor();
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

		private List<DataIdentification> neueAnmeldungen = new ArrayList<DataIdentification>();
		private List<DataIdentification> neueAbmeldungen = new ArrayList<DataIdentification>();

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

			boolean archivieren = newSettings.getUnscaledValue("Archivieren").intValue() > 0;

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
			long start = System.currentTimeMillis();

			for (DataIdentification id : neueAbmeldungen) {
				connection.unsubscribeReceiver(Dav2RestSender.this, id.getObject(), id.getDataDescription());
			}
			neueAbmeldungen.clear();

			for (DataIdentification id : neueAnmeldungen) {
				try {
					connection.subscribeReceiver(Dav2RestSender.this, id.getObject(), id.getDataDescription(),
							ReceiveOptions.delayed(), ReceiverRole.receiver());
					konfigurationPersistieren(id.getObject());
					mengenPersistieren(id.getObject());
				} catch (Exception e) {
					// TODO: Fehlerbehandlung oder ignorieren!?!?
					// LOGGER.warning("Anmeldung als Empfänger für "+id+" ist fehlgeschlagen.",
					// e);
				}
			}
			neueAnmeldungen.clear();
			LOGGER.info("An- und Abmeldung für Archivdatensaetze abgeschlossen - "
					+ (System.currentTimeMillis() - start) + " ms");
		}

		private void mengenPersistieren(SystemObject object) {
			objects2StoreList.add(object);
		}

		/**
		 * Konfigurationsdaten auslesen und zur Persistierung vormerken.
		 */
		private void konfigurationPersistieren(SystemObject object) {
			for (AttributeGroupUsage usage : object.getUsedAttributeGroupUsages()) {
				AttributeGroup attributeGroup = usage.getAttributeGroup();
				Aspect asp = usage.getAspect();

				Data data = object.getConfigurationData(attributeGroup);

				ResultData rd = new ResultData(object, new DataDescription(attributeGroup, asp),
						System.currentTimeMillis(), data);
				data2StoreList.add(rd);
			}
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

			while (!objects2StoreList.isEmpty()) {

				// Injector für das ClientDavInterface
				Injector injector = Guice.createInjector(new AbstractModule() {
					@Override
					protected void configure() {
						bind(ClientDavInterface.class).toInstance(connection);
					}
				});
				try {
					List<SystemObjekt> liste = getObjekte(injector);
					target.path("/systemobjekte").request().post(Entity.entity(liste, MediaType.APPLICATION_JSON));
				} catch (Exception ex) {
					LOGGER.error("Dav Objekte konnten nicht versendet werden.", ex);
				} finally {

				}

				// LOGGER.info("Warteschlange = " + data2StoreList.size());
			}

		}

		private List<SystemObjekt> getObjekte(Injector injector) throws InterruptedException {
			List<SystemObjekt> result = new ArrayList<>();
			int i = 0;

			Set<Class<?>> annotatedWith = reflections.getTypesAnnotatedWith(DavJsonObjektConverter.class);

			SystemObject sysObj = objects2StoreList.take();

			while (sysObj != null && i++ < 500) {

				for (Class<?> clazz : annotatedWith) {
					DavJsonObjektConverter annotation = ((DavJsonObjektConverter) clazz
							.getAnnotation(DavJsonObjektConverter.class));
					if (annotation == null) {
						SystemObjectJsonConverter convert = new SystemObjectJsonConverter();
						result.add(convert.dav2Json(sysObj));
					} else if (sysObj.getType().getPid().equals(annotation.davTyp())) {

						try {
							Constructor<?> constructor = clazz.getConstructor();
							Object newInstance = constructor.newInstance();

							// Wenn die Klasse einen EntityManager oder
							// ClientDacInterface Injected, dann werden die hier
							// initialisiert.
							injector.injectMembers(newInstance);
							injector.getProvider(clazz).get();

							if (newInstance instanceof DavJsonConverter) {
								result.add((SystemObjekt) ((DavJsonConverter) newInstance).dav2Json(sysObj));
							}
						} catch (NoSuchMethodException | SecurityException e) {
							LOGGER.error("Es wurde kein Konstruktor für die Klasse " + clazz
									+ " mit passenden Parametern gefunden.", e);
						} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
								| InvocationTargetException e) {
							LOGGER.error("Instanziierung der Klasse " + clazz + " fehlgeschlagen.", e);
						}
					}
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
//		LOGGER.info("Suche Entities im Package : "
//				+ Archivator.class.getPackage().getName());

		reflections = new Reflections(Dav2RestSender.class.getPackage().getName());
		Set<Class<?>> annotatedWith = reflections.getTypesAnnotatedWith(DavJsonObjektConverter.class);

		LOGGER.info("Folgende Objekt-Converter wurden via Reflection gefunden: " + annotatedWith);
//
//		DBOjectStorageThread objectStorageThread = new DBOjectStorageThread();
//		objectStorageThread.start();
//
//		DBStorageThread dataStorageThread = new DBStorageThread();
//		dataStorageThread.start();

		subscribeDavData();
	}

	private void subscribeDavData() {

		AttributeGroup atgArchiv = connection.getDataModel().getAttributeGroup("atg.archiv");
		Aspect aspParameterSoll = connection.getDataModel().getAspect("asp.parameterSoll");
		DataDescription desc = new DataDescription(atgArchiv, aspParameterSoll);
		DataIdentification dataId = new DataIdentification(archivObjekt, desc);
		SettingsManager settingsManager = new SettingsManager(connection, dataId);

		SettingsManagerUpdateListener settingsManagerUpdateListener = new SettingsManagerUpdateListener();
		settingsManager.addUpdateListener(settingsManagerUpdateListener);
		settingsManager.addEndOfSettingsListener(settingsManagerUpdateListener);
		settingsManager.start();
		LOGGER.info("Anmeldung am Datenverteiler abgeschlossen, jetzt gehts los...");
	}

	@Override
	public void update(ResultData[] results) {
		if (results != null) {
			try {
				for (ResultData rd : results) {
					data2StoreList.add(rd);
				}
				executor.execute(new RestSenderRunnable());
			} catch (Exception e) {
				LOGGER.error("Archiv kann Datensatz nicht der Warteschlange hinzufügen.", e);
			}
		}

	}
}
