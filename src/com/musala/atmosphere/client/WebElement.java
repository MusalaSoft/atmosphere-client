package com.musala.atmosphere.client;

import java.util.List;

import com.musala.atmosphere.commons.ui.selector.WebElementSelectionCriterion;

/**
 * Contains the common logic between {@link UiWebElement} and WebView.
 * 
 * @author denis.bialev
 *
 */
public abstract class WebElement {
    /**
     * Finds {@link UiWebElement} within the current {@link WebElement} by the given
     * {@link WebElementSelectionCriterion selection criterion} and the corresponding value.
     * 
     * @return the wanted element
     */
    UiWebElement findElement(WebElementSelectionCriterion selectionCriterion, String criterionValue) {
        // TODO Implement the method
        return null;
    }

    /**
     * Finds {@link UiWebElement UiWebElements} within the current {@link WebElement} by the given
     * {@link WebElementSelectionCriterion selection criterion} and the corresponding value.
     * 
     * @return the wanted element
     */
    List<UiWebElement> findElements(WebElementSelectionCriterion selectionCriterion, String criterionValue) {
        // TODO Implement the method
        return null;
    }
}
