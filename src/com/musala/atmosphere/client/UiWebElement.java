package com.musala.atmosphere.client;

import java.util.List;
import java.util.Map;

import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.geometry.Point;
import com.musala.atmosphere.commons.util.Pair;
import com.musala.atmosphere.commons.webelement.actions.WebElementAction;
import com.musala.atmosphere.commons.webelement.selection.WebElementSelectionCriterion;

/**
 * Represents element in the {@link WebView} containing all possible interaction functionality we can execute on it.
 * 
 * @author denis.bialev
 *
 */
public class UiWebElement extends WebElement {

    private Map<String, Object> elementProperties;

    private WebElementSelectionCriterion selectionCriterion;

    private String criterionValue;

    UiWebElement(Device device,
            Map<String, Object> elementProperties,
            WebElementSelectionCriterion selectionCriterion,
            String criterionValue) {
        super(device);
        this.elementProperties = elementProperties;
        this.selectionCriterion = selectionCriterion;
        this.criterionValue = criterionValue;
    }

    /**
     * Taps on the element. If this causes a new page to load, this method will attempt to block until the page has
     * loaded.
     */
    public void tap() {
        revalidate();
        deviceCommunicator.sendAction(RoutingAction.WEB_ELEMENT_ACTION,
                                      WebElementAction.TAP,
                                      selectionCriterion,
                                      criterionValue);
    }

    /**
     * If this element is a text entry element, this will clear the value. Has no effect on other elements. Text entry
     * elements are INPUT and TEXTAREA elements. Note that the events fired by this event may not be as you'd expect. In
     * particular, we don't fire any keyboard or mouse events. If you want to ensure keyboard events are fired, consider
     * using something like inputText(String) with the backspace key. To ensure you get a change event, consider
     * following with a call to inputText(String) with the tab key.
     */
    public boolean clearText() {
        revalidate();
        focus();
        device.selectAllText();
        return device.clearText();
    }

    /**
     * Gets the upper left corner location of the element relative to the {@link WebView} that contains the element.
     * 
     * @return {@link Point} representing the relative position of the element
     */
    public Point getRelativePosition() {
        return (Point) deviceCommunicator.sendAction(RoutingAction.WEB_ELEMENT_ACTION,
                                                     WebElementAction.GET_POSITION,
                                                     selectionCriterion,
                                                     criterionValue);
    }

    /**
     * Gets width and height of the element.
     * 
     * @return {@link Pair} containing the size of the element
     */
    @SuppressWarnings("unchecked")
    public Pair<Integer, Integer> getSize() {
        return (Pair<Integer, Integer>) deviceCommunicator.sendAction(RoutingAction.WEB_ELEMENT_ACTION,
                                                                      WebElementAction.GET_SIZE,
                                                                      selectionCriterion,
                                                                      criterionValue);
    }

    /**
     * Inputs text into the Web Element <b>if it supports text input</b>.
     *
     * @param text
     *        - text to input
     * @return <code>true</code> if the text input is successful, <code>false</code> if it fails
     */
    public boolean inputText(String text) {
        return inputText(text, 0);
    }

    /**
     * Inputs text into the Web Element, <b> if it supports text input </b> with interval in milliseconds between the
     * input of each symbol.
     *
     * @param text
     *        - text to input
     * @param interval
     *        - interval in milliseconds between the input of each symbol
     * @return <code>true</code> if the text input is successful, <code>false</code> if it fails
     */
    public boolean inputText(String text, long interval) {
        revalidate();
        focus();

        return device.inputText(text, interval);
    }

    /**
     * If the current element is a form, or an element within a form, then the element will be submitted to the remote
     * server. If this causes the current page to change, then this method will block until the new page is loaded.
     */
    public void submitForm() {
        // TODO Implement the method
    }

    /**
     * Get the value of a the given attribute of the element. Will return the current value, even if this has been
     * modified after the page has been loaded. More exactly, this method will return the value of the given attribute,
     * unless that attribute is not present, in which case the value of the property with the same name is returned (for
     * example for the "value" property of a textarea element). If neither value is set, null is returned. The "style"
     * attribute is converted as best can be to a text representation with a trailing semi-colon. The following are
     * deemed to be "boolean" attributes, and will return either "true" or null: async, autofocus, autoplay, checked,
     * compact, complete, controls, declare, defaultchecked, defaultselected, defer, disabled, draggable, ended,
     * formnovalidate, hidden, indeterminate, iscontenteditable, ismap, itemscope, loop, multiple, muted, nohref,
     * noresize, noshade, novalidate, nowrap, open, paused, pubdate, readonly, required, reversed, scoped, seamless,
     * seeking, selected, spellcheck, truespeed, willvalidate Finally, the following commonly mis-capitalized
     * attribute/property names are evaluated as expected: "class" "readonly"
     * 
     * @param attributeKey
     *        - the name of the attribute
     * @return the attribute/property's current value or <code>null</code> if the value is not set or the key is not
     *         present
     */
    public Object getAttribute(String attributeKey) {
        revalidate();
        return elementProperties.get(attributeKey);
    }

