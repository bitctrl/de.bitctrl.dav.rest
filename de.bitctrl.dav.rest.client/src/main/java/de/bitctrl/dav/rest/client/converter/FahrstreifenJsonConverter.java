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

import java.util.Date;

import de.bitctrl.dav.rest.api.model.FahrStreifen;
import de.bitctrl.dav.rest.api.model.FahrStreifenImpl;
import de.bitctrl.dav.rest.client.annotations.DavJsonObjektConverter;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.DataModel;
import de.bsvrz.dav.daf.main.config.SystemObject;

/**
 * Konverter von {@link SystemObject} in {@link FahrStreifen}.
 *
 * @author BitCtrl Systems GmbH, ChHoesel
 *
 */
@DavJsonObjektConverter(davTyp = "typ.fahrStreifen")
public class FahrstreifenJsonConverter implements DavJsonConverter<SystemObject, FahrStreifen> {

	@Override
	public FahrStreifen dav2Json(SystemObject davObj) {
		final FahrStreifen result = new FahrStreifenImpl();
		result.setId(davObj.getPid());
		result.setName(davObj.getName());
		result.setVersion(new Date(davObj.getConfigurationArea().getTimeOfLastActiveConfigurationChange()));
		
		final FahrStreifen.LageType lage = bestimmeLage(davObj);
		result.setLage(lage);
		return result;
	}

	private FahrStreifen.LageType bestimmeLage(SystemObject davObj) {
		final DataModel dataModel = davObj.getDataModel();
		final AttributeGroup atg = dataModel.getAttributeGroup("atg.fahrStreifen");
		final Data daten = davObj.getConfigurationData(atg);
		if (daten.getUnscaledValue("Lage").isState()) {
			final String lage = daten.getScaledValue("Lage").getText();
			switch (lage) {
			case "1ÜFS":
				return FahrStreifen.LageType._1FS;
			case "2ÜFS":
				return FahrStreifen.LageType._2FS;
			case "3ÜFS":
				return FahrStreifen.LageType._3FS;
			case "4ÜFS":
				return FahrStreifen.LageType._4FS;
			case "5ÜFS":
				return FahrStreifen.LageType._5FS;
			case "6ÜFS":
				return FahrStreifen.LageType._6FS;
			default:
				return FahrStreifen.LageType.HFS;
			}
		}
		return FahrStreifen.LageType.HFS;
	}

}
