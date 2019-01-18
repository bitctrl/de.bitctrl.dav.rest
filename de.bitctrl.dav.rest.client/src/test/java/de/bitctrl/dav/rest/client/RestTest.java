package de.bitctrl.dav.rest.client;

import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
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
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.bitctrl.dav.rest.api.SystemObjekt;
import de.bitctrl.dav.rest.api.SystemObjektImpl;

public class RestTest {
	
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


	@BeforeClass
	public static void beforeClass() throws NoSuchAlgorithmException, KeyManagementException {
		final ClientConfig config = new ClientConfig();
		
//		SSLContext ctx = SSLContext.getInstance("SSL");
//		TrustManager[] trustAllCerts = { new InsecureTrustManager() };
//		ctx.init(null, trustAllCerts, null);
		
		config.register(JacksonFeature.class);
		
		
//		ClientBuilder.newBuilder().sslContext(ctx);
		client = ClientBuilder.newClient(config);

		target = client.target(uri);
		
	}
	
	@Test
	public void testSystemObjects() {
		SystemObjekt obj = new SystemObjektImpl();
		obj.setName("SystemObject1.");
		obj.setId("bubub");
		ArrayList<SystemObjekt> list = new ArrayList<>();
		list.add(obj);
		target.path("/systemobjekte").request().post(Entity.entity(list, MediaType.APPLICATION_JSON));
		
		Response response = target.path("/systemobjekte").request().get();
		Collection<SystemObjekt> objecte = response.readEntity(new GenericType<List<SystemObjekt>>(){} );
		
		Assert.assertTrue(objecte.contains(obj));		
	}

	@AfterClass
	public static void afterClass() {
		client.close();
	}


}
