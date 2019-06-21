package com.au.grid;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Class used for creating --node config files based on device data which is the
 * input for selenium grid node.
 * @author Prajith
 */

public class JsonConfigGenerator {

	/**
	 * Read config template from readConfigTemplate() and update existing info with
	 * deviceData received and save files in project folder/node-config with file
	 * name config[increment variable].json.
	 */
	public void createConfigs(ArrayList<String> deviceData) {
		try {

			String ip;
			// Returns node config template.
			String templateData = readConfigTemplate();

			// Arraylist to store device data.
			ArrayList<String> deviceNames = new ArrayList<String>();
			ArrayList<String> ports = new ArrayList<String>();
			ArrayList<String> versions = new ArrayList<String>();

			// Json object to access / update existing config template.
			JSONObject configData = new JSONObject(templateData);
			JSONArray capabilities = configData.getJSONArray("capabilities");
			JSONObject capabilityData = (JSONObject) capabilities.get(0);
			JSONObject configuration = configData.getJSONObject("configuration");

			// Assign deviceData(devicenames, version) to below arraylists.
			int deviceCount = deviceData.size() / 2;
			for (int i = 0; i < deviceData.size(); i++) {
				if (i % 2 == 0) {
					deviceNames.add(deviceData.get(i));
				} else {
					versions.add(deviceData.get(i));
				}
			}

			// Get few port numbers based on device count.
			SystemInfo system = new SystemInfo();
			ports = system.assignPort(deviceCount);

			// Get system IP address.
			ip = system.getIpaddress();

			// Storing device data to class for further usage.
			DeviceInfo storeinfo = new DeviceInfo();
			storeinfo.setValues(deviceNames, ports, versions, ip);

			// Updating existing template and store as a file in node-config folder.
			for (int i = 0, j = 1; i < deviceCount; i++, j++) {
				capabilityData.put("deviceName", deviceNames.get(i));
				capabilityData.put("version", versions.get(i));
				configuration.put("port", ports.get(i));
				configuration.put("url", "http://" + ip + ":" + ports.get(i) + "/wd/hub");
				configuration.put("host", ip);
				configuration.put("hubHost", ip);
				// Write JSON file
				try (FileWriter file = new FileWriter(Parameters.ROOT_FOLDER + "\\node-config\\Config" + j + ".json")) {
					file.write(configData.toString());
					file.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			LogGen.log.info("Node Config's generated for devices.");

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Reads template json file and returns file content as string to, update json
	 * based on device data.
	 * @param none
	 * @return File content --node config template (String)
	 */
	private String readConfigTemplate() {
		String fileContent = null;
		String tempLoc = Parameters.ROOT_FOLDER + "\\node-config\\ConfigTemplate.json";
		Path confTempPath = Paths.get(tempLoc);
		if (Files.exists(confTempPath)) {
			// Read JSON template for config.
			try (FileReader reader = new FileReader(tempLoc)) {
				// Read JSON file
				JSONParser jsonParser = new JSONParser();
				Object obj = jsonParser.parse(reader);
				fileContent = obj.toString();
				LogGen.log.debug(fileContent);
				LogGen.log.info("Config template read.");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			LogGen.log.error("Config template doesnt exist " + tempLoc + " download file from "
					+ "git https://tinyurl.com/y2kykkp9 and place it if not present in workspace");
		}

		return fileContent;
	}

}
