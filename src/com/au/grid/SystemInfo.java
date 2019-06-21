package com.au.grid;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.au.grid.Parameters.Constant;

public class SystemInfo {

	public String getIpaddress() {
		String ip = null;
		try {
			InetAddress localhost;
			localhost = InetAddress.getLocalHost();
			ip = localhost.getHostAddress().trim();
			if (ip.equals(Constant.LOCAL_IP.value())) {
				LogGen.log.error("No internet connection.");
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return ip;
	}

	public ArrayList<String> assignPort(int devicecount) {
		ArrayList<String> ports = new ArrayList<String>();
		int startPort = 4722;
		try {
			for (int i = 0; i < devicecount; i++) {
				ports.add(Integer.toString(startPort));
				startPort++;
			}
			StringBuilder portNumbers = new StringBuilder();
			for (String port : ports) {
				portNumbers.append(port + " ");
			}
			LogGen.log.debug("ports alloted : " + portNumbers);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ports;
	}

}