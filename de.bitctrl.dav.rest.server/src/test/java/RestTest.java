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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
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

		final WebTarget target = target("/systemobjekte/messquerschnitt");

		final MessQuerschnitt obj = new MessQuerschnittImpl();
		obj.setName("Mein Messquerschnitt");
		obj.setId("my.mq");
		obj.setBreite(1d);
		obj.setLaenge(12d);
		final ArrayList<SystemObjekt> list = new ArrayList<>();
		list.add(obj);
		Response response = target.request().post(Entity.entity(list, MediaType.APPLICATION_JSON));
		Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());

		response = target.request().get();
		response.bufferEntity();
		response.readEntity(String.class);
		final Collection<MessQuerschnitt> objecte = response.readEntity(new GenericType<List<MessQuerschnitt>>() {
		});
		Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
		Assert.assertTrue(objecte.contains(obj));

	}

	@Test
	public void testAnzeige() {

		final WebTarget target = target("/systemobjekte/anzeige");

		final Anzeige obj = new AnzeigeImpl();
		obj.setName("Test Anzeige");
		obj.setId("test.anzeige");

		final ArrayList<SystemObjekt> list = new ArrayList<>();
		list.add(obj);
		Response response = target.request().post(Entity.entity(list, MediaType.APPLICATION_JSON));
		Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());

		response = target.request().get();
		final Collection<Anzeige> objecte = response.readEntity(new GenericType<List<Anzeige>>() {
		});
		Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
		Assert.assertTrue(objecte.contains(obj));

	}

	@Test
	public void testAnzeigequerschnitt() {

		final WebTarget target = target("/systemobjekte/anzeigequerschnitt");

		final AnzeigeQuerschnitt obj = new AnzeigeQuerschnittImpl();
		obj.setName("Test AQ");
		obj.setId("test.aq");
		obj.setAnzeigen(Arrays.asList("test.anzeige"));

		final ArrayList<SystemObjekt> list = new ArrayList<>();
		list.add(obj);
		Response response = target.request().post(Entity.entity(list, MediaType.APPLICATION_JSON));
		Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());

		response = target.request().get();
		final Collection<AnzeigeQuerschnitt> objecte = response.readEntity(new GenericType<List<AnzeigeQuerschnitt>>() {
		});
		Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
		Assert.assertTrue(objecte.contains(obj));

	}

	@Test
	public void testFahrstreifen() {

		final WebTarget target = target("/systemobjekte/fahrstreifen");

		final FahrStreifen obj = new FahrStreifenImpl();
		obj.setName("Test Fahrstreifen");
		obj.setId("test.fs");

		final ArrayList<SystemObjekt> list = new ArrayList<>();
		list.add(obj);
		Response response = target.request().post(Entity.entity(list, MediaType.APPLICATION_JSON));
		Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());

		response = target.request().get();
		final Collection<FahrStreifen> objecte = response.readEntity(new GenericType<List<FahrStreifen>>() {
		});

		Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
		Assert.assertTrue(objecte.contains(obj));
	}

	@Test
	public void testSendeVerkehrsDatenKurzZeit() {
		final WebTarget target = target("/onlinedaten/verkehrsdatenkurzzeit");

		final VerkehrsdatenKurzzeit daten = new VerkehrsdatenKurzzeitImpl();

		final Response response = target.request()
				.post(Entity.entity(Arrays.asList(daten), MediaType.APPLICATION_JSON));

		Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());

	}

	@Test
	public void testSendeAnzeigeEigenschaft() {
		final WebTarget target = target("/onlinedaten/anzeigeeigenschaft");

		final AnzeigeEigenschaft daten = new AnzeigeEigenschaftImpl();

		final Response response = target.request()
				.post(Entity.entity(Arrays.asList(daten), MediaType.APPLICATION_JSON));

		Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());

	}

	@Test
	public void testSendeAnzeigeEigenschaftFreitext() {
		final WebTarget target = target("/onlinedaten/anzeigeeigenschaft");

		final AnzeigeEigenschaft daten = new AnzeigeEigenschaftImpl();
		daten.setSystemObjektId("test.anzeige.freitext");
		daten.setZeitstempel(
				Date.from(LocalDateTime.of(2019, 12, 2, 12, 12).atZone(ZoneId.systemDefault()).toInstant()));
		daten.setStatus(AnzeigeEigenschaft.StatusType.OK);
		daten.setDatenStatus("Nutzdaten");
		daten.setText("Hello DLR!");
		daten.setWvzInhalt(null);

		final Response response = target.request()
				.post(Entity.entity(Arrays.asList(daten), MediaType.APPLICATION_JSON));

		Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
	}

	private static byte[] toByteArray(InputStream is) throws IOException {
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			byte[] b = new byte[4096];
			int n = 0;
			while ((n = is.read(b)) != -1) {
				output.write(b, 0, n);
			}
			return output.toByteArray();
		}
	}

	@Test
	public void testSendeAnzeigeEigenschaftSymbol() throws IOException {
		final WebTarget target = target("/onlinedaten/anzeigeeigenschaft");

		final AnzeigeEigenschaft daten = new AnzeigeEigenschaftImpl();
		daten.setSystemObjektId("test.anzeige.grafik");
		daten.setZeitstempel(
				Date.from(LocalDateTime.of(2019, 12, 1, 12, 12).atZone(ZoneId.systemDefault()).toInstant()));
		daten.setStatus(AnzeigeEigenschaft.StatusType.OK);
		daten.setDatenStatus("Nutzdaten");
		daten.setWvzInhalt("Fußball");

		ClassLoader classLoader = getClass().getClassLoader();
		try (InputStream in = classLoader.getResourceAsStream("Fußball_32x32.bmp")){
			byte[] byteArray = toByteArray(in);
			if (byteArray != null && byteArray.length > 0) {
				daten.setGrafik(Base64.getEncoder().encodeToString(byteArray));
			}	
		}

		final Response response = target.request()
				.post(Entity.entity(Arrays.asList(daten), MediaType.APPLICATION_JSON));

		Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
	}
	
	@Test
	public void testSendeAnzeigenEigenschaftNurStellcode() {
		final WebTarget target = target("/onlinedaten/anzeigeeigenschaft");

		final AnzeigeEigenschaft daten = new AnzeigeEigenschaftImpl();
		daten.setSystemObjektId("test.anzeige.nur.stellcode");
		daten.setZeitstempel(
				Date.from(LocalDateTime.of(2019, 12, 3, 12, 12).atZone(ZoneId.systemDefault()).toInstant()));
		daten.setStatus(AnzeigeEigenschaft.StatusType.OK);
		daten.setDatenStatus("Nutzdaten");
		daten.setWvzInhalt("Code 42");

		final Response response = target.request()
				.post(Entity.entity(Arrays.asList(daten), MediaType.APPLICATION_JSON));

		Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
	}
	
	@Test
	public void testSendeAnzeigenEigenschaftStellcodeDunkel() {
		final WebTarget target = target("/onlinedaten/anzeigeeigenschaft");

		final AnzeigeEigenschaft daten = new AnzeigeEigenschaftImpl();
		daten.setSystemObjektId("test.anzeige.nur.stellcode");
		daten.setZeitstempel(
				Date.from(LocalDateTime.of(2019, 12, 3, 12, 13).atZone(ZoneId.systemDefault()).toInstant()));
		daten.setStatus(AnzeigeEigenschaft.StatusType.OK);
		daten.setDatenStatus("Nutzdaten");
		daten.setWvzInhalt("Code 0");

		final Response response = target.request()
				.post(Entity.entity(Arrays.asList(daten), MediaType.APPLICATION_JSON));

		Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
	}
	
	@Test
	public void testSendeAnzeigenEigenschaftGestoert() {
		final WebTarget target = target("/onlinedaten/anzeigeeigenschaft");

		final AnzeigeEigenschaft daten = new AnzeigeEigenschaftImpl();
		daten.setSystemObjektId("test.anzeige.nur.stellcode");
		daten.setZeitstempel(
				Date.from(LocalDateTime.of(2019, 12, 3, 12, 14).atZone(ZoneId.systemDefault()).toInstant()));
		daten.setStatus(AnzeigeEigenschaft.StatusType.GESTRT);
		daten.setDatenStatus("Nutzdaten");
		daten.setWvzInhalt("Code 42");

		final Response response = target.request()
				.post(Entity.entity(Arrays.asList(daten), MediaType.APPLICATION_JSON));

		Assert.assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
	}

}
