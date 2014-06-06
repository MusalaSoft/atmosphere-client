package com.musala.atmosphere.client.exceptions;

import com.musala.atmosphere.client.Device;
import com.musala.atmosphere.client.Screen;
import com.musala.atmosphere.client.UiElement;
import com.musala.atmosphere.commons.exceptions.AtmosphereRuntimeException;

/**
 * Thrown when a {@link UiElement UiElement} is no longer valid in a {@link Device Device}'s {@link Screen Screen}
 * context.
 * 
 * @author georgi.gaydarov
 * 
 */
public class StaleElementReferenceException extends AtmosphereRuntimeException {

    /**
     * auto generated serialization id
     */
    private static final long serialVersionUID = 838199118036199940L;

    public StaleElementReferenceException() {
    }

    public StaleElementReferenceException(String message) {
        super(message);
    }

    public StaleElementReferenceException(String message, Throwable inner) {
        super(message, inner);
    }
}
