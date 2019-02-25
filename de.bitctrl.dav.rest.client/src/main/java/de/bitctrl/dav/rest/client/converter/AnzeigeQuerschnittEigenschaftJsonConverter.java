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

import de.bitctrl.dav.rest.api.model.AnzeigeQuerschnittEigenschaft;
import de.bitctrl.dav.rest.api.model.AnzeigeQuerschnittEigenschaft.BetriebszustandType;
import de.bitctrl.dav.rest.api.model.AnzeigeQuerschnittEigenschaft.StatusType;
import de.bitctrl.dav.rest.api.model.AnzeigeQuerschnittEigenschaftImpl;
import de.bitctrl.dav.rest.client.annotations.DavJsonDatensatzConverter;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.config.SystemObject;

/**
 * Konverter von {@link ResultData} in {@link AnzeigeQuerschnittEigenschaftIst}.
 *
 * @author BitCtrl Systems GmbH, ChHoesel
 */
@DavJsonDatensatzConverter(davAttributGruppe = { "atg.anzeigeQuerschnittEigenschaftIst" })
public class AnzeigeQuerschnittEigenschaftJsonConverter
		implements DavJsonConverter<ResultData, AnzeigeQuerschnittEigenschaft> {

	@Override
	public AnzeigeQuerschnittEigenschaft dav2Json(ResultData resultData) {
		final AnzeigeQuerschnittEigenschaft result = new AnzeigeQuerschnittEigenschaftImpl();
		final SystemObject object = resultData.getObject();
		result.setSystemObjektId(object.getPid());

		result.setDatenStatus(resultData.getDataState().toString());
		result.setZeitstempel(new Date(resultData.getDataTime()));
		final Data data = resultData.getData();
		if (data != null) {
			if (data.getUnscaledValue("Status").isState()) {
				result.setStatus(extraktStatus(data.getScaledValue("Status").getText()));
			} else {
				result.setStatus(StatusType.UNDEFINIERT);
			}
			
			Data eigenschaft = data.getItem("Eigenschaft");
			if(!eigenschaft.getUnscaledValue("Helligkeit").isState()) {
				result.setHelligkeit(eigenschaft.getScaledValue("Helligkeit").floatValue());
			}
			if(eigenschaft.getUnscaledValue("Betriebszustand").isState()) {
				result.setBetriebszustand(extraktBetriebszustand(eigenschaft.getScaledValue("Betriebszustand").getText()));
			}
			
		}

		return result;
	}

	private static BetriebszustandType extraktBetriebszustand(String betriebszustand) {
		switch (betriebszustand) {
		case "AQNormalbetrieb":
			return BetriebszustandType.NORMALBETRIEB;
		case "AQBlindbetrieb":
			return BetriebszustandType.BLINDBETRIEB;
		case "AQVorortbetrieb":
			return BetriebszustandType.VORORTBETRIEB;
		case "AQAutarkbetrieb":
			return BetriebszustandType.AUTARKBETRIEB;
		case "AQTestbetrieb":
			return BetriebszustandType.TESTBETRIEB;
		case "AQNotbetrieb":
			return BetriebszustandType.NOTBETRIEB;
		case "AQExterneSteuerung":
			return BetriebszustandType.EXTERNESTEUERUNG;
		case "AQTunnelbetrieb":
			return BetriebszustandType.TUNNELBETRIEB;
		default:
			return BetriebszustandType.UNDEFINIERT;
		}
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
