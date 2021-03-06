// This file is part of the ATMOSPHERE mobile testing framework.
// Copyright (C) 2016 MusalaSoft
//
// ATMOSPHERE is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// ATMOSPHERE is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with ATMOSPHERE.  If not, see <http://www.gnu.org/licenses/>.

package com.musala.atmosphere.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.exceptions.ActionFailedException;
import com.musala.atmosphere.client.exceptions.InvalidCssQueryException;
import com.musala.atmosphere.client.exceptions.MultipleElementsFoundException;
import com.musala.atmosphere.client.uiutils.AccessibilityElementUtils;
import com.musala.atmosphere.client.uiutils.CssToXPathConverter;
import com.musala.atmosphere.client.util.ConfigurationPropertiesLoader;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.exceptions.UiElementFetchingException;
import com.musala.atmosphere.commons.ui.selector.CssAttribute;
import com.musala.atmosphere.commons.ui.selector.UiElementSelectionOption;
import com.musala.atmosphere.commons.ui.selector.UiElementSelector;
import com.musala.atmosphere.commons.ui.tree.AccessibilityElement;

/**
 * Class that holds a device screen information.
 *
 * @author georgi.gaydarov
 *
 */

public class Screen {
    public static final int WAIT_AND_GET_DEFAULT_TIMEOUT = 10000;

    private static final Logger LOGGER = Logger.getLogger(Screen.class.getCanonicalName());

    private static final String TIME_PICKER_WIDGET = "android.widget.TimePicker";

    private static final String DATE_PICKER_WIDGET = "android.widget.DatePicker";

    private static final String PICKERS_MESSAGE = "No %s picker is currently available on the screen.";

    private static final String MULTIPLE_PICKERS_AVAILABLE_MESSAGE = "More than one %s picker is currently available on the screen.";

    private final DeviceCommunicator communicator;

    private final AccessibilityElementUtils elementUtils;

    private WebView webview;

    Screen(DeviceCommunicator communicator) {
        this.communicator = communicator;
        this.elementUtils = new AccessibilityElementUtils(communicator);
    }

    /**
     * Gets a list with all UI elements present on the {@link Screen active screen} and matching the given selector.
     *
     * @param selector
     *        - contains the matching criteria
     * @param visibleOnly
     *        - <code>true</code> to search for visible elements only and <code>false</code> to search all elements
     * @return list with all UI elements present on the screen and matching the given selector
     * @throws UiElementFetchingException
     */
    private List<UiElement> getElements(UiElementSelector selector, Boolean visibleOnly)
        throws UiElementFetchingException {
        return elementUtils.getElements(selector, visibleOnly);
    }

    /**
     * Gets a list with all UI elements present on the {@link Screen active screen} and matching the given selector.
     *
     * @param selector
     *        - contains the matching criteria
     * @return list with all UI elements present on the screen and matching the given selector
     * @throws UiElementFetchingException
     *         if no elements are found
     */
    public List<UiElement> getElements(UiElementSelector selector) throws UiElementFetchingException {
        return getElements(selector, true);
    }

    /**
     * Searches for given UI element in the current screen using CSS.
     * <p>
     * <b>Note:</b> Two-word attributes should be written in camelCase. For example content-desc should be contentDesc.
     * </p>
     *
     * @param query
     *        - CSS selector query.
     * @return the requested {@link UiElement UiElement}.
     * @throws MultipleElementsFoundException
     *         if more than one element is found for the passed query
     * @throws UiElementFetchingException
     *         if no elements are found
     */
    public UiElement getElementByCSS(String query)
        throws InvalidCssQueryException,
            MultipleElementsFoundException,
            UiElementFetchingException {
        String xpathQuery = CssToXPathConverter.convertCssToXPath(query);
        return getElementByXPath(xpathQuery);
    }

