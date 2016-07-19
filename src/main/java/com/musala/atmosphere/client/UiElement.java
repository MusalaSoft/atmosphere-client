package com.musala.atmosphere.client;

import java.io.IOException;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

import com.musala.atmosphere.client.entity.AccessibilityElementEntity;
import com.musala.atmosphere.client.entity.ImageEntity;
import com.musala.atmosphere.client.exceptions.InvalidCssQueryException;
import com.musala.atmosphere.client.exceptions.MultipleElementsFoundException;
import com.musala.atmosphere.client.exceptions.StaleElementReferenceException;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.beans.SwipeDirection;
import com.musala.atmosphere.commons.exceptions.UiElementFetchingException;
import com.musala.atmosphere.commons.geometry.Bounds;
import com.musala.atmosphere.commons.geometry.Point;
import com.musala.atmosphere.commons.ui.UiElementPropertiesContainer;
import com.musala.atmosphere.commons.ui.selector.CssAttribute;
import com.musala.atmosphere.commons.ui.selector.UiElementSelector;

/**
 * Used to access and manipulate certain views on the testing device, for example tapping, double-taping or holding
 * finger on given widget.
 *
 * @author georgi.gaydarov
 *
 */
public abstract class UiElement {

    private static final long UI_ELEMENT_OPERATION_WAIT_TIME = 500;

    private static final long TIMEOUT_BEFORE_SELECT_ALL = 4000;

    private static final Logger LOGGER = Logger.getLogger(UiElement.class);

    protected UiElementPropertiesContainer propertiesContainer;

    protected ImageEntity imageEntity;

    protected AccessibilityElementEntity elementEntity;

    protected boolean isStale;

    protected DeviceCommunicator communicator;

    // TODO Remove the obsolete constructors when all entities are migrated to the Agent
    @Deprecated
    UiElement(UiElementPropertiesContainer properties,
            ImageEntity imageEntity,
            AccessibilityElementEntity elementEntity) {
        this.propertiesContainer = properties;
        this.imageEntity = imageEntity;
        this.elementEntity = elementEntity;

        isStale = false;
    }

    @Deprecated
    UiElement(UiElement uiElement) {
        this(uiElement.propertiesContainer,
             uiElement.imageEntity,
             uiElement.elementEntity);
    }

    UiElement(UiElementPropertiesContainer properties, DeviceCommunicator communicator) {
        this.propertiesContainer = properties;
        this.communicator = communicator;

        isStale = false;
    }

    UiElement(UiElement uiElement, DeviceCommunicator communicator) {
        this(uiElement.propertiesContainer, communicator);
    }

    /**
     * Gets all child UiElements that match the given {@link UiElementSelector}.
     *
     * @param childrenSelector
     *        - an object of type {@link UiElementSelector} that needs to match child UI elements
     * @return a list of {@link UiElement} children that match the given selector
     * @throws UiElementFetchingException
     *         if no children matching the given selector are found
     */
    public abstract List<UiElement> getChildren(UiElementSelector childrenSelector) throws UiElementFetchingException;

    /**
     * Gets a list with all UI element's children present on the {@link Screen active screen} and matching the given
     * xpath query.
     * <p>
     * <b>Note:</b> Two-word attributes should be written in camelCase. For example content-desc should be contentDesc.
     * </p>
     *
     * @param xpathQuery
     *        - a string representing an XPath query
     * @return list with all UI element's children present on the screen and matching the given xpath query
     * @throws UiElementFetchingException
     *         if no children matching the given xpath query are found
     */
    public abstract List<UiElement> getChildrenByXPath(String xpathQuery) throws UiElementFetchingException;

    /**
     * Gets all direct children of a {@link UiElement}, represented by XPath node.
     *
     * @return list, containing all {@link UiElement} that directly ascend the current {@link UiElement}
     * @throws UiElementFetchingException
     *         if no direct children are found
     */
    public abstract List<UiElement> getDirectChildren() throws UiElementFetchingException;

    /**
     * Gets all direct children of a {@link UiElement} that match the given {@link UiElementSelector}.
     *
     * @param selector
     *        - an UI element selector
     * @return list, containing all {@link UiElement UI elements} matching the {@link UiElementSelector selector} and
     *         directly ascend the current {@link UiElement}
     * @throws UiElementFetchingException
     *         if no direct children matching the given selector are found
     */
    public abstract List<UiElement> getDirectChildren(UiElementSelector selector) throws UiElementFetchingException;

