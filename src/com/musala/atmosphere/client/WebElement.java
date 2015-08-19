package com.musala.atmosphere.client;

import java.util.List;

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

    WebElement(Device device) {
        this.device = device;
        this.deviceCommunicator = device.getCommunicator();
    }

    /**
     * Finds {@link UiWebElement} within the current {@link WebElement} by the given
     * {@link WebElementSelectionCriterion selection criterion} and the corresponding value.
     * 
     * @return the wanted element
     */
    public abstract UiWebElement findElement(WebElementSelectionCriterion selectionCriterion, String criterionValue);

    /**
     * Finds {@link UiWebElement UiWebElements} within the current {@link WebElement} by the given
     * {@link WebElementSelectionCriterion selection criterion} and the corresponding value.
     * 
     * @return the wanted element
     */
    public abstract List<UiWebElement> findElements(WebElementSelectionCriterion selectionCriterion,
                                                    String criterionValue);
}
