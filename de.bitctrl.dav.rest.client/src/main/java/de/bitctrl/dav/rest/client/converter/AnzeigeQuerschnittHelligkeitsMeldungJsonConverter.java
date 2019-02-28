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
package de.bitctrl.dav.rest.client.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.bitctrl.dav.rest.api.model.AnzeigeQuerschnittHelligkeitsMeldung;
import de.bitctrl.dav.rest.api.model.AnzeigeQuerschnittHelligkeitsMeldungImpl;
import de.bitctrl.dav.rest.client.annotations.DavJsonDatensatzConverter;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.ConfigurationObject;
import de.bsvrz.dav.daf.main.config.DataModel;
import de.bsvrz.dav.daf.main.config.SystemObject;

/**
 * Konverter von {@link ResultData} einer DEUfd in
 * {@link AnzeigeQuerschnittHelligkeitsMeldung}.
 *
 * @author BitCtrl Systems GmbH, ChHoesel
 */
@DavJsonDatensatzConverter(davAttributGruppe = { "atg.tlsUfdErgebnisMeldungHelligkeitHK" })
public class AnzeigeQuerschnittHelligkeitsMeldungJsonConverter
		implements DavJsonConverter<ResultData, AnzeigeQuerschnittHelligkeitsMeldung> {

	@Override
	public Collection<AnzeigeQuerschnittHelligkeitsMeldung> dav2Json(ResultData resultData) {

		final SystemObject systemObject = resultData.getObject();
		final List<AnzeigeQuerschnittHelligkeitsMeldung> meldungen = findeAnzeigeQuerschnitte(systemObject).stream()
				.map(s -> erzeugeLeereAQHelligkeitsMeldung(s)).collect(Collectors.toList());

		meldungen.stream().forEach(m -> {
			m.setZeitstempel(new Date(resultData.getDataTime()));
			m.setDatenStatus(resultData.getDataState().toString());
		});
		final Data data = resultData.getData();
		if (data != null) {
			final double value = data.getUnscaledValue("Helligkeit").doubleValue();
			meldungen.stream().forEach(m -> m.setHelligkeit(value));
		}

		return meldungen;
	}

	private AnzeigeQuerschnittHelligkeitsMeldung erzeugeLeereAQHelligkeitsMeldung(SystemObject anzeigeQuerschnitt) {
		final AnzeigeQuerschnittHelligkeitsMeldung meldung = new AnzeigeQuerschnittHelligkeitsMeldungImpl();
		meldung.setSystemObjektId(anzeigeQuerschnitt.getPid());
		return meldung;
	}

	/**
	 * Sucht alle AnzeigeQuerschnitte, zu denendie übergebene DeUFD gehört.
	 *
	 */
	private List<SystemObject> findeAnzeigeQuerschnitte(SystemObject systemObject) {
		if (systemObject.isOfType("typ.deUfd")) {

			final DataModel dataModel = systemObject.getDataModel();
			final List<SystemObject> alleEaks = dataModel.getType("typ.eak").getElements();

			final Optional<SystemObject> eakOptional = alleEaks.stream()
					.filter(eak -> ((ConfigurationObject) eak).getObjectSet("De") != null
							&& ((ConfigurationObject) eak).getObjectSet("De").getElements().contains(systemObject))
					.findFirst();
			if (eakOptional.isPresent()) {
				final SystemObject eak = eakOptional.get();
				final List<SystemObject> alleSteuermodule = dataModel.getType("typ.steuerModul").getElements();

				final Optional<SystemObject> steuerModulOptional = alleSteuermodule.stream()
						.filter(s -> ((ConfigurationObject) s).getObjectSet("Eak") != null
								&& ((ConfigurationObject) s).getObjectSet("Eak").getElements().contains(eak))
						.findFirst();
				if (steuerModulOptional.isPresent()) {
					final ConfigurationObject steuermodul = (ConfigurationObject) steuerModulOptional.get();
					final List<SystemObject> clusterDe = findeClusterDes(dataModel, steuermodul);

					if (!clusterDe.isEmpty()) {
						return findeAqAktoren(dataModel, clusterDe);
					}
				}
			}
		}
		return new ArrayList<>();
	}

	/**
	 * Findet alle Cluster-DE an einem Steuermodul.
	 */
	private List<SystemObject> findeClusterDes(DataModel dataModel, ConfigurationObject steuermodul) {
		final AttributeGroup atgDe = dataModel.getAttributeGroup("atg.de");
		final List<SystemObject> clusterDe = steuermodul.getObjectSet("Eak").getElements().stream()
				.filter(e -> ((ConfigurationObject) e).getObjectSet("De") != null)
				.flatMap(e -> ((ConfigurationObject) e).getObjectSet("De").getElements().stream())
				.filter(de -> de.getConfigurationData(atgDe).getScaledValue("Cluster").getText().equals("Ja"))
				.collect(Collectors.toList());
		return clusterDe;
	}

	/**
	 * Findet alle AQ-Aktoren aus dem Anzeigenmodell, zu einer Menge von
	 * Cluster-DE's.
	 */
	private List<SystemObject> findeAqAktoren(DataModel dataModel, List<SystemObject> clusterDe) {
		final List<SystemObject> alleAqAktoren = dataModel.getType("typ.anzeigeQuerschnittAktorTls").getElements();
		final AttributeGroup atgAqAktorTls = dataModel.getAttributeGroup("atg.anzeigeQuerschnittAktorTls");
		final List<SystemObject> aqAktoren = alleAqAktoren.stream()
				.filter(a -> clusterDe.contains(
						a.getConfigurationData(atgAqAktorTls).getReferenceValue("ZugehörigeDE").getSystemObject()))
				.collect(Collectors.toList());

		final AttributeGroup atgAqAktor = dataModel.getAttributeGroup("atg.anzeigeQuerschnittAktor");
		return aqAktoren.stream().map(a -> a.getConfigurationData(atgAqAktor)
				.getReferenceValue("AnzeigeQuerschnittReferenz").getSystemObject()).collect(Collectors.toList());
	}

}
