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
import java.util.Collection;

import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.bitctrl.dav.rest.api.model.MessQuerschnitt;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.puk.config.configFile.datamodel.ConfigDataModel;

/**
 * Tests für die Konvertierung der Messquerschnitte vom Datenverteiler Modell in
 * das JSON Modell.
 * 
 * @author BitCtrl Systems GmbH, ChHoesel
 *
 */
public class MessQuerschnittJsonConverterTest {

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
	public void testMQDav2Json() {
		final SystemObject mq1 = dataModel.getObject("test.mq1");

		final MessQuerschnittJsonConverter konverter = new MessQuerschnittJsonConverter();
		final Collection<MessQuerschnitt> resultSet = konverter.dav2Json(mq1);

		Assert.assertEquals(1, resultSet.size());
		final MessQuerschnitt resultMq = resultSet.iterator().next();
		Assert.assertEquals("Test Messquerschnitt 1", resultMq.getName());
		Assert.assertEquals("test.mq1", resultMq.getId());
		Assert.assertEquals(53.029455, resultMq.getBreite());
		Assert.assertEquals(8.8609, resultMq.getLaenge());
		Assert.assertEquals(7, resultMq.getFahrstreifen().size());
		Assert.assertThat(resultMq.getFahrstreifen(), CoreMatchers.hasItems("test.fs1", "test.fs.2", "test.fs.3",
				"test.fs.4", "test.fs.5", "test.fs.6", "test.fs.7"));

	}

}
