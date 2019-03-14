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

import de.bitctrl.dav.rest.api.model.Geschwindigkeit;
import de.bitctrl.dav.rest.api.model.GeschwindigkeitImpl;
import de.bitctrl.dav.rest.api.model.VerkehrsdatenKurzzeit;
import de.bitctrl.dav.rest.api.model.VerkehrsdatenKurzzeit.AspektType;
import de.bitctrl.dav.rest.api.model.Verkehrsstaerke;
import de.bitctrl.dav.rest.api.model.VerkehrsstaerkeImpl;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.config.Aspect;

/**
 * Util Klasse für die Konvertierung in {@link VerkehrsdatenKurzzeit}.
 *
 * @author BitCtrl Systems GmbH, ChHoesel
 *
 */
public final class VerkehrDatenKurzZeitUtil {

	private VerkehrDatenKurzZeitUtil() {
		// Default-Konstruktor verstecken
	}

	static VerkehrsdatenKurzzeit.AspektType extraktAspekt(ResultData resultData) {
		final Aspect aspect = resultData.getDataDescription().getAspect();
		switch (aspect.getPid()) {
		case "asp.agregation1Minute":
			return AspektType.AGGREGATION1MINUTE;
		case "asp.agregation5Minuten":
			return AspektType.AGGREGATION5MINUTEN;
		case "asp.agregation15Minuten":
			return AspektType.AGGREGATION15MINUTEN;
		case "asp.agregation30Minuten":
			return AspektType.AGGREGATION30MINUTEN;
		case "asp.agregation60Minuten":
			return AspektType.AGGREGATION60MINUTEN;
		case "asp.analyseHB":
			return AspektType.ANALYSEHB;
		case "asp.externeErfassung":
			return AspektType.EXTERNEERFASSUNG;
		case "asp.messWertErsetzung":
			return AspektType.MESSWERTERSETZUNG;
		case "asp.plausibilitätsPrüfungFormal":
			return AspektType.PLAUSIBILITTSPRFUNGFORMAL;
		case "asp.plausibilitätsPrüfungLogisch":
			return AspektType.PLAUSIBILITTSPRFUNGLOGISCH;
		default:
			return AspektType.ANALYSE;

		}
	}

	static Geschwindigkeit extraktGeschwindigkeit(Data data) {
		final Geschwindigkeit vKfz = new GeschwindigkeitImpl();
		if (data.getUnscaledValue("Wert").isState()) {
			vKfz.setWert(data.getUnscaledValue("Wert").intValue());
		} else {
			vKfz.setWert(data.getScaledValue("Wert").intValue());
		}
		if(data.getItem("Güte").getUnscaledValue("Index").isState()) {
			vKfz.setGuete(data.getItem("Güte").getUnscaledValue("Index").doubleValue());
		}else {
			vKfz.setGuete(data.getItem("Güte").getScaledValue("Index").doubleValue());	
		}
		return vKfz;
	}

	static Verkehrsstaerke extraktVerkehrsStaerke(Data data) {
		final Verkehrsstaerke qKfz = new VerkehrsstaerkeImpl();
		if (data.getUnscaledValue("Wert").isState()) {
			qKfz.setWert(data.getUnscaledValue("Wert").intValue());
		} else {
			qKfz.setWert(data.getScaledValue("Wert").intValue());
		}
		if(data.getItem("Güte").getUnscaledValue("Index").isState()) {
			qKfz.setGuete(data.getItem("Güte").getUnscaledValue("Index").doubleValue());
		}else {
			qKfz.setGuete(data.getItem("Güte").getScaledValue("Index").doubleValue());	
		}
		
		return qKfz;
	}

}
