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

			neueAnmeldungen.parallelStream().forEach(id -> {
				try {
					connection.subscribeReceiver(Dav2RestSender.this, id.getObject(), id.getDataDescription(),
							ReceiveOptions.delayed(), ReceiverRole.receiver());
					objects2StoreList.add(id.getObject());
				} catch (Exception e) {
					// geht halt nich
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
			Injector injector = Guice.createInjector(new AbstractModule() {
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
					List<OnlineDatum> liste = getOnlineDaten(injector);
					if (!liste.isEmpty()) {
						target.path("/onlinedaten").request().post(Entity.entity(liste, MediaType.APPLICATION_JSON));
					}
				} catch (Exception ex) {
					LOGGER.error("OnlineDaten konnten nicht versendet werden.", ex);
				} finally {

				}

				// LOGGER.info("Warteschlange = " + data2StoreList.size());
			}
		}

		private List<OnlineDatum> getOnlineDaten(Injector injector) throws InterruptedException {
			List<OnlineDatum> result = new ArrayList<>();
			int i = 0;

			Set<Class<?>> annotatedWith = reflections.getTypesAnnotatedWith(DavJsonDatensatzConverter.class);

			ResultData resultData = data2StoreList.take();

			while (resultData != null && i++ < 1000) {

				AttributeGroup atg = resultData.getDataDescription().getAttributeGroup();
				Optional<Class<?>> findFirst = annotatedWith.stream().filter(
						c -> atg.getPid().equals(c.getAnnotation(DavJsonDatensatzConverter.class).davAttributGruppe()))
						.findFirst();

				if (findFirst.isPresent()) {
					Class<?> clazz = findFirst.get();
					try {
						Object newInstance = injector.getProvider(clazz).get();

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
					List<SystemObjekt> liste = getObjekte(injector);
					if (!liste.isEmpty()) {
						target.path("/systemobjekte").request().post(Entity.entity(liste, MediaType.APPLICATION_JSON));
					}
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

			while (sysObj != null && i++ < 1000) {

				SystemObjectType sysObjType = sysObj.getType();
				Optional<Class<?>> findFirst = annotatedWith.stream()
						.filter(c -> sysObjType.getPid().equals(c.getAnnotation(DavJsonObjektConverter.class).davTyp()))
						.findFirst();

				if (findFirst.isPresent()) {
					Class<?> clazz = findFirst.get();
					try {
						Object newInstance = injector.getProvider(clazz).get();

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
		Set<Class<?>> annotatedWith = reflections.getTypesAnnotatedWith(DavJsonObjektConverter.class);

		LOGGER.info("Folgende Objekt-Converter wurden via Reflection gefunden: " + annotatedWith);

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
