package de.bitctrl.dav.rest.client;

import java.io.File;
import java.util.Collection;

import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.bitctrl.dav.rest.api.model.MessQuerschnitt;
import de.bitctrl.dav.rest.client.converter.MessQuerschnittJsonConverter;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.puk.config.configFile.datamodel.ConfigDataModel;

public class MessQuerschnittJsonConverterTest {

	private static ConfigDataModel dataModel;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		File konfiguration = new File("src/test/resources/konfiguration/verwaltungsdaten.xml");
		dataModel = new ConfigDataModel(konfiguration);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		dataModel.close();
	}

	@Test
	public void testMQDav2Json() {
		SystemObject mq1 = dataModel.getObject("test.mq1");

		MessQuerschnittJsonConverter konverter = new MessQuerschnittJsonConverter();
		Collection<MessQuerschnitt> resultSet = konverter.dav2Json(mq1);

		Assert.assertEquals(1, resultSet.size());
		MessQuerschnitt resultMq = resultSet.iterator().next();
		Assert.assertEquals("Test Messquerschnitt 1", resultMq.getName());
		Assert.assertEquals("test.mq1", resultMq.getId());
		Assert.assertEquals(53.029455, resultMq.getBreite());
		Assert.assertEquals(8.8609, resultMq.getLaenge());
		Assert.assertEquals(7, resultMq.getFahrstreifen().size());
		Assert.assertThat(resultMq.getFahrstreifen(), CoreMatchers.hasItems("test.fs1", "test.fs.2", "test.fs.3",
				"test.fs.4", "test.fs.5", "test.fs.6", "test.fs.7"));

	}

}
