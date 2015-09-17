package com.musala.atmosphere.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.musala.atmosphere.client.entity.GestureEntity;
import com.musala.atmosphere.client.exceptions.InvalidCssQueryException;
import com.musala.atmosphere.client.exceptions.MultipleElementsFoundException;
import com.musala.atmosphere.client.uiutils.CssToXPathConverter;
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

    private String screenXml;

    private final Device onDevice;

    private GestureEntity gestureEntity;

    private Document xPathDomDocument;

    private org.jsoup.nodes.Document jSoupDocument;

    private final DeviceCommunicator communicator;

    @Deprecated
    Screen(Device onDevice, GestureEntity gestureEntity, String uiHierarchyXml) {
        this.onDevice = onDevice;
        this.gestureEntity = gestureEntity;
        communicator = onDevice.getCommunicator();
        screenXml = uiHierarchyXml;

        // XPath DOM Document building
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            xPathDomDocument = documentBuilder.parse(new InputSource(new StringReader(screenXml)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            String message = "XPath XML to DOM Document parsing failed.";
            LOGGER.warn(message, e);
        }

        // JSoup Document building
        jSoupDocument = Jsoup.parse(screenXml);
    }

    Screen(Device onDevice, GestureEntity gestureEntity) {
        this.onDevice = onDevice;
        this.gestureEntity = gestureEntity;
        communicator = onDevice.getCommunicator();
    }

    /**
     * Updates the current {@link Screen} instance to contain the newest possible device screen information. Equivalent
     * to reinvoking the {@link Device#getActiveScreen()} method.
     */
    @Deprecated
    public void updateScreen() {
        // This will be removed.
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
    @SuppressWarnings("unchecked")
    private List<UiElement> getElements(UiElementSelector selector, Boolean visibleOnly)
        throws UiElementFetchingException {
        List<AccessibilityElement> foundElements = (List<AccessibilityElement>) communicator.sendAction(RoutingAction.GET_UI_ELEMENTS,
                                                                                                        selector,
                                                                                                        visibleOnly);
        if (foundElements.isEmpty()) {
            throw new UiElementFetchingException("No elements found matching the given selector.");
        }

        List<UiElement> uiElements = new ArrayList<UiElement>();
        for (AccessibilityElement element : foundElements) {
            uiElements.add(new AccessibilityUiElement(element, onDevice, gestureEntity));
        }

        return uiElements;
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
     * Saves the underlying device UI XML into a file.
     * 
     * @param path
     *        - full path to file in which the UI XML should be saved.
     * @throws FileNotFoundException
     *         when the passed argument does not denote already existing and writable file or such can not be created
     *         for some reason
     */
    @Deprecated
    public void exportToXml(String path) throws FileNotFoundException {
        // FIXME this implementation is not valid anymore. UiAutomator should be used here.
        PrintStream export = new PrintStream(path);
        export.print(screenXml);
        export.close();
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
        return new ScrollableView(getElementByXPath(query));
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
        List<UiElement> foundElements = getElements(selector, visibleOnly);

        int foundElementsCount = foundElements.size();
        if (foundElementsCount > 1) {
            throw new MultipleElementsFoundException(String.format("Searching for a single UiElement but %d that match the given properties %s were found.",
                                                                   foundElementsCount,
                                                                   selector));
        } else {
            return getElements(selector, true).get(0);
        }
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

        List<UiElement> uiElements = new ArrayList<UiElement>();
        for (AccessibilityElement element : foundElements) {
            uiElements.add(new AccessibilityUiElement(element, onDevice, gestureEntity));
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
        return getElement(selector, true);
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
        return new ScrollableView(getElement(selector));
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
            updateScreen();
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
     * @return List containing all found elements of type {@link XmlNodeUiElement XmlNodeUiElement}.
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
     * @return List containing all found elements of type {@link XmlNodeUiElement XmlNodeUiElement}.
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
     * 
     * @param timeout
     *        - the given timeout.
     * 
     * @return boolean indicating if this action was successful.
     */
    public boolean waitForElementExists(UiElementSelector selector, Integer timeout) {
        boolean response = (boolean) communicator.sendAction(RoutingAction.WAIT_FOR_EXISTS, selector, timeout);
        updateScreen();
        return response;
    }

    /**
     * Waits until a given UiElement disappears with a given timeout.
     * 
     * @param selector
     *        - the selector of the given UI element.
     * 
     * @param timeout
     *        - the given timeout.
     * 
     * @return boolean indicating if this action was successful.
     */
    public boolean waitUntilElementGone(UiElementSelector selector, Integer timeout) {
        boolean response = (boolean) communicator.sendAction(RoutingAction.WAIT_UNTIL_GONE, selector, timeout);
        updateScreen();
        return response;
    }

    /**
     * Waits for a window content update event to occur. If a package name for the window is specified, but the current
     * window does not have the same package name, the function returns immediately.
     * 
     * @param packageName
     *        - the specified window package name (can be null). If null, a window update from any front-end window will
     *        end the wait
     * @param timeout
     *        - the timeout of the operation
     * @return <code>true</code> if a window update occurred, <code>false</code> if timeout has elapsed or if the
     *         current window does not have the specified package name
     * @Note The behavior of this method depends on the application that it is used on.
     */
    public boolean waitForWindowUpdate(String packageName, int timeout) {
        boolean response = (boolean) communicator.sendAction(RoutingAction.WAIT_FOR_WINDOW_UPDATE,
                                                             packageName,
                                                             timeout);
        updateScreen();
        return response;
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
        return new WebView(onDevice);
    }
}
