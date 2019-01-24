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
package de.bitctrl.dav.rest.client;

import java.net.URI;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import de.bsvrz.dav.daf.main.ClientDavInterface;
import de.bsvrz.dav.daf.main.DavConnectionListener;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.sys.funclib.application.StandardApplication;
import de.bsvrz.sys.funclib.application.StandardApplicationRunner;
import de.bsvrz.sys.funclib.commandLineArgs.ArgumentList;

/**
 * REST Client Applikation, die sich am Datenverteiler anmeldet und gemäß der entsprechenden
 * Parametrierung die Daten aus dem Datenverteiler an einen REST Server
 * versendet.
 * 
 * @author BitCtrl Systems GmbH, ChHoesel
 *
 */
public class Client implements StandardApplication, DavConnectionListener {

	private ClientDavInterface davConnection;

	public static void main(final String[] args) {
		StandardApplicationRunner.run(new Client(), args);
	}

	private String archivObjektPid;
	private String serverUrl;
	private String port;
	private javax.ws.rs.client.Client client;

	@Override
	public void parseArguments(ArgumentList argumentList) throws Exception {
		archivObjektPid = argumentList.fetchArgument("-objekt=").asString();
		serverUrl = argumentList.fetchArgument("-url=http://localhost").asString();
		port = argumentList.fetchArgument("-port=9998").asString();

	}

	@Override
	public void initialize(ClientDavInterface connection) throws Exception {
		davConnection = connection;
		davConnection.addConnectionListener(this);

		SystemObject archivObject = connection.getLocalConfigurationAuthority();
		if (archivObjektPid != null && !archivObjektPid.isEmpty()) {
			archivObject = connection.getDataModel().getObject(archivObjektPid);
			if (archivObject == null) {
				throw new IllegalArgumentException(
						"Das übergebene Archiv-Objekt " + archivObjektPid + " existiert nicht.");
			} else if (!archivObject.isOfType("typ.archiv")) {
				throw new IllegalArgumentException(
						"Das übergebene Archiv-Objekt " + archivObjektPid + " ist nicht vom Typ \"typ.archiv\".");
			}
		}

		final ClientConfig config = new ClientConfig();
		config.register(JacksonFeature.class);

//		ClientBuilder.newBuilder().sslContext(ctx);
		client = ClientBuilder.newClient(config);
		final URI uri = UriBuilder.fromUri(serverUrl).port(Integer.parseInt(port)).build();
		final WebTarget target = client.target(uri);

		final Dav2RestSender restSender = new Dav2RestSender(target, connection, archivObject);
		restSender.anmelden();
	}

	@Override
	public void connectionClosed(ClientDavInterface connection) {
		client.close();

	}

}