    /**
     * Checks if the current element is still valid (on the screen) and updates it's attributes container. This is
     * executed before each operation that requires the element to be still present on the screen.
     *
     * @return true if the current element is still valid, false otherwise
     */
    public abstract boolean revalidate();

    /**
     * Returns the current UI element's attributes properties container.
     *
     * @return a {@link UiElementPropertiesContainer} instance, containing all properties of this UiElement
     */
    public UiElementPropertiesContainer getProperties() {
        return propertiesContainer;
    }

    /**
     * Simulates tapping on a relative point in the current UI element.
     *
     * @param point
     *        - the relative point that will be added to the upper left corner's coordinates
     * @return <code>true</code> if the tapping is successful, <code>false</code> if it fails
     * @throws StaleElementReferenceException
     *         if the element has become stale before executing this method
     */
    public boolean tap(Point point) {
        revalidateThrowing();
        Bounds elementBounds = propertiesContainer.getBounds();
        Point tapPoint = elementBounds.getUpperLeftCorner();
        tapPoint.addVector(point);

        if (elementBounds.contains(tapPoint)) {
            boolean isElementTapped = (boolean) communicator.sendAction(RoutingAction.GESTURE_TAP, tapPoint);
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
     * @return <code>true</code> if the tapping is successful, <code>false</code> if it fails
     * @throws StaleElementReferenceException
     *         if the element has become stale before executing this method
     */
    public boolean tap() {
        Bounds elementBounds = propertiesContainer.getBounds();
        Point centerPoint = elementBounds.getCenter();
        Point tapPoint = elementBounds.getRelativePoint(centerPoint);

        return tap(tapPoint);
    }

    /**
     * Searches for a child UI element that corresponds to the given {@link UiElementSelector} and taps on it.
     *
     * @param selector
     *        - a {@link UiElementSelector} that needs to match a certain child UI element
     * @return <code>true</code> if the tap on the child UI element was successful,<code>false</code> otherwise
     * @throws StaleElementReferenceException
     *         if the element has become stale before executing this method
     * @throws UiElementFetchingException
     *         if the element does not have any children matching the provided selector
     */
    public boolean tapOnChildElement(UiElementSelector selector) throws UiElementFetchingException {
        List<UiElement> childrenElements = getChildren(selector);

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
     * @return <code>String</code> with the content of the text property of this UiElement
     */
    public String getText() {
        return propertiesContainer.getText();
    }

    /**
     * Simulates holding finger in the center of this UiElement. <i><b>Warning: method not yet implemented!</b></i>
     *
     * @return <code>true</code> if the holding is successful, <code>false</code> if it fails
     */
    public boolean hold() {
        // TODO implement uiElement.hold() and update the java doc
        return false;
    }

    /**
     * Simulates double-tapping in the center of this UiElement.
     *
     * @return <code>true</code> if the double tapping is successful, <code>false</code> if it fails
     * @throws StaleElementReferenceException
     *         if the element has become stale before executing this method
     *
     */
    public boolean doubleTap() {
        Bounds elementBounds = propertiesContainer.getBounds();
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
     * @return <code>true</code> if the double tapping is successful, <code>false</code> if it fails
     * @throws StaleElementReferenceException
     *         if the element has become stale before executing this method
     */
    public boolean doubleTap(Point point) {
        revalidateThrowing();
        Bounds elementBounds = propertiesContainer.getBounds();
        Point tapPoint = elementBounds.getUpperLeftCorner();
        tapPoint.addVector(point);

        if (elementBounds.contains(tapPoint)) {
            boolean isElementTapped = (boolean) communicator.sendAction(RoutingAction.GESTURE_DOUBLE_TAP, tapPoint);
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
     * @return <code>true</code> if the pinch in is successful, <code>false</code> if it fails
     * @throws StaleElementReferenceException
     *         if the element has become stale before executing this method
     */
    public boolean pinchIn() {
        revalidateThrowing();

        Bounds elementBounds = propertiesContainer.getBounds();
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

        boolean result = (boolean) communicator.sendAction(RoutingAction.GESTURE_PINCH_IN, firstFingerInitial, secondFingerInitial);

        return result;
    }

    /**
     * Simulates a pinch out on the element. NOTE emulator devices may not detect pinch gestures on UI elements with
     * size smaller than 100x100dp.
     *
     * @return <code>true</code> if the pinch out is successful, <code>false</code> if it fails
     * @throws StaleElementReferenceException
     *         if the element has become stale before executing this method
     */
    public boolean pinchOut() {
        revalidateThrowing();

        Bounds elementBounds = propertiesContainer.getBounds();
        Point firstFingerEnd = elementBounds.getUpperLeftCorner();
        Point secondFingerEnd = elementBounds.getLowerRightCorner();

        boolean result = (boolean) communicator.sendAction(RoutingAction.GESTURE_PINCH_OUT, firstFingerEnd, secondFingerEnd);
        return result;
    }

    /**
     * Drags the element to the center of an element with given text
     *
     * @param destinationElementText
     *        - the text of the destination element
     * @return <code>true</code> if the dragging is successful, <code>false</code> if it fails
     * @throws XPathExpressionException
     *         when an invalid XPath expression
     * @throws UiElementFetchingException
     *         when cannot fetch UiElement
     * @throws MultipleElementsFoundException
     *         when multiple elements are found
     * @throws InvalidCssQueryException
     *         when an invalid CSS query is used
     */
    public boolean drag(String destinationElementText)
        throws XPathExpressionException,
            UiElementFetchingException,
            MultipleElementsFoundException,
            InvalidCssQueryException {
        UiElementSelector selector = new UiElementSelector();
        selector.addSelectionAttribute(CssAttribute.TEXT, destinationElementText);

        UiElement destinationElement = elementEntity.getElement(selector, true);

        Point startPoint = propertiesContainer.getBounds().getCenter();
        Point endPoint = destinationElement.propertiesContainer.getBounds().getCenter();

        return (boolean) communicator.sendAction(RoutingAction.GESTURE_DRAG, startPoint, endPoint);
    }

    /**
     * Drags the element to the center of an element with given selector.
     *
     * @param destinationSelector
     *        - selector of the destination element
     * @return <code>true</code> if the dragging is successful, <code>false</code> if it fails
     * @throws MultipleElementsFoundException
     *         when selector's query finds multiple elements
     * @throws InvalidCssQueryException
     *         when an invalid CSS query is used
     * @throws UiElementFetchingException
     *         when trying to fetch UiElement but it does not exist on the screen
     * @throws XPathExpressionException
     *         when there is error in the Xpath expression
     */
    public boolean drag(UiElementSelector destinationSelector)
        throws XPathExpressionException,
            UiElementFetchingException,
            InvalidCssQueryException,
            MultipleElementsFoundException {
        UiElement destinationElement = elementEntity.getElement(destinationSelector, true);

        Point startPoint = propertiesContainer.getBounds().getCenter();
        Point endPoint = destinationElement.getProperties().getBounds().getCenter();

        return (boolean) communicator.sendAction(RoutingAction.GESTURE_DRAG, startPoint, endPoint);
    }

    /**
     * Drags the element to the center of another element.
     *
     * @param destinationElement
     *        - destination element
     * @return <code>true</code> if the dragging is successful, <code>false</code> if it fails
     * @throws MultipleElementsFoundException
     *         when selector's query finds multiple elements
     * @throws InvalidCssQueryException
     *         when an invalid CSS query is used
     * @throws UiElementFetchingException
     *         when trying to fetch UiElement but it does not exist on the screen
     */
    public boolean drag(UiElement destinationElement)
        throws UiElementFetchingException,
            InvalidCssQueryException,
            MultipleElementsFoundException {
        Point startPoint = propertiesContainer.getBounds().getCenter();
        Point endPoint = destinationElement.getProperties().getBounds().getCenter();

        return (boolean) communicator.sendAction(RoutingAction.GESTURE_DRAG, startPoint, endPoint);
    }

    /**
     * Drags the element until his center point stands at (Point endPoint) on the screen.
     *
     * @param endPoint
     *        - the point where we want to move the element
     * @return <code>true</code> if the dragging is successful, <code>false</code> if it fails
     */
    public boolean drag(Point endPoint) {
        Point startPoint = propertiesContainer.getBounds().getCenter();
        return (boolean) communicator.sendAction(RoutingAction.GESTURE_DRAG, startPoint, endPoint);
    }

    /**
     * Simulates swiping this UiElement.
     *
     * @param swipeDirection
     *        - a {@link SwipeDirection}, describing the direction of the swipe.
     * @return <code>true</code> if the swiping is successful, <code>false</code> if it fails
     * @throws StaleElementReferenceException
     *         if the element has become stale before executing this method
     */
    public boolean swipe(SwipeDirection swipeDirection) {
        Bounds elementBounds = propertiesContainer.getBounds();
        Point centerPoint = elementBounds.getCenter();

        return swipe(centerPoint, swipeDirection);
    }

    /**
     * Swipes this element in particular direction.
     *
     * @param point
     *        -a {@link Point} the point from which the swipe start
     * @param direction
     *        - a {@link SwipeDirection}, describing the direction of the swipe
     * @return boolean indicating if this action was successful
     * @throws StaleElementReferenceException
     *         if the element has become stale before executing this method
     */
    public boolean swipe(Point point, SwipeDirection direction) {
        revalidateThrowing();
        boolean response = (boolean) communicator.sendAction(RoutingAction.GESTURE_SWIPE, point, direction);
        return response;
    }

    /**
     * Cuts the selected text from this element.
     *
     * @return <code>true</code> if the operation is successful, <code>false</code> if it fails
     * @throws StaleElementReferenceException
     *         if the element has become stale before executing this method
     */
    public boolean cutText() {
        revalidateThrowing();

        return (boolean) communicator.sendAction(RoutingAction.IME_CUT_TEXT);
    }

    /**
     * Copies the current selection of the content in this element.
     *
     * @return <code>true</code> if copy operation is successful, <code>false</code> if it fails
     * @throws StaleElementReferenceException
     *         if the element has become stale before executing this method
     */
    public boolean copyText() {
        revalidateThrowing();

        return (boolean) communicator.sendAction(RoutingAction.IME_COPY_TEXT);
    }

    /**
     * Paste a copied text in this element.
     *
     * @return <code>true</code> if the operation is successful, <code>false</code> if it fails
     * @throws StaleElementReferenceException
     *         if the element has become stale before executing this method
     */
    public boolean pasteText() {
        focus();
        return (boolean) communicator.sendAction(RoutingAction.IME_PASTE_TEXT);
    }

    /**
     * Selects the content of this element.
     *
     * @return <code>true</code> if the text selecting is successful, <code>false</code> if it fails
     * @throws StaleElementReferenceException
     *         if the element has become stale before executing this method
     */
    public boolean selectAllText() {
        boolean isFocused = propertiesContainer.isFocused();
        focus();

        // If the text element is focused just before selecting the text in it, the selecting will fail.
        if (!isFocused) {
            try {
                Thread.sleep(TIMEOUT_BEFORE_SELECT_ALL);
            } catch (InterruptedException e) {
                LOGGER.error("A text selection failed.", e);
            }
        }

        return (boolean) communicator.sendAction(RoutingAction.IME_SELECT_ALL_TEXT);
    }

    /**
     * Clears the contents of this element.
     *
     * @return <code>true</code> if clear text is successful, <code>false</code> if it fails
     * @throws StaleElementReferenceException
     *         if the element has become stale before executing this method
     */
    public boolean clearText() {
        // TODO validate when an element can get it's text cleared
        focus();

        return (boolean) communicator.sendAction(RoutingAction.IME_CLEAR_TEXT);
    }

    /**
     * Inputs text into the UI Element, <b> if it supports text input </b> with interval in milliseconds between the
     * input of each letter.
     *
     * @param text
     *        - text to be input
     * @param intervalInMs
     *        - interval in milliseconds between the input of each letter
     * @return <code>true</code> if the text input is successful, <code>false</code> if it fails
     * @throws StaleElementReferenceException
     *         if the element has become stale before executing this method
     */
    public boolean inputText(String text, long intervalInMs) {
        focus();

        return (boolean) communicator.sendAction(RoutingAction.IME_INPUT_TEXT, text, intervalInMs);
    }

    /**
     * Inputs text into the UI Element <b>if it supports text input</b>.
     *
     * @param text
     *        - text to be input
     * @return <code>true</code> if the text input is successful, <code>false</code> if it fails
     * @throws StaleElementReferenceException
     *         if the element has become stale before executing this method
     */
    public boolean inputText(String text) {
        return inputText(text, 0);
    }

    /**
     * Focuses the current element.
     *
     * @return <code>true</code> if the focusing is successful, <code>false</code> if it fails
     * @throws StaleElementReferenceException
     *         if the element has become stale before executing this method
     */
    public boolean focus() {
        // TODO validate that the element can be focused

        if (propertiesContainer.isFocused()) {
            return true;
        }

        // TODO validate that the element is truly focused
        return tap();
    }

    /**
     * Simulates long press on the current element with default timeout value.
     *
     * @return true, if operation is successful, and false otherwise
     * @throws StaleElementReferenceException
     *         if the element has become stale before executing this method
     * @see Device#LONG_PRESS_DEFAULT_TIMEOUT
     */
    public boolean longPress() {
        Bounds elementBounds = propertiesContainer.getBounds();
        Point centerPoint = elementBounds.getCenter();
        Point tapPoint = elementBounds.getRelativePoint(centerPoint);

        return longPress(tapPoint, Device.LONG_PRESS_DEFAULT_TIMEOUT);
    }

    /**
     * Simulates long press on the current element with passed timeout value.
     *
     * @param timeout
     *        - time in ms for which the element should be held
     * @return true, if operation is successful, and false otherwise
     * @throws StaleElementReferenceException
     *         if the element has become stale before executing this method
     */
    public boolean longPress(int timeout) {
        Bounds elementBounds = propertiesContainer.getBounds();
        Point centerPoint = elementBounds.getCenter();
        Point tapPoint = elementBounds.getRelativePoint(centerPoint);

        return longPress(tapPoint, timeout);
    }

    /**
     * Simulates long press on given point inside the current {@link UiElement uielement} for given time.
     *
     * @param innerPoint
     *        - point, representing the relative coordinates of the point for long press, inside the element's bounds
     * @param timeout
     *        - time in ms for which the element should be held
     * @return true, if operation is successful, and false otherwise
     * @throws StaleElementReferenceException
     *         if the element has become stale before executing this method
     */
    public boolean longPress(Point innerPoint, int timeout) {
        revalidateThrowing();
        Bounds elementBounds = propertiesContainer.getBounds();
        Point longPressPoint = elementBounds.getUpperLeftCorner();
        longPressPoint.addVector(innerPoint);

        if (elementBounds.contains(longPressPoint)) {
            boolean isElementTapped = (boolean) communicator.sendAction(RoutingAction.GESTURE_LONG_PRESS, longPressPoint, timeout);
            finalizeUiElementOperation();
            return isElementTapped;
        } else {
            String message = String.format("Point %s not in element bounds.", innerPoint.toString());
            LOGGER.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Attempts revalidation and if it fails throws exception.
     *
     * @throws StaleElementReferenceException
     *         if the revalidation fails
     */
    protected void revalidateThrowing() {
        if (!revalidate()) {
            String message = "Element revalidation failed. This element is most likely not present on the screen anymore.";
            LOGGER.error(message);
            throw new StaleElementReferenceException(message);
        }
    }

    /**
     * Marks this element as stale.
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
     * Crops this {@link UiElement} as an image, using the bounds of the element.
     *
     * @return {@link Image} contained in the element's bounds
     * @throws IOException
     *         - if getting screenshot from the device fails
     */
    public Image getElementImage() throws IOException {
        return imageEntity.getElementImage(propertiesContainer);
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
        return new EqualsBuilder().append(propertiesContainer, uiElement.propertiesContainer).isEquals();
    }

    /**
     * Returns a hash code for this value.
     *
     * @return - the hashcode of this object
     * @see HashCodeBuilder
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(propertiesContainer).toHashCode();
    }

    /**
     * Gets all child UiElements that match the given CSS query.
     *
     * @param cssQuery
     *        - a string representing a CSS Query
     * @return Returns all children of the UiElement that match the given CSS query
     * @throws UiElementFetchingException
     *         - if failed to fetch an element
     */
    public abstract List<UiElement> getChildrenByCssQuery(String cssQuery) throws UiElementFetchingException;
}
