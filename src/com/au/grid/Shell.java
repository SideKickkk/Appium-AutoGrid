package com.au.grid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.simple.parser.ParseException;

import com.au.grid.Parameters.Commands;

public class Shell {
	Process shell;

	/*
	 * Executes command in command prompt.
	 */
	public String adbCommandExecutor(String command) {
		String result = null;
		try {
			LogGen.log.debug("Executing command " + command);
			shell = Runtime.getRuntime().exec(command);
			result = logBufferReader(shell);
			shell.waitFor();
		} catch (Exception e) {
			LogGen.log.error("Check adb command.");
			e.printStackTrace();
		}
		shell.destroyForcibly();
		return result;
	}

	/*
	 * Reads process buffer and return the logs.
	 */
	public String logBufferReader(Process p) throws IOException, InterruptedException {
		String line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		StringBuffer output = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			output.append(line);
			// output.append(System.getProperty("line.separator"));
		}
		LogGen.log.debug("logBufferReader returned " + output.toString());
		reader.close();
		return output.toString();
	}

	/*
	 * Retrieves device names from buffer.
	 */
	public ArrayList<String> deviceNameExtractor(String command) {
		ArrayList<String> deviceList = new ArrayList<String>();
		try {
			String line;
			LogGen.log.info("Looking for devices.");
			shell = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(shell.getInputStream()));
			while ((line = reader.readLine()) != null) {
				if (line.contains("device") && !line.contains("attached")) {
					String[] deviceLine = line.split("\t");
					deviceList.add(deviceLine[0]);
				}
			}

		} catch (IOException e) {
			LogGen.log.error("Seems like ADB_HOME path is not set in your machine");
			e.printStackTrace();
		}

		return deviceList;
	}

	/*
	 * Retrieves device properties from adb properties.
	 */
	public ArrayList<String> devicePropertyExtractor(ArrayList<String> deviceList) throws ParseException {
		int deviceCount = deviceList.size();
		LogGen.log.info("Connected devices : " + deviceCount);

		ArrayList<String> properties = new ArrayList<String>();

		for (int i = 0; i < deviceCount; i++) {
			ArrayList<String> commandBuilder = new ArrayList<String>();
			commandBuilder.add(Commands.SPECIFY_DEVICE.command() + " " + deviceList.get(i) + " "
					+ Commands.DEVICE_PROPERTY.command() + " " + Commands.GET_ANDROID_VERSION);
			properties.add(deviceList.get(i));
			for (int j = 0; j < commandBuilder.size(); j++) {
				properties.add(adbCommandExecutor(commandBuilder.get(j)));
			}

		}
		int propSize = properties.size();
		LogGen.log.info("Properties : " + propSize);
		StringBuilder propString = new StringBuilder();
		for (int i = 0; i < propSize; i++) {
			propString.append(properties.get(i) + " ");
		}
		LogGen.log.debug(propString.toString());

		return properties;
	}

}
