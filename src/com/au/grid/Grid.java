package com.au.grid;

import java.util.ArrayList;
import org.json.simple.parser.ParseException;
import com.au.grid.Parameters.Commands;
import com.au.grid.Parameters.Constant;

/**
 * Class used for setting up selenim grid, node.
 * 
 * @author Prajith
 *
 */
public class Grid {

	public static boolean isHubWorking = false, isNodesWorking = false, isNodesClosed = false;
	public static int deviceCount;

	public boolean isHubWorking() {
		return isHubWorking;
	}

	public boolean isNodeWorking() {
		return isNodesWorking;
	}

	public static boolean isNodeClosed() {
		return isNodesClosed;
	}

	/**
	 * Getting device info, generates config files and trigers grid and nodes.
	 */
	public boolean autoConfigureGrid() {

		// Set log properties.
		LogGen logger = new LogGen();
		logger.setProperty();

		// Get devices names.
		Shell exe = new Shell();
		ArrayList<String> deviceList = exe.deviceNameExtractor(Commands.GET_DEVICES.command());

		deviceCount = deviceList.size();

		// If device exist.
		if (deviceCount > 0) {
			try {
				// Get device property.
				ArrayList<String> properties = exe.devicePropertyExtractor(deviceList);
				// Create config files using properties.
				JsonConfigGenerator gen = new JsonConfigGenerator();
				gen.createConfigs(properties);
				// Start hub and add all nodes.
				start();
			} catch (ParseException e) {
				LogGen.log.fatal("Error while creating config.");
				e.printStackTrace();
			}
			// Testng XMl generate call
			TestngXmlGenerator CreateTestNgxml1_obj = new TestngXmlGenerator();
			CreateTestNgxml1_obj.createTestNgxmlfile(Constant.PARALLEL.value());

			int timeout = 120000;
			long startMills = System.currentTimeMillis();
			long currentMills = System.currentTimeMillis();
			LogGen.log.info("Checking hub and node started please wait.\n");
			Boolean hubStatus = false, nodeStatus = false;
			while (!hubStatus || !nodeStatus && !(currentMills - startMills > timeout)) {
				hubStatus = isHubWorking();
				nodeStatus = isNodeWorking();
				currentMills = System.currentTimeMillis();
				System.out.print("");
			}
			long timeTaken = currentMills - startMills;
			LogGen.log.debug("HubStatus -> " + hubStatus + " Node status -> " + nodeStatus);
			LogGen.log.debug("Exited from wait took " + timeTaken + "ms" + "(" + timeTaken / 1000
					+ " second) for to start. isHubWorking flag value: " + isHubWorking);
		} else {
			LogGen.log.error("None Connected.");
		}

		if (!isNodesWorking)
			Listners.testCompleted = true;

		return isNodesWorking;

	}

	/**
	 * Start both hub and node.
	 */
	public void start() {
		LogGen.log.info("Start Hub & Node called.");
		Hub hub = new Hub();
		Node node = new Node();
		hub.start();
		node.start();
	}

}
