package com.au.test;

import java.util.ArrayList;
import org.testng.TestNG;
import com.au.grid.Grid;

/**
 * @author Prajith This class demonstrates how to run a test for multiple
 *         devices in paralled using the Appium-AutoGrid
 *
 */
public class RunTest {

	public static void main(String[] args) {
		Grid con = new Grid();
		if (con.autoConfigureGrid()) {
			System.out.println("Configuration completed ready to test.");
			RunTest test = new RunTest();
			test.start();
		} else {
			System.out.println("Configuration failed.");
		}
	}

	/**
	 * Once grid gets configured testng file will be having all the information to
	 * run test in different devices so just need to trigger the file.
	 */
	public void start() {
		TestNG service = new TestNG();
		ArrayList<String> testList = new ArrayList<String>();
		testList.add(".\\testng.xml");
		service.setTestSuites(testList);
		service.run();
	}

}
