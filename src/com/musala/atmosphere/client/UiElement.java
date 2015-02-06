package com.musala.atmosphere.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.musala.atmosphere.client.exceptions.InvalidCssQueryException;
import com.musala.atmosphere.client.exceptions.InvalidElementActionException;
import com.musala.atmosphere.client.exceptions.StaleElementReferenceException;
import com.musala.atmosphere.client.exceptions.UiElementFetchingException;
import com.musala.atmosphere.client.geometry.Bounds;
import com.musala.atmosphere.client.geometry.Point;
import com.musala.atmosphere.client.uiutils.CssAttribute;
import com.musala.atmosphere.client.uiutils.CssToXPathConverter;
import com.musala.atmosphere.client.uiutils.UiElementSelector;
import com.musala.atmosphere.client.uiutils.UiXmlParser;
import com.musala.atmosphere.client.util.settings.ElementValidationType;
import com.musala.atmosphere.commons.beans.SwipeDirection;

/**
 * Used to access and manipulate certain views on the testing device, for example tapping, double-taping or holding
 * finger on given widget.
 * 
 * @author georgi.gaydarov
 * 
 */
public class UiElement {
    protected enum ElementNodeType {
        XPATH_NODE,
        JSOUP_NODE;
    }

    private static final String CUT_BUTTON_CONTENT_DESCRIPTION = "Cut";

    private static final String TEXT_VIEW_CLASS_NAME = "android.widget.TextView";

    private static final String SELECT_ALL_CONTENT_DESCRIPTION = "Select all";

    protected ElementNodeType underlyingNodeType;

    private static final long UI_ELEMENT_OPERATION_WAIT_TIME = 500;

    protected Node representedNodeXPath;

    private static final Logger LOGGER = Logger.getLogger(UiElement.class);

    protected UiElementSelector elementSelector;

    protected org.jsoup.nodes.Node representedNodeJSoup;

    protected Device onDevice;

    protected DeviceCommunicator communicator;

    private ElementValidationType validationType;

    private UiElementValidator validator = new UiElementValidator();

    private boolean isStale = false;

    protected UiElement(Map<String, String> nodeAttributesMap, Device onDevice) {
        elementSelector = new UiElementSelector(nodeAttributesMap);
        this.onDevice = onDevice;
        communicator = onDevice.getCommunicator();
        validationType = ElementValidationType.MANUAL;
        validator = onDevice.getUiValidator();
        validator.addElementForValidation(this);
    }

    /**
     * Constructor for element creation via a XPath query.
     * 
     * @param representingNode
     * @param onDevice
     */
    protected UiElement(Node representingNode, Device onDevice) {
        this(UiXmlParser.getAttributeMapOfNode(representingNode), onDevice);
        underlyingNodeType = ElementNodeType.XPATH_NODE;
        representedNodeXPath = representingNode;
    }

    UiElement(UiElement uiElement) {
        Map<String, String> nodeAttributesMap;
        if (uiElement.underlyingNodeType == ElementNodeType.JSOUP_NODE) {
            nodeAttributesMap = UiXmlParser.getAttributeMapOfNode(uiElement.representedNodeJSoup);
            representedNodeJSoup = uiElement.representedNodeJSoup;
        } else {
            nodeAttributesMap = UiXmlParser.getAttributeMapOfNode(uiElement.representedNodeXPath);
            representedNodeXPath = uiElement.representedNodeXPath;
        }
        underlyingNodeType = uiElement.underlyingNodeType;
        elementSelector = new UiElementSelector(nodeAttributesMap);
        this.onDevice = uiElement.onDevice;
        communicator = onDevice.getCommunicator();
        validationType = ElementValidationType.MANUAL;
        validator = onDevice.getUiValidator();
        validator.addElementForValidation(this);
    }

    /**
     * Sets the level of element validity verification of the given UI element before operating with it.
     * 
     * @param validation
     *        - the way of how the UiElement should be checked for validity - manually by the QA, or automatically, just
     *        before an operation with it arises.
     */
    public void setValidationType(ElementValidationType validation) {
        validationType = validation;
    }

