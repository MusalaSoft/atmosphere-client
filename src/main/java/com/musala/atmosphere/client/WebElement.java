package com.musala.atmosphere.client;

import java.util.ArrayList;
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
    protected DeviceCommunicator deviceCommunicator;

    protected String xpathQuery;

    WebElement(DeviceCommunicator communicator) {
        this.deviceCommunicator = communicator;
        xpathQuery = "";
    }

    /**
     * Finds {@link UiWebElement} within the current {@link WebElement} by the given {@link WebElementSelectionCriterion
     * selection criterion} and the corresponding value.
     *
     * @param selectionCriterion
     *        - a {@link WebElementSelectionCriterion selection criterion}
     * @param criterionValue
     *        - a {@link WebElementSelectionCriterion selection criterion value}
     * @return the wanted element
     * @throws InvalidCssQueryException
     *         if {@link WebElementSelectionCriterion selection criterion} is set to CSS_SELECTOR and the query is
     *         invalid
     */
    @SuppressWarnings("unchecked")
    public UiWebElement findElement(WebElementSelectionCriterion selectionCriterion, String criterionValue) {
        String xpathCriterionValue = WebElementSelectionCriterionConverter.convertToXpathQuery(selectionCriterion,
                                                                                               criterionValue);
        String findElementQuery = this.xpathQuery + xpathCriterionValue;
        Map<String, Object> attributes = (Map<String, Object>) deviceCommunicator.sendAction(RoutingAction.FIND_WEB_ELEMENT,
                                                                                             findElementQuery);
        return new UiWebElement(deviceCommunicator, attributes, findElementQuery);
    }

    /**
     * Finds {@link UiWebElement UiWebElements} within the current {@link WebElement} by the given
     * {@link WebElementSelectionCriterion selection criterion} and the corresponding value.
     *
     * @param selectionCriterion
     *        - a {@link WebElementSelectionCriterion selection criterion}
     * @param criterionValue
     *        - a {@link WebElementSelectionCriterion selection criterion value}
     * @return the wanted element
     * @throws InvalidCssQueryException
     *         if {@link WebElementSelectionCriterion selection criterion} is set to CSS_SELECTOR and the query is
     *         invalid
     */
    @SuppressWarnings("unchecked")
    public List<UiWebElement> findElements(WebElementSelectionCriterion selectionCriterion, String criterionValue) {
        List<UiWebElement> webElements = new ArrayList<>();
        String xpathCriterionValue = WebElementSelectionCriterionConverter.convertToXpathQuery(selectionCriterion,
                                                                                               criterionValue);
        String findElementQuery = this.xpathQuery + xpathCriterionValue;
        List<Map<String, Object>> attributesList = (List<Map<String, Object>>) deviceCommunicator.sendAction(RoutingAction.FIND_WEB_ELEMENTS,
                                                                                                             findElementQuery);

        int index = 1;
        for (Map<String, Object> elementAttributes : attributesList) {
            String elementXpathCriterionValue = WebElementSelectionCriterionConverter.convertToXpathQuery(selectionCriterion,
                                                                                                          criterionValue,
                                                                                                          index);
            String findSingleElementQuery = this.xpathQuery + elementXpathCriterionValue;
            UiWebElement webElement = new UiWebElement(deviceCommunicator,
                                                       elementAttributes,
                                                       findSingleElementQuery);
            webElements.add(webElement);
            index++;
        }

        return webElements;
    }
}
