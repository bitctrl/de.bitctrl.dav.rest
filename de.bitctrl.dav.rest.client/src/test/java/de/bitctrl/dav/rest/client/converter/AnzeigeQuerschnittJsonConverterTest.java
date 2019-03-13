package de.bitctrl.dav.rest.client.converter;

import static org.junit.Assert.*;

import java.io.File;

import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.bitctrl.dav.rest.api.model.AnzeigeQuerschnitt;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.puk.config.configFile.datamodel.ConfigDataModel;

public class AnzeigeQuerschnittJsonConverterTest {

	private static ConfigDataModel dataModel;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		final File konfiguration = new File("src/test/resources/konfiguration/verwaltungsdaten.xml");
		dataModel = new ConfigDataModel(konfiguration);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		dataModel.close();
	}

	@Test
	public void testAq1() {
		SystemObject sysObj = dataModel.getObject("aq.test.1");

		AnzeigeQuerschnittJsonConverter converter = new AnzeigeQuerschnittJsonConverter();
		AnzeigeQuerschnitt aq = converter.dav2Json(sysObj).iterator().next();

		assertEquals("aq.test.1", aq.getId());
		assertEquals("AnzeigeQuerschnitt Test 1", aq.getName());
		assertEquals(52.336232, aq.getBreite());
		assertEquals(10.41158, aq.getLaenge());
		assertThat(aq.getAnzeigen(),
				CoreMatchers.hasItems("ant.test.wzg.a.rechts", "anz.test.freitext.224x24", "anz.test.symbol.1.links",
						"anz.test.symbol.2.3ufs.4ufs.5ufs.6ufs", "anz.test.wzg.a.hfs", "anz.test.wzg.b.hfs1ufs",
						"anz.test.wzg.c.1ufs2ufs"));
	}

}
