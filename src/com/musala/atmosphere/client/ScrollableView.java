package com.musala.atmosphere.client;

import org.w3c.dom.Node;

import com.musala.atmosphere.client.uiutils.CssAttribute;
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

    ScrollableView(org.jsoup.nodes.Node representingNode, Device onDevice) {
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
        return scrollToPosition(maxSwipes, steps, ScrollDirection.SCROLL_TO_END);
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
        return scrollToPosition(maxSwipes, steps, ScrollDirection.SCROLL_TO_BEGINNING);
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
        return scrollToPosition(maxSwipes, 0, ScrollDirection.SCROLL_TO_BEGINNING);
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
        return scrollToPosition(maxSwipes, 0, ScrollDirection.SCROLL_TO_END);
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
        return scrollToPosition(0, steps, ScrollDirection.SCROLL_BACKWARD);
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
        return scrollToPosition(0, steps, ScrollDirection.SCROLL_FORWARD);
    }

    /**
     * Perform a scroll backward. If this view is set to vertical then the scroll will be executed from bottom to top.
     * If this view is set to horizontal then the scroll will be executed from right to left. A default value for the
     * steps count will be used.
     * 
     * @return true if scrolled and false if can't scroll anymore
     */
    public boolean scrollBackward() {
        return scrollToPosition(0, 0, ScrollDirection.SCROLL_BACKWARD);
    }

    /**
     * Perform a scroll forward. If this view is set to vertical then the scroll will be executed from top to bottom. If
     * this view is set to horizontal then the scroll will be executed from left to right. A default value for the steps
     * count will be used.
     * 
     * @return true if scrolled and false if can't scroll anymore
     */
    public boolean scrollForward() {
        return scrollToPosition(0, 0, ScrollDirection.SCROLL_FORWARD);
    }

    /**
     * Performs a swipe up on the UI element until the requested text is visible or until swipe attempts have been
     * exhausted.
     * 
     * @param text
     *        text to search
     * @return true if the text is found else false
     */
    public boolean scrollTextIntoView(String text) {
        UiElementSelector textViewSelector = new UiElementSelector();
        textViewSelector.addSelectionAttribute(CssAttribute.TEXT, text);

        return scrollIntoView(textViewSelector);
    }

    /**
     * Perform a scroll search for a UI element matching the {@link UiElementSelector} innerViewselector argument
     * 
     * @param innerViewSelector
     *        UI selector representing the view into which will be scrolled
     * @return true if item is found else false
     */
    public boolean scrollIntoView(UiElementSelector innerViewSelector) {
        // FIXME current implementation invokes scrollIntoView from UiScrollable in uiautomator.
        // Think of a better solution, because in case of a view with horizontal orientation that method has a strange
        // behavior, when the orientation is vertical the the element seemed to be found but false is returned.
        UiElementDescriptor viewDescriptor = UiElementAttributeExtractor.extract(elementSelector);

        UiElementDescriptor innerViewDescriptor = UiElementAttributeExtractor.extract(innerViewSelector);

        boolean response = (boolean) communicator.sendAction(RoutingAction.SCROLL_INTO_VIEW,
                                                             viewDescriptor,
                                                             innerViewDescriptor,
                                                             isVertical);
        return response;

    }

    private boolean scrollToPosition(Integer maxSwipes, Integer maxSteps, ScrollDirection scrollAction) {
        UiElementDescriptor viewDescriptor = UiElementAttributeExtractor.extract(elementSelector);
        boolean response = (boolean) communicator.sendAction(RoutingAction.SCROLL_TO_DIRECTION,
                                                             scrollAction,
                                                             viewDescriptor,
                                                             maxSwipes,
                                                             maxSteps,
                                                             isVertical);

        return response;
    }

}
