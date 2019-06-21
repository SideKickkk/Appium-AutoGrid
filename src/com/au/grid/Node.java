package com.au.grid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Node implements Runnable {

	public int deviceCount;
	public static Node[] nodes;
	public int i = 0, j = 1;
	private boolean isRunning = false;
	private Thread thread, inputStream;
	public ProcessBuilder builder;
	public Process nodeInstance;
	private static boolean exitFlag = false;
	private static boolean inputStreamFinishedFlag = false, errorStreamFinishedFlag = false;
	

	public Node(int i, int j) {
		this.i = i;
		this.j = j;
	}

	public Node() {
		// TODO Auto-generated constructor stub
	}

	public boolean isRunning() {
		return isRunning;
	}

	public boolean getInputStreamStatus() {
		return inputStreamFinishedFlag;
	}

	public boolean getErrorStreamStatus() {
		return errorStreamFinishedFlag;
	}
	

	public void run() {
		
		LogGen.log.info("Node started > " + thread.getName());
		deviceCount = DeviceInfo.deviceNames.size();
		LogGen.log.debug("Input for next thread is i: \"" + i + "\" j:\"" + j + "\"");
		try {
			Path nodeConfigPath = Paths.get(Parameters.ROOT_FOLDER + "\\node-config\\Config" + j + ".json");
			if (Files.exists(nodeConfigPath)) {
				LogGen.log.debug("File path exist.");
				String port =DeviceInfo.ports.get(i);
				String nodeCommand = "appium -p " + port + " --nodeconfig "
						+ nodeConfigPath.toString();
				LogGen.log.debug(nodeCommand);
				builder = new ProcessBuilder("cmd.exe", "/c", nodeCommand);
				nodeInstance = builder.start();

				//readErrorStream(nodeInstance);
				readInputStream(nodeInstance,port);		

			} else {
				LogGen.log.error("Config file doesnt exist. " + nodeConfigPath.toString());
			}

		} catch (IOException e) {
			LogGen.log.fatal("node configuration failed" + thread.getName());
			e.printStackTrace();
		}

		LogGen.log.debug("Node run execution completed");

	}

	public Node[] start() {
		LogGen.log.info("Trying to start node");
		deviceCount = DeviceInfo.deviceNames.size();
		LogGen.log.debug("Node adding started for \"" + deviceCount + "\" devices");
		nodes = new Node[deviceCount];
		for (int n = 0, c = 1; n < nodes.length; n++, c++) {
			nodes[n] = new Node(i++, j++);
			Thread t = new Thread(nodes[n]);
			nodes[n].setThread(t);
			nodes[n].thread.setName("node-"+n);
			nodes[n].thread.start();
			LogGen.log.debug(
					"Thread number \"" + c + "\" with name " + nodes[n].thread.getName() + " started executing.");
		}

		int timeout = 20000;

		boolean completed = false;
		long startMills = System.currentTimeMillis();
		long currentMills = System.currentTimeMillis();
		while (!completed && !(currentMills - startMills > timeout)) {
			completed = true;
			for (Node node : nodes) {
				completed &= node.isRunning();
			}
			if (completed) {
				Grid.isNodesWorking = true;
			}
			currentMills = System.currentTimeMillis();
		}

		LogGen.log.info("All node creation completed.");

		return nodes;
	}

	private void setThread(Thread t) {
		this.thread = t;
	}


	/*public void readErrorStream(Process p) {
		errorStream = new Thread(() -> {
			try {
				final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String line = null;
				Listners listner = new Listners();
				while ((line = reader.readLine()) != null && !exitFlag) {
					exitFlag = listner.isTestCompleted();
					System.err.println("Node error steam: " + line);
				}
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			LogGen.log.info("Node errorstream stopped. ExitFlag: " + exitFlag);
			//p.destroy();
			errorStreamFinishedFlag = true;
		});
		errorStream.setName("nodeErrorStream");
		errorStream.start();

	}*/

	public void readInputStream(Process nodeInstance, String port) {
		LogGen.log.debug("Node read Input stream called");
		
		inputStream = new Thread(() -> {
			LogGen.log.debug("Node thread started.");
			try {
				final BufferedReader reader = new BufferedReader(new InputStreamReader(nodeInstance.getInputStream()));
				String line = null;
				Listners listner = new Listners();
				while ((line = reader.readLine()) != null && !exitFlag) {
					exitFlag = listner.isTestCompleted();
					LogGen.log.debug("Node input steam:  " + line);
					if (line.contains("Appium REST http interface listener started")) {
						isRunning = true;
					}
				}
				reader.close();
				nodeInstance.getErrorStream().close();
				nodeInstance.getInputStream().close();
				nodeInstance.getOutputStream().close();
			} catch (IOException e) {
				LogGen.log.error("Stream closed already.");
			}
			inputStreamFinishedFlag = true;
			LogGen.log.info("Node inputstream stopped. ExitFlag: " + exitFlag);
			
		});
		
		String threadname=Thread.currentThread().getName();
		inputStream.setName(threadname+"nodeInputStream");
		inputStream.start();
		
		Listners listner = new Listners();
		
		listner.nodeListner(nodeInstance,Thread.currentThread().getName(),port);

	}
}
