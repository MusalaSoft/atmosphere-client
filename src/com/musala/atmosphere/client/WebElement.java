package com.musala.atmosphere.client;

import java.util.List;
import java.util.Map;

import com.musala.atmosphere.client.exceptions.InvalidCssQueryException;
import com.musala.atmosphere.client.util.webview.WebElementSelectionCriterionConverter;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.webelement.selection.WebElementSelectionCriterion;

/**
 * Contains the common logic between {@link UiWebElement} and WebView.
 * 
 * @author denis.bialev
 *
 */
public abstract class WebElement {
    protected Device device;

    protected DeviceCommunicator deviceCommunicator;

    protected WebElementSelectionCriterion selectionCriterion;

    protected String criterionValue;

    WebElement(Device device) {
        this.device = device;
        this.deviceCommunicator = device.getCommunicator();
        selectionCriterion = WebElementSelectionCriterion.XPATH;
        criterionValue = "";
    }

    /**
     * Finds {@link UiWebElement} within the current {@link WebElement} by the given {@link WebElementSelectionCriterion
     * selection criterion} and the corresponding value.
     * 
     * @return the wanted element
     * @throws InvalidCssQueryException
     *         if {@link WebElementSelectionCriterion selection criterion} is set to CSS_SELECTOR and the query is
     *         invalid
     */
    @SuppressWarnings("unchecked")
    public UiWebElement findElement(WebElementSelectionCriterion selectionCriterion, String criterionValue) {
        String xpathCriterionValue = WebElementSelectionCriterionConverter.convertToXpathQuery(selectionCriterion,
                                                                                               criterionValue);
        String findElementQuery = this.criterionValue + xpathCriterionValue;
        Map<String, Object> attributes = (Map<String, Object>) deviceCommunicator.sendAction(RoutingAction.FIND_WEB_ELEMENT,
                                                                                             this.selectionCriterion,
                                                                                             findElementQuery);
        return new UiWebElement(device, attributes, this.selectionCriterion, findElementQuery);
    }

    /**
     * Finds {@link UiWebElement UiWebElements} within the current {@link WebElement} by the given
     * {@link WebElementSelectionCriterion selection criterion} and the corresponding value.
     * 
     * @return the wanted element
     * @throws InvalidCssQueryException
     */
    public abstract List<UiWebElement> findElements(WebElementSelectionCriterion selectionCriterion,
                                                    String criterionValue);
}
