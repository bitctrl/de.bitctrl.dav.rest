package de.bitctrl.dav.rest.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import de.bitctrl.dav.rest.api.SystemObjekt;
import de.bitctrl.dav.rest.api.Systemobjekte;

public class SystemobjekteImpl implements Systemobjekte {
	
	private static Set<SystemObjekt> objectSet = ConcurrentHashMap.newKeySet();

	@Override
	public GetSystemobjekteResponse getSystemobjekte() {
		return GetSystemobjekteResponse.respond200WithApplicationJson(new ArrayList<SystemObjekt>(objectSet));
	}

	@Override
	public void postSystemobjekte(List<SystemObjekt> entity) {
		objectSet.addAll(entity);
		
	}

}