    /**
     * Searches for given ScrollableView in the current screen using CSS
     *
     * @param query
     *        CSS selector query
     * @return the requested ScrollableView
     * @throws InvalidCssQueryException
     *         if the passed argument is invalid CSS query
     * @throws UiElementFetchingException
     *         if no elements are found for the passed query
     * @throws MultipleElementsFoundException
     *         if more than one element is found for the passed query
     */
    public ScrollableView getScrollableViewtByCSS(String query)
        throws UiElementFetchingException,
            MultipleElementsFoundException,
            InvalidCssQueryException {
        String xpathQuery = CssToXPathConverter.convertCssToXPath(query);
        return getScrollableViewByXPath(xpathQuery);
    }

    /**
     * Searches for given ScrollableView in the current screen using XPath.
     * <p>
     * <b>Note:</b> Two-word attributes should be written in camelCase. For example content-desc should be contentDesc.
     * </p>
     *
     * @param query
     *        - an XPath query
     * @return the requested ScrollableView
     * @throws UiElementFetchingException
     *         if no elements are found for the passed query
     * @throws MultipleElementsFoundException
     *         if more than one element is found for the passed query
     */
    public ScrollableView getScrollableViewByXPath(String query)
        throws MultipleElementsFoundException,
            UiElementFetchingException {
        return new ScrollableView(getElementByXPath(query), elementUtils, communicator);
    }

    /**
     * Searches for given UI element in the current screen using XPath.
     * <p>
     * <b>Note:</b> Two-word attributes should be written in camelCase. For example content-desc should be contentDesc.
     * </p>
     *
     * @param query
     *        - XPath query by which the element will be selected
     * @return the requested {@link UiElement UiElement}
     * @throws MultipleElementsFoundException
     *         if more than one element is found for the passed query
     * @throws UiElementFetchingException
     *         if no elements are found
     */
    public UiElement getElementByXPath(String query) throws MultipleElementsFoundException, UiElementFetchingException {
        List<UiElement> foundElements = getAllElementsByXPath(query, true);
        int foundElementsCount = foundElements.size();
        if (foundElementsCount > 1) {
            throw new MultipleElementsFoundException(String.format("Searching for a single UiElement but %d that match the given properties %s were found.",
                                                                   foundElementsCount,
                                                                   query));
        } else {
            return foundElements.get(0);
        }
    }

    /**
     * Searches for {@link AccessibilityUiElement UI element} on the active screen that matches the given selector.
     *
     * @param selector
     *        - by which the {@link AccessibilityUiElement UI element} will be searched
     * @param visibleOnly
     *        - <code>true</code> to search for visible elements only and <code>false</code> to search all elements
     * @return the found {@link AccessibilityUiElement UI element}
     * @throws MultipleElementsFoundException
     *         if more than one elements are found matching the given selector
     * @throws UiElementFetchingException
     *         if no element was found matching the given selector
     */
    public UiElement getElement(UiElementSelector selector, Boolean visibleOnly)
        throws MultipleElementsFoundException,
            UiElementFetchingException {
        return elementUtils.getElement(selector, visibleOnly);
    }

    /**
     * Gets a list with all UI elements present on the {@link Screen active screen} and matching the given xpath query.
     * <p>
     * <b>Note:</b> Two-word attributes should be written in camelCase. For example content-desc should be contentDesc.
     * </p>
     *
     * @param xpathQuery
     *        - contains the matching criteria
     * @param visibleOnly
     *        - <code>true</code> to search for visible elements only and <code>false</code> to search all elements
     * @return list with all UI elements present on the screen and matching the given selector
     * @throws UiElementFetchingException
     *         if no elements are found
     */
    @SuppressWarnings("unchecked")
    private List<UiElement> getAllElementsByXPath(String xpathQuery, boolean visibleOnly)
        throws UiElementFetchingException {
        List<AccessibilityElement> foundElements = (List<AccessibilityElement>) communicator.sendAction(RoutingAction.EXECUTE_XPATH_QUERY,
                                                                                                        xpathQuery,
                                                                                                        visibleOnly);
        if (foundElements.isEmpty()) {
            throw new UiElementFetchingException("No elements found matching the given selector.");
        }

        List<UiElement> uiElements = new ArrayList<>();
        for (AccessibilityElement element : foundElements) {
            uiElements.add(new AccessibilityUiElement(element,
                                                      elementUtils,
                                                      communicator));
        }

        return uiElements;
    }

