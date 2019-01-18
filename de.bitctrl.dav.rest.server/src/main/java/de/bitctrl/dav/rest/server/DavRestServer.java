package de.bitctrl.dav.rest.server;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class DavRestServer {

	public static void main(String[] args) throws Exception {

		URI baseUri = UriBuilder.fromUri("http://localhost/").port(9998).build();
		ResourceConfig config = new ResourceConfig(OnlinedatenImpl.class, SystemobjekteImpl.class);

		Server server = JettyHttpContainerFactory.createServer(baseUri, config);
		server.start();
	}

}
