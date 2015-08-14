package com.musala.atmosphere.client;

import java.util.List;

/**
 * Contains the common logic between {@link UiWebElement} and WebView.
 * 
 * @author denis.bialev
 *
 */
public abstract class WebElement {
    /**
     * Finds {@link UiWebElement} within the current {@link WebElement} by the given selector.
     * 
     * @return the wanted element
     */
    UiWebElement findElement(/* Selector */) {
        // TODO Implement the method
        return null;
    }

    /**
     * Finds {@link UiWebElement UiWebElements} within the current {@link WebElement} by the given selector.
     * 
     * @return the wanted element
     */
    List<UiWebElement> findElements() {
        // TODO Implement the method
        return null;
    }
}
