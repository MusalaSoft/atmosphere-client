package com.musala.atmosphere.client;

import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

import com.musala.atmosphere.client.exceptions.InvalidCssQueryException;
import com.musala.atmosphere.client.exceptions.UiElementFetchingException;
import com.musala.atmosphere.client.uiutils.UiElementAttributeExtractor;
import com.musala.atmosphere.client.uiutils.UiElementSelector;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.ScrollDirection;
import com.musala.atmosphere.commons.ui.UiElementDescriptor;

/**
 * UI element representing ScrollableView used to perform scroll actions to end, beginning, forward, backward, to a
 * certain text or into inner view.
 * 
 * @author filareta.yordanova
 */
public class ScrollableView extends UiCollection {

    /**
     * Used to determine scroll direction
     */
    private boolean isVertical = true;

    ScrollableView(Node representingNode, Device onDevice) {
        super(representingNode, onDevice);
    }

    ScrollableView(UiElement uiElement) {
        super(uiElement);
    }

    /**
     * Sets the view orientation as horizontal to perform left, right, beginning and end scrolling.
     */
    public void setAsHorizontalScrollableView() {
        isVertical = false;
    }

    /**
     * Sets the view orientation as vertical to perform up, down, beginning and end scrolling.
     */
    public void setAsVerticalScrollableView() {
        isVertical = true;
    }

    /**
     * Scrolls to the end of a scrollable UI element. The end could be the bottom most in case of vertical controls or
     * the right most for horizontal controls.
     * 
     * @param steps
     *        used to control the speed
     * @param maxSwipes
     *        maximum swipes to perform a scroll action
     * @return true on scrolled else false
     */
    public boolean scrollToEnd(Integer maxSwipes, Integer steps) {
        return scroll(maxSwipes, steps, ScrollDirection.SCROLL_TO_END);
    }

    /**
     * Scrolls to the beginning of a scrollable UI element. The beginning could be the top most in case of vertical
     * controls or the left most for horizontal controls.
     * 
     * @param steps
     *        used to control the speed
     * @param maxSwipes
     *        maximum swipes to perform a scroll action
     * @return true on scrolled else false
     */
    public boolean scrollToBeginning(Integer maxSwipes, Integer steps) {
        return scroll(maxSwipes, steps, ScrollDirection.SCROLL_TO_BEGINNING);
    }

    // TODO: Use optional null parameter instead of 0, when the class is created.

    /**
     * Scrolls to the beginning of a scrollable UI element. The beginning could be the top most in case of vertical
     * controls or the left most for horizontal controls. A default value for the steps count will be used.
     * 
     * @param maxSwipes
     *        maximum swipes to perform a scroll action
     * @return true on scrolled else false
     */
    public boolean scrollToBeginning(Integer maxSwipes) {
        return scroll(maxSwipes, 0, ScrollDirection.SCROLL_TO_BEGINNING);
    }

    /**
     * Scrolls to the end of a scrollable UI element. The end could be the bottom most in case of vertical controls or
     * the right most for horizontal controls. A default value for the steps count will be used.
     * 
     * @param maxSwipes
     *        maximum swipes to perform a scroll action
     * @return true on scrolled else false
     */
    public boolean scrollToEnd(Integer maxSwipes) {
        return scroll(maxSwipes, 0, ScrollDirection.SCROLL_TO_END);
    }

    /**
     * Perform a scroll backward. If this view is set to vertical then the scroll will be executed from bottom to top.
     * If this view is set to horizontal then the scroll will be executed from right to left.
     * 
     * @param steps
     *        used to control the speed
     * @return true if scrolled and false if can't scroll anymore
     */
    public boolean scrollBackward(int steps) {
        return scroll(0, steps, ScrollDirection.SCROLL_BACKWARD);
    }

    /**
     * Perform a scroll forward. If this view is set to vertical then the scroll will be executed from top to bottom. If
     * this view is set to horizontal then the scroll will be executed from left to right.
     * 
     * @param steps
     *        used to control the speed
     * @return true if scrolled and false if can't scroll anymore
     */
    public boolean scrollForward(int steps) {
        return scroll(0, steps, ScrollDirection.SCROLL_FORWARD);
    }

    /**
     * Perform a scroll backward. If this view is set to vertical then the scroll will be executed from bottom to top.
     * If this view is set to horizontal then the scroll will be executed from right to left. A default value for the
     * steps count will be used.
     * 
     * @return true if scrolled and false if can't scroll anymore
     */
    public boolean scrollBackward() {
        return scroll(0, 0, ScrollDirection.SCROLL_BACKWARD);
    }

