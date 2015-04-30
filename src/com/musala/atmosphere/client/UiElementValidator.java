package com.musala.atmosphere.client;

import java.util.LinkedList;
import java.util.List;

import com.musala.atmosphere.commons.ui.selector.CssAttribute;
import com.musala.atmosphere.commons.ui.selector.UiElementSelectionOption;
import com.musala.atmosphere.commons.ui.selector.UiElementSelector;

/**
 * Class responsible for all {@link UiElement} instances revalidation.
 * 
 * @author georgi.gaydarov
 * 
 */
class UiElementValidator {
    private final List<XmlNodeUiElement> elements = new LinkedList<XmlNodeUiElement>();

    private Screen currentScreen;

    /**
     * Adds an element for automatic validation.
     * 
     * @param element
     *        - the element to be added.
     */
    public void addElementForValidation(XmlNodeUiElement element) {
        elements.add(element);
    }

    /**
     * Sets the new active screen. Invoked by the {@link Screen} itself on construction.
     * 
     * @param currentScreen
     */
    void setActiveScreen(Screen currentScreen) {
        this.currentScreen = currentScreen;
        validateElements();
    }

    /**
     * Forces a screen refetching (which results in all elements being validated).
     */
    public void forceRevalidation() {
        currentScreen.updateScreen();
    }

    private void validateElements() {
        List<XmlNodeUiElement> invalidElements = new LinkedList<XmlNodeUiElement>();
        for (XmlNodeUiElement element : elements) {
            boolean valid = validateElement(element);
            if (!valid) {
                invalidElements.add(element);
            }
        }
        for (UiElement invalidElement : invalidElements) {
            elements.remove(invalidElement);
        }
    }

    /**
     * Validates an {@link XmlNodeUiElement} instance (checks if it is currently on the newest fetched screen).
     * 
     * @param element
     *        - the element to be validated
     * @return boolean indicating if the element is valid
     */
    public boolean validateElement(XmlNodeUiElement element) {
        if (!elements.contains(element)) {
            return false;
        }

        UiElementSelector elementSelector = element.getElementSelector();
        // FIXME workaround of the inability to match new lines before text in a jsoup query
        String elementText = elementSelector.getText();

        if (elementText != null && elementText.length() > 0 && Character.isWhitespace(elementText.charAt(0))) {
            elementSelector.addSelectionAttribute(CssAttribute.TEXT, UiElementSelectionOption.CONTAINS, elementText);
        }

        String elementQuery = elementSelector.buildCssQuery();
        boolean present = currentScreen.containsElementByCSS(elementQuery);
        if (present) {
            return true;
        } else {
            element.setAsStale();
            return false;
        }
    }
}
