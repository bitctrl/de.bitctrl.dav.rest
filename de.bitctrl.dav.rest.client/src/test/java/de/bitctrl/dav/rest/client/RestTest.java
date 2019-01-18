package de.bitctrl.dav.rest.client;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


import de.bitctrl.dav.rest.api.SystemObject;
import de.bitctrl.dav.rest.api.SystemObjectImpl;

public class RestTest {
	
	protected static Client client;

	protected static final URI uri = UriBuilder.fromUri("http://localhost/").port(9998).build();

	protected static WebTarget target;

	protected static Map<String, NewCookie> cookies;


	@BeforeClass
	public static void beforeClass() {
		final ClientConfig config = new ClientConfig();
		config.register(JacksonFeature.class);
		client = ClientBuilder.newClient(config);

		target = client.target(uri);
		
	}
	
	@Test
	public void testSystemObjects() {
		SystemObject obj = new SystemObjectImpl();
		obj.setName("SystemObject1.");
		obj.setId("bubub");
		ArrayList<SystemObject> list = new ArrayList<>();
		list.add(obj);
		target.path("/systemobjects").request().post(Entity.entity(list, MediaType.APPLICATION_JSON));
		
		Response response = target.path("/systemobjects").request().get();
		Collection<SystemObject> objecte = response.readEntity(new GenericType<List<SystemObject>>(){} );
		
		Assert.assertTrue(objecte.contains(obj));		
	}

	@AfterClass
	public static void afterClass() {
		client.close();
	}


}
