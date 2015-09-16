package com.musala.atmosphere.client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.musala.atmosphere.client.exceptions.InvalidCssQueryException;
import com.musala.atmosphere.client.uiutils.CssToXPathConverter;
import com.musala.atmosphere.client.uiutils.UiXmlParser;
import com.musala.atmosphere.commons.exceptions.UiElementFetchingException;
import com.musala.atmosphere.commons.ui.selector.UiElementSelector;

/**
 * {@link UiElement} represented as node of a UI hierarchy XML structure. Provides XPath and JSoup compatible methods
 * for retrieving and operating on Xml Nodes.
 * 
 * @author vassil.angelov
 *
 */
public class XmlNodeUiElement extends UiElement {

    private static final Logger LOGGER = Logger.getLogger(XmlNodeUiElement.class);

    protected enum ElementNodeType {
        XPATH_NODE,
        JSOUP_NODE;
    }

    protected ElementNodeType underlyingNodeType;

    protected Node representedNodeXPath;

    protected org.jsoup.nodes.Node representedNodeJSoup;

    private UiElementValidator validator;

    /**
     * Constructor for element creation vie attributes map.
     * 
     * @param nodeAttributesMap
     *        - the map of UI element attributes and values
     * @param onDevice
     *        - the device associated with this {@link UiElement}
     * @see UiElementSelector
     */
    protected XmlNodeUiElement(Map<String, String> nodeAttributesMap, Device onDevice) {
        super(new UiElementSelector(nodeAttributesMap), onDevice);
        validator = onDevice.getUiValidator();
        validator.addElementForValidation(this);
    }

    /**
     * Constructor for element creation via a XPath query.
     * 
     * @param representingNode
     *        - the {@link Node} which represents this {@link UiElement}
     * @param onDevice
     *        - the device associated with this {@link UiElement}
     */
    protected XmlNodeUiElement(Node representingNode, Device onDevice) {
        this(UiXmlParser.getAttributeMapOfNode(representingNode), onDevice);
        underlyingNodeType = ElementNodeType.XPATH_NODE;
        representedNodeXPath = representingNode;
    }

    /**
     * Returns the current UI element's attributes data container.
     *
     * @return a {@link UiElementSelector} instance, containing all attributes of this UiElement.
     */
    @Deprecated
    public UiElementSelector getElementSelector() {
        return (UiElementSelector) propertiesContainer;
    }

    /**
     * Copy constructor from another {@link XmlNodeUiElement}.
     * 
     * @param other
     *        - the element to copy from.
     */
    protected XmlNodeUiElement(XmlNodeUiElement other) {
        super(new UiElementSelector(other.getAttributesMap()), other.onDevice);

        underlyingNodeType = other.underlyingNodeType;
        representedNodeXPath = other.representedNodeXPath;
        representedNodeJSoup = other.representedNodeJSoup;

        validator = onDevice.getUiValidator();
        validator.addElementForValidation(this);
    }

    private Map<String, String> getAttributesMap() {
        if (underlyingNodeType == ElementNodeType.JSOUP_NODE) {
            return UiXmlParser.getAttributeMapOfNode(representedNodeJSoup);

        } else {
            return UiXmlParser.getAttributeMapOfNode(representedNodeXPath);
        }
    }

    @Override
    public boolean revalidate() {
        // TODO: The revalidation is not working as expected and must be fixed.
        // if (!isStale) {
        // validator.forceRevalidation();
        // // if this element is no longer valid, the revalidation procedure
        // // will have set it to stale.
        // }
        return !isStale;
    }

    @Override
    public List<UiElement> getDirectChildren() {
        NodeList nodeChildren = representedNodeXPath.getChildNodes();
        List<UiElement> result = new LinkedList<UiElement>();

        for (int i = 0; i < nodeChildren.getLength(); i++) {
            Node childNode = nodeChildren.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                // our node is 'fake' and it's not used in the screen's xml
                continue;
            }
            UiElement childElement = new XmlNodeUiElement(childNode, onDevice);
            result.add(childElement);
        }

        return result;
    }

    /**
     * Gets all child {@link UiElement UiElements} that match the passed query.
     * 
     * @param xPathQuery
     *        XPath type node selecting query.
     * @return Returns all the children of the UiElement that matched the xPathQuery
     */
    public List<UiElement> getChildrenByXPath(String xPathQuery) {

        // Creating new Document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            String message = "Creating new document builder failed.";
            LOGGER.error(message, e);
            // there is no point in continuing
            return null;
        }
        Document newDocument = builder.newDocument();

        // Importing the node of the UiElement in the new Document
        // We can't use the Node directly a new Node instance is needed
        Node importedNode = newDocument.importNode(representedNodeXPath, true);
        newDocument.appendChild(importedNode);

        // Constructing the UiElements by given xPathNode
        NodeList matchedNodes = null;
        try {
            matchedNodes = UiXmlParser.getXPathNodeChildren(newDocument, xPathQuery);
        } catch (XPathExpressionException | UiElementFetchingException e) {
            String message = "Getting Ui element's children failed.";
            LOGGER.error(message, e);
            return null;
        }

        List<UiElement> matchedChildrenNodes = new LinkedList<UiElement>();
        for (int i = 0; i < matchedNodes.getLength(); i++) {
            Node childNode = matchedNodes.item(i);
            if (!childNode.isEqualNode(representedNodeXPath)) {
                XmlNodeUiElement returnElement = new XmlNodeUiElement(childNode, onDevice);
                matchedChildrenNodes.add(returnElement);
            }
        }

        return matchedChildrenNodes;
    }

    @Override
    public List<UiElement> getChildrenByCssQuery(String cssQuery) {
        String childRetrievalErrorMessage = String.format("Failed attempt to retrieve children from %s.",
                                                          propertiesContainer.getPackageName());
        String convertedXPathQuery = null;
        try {
            convertedXPathQuery = CssToXPathConverter.convertCssToXPath(cssQuery);
        } catch (InvalidCssQueryException e) {
            LOGGER.error(childRetrievalErrorMessage, e);
        }

        return getChildrenByXPath(convertedXPathQuery);
    }

    /**
     * Gets all child UiElements that match the given {@link UiElementSelector}.
     * 
     * @param childrenSelector
     *        - an object of type {@link UiElementSelector} that needs to match child UI elements
     * @return a list of {@link UiElement} children that match the given selector
     * 
     */
    public List<UiElement> getChildren(UiElementSelector childrenSelector) {
        String cssQuery = childrenSelector.buildCssQuery();
        return getChildrenByCssQuery(cssQuery);
    }

    @Override
    public List<UiElement> getDirectChildren(UiElementSelector childrenSelector) {
        String cssQuery = childrenSelector.buildCssQuery();

        try {
            String convertedXPathQuery = CssToXPathConverter.convertCssToXPath(cssQuery);
            String queryPrefix = String.format("/%s/child::", representedNodeXPath.getNodeName());
            convertedXPathQuery = convertedXPathQuery.replaceFirst("//", queryPrefix);
            return new ArrayList<UiElement>(getChildrenByXPath(convertedXPathQuery));
        } catch (InvalidCssQueryException e) {
            LOGGER.error(String.format("Failed attempt to retrieve children from %s.",
                                       propertiesContainer.getPackageName()),
                         e);
        }

        return new ArrayList<UiElement>();
    }
}
