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

import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.bitctrl.dav.rest.api.model.Anzeige;
import de.bitctrl.dav.rest.api.model.FahrstreifenLage;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.puk.config.configFile.datamodel.ConfigDataModel;

/**
 * Tests für die Konvertierung der Anzeigen vom Datenverteiler Modell ins Json
 * Modell.
 * 
 * @author BitCtrl Systems GmbH, ChHoesel
 *
 */
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
		final SystemObject anz = dataModel.getObject("anz.test.freitext.224x24");

		final AnzeigeJsonConverter converter = new AnzeigeJsonConverter();
		final Anzeige result = converter.dav2Json(anz).iterator().next();

		Assert.assertEquals("anz.test.freitext.224x24", result.getId());
		Assert.assertEquals("Anzeige Test Freitext 224x24", result.getName());
		Assert.assertTrue(result.getWvzInhalte().isEmpty());
		Assert.assertThat(result.getFahrstreifen(), CoreMatchers.hasItems(FahrstreifenLage.values()));
	}

	@Test
	public void testWzgAHfs() {
		final SystemObject anz = dataModel.getObject("anz.test.wzg.a.hfs");

		final AnzeigeJsonConverter converter = new AnzeigeJsonConverter();
		final Anzeige result = converter.dav2Json(anz).iterator().next();

		Assert.assertEquals("anz.test.wzg.a.hfs", result.getId());
		Assert.assertEquals("Anzeige Test WZG-A HFS", result.getName());
		Assert.assertThat(result.getWvzInhalte(),
				CoreMatchers.hasItems("100 km/h", "StVO-Nr. 280 + 1049-13", "Schleudergefahr", "Stau", "unfall"));
		Assert.assertThat(result.getFahrstreifen(), CoreMatchers.hasItems(FahrstreifenLage.HFS));
	}

	@Test
	public void testWzgBHfs1Ufs() {
		final SystemObject anz = dataModel.getObject("anz.test.wzg.b.hfs1ufs");

		final AnzeigeJsonConverter converter = new AnzeigeJsonConverter();
		final Anzeige result = converter.dav2Json(anz).iterator().next();

		Assert.assertEquals("anz.test.wzg.b.hfs1ufs", result.getId());
		Assert.assertEquals("Anzeige Test WZG-B HFS 1UFS", result.getName());
		Assert.assertThat(result.getWvzInhalte(), CoreMatchers.hasItems("StVO-Nr. 280 + 1049-13", "Schleudergefahr"));
		Assert.assertThat(result.getFahrstreifen(), CoreMatchers.hasItems(FahrstreifenLage.HFS, FahrstreifenLage._1FS));
	}

	@Test
	public void testWzgC1Ufs2Ufs() {
		final SystemObject anz = dataModel.getObject("anz.test.wzg.c.1ufs2ufs");

		final AnzeigeJsonConverter converter = new AnzeigeJsonConverter();
		final Anzeige result = converter.dav2Json(anz).iterator().next();

		Assert.assertEquals("anz.test.wzg.c.1ufs2ufs", result.getId());
		Assert.assertEquals("Anzeige Test WZG-C 1UFS2UFS", result.getName());
		Assert.assertThat(result.getWvzInhalte(), CoreMatchers.hasItems("100 km/h"));
		Assert.assertThat(result.getFahrstreifen(),
				CoreMatchers.hasItems(FahrstreifenLage._1FS, FahrstreifenLage._2FS));
	}

	@Test
	public void testSymbol1Links() {
		final SystemObject anz = dataModel.getObject("anz.test.symbol.1.links");

		final AnzeigeJsonConverter converter = new AnzeigeJsonConverter();
		final Anzeige result = converter.dav2Json(anz).iterator().next();

		Assert.assertEquals("anz.test.symbol.1.links", result.getId());
		Assert.assertEquals("Anzeige Test Symbol 1 Links", result.getName());
		Assert.assertTrue(result.getWvzInhalte().isEmpty());
		Assert.assertThat(result.getFahrstreifen(), CoreMatchers.hasItems(FahrstreifenLage._1FS));
	}

	@Test
	public void testWzgARechts() {
		final SystemObject anz = dataModel.getObject("ant.test.wzg.a.rechts");

		final AnzeigeJsonConverter converter = new AnzeigeJsonConverter();
		final Anzeige result = converter.dav2Json(anz).iterator().next();

		Assert.assertEquals("ant.test.wzg.a.rechts", result.getId());
		Assert.assertEquals("Anzeige Test WZG-A Rechts", result.getName());
		Assert.assertThat(result.getWvzInhalte(),
				CoreMatchers.hasItems("100 km/h", "StVO-Nr. 280 + 1049-13", "Schleudergefahr", "Stau", "unfall"));
		Assert.assertThat(result.getFahrstreifen(), CoreMatchers.hasItems(FahrstreifenLage.HFS));
	}

	@Test
	public void testSymbol23UFS4UFS5UFS6UFS() {
		final SystemObject anz = dataModel.getObject("anz.test.symbol.2.3ufs.4ufs.5ufs.6ufs");

		final AnzeigeJsonConverter converter = new AnzeigeJsonConverter();
		final Anzeige result = converter.dav2Json(anz).iterator().next();

		Assert.assertEquals("anz.test.symbol.2.3ufs.4ufs.5ufs.6ufs", result.getId());
		Assert.assertEquals("Anzeige Test Symbol 2 3UFS 4UFS 5UFS 6UFS", result.getName());
		Assert.assertTrue(result.getWvzInhalte().isEmpty());
		Assert.assertThat(result.getFahrstreifen(), CoreMatchers.hasItems(FahrstreifenLage._3FS, FahrstreifenLage._4FS,
				FahrstreifenLage._5FS, FahrstreifenLage._6FS));
	}

}
