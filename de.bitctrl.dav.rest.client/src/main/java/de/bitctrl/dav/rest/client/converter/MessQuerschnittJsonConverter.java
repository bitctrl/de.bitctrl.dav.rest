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

import de.bitctrl.dav.rest.api.MessQuerschnitt;
import de.bitctrl.dav.rest.api.MessQuerschnittImpl;
import de.bitctrl.dav.rest.client.annotations.DavJsonObjektConverter;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
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

		final DataModel dataModel = davObj.getDataModel();
		final AttributeGroup atg = dataModel.getAttributeGroup("atg.punktKoordinaten");
		final Data daten = davObj.getConfigurationData(atg);
		if (daten != null && !daten.getUnscaledValue("x").isState()) {
			result.setLaenge(daten.getScaledValue("x").doubleValue());
		}
		if (daten != null && !daten.getUnscaledValue("y").isState()) {
			result.setBreite(daten.getScaledValue("y").doubleValue());
		}

		return result;
	}

}
