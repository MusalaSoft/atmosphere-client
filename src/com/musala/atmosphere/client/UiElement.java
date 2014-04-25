package com.musala.atmosphere.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.musala.atmosphere.client.exceptions.InvalidElementActionException;
import com.musala.atmosphere.client.exceptions.StaleElementReferenceException;
import com.musala.atmosphere.client.geometry.Bounds;
import com.musala.atmosphere.client.geometry.Point;
import com.musala.atmosphere.client.uiutils.CssAttribute;
import com.musala.atmosphere.client.uiutils.UiElementAttributeExtractor;
import com.musala.atmosphere.client.uiutils.UiElementSelector;
import com.musala.atmosphere.client.uiutils.UiXmlParser;
import com.musala.atmosphere.client.util.settings.ElementValidationType;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.beans.SwipeDirection;
import com.musala.atmosphere.commons.ui.UiElementDescriptor;

/**
 * Used to access and manipulate certain views on the testing device, for example tapping, double-taping or holding
 * finger on given widget.
 * 
 * @author georgi.gaydarov
 * 
 */
public class UiElement {
    private enum ElementNodeType {
        XPATH_NODE,
        JSOUP_NODE;
    }

    private ElementNodeType underlyingNodeType;

    private Node representedNodeXPath;

    private org.jsoup.nodes.Node representedNodeJSoup;

    private UiElementSelector elementSelector;

    private Device onDevice;

    private DeviceCommunicator communicator;

    private ElementValidationType validationType;

    private static final Logger LOGGER = Logger.getLogger(UiElement.class);

    private static final long UI_ELEMENT_OPERATION_WAIT_TIME = 500;

    private UiElementValidator validator = new UiElementValidator();

    private boolean isStale = false;

    private UiElement(Map<String, String> nodeAttributesMap, Device onDevice) {
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
    UiElement(Node representingNode, Device onDevice) {
        this(UiXmlParser.getAttributeMapOfNode(representingNode), onDevice);
        underlyingNodeType = ElementNodeType.XPATH_NODE;
        representedNodeXPath = representingNode;
    }

    /**
     * Constructor for element creation via a JSoup query.
     * 
     * @param representingNode
     * @param onDevice
     */
    UiElement(org.jsoup.nodes.Node representingNode, Device onDevice) {
        this(UiXmlParser.getAttributeMapOfNode(representingNode), onDevice);
        underlyingNodeType = ElementNodeType.JSOUP_NODE;
        representedNodeJSoup = representingNode;
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
     * @return - an {@link ElementValidationType} instance.
     */
    public ElementValidationType getValidationType() {
        return validationType;
    }

    /**
     * Returns the current UI element's attributes data container.
     * 
     * @return a {@link UiElementSelector} instance.
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
            throw new IllegalArgumentException("Point " + point + " not in element bounds.");
        }
    }

    /**
     * Simulates tapping on the current UI Element.
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
     * Used to get list of children of the given UI Element. Works only for ViewGroups.
     * 
     * @return List with all the UI elements that inherit from this UI element or empty List if such don't exist.
     */
    public List<UiElement> getChildren() {
        List<UiElement> result;
        if (underlyingNodeType == ElementNodeType.XPATH_NODE) {
            result = getChildrenForXPath();
        } else {
            result = getChildrenForJSoup();
        }
        return result;
    }

    private List<UiElement> getChildrenForXPath() {
        NodeList nodeChildren = representedNodeXPath.getChildNodes();
        List<UiElement> result = new LinkedList<UiElement>();

        for (int i = 0; i < nodeChildren.getLength(); i++) {
            Node childNode = nodeChildren.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            UiElement childElement = new UiElement(childNode, onDevice);
            result.add(childElement);
        }

        return result;
    }

    private List<UiElement> getChildrenForJSoup() {
        List<org.jsoup.nodes.Node> nodeChildren = representedNodeJSoup.childNodes();
        List<UiElement> result = new LinkedList<UiElement>();

        for (org.jsoup.nodes.Node childNode : nodeChildren) {
            // This is a workaround to check if the child node is an element
            if (childNode.attr("bounds") == null) {
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
    public boolean hold() {
        // TODO implement uiElement.hold()
        return false;
    }

    /**
     * Simulates double-tapping on the given UI element.
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
     * Simulates double-tapping on a point in the given UI element.
     * 
     * @param point
     *        - the point to be tapped
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
            throw new IllegalArgumentException("Point " + point + " not in element bounds.");
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
     * Simulates dragging the UI widget until his ( which corner exactly? ) upper-left corner stands at position
     * (toX,toY) on the screen.
     * 
     * @param toX
     * @param toY
     * @return <code>true</code> if the dragging is successful, <code>false</code> if it fails.
     */
    public boolean drag(int toX, int toY) {
        // TODO implement uiElement.drag()
        return false;
    }

    /**
     * Simulates swiping.
     * 
     * @param swipeDirection
     *        - a {@link SwipeDirection} enum instance, describing the direction this element should be swiped in.
     * @return <code>true</code> if the swiping is successful, <code>false</code> if it fails.
     */
    public boolean swipe(SwipeDirection swipeDirection) {
        Bounds elementBounds = elementSelector.getBoundsValue(CssAttribute.BOUNDS);
        Point centerPoint = elementBounds.getCenter();

        return swipe(centerPoint, swipeDirection);
    }

    /**
     * Swipes this element in a given direction.
     * 
     * @param direction
     *        - a {@link SwipeDirection} enum instance, describing the direction this element should be swiped in.
     * @param point
     *        -a {@link Point} the point from which the swipe start.
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
     */
    public boolean clearText() {
        // TODO validate when an element can get it's text cleared
        // if (!elementSelector.getBooleanValue(CssAttribute.))
        // {
        // throw new InvalidElementActionException("Cannot to clear a non-clickable element.");
        // }
        // FIXME fix this method when the UIAutomator internal exception problem is solved.

        innerRevalidation();
        UiElementDescriptor descriptor = UiElementAttributeExtractor.extract(elementSelector);
        Object response = communicator.sendAction(RoutingAction.CLEAR_FIELD, descriptor);

        finalizeUiElementOperation();

        return response == DeviceCommunicator.VOID_SUCCESS;
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
            throw new InvalidElementActionException("Attempting to focus a non-focusable element.");
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
        if (isStale) {
            throw new StaleElementReferenceException("Element revalidation failed. This element is most likely not present on the screen anymore.");
        }

        if (validationType == ElementValidationType.ALWAYS) {
            revalidateThrowing();
        }
    }

    private void revalidateThrowing() {
        if (revalidate()) {
            throw new StaleElementReferenceException("Element revalidation failed. This element is most likely not present on the screen anymore.");
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
}
