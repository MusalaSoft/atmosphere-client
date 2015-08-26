package com.musala.atmosphere.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.musala.atmosphere.client.util.webview.WebElementSelectionCriterionConverter;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.webelement.selection.WebElementSelectionCriterion;

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

    WebView(Device device) {
        super(device);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UiWebElement> findElements(WebElementSelectionCriterion selectionCriterion, String criterionValue) {
        List<UiWebElement> webElements = new ArrayList<UiWebElement>();
        List<Map<String, Object>> attributesList = (List<Map<String, Object>>) deviceCommunicator.sendAction(RoutingAction.FIND_WEB_ELEMENTS,
                                                                                                             selectionCriterion,
                                                                                                             criterionValue);

        int index = 1;
        for (Map<String, Object> elementAttributes : attributesList) {
            String xpathCriterionValue = WebElementSelectionCriterionConverter.convertToXpathQuery(selectionCriterion,
                                                                                                   criterionValue,
                                                                                                   index);
            UiWebElement webElement = new UiWebElement(device,
                                                       elementAttributes,
                                                       WebElementSelectionCriterion.XPATH,
                                                       xpathCriterionValue);
            webElements.add(webElement);
            index++;
        }

        return webElements;
    }
}
