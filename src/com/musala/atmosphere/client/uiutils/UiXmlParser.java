package com.musala.atmosphere.client.uiutils;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.musala.atmosphere.client.exceptions.UiElementFetchingException;

/**
 * Contains static helper methods that parse XML documents.
 * 
 * @author georgi.gaydarov
 * 
 */
public class UiXmlParser
{
	/**
	 * Gets a {@link Node Node} from a {@link Document Document} by a XPath query.
	 * 
	 * @param domDocument
	 *        document to search in.
	 * @param xPathQuery
	 *        XPath type node selecting query.
	 * @return the found {@link Node Node} object.
	 * @throws UiElementFetchingException
	 * @throws XPathExpressionException
	 */
	public static Node getXPathNode(Document domDocument, String xPathQuery)
		throws UiElementFetchingException,
			XPathExpressionException
	{
		XPath x = XPathFactory.newInstance().newXPath();
		XPathExpression expression = x.compile(xPathQuery);
		Node node = (Node) expression.evaluate(domDocument, XPathConstants.NODE);

		if (node == null)
		{
			throw new UiElementFetchingException("No element found for the passed XPath expression.");
		}

		return node;
	}

	/**
	 * Returns all attributes of an XPath {@link Node Node} object.
	 * 
	 * @param node
	 * @return the object attribute map.
	 */
	public static Map<String, String> getAttributeMapOfNode(Node node)
	{
		Map<String, String> nodeAttributeMap = new HashMap<String, String>();
		NamedNodeMap xPathAttributeMap = node.getAttributes();
		for (int i = 0; i < xPathAttributeMap.getLength(); i++)
		{
			Node attribute = xPathAttributeMap.item(i);
			if (attribute.getNodeType() != Node.ATTRIBUTE_NODE)
			{
				continue;
			}
			nodeAttributeMap.put(attribute.getNodeName(), attribute.getNodeValue());
		}

		return nodeAttributeMap;
	}

	/**
	 * Gets a {@link org.jsoup.nodes.Node Node} from a {@link org.jsoup.nodes.Document Document} by a JSoup query.
	 * 
	 * @param document
	 *        document to search in.
	 * @param query
	 *        JSoup type element selecting query.
	 * @return the found {@link org.jsoup.nodes.Node Node} object.
	 * @throws UiElementFetchingException
	 */
	public static org.jsoup.nodes.Node getJSoupNode(org.jsoup.nodes.Document document, String query)
		throws UiElementFetchingException
	{
		Elements elements = document.select(query);
		int foundElements = elements.size();
		if (foundElements == 0)
		{
			throw new UiElementFetchingException("No element found for the passed JSoup expression.");
		}
		if (foundElements > 1)
		{
			throw new UiElementFetchingException("Found " + foundElements
					+ " elements that match the element selecting query. Please be more specific.");
		}

		org.jsoup.nodes.Node node = elements.get(0);
		return node;
	}

	/**
	 * Returns all attributes of an JSoup {@link org.jsoup.nodes.Node Node} object.
	 * 
	 * @param node
	 * @return the object attribute map.
	 */
	public static Map<String, String> getAttributeMapOfNode(org.jsoup.nodes.Node node)
	{
		Map<String, String> nodeAttributeMap = new HashMap<String, String>();
		Attributes nodeAttributes = node.attributes();
		for (Attribute attribute : nodeAttributes)
		{
			nodeAttributeMap.put(attribute.getKey(), attribute.getValue());
		}
		return nodeAttributeMap;
	}

}
