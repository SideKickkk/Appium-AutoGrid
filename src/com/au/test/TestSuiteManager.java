package com.au.test;

import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import com.au.grid.Listners;
import com.au.grid.LogGen;
import io.appium.java_client.android.AndroidDriver;

/**
 * @author prajith
 * Class with all testng annotation functions to manage execution cycle.
 *
 */
public class TestSuiteManager {

	protected AndroidDriver<WebElement> driver;

	@AfterSuite()
	public void afterSuite() {
		LogGen.log.debug("afterSuite called.");
		Listners.testCompleted = true;
		LogGen.log.debug("afterSuite completed.");
	}

	@BeforeTest(alwaysRun = true)
	@Parameters({ "port", "device", "platformVersion", "systemIp", "systemPort" })
	public void beforeTest(String port, String device, String platformVersion, String systemIp, String systemPort) {
		LogGen.log.debug("Before test called ");
		try {
			LogGen.log.debug(port + " " + device + " " + platformVersion + " " + systemIp);
			LogGen.log.debug("Driver create called ");
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability("deviceName", device);
			capabilities.setCapability("udid", device);
			capabilities.setCapability("platformVersion", platformVersion);
			capabilities.setCapability("platformName", "Android");

			// You need to do.
			capabilities.setCapability("appPackage", "Your app name");
			capabilities.setCapability("appActivity", "Your apps launch activity");

			capabilities.setCapability("systemPort", systemPort);
			capabilities.setCapability("automationName", "UiAutomator2");
			LogGen.log.debug("http://" + systemIp + ":" + port + "/wd/hub");
			driver = new AndroidDriver<WebElement>(new URL("http://" + systemIp + ":" + port + "/wd/hub"),
					capabilities);
			driver.manage().timeouts().implicitlyWait(1, TimeUnit.MINUTES);
			LogGen.log.debug("Driver created successfully for device > " + device);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
