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

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Beispiel-Implementierung für einen REST Server.
 *
 * @author BitCtrl Systems GmbH, ChHoesel
 *
 */
public class DavRestServer {

	public static void main(String[] args) throws Exception {

		final URI baseUri = UriBuilder.fromUri("http://localhost/").port(9998).build();
		final ResourceConfig config = new ResourceConfig(OnlinedatenImpl.class, SystemobjekteImpl.class);

//		SslContextFactory sslContextFactory = new SslContextFactory(true);
//		Server server = JettyHttpContainerFactory.createServer(baseUri, sslContextFactory,config);
		final Server server = JettyHttpContainerFactory.createServer(baseUri, config);
		server.start();
	}

}
