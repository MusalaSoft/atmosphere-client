package com.musala.atmosphere.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

import com.musala.atmosphere.client.exceptions.StaleElementReferenceException;
import com.musala.atmosphere.commons.beans.SwipeDirection;
import com.musala.atmosphere.commons.exceptions.UiElementFetchingException;
import com.musala.atmosphere.commons.geometry.Bounds;
import com.musala.atmosphere.commons.geometry.Point;
import com.musala.atmosphere.commons.ui.UiElementPropertiesContainer;
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

    private static final long TIMEOUT_BEFORE_SELECT_ALL = 3000;

    private static final Logger LOGGER = Logger.getLogger(UiElement.class);

    protected UiElementPropertiesContainer propertiesContainer;

    protected Device onDevice;

    protected DeviceCommunicator communicator;

    protected boolean isStale;

    protected UiElement(UiElementPropertiesContainer properties, Device device) {
        propertiesContainer = properties;
        onDevice = device;
        communicator = device.getCommunicator();
        isStale = false;
    }

    /**
     * Gets all child UiElements that match the given {@link UiElementSelector}.
     * 
     * @param childrenSelector
     *        - an object of type {@link UiElementSelector} that needs to match child UI elements
     * @return a list of {@link UiElement} children that match the given selector
     */
    public abstract List<UiElement> getChildren(UiElementSelector childrenSelector);

    /**
     * Gets all direct children of a {@link UiElement}, represented by XPath node.
     * 
     * @return list, containing all {@link UiElements} that directly ascend the current {@link UiElement}
     */
    public abstract List<UiElement> getDirectChildren();

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

        boolean result = onDevice.pinchIn(firstFingerInitial, secondFingerInitial);

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
     * @return <code>true</code> if the dragging is successful, <code>false</code> if it fails
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
        boolean response = onDevice.swipe(point, direction);
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

        return onDevice.cutText();
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

        return onDevice.copyText();
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
        return onDevice.pasteText();
    }

    /**
     * Selects the content of this element.
     * 
     * @return <code>true</code> if the text selecting is successful, <code>false</code> if it fails
     * @throws StaleElementReferenceException
     *         if the element has become stale before executing this method
     */
    public boolean selectAllText() {
        focus();

        try {
            Thread.sleep(TIMEOUT_BEFORE_SELECT_ALL);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return onDevice.selectAllText();
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

        return onDevice.clearText();
    }

    /**
     * Inputs text into the UI Element, <b> if it supports text input </b> with interval in milliseconds between the
     * input of each letter.
     * 
     * @param text
     *        - text to be input
     * @param intervalInMs
     *        - interval in milliseconds between the input of each letter
     * @param revalidateElement
     *        - boolean indicating if the element should be revalidated prior to text inputting
     * @return <code>true</code> if the text input is successful, <code>false</code> if it fails
     * @throws StaleElementReferenceException
     *         if the element has become stale before executing this method
     */
    public boolean inputText(String text, long intervalInMs) {
        focus();

        boolean success = onDevice.inputText(text, intervalInMs);
        return success;
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

        Bounds elementBounds = propertiesContainer.getBounds();

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
}
