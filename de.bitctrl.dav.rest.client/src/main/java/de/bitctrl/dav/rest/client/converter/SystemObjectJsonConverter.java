package de.bitctrl.dav.rest.client.converter;

import de.bitctrl.dav.rest.api.SystemObjekt;
import de.bitctrl.dav.rest.api.SystemObjektImpl;
import de.bitctrl.dav.rest.client.annotations.DavJsonObjektConverter;
import de.bsvrz.dav.daf.main.config.SystemObject;

@DavJsonObjektConverter(davTyp="typ.systemObjekt")
public class SystemObjectJsonConverter implements DavJsonConverter<SystemObject, SystemObjekt> {
	
	@Override
	public SystemObjekt dav2Json(SystemObject davObj) {
		SystemObjekt result = new SystemObjektImpl();
		result.setId(davObj.getPid());
		result.setName(davObj.getName());
		return result;
	}

}
