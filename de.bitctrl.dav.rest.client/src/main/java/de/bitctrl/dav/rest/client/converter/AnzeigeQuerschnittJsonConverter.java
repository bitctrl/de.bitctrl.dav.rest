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
import java.util.List;
import java.util.stream.Collectors;

import de.bitctrl.dav.rest.api.model.AnzeigeQuerschnitt;
import de.bitctrl.dav.rest.api.model.AnzeigeQuerschnittImpl;
import de.bitctrl.dav.rest.client.annotations.DavJsonObjektConverter;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.ConfigurationObject;
import de.bsvrz.dav.daf.main.config.DataModel;
import de.bsvrz.dav.daf.main.config.SystemObject;

/**
 * Konverter von {@link SystemObject} in einen {@link AnzeigeQuerschnitt}.
 *
 * @author BitCtrl Systems GmbH, ChHoesel
 *
 */
@DavJsonObjektConverter(davTyp = "typ.anzeigeQuerschnitt")
public class AnzeigeQuerschnittJsonConverter implements DavJsonConverter<SystemObject, AnzeigeQuerschnitt> {

	@Override
	public AnzeigeQuerschnitt dav2Json(SystemObject davObj) {
		final AnzeigeQuerschnitt result = new AnzeigeQuerschnittImpl();
		result.setId(davObj.getPid());
		result.setName(davObj.getName());

		extractedAtgPunkKoordinaten(davObj, result);

		final ConfigurationObject ko = (ConfigurationObject) davObj;
		if (ko.getObjectSet("Anzeigen") == null) {
			result.setAnzeigen(new ArrayList<>());
		} else {
			final List<String> anzeigen = ko.getObjectSet("Anzeigen").getElements().stream()
					.map(s -> s.getPidOrNameOrId()).collect(Collectors.toList());
			result.setAnzeigen(anzeigen);
		}

		return result;
	}

	private void extractedAtgPunkKoordinaten(SystemObject davObj, final AnzeigeQuerschnitt result) {
		final DataModel dataModel = davObj.getDataModel();
		final AttributeGroup atg = dataModel.getAttributeGroup("atg.punktKoordinaten");
		final Data daten = davObj.getConfigurationData(atg);
		if (daten != null && !daten.getUnscaledValue("x").isState()) {
			result.setLaenge(daten.getScaledValue("x").doubleValue());
		}
		if (daten != null && !daten.getUnscaledValue("y").isState()) {
			result.setBreite(daten.getScaledValue("y").doubleValue());
		}
	}

}