    /**
     * Searches for {@link AccessibilityUiElement UI element} on the active screen that matches the given selector.
     * Searches only visible elements.
     *
     * @param selector
     *        - by which the {@link AccessibilityUiElement UI element} will be searched
     * @return the found {@link AccessibilityUiElement UI element}
     * @throws MultipleElementsFoundException
     *         if more than one elements are found matching the given selector
     * @throws UiElementFetchingException
     *         if no element was found matching the given selector
     */
    public UiElement getElement(UiElementSelector selector)
        throws MultipleElementsFoundException,
            UiElementFetchingException {
        return elementUtils.getElement(selector, true);
    }

    /**
     * Searches for given ScrollableView in the current screen using a {@link UiElementSelector UiElementSelector}
     * instance.
     *
     * @param selector
     *        - {@link UiElementSelector} object that contains all the selection criteria for the required elements.
     * @return the requested {@link ScrollableView ScrollableView}
     * @throws UiElementFetchingException
     *         if no elements are found for the passed query
     * @throws MultipleElementsFoundException
     *         if more than one element is found for the passed query
     */
    public ScrollableView getScrollableView(UiElementSelector selector)
        throws MultipleElementsFoundException,
            UiElementFetchingException {
        // There is a problem with the ScrollableViews created from ListView elements. They can scroll but
        // cannot find inner elements. Make sure we throw an exception if such ScrollableView is created.
        String className = getElement(selector).getProperties().getClassName();
        if (className.endsWith("ListView")) {
            String message = "Creating a ScrollableView from a ListView element is not supported. "
                    + "Instead, please select the first parent element of the ListView, which has a resource id.";
            throw new ActionFailedException(message);
        }

        return new ScrollableView(getElement(selector), elementUtils, communicator);
    }

    /**
     * Waits for an element matching the given selector to appear with a given timeout. If the element appears on
     * screen, returns it.
     *
     * @param selector
     *        - an {@link UiElementSelector} describing the desired element
     * @param waitTimeout
     *        - a timeout for the wait operation
     * @return an {@link UiElement} matching the passed selector
     * @throws UiElementFetchingException
     *         if no elements are found for the passed query
     * @throws MultipleElementsFoundException
     *         if more than one element is found for the passed query
     */
    public UiElement getElementWhenPresent(UiElementSelector selector, int waitTimeout)
        throws MultipleElementsFoundException,
            UiElementFetchingException {
        boolean isElementPresent = waitForElementExists(selector, waitTimeout);

        if (isElementPresent) {
            UiElement selectedElement = getElement(selector);
            return selectedElement;
        } else {
            String message = "Waiting for an element matching the selector timed out, but still no such element was present.";
            LOGGER.error(message);
            throw new UiElementFetchingException(message);
        }
    }

    /**
     * Waits for an element matching the given selector to appear on screen. If the element appears on screen, returns
     * it. Uses the value specified in {@link #WAIT_AND_GET_DEFAULT_TIMEOUT} as a timeout for the wait operation.
     *
     * @param selector
     *        - an {@link UiElementSelector} describing the desired element
     * @return an {@link UiElement} matching the passed selector
     * @throws UiElementFetchingException
     *         if no elements are found for the passed query
     * @throws MultipleElementsFoundException
     *         if more than one element is found for the passed query
     */
    public UiElement getElementWhenPresent(UiElementSelector selector)
        throws MultipleElementsFoundException,
            UiElementFetchingException {
        return getElementWhenPresent(selector, WAIT_AND_GET_DEFAULT_TIMEOUT);
    }

