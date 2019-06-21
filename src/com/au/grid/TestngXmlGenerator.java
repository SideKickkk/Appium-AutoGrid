package com.au.grid;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Generates testng xml file the data provided in runtime.
 */
public class TestngXmlGenerator {
	List<String> TCList;

	public void createTestNgxmlfile(String mode) {
		try {

		
			TCList = new ArrayList<String>();
			TCList.add("com.au.test.SampleTestClass");

			int tcListPointer = 0;

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
			Document doc = dbBuilder.newDocument();

			// <suite
			Element rootElement = doc.createElement("suite");
			doc.appendChild(rootElement);
			
			Attr rootNameAttribute = doc.createAttribute("name");
			// You need to do
			rootNameAttribute.setValue("Your app name");
			rootElement.setAttributeNode(rootNameAttribute);

			if (mode.toLowerCase().contains("parallel")) {
				// parallel="tests"
				Attr rootParallelAttribute = doc.createAttribute("parallel");
				rootParallelAttribute.setValue("tests");
				rootElement.setAttributeNode(rootParallelAttribute);
			}
			// thread-count="5"
			Attr rootThreadCountAttribute = doc.createAttribute("thread-count");
			rootThreadCountAttribute.setValue("5");
			rootElement.setAttributeNode(rootThreadCountAttribute);

			// Attr rootPreserveOrderAttribute = doc.createAttribute("preserve-order");
			// rootPreserveOrderAttribute.setValue("true");
			// rootElement.setAttributeNode(rootPreserveOrderAttribute);
			int totalConnectedDevice = DeviceInfo.deviceNames.size();
			int devicePointer = 1; // initialize with first device
			//LogGen.log.debug("TotalConnectedDevice=" + totalConnectedDevice);
			int tcForEachDevice = 0;
			if (mode.toLowerCase().contains("serial")) {
				//LogGen.log.debug("TCList.size=" + TCList.size());
				tcForEachDevice = (TCList.size() / totalConnectedDevice);
				//LogGen.log.debug("val-->" + tcForEachDevice);
			}
			for (int j = 0; j < totalConnectedDevice; j++) {
				/////
				// <test
				Element testElement = doc.createElement("test");
				rootElement.appendChild(testElement);
				// name="Device-1..."
				Attr testNameAttribute = doc.createAttribute("name");
				testNameAttribute.setValue("Device-" + (j + 1));
				testElement.setAttributeNode(testNameAttribute);
				
				// <parameter
				createTestParamater(doc, testElement, "port", DeviceInfo.ports.get(j));
				createTestParamater(doc, testElement, "device", DeviceInfo.deviceNames.get(j));
				createTestParamater(doc, testElement, "platformVersion", DeviceInfo.version.get(j));
				createTestParamater(doc, testElement, "systemIp", DeviceInfo.ip);
				createTestParamater(doc, testElement, "systemPort", DeviceInfo.sysports.get(j));

				Element classesElement = doc.createElement("classes");
				testElement.appendChild(classesElement);

				if (mode.toLowerCase().contains("parallel")) {
					for (int i = 0; i < TCList.size(); i++) {
						createClass(doc, classesElement, TCList.get(i));
					}
				} else // for serial execution
				{
					if (totalConnectedDevice > 1 && TCList.size() == 1) // if more device connected & number of TC is
																		// only one
					{
						//LogGen.log.debug("more device connected & number of TC is only one");
						createClass(doc, classesElement, TCList.get(0));
					} else if (totalConnectedDevice > TCList.size()) // if connected device is more than number of TC
					{
						//LogGen.log.debug("connected device is more than number of TC");
					} else // if connected device is less than number of TC
					{
						if (devicePointer != totalConnectedDevice) // if not last device
						{
							//LogGen.log.debug("Not last device==" + devicePointer);
							for (int temp = 0; temp < tcForEachDevice; temp++) {
								createClass(doc, classesElement, TCList.get(tcListPointer));
								tcListPointer++;
							}
							devicePointer++;
						} else {
							//LogGen.log.debug("for last device==" + devicePointer);
							for (int temp = tcListPointer; temp < TCList.size(); temp++) {
								createClass(doc, classesElement, TCList.get(tcListPointer));
								tcListPointer++;
							}
						}
					}
				}
			}
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			// for pretty print
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://testng.org/testng-1.0.dtd");
			DOMSource source = new DOMSource(doc);
			// Output to testing file
			StreamResult result = new StreamResult(new File("./testng.xml"));
			transformer.transform(source, result);
			// Output to console for testing
			StreamResult consoleResult = new StreamResult(System.out);
			transformer.transform(source, consoleResult);
			// // Output to console for testing
			// @SuppressWarnings("unused")
			// StreamResult logResult = new StreamResult(System.out);
			// transformer.transform(source, consoleResult);
			//LogGen.log.debug("Generated testng_JB.xml file");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method include parameters for test block
	 * 
	 * @param             doc: Document
	 * @param             testElement: Element
	 * @param param_name
	 * @param param_value
	 */
	private void createTestParamater(Document doc, Element testElement, String param_name, String param_value) {
		Element parameterElement;
		Attr parameterNameAttribute, parameterValueAttribute;
		parameterElement = doc.createElement("parameter");
		testElement.appendChild(parameterElement);
		// name="port"
		parameterNameAttribute = doc.createAttribute("name");
		parameterNameAttribute.setValue(param_name);
		parameterElement.setAttributeNode(parameterNameAttribute);
		// value="4733"
		parameterValueAttribute = doc.createAttribute("value");
		parameterValueAttribute.setValue(param_value);
		parameterElement.setAttributeNode(parameterValueAttribute);
	}

	/**
	 * This method include class for classes block
	 * 
	 * @param doc
	 * @param classesElement
	 * @param param_value
	 */
	private void createClass(Document doc, Element classesElement, String param_value) {
		Element classElement = doc.createElement("class");
		Attr classNameAttribute = doc.createAttribute("name");
		classNameAttribute.setValue(param_value);
		classElement.setAttributeNode(classNameAttribute);
		classesElement.appendChild(classElement);
	}
}
