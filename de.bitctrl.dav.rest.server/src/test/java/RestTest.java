
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

import de.bitctrl.dav.rest.api.model.SystemObjekt;
import de.bitctrl.dav.rest.api.model.SystemObjektImpl;
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
		return new ResourceConfig(SystemobjekteImpl.class);
	}

	@Test
	public void testSystemObjects() {
		SystemObjekt obj = new SystemObjektImpl();
		obj.setName("SystemObject1.");
		obj.setId("bubub");
		ArrayList<SystemObjekt> list = new ArrayList<>();
		list.add(obj);
		target("/systemobjekte").request().post(Entity.entity(list, MediaType.APPLICATION_JSON));

		Response response = target("/systemobjekte").request().get();
		Collection<SystemObjekt> objecte = response.readEntity(new GenericType<List<SystemObjekt>>() {
		});

		Assert.assertTrue(objecte.contains(obj));
	}

}
