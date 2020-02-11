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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import de.bitctrl.dav.rest.api.model.VerkehrsdatenKurzzeit;
import de.bsvrz.dav.daf.communication.dataRepresentation.AttributeBaseValueDataFactory;
import de.bsvrz.dav.daf.communication.dataRepresentation.AttributeHelper;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.DataDescription;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.config.Aspect;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.SystemObject;
import de.bsvrz.puk.config.configFile.datamodel.ConfigDataModel;

/**
 * Tests zur Konvertierung von Verkehrdaten der Attributgruppe
 * "atg.verkehrsDatenKurzZeitFs" eines Fahrstreifens in das JSON Modell.
 * 
 * @author BitCtrl Systems GmbH, ChHoesel
 *
 */
@RunWith(Parameterized.class)
public class VerkehrsDatenKurzZeitIntervallFSConverterTest {

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

	@Parameters(name = "{index}: anzahl->{0}, geschwindigkeit->{1}, guete->{2}, datum->{3}, belegung->{4}, asp->{5}")
	public static Collection<Object[]> data() {

		return Arrays.asList(new Object[][] {
				// anahl Fz, geschwindigkeit, güte, datum, belegung
				{ -3, -3, 0.2d, LocalDateTime.of(2019, 1, 1, 0, 0), 0,"asp.externeErfassung","" },
				{ 3, 3, 0.9d, LocalDateTime.of(2019, 1, 1, 0, 0), 1,"asp.externeErfassung","Fz/Intervall" },
				{ -2, -2, 0.999d, LocalDateTime.of(2019, 12, 31, 23, 59), 1 ,"asp.messWertErsetzung",""},
				{ 2, 2, 0.999d, LocalDateTime.of(2019, 12, 31, 23, 59), 1 ,"asp.messWertErsetzung","Fz/Intervall"},
				{ -2, -2, 0.0, LocalDateTime.of(2019, 3, 31, 2, 0), 99 ,"asp.plausibilitätsPrüfungFormal",""},
				{ 2, 2, 0.0, LocalDateTime.of(2019, 3, 31, 2, 0), 99 ,"asp.plausibilitätsPrüfungFormal","Fz/Intervall"},
				{ -1, -1, 1d, LocalDateTime.of(2019, 2, 28, 12, 0), 100 ,"asp.plausibilitätsPrüfungLogisch",""},
				{ 1, 1, 1d, LocalDateTime.of(2019, 2, 28, 12, 0), 100 ,"asp.plausibilitätsPrüfungLogisch","Fz/Intervall"},
				{ 0, 0, 0.5d, LocalDateTime.of(2019, 9, 27, 12, 0), 1 ,"asp.plausibilitätsPrüfungFormal","Fz/Intervall"},
				{ 100, 254, 0.5d, LocalDateTime.of(2019, 3, 14, 12, 0), 55,"asp.plausibilitätsPrüfungLogisch","Fz/Intervall" },
				{ 30, 111, 1d, LocalDateTime.of(2020, 2, 10, 12, 0), 12,"asp.externeErfassung","Fz/Intervall" },
				{ 45, 22, 0.1d, LocalDateTime.of(2020,1, 10, 12, 0), 33,"asp.messWertErsetzung","Fz/Intervall" }

		});

	}

	@Parameter(0)
	public int anzahl;

	@Parameter(1)
	public int geschwindigkeit;

	@Parameter(2)
	public double guete;

	@Parameter(3)
	public LocalDateTime time;

	@Parameter(4)
	public int belegung;
	
	@Parameter(5)
	public String aspPid;
	
	@Parameter(6)
	public String dimension; 

	@Test
	public void test() {
		final SystemObject fs = dataModel.getObject("test.fs1");
		final AttributeGroup atg = dataModel.getAttributeGroup("atg.verkehrsDatenKurzZeitIntervall");
		final Aspect asp = dataModel.getAspect(aspPid);

		final Data data = AttributeBaseValueDataFactory.createAdapter(atg, AttributeHelper.getAttributesValues(atg));
		data.getItem("qKfz").getUnscaledValue("Wert").set(anzahl);
		data.getItem("qKfz").getItem("Güte").getScaledValue("Index").set(guete);
		data.getItem("vKfz").getUnscaledValue("Wert").set(geschwindigkeit);
		data.getItem("vKfz").getItem("Güte").getScaledValue("Index").set(guete);
		data.getItem("qLkw").getUnscaledValue("Wert").set(anzahl);
		data.getItem("qLkw").getItem("Güte").getScaledValue("Index").set(guete);
		data.getItem("vLkw").getUnscaledValue("Wert").set(geschwindigkeit);
		data.getItem("vLkw").getItem("Güte").getScaledValue("Index").set(guete);
		data.getItem("qPkw").getUnscaledValue("Wert").set(anzahl);
		data.getItem("qPkw").getItem("Güte").getScaledValue("Index").set(guete);
		data.getItem("vPkw").getUnscaledValue("Wert").set(geschwindigkeit);
		data.getItem("vPkw").getItem("Güte").getScaledValue("Index").set(guete);
		data.getItem("b").getScaledValue("Wert").set(belegung / 1000d);

		final ResultData rd = new ResultData(fs, new DataDescription(atg, asp),
				time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), data);
		
		
		final VerkehrsDatenKurzZeitIntervallFSConverter converter = new VerkehrsDatenKurzZeitIntervallFSConverter();
		final VerkehrsdatenKurzzeit result = converter.dav2Json(rd).iterator().next();

		Assert.assertEquals(anzahl, result.getQKfz().getWert());
		Assert.assertEquals(dimension, result.getQKfz().getDimension());
		Assert.assertEquals(anzahl, result.getQLkw().getWert());
		Assert.assertEquals(dimension, result.getQLkw().getDimension());
		Assert.assertEquals(anzahl, result.getQPkw().getWert());
		Assert.assertEquals(dimension, result.getQPkw().getDimension());
		Assert.assertEquals(geschwindigkeit, result.getVKfz().getWert());
		Assert.assertEquals(geschwindigkeit, result.getVLkw().getWert());
		Assert.assertEquals(geschwindigkeit, result.getVPkw().getWert());
		Assert.assertEquals(guete, result.getQKfz().getGuete());
		Assert.assertEquals(guete, result.getQLkw().getGuete());
		Assert.assertEquals(guete, result.getQPkw().getGuete());
	}

}
