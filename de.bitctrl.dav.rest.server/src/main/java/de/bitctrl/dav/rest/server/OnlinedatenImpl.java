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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.validation.Valid;

import de.bitctrl.dav.rest.api.Onlinedaten;
import de.bitctrl.dav.rest.api.model.AnzeigeEigenschaft;
import de.bitctrl.dav.rest.api.model.AnzeigeQuerschnittEigenschaft;
import de.bitctrl.dav.rest.api.model.AnzeigeQuerschnittHelligkeitsMeldung;
import de.bitctrl.dav.rest.api.model.GmaUmfelddaten;
import de.bitctrl.dav.rest.api.model.VerkehrsdatenKurzzeit;

/**
 * Verarbeiten der {@link Onlinedaten}.
 *
 * @author BitCtrl Systems GmbH, ChHoesel
 *
 */
public class OnlinedatenImpl implements Onlinedaten {

	private final Logger logger = Logger.getLogger(getClass().getName());

	@Override
	public void postOnlinedatenVerkehrsdatenkurzzeit(@Valid List<VerkehrsdatenKurzzeit> entity) {
		logger.log(Level.INFO, "Empfange " + entity.size() + " VerkehrsdatenKurzzeit: " + entity);
	}

	@Override
	public void postOnlinedatenAnzeigeeigenschaft(@Valid List<AnzeigeEigenschaft> entity) {
		logger.log(Level.INFO, "Empfange " + entity.size() + " AnzeigeEigenschaft: " + entity);
		
		
	}

	@Override
	public void postOnlinedatenAnzeigequerschnitteigenschaft(@Valid List<AnzeigeQuerschnittEigenschaft> entity) {
		logger.log(Level.INFO, "Empfange " + entity.size() + " AnzeigeQuerschnittEigenschaft: " + entity);
	}

	@Override
	public void postOnlinedatenAnzeigequerschnitthelligkeitsmeldung(
			@Valid List<AnzeigeQuerschnittHelligkeitsMeldung> entity) {
		logger.log(Level.INFO, "Empfange " + entity.size() + " AnzeigeQuerschnittHelligkeitsMeldung: " + entity);
		
	}

	@Override
	public void postOnlinedatenGmaumfelddaten(List<GmaUmfelddaten> entity) {
		logger.log(Level.INFO, "Empfange " + entity.size() + " GMAUmfelddaten: " + entity);
		
	}


}