    /**
     * Searches for UI elements in the current screen using given CSS. Returns a list of all found elements having the
     * used CSS.
     * <p>
     * <b>Note:</b> Two-word attributes should be written in camelCase. For example content-desc should be contentDesc.
     * </p>
     *
     * @param cssQuery
     *        - CSS selector query.
     * @return List containing all found elements of type {@link UiElement UiElement}.
     * @throws InvalidCssQueryException
     *         if the passed argument is invalid CSS query
     * @throws UiElementFetchingException
     *         if no elements are found for the passed query
     */
    public List<UiElement> getAllElementsByCSS(String cssQuery)
        throws InvalidCssQueryException,
            UiElementFetchingException {
        String xPathQuery = CssToXPathConverter.convertCssToXPath(cssQuery);

        return getAllElementsByXPath(xPathQuery);
    }

    /**
     * Searches for UI elements in the current screen using given XPath. Returns a list of all found elements having the
     * used XPath.
     * <p>
     * <b>Note:</b> Two-word attributes should be written in camelCase. For example content-desc should be contentDesc.
     * </p>
     *
     * @param xPathQuery
     *        - an XPath query that should match the elements
     * @return List containing all found elements of type {@link UiElement UiElement}.
     * @throws UiElementFetchingException
     *         if no elements are found for the passed query
     */
    public List<UiElement> getAllElementsByXPath(String xPathQuery) throws UiElementFetchingException {
        // TODO The query should start with "//*".
        return getAllElementsByXPath(xPathQuery, true);
    }

    /**
     * Tap on first found {@link UiElement UiElement}, displaying exactly the supplied search text.
     *
     * @param text
     *        - search text.
     * @return <code>true</code> if the tapping of element is successful, <code>false</code> if it fails.
     * @throws UiElementFetchingException
     *         if no elements or more than 1 are found for the passed query
     */
    public boolean tapElementWithText(String text) throws UiElementFetchingException, InvalidCssQueryException {
        return tapElementWithText(text, 0);
    }

    /**
     * Tap on {@link UiElement}, displaying exactly the supplied search text.
     *
     * @param text
     *        - search text.
     * @param match
     *        - determines which element to tap if multiple matches exist; zero based index.
     * @return <code>true</code> if the tapping of element is successful, <code>false</code> if it fails.
     * @throws UiElementFetchingException
     *         if no elements or more than 1 are found for the passed query
     */
    public boolean tapElementWithText(String text, int match) throws UiElementFetchingException {
        UiElementSelector selector = new UiElementSelector();
        selector.addSelectionAttribute(CssAttribute.TEXT, UiElementSelectionOption.EQUALS, text);
        List<UiElement> elementList = getElements(selector);
        int listSize = elementList.size();
        if (listSize <= match) {
            String message = String.format("Tapping match with index %s requested, but only %s elements matching the criteria found.",
                                           match,
                                           listSize);
            LOGGER.error(message);
            throw new UiElementFetchingException(message);
        }
        UiElement element = elementList.get(match);
        return element.tap();
    }

    /**
     * Check existence of element, displaying exactly the supplied search text.
     *
     * @param text
     *        - search text.
     * @return - true if element with supplied search text exists on screen.
     */
    public boolean hasElementWithText(String text) {
        UiElementSelector selector = new UiElementSelector();
        selector.addSelectionAttribute(CssAttribute.TEXT, UiElementSelectionOption.EQUALS, text);

        try {
            List<UiElement> elementList = getElements(selector);
            return elementList.size() > 0;
        } catch (UiElementFetchingException e) {
            return false;
        }
    }

