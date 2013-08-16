package com.musala.atmosphere.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.musala.atmosphere.client.exceptions.ActionFailedException;
import com.musala.atmosphere.client.exceptions.InvalidElementActionException;
import com.musala.atmosphere.client.exceptions.StaleElementReferenceException;
import com.musala.atmosphere.client.exceptions.UiElementFetchingException;
import com.musala.atmosphere.client.geometry.Bounds;
import com.musala.atmosphere.client.geometry.Point;
import com.musala.atmosphere.client.uiutils.UiXmlParser;

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

	/**
	 * Refreshes and then returns the current UI element's attributes data container.
	 * 
	 * @return a {@link UiElementAttributes UiElementAttributes} instance.
	 */
	public UiElementAttributes getElementAttributes()
	{
		UiElementAttributes result = getElementAttributes(true);
		return result;
	}

	/**
	 * Returns the current UI element's attributes data container.
	 * 
	 * @param refresh
	 *        - boolean indicating if the attributes should be refreshed prior to returning.
	 * @return a {@link UiElementAttributes UiElementAttributes} instance.
	 */
	public UiElementAttributes getElementAttributes(boolean refresh)
	{
		if (refresh)
		{
			revalidateThrowing();
		}
		return elementAttributes;
	}

	/**
	 * Simulates tapping on a relative point in the current UI element.
	 * 
	 * @param point
	 *        - the relative point that will be added to the upper left corner's coordinates.
	 * @param revalidateElement
	 *        - boolean indicating if the element should be revalidated prior to tapping.
	 */
	public void tap(Point point, boolean revalidateElement)
	{
		if (revalidateElement)
		{
			revalidateThrowing();
		}

		Bounds elementBounds = elementAttributes.getBounds();
		Point tapPoint = elementBounds.getUpperLeftCorner();
		tapPoint.addVector(point);

		if (elementBounds.contains(tapPoint))
		{
			onDevice.tapScreenLocation(tapPoint);
		}
		else
		{
			throw new IllegalArgumentException("Point " + point + " not in element bounds.");
		}
	}

	/**
	 * Checks if the current UI element is still valid and if so, simulates tapping on a relative point in it.
	 * 
	 * @param point
	 *        - the relative point that will be added to the upper left corner's coordinates.
	 */
	public void tap(Point point)
	{
		tap(point, true);
	}

	/**
	 * Simulates tapping on the current UI Element.
	 * 
	 * @param revalidateElement
	 *        - boolean indicating if the element should be revalidated prior to tapping.
	 */
	public void tap(boolean revalidateElement)
	{
		Bounds elementBounds = elementAttributes.getBounds();
		Point centerPoint = elementBounds.getCenter();
		Point tapPoint = elementBounds.getRelativePoint(centerPoint);

		tap(tapPoint, revalidateElement);
	}

	/**
	 * Checks if the current UI element is still valid and if so, simulates tapping on it.
	 * 
	 * @param revalidateElement
	 *        - boolean indicating if the element should be revalidated prior to tapping.
	 */
	public void tap()
	{
		tap(true);
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

	/**
	 * Inputs text into the UI Element, <b> if it supports text input </b> with interval in milliseconds between the
	 * input of each letter.
	 * 
	 * @param text
	 *        - text to be input.
	 * @param intervalInMs
	 *        - interval in milliseconds between the input of each letter.
	 * @param revalidateElement
	 *        - boolean indicating if the element should be revalidated prior to text inputting.
	 */
	public void inputText(String text, int intervalInMs, boolean revalidateElement)
	{
		focus(revalidateElement);
		onDevice.inputText(text, intervalInMs);
	}

	/**
	 * Checks if the current UI element is still valid and if so, inputs text into it <b>if it supports text input</b>
	 * with interval in milliseconds between the input of each letter.
	 * 
	 * @param text
	 *        - text to be input.
	 * @param intervalInMs
	 *        - interval in milliseconds between the input of each letter.
	 */
	public void inputText(String text, int intervalInMs)
	{
		inputText(text, intervalInMs, true);
	}

	/**
	 * Inputs text into the UI Element <b>if it supports text input</b>.
	 * 
	 * @param text
	 *        - text to be input.
	 * @param revalidateElement
	 *        - boolean indicating if the element should be revalidated prior to text inputting.
	 */
	public void inputText(String text, boolean revalidateElement)
	{
		inputText(text, 0, revalidateElement);
	}

	/**
	 * Checks if the current UI element is still valid and if so, inputs text into it <b>if it supports text input</b>.
	 * 
	 * @param text
	 *        - text to be input.
	 */
	public void inputText(String text)
	{
		inputText(text, true);
	}

	/**
	 * Focuses the current element.
	 * 
	 * @param revalidateElement
	 *        - boolean indicating if the element should be revalidated prior to focusing.
	 */
	public void focus(boolean revalidateElement)
	{
		if (revalidateElement)
		{
			revalidateThrowing();
		}

		if (!elementAttributes.isFocusable())
		{
			throw new InvalidElementActionException("Attempting to focus a non-focusable element.");
		}
		if (elementAttributes.isFocused())
		{
			return;
		}

		// The element is already validated if the flag is set, so no need to validate it again.
		tap(false);

		if (revalidate())
		{
			if (!elementAttributes.isFocused())
			{
				throw new ActionFailedException("Focusing element failed.");
			}
		}
	}

	/**
	 * Checks if the current UI element is still valid and if so, focuses it.
	 */
	public void focus()
	{
		focus(true);
	}

	private void revalidateThrowing()
	{
		String thisElementQuery = elementAttributes.buildCssQuery();
		Screen newScreen = onDevice.getActiveScreen();
		try
		{
			UiElement thisElementRefetched = newScreen.getElementCSS(thisElementQuery);
			elementAttributes = thisElementRefetched.getElementAttributes(false);
		}
		catch (UiElementFetchingException e)
		{
			// If fetching this element resulted in fetching exception, it is no longer valid.
			throw new StaleElementReferenceException("Element revalidation failed.", e);
		}
	}

	/**
	 * Checks if the current element is still valid (on the screen) and updates it's attributes container.
	 * 
	 * @return true if the current element is still valid, false otherwise.
	 */
	public boolean revalidate()
	{
		try
		{
			revalidateThrowing();
			return true;
		}
		catch (StaleElementReferenceException e)
		{
			return false;
		}
	}
}
