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

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import de.bitctrl.dav.rest.api.model.Glaettemeldeanlage;
import de.bitctrl.dav.rest.api.model.GlaettemeldeanlageImpl;
import de.bitctrl.dav.rest.client.annotations.DavJsonObjektConverter;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.DataModel;
import de.bsvrz.dav.daf.main.config.SystemObject;

/**
 * Konverter von {@link SystemObject} in {@link Glaettemeldeanlage}.
 *
 * @author BitCtrl Systems GmbH, ChHoesel
 *
 */
@DavJsonObjektConverter(davTyp = "typ.gma")
public class GlaetteMeldeAnlageJsonConverter implements DavJsonConverter<SystemObject, Glaettemeldeanlage> {

	@Override
	public Collection<Glaettemeldeanlage> dav2Json(SystemObject davObj) {
		final Glaettemeldeanlage result = new GlaettemeldeanlageImpl();
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

		final AttributeGroup atgGma = dataModel.getAttributeGroup("atg.gma");
		final Data configurationData = davObj.getConfigurationData(atgGma);
		result.setDwdKennung(configurationData.getTextValue("DWDKennung").getText());
		result.setHHe(configurationData.getScaledValue("Höhe").intValue());

		return Arrays.asList(result);
	}

}
