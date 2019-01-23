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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import de.bitctrl.dav.rest.api.SystemObjekt;
import de.bitctrl.dav.rest.api.SystemObjektImpl;

/**
 * Beispiel Client.
 *
 * @author BitCtrl Systems GmbH, ChHoesel
 *
 */
public class ClientExample {

	protected static Client client;

	protected static final URI uri = UriBuilder.fromUri("http://localhost/").port(9998).build();

	protected static WebTarget target;

	protected static Map<String, NewCookie> cookies;

	private static class InsecureTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
				throws java.security.cert.CertificateException {
			// TODO Auto-generated method stub

		}

		@Override
		public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
				throws java.security.cert.CertificateException {
			// TODO Auto-generated method stub

		}

		@Override
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return new java.security.cert.X509Certificate[0];
		}
	}

	public static void main(String[] args) throws Exception {
		final ClientConfig config = new ClientConfig();

//		SSLContext ctx = SSLContext.getInstance("SSL");
//		TrustManager[] trustAllCerts = { new InsecureTrustManager() };
//		ctx.init(null, trustAllCerts, null);

		config.register(JacksonFeature.class);

//		ClientBuilder.newBuilder().sslContext(ctx);
		client = ClientBuilder.newClient(config);

		target = client.target(uri);

		sendetSystemObjekt();

		client.close();

	}

	private static void sendetSystemObjekt() {
		final SystemObjekt obj = new SystemObjektImpl();
		obj.setName("SystemObject1.");
		obj.setId("bubub");
		final ArrayList<SystemObjekt> list = new ArrayList<>();
		list.add(obj);
		target.path("/systemobjekte").request().post(Entity.entity(list, MediaType.APPLICATION_JSON));

		final Response response = target.path("/systemobjekte").request().get();
		final Collection<SystemObjekt> objecte = response.readEntity(new GenericType<List<SystemObjekt>>() {
		});

	}

}
