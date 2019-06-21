package com.au.test;

import org.testng.annotations.Test;

public class SampleTestClass extends TestSuiteManager {

	@Test
	public void sampleTest() throws InterruptedException {
		try {

			// Put your test code here.

		} catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			driver.quit();
		}
	}
}
