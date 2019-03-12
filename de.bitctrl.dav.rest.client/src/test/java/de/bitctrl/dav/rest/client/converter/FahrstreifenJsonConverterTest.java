package de.bitctrl.dav.rest.client.converter;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.bitctrl.dav.rest.api.model.FahrStreifen;
import de.bitctrl.dav.rest.api.model.FahrstreifenLage;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.puk.config.configFile.datamodel.ConfigDataModel;

public class FahrstreifenJsonConverterTest {

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
	public void testFS1Dav2Json() {
		final SystemObject fs = dataModel.getObject("test.fs1");
		
		FahrstreifenJsonConverter converter = new FahrstreifenJsonConverter();
		FahrStreifen result = converter.dav2Json(fs).iterator().next();
		
		Assert.assertEquals("test.fs1", result.getId());
		Assert.assertEquals("Test Fahrstreifen 1", result.getName());
		Assert.assertEquals(FahrstreifenLage.HFS, result.getLage());
	}
	
	@Test
	public void testFS2Dav2Json() {
		final SystemObject fs = dataModel.getObject("test.fs.2");
		
		FahrstreifenJsonConverter converter = new FahrstreifenJsonConverter();
		FahrStreifen result = converter.dav2Json(fs).iterator().next();
		
		Assert.assertEquals("test.fs.2", result.getId());
		Assert.assertEquals("Test Fahrstreifen 2", result.getName());
		Assert.assertEquals(FahrstreifenLage._1FS, result.getLage());
	}
	
	@Test
	public void testFS3Dav2Json() {
		final SystemObject fs = dataModel.getObject("test.fs.3");
		
		FahrstreifenJsonConverter converter = new FahrstreifenJsonConverter();
		FahrStreifen result = converter.dav2Json(fs).iterator().next();
		
		Assert.assertEquals("test.fs.3", result.getId());
		Assert.assertEquals("Test Fahrstreifen 3", result.getName());
		Assert.assertEquals(FahrstreifenLage._2FS, result.getLage());
	}
	
	@Test
	public void testFS4Dav2Json() {
		final SystemObject fs = dataModel.getObject("test.fs.4");
		
		FahrstreifenJsonConverter converter = new FahrstreifenJsonConverter();
		FahrStreifen result = converter.dav2Json(fs).iterator().next();
		
		Assert.assertEquals("test.fs.4", result.getId());
		Assert.assertEquals("Test Fahrstreifen 4", result.getName());
		Assert.assertEquals(FahrstreifenLage._3FS, result.getLage());
	}

	@Test
	public void testFS5Dav2Json() {
		final SystemObject fs = dataModel.getObject("test.fs.5");
		
		FahrstreifenJsonConverter converter = new FahrstreifenJsonConverter();
		FahrStreifen result = converter.dav2Json(fs).iterator().next();
		
		Assert.assertEquals("test.fs.5", result.getId());
		Assert.assertEquals("Test Fahrstreifen 5", result.getName());
		Assert.assertEquals(FahrstreifenLage._4FS, result.getLage());
	}
	
	@Test
	public void testFS6Dav2Json() {
		final SystemObject fs = dataModel.getObject("test.fs.6");
		
		FahrstreifenJsonConverter converter = new FahrstreifenJsonConverter();
		FahrStreifen result = converter.dav2Json(fs).iterator().next();
		
		Assert.assertEquals("test.fs.6", result.getId());
		Assert.assertEquals("Test Fahrstreifen 6", result.getName());
		Assert.assertEquals(FahrstreifenLage._5FS, result.getLage());
	}
	
	@Test
	public void testFS7Dav2Json() {
		final SystemObject fs = dataModel.getObject("test.fs.7");
			
		FahrstreifenJsonConverter converter = new FahrstreifenJsonConverter();
		FahrStreifen result = converter.dav2Json(fs).iterator().next();
		
		Assert.assertEquals("test.fs.7", result.getId());
		Assert.assertEquals("Test Fahrstreifen 7", result.getName());
		Assert.assertEquals(FahrstreifenLage._6FS, result.getLage());
	}



}
