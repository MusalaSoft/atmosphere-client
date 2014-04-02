package com.musala.atmosphere.client;

import java.util.LinkedList;
import java.util.List;

/**
 * Class responsible for all {@link UiElement} instances revalidation.
 * 
 * @author georgi.gaydarov
 * 
 */
class UiElementValidator {
    private final List<UiElement> elements = new LinkedList<UiElement>();

    private Screen currentScreen;

    /**
     * Adds an element for automatic validation.
     * 
     * @param element
     *        - the element to be added.
     */
    public void addElementForValidation(UiElement element) {
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
        List<UiElement> invalidElements = new LinkedList<UiElement>();
        for (UiElement element : elements) {
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
     * Validates an {@link UiElement} instance (checks if it is currently on the newest fetched screen).
     * 
     * @param element
     *        - the element to be validated.
     * @return boolean indicating if the element is valid.
     */
    public boolean validateElement(UiElement element) {
        if (!elements.contains(element)) {
            return false;
        }

        String elementQuery = element.getElementSelector().buildCssQuery();
        boolean present = currentScreen.containsElementByCSS(elementQuery);
        if (present) {
            return true;
        } else {
            element.setAsStale();
            return false;
        }
    }

}