    /**
     * Get the values of all attributes of the element.
     * 
     * @return Map containing the element attributes and their values
     */
    public Map<String, Object> getAttributes() {
        revalidate();
        return elementProperties;
    }

    /**
     * Is the element currently enabled or not? This will generally return true for everything but disabled input
     * elements.
     * 
     * @return <code>true</code> if the element is enabled, <code>false</code> otherwise
     */
    public boolean isEnabled() {
        revalidate();
        return !elementProperties.containsKey("disabled");
    }

    /**
     * Determines whether or not this element is selected or not. This operation only applies to input elements such as
     * check boxes, options in a select and radio buttons.
     * 
     * @return <code>true</code> if the element is selected, <code>false</code> otherwise
     */
    public boolean isSelected() {
        return (boolean) deviceCommunicator.sendAction(RoutingAction.WEB_ELEMENT_ACTION,
                                                       WebElementAction.IS_SELECTED,
                                                       selectionCriterion,
                                                       criterionValue);
    }

    /**
     * Checks if the element is disabled.
     * 
     * @return <code>true</code> if the element is disabled, <code>false</code> otherwise
     */
    public boolean isDisplayed() {
        return (boolean) deviceCommunicator.sendAction(RoutingAction.WEB_ELEMENT_ACTION,
                                                       WebElementAction.IS_DISPLAYED,
                                                       selectionCriterion,
                                                       criterionValue);
    }

    /**
     * Get the value of a given CSS property. Color values should be returned as rgba strings, so, for example if the
     * "background-color" property is set as "green" in the HTML source, the returned value will be "rgba(0, 255, 0, 1)"
     * . Note that shorthand CSS properties (e.g. background, font, border, border-top, margin, margin-top, padding,
     * padding-top, list-style, outline, pause, cue) are not returned, in accordance with the DOM CSS2 specification -
     * you should directly access the longhand properties (e.g. background-color) to access the desired values.
     * 
     * @param key
     *        - by which the property will be selected
     * @return the wanted property
     */
    public String getCssValue(String key) {
        // TODO Implement the method
        return null;
    }

    /**
     * Get the tag name of this element. Not the value of the name attribute: will return "input" for the element
     * <input name="foo" />.
     * 
     * @return the tag name of the element
     */
    public String getTagName() {
        return (String) deviceCommunicator.sendAction(RoutingAction.WEB_ELEMENT_ACTION,
                                                      WebElementAction.GET_TAG_NAME,
                                                      selectionCriterion,
                                                      criterionValue);
    }

    /**
     * Get the visible (i.e. not hidden by CSS) innerText of this element, including sub-elements, without any leading
     * or trailing whitespace.
     * 
     * @return the innerText of this element
     */
    public String getText() {
        return (String) deviceCommunicator.sendAction(RoutingAction.WEB_ELEMENT_ACTION,
                                                      WebElementAction.GET_TEXT,
                                                      selectionCriterion,
                                                      criterionValue);
    }

    @Override
    public UiWebElement findElement(WebElementSelectionCriterion selectionCriterion, String criterionValue) {
        // TODO Implement the method
        return null;
    }

    @Override
    public List<UiWebElement> findElements(WebElementSelectionCriterion selectionCriterion, String criterionValue) {
        // TODO Implement the method
        return null;
    }

    /**
     * Updates the attributes container of the element.
     */
    private void revalidate() {
        elementProperties = (Map<String, Object>) deviceCommunicator.sendAction(RoutingAction.FIND_WEB_ELEMENT,
                                                                                selectionCriterion,
                                                                                criterionValue);
    }

    /**
     * Focuses the current web element.
     */
    private void focus() {
        deviceCommunicator.sendAction(RoutingAction.WEB_ELEMENT_ACTION,
                                      WebElementAction.FOCUS,
                                      selectionCriterion,
                                      criterionValue);
    }
}
