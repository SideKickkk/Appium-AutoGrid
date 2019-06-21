package com.au.grid;

import java.util.ArrayList;
import java.util.List;

public class Listners {
	public static boolean testCompleted = false;
	public static List<String> nodeStatus = new ArrayList<>();
	Thread hubService, nodeService;

	public synchronized boolean isTestCompleted() {
		return testCompleted;
	}

	public List<String> isNodeStopped() {
		return nodeStatus;
	}

	public synchronized void nodeListner(Process nodeProcess, String threadName, String port) {
		Thread nodeTracker = new Thread(() -> {
			try {
				boolean checkFlag = isTestCompleted();

				while (!checkFlag) {
					checkFlag = isTestCompleted();
				}

				nodeProcess.destroy();

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			LogGen.log.debug("threadname > " + threadName + " Port > " + port);
			
			/*
			 * Worst way to do it but dont have option stdout is always printing something
			 * in buffer and JVM is not able to close the process.
			 * https://bugs.java.com/bugdatabase/view_bug.do?bug_id=4073195
			 */
			WinProcess win = new WinProcess();
			long pId = win.getProcessId(port);
			if (pId == 0) {
				LogGen.log.debug("Process already destroyed which was using port > " + port);
			} else {
				LogGen.log.debug("pid received to listner for port > " + port + " is pid > " + pId);
				boolean status = win.forceDestroyProcess(pId);
				LogGen.log.info("Node process destroyed ? " + status);
			}
		});
		
		String threadname = Thread.currentThread().getName();
		nodeTracker.setName(threadname + "-nodeTracker");
		nodeTracker.start();
	}
}
