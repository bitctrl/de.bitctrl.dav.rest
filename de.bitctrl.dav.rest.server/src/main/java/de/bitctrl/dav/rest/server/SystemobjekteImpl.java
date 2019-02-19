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
package de.bitctrl.dav.rest.server;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.validation.Valid;

import de.bitctrl.dav.rest.api.Systemobjekte;
import de.bitctrl.dav.rest.api.model.Anzeige;
import de.bitctrl.dav.rest.api.model.AnzeigeQuerschnitt;
import de.bitctrl.dav.rest.api.model.FahrStreifen;
import de.bitctrl.dav.rest.api.model.Glaettemeldeanlage;
import de.bitctrl.dav.rest.api.model.MessQuerschnitt;
import de.bitctrl.dav.rest.api.model.SystemObjekt;

/**
 * Verarbeiten der {@link Systemobjekte}.
 *
 * @author BitCtrl Systems GmbH, ChHoesel
 *
 */
public class SystemobjekteImpl implements Systemobjekte {

	private static Set<SystemObjekt> objectSet = ConcurrentHashMap.newKeySet();

	private final Logger logger = Logger.getLogger(getClass().getName());

	@Override
	public void postSystemobjekteMessquerschnitt(@Valid List<MessQuerschnitt> entity) {
		logger.log(Level.INFO, "Empfange " + entity.size() + " Messquerschnitte: " + entity);
		objectSet.addAll(entity);

	}

	@Override
	public void postSystemobjekteAnzeigequerschnitt(@Valid List<AnzeigeQuerschnitt> entity) {
		logger.log(Level.INFO, "Empfange " + entity.size() + " AnzeigeQuerschnitt" + entity);
		objectSet.addAll(entity);

	}

	@Override
	public void postSystemobjekteAnzeige(@Valid List<Anzeige> entity) {
		logger.log(Level.INFO, "Empfange " + entity.size() + " Anzeigen" + entity);
		objectSet.addAll(entity);

	}

	@Override
	public GetSystemobjekteMessquerschnittResponse getSystemobjekteMessquerschnitt() {
		return GetSystemobjekteMessquerschnittResponse.respond200WithApplicationJson(objectSet.stream()
				.filter(o -> o instanceof MessQuerschnitt).map(o -> (MessQuerschnitt) o).collect(Collectors.toList()));
	}

	@Override
	public GetSystemobjekteAnzeigequerschnittResponse getSystemobjekteAnzeigequerschnitt() {
		return GetSystemobjekteAnzeigequerschnittResponse
				.respond200WithApplicationJson(objectSet.stream().filter(o -> o instanceof AnzeigeQuerschnitt)
						.map(o -> (AnzeigeQuerschnitt) o).collect(Collectors.toList()));
	}

	@Override
	public GetSystemobjekteAnzeigeResponse getSystemobjekteAnzeige() {
		return GetSystemobjekteAnzeigeResponse.respond200WithApplicationJson(objectSet.stream()
				.filter(o -> o instanceof Anzeige).map(o -> (Anzeige) o).collect(Collectors.toList()));
	}

	@Override
	public void postSystemobjekteFahrstreifen(@Valid List<FahrStreifen> entity) {
		logger.log(Level.INFO, "Empfange " + entity.size() + " Fahrstreifen" + entity);
		objectSet.addAll(entity);

	}

	@Override
	public GetSystemobjekteFahrstreifenResponse getSystemobjekteFahrstreifen() {
		return GetSystemobjekteFahrstreifenResponse.respond200WithApplicationJson(objectSet.stream()
				.filter(o -> o instanceof FahrStreifen).map(o -> (FahrStreifen) o).collect(Collectors.toList()));
	}

	@Override
	public void postSystemobjekteGlaettemeldeanlage(@Valid List<Glaettemeldeanlage> entity) {
		logger.log(Level.INFO, "Empfange " + entity.size() + " Glättemeldeanlagen" + entity);
		objectSet.addAll(entity);
	}

	@Override
	public GetSystemobjekteGlaettemeldeanlageResponse getSystemobjekteGlaettemeldeanlage() {
		return GetSystemobjekteGlaettemeldeanlageResponse.respond200WithApplicationJson(objectSet.stream()
				.filter(o -> o instanceof Glaettemeldeanlage).map(o -> (Glaettemeldeanlage) o).collect(Collectors.toList()));
	}

}
