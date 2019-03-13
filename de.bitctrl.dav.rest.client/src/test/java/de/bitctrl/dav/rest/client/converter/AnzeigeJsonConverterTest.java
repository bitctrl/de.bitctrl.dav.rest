package de.bitctrl.dav.rest.client.converter;

import java.io.File;

import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.bitctrl.dav.rest.api.model.Anzeige;
import de.bitctrl.dav.rest.api.model.FahrstreifenLage;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.puk.config.configFile.datamodel.ConfigDataModel;

public class AnzeigeJsonConverterTest {
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
	public void testFreitextAnzeige() {
		SystemObject anz = dataModel.getObject("anz.test.freitext.224x24");

		AnzeigeJsonConverter converter = new AnzeigeJsonConverter();
		Anzeige result = converter.dav2Json(anz).iterator().next();

		Assert.assertEquals("anz.test.freitext.224x24", result.getId());
		Assert.assertEquals("Anzeige Test Freitext 224x24", result.getName());
		Assert.assertThat(result.getFahrstreifen(), CoreMatchers.hasItems(FahrstreifenLage.values()));
	}

	@Test
	public void testWzgAHfs() {
		SystemObject anz = dataModel.getObject("anz.test.wzg.a.hfs");

		AnzeigeJsonConverter converter = new AnzeigeJsonConverter();
		Anzeige result = converter.dav2Json(anz).iterator().next();

		Assert.assertEquals("anz.test.wzg.a.hfs", result.getId());
		Assert.assertEquals("Anzeige Test WZG-A HFS", result.getName());
		Assert.assertThat(result.getFahrstreifen(), CoreMatchers.hasItems(FahrstreifenLage.HFS));
	}

	@Test
	public void testWzgBHfs1Ufs() {
		SystemObject anz = dataModel.getObject("anz.test.wzg.b.hfs1ufs");

		AnzeigeJsonConverter converter = new AnzeigeJsonConverter();
		Anzeige result = converter.dav2Json(anz).iterator().next();

		Assert.assertEquals("anz.test.wzg.b.hfs1ufs", result.getId());
		Assert.assertEquals("Anzeige Test WZG-B HFS 1UFS", result.getName());
		Assert.assertThat(result.getFahrstreifen(), CoreMatchers.hasItems(FahrstreifenLage.HFS, FahrstreifenLage._1FS));
	}

	@Test
	public void testWzgC1Ufs2Ufs() {
		SystemObject anz = dataModel.getObject("anz.test.wzg.c.1ufs2ufs");

		AnzeigeJsonConverter converter = new AnzeigeJsonConverter();
		Anzeige result = converter.dav2Json(anz).iterator().next();

		Assert.assertEquals("anz.test.wzg.c.1ufs2ufs", result.getId());
		Assert.assertEquals("Anzeige Test WZG-C 1UFS2UFS", result.getName());
		Assert.assertThat(result.getFahrstreifen(),
				CoreMatchers.hasItems(FahrstreifenLage._1FS, FahrstreifenLage._2FS));
	}
	
	@Test
	public void testSymbol1Links() {
		SystemObject anz = dataModel.getObject("anz.test.symbol.1.links");

		AnzeigeJsonConverter converter = new AnzeigeJsonConverter();
		Anzeige result = converter.dav2Json(anz).iterator().next();

		Assert.assertEquals("anz.test.symbol.1.links", result.getId());
		Assert.assertEquals("Anzeige Test Symbol 1 Links", result.getName());
		Assert.assertThat(result.getFahrstreifen(),
				CoreMatchers.hasItems(FahrstreifenLage._1FS));
	}

}
