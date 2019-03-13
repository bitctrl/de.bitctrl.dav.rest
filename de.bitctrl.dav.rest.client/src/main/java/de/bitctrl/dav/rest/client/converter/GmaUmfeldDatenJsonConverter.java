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

import de.bitctrl.dav.rest.api.model.Atmosphaerendaten;
import de.bitctrl.dav.rest.api.model.AtmosphaerendatenImpl;
import de.bitctrl.dav.rest.api.model.GmaUmfelddaten;
import de.bitctrl.dav.rest.api.model.GmaUmfelddatenImpl;
import de.bitctrl.dav.rest.api.model.UmfelddatenFuerFahrstreifen;
import de.bitctrl.dav.rest.api.model.UmfelddatenFuerFahrstreifen.FahrBahnGlTteType;
import de.bitctrl.dav.rest.api.model.UmfelddatenFuerFahrstreifen.FahrBahnOberFlChenZustandType;
import de.bitctrl.dav.rest.api.model.UmfelddatenFuerFahrstreifenImpl;
import de.bitctrl.dav.rest.client.annotations.DavJsonDatensatzConverter;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.config.SystemObject;

/**
 * Konverter von {@link ResultData} in {@link GmaUmfelddaten}.
 *
 * @author BitCtrl Systems GmbH, ChHoesel
 */
@DavJsonDatensatzConverter(davAttributGruppe = { "atg.gmaUmfelddaten" })
public class GmaUmfeldDatenJsonConverter implements DavJsonConverter<ResultData, GmaUmfelddaten> {

	@Override
	public Collection<GmaUmfelddaten> dav2Json(ResultData resultData) {
		final GmaUmfelddaten result = new GmaUmfelddatenImpl();
		final SystemObject object = resultData.getObject();
		result.setSystemObjektId(object.getPid());

		result.setDatenStatus(resultData.getDataState().toString());
		result.setZeitstempel(new Date(resultData.getDataTime()));
		final Data data = resultData.getData();
		if (data != null) {
			result.setAtmosphRendaten(extractedAtmosphaerenDaten(data));
			result.setUmfeldatenFRFahrstreifen(extractedUfdFahrstreifen(data));
		}
		return Arrays.asList(result);
	}

	private static List<UmfelddatenFuerFahrstreifen> extractedUfdFahrstreifen(final Data data) {
		Data.Array arraysFsUmfeldDaten = data.getArray("UmfelddatenFuerFahrstreifen");
		List<UmfelddatenFuerFahrstreifen> result = new ArrayList<>();
		for (int i = 0; i < arraysFsUmfeldDaten.getLength(); i++) {
			Data dataItem = arraysFsUmfeldDaten.getItem(i);
			UmfelddatenFuerFahrstreifen ufdFahrstreifen = new UmfelddatenFuerFahrstreifenImpl();

			final Data.NumberArray arrayFahrBahnOberFlaechenTemperatur = dataItem
					.getScaledArray("FahrBahnOberFlächenTemperatur");
			if (arrayFahrBahnOberFlaechenTemperatur.getLength() >= 1
					&& arrayFahrBahnOberFlaechenTemperatur.getValue(0).isNumber()) {
				ufdFahrstreifen
						.setFahrBahnOberflChenTemperatur(arrayFahrBahnOberFlaechenTemperatur.getValue(0).doubleValue());
			}

			final Data.NumberArray arrayGefrieTemp = dataItem.getScaledArray("GefrierTemperatur");
			if (arrayGefrieTemp.getLength() >= 1 && arrayGefrieTemp.getValue(0).isNumber()) {
				ufdFahrstreifen.setGefrierTemperatur(arrayGefrieTemp.getValue(0).doubleValue());
			}

			final Data.NumberArray arrayTempTiefe1 = dataItem.getScaledArray("TemperaturInTiefe1");
			if (arrayTempTiefe1.getLength() >= 1 && arrayTempTiefe1.getValue(0).isNumber()) {
				ufdFahrstreifen.setTemperaturInTiefe1(arrayTempTiefe1.getValue(0).doubleValue());
			}

			final Data.NumberArray arrayTempTiefe2 = dataItem.getScaledArray("TemperaturInTiefe2");
			if (arrayTempTiefe2.getLength() >= 1 && arrayTempTiefe2.getValue(0).isNumber()) {
				ufdFahrstreifen.setTemperaturInTiefe2(arrayTempTiefe2.getValue(0).doubleValue());
			}

			final Data.NumberArray arrayTempTiefe3 = dataItem.getScaledArray("TemperaturInTiefe3");
			if (arrayTempTiefe3.getLength() >= 1 && arrayTempTiefe3.getValue(0).isNumber()) {
				ufdFahrstreifen.setTemperaturInTiefe3(arrayTempTiefe3.getValue(0).doubleValue());
			}

			final Data.NumberArray arrayFBOberflaechenZustand = dataItem.getUnscaledArray("FahrBahnOberFlächenZustand");
			if (arrayFBOberflaechenZustand.getLength() >= 1) {
				ufdFahrstreifen.setFahrBahnOberFlChenZustand(
						extraktFBOberflaechenZustand(arrayFBOberflaechenZustand.getValue(0).getValueText()));
			}

			final Data.NumberArray arrayWasserFilmdicke = dataItem.getScaledArray("WasserFilmDicke");
			if (arrayWasserFilmdicke.getLength() >= 1 && arrayWasserFilmdicke.getValue(0).isNumber()) {
				ufdFahrstreifen.setWasserFilmDicke(arrayWasserFilmdicke.getValue(0).doubleValue());
			}

			final Data.NumberArray arrayTauStoffKonzentration = dataItem.getUnscaledArray("TauStoffKonzentration");
			if (arrayTauStoffKonzentration.getLength() >= 1) {
				ufdFahrstreifen.setTauStoffKonzentration(arrayTauStoffKonzentration.getValue(0).intValue());
			}

			final Data.NumberArray arrayFBGlaette = dataItem.getUnscaledArray("FahrBahnGlätte");
			if (arrayFBGlaette.getLength() >= 1) {
				ufdFahrstreifen.setFahrBahnGlTte(extraktFBGlaette(arrayFBGlaette.getValue(0).getValueText()));
			}

			final Data.NumberArray arrayZREisglaette = dataItem.getUnscaledArray("ZeitreserveEisglätte");
			if (arrayZREisglaette.getLength() >= 1) {
				ufdFahrstreifen.setZeitreserveEisglTte(arrayZREisglaette.getValue(0).intValue());
			}

			final Data.NumberArray arrayZRReifglaette = dataItem.getUnscaledArray("ZeitreserveReifglätte");
			if (arrayZRReifglaette.getLength() >= 1) {
				ufdFahrstreifen.setZeitreserveReifglTte(arrayZRReifglaette.getValue(0).intValue());
			}

			result.add(ufdFahrstreifen);
		}
		return result;
	}

