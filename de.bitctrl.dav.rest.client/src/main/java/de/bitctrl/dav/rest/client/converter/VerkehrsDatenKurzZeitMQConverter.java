package de.bitctrl.dav.rest.client.converter;

import java.util.Date;

import de.bitctrl.dav.rest.api.Geschwindigkeit;
import de.bitctrl.dav.rest.api.GeschwindigkeitImpl;
import de.bitctrl.dav.rest.api.OnlineDatum;
import de.bitctrl.dav.rest.api.VerkehrsdatenKurzzeit;
import de.bitctrl.dav.rest.api.VerkehrsdatenKurzzeitImpl;
import de.bitctrl.dav.rest.api.VerkehrstaerkeStunde;
import de.bitctrl.dav.rest.api.VerkehrstaerkeStundeImpl;
import de.bitctrl.dav.rest.client.annotations.DavJsonDatensatzConverter;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.ResultData;

@DavJsonDatensatzConverter(davAttributGruppe = "atg.verkehrsDatenKurzZeitMq")
public class VerkehrsDatenKurzZeitMQConverter implements DavJsonConverter<ResultData, OnlineDatum> {

	@Override
	public OnlineDatum dav2Json(ResultData resultData) {

		VerkehrsdatenKurzzeit result = new VerkehrsdatenKurzzeitImpl();
		result.setSystemObjectId(resultData.getObject().getPid());
		result.setAspekt(resultData.getDataDescription().getAspect().getName());
		result.setDatenStatus(resultData.getDataState().toString());
		result.setZeitstempel(new Date(resultData.getDataTime()));
		Data data = resultData.getData();
		if (data != null) {
			VerkehrstaerkeStunde qKfz = extraktVerkehrsStaerke(data.getItem("QKfz"));
			result.setQKfz(qKfz);
			Geschwindigkeit vKfz = extraktGeschwindigkeit(data.getItem("VKfz"));
			result.setVKfz(vKfz);
			VerkehrstaerkeStunde qLkw = extraktVerkehrsStaerke(data.getItem("QLkw"));
			result.setQLkw(qLkw);
			Geschwindigkeit vLkw = extraktGeschwindigkeit(data.getItem("VLkw"));
			result.setVLkw(vLkw);
			VerkehrstaerkeStunde qPkw = extraktVerkehrsStaerke(data.getItem("QPkw"));
			result.setQPkw(qPkw);
			Geschwindigkeit vPkw = extraktGeschwindigkeit(data.getItem("VPkw"));
			result.setVPkw(vPkw);
			
			Data b = data.getItem("B");
			if (b.getUnscaledValue("Wert").isState()) {
				result.setB(b.getUnscaledValue("Wert").doubleValue());
			}else {
				result.setB(b.getScaledValue("Wert").doubleValue());
			}
		}
		return result;
	}

	
	
	private Geschwindigkeit extraktGeschwindigkeit(Data data) {
		Geschwindigkeit vKfz = new GeschwindigkeitImpl();
		if(data.getUnscaledValue("Wert").isState()) {
			vKfz.setWert(data.getUnscaledValue("Wert").intValue());
		}else {
			vKfz.setWert(data.getScaledValue("Wert").intValue());
		}
		vKfz.setGuete(data.getItem("Güte").getItem("Index").asScaledValue().doubleValue());
		return vKfz;
	}

	private VerkehrstaerkeStunde extraktVerkehrsStaerke(Data data) {
		VerkehrstaerkeStunde qKfz = new VerkehrstaerkeStundeImpl();
		if(data.getUnscaledValue("Wert").isState()){
			qKfz.setWert(data.getUnscaledValue("Wert").intValue());
		}else {
			qKfz.setWert(data.getScaledValue("Wert").intValue());
		}
		qKfz.setGuete(data.getItem("Güte").getItem("Index").asScaledValue().doubleValue());
		return qKfz;
	}

}
