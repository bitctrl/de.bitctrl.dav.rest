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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.bitctrl.dav.rest.api.SystemObjekt;
import de.bitctrl.dav.rest.api.Systemobjekte;

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
	public GetSystemobjekteResponse getSystemobjekte() {
		return GetSystemobjekteResponse.respond200WithApplicationJson(new ArrayList<>(objectSet));
	}

	@Override
	public void postSystemobjekte(List<SystemObjekt> entity) {
		logger.log(Level.INFO, "Empfange: " + entity);
		objectSet.addAll(entity);
	}

}
