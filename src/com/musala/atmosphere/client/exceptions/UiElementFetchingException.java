package com.musala.atmosphere.client.exceptions;

import com.musala.atmosphere.client.UiElement;

/**
 * Thrown when a method that attempts to fetch a {@link UiElement UiElement} does not succeed for some reason like the
 * wanted {@link UiElement UiElement} does not exist on the current screen, the given xPath or Css are invalid or there
 * are two or more {@link UiElement UiElement} with the wanted characteristic.
 * 
 * @author georgi.gaydarov
 * 
 */
public class UiElementFetchingException extends Exception {
    /**
     * Auto generated serial version id
     */
    private static final long serialVersionUID = 1021075011386922097L;

    public UiElementFetchingException() {
    }

    public UiElementFetchingException(String message) {
        super(message);
    }

    public UiElementFetchingException(String message, Throwable inner) {
        super(message, inner);
    }
}
