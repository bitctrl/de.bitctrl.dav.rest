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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import de.bitctrl.dav.rest.api.model.AnzeigeEigenschaft;
import de.bitctrl.dav.rest.api.model.AnzeigeEigenschaft.StatusType;
import de.bitctrl.dav.rest.api.model.AnzeigeEigenschaftImpl;
import de.bitctrl.dav.rest.client.annotations.DavJsonDatensatzConverter;
import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.Data.ReferenceArray;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.config.Aspect;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.DataModel;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.debug.Debug;

/**
 * Konverter von {@link ResultData} in {@link AnzeigeEigenschaft}.
 *
 * @author BitCtrl Systems GmbH, ChHoesel
 */
@DavJsonDatensatzConverter(davAttributGruppe = { "atg.anzeigeEigenschaftIst" })
public class AnzeigeEigenschaftJsonConverter implements DavJsonConverter<ResultData, AnzeigeEigenschaft> {

	@Inject
	private ClientDavInterface dav;

	private static final Debug LOGGER = Debug.getLogger();

	@Override
	public Collection<AnzeigeEigenschaft> dav2Json(ResultData resultData) {
		final AnzeigeEigenschaft result = new AnzeigeEigenschaftImpl();
		final SystemObject object = resultData.getObject();
		final DataModel dataModel = object.getDataModel();
		result.setSystemObjektId(object.getPid());

		result.setDatenStatus(resultData.getDataState().toString());
		result.setZeitstempel(new Date(resultData.getDataTime()));
		final Data data = resultData.getData();
		if (data != null && data.getUnscaledValue("Status").isState()) {
			result.setStatus(extraktStatus(data.getScaledValue("Status").getText()));
		} else {
			result.setStatus(StatusType.UNDEFINIERT);
		}

		if (data != null) {
			if (anzeigeKannFreitext(resultData)) {
				final String text = extraktDynWechseltext(dataModel, data);
				result.setText(text);
			}
			result.setWvzInhalt(extraktWvzInhalt(dataModel, data));
			byte[] grafikSymbol = extraktWzgGrafikSymbol(dataModel, data);
			if (grafikSymbol != null && grafikSymbol.length > 0) {
				result.setGrafik(convertToIntList(grafikSymbol));
			}
		}
		return Arrays.asList(result);
	}

	public static List<Integer> convertToIntList(byte[] input) {
		List<Integer> ret = new ArrayList<>();
		for (int i = 0; i < input.length; i++) {
			ret.add(input[i] & 0xff); // Range 0 to 255, not -128 to 127
		}
		return ret;
	}

	private String extraktWvzInhalt(DataModel dataModel, final Data data) {
		String bildinhalt = null;
		final Data eigenschaftData = data.getItem("Eigenschaft");
		final long idAnzeigeInhalt = eigenschaftData.getReferenceValue("AnzeigeInhalt").getId();
		if (idAnzeigeInhalt != 0) {
			final SystemObject wvzInhalt = dataModel.getObject(idAnzeigeInhalt);
			if (wvzInhalt != null) {
				final Data wvzInhaltData = wvzInhalt.getConfigurationData(dataModel.getAttributeGroup("atg.wvzInhalt"));
				bildinhalt = wvzInhaltData.getTextValue("Bildinhalt").getText();
				ReferenceArray array = wvzInhaltData.getReferenceArray("GrafikDarstellungen");
				if (array.getLength() > 0) {
					SystemObject grafikdarstellung = array.getSystemObject(0);
					if (grafikdarstellung.isOfType("typ.wzgInhaltGrafikSymbol")) {
						AttributeGroup atg = dataModel.getAttributeGroup("atg.wzgInhaltGrafikSymbol");
						Aspect asp = dataModel.getAspect("asp.parameterSoll");
						ResultData resultData = dav.getData(grafikdarstellung, new DataDescription(atg, asp), 6000);
						if (resultData.hasData()) {
							Data wzgInhalteGrafikSymbolData = resultData.getData();
							bildinhalt = wzgInhalteGrafikSymbolData.getTextValue("SymbolName").getText();
						}
					}
				}

			}
		}
		return bildinhalt;
	}

