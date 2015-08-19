package com.musala.atmosphere.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public UiWebElement findElement(WebElementSelectionCriterion selectionCriterion, String criterionValue) {
        Map<String, Object> attributes = (Map<String, Object>) deviceCommunicator.sendAction(RoutingAction.FIND_WEB_ELEMENT,
                                                                                             selectionCriterion,
                                                                                             criterionValue);
        return new UiWebElement(device, attributes, selectionCriterion, criterionValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UiWebElement> findElements(WebElementSelectionCriterion selectionCriterion, String criterionValue) {
        List<UiWebElement> webElements = new ArrayList<UiWebElement>();
        List<Map<String, Object>> attributesList = (List<Map<String, Object>>) deviceCommunicator.sendAction(RoutingAction.FIND_WEB_ELEMENTS,
                                                                                                             selectionCriterion,
                                                                                                             criterionValue);

        for (Map<String, Object> elementAttributes : attributesList) {
            webElements.add(new UiWebElement(device, elementAttributes, selectionCriterion, criterionValue));
        }

        return webElements;
    }
}
