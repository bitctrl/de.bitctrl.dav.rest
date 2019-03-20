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

		final FahrstreifenJsonConverter converter = new FahrstreifenJsonConverter();
		final FahrStreifen result = converter.dav2Json(fs).iterator().next();

		Assert.assertEquals("test.fs1", result.getId());
		Assert.assertEquals("Test Fahrstreifen 1", result.getName());
		Assert.assertEquals(FahrstreifenLage.HFS, result.getLage());
	}

	@Test
	public void testFS2Dav2Json() {
		final SystemObject fs = dataModel.getObject("test.fs.2");

		final FahrstreifenJsonConverter converter = new FahrstreifenJsonConverter();
		final FahrStreifen result = converter.dav2Json(fs).iterator().next();

		Assert.assertEquals("test.fs.2", result.getId());
		Assert.assertEquals("Test Fahrstreifen 2", result.getName());
		Assert.assertEquals(FahrstreifenLage._1FS, result.getLage());
	}

	@Test
	public void testFS3Dav2Json() {
		final SystemObject fs = dataModel.getObject("test.fs.3");

		final FahrstreifenJsonConverter converter = new FahrstreifenJsonConverter();
		final FahrStreifen result = converter.dav2Json(fs).iterator().next();

		Assert.assertEquals("test.fs.3", result.getId());
		Assert.assertEquals("Test Fahrstreifen 3", result.getName());
		Assert.assertEquals(FahrstreifenLage._2FS, result.getLage());
	}

	@Test
	public void testFS4Dav2Json() {
		final SystemObject fs = dataModel.getObject("test.fs.4");

		final FahrstreifenJsonConverter converter = new FahrstreifenJsonConverter();
		final FahrStreifen result = converter.dav2Json(fs).iterator().next();

		Assert.assertEquals("test.fs.4", result.getId());
		Assert.assertEquals("Test Fahrstreifen 4", result.getName());
		Assert.assertEquals(FahrstreifenLage._3FS, result.getLage());
	}

	@Test
	public void testFS5Dav2Json() {
		final SystemObject fs = dataModel.getObject("test.fs.5");

		final FahrstreifenJsonConverter converter = new FahrstreifenJsonConverter();
		final FahrStreifen result = converter.dav2Json(fs).iterator().next();

		Assert.assertEquals("test.fs.5", result.getId());
		Assert.assertEquals("Test Fahrstreifen 5", result.getName());
		Assert.assertEquals(FahrstreifenLage._4FS, result.getLage());
	}

	@Test
	public void testFS6Dav2Json() {
		final SystemObject fs = dataModel.getObject("test.fs.6");

		final FahrstreifenJsonConverter converter = new FahrstreifenJsonConverter();
		final FahrStreifen result = converter.dav2Json(fs).iterator().next();

		Assert.assertEquals("test.fs.6", result.getId());
		Assert.assertEquals("Test Fahrstreifen 6", result.getName());
		Assert.assertEquals(FahrstreifenLage._5FS, result.getLage());
	}

	@Test
	public void testFS7Dav2Json() {
		final SystemObject fs = dataModel.getObject("test.fs.7");

		final FahrstreifenJsonConverter converter = new FahrstreifenJsonConverter();
		final FahrStreifen result = converter.dav2Json(fs).iterator().next();

		Assert.assertEquals("test.fs.7", result.getId());
		Assert.assertEquals("Test Fahrstreifen 7", result.getName());
		Assert.assertEquals(FahrstreifenLage._6FS, result.getLage());
	}

}