	private byte[] extraktWzgGrafikSymbol(DataModel dataModel, final Data data) {
		final Data eigenschaftData = data.getItem("Eigenschaft");
		final long idAnzeigeInhalt = eigenschaftData.getReferenceValue("AnzeigeInhalt").getId();
		if (idAnzeigeInhalt != 0) {
			final SystemObject wvzInhalt = dataModel.getObject(idAnzeigeInhalt);
			if (wvzInhalt != null) {
				final Data wvzInhaltData = wvzInhalt.getConfigurationData(dataModel.getAttributeGroup("atg.wvzInhalt"));
				ReferenceArray array = wvzInhaltData.getReferenceArray("GrafikDarstellungen");
				if (array.getLength() > 0) {
					SystemObject grafikdarstellung = array.getSystemObject(0);
					if (grafikdarstellung.isOfType("typ.wzgInhaltGrafikSymbol")) {
						AttributeGroup atg = dataModel.getAttributeGroup("atg.wzgInhaltGrafikSymbol");
						Aspect asp = dataModel.getAspect("asp.parameterSoll");
						ResultData resultData = dav.getData(grafikdarstellung, new DataDescription(atg, asp), 6000);
						if (resultData.hasData()) {
							Data wzgInhalteGrafikSymbolData = resultData.getData();
							return wzgInhalteGrafikSymbolData.getArray("SymbolBitmap").asUnscaledArray().getByteArray();
						}
					}
				}
			}
		}
		return new byte[0];
	}

	private static String extraktDynWechseltext(DataModel dataModel, final Data data) {
		final Data eigenschaftData = data.getItem("Eigenschaft");
		final StringBuffer wechseltext = new StringBuffer();
		final Data dynamischerWechseltextData = eigenschaftData.getItem("DynamischerWechseltext");
		final Data.ReferenceArray arrayTextdefinition = dynamischerWechseltextData.getReferenceArray("Textdefinition");
		for (int i = 0; i < arrayTextdefinition.getLength(); ++i) {
			final long idTextdefinition = dynamischerWechseltextData.getReferenceArray("Textdefinition")
					.getReferenceValue(i).getId();
			if (idTextdefinition != 0) {
				final SystemObject soTextdefinition = dataModel.getObject(idTextdefinition);
				if (soTextdefinition != null) {

					final String zeichen = soTextdefinition
							.getConfigurationData(dataModel.getAttributeGroup("atg.zeichen"))
							.getTextValue("Zeichenname").getText();
					wechseltext.append(zeichen);
				}
			}
		}

		if (wechseltext.length() > 0) {
			return wechseltext.toString();
		}
		return null;
	}

	private static boolean anzeigeKannFreitext(ResultData resultData) {
		boolean result = false;
		final SystemObject anzeige = resultData.getObject();
		final DataModel dataModel = anzeige.getDataModel();
		final AttributeGroup atgAnzeige = dataModel.getAttributeGroup("atg.anzeige");

		final SystemObject anzeigeTyp = anzeige.getConfigurationData(atgAnzeige).getReferenceValue("AnzeigeTyp")
				.getSystemObject();
		final AttributeGroup atgAnzeigeTyp = dataModel.getAttributeGroup("atg.anzeigeTyp");
		final Data anzeigeTypData = anzeigeTyp.getConfigurationData(atgAnzeigeTyp);
		if (anzeigeTypData.getUnscaledValue("KannFreiText").isState()) {
			result = "Ja".equalsIgnoreCase(anzeigeTypData.getScaledValue("KannFreiText").getText());
		}
		return result;
	}

	private static StatusType extraktStatus(String davStatus) {
		switch (davStatus) {
		case "OK":
			return StatusType.OK;
		case "Gestört":
			return StatusType.GESTRT;
		case "Teilstörung":
			return StatusType.TEILSTRUNG;
		case "Stromausfall":
			return StatusType.STROMAUSFALL;
		case "Kommunikationsausfall":
			return StatusType.KOMMUNIKATIONSAUSFALL;
		case "Türkontakt":
			return StatusType.TRKONTAKT;
		default:
			return StatusType.UNDEFINIERT;
		}
	}

}
