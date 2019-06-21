package com.au.grid;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class to store device data to be accessed from different places.
 * @author Prajith
 */
public class DeviceInfo {
	public static ArrayList<String> deviceNames;
	public static ArrayList<String> ports;
	public static ArrayList<String> version;
	// PENDING To-do need to assign dynamically by checking port availability
	public static ArrayList<String> sysports = new ArrayList<String>(Arrays.asList("8201","8202","8203","8204","8205"));
	public static String ip;

	/**
	 * Sets device info to static variables.
	 * @param deviceNames
	 * @param ports
	 * @param version
	 * @param ip
	 */
	public void setValues(ArrayList<String> deviceNames, ArrayList<String> ports, ArrayList<String> version,
			String ip) {
		DeviceInfo.deviceNames = deviceNames;
		DeviceInfo.ports = ports;
		DeviceInfo.version = version;
		DeviceInfo.ip = ip;

	}

}
