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

import de.bitctrl.dav.rest.api.model.Geschwindigkeit;
import de.bitctrl.dav.rest.api.model.VerkehrsdatenKurzzeit;
import de.bitctrl.dav.rest.api.model.VerkehrsdatenKurzzeitImpl;
import de.bitctrl.dav.rest.api.model.VerkehrstaerkeStunde;
import de.bitctrl.dav.rest.client.annotations.DavJsonDatensatzConverter;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.ResultData;

/**
 * Konverter von {@link ResultData} in ein {@link VerkehrsdatenKurzzeit}.
 *
 * @author BitCtrl Systems GmbH, ChHoesel
 *
 */
@DavJsonDatensatzConverter(davAttributGruppe = { "atg.verkehrsDatenKurzZeitFs" })
public class VerkehrsDatenKurzZeitFSConverter implements DavJsonConverter<ResultData, VerkehrsdatenKurzzeit> {

	@Override
	public VerkehrsdatenKurzzeit dav2Json(ResultData resultData) {

		final VerkehrsdatenKurzzeit result = new VerkehrsdatenKurzzeitImpl();
		result.setSystemObjectId(resultData.getObject().getPid());

		final VerkehrsdatenKurzzeit.AspektType aspekt = VerkehrDatenKurzZeitUtil.extractAspekt(resultData);
		result.setAspekt(aspekt);

		result.setDatenStatus(resultData.getDataState().toString());
		result.setZeitstempel(new Date(resultData.getDataTime()));
		final Data data = resultData.getData();
		if (data != null) {
			final VerkehrstaerkeStunde qKfz = VerkehrDatenKurzZeitUtil.extraktVerkehrsStaerke(data.getItem("qKfz"));
			result.setQKfz(qKfz);
			final Geschwindigkeit vKfz = VerkehrDatenKurzZeitUtil.extraktGeschwindigkeit(data.getItem("vKfz"));
			result.setVKfz(vKfz);
			final VerkehrstaerkeStunde qLkw = VerkehrDatenKurzZeitUtil.extraktVerkehrsStaerke(data.getItem("qLkw"));
			result.setQLkw(qLkw);
			final Geschwindigkeit vLkw = VerkehrDatenKurzZeitUtil.extraktGeschwindigkeit(data.getItem("vLkw"));
			result.setVLkw(vLkw);
			final VerkehrstaerkeStunde qPkw = VerkehrDatenKurzZeitUtil.extraktVerkehrsStaerke(data.getItem("qPkw"));
			result.setQPkw(qPkw);
			final Geschwindigkeit vPkw = VerkehrDatenKurzZeitUtil.extraktGeschwindigkeit(data.getItem("vPkw"));
			result.setVPkw(vPkw);

			final Data b = data.getItem("b");
			if (b.getUnscaledValue("Wert").isState()) {
				result.setB(b.getUnscaledValue("Wert").doubleValue());
			} else {
				result.setB(b.getScaledValue("Wert").doubleValue());
			}
		}
		return result;
	}

}
