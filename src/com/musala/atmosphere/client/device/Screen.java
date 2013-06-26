package com.musala.atmosphere.client.device;

/**
 * This structure will hold active view of the testing device in XML format.
 * 
 * @author vladimir.vladimirov
 * 
 */

public class Screen
{
	private String screenXml;

	/**
	 * Exports the current active view on the testing device in XML file.
	 * 
	 * @param path
	 *        - path where the XML should be saved
	 */
	public void exportToXml(String path)
	{
		// TODO implement screen.exportToXml()
	}

	/**
	 * Searches for given UI element on the screen using CSS.
	 * 
	 * @param query
	 *        - query in CSS syntax format
	 * @return object of type {@link UiElement} or <b>null</b> if requested element is not found
	 */
	public UiElement getElementCSS(String query)
	{
		// TODO implement screen.getElementCSS()
		return null;
	}

	/**
	 * Searches for given UI element on the screen using XPath.
	 * 
	 * @param query
	 *        - query in XPath syntax format
	 * @return object of type UiElement or null if requested element is not found
	 */
	public UiElement getElementXPath(String query)
	{
		// TODO implement screen.getElementXPath()
		return null;
	}

	/**
	 * Searches for given UI element on the screen using ATMOSPHERE's conventions.
	 * 
	 * @param selector
	 *        - object of type {@link UIElementSelector}
	 * @return object of type UiElement or null if requested element is not found
	 */
	public UiElement getElement(UiElementSelector selector)
	{
		// TODO implement screen.getElement()
		return null;

	}

}
