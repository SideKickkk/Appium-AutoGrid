package com.au.grid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class WinProcess {

	/**
	 * get all process ids which are running and having same process name / exename.
	 * 
	 * @param (executable file Name)
	 */
	public synchronized ArrayList<Long> getAllPids(String exeName) {
		ArrayList<Long> pIds = new ArrayList<Long>();
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "tasklist | findstr \"" + exeName + "\"");
		try {
			Process process = builder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			process.waitFor();
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains("node.exe")) {
					line = line.replaceAll("[\\s]{2,}", " ");
					try {
						String[] output = line.split("\\s");
						Long pid = Long.parseLong(output[1]);
						pIds.add(pid);
					} catch (NumberFormatException e) {
						LogGen.log.error("Seems like you dont have windows shell commands."
								+ " Check whether tasklist command is present in cmd.");
					}
				}
			}

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return pIds;
	}

	public synchronized long getProcessId(String port) {
		long pId = 0;
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "netstat -o | findstr \""+port+"\"");
		try {
			Process process = builder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			process.waitFor();
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains(":"+port) && line.contains("ESTABLISHED")) {
					LogGen.log.debug(line);
					line = line.replaceAll("[\\s]{2,}", " ");
					String[] output = line.split("\\s");
					pId=Long.parseLong(output[5]);
					break;
				}
				else {
					LogGen.log.debug(line);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return pId;
	}

	/**
	 * To forcefully destroy the process from tasklist of windows.
	 * 
	 * @param processId
	 * @return boolean result whether process was destroyed or not.
	 */
	public synchronized boolean forceDestroyProcess(long processId) {
		boolean status = false;
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "Taskkill /PID " + processId + " /F");
		try {
			Process process = builder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			process.waitFor();
			String line;
			while ((line = reader.readLine()) != null) {
				LogGen.log.debug("line > "+line);
				if (line.contains("SUCCESS")) {
					status = true;
				}
			}
		} catch (IOException | InterruptedException e) {
			LogGen.log.error("Failed to stop the process.");
		}
		return status;
	}

	/**
	 * @param exeName
	 * @return To destroy all process with the same exe name.
	 */
	public synchronized boolean forceDestroyAllProcess(String exeName) {
		boolean status = false;

		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "Taskkill /IM " + exeName + " /F");
		try {
			Process process = builder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			process.waitFor();
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains("SUCCESS")) {
					status = true;
				}
			}
		} catch (IOException | InterruptedException e) {
			LogGen.log.error("Failed to stop the process.");
		}
		return status;
	}

}