    /**
     * Perform a scroll forward. If this view is set to vertical then the scroll will be executed from top to bottom. If
     * this view is set to horizontal then the scroll will be executed from left to right. A default value for the steps
     * count will be used.
     * 
     * @return true if scrolled and false if can't scroll anymore
     */
    public boolean scrollForward() {
        return scroll(0, 0, ScrollDirection.SCROLL_FORWARD);
    }

    /**
     * Method that scrolls in the given direction
     * 
     * @param maxSwipes
     *        - maximum number of swipes to perform a scroll action
     * @param maxSteps
     *        - maximum number of steps, used to control the speed
     * @param scrollDirection
     * @return true if the scroll was successful, false if it was not
     */
    private boolean scroll(Integer maxSwipes, Integer maxSteps, ScrollDirection scrollDirection) {
        UiElementDescriptor viewDescriptor = UiElementAttributeExtractor.extract(elementSelector);
        boolean response = (boolean) communicator.sendAction(RoutingAction.SCROLL_TO_DIRECTION,
                                                             scrollDirection,
                                                             viewDescriptor,
                                                             maxSwipes,
                                                             maxSteps,
                                                             isVertical);

        return response;
    }

    /**
     * Finds a specific element in ScrollableView corresponding to the given selector using scrolling if necessary
     * 
     * @param maxSwipes
     *        - the maximum number of swipes to perform a scroll action
     * @param innerViewSelector
     *        - the corresponding selector
     * @return true if the method finds the element corresponding to the selector, false if it does not find it
     * @throws XPathExpressionException
     * @throws InvalidCssQueryException
     * @throws ParserConfigurationException
     */
    public boolean scrollToElementBySelector(Integer maxSwipes, UiElementSelector innerViewSelector)
        throws XPathExpressionException,
            InvalidCssQueryException,
            ParserConfigurationException {
        String cssQuery = innerViewSelector.buildCssQuery();
        Screen deviceActiveScreen = onDevice.getActiveScreen();
        UiElementSelector scrollableViewSelector = this.getElementSelector();
        ScrollableView updatedScrollableView = null;

        this.scrollToBeginning(maxSwipes);

        for (int i = 0; i < maxSwipes; i++) {
            try {
                deviceActiveScreen.updateScreen();
                updatedScrollableView = deviceActiveScreen.getScrollableView(scrollableViewSelector);
                updatedScrollableView.getChildrenByCssQuery(cssQuery);

                return true;
            } catch (UiElementFetchingException expection) {
                if (!this.scrollForward()) {
                    return false;
                }
            }
        }

        return false;
    }

    /**
     * Tries to find an UI element in the view without the usage of scroll and taps on it.
     * 
     * @param innerViewSelector
     *        - the corresponding selector
     * @return true if you can find and tap on an element in ScrollableView corresponding to the given selector without
     *         scrolling, false if you cannot
     * @throws XPathExpressionException
     * @throws InvalidCssQueryException
     * @throws UiElementFetchingException
     * @throws ParserConfigurationException
     */
    public boolean tapElementBySelectorWithoutScrolling(UiElementSelector innerViewSelector)
        throws XPathExpressionException,
            InvalidCssQueryException,
            UiElementFetchingException,
            ParserConfigurationException {
        String cssQuery = innerViewSelector.buildCssQuery();

        try {
            List<UiElement> innerViewChildren = this.getChildrenByCssQuery(cssQuery);

            return innerViewChildren.get(0).tap();
        } catch (UiElementFetchingException e) {
            return false;
        }
    }

    /**
     * Tries to find an UI element in the view with the usage of scroll and taps on it.
     * 
     * @param innerViewSelector
     *        - the corresponding selector
     * @return true if you can find and tap on an element in ScrollableView corresponding to the given selector with
     *         scrolling, false if you cannot
     * @throws XPathExpressionException
     * @throws InvalidCssQueryException
     * @throws ParserConfigurationException
     * @throws UiElementFetchingException
     */
    public boolean tapElementBySelectorWithScrolling(Integer maxSwipes, UiElementSelector innerViewSelector)
        throws XPathExpressionException,
            InvalidCssQueryException,
            ParserConfigurationException,
            UiElementFetchingException {
        if (!scrollToElementBySelector(maxSwipes, innerViewSelector)) {
            return false;
        }

        Screen deviceActiveScreen = onDevice.getActiveScreen();
        String cssQuery = innerViewSelector.buildCssQuery();
        UiElementSelector scrollableViewSelector = this.getElementSelector();
        ScrollableView updatedScrollableView = deviceActiveScreen.getScrollableView(scrollableViewSelector);
        List<UiElement> innerViewChildren = updatedScrollableView.getChildrenByCssQuery(cssQuery);

        return innerViewChildren.get(0).tap();
    }
}
