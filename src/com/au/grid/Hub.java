package com.au.grid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Prajith Holds selenium grid process and monitors untill execution is
 *         completed.
 *
 */
public class Hub {

	public static Process hubInstance;
	Thread hubThread, errorStream, inputStream;
	private boolean isRunning = false, streamExitFLag = false, nodeStopped = false;
	private boolean exitFlag = false;

	public boolean isRunning() {
		return isRunning;
	}

	public boolean isStreamComplete() {
		return streamExitFLag;
	}

	public boolean readyToCloseHub() {
		return nodeStopped;
	}

	/**
	 * Hosts selenium grid server as hub (http://systemip:4444/grid/console)
	 * 
	 * @param none
	 */
	public void start() {
		LogGen.log.info("Hub start function called.");

		hubThread = new Thread(() -> {
			LogGen.log.info("Hub Thread started.");
			try {
				Path serverJarPath = Paths.get(Parameters.ROOT_FOLDER + "\\hub\\server.jar");
				if (Files.exists(serverJarPath)) {
					hubInstance = Runtime.getRuntime().exec("java -jar " + serverJarPath.toString() + " -role hub");
					LogGen.log.info("Hub process created.");
					readErrorStream(hubInstance);
				} else {
					String errorMsg = "File \"server.jar\" not found in following path " + serverJarPath.toString();
					LogGen.log.fatal(errorMsg);
				}

				hubInstance.waitFor();
				LogGen.log.debug("Process waitfor completed.");

			} catch (IOException e) {
				hubInstance.destroyForcibly();
				e.printStackTrace();
			} catch (InterruptedException e) {
				hubInstance.destroyForcibly();
				LogGen.log.fatal("Stopped hub forcefully.");
			}
			LogGen.log.debug("Hub thread completed.");

		});
		hubThread.setName("hubThread");
		hubThread.start();

	}

	/**
	 * Interrupts thread holding selenium grid server.
	 * 
	 * @param cause
	 */
	/*
	 * public void stop(String cause) { LogGen.log.debug("Hub stop started. Cause: "
	 * + cause); hubInstance.destroy(); LogGen.log.debug("Hub stop completed."); }
	 */

	public void readErrorStream(Process p) {
		LogGen.log.info("Hub readErrorStream called.");

		errorStream = new Thread(() -> {
			try {
				LogGen.log.info("Hub error stream started.");
				final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String line = null;
				while ((line = reader.readLine()) != null && !exitFlag) {
					LogGen.log.debug("Hub stream: " + line);
					exitFlag = readyToCloseHub();
					if (line.contains("Clients should connect to")) {
						Grid.isHubWorking = true;
						LogGen.log.debug("Exit flag -> " + exitFlag
								+ " Verified hub streams logs setting flags to true isHubWorking: " + Grid.isHubWorking
								+ " isRunning: " + isRunning);
					} else if (line.contains("ERROR")) {
						System.err.println("Encountered an error: " + line);
						Grid.isHubWorking = false;
					} else if (line.contains("as down:")) {
						Listners.nodeStatus.add("true");
						LogGen.log.debug(Listners.nodeStatus.size());
						LogGen.log.debug("Node down > " + Thread.currentThread().getName());
						if (Listners.nodeStatus.size() == Grid.deviceCount) {
							nodeStopped = true;
							hubInstance.destroy();
						}

					}
				}
				reader.close();
				streamExitFLag = true;
			} catch (final Exception e) {
				e.printStackTrace();
			}
			LogGen.log.info("Hub readErrorStream completed.");
		});
		errorStream.setName("hubErrorStream");
		errorStream.start();

	}

}
