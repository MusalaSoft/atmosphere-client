package com.musala.atmosphere.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.musala.atmosphere.client.uiutils.UiXmlParser;
import com.musala.atmosphere.commons.Pair;

/**
 * Used to access and manipulate certain views on the testing device, for example tapping, double-taping or holding
 * finger on given widget.
 * 
 * @author georgi.gaydarov
 * 
 */
public class UiElement
{
	private enum ElementNodeType
	{
		XPATH_NODE, JSOUP_NODE;
	}

	private ElementNodeType underlyingNodeType;

	private Node representedNodeXPath;

	private org.jsoup.nodes.Node representedNodeJSoup;

	private UiElementAttributes elementAttributes;

	private Device onDevice;

	/**
	 * Constructor for element creation via a XPath query.
	 * 
	 * @param representingNode
	 * @param onDevice
	 */
	UiElement(Node representingNode, Device onDevice)
	{
		underlyingNodeType = ElementNodeType.XPATH_NODE;
		representedNodeXPath = representingNode;
		Map<String, String> nodeAttributesMap = UiXmlParser.getAttributeMapOfNode(representingNode);
		elementAttributes = new UiElementAttributes(nodeAttributesMap);
		this.onDevice = onDevice;
	}

	/**
	 * Constructor for element creation via a JSoup query.
	 * 
	 * @param representingNode
	 * @param onDevice
	 */
	UiElement(org.jsoup.nodes.Node representingNode, Device onDevice)
	{
		underlyingNodeType = ElementNodeType.JSOUP_NODE;
		representedNodeJSoup = representingNode;
		Map<String, String> nodeAttributesMap = UiXmlParser.getAttributeMapOfNode(representingNode);
		elementAttributes = new UiElementAttributes(nodeAttributesMap);
		this.onDevice = onDevice;
	}

	public UiElementAttributes getElementAttributes()
	{
		return elementAttributes;
	}

	/**
	 * Simulates tapping on the current UI Element
	 */
	public void tap()
	{
		Pair<Integer, Integer> firstBound = elementAttributes.getBounds().getKey();
		Pair<Integer, Integer> secondBound = elementAttributes.getBounds().getValue();
		int pressX = (firstBound.getKey() + secondBound.getKey()) / 2;
		int pressY = (firstBound.getValue() + secondBound.getValue()) / 2;

		onDevice.tapScreenLocation(pressX, pressY);
	}

	/**
	 * Used to get list of children of the given UI Element. Works only for ViewGroups.
	 * 
	 * @return List with all the UI elements that inherit from this UI element or empty List if such don't exist.
	 */
	public List<UiElement> getChildren()
	{
		List<UiElement> result;
		if (underlyingNodeType == ElementNodeType.XPATH_NODE)
		{
			result = getChildrenForXPath();
		}
		else
		{
			result = getChildrenForJSoup();
		}
		return result;
	}

	private List<UiElement> getChildrenForXPath()
	{
		NodeList nodeChildren = representedNodeXPath.getChildNodes();
		List<UiElement> result = new LinkedList<UiElement>();

		for (int i = 0; i < nodeChildren.getLength(); i++)
		{
			Node childNode = nodeChildren.item(i);
			if (childNode.getNodeType() != Node.ELEMENT_NODE)
			{
				continue;
			}
			UiElement childElement = new UiElement(childNode, onDevice);
			result.add(childElement);
		}

		return result;
	}

	private List<UiElement> getChildrenForJSoup()
	{
		List<org.jsoup.nodes.Node> nodeChildren = representedNodeJSoup.childNodes();
		List<UiElement> result = new LinkedList<UiElement>();

		for (org.jsoup.nodes.Node childNode : nodeChildren)
		{
			// This is a workaround to check if the child node is an element
			if (childNode.attr("bounds") == null)
			{
				continue;
			}
			UiElement childElement = new UiElement(childNode, onDevice);
			result.add(childElement);
		}

		return result;
	}

	/**
	 * Simulates holding finger on the screen.
	 */
	public void hold()
	{
		// TODO implement uiElement.hold()
	}

	/**
	 * Simulates double-tapping on the given UI element.
	 */
	public void doubleTap()
	{
		// TODO implement uiElement.doubleTap()
	}

	/**
	 * Simulates dragging the UI widget until his ( which corner exactly? ) upper-left corner stands at position
	 * (toX,toY) on the screen.
	 * 
	 * @param toX
	 * @param toY
	 */
	public void drag(int toX, int toY)
	{
		// TODO implement uiElement.drag()
	}

}
