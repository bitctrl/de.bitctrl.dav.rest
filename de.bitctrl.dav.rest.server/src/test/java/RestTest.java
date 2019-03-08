
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

import de.bitctrl.dav.rest.api.model.Anzeige;
import de.bitctrl.dav.rest.api.model.AnzeigeEigenschaft;
import de.bitctrl.dav.rest.api.model.AnzeigeEigenschaftImpl;
import de.bitctrl.dav.rest.api.model.AnzeigeImpl;
import de.bitctrl.dav.rest.api.model.AnzeigeQuerschnitt;
import de.bitctrl.dav.rest.api.model.AnzeigeQuerschnittImpl;
import de.bitctrl.dav.rest.api.model.FahrStreifen;
import de.bitctrl.dav.rest.api.model.FahrStreifenImpl;
import de.bitctrl.dav.rest.api.model.MessQuerschnitt;
import de.bitctrl.dav.rest.api.model.MessQuerschnittImpl;
import de.bitctrl.dav.rest.api.model.SystemObjekt;
import de.bitctrl.dav.rest.api.model.VerkehrsdatenKurzzeit;
import de.bitctrl.dav.rest.api.model.VerkehrsdatenKurzzeitImpl;
import de.bitctrl.dav.rest.server.OnlinedatenImpl;
import de.bitctrl.dav.rest.server.SystemobjekteImpl;

/**
 * Tests der REST Schnittstelle des Servers.
 * 
 * @author BitCtrl Systems GmbH, ChHoesel
 *
 */
public class RestTest extends JerseyTest {

	@Override
	protected Application configure() {
		return new ResourceConfig(SystemobjekteImpl.class, OnlinedatenImpl.class);
	}

	@Test
	public void testMessquerschnitt() {

		WebTarget target = target("/systemobjekte/messquerschnitt");

		MessQuerschnitt obj = new MessQuerschnittImpl();
		obj.setName("Mein Messquerschnitt");
		obj.setId("my.mq");
		obj.setBreite(1d);
		obj.setLaenge(12d);
		ArrayList<SystemObjekt> list = new ArrayList<>();
		list.add(obj);
		Response response = target.request().post(Entity.entity(list, MediaType.APPLICATION_JSON));
		Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());

		response = target.request().get();
		response.bufferEntity();
		response.readEntity(String.class);
		Collection<MessQuerschnitt> objecte = response.readEntity(new GenericType<List<MessQuerschnitt>>() {
		});
		Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
		Assert.assertTrue(objecte.contains(obj));

	}

	@Test
	public void testAnzeige() {

		WebTarget target = target("/systemobjekte/anzeige");

		Anzeige obj = new AnzeigeImpl();
		obj.setName("Test Anzeige");
		obj.setId("test.anzeige");

		ArrayList<SystemObjekt> list = new ArrayList<>();
		list.add(obj);
		Response response = target.request().post(Entity.entity(list, MediaType.APPLICATION_JSON));
		Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());

		response = target.request().get();
		Collection<Anzeige> objecte = response.readEntity(new GenericType<List<Anzeige>>() {
		});
		Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
		Assert.assertTrue(objecte.contains(obj));

	}

	@Test
	public void testAnzeigequerschnitt() {

		WebTarget target = target("/systemobjekte/anzeigequerschnitt");

		AnzeigeQuerschnitt obj = new AnzeigeQuerschnittImpl();
		obj.setName("Test AQ");
		obj.setId("test.aq");
		obj.setAnzeigen(Arrays.asList("test.anzeige"));

		ArrayList<SystemObjekt> list = new ArrayList<>();
		list.add(obj);
		Response response = target.request().post(Entity.entity(list, MediaType.APPLICATION_JSON));
		Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());

		response = target.request().get();
		Collection<AnzeigeQuerschnitt> objecte = response.readEntity(new GenericType<List<AnzeigeQuerschnitt>>() {
		});
		Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
		Assert.assertTrue(objecte.contains(obj));

	}

	@Test
	public void testFahrstreifen() {

		WebTarget target = target("/systemobjekte/fahrstreifen");

		FahrStreifen obj = new FahrStreifenImpl();
		obj.setName("Test Fahrstreifen");
		obj.setId("test.fs");

		ArrayList<SystemObjekt> list = new ArrayList<>();
		list.add(obj);
		Response response = target.request().post(Entity.entity(list, MediaType.APPLICATION_JSON));
		Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());

		response = target.request().get();
		Collection<FahrStreifen> objecte = response.readEntity(new GenericType<List<FahrStreifen>>() {
		});

		Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
		Assert.assertTrue(objecte.contains(obj));
	}

	@Test
	public void testSendeVerkehrsDatenKurzZeit() {
		WebTarget target = target("/onlinedaten/verkehrsdatenkurzzeit");

		VerkehrsdatenKurzzeit daten = new VerkehrsdatenKurzzeitImpl();

		Response response = target.request().post(Entity.entity(Arrays.asList(daten), MediaType.APPLICATION_JSON));

		Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());

	}
	
	@Test
	public void testSendeAnzeigeEigenschaft() {
		WebTarget target = target("/onlinedaten/anzeigeeigenschaft");

		AnzeigeEigenschaft daten = new AnzeigeEigenschaftImpl();

		Response response = target.request().post(Entity.entity(Arrays.asList(daten), MediaType.APPLICATION_JSON));

		Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());

	}

}
