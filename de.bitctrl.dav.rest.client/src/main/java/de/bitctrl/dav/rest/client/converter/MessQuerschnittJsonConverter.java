package de.bitctrl.dav.rest.client.converter;

import de.bitctrl.dav.rest.api.MessQuerschnitt;
import de.bitctrl.dav.rest.api.MessQuerschnittImpl;
import de.bitctrl.dav.rest.client.annotations.DavJsonObjektConverter;
import de.bsvrz.dav.daf.main.Data;
import de.bsvrz.dav.daf.main.config.AttributeGroup;
import de.bsvrz.dav.daf.main.config.DataModel;
import de.bsvrz.dav.daf.main.config.SystemObject;

@DavJsonObjektConverter(davTyp = "typ.messQuerschnitt")
public class MessQuerschnittJsonConverter implements DavJsonConverter<SystemObject, MessQuerschnitt> {


	@Override
	public MessQuerschnitt dav2Json(SystemObject davObj) {
		MessQuerschnitt result = new MessQuerschnittImpl();
		result.setId(davObj.getPid());
		result.setName(davObj.getName());

		DataModel dataModel = davObj.getDataModel();
		AttributeGroup atg = dataModel.getAttributeGroup("atg.punktKoordinaten");
		Data daten = davObj.getConfigurationData(atg);
		if (daten != null && !daten.getUnscaledValue("x").isState()) {
			result.setLaenge(daten.getScaledValue("x").doubleValue());
		}
		if (daten != null && !daten.getUnscaledValue("y").isState()) {
			result.setBreite(daten.getScaledValue("y").doubleValue());
		}

		return result;
	}

}
