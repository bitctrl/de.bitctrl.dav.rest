package de.bitctrl.dav.rest.server;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.bitctrl.dav.rest.api.OnlineDatum;
import de.bitctrl.dav.rest.api.Onlinedaten;

public class OnlinedatenImpl implements Onlinedaten {

	private Logger logger = Logger.getLogger(getClass().getName());

	public void postOnlinedaten(List<OnlineDatum> entity) {
		logger.log(Level.INFO, "Empfange Onlinedaten: " + entity);
	}

}
