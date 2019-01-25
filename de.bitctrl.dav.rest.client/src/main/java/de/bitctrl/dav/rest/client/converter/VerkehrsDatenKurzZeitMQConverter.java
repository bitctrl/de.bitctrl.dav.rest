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

import de.bitctrl.dav.rest.api.Geschwindigkeit;
import de.bitctrl.dav.rest.api.GeschwindigkeitImpl;
import de.bitctrl.dav.rest.api.OnlineDatum;
import de.bitctrl.dav.rest.api.VerkehrsdatenKurzzeit;
import de.bitctrl.dav.rest.api.VerkehrsdatenKurzzeit.AspektType;
import de.bitctrl.dav.rest.api.VerkehrsdatenKurzzeitImpl;
import de.bitctrl.dav.rest.api.VerkehrstaerkeStunde;
import de.bitctrl.dav.rest.api.VerkehrstaerkeStundeImpl;
import de.bitctrl.dav.rest.client.annotations.DavJsonDatensatzConverter;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.ResultData;

/**
 * Konverter von {@link ResultData} in ein {@link OnlineDatum}.
 * 
 * @author BitCtrl Systems GmbH, ChHoesel
 *
 */
@DavJsonDatensatzConverter(davAttributGruppe = "atg.verkehrsDatenKurzZeitMq")
public class VerkehrsDatenKurzZeitMQConverter implements DavJsonConverter<ResultData, OnlineDatum> {

	@Override
	public OnlineDatum dav2Json(ResultData resultData) {

		final VerkehrsdatenKurzzeit result = new VerkehrsdatenKurzzeitImpl();
		result.setSystemObjectId(resultData.getObject().getPid());

		AspektType aspektType = VerkehrsdatenKurzzeit.AspektType
				.valueOf(resultData.getDataDescription().getAspect().getName());
		result.setAspekt(aspektType);
		result.setDatenStatus(resultData.getDataState().toString());
		result.setZeitstempel(new Date(resultData.getDataTime()));
		final Data data = resultData.getData();
		if (data != null) {
			final VerkehrstaerkeStunde qKfz = extraktVerkehrsStaerke(data.getItem("QKfz"));
			result.setQKfz(qKfz);
			final Geschwindigkeit vKfz = extraktGeschwindigkeit(data.getItem("VKfz"));
			result.setVKfz(vKfz);
			final VerkehrstaerkeStunde qLkw = extraktVerkehrsStaerke(data.getItem("QLkw"));
			result.setQLkw(qLkw);
			final Geschwindigkeit vLkw = extraktGeschwindigkeit(data.getItem("VLkw"));
			result.setVLkw(vLkw);
			final VerkehrstaerkeStunde qPkw = extraktVerkehrsStaerke(data.getItem("QPkw"));
			result.setQPkw(qPkw);
			final Geschwindigkeit vPkw = extraktGeschwindigkeit(data.getItem("VPkw"));
			result.setVPkw(vPkw);

			final Data b = data.getItem("B");
			if (b.getUnscaledValue("Wert").isState()) {
				result.setB(b.getUnscaledValue("Wert").doubleValue());
			} else {
				result.setB(b.getScaledValue("Wert").doubleValue());
			}
		}
		return result;
	}

	private Geschwindigkeit extraktGeschwindigkeit(Data data) {
		final Geschwindigkeit vKfz = new GeschwindigkeitImpl();
		if (data.getUnscaledValue("Wert").isState()) {
			vKfz.setWert(data.getUnscaledValue("Wert").intValue());
		} else {
			vKfz.setWert(data.getScaledValue("Wert").intValue());
		}
		vKfz.setGuete(data.getItem("Güte").getItem("Index").asScaledValue().doubleValue());
		return vKfz;
	}

	private VerkehrstaerkeStunde extraktVerkehrsStaerke(Data data) {
		final VerkehrstaerkeStunde qKfz = new VerkehrstaerkeStundeImpl();
		if (data.getUnscaledValue("Wert").isState()) {
			qKfz.setWert(data.getUnscaledValue("Wert").intValue());
		} else {
			qKfz.setWert(data.getScaledValue("Wert").intValue());
		}
		qKfz.setGuete(data.getItem("Güte").getItem("Index").asScaledValue().doubleValue());
		return qKfz;
	}

}
