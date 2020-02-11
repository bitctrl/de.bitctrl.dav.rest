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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import de.bitctrl.dav.rest.api.model.Anzeige;
import de.bitctrl.dav.rest.api.model.AnzeigeImpl;
import de.bitctrl.dav.rest.api.model.FahrstreifenLage;
import de.bitctrl.dav.rest.client.annotations.DavJsonObjektConverter;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.ConfigurationObject;
import de.bsvrz.dav.daf.main.config.ObjectSet;
import de.bsvrz.dav.daf.main.config.SystemObject;

/**
 * Konverter von {@link SystemObject} in {@link Anzeige}.
 *
 * @author BitCtrl Systems GmbH, ChHoesel
 *
 */
@DavJsonObjektConverter(davTyp = "typ.anzeige")
public class AnzeigeJsonConverter implements DavJsonConverter<SystemObject, Anzeige> {

	@Override
	public Collection<Anzeige> dav2Json(SystemObject davObj) {
		final Anzeige result = new AnzeigeImpl();
		result.setId(davObj.getPid());
		result.setName(davObj.getName());

		result.setVersion(new Date(davObj.getConfigurationArea().getTimeOfLastActiveConfigurationChange()));

		result.setFahrstreifen(fahrstreifenErmitteln(davObj));
		result.setWvzInhalte(wvzInhalteErmitteln(davObj));
		return Arrays.asList(result);
	}

	private List<String> wvzInhalteErmitteln(SystemObject davObj) {
		final List<String> result = new ArrayList<>();

		final AttributeGroup atgAnzeige = davObj.getDataModel().getAttributeGroup("atg.anzeige");
		final AttributeGroup atgWvzInhalt = davObj.getDataModel().getAttributeGroup("atg.wvzInhalt");
		final SystemObject anzeigeTyp = davObj.getConfigurationData(atgAnzeige).getReferenceValue("AnzeigeTyp")
				.getSystemObject();

		final ObjectSet wvzInhalteSet = ((ConfigurationObject) anzeigeTyp).getObjectSet("WvzInhalt");
		if (wvzInhalteSet != null) {
			wvzInhalteSet.getElements().stream()
					.map(o -> o.getConfigurationData(atgWvzInhalt).getTextValue("Bildinhalt"))
					.forEach(t -> result.add(t.getText()));
		}

		return result;
	}

	/**
	 * Ermittelt die Fahrstreifen, zu denen die Anzeige gehört anhand der PID der
	 * Anzeige.
	 */
	private List<FahrstreifenLage> fahrstreifenErmitteln(SystemObject davObj) {
		final List<FahrstreifenLage> lage = new ArrayList<>();

		final String pid = davObj.getPid();
		if (pid.contains("hfs") || pid.contains("rechts")) {
			lage.add(FahrstreifenLage.HFS);
		}
		if (pid.contains("1ufs") || pid.contains("links")) {
			lage.add(FahrstreifenLage._1FS);
		}
		if (pid.contains("2ufs")) {
			lage.add(FahrstreifenLage._2FS);
		}
		if (pid.contains("3ufs")) {
			lage.add(FahrstreifenLage._3FS);
		}
		if (pid.contains("4ufs")) {
			lage.add(FahrstreifenLage._4FS);
		}
		if (pid.contains("5ufs")) {
			lage.add(FahrstreifenLage._5FS);
		}
		if (pid.contains("6ufs")) {
			lage.add(FahrstreifenLage._6FS);
		}

		if (lage.isEmpty()) {
			lage.addAll(Arrays.asList(FahrstreifenLage.values()));
		}
		return lage;
	}

}