	private static FahrBahnGlTteType extraktFBGlaette(String valueText) {
		switch (valueText) {
		case "Keine Glättegefahr":
			return FahrBahnGlTteType.KEINEGLTTEGEFAHR;
		case "Eisglätte möglich":
			return FahrBahnGlTteType.EISGLTTEMGLICH;
		case "Tendenzberechnung nicht möglich":
			return FahrBahnGlTteType.TENDENZBERECHNUNGNICHTMGLICH;
		case "Schneeglätte oder Glatteis bei Niederschlag möglich":
			return FahrBahnGlTteType.SCHNEEGLTTEODERGLATTEISBEINIEDERSCHLAGMGLICH;
		case "Schneeglätte oder Glatteis bei Niederschlag sofort möglich":
			return FahrBahnGlTteType.SCHNEEGLTTEODERGLATTEISBEINIEDERSCHLAGSOFORTMGLICH;
		case "Eisglätte sofort möglich":
			return FahrBahnGlTteType.EISGLTTESOFORTMGLICH;
		case "Glätte vorhanden":
			return FahrBahnGlTteType.GLTTEVORHANDEN;
		case "Eis oder Schnee auf der Fahrbahn":
			return FahrBahnGlTteType.EISODERSCHNEEAUFDERFAHRBAHN;
		default:
			return null;
		}
	}

	private static UmfelddatenFuerFahrstreifen.FahrBahnOberFlChenZustandType extraktFBOberflaechenZustand(String name) {
		switch (name) {
		case "trocken":
			return FahrBahnOberFlChenZustandType.TROCKEN;
		case "feucht":
			return FahrBahnOberFlChenZustandType.FEUCHT;
		case "nass":
			return FahrBahnOberFlChenZustandType.NASS;
		case "gefrorenes Wasser":
			return FahrBahnOberFlChenZustandType.GEFRORENESWASSER;
		case "Schnee/Schneematsch":
			return FahrBahnOberFlChenZustandType.SCHNEESCHNEEMATSCH;
		case "Eis":
			return FahrBahnOberFlChenZustandType.EIS;
		case "Raureif":
			return FahrBahnOberFlChenZustandType.RAUREIF;
		default:
			return null;
		}
	}

