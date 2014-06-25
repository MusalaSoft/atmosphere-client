package com.musala.atmosphere.client.uiutils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
import org.w3c.dom.NodeList;

import com.musala.atmosphere.client.exceptions.UiElementFetchingException;

/**
 * Contains static helper methods that parse XML documents.
 * 
 * @author georgi.gaydarov
 * 
 */
public class UiXmlParser {
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
            XPathExpressionException {
        XPath x = XPathFactory.newInstance().newXPath();
        XPathExpression expression = x.compile(xPathQuery);

        NodeList nodeList = (NodeList) expression.evaluate(domDocument, XPathConstants.NODESET);
        int foundElements = 0;
        if (nodeList == null || nodeList.getLength() == 0) {
            throw new UiElementFetchingException("No element found for the XPath expression .");
        }
        foundElements = nodeList.getLength();
        if (foundElements > 1) {
            throw new UiElementFetchingException("Found " + foundElements
                    + " elements that match the element selecting query. Please be more specific.");
        }

        return nodeList.item(0);
    }

    /**
     * Returns all attributes of an XPath {@link Node Node} object.
     * 
     * @param node
     * @return the object attribute map.
     */
    public static Map<String, String> getAttributeMapOfNode(Node node) {
        Map<String, String> nodeAttributeMap = new HashMap<String, String>();
        NamedNodeMap xPathAttributeMap = node.getAttributes();
        for (int i = 0; i < xPathAttributeMap.getLength(); i++) {
            Node attribute = xPathAttributeMap.item(i);
            if (attribute.getNodeType() != Node.ATTRIBUTE_NODE) {
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
        throws UiElementFetchingException {
        List<org.jsoup.nodes.Node> elements = getAllJSoupNodes(document, query);
        int foundElements = elements.size();
        if (foundElements > 1) {
            String exceptionMessage = String.format("Found %d elements that match the query \"%s\". Please be more specific.",
                                                    foundElements,
                                                    query);
            throw new UiElementFetchingException(exceptionMessage);
        }

        org.jsoup.nodes.Node node = elements.get(0);
        return node;
    }

    /**
     * Gets a List<{@link org.jsoup.nodes.Node Node}> from a {@link org.jsoup.nodes.Document Document} by a JSoup query.
     * 
     * @param document
     *        document to search in.
     * @param query
     *        JSoup type element selecting query.
     * @return - list with all found {@link org.jsoup.nodes.Node Node} objects.
     * @throws UiElementFetchingException
     *         - when no elements are found for the passed JSoup expression.
     */
    public static List<org.jsoup.nodes.Node> getAllJSoupNodes(org.jsoup.nodes.Document document, String query)
        throws UiElementFetchingException {
        Elements elements = document.select(query);

        if (elements.isEmpty()) {
            String exceptionMessage = "No elements found for the passed JSoup expression.";
            throw new UiElementFetchingException(exceptionMessage);
        }

        List<org.jsoup.nodes.Node> allNodes = new LinkedList<>();
        allNodes.addAll(elements);
        return allNodes;
    }

    /**
     * Gets an {@link org.jsoup.select.Elements Elements} object from a {@link org.jsoup.nodes.Document Document} by a
     * JSoup query. Elements contains all elements of type {@link org.jsoup.nodes.Node Node} found by the query.
     * 
     * @param document
     *        document to search in.
     * @param query
     *        JSoup type element selecting query.
     * @return the found {@link org.jsoup.select.Elements Elements}.
     * @throws UiElementFetchingException
     */
    public static Elements getJSoupElements(org.jsoup.nodes.Document document, String query)
        throws UiElementFetchingException {
        Elements elements = document.select(query);
        int foundElements = elements.size();
        if (foundElements == 0) {
            throw new UiElementFetchingException("No element found for the passed JSoup expression.");
        }
        return elements;
    }

    /**
     * Returns all attributes of an JSoup {@link org.jsoup.nodes.Node Node} object.
     * 
     * @param node
     * @return the object attribute map.
     */
    public static Map<String, String> getAttributeMapOfNode(org.jsoup.nodes.Node node) {
        Map<String, String> nodeAttributeMap = new HashMap<String, String>();
        Attributes nodeAttributes = node.attributes();
        for (Attribute attribute : nodeAttributes) {
            nodeAttributeMap.put(attribute.getKey(), attribute.getValue());
        }
        return nodeAttributeMap;
    }

    /**
     * Gets a NodeList of all the Nodes that matched the query
     * 
     * @param domDocument
     *        document to search in.
     * @param xPathQuery
     *        XPath type node selecting query.
     * @return NodeList containing all Nodes that matched the query
     * @throws UiElementFetchingException
     * @throws XPathExpressionException
     */
    public static NodeList getXPathNodeChildren(Document domDocument, String xPathQuery)
        throws UiElementFetchingException,
            XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression expression = xPath.compile(xPathQuery);

        NodeList nodeListResults = (NodeList) expression.evaluate(domDocument, XPathConstants.NODESET);
        if (nodeListResults == null || nodeListResults.getLength() == 0) {
            throw new UiElementFetchingException("No elements found for the XPath expression .");
        }
        return nodeListResults;
    }

}
