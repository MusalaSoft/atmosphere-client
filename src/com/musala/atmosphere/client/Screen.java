package com.musala.atmosphere.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.musala.atmosphere.client.exceptions.UiElementFetchingException;
import com.musala.atmosphere.client.uiutils.UiElementSelector;
import com.musala.atmosphere.client.uiutils.UiXmlParser;

/**
 * Class that holds a device screen information.
 * 
 * @author georgi.gaydarov
 * @author vladimir.vladimirov
 * 
 */

public class Screen
{
	private static final Logger LOGGER = Logger.getLogger(Screen.class.getCanonicalName());

	private String screenXml;

	private Device onDevice;

	private Document xPathDomDocument;

	private org.jsoup.nodes.Document jSoupDocument;

	/**
	 * 
	 * @param onDevice
	 * @param uiHierarchyXml
	 */
	Screen(Device onDevice, String uiHierarchyXml)
	{
		this.onDevice = onDevice;
		screenXml = uiHierarchyXml;

		// XPath DOM Document building
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		try
		{
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			xPathDomDocument = documentBuilder.parse(new InputSource(new StringReader(screenXml)));
		}
		catch (ParserConfigurationException | SAXException | IOException e)
		{
			LOGGER.warn("XPath XML to DOM Document parsing failed.", e);
		}

		// JSoup Document building
		jSoupDocument = Jsoup.parse(screenXml);
	}

	/**
	 * Saves the underlying device UI XML into a file.
	 * 
	 * @param path
	 *        - file to which the UI XML should be saved.
	 * @throws FileNotFoundException
	 */
	public void exportToXml(String path) throws FileNotFoundException
	{
		PrintStream export = new PrintStream(path);
		export.print(screenXml);
		export.close();
	}

	/**
	 * Searches for given UI element in the current screen XML structure using CSS.
	 * 
	 * @param query
	 *        - CSS selector query.
	 * @return the requested {@link UiElement UiElement}.
	 * @throws UiElementFetchingException
	 */
	public UiElement getElementCSS(String query) throws UiElementFetchingException
	{
		org.jsoup.nodes.Node node = UiXmlParser.getJSoupNode(jSoupDocument, query);
		UiElement returnElement = new UiElement(node, onDevice);
		return returnElement;
	}

	/**
	 * Searches for given UI element in the current screen XML structure using XPath.
	 * 
	 * @param query
	 *        XPath query.
	 * @return the requested {@link UiElement UiElement}.
	 * @throws XPathExpressionException
	 * @throws UiElementFetchingException
	 */
	public UiElement getElementXPath(String query) throws XPathExpressionException, UiElementFetchingException
	{
		Node node = UiXmlParser.getXPathNode(xPathDomDocument, query);
		UiElement returnElement = new UiElement(node, onDevice);
		return returnElement;
	}

	/**
	 * Searches for given UI element in the current screen XML structure using a {@link UIElementSelector
	 * UIElementSelector} instance.
	 * 
	 * @param selector
	 *        - object of type {@link UIElementSelector}
	 * @return the requested {@link UiElement UiElement}.
	 * @throws UiElementFetchingException
	 */
	public UiElement getElement(UiElementSelector selector) throws UiElementFetchingException
	{
		String cssQuery = selector.buildCssQuery();
		UiElement result = getElementCSS(cssQuery);
		return result;
	}
}