	private static Atmosphaerendaten extractedAtmosphaerenDaten(Data data) {
		Data dataAtmoshpaere = data.getItem("Atmosphärendaten");
		Atmosphaerendaten atmosphaerenDaten = new AtmosphaerendatenImpl();
		final Data.NumberArray arrayLuftTemperatur = dataAtmoshpaere.getScaledArray("LuftTemperatur");
		if (arrayLuftTemperatur.getLength() >= 1 && arrayLuftTemperatur.getValue(0).isNumber()) {
			atmosphaerenDaten.setLuftTemperatur(arrayLuftTemperatur.getValue(0).doubleValue());
		}

		final Data.NumberArray arrayNI = dataAtmoshpaere.getScaledArray("NiederschlagsIntensität");
		if (arrayNI.getLength() >= 1 && arrayNI.getValue(0).isNumber()) {
			atmosphaerenDaten.setNiederschlagsIntensitT(arrayNI.getValue(0).doubleValue());
		}

		final Data.NumberArray arrayLuftDruck = dataAtmoshpaere.getUnscaledArray("LuftDruck");
		if (arrayLuftDruck.getLength() >= 1) {
			atmosphaerenDaten.setLuftDruck(arrayLuftDruck.getValue(0).intValue());
		}

		final Data.NumberArray arrayRelLuftFeuchte = dataAtmoshpaere.getUnscaledArray("RelativeLuftFeuchte");
		if (arrayRelLuftFeuchte.getLength() >= 1) {
			atmosphaerenDaten.setRelativeLuftfeuchte(arrayRelLuftFeuchte.getValue(0).doubleValue());
		}

		final Data.NumberArray arrayWindRichtung = dataAtmoshpaere.getUnscaledArray("WindRichtung");
		if (arrayWindRichtung.getLength() >= 1) {
			atmosphaerenDaten.setWindRichtung(arrayWindRichtung.getValue(0).intValue());
		}

		final Data.NumberArray arrayWindGeschwindigkeitMittelWert = dataAtmoshpaere
				.getScaledArray("WindGeschwindigkeitMittelWert");
		if (arrayWindGeschwindigkeitMittelWert.getLength() >= 1
				&& arrayWindGeschwindigkeitMittelWert.getValue(0).isNumber()) {
			atmosphaerenDaten
					.setWindGeschwindigkeitMittelWert(arrayWindGeschwindigkeitMittelWert.getValue(0).doubleValue());
		}

		final Data.NumberArray arraySchneeHoehe = dataAtmoshpaere.getUnscaledArray("SchneeHöhe");
		if (arraySchneeHoehe.getLength() >= 1) {
			atmosphaerenDaten.setSchneeHHe(arraySchneeHoehe.getValue(0).doubleValue());
		}

		final Data.NumberArray arraysichtWeite = dataAtmoshpaere.getUnscaledArray("SichtWeite");
		if (arraysichtWeite.getLength() >= 1) {
			atmosphaerenDaten.setSichtWeite(arraysichtWeite.getValue(0).intValue());
		}

		final Data.NumberArray arrayHelligkeit = dataAtmoshpaere.getUnscaledArray("Helligkeit");
		if (arrayHelligkeit.getLength() >= 1) {
			atmosphaerenDaten.setHelligkeit(arrayHelligkeit.getValue(0).intValue());
		}

		final Data.NumberArray arrayWindGeschwindigkeitSpitze = dataAtmoshpaere
				.getScaledArray("WindGeschwindigkeitSpitzenWert");
		if (arrayWindGeschwindigkeitSpitze.getLength() >= 1 && arrayWindGeschwindigkeitSpitze.getValue(0).isNumber()) {
			atmosphaerenDaten
					.setWindGeschwindigkeitSpitzenWert(arrayWindGeschwindigkeitSpitze.getValue(0).doubleValue());
		}

		final Data.NumberArray arrayTauPunktTemperatur = dataAtmoshpaere.getScaledArray("TaupunktTemperatur");
		if (arrayTauPunktTemperatur.getLength() >= 1 && arrayTauPunktTemperatur.getValue(0).isNumber()) {
			atmosphaerenDaten.setTaupunktTemperatur(arrayTauPunktTemperatur.getValue(0).doubleValue());
		}

		final Data.NumberArray arrayNiederSchlagsArt = dataAtmoshpaere.getScaledArray("NiederschlagsArt");
		if (arrayNiederSchlagsArt.getLength() >= 1 && arrayNiederSchlagsArt.getValue(0).isState()) {
			atmosphaerenDaten.setNiederschlagsArt(arrayNiederSchlagsArt.getValue(0).getState().getName());
		}

		final Data.NumberArray arrayNiederSchlagesMenge = dataAtmoshpaere.getScaledArray("NiederschlagsMenge");
		if (arrayNiederSchlagesMenge.getLength() >= 1 && arrayNiederSchlagesMenge.getValue(0).isNumber()) {
			atmosphaerenDaten.setNiederschlagsMenge(arrayNiederSchlagesMenge.getValue(0).doubleValue());
		}

		return atmosphaerenDaten;
	}

}
