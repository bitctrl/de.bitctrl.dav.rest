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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import de.bitctrl.dav.rest.api.model.MessQuerschnitt;
import de.bitctrl.dav.rest.api.model.MessQuerschnittImpl;
import de.bitctrl.dav.rest.client.annotations.DavJsonObjektConverter;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.ConfigurationArea;
import de.bsvrz.dav.daf.main.config.ConfigurationObject;
import de.bsvrz.dav.daf.main.config.DataModel;
import de.bsvrz.dav.daf.main.config.SystemObject;

/**
 * Konverter von {@link SystemObject} in einen {@link MessQuerschnitt}.
 *
 * @author BitCtrl Systems GmbH, ChHoesel
 *
 */
@DavJsonObjektConverter(davTyp = "typ.messQuerschnitt")
public class MessQuerschnittJsonConverter implements DavJsonConverter<SystemObject, MessQuerschnitt> {

	@Override
	public MessQuerschnitt dav2Json(SystemObject davObj) {
		final MessQuerschnitt result = new MessQuerschnittImpl();
		result.setId(davObj.getPid());
		result.setName(davObj.getName());
		result.setVersion(new Date(davObj.getConfigurationArea().getTimeOfLastActiveConfigurationChange()));

		final DataModel dataModel = davObj.getDataModel();
		final AttributeGroup atg = dataModel.getAttributeGroup("atg.punktKoordinaten");
		final Data daten = davObj.getConfigurationData(atg);
		if (daten != null && !daten.getUnscaledValue("x").isState()) {
			result.setLaenge(daten.getScaledValue("x").doubleValue());
		}
		if (daten != null && !daten.getUnscaledValue("y").isState()) {
			result.setBreite(daten.getScaledValue("y").doubleValue());
		}

		final ConfigurationObject ko = (ConfigurationObject) davObj;
		if (ko.getObjectSet("FahrStreifen") == null) {
			result.setFahrstreifen(new ArrayList<>());
		} else {
			final List<String> fahrstreifenIds = ko.getObjectSet("FahrStreifen").getElements().stream()
					.map(s -> s.getPidOrNameOrId()).collect(Collectors.toList());
			result.setFahrstreifen(fahrstreifenIds);
		}

		return result;
	}

}
