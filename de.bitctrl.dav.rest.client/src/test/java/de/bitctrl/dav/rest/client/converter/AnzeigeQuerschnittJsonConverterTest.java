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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.bitctrl.dav.rest.api.model.AnzeigeQuerschnitt;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.puk.config.configFile.datamodel.ConfigDataModel;

/**
 * Tests für die Konvertierung der Anzeigequerschnitte vom Datenverteiler Modell
 * in das JSon Modell.
 * 
 * @author BitCtrl Systems GmbH, ChHoesel
 *
 */
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
		final SystemObject sysObj = dataModel.getObject("aq.test.1");

		final AnzeigeQuerschnittJsonConverter converter = new AnzeigeQuerschnittJsonConverter();
		final AnzeigeQuerschnitt aq = converter.dav2Json(sysObj).iterator().next();

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
