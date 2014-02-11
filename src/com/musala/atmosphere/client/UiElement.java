package com.musala.atmosphere.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.musala.atmosphere.client.exceptions.InvalidElementActionException;
import com.musala.atmosphere.client.exceptions.StaleElementReferenceException;
import com.musala.atmosphere.client.exceptions.UiElementFetchingException;
import com.musala.atmosphere.client.geometry.Bounds;
import com.musala.atmosphere.client.geometry.Point;
import com.musala.atmosphere.client.uiutils.CssAttribute;
import com.musala.atmosphere.client.uiutils.UiElementSelector;
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

	private UiElementSelector elementSelector;

	private Device onDevice;

	/**
	 * Constructor for element creation via a XPath query.
	 *
	 * @param representingNode
	 * @param onDevice
	 */
	UiElement(Node representingNode, Device onDevice)
	{
		this.underlyingNodeType = ElementNodeType.XPATH_NODE;
		this.representedNodeXPath = representingNode;
		Map<String, String> nodeAttributesMap = UiXmlParser.getAttributeMapOfNode(representingNode);
		this.elementSelector = new UiElementSelector(nodeAttributesMap);
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
		this.underlyingNodeType = ElementNodeType.JSOUP_NODE;
		this.representedNodeJSoup = representingNode;
		Map<String, String> nodeAttributesMap = UiXmlParser.getAttributeMapOfNode(representingNode);
		this.elementSelector = new UiElementSelector(nodeAttributesMap);
		this.onDevice = onDevice;
	}

	/**
	 * Refreshes and then returns the current UI element's attributes data container.
	 *
	 * @return a {@link UiElementSelector} instance.
	 */
	public UiElementSelector getElementSelector()
	{
		UiElementSelector result = getElementSelector(true);
		return result;
	}

	/**
	 * Returns the current UI element's attributes data container.
	 *
	 * @param refresh
	 *        - boolean indicating if the attributes should be refreshed prior to returning.
	 * @return a {@link UiElementSelector} instance.
	 */
	public UiElementSelector getElementSelector(boolean refresh)
	{
		if (refresh)
		{
			revalidateThrowing();
		}
		return elementSelector;
	}

	/**
	 * Simulates tapping on a relative point in the current UI element.
	 *
	 * @param point
	 *        - the relative point that will be added to the upper left corner's coordinates.
	 * @param revalidateElement
	 *        - boolean indicating if the element should be revalidated prior to tapping.
	 * @return <code>true</code> if the tapping is successful, <code>false</code> if it fails.
	 */
	public boolean tap(Point point, boolean revalidateElement)
	{
		if (revalidateElement)
		{
			revalidateThrowing();
		}

		Bounds elementBounds = elementSelector.getBoundsValue(CssAttribute.BOUNDS);
		Point tapPoint = elementBounds.getUpperLeftCorner();
		tapPoint.addVector(point);

		if (elementBounds.contains(tapPoint))
		{
			return onDevice.tapScreenLocation(tapPoint);
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
	 * @return <code>true</code> if the tapping is successful, <code>false</code> if it fails.
	 */
	public boolean tap(Point point)
	{
		return tap(point, true);
	}

	/**
	 * Simulates tapping on the current UI Element.
	 *
	 * @param revalidateElement
	 *        - boolean indicating if the element should be revalidated prior to tapping.
	 * @return <code>true</code> if the tapping is successful, <code>false</code> if it fails.
	 */
	public boolean tap(boolean revalidateElement)
	{
		Bounds elementBounds = elementSelector.getBoundsValue(CssAttribute.BOUNDS);
		Point centerPoint = elementBounds.getCenter();
		Point tapPoint = elementBounds.getRelativePoint(centerPoint);

		return tap(tapPoint, revalidateElement);
	}

	/**
	 * Checks if the current UI element is still valid and if so, simulates tapping on it.
	 *
	 * @param revalidateElement
	 *        - boolean indicating if the element should be revalidated prior to tapping.
	 * @return <code>true</code> if the tapping is successful, <code>false</code> if it fails.
	 */
	public boolean tap()
	{
		return tap(true);
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
	 *
	 * @return <code>true</code> if the holding is successful, <code>false</code> if it fails.
	 */
	public boolean hold()
	{
		// TODO implement uiElement.hold()
		return false;
	}

	/**
	 * Simulates double-tapping on the given UI element.
	 *
	 * @return <code>true</code> if the double tapping is successful, <code>false</code> if it fails.
	 */
	public boolean doubleTap()
	{
		// TODO implement uiElement.doubleTap()
		return false;
	}

	/**
	 * Simulates dragging the UI widget until his ( which corner exactly? ) upper-left corner stands at position
	 * (toX,toY) on the screen.
	 *
	 * @param toX
	 * @param toY
	 * @return <code>true</code> if the dragging is successful, <code>false</code> if it fails.
	 */
	public boolean drag(int toX, int toY)
	{
		// TODO implement uiElement.drag()
		return false;
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
	 * @return <code>true</code> if the text input is successful, <code>false</code> if it fails.
	 */
	public boolean inputText(String text, int intervalInMs, boolean revalidateElement)
	{
		focus(revalidateElement);
		return onDevice.inputText(text, intervalInMs);
	}

	/**
	 * Checks if the current UI element is still valid and if so, inputs text into it <b>if it supports text input</b>
	 * with interval in milliseconds between the input of each letter.
	 *
	 * @param text
	 *        - text to be input.
	 * @param intervalInMs
	 *        - interval in milliseconds between the input of each letter.
	 * @return <code>true</code> if the text input is successful, <code>false</code> if it fails.
	 */
	public boolean inputText(String text, int intervalInMs)
	{
		return inputText(text, intervalInMs, true);
	}

	/**
	 * Inputs text into the UI Element <b>if it supports text input</b>.
	 *
	 * @param text
	 *        - text to be input.
	 * @param revalidateElement
	 *        - boolean indicating if the element should be revalidated prior to text inputting.
	 * @return <code>true</code> if the text input is successful, <code>false</code> if it fails.
	 */
	public boolean inputText(String text, boolean revalidateElement)
	{
		return inputText(text, 0, revalidateElement);
	}

	/**
	 * Checks if the current UI element is still valid and if so, inputs text into it <b>if it supports text input</b>.
	 *
	 * @param text
	 *        - text to be input.
	 * @return <code>true</code> if the text input is successful, <code>false</code> if it fails.
	 */
	public boolean inputText(String text)
	{
		return inputText(text, true);
	}

	/**
	 * Focuses the current element.
	 *
	 * @param revalidateElement
	 *        - boolean indicating if the element should be revalidated prior to focusing.
	 * @return <code>true</code> if the focusing is successful, <code>false</code> if it fails.
	 */
	public boolean focus(boolean revalidateElement)
	{
		if (revalidateElement)
		{
			revalidateThrowing();
		}

		if (!elementSelector.getBooleanValue(CssAttribute.FOCUSABLE))
		{
			throw new InvalidElementActionException("Attempting to focus a non-focusable element.");
		}
		if (elementSelector.getBooleanValue(CssAttribute.FOCUSED))
		{
			return true;
		}

		// The element is already validated if the flag is set, so no need to validate it again.
		tap(false);

		if (revalidate())
		{
			if (!elementSelector.getBooleanValue(CssAttribute.FOCUSED))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the current UI element is still valid and if so, focuses it.
	 *
	 * @return <code>true</code> if the focusing is successful, <code>false</code> if it fails.
	 */
	public boolean focus()
	{
		return focus(true);
	}

	private void revalidateThrowing()
	{
		String thisElementQuery = elementSelector.buildCssQuery();
		Screen newScreen = onDevice.getActiveScreen();
		try
		{
			UiElement thisElementRefetched = newScreen.getElementByCSS(thisElementQuery);
			elementSelector = thisElementRefetched.getElementSelector(false);
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
