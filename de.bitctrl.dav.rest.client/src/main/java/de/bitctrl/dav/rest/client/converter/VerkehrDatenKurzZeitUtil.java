package de.bitctrl.dav.rest.client.converter;

import de.bitctrl.dav.rest.api.model.Geschwindigkeit;
import de.bitctrl.dav.rest.api.model.GeschwindigkeitImpl;
import de.bitctrl.dav.rest.api.model.VerkehrsdatenKurzzeit;
import de.bitctrl.dav.rest.api.model.VerkehrsdatenKurzzeit.AspektType;
import de.bitctrl.dav.rest.api.model.VerkehrstaerkeStunde;
import de.bitctrl.dav.rest.api.model.VerkehrstaerkeStundeImpl;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.ResultData;
import de.bsvrz.dav.daf.main.config.Aspect;

public class VerkehrDatenKurzZeitUtil {

	static VerkehrsdatenKurzzeit.AspektType extractAspekt(ResultData resultData) {
		final Aspect aspect = resultData.getDataDescription().getAspect();
		switch (aspect.getPid()) {
		case "asp.agregation1Minute":
			return AspektType.AGGREGATION1MINUTE;
		case "asp.agregation5Minuten":
			return AspektType.AGGREGATION5MINUTEN;
		case "asp.agregation15Minuten":
			return AspektType.AGGREGATION15MINUTEN;
		case "asp.agregation30Minuten":
			return AspektType.AGGREGATION30MINUTEN;
		case "asp.agregation60Minuten":
			return AspektType.AGGREGATION60MINUTEN;
		case "asp.analyseHB":
			return AspektType.ANALYSEHB;
		default:
			return AspektType.ANALYSE;
	
		}
	}

	static Geschwindigkeit extraktGeschwindigkeit(Data data) {
		final Geschwindigkeit vKfz = new GeschwindigkeitImpl();
		if (data.getUnscaledValue("Wert").isState()) {
			vKfz.setWert(data.getUnscaledValue("Wert").intValue());
		} else {
			vKfz.setWert(data.getScaledValue("Wert").intValue());
		}
		vKfz.setGuete(data.getItem("Güte").getItem("Index").asScaledValue().doubleValue());
		return vKfz;
	}

	static VerkehrstaerkeStunde extraktVerkehrsStaerke(Data data) {
		final VerkehrstaerkeStunde qKfz = new VerkehrstaerkeStundeImpl();
		if (data.getUnscaledValue("Wert").isState()) {
			qKfz.setWert(data.getUnscaledValue("Wert").intValue());
		} else {
			qKfz.setWert(data.getScaledValue("Wert").intValue());
		}
		qKfz.setGuete(data.getItem("Güte").getItem("Index").asScaledValue().doubleValue());
		return qKfz;
	}

}