    /**
     * Returns an element that shows how the type the validity of the given {@link UiElement} is done - manually or
     * automatically.
     * 
     * @return - an {@link ElementValidationType} instance, denoting whether this UiElement is updated automatically
     *         when the screen changes, or the user should update its screen regularly.
     */
    public ElementValidationType getValidationType() {
        return validationType;
    }

    /**
     * Returns the current UI element's attributes data container.
     * 
     * @return a {@link UiElementSelector} instance, containing all attributes of this UiElement.
     */
    public UiElementSelector getElementSelector() {
        innerRevalidation();
        return elementSelector;
    }

    /**
     * Simulates tapping on a relative point in the current UI element.
     * 
     * @param point
     *        - the relative point that will be added to the upper left corner's coordinates.
     * @return <code>true</code> if the tapping is successful, <code>false</code> if it fails.
     */
    public boolean tap(Point point) {
        innerRevalidation();
        Bounds elementBounds = elementSelector.getBoundsValue(CssAttribute.BOUNDS);
        Point tapPoint = elementBounds.getUpperLeftCorner();
        tapPoint.addVector(point);

        if (elementBounds.contains(tapPoint)) {
            boolean isElementTapped = onDevice.tapScreenLocation(tapPoint);
            finalizeUiElementOperation();
            return isElementTapped;
        } else {
            String message = String.format("Point %s not in element bounds.", point.toString());
            LOGGER.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Simulates tapping in the center of this UI Element.
     * 
     * @return <code>true</code> if the tapping is successful, <code>false</code> if it fails.
     */
    public boolean tap() {
        Bounds elementBounds = elementSelector.getBoundsValue(CssAttribute.BOUNDS);
        Point centerPoint = elementBounds.getCenter();
        Point tapPoint = elementBounds.getRelativePoint(centerPoint);

        return tap(tapPoint);
    }

    /**
     * Searches for a child UI element that corresponds to the given {@link UiElementSelector} and taps on it.
     * 
     * @param selector
     *        - a {@link UiElementSelector} that needs to match a certain child UI element
     * @return true if the tap on the child UI element was successful, false otherwise
     * @throws UiElementFetchingException
     *         - if the UI element could not be found
     * @throws XPathExpressionException
     *         - if the XPath expression resulting from the conversion of the {@link UiElementSelector} is invalid
     * @throws InvalidCssQueryException
     *         - if the CSS query corresponding to the {@link UiElementSelector} is invalid
     * @throws ParserConfigurationException
     *         - if an error with internal XPath configuration occurs
     */
    public boolean tapOnChildElement(UiElementSelector selector)
        throws UiElementFetchingException,
            XPathExpressionException,
            InvalidCssQueryException,
            ParserConfigurationException {
        String cssQuery = selector.buildCssQuery();
        List<UiElement> childrenElements = getChildrenByCssQuery(cssQuery);

        if (childrenElements.isEmpty()) {
            String message = "No child element corresponding to the given selector was found.";
            LOGGER.error(message);
            throw new UiElementFetchingException(message);
        }

        if (childrenElements.size() > 1) {
            String message = "More than one child element corresponding to the given selector was found.";
            LOGGER.error(message);
            throw new UiElementFetchingException(message);
        }

        UiElement elementToTapOn = childrenElements.get(0);

        return elementToTapOn.tap();
    }

    /**
     * Used to get the text of this UiElement.
     * 
     * @return <code>String</code> with the content of the text property of this UiElement.
     */
    public String getText() {
        String text = elementSelector.getStringValue(CssAttribute.TEXT);
        return text;
    }

    /**
     * Simulates holding finger in the center of this UiElement. <i><b>Warning: method not yet implemented!</b></i>
     * 
     * @return <code>true</code> if the holding is successful, <code>false</code> if it fails.
     */
    public boolean hold() {
        // TODO implement uiElement.hold() and update the java doc
        return false;
    }

    /**
     * Simulates double-tapping in the center of this UiElement.
     * 
     * @return <code>true</code> if the double tapping is successful, <code>false</code> if it fails.
     */
    public boolean doubleTap() {
        Bounds elementBounds = elementSelector.getBoundsValue(CssAttribute.BOUNDS);
        Point centerPoint = elementBounds.getCenter();
        Point tapPoint = elementBounds.getRelativePoint(centerPoint);

        return doubleTap(tapPoint);
    }

    /**
     * Simulates double-tapping on a point in this UiElement.
     * 
     * @param point
     *        - a {@link Point} object, representing the relative coordinates of the point to tap inside this UiElement.
     *        <i><b><u>Note</u></b>: the point with relative coordinates (0,0) denotes the upper-left corner of the
     *        UiElement</i>
     * @return <code>true</code> if the double tapping is successful, <code>false</code> if it fails.
     */
    public boolean doubleTap(Point point) {
        innerRevalidation();
        Bounds elementBounds = elementSelector.getBoundsValue(CssAttribute.BOUNDS);
        Point tapPoint = elementBounds.getUpperLeftCorner();
        tapPoint.addVector(point);

        if (elementBounds.contains(tapPoint)) {
            boolean isElementTapped = onDevice.doubleTap(tapPoint);
            finalizeUiElementOperation();
            return isElementTapped;
        } else {
            String message = String.format("Point %s not in element bounds.", point.toString());
            LOGGER.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Simulates a pinch in on the element. NOTE emulator devices may not detect pinch gestures on UI elements with size
     * smaller than 100x100dp.
     * 
     * @return <code>true</code> if the pinch in is successful, <code>false</code> if it fails.
     */
    public boolean pinchIn() {
        innerRevalidation();

        Bounds elementBounds = elementSelector.getBoundsValue(CssAttribute.BOUNDS);
        final int BOUNDS_OFFSET_DENOMINATOR = 10;
        final int WIDTH_OFFSET = elementBounds.getWidth() / BOUNDS_OFFSET_DENOMINATOR;
        final int HEIGHT_OFFSET = elementBounds.getHeight() / BOUNDS_OFFSET_DENOMINATOR;

        // starting the pinch at a distance from the exact bounds of the element so that it will not affect other UI
        // elements
        Point lowerRight = elementBounds.getLowerRightCorner();
        int firstFingerInitialX = lowerRight.getX() - WIDTH_OFFSET;
        int firstFingerInitialY = lowerRight.getY() - HEIGHT_OFFSET;
        Point firstFingerInitial = new Point(firstFingerInitialX, firstFingerInitialY);

        Point upperLeft = elementBounds.getUpperLeftCorner();
        int secondFingerInitialX = upperLeft.getX() + WIDTH_OFFSET;
        int secondFingerInitialY = upperLeft.getY() + HEIGHT_OFFSET;
        Point secondFingerInitial = new Point(secondFingerInitialX, secondFingerInitialY);

        boolean result = onDevice.pinchIn(firstFingerInitial, secondFingerInitial);

        return result;
    }

    /**
     * Simulates a pinch out on the element. NOTE emulator devices may not detect pinch gestures on UI elements with
     * size smaller than 100x100dp.
     * 
     * @return <code>true</code> if the pinch out is successful, <code>false</code> if it fails.
     */
    public boolean pinchOut() {
        innerRevalidation();

        Bounds elementBounds = elementSelector.getBoundsValue(CssAttribute.BOUNDS);
        Point firstFingerEnd = elementBounds.getUpperLeftCorner();
        Point secondFingerEnd = elementBounds.getLowerRightCorner();

        boolean result = onDevice.pinchOut(firstFingerEnd, secondFingerEnd);
        return result;
    }

    /**
     * Simulates dragging the UI widget until his upper-left corner stands at position (toX,toY) on the screen.
     * <i><b>Warning: method not yet implemented!</b></i>
     * 
     * @param toX
     *        - X-coordinate of the upper-left corner of the UI widget after the dragging is done
     * @param toY
     *        - Y-coordinate of the upper-left corner of the UI widget after the dragging is done
     * @return <code>true</code> if the dragging is successful, <code>false</code> if it fails.
     */
    public boolean drag(int toX, int toY) {
        // TODO implement uiElement.drag()
        return false;
    }

    /**
     * Simulates swiping this UiElement.
     * 
     * @param swipeDirection
     *        - a {@link SwipeDirection}, describing the direction of the swipe.
     * @return <code>true</code> if the swiping is successful, <code>false</code> if it fails.
     */
    public boolean swipe(SwipeDirection swipeDirection) {
        Bounds elementBounds = elementSelector.getBoundsValue(CssAttribute.BOUNDS);
        Point centerPoint = elementBounds.getCenter();

        return swipe(centerPoint, swipeDirection);
    }

    /**
     * Swipes this element in particular direction.
     * 
     * @param point
     *        -a {@link Point} the point from which the swipe start.
     * @param direction
     *        - a {@link SwipeDirection}, describing the direction of the swipe.
     * @return boolean indicating if this action was successful.
     */
    public boolean swipe(Point point, SwipeDirection direction) {
        innerRevalidation();
        boolean response = onDevice.swipe(point, direction);
        return response;
    }

    /**
     * Clears the contents of this element.
     * 
     * @return boolean indicating if this action was successful.
     * @throws InvalidCssQueryException
     *         if element is searched by invalid CSS query
     * @throws UiElementFetchingException
     *         if element could not be found
     * @throws XPathExpressionException
     *         if element is searched by invalid XPath query
     */
    public boolean clearText() throws XPathExpressionException, UiElementFetchingException, InvalidCssQueryException {
        // TODO validate when an element can get it's text cleared
        innerRevalidation();
        focus();

        return onDevice.clearText();
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
    public boolean inputText(String text, int intervalInMs) {
        innerRevalidation();
        focus();
        boolean success = onDevice.inputText(text, intervalInMs);
        finalizeUiElementOperation();
        return success;
    }

    /**
     * Inputs text into the UI Element <b>if it supports text input</b>.
     * 
     * @param text
     *        - text to be input.
     * @return <code>true</code> if the text input is successful, <code>false</code> if it fails.
     */
    public boolean inputText(String text) {
        return inputText(text, 0);
    }

    /**
     * Focuses the current element.
     * 
     * @return <code>true</code> if the focusing is successful, <code>false</code> if it fails.
     */
    public boolean focus() {
        innerRevalidation();

        if (!elementSelector.getBooleanValue(CssAttribute.FOCUSABLE)) {
            String message = "Attempting to focus a non-focusable element.";
            LOGGER.error(message);
            throw new InvalidElementActionException(message);
        }
        if (elementSelector.getBooleanValue(CssAttribute.FOCUSED)) {
            return true;
        }

        // The element is already validated if the flag is set, so no need to
        // validate it again.
        tap();

        finalizeUiElementOperation();

        if (revalidate()) {
            if (!elementSelector.getBooleanValue(CssAttribute.FOCUSED)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Simulates long press on the current element with default timeout value.
     * 
     * @return true, if operation is successful, and false otherwise.
     * @see Device#LONG_PRESS_DEFAULT_TIMEOUT
     */
    public boolean longPress() {
        Bounds elementBounds = elementSelector.getBoundsValue(CssAttribute.BOUNDS);
        Point centerPoint = elementBounds.getCenter();
        Point tapPoint = elementBounds.getRelativePoint(centerPoint);

        return longPress(tapPoint, Device.LONG_PRESS_DEFAULT_TIMEOUT);
    }

    /**
     * Simulates long press on the current element with passed timeout value.
     * 
     * @param timeout
     *        - time in ms for which the element should be held.
     * @return true, if operation is successful, and false otherwise.
     */
    public boolean longPress(int timeout) {
        Bounds elementBounds = elementSelector.getBoundsValue(CssAttribute.BOUNDS);
        Point centerPoint = elementBounds.getCenter();
        Point tapPoint = elementBounds.getRelativePoint(centerPoint);

        return longPress(tapPoint, timeout);
    }

    /**
     * Simulates long press on given point inside the current {@link UiElement uielement} for given time.
     * 
     * @param innerPoint
     *        - point, representing the relative coordinates of the point for long press, inside the element's bounds.
     * @param timeout
     *        - time in ms for which the element should be held.
     * @return true, if operation is successful, and false otherwise.
     */
    public boolean longPress(Point innerPoint, int timeout) {
        innerRevalidation();
        Bounds elementBounds = elementSelector.getBoundsValue(CssAttribute.BOUNDS);
        Point longPressPoint = elementBounds.getUpperLeftCorner();
        longPressPoint.addVector(innerPoint);

        if (elementBounds.contains(longPressPoint)) {
            boolean isElementTapped = onDevice.longPress(longPressPoint, timeout);
            finalizeUiElementOperation();
            return isElementTapped;
        } else {
            String message = String.format("Point %s not in element bounds.", innerPoint.toString());
            LOGGER.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Checks if the current element is still valid (on the screen) and updates it's attributes container.
     * 
     * @return true if the current element is still valid, false otherwise.
     */
    public boolean revalidate() {
        if (!isStale) {
            validator.forceRevalidation();
            // if this element is no longer valid, the revalidation procedure
            // will have set it to stale.
        }
        return !isStale;
    }

    /**
     * Should be invoked exactly once in all public methods, whether its directly or indirectly
     */
    private void innerRevalidation() {
        if (validationType == ElementValidationType.ALWAYS) {
            if (isStale) {
                String message = "Element revalidation failed. This element is most likely not present on the screen anymore.";
                LOGGER.error(message);
                throw new StaleElementReferenceException(message);
            }
            revalidateThrowing();
        }
    }

    private void revalidateThrowing() {
        if (revalidate()) {
            String message = "Element revalidation failed. This element is most likely not present on the screen anymore.";
            LOGGER.error(message);
            throw new StaleElementReferenceException(message);
        }
    }

    /**
     * Used by the {@link UiElementValidator} to mark this element as stale.
     */
    void setAsStale() {
        isStale = true;
    }

    private void finalizeUiElementOperation() {
        // Should be invoked exactly once in the end of all element-operating
        // methods, whether its directly or indirectly invoked.
        try {
            Thread.sleep(UI_ELEMENT_OPERATION_WAIT_TIME);
        } catch (InterruptedException e) {
            LOGGER.info(e);
        }
    }

    /**
     * Gets all child {@link UiElement UiElements} that match the passed query.
     * 
     * @param xPathQuery
     *        XPath type node selecting query.
     * @return Returns all the children of the UiElement that matched the xPathQuery
     * @throws UiElementFetchingException
     *         if no appropriate UiElements are found
     * @throws XPathExpressionException
     *         if the passed XPath query is invalid
     * @throws ParserConfigurationException
     *         if an error with internal XPath configuration occurs
     */
    public List<UiElement> getChildren(String xPathQuery)
        throws XPathExpressionException,
            UiElementFetchingException,
            ParserConfigurationException {

        // Creating new Document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document newDocument = builder.newDocument();

        // Importing the node of the UiElement in the new Document
        // We can't use the Node directly a new Node instance is needed
        Node importedNode = newDocument.importNode(representedNodeXPath, true);
        newDocument.appendChild(importedNode);

        // Constructing the UiElements by given xPathNode
        NodeList matchedNodes = UiXmlParser.getXPathNodeChildren(newDocument, xPathQuery);
        List<UiElement> matchedChildrenNodes = new LinkedList<UiElement>();
        for (int i = 0; i < matchedNodes.getLength(); i++) {
            Node childNode = matchedNodes.item(i);
            if (!childNode.isEqualNode(representedNodeXPath)) {
                UiElement returnElement = new UiElement(childNode, onDevice);
                matchedChildrenNodes.add(returnElement);
            }
        }

        if (matchedChildrenNodes == null || matchedChildrenNodes.size() == 0) {
            String message = "No elements found for the XPath expression .";
            LOGGER.error(message);
            throw new UiElementFetchingException(message);
        }

        return matchedChildrenNodes;
    }

    /**
     * Gets all child UiElements that match the given CSS query
     * 
     * @param cssQuery
     *        - a string representing a CSS Query
     * @return Returns all children of the UiElement that match the given CSS query
     * @throws InvalidCssQueryException
     *         if element is searched by invalid CSS query
     * @throws UiElementFetchingException
     *         if element could not be found
     * @throws XPathExpressionException
     *         if element is searched by invalid XPath query
     * @throws ParserConfigurationException
     *         if an error with internal XPath configuration occurs
     */
    public List<UiElement> getChildrenByCssQuery(String cssQuery)
        throws InvalidCssQueryException,
            XPathExpressionException,
            UiElementFetchingException,
            ParserConfigurationException {
        String convertedXPathQuery = CssToXPathConverter.convertCssToXPath(cssQuery);

        return getChildren(convertedXPathQuery);
    }

    /**
     * Gets all child UiElements that match the given {@link UiElementSelector}.
     * 
     * @param childrenSelector
     *        - an object of type {@link UiElementSelector} that needs to match child UI elements
     * @return a list of {@link UiElement} children that match the given selector
     * @throws InvalidCssQueryException
     *         if element is searched by invalid CSS query
     * @throws UiElementFetchingException
     *         if element could not be found
     * @throws XPathExpressionException
     *         if element is searched by invalid XPath query
     * @throws ParserConfigurationException
     *         if an error with internal XPath configuration occurs
     */
    public List<UiElement> getChildrenBySelector(UiElementSelector childrenSelector)
        throws XPathExpressionException,
            InvalidCssQueryException,
            UiElementFetchingException,
            ParserConfigurationException {
        String cssQuery = childrenSelector.buildCssQuery();

        return getChildrenByCssQuery(cssQuery);
    }

    /**
     * Gets all direct children of a {@link UiElement}, represented by XPath node.
     * 
     * @return list, containing all {@link UiElements} that directly ascend the current {@link UiElement}.
     */
    public List<UiElement> getDirectChildren() {
        NodeList nodeChildren = representedNodeXPath.getChildNodes();
        List<UiElement> result = new LinkedList<UiElement>();

        for (int i = 0; i < nodeChildren.getLength(); i++) {
            Node childNode = nodeChildren.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                // our node is 'fake' and it's not used in the screen's xml
                continue;
            }
            UiElement childElement = new UiElement(childNode, onDevice);
            result.add(childElement);
        }

        return result;
    }

    /**
     * Used to get this {@link UiElement} as an image, using the bounds of the element.
     * 
     * @return {@link Image} contained in the element's bounds
     * @throws IOException
     *         - if getting screenshot from the device fails
     */
    public Image getElementImage() throws IOException {
        byte[] imageInByte = onDevice.getScreenshot();
        InputStream inputStream = new ByteArrayInputStream(imageInByte);
        BufferedImage bufferedImage = ImageIO.read(inputStream);

        UiElementSelector elementSelector = getElementSelector();
        Bounds elementBounds = elementSelector.getBoundsValue(CssAttribute.BOUNDS);

        Point upperLeftCorner = elementBounds.getUpperLeftCorner();

        int upperLeftCornerPointX = upperLeftCorner.getX();
        int upperLeftCornerPointY = upperLeftCorner.getY();
        int elementWidth = elementBounds.getWidth();
        int elementHeight = elementBounds.getHeight();
        BufferedImage croppedImage = bufferedImage.getSubimage(upperLeftCornerPointX,
                                                               upperLeftCornerPointY,
                                                               elementWidth,
                                                               elementHeight);
        return new Image(croppedImage);
    }

    /**
     * Checks if this {@link UiElement} has the same properties as the passed one.
     * 
     * @param object
     *        - the {@link UiElement} for comparison
     * 
     * @return <code>true</code>, if this UiElement has the same properties as the passed <code>object</code> and
     *         <code>false</code> if the passed object is not an {@link UiElement} or differs from this
     *         {@link UiElement}
     * @see EqualsBuilder
     */
    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (getClass() != object.getClass()) {
            return false;
        }

        UiElement uiElement = (UiElement) object;

        return new EqualsBuilder().append(elementSelector, uiElement.elementSelector).isEquals();
    }

    /**
     * Returns a hash code for this value.
     * 
     * @return - the hashcode of this object.
     * @see HashCodeBuilder
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(elementSelector).toHashCode();
    }
}
