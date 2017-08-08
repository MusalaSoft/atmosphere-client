package com.musala.atmosphere.client;

import java.util.Set;

import com.musala.atmosphere.client.util.webview.WebElementSelectionCriterionConverter;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.webelement.action.WebElementWaitCondition;
import com.musala.atmosphere.commons.webelement.selection.WebElementSelectionCriterion;
import com.musala.atmosphere.commons.webview.selection.WebViewSelectionCriterion;

/**
 * Class that holds information and provides interface for interaction with a WebView present on the {@link Screen
 * screen}.
 *
 * @author filareta.yordanova
 *
 */
public class WebView extends WebElement {
    // TODO: Some additional fields and information might be kept in the class after the prototype supports more than
    // one WebView visible on the screen.

    WebView(DeviceCommunicator communicator) {
        super(communicator);
    }

    /**
     * Waits for the existence of a given {@link UiWebElement} with a given timeout.
     *
     * @param selectionCriterion
     *        - {@link WebElementSelectionCriterion} by which the element will be selected
     * @param criterionValue
     *        - value of the criterion
     * @param timeout
     *        - the given timeout in milliseconds
     * @return <code>true</code> if the element is present on the screen, before the given timeout, <code>false</code>
     *         otherwise
     */
    public boolean waitForElementExists(WebElementSelectionCriterion selectionCriterion,
                                        String criterionValue,
                                        int timeout) {
        String xpathQuery = WebElementSelectionCriterionConverter.convertToXpathQuery(selectionCriterion,
                                                                                      criterionValue);
        return (boolean) deviceCommunicator.sendAction(RoutingAction.WAIT_FOR_WEB_ELEMENT,
                                                       xpathQuery,
                                                       WebElementWaitCondition.ELEMENT_EXISTS,
                                                       timeout);
    }

    /**
     * Gets a set all web view window handlers.
     *
     * @return a set of window handlers
     */
    @SuppressWarnings("unchecked")
    public Set<String> getWindowHandlers() {
        Set<String> windowHandlers = (Set<String>) deviceCommunicator.sendAction(RoutingAction.GET_WEB_VIEWS);

        return windowHandlers;
    }

    /**
     * Switches the WebDriver to another web view window by a child element selector.
     *
     * @param selectionCriterion
     *        - a child web element selection criterion
     * @param criterionValue
     *        - a criterion value
     */
    public void switchToAnotherWebViewByChildWebElement(WebElementSelectionCriterion selectionCriterion,
                                                        String criterionValue) {
        String xpathQuery = WebElementSelectionCriterionConverter.convertToXpathQuery(selectionCriterion,
                                                                                      criterionValue);
        deviceCommunicator.sendAction(RoutingAction.SWITCH_TO_WEBVIEW_BY_CHILD, xpathQuery);
    }

    /**
     * Switches the WebDriver to another web view window by WebView selection criterion and value.
     *
     * @param selectionCriterion
     *        - a criterion used for the web view selection
     * @param criterionValue
     *        - the value of the criterion
     */
    public void switchToAnotherWebView(WebViewSelectionCriterion selectionCriterion, String criterionValue) {
        deviceCommunicator.sendAction(RoutingAction.SWITCH_TO_WEBVIEW, selectionCriterion, criterionValue);
    }

    /**
     * Gets the URL of the current web view.
     *
     * @return the URL of the current web view
     */
    public String getUrl() {
        String webViewUrl = (String) deviceCommunicator.sendAction(RoutingAction.GET_WEBVIEW_URL);

        return webViewUrl;
    }

    /**
     * Gets the title attribute from the current web view.
     *
     * @return a title
     */
    public String getTitle() {
        String webViewTitle = (String) deviceCommunicator.sendAction(RoutingAction.GET_WEBVIEW_TITLE);

        return webViewTitle;
    }

}
