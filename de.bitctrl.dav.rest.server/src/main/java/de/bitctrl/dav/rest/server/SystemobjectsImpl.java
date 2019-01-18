package de.bitctrl.dav.rest.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import de.bitctrl.dav.rest.api.SystemObject;
import de.bitctrl.dav.rest.api.Systemobjects;

public class SystemobjectsImpl implements Systemobjects {
	
	private static Set<SystemObject> objectSet = ConcurrentHashMap.newKeySet();

	public GetSystemobjectsResponse getSystemobjects() {
		return GetSystemobjectsResponse.respond200WithApplicationJson(new ArrayList<SystemObject>(objectSet));
	}

	public void postSystemobjects(List<SystemObject> entity) {
		objectSet.addAll(entity);
	}

}
