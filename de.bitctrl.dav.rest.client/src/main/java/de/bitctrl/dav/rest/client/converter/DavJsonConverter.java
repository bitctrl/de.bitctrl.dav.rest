package de.bitctrl.dav.rest.client.converter;

public interface DavJsonConverter<D,J> {
	
	J dav2Json(D davObj);

}
