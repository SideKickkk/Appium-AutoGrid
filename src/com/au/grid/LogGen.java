package com.au.grid;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LogGen {
	public static Logger log = Logger.getLogger(LogGen.class.getPackage().getName());

	public void setProperty() {
		PropertyConfigurator.configure("log4j.properties");
	}
}