    /**
     * Gets an instance for android TimePicker widgets.
     *
     * @return a {@link TimePicker} object representing the only time picker widget on screen.
     * @throws UiElementFetchingException
     *         if no active time pickers are found in the screen
     * @throws MultipleElementsFoundException
     *         if there are more than one time pickers
     */
    public TimePicker getTimePicker() throws MultipleElementsFoundException, UiElementFetchingException {
        String message = String.format(PICKERS_MESSAGE, "time");
        UiElementSelector timePickerSelector = new UiElementSelector();
        timePickerSelector.addSelectionAttribute(CssAttribute.CLASS_NAME, TIME_PICKER_WIDGET);

        try {
            getElement(timePickerSelector);
        } catch (MultipleElementsFoundException e) {
            message = String.format(MULTIPLE_PICKERS_AVAILABLE_MESSAGE, "time");
            LOGGER.error(message, e);
            throw new MultipleElementsFoundException(message, e);
        }

        return new TimePicker(this);
    }

    /**
     * Gets the date picker currently on screen.
     *
     * @return a {@link DatePicker} object representing the time picker widget on screen.
     * @throws UiElementFetchingException
     *         if there is no date picker available on the screen
     * @throws MultipleElementsFoundException
     *         if multiple date pickers are present on the screen
     */
    public DatePicker getDatePicker() throws UiElementFetchingException, MultipleElementsFoundException {
        String message = String.format(PICKERS_MESSAGE, "date");
        UiElementSelector timePickerSelector = new UiElementSelector();
        timePickerSelector.addSelectionAttribute(CssAttribute.CLASS_NAME, DATE_PICKER_WIDGET);

        try {
            getElement(timePickerSelector);
        } catch (MultipleElementsFoundException e) {
            message = String.format(MULTIPLE_PICKERS_AVAILABLE_MESSAGE, "date");
            LOGGER.error(message, e);
            throw new MultipleElementsFoundException();
        }

        return new DatePicker(this);
    }

    /**
     * Waits for the existence of a given UiElement with a given timeout.
     *
     * @param selector
     *        - the selector of the given UI element.
     * @param timeout
     *        - the given timeout.
     * @return boolean indicating if this action was successful.
     */
    public boolean waitForElementExists(UiElementSelector selector, Integer timeout) {
        return elementUtils.waitForElementExists(selector, timeout);
    }

    /**
     * Waits until a given UiElement disappears with a given timeout.
     *
     * @param selector
     *        - the selector of the given UI element.
     * @param timeout
     *        - the given timeout.
     * @return boolean indicating if this action was successful.
     */
    public boolean waitUntilElementGone(UiElementSelector selector, Integer timeout) {
        return elementUtils.waitUntilElementGone(selector, timeout);
    }

    /**
     * Waits for a window content update event to occur. If a package name for the window is specified, but the current
     * window does not have the same package name, the function returns immediately. The behavior of this method depends
     * on the application that it is used on.
     *
     * @param packageName
     *        - the specified window package name (can be null). If null, a window update from any front-end window will
     *        end the wait
     * @param timeout
     *        - the timeout of the operation
     * @return <code>true</code> if a window update occurred, <code>false</code> if timeout has elapsed or if the
     *         current window does not have the specified package name
     */
    public boolean waitForWindowUpdate(String packageName, int timeout) {
        return elementUtils.waitForWindowUpdate(packageName, timeout);
    }

    /**
     * Gets the present {@link WebView} on the active screen.
     *
     * @param packageName
     *        - package of the application from which the {@link WebView} will be selected
     * @return the present {@link WebView} on the active screen
     */
    public WebView getWebView(String packageName) {
        communicator.sendAction(RoutingAction.GET_WEB_VIEW, packageName);

        int implicitWaitTimeout = ConfigurationPropertiesLoader.getImplicitWaitTimeout();
        if (implicitWaitTimeout != 0) {
            communicator.sendAction(RoutingAction.SET_WEB_VIEW_IMPLICIT_WAIT, implicitWaitTimeout);
        }

        webview = new WebView(communicator);

        return webview;
    }

    boolean hasWebView() {
        return webview != null;
    }
}
