package com.musala.atmosphere.client.exceptions;

import com.musala.atmosphere.client.UiElement;
import com.musala.atmosphere.commons.exceptions.AtmosphereRuntimeException;

/**
 * Thrown when we want to execute an action on a {@link UiElement UiElement} instance that is inadequate for the element
 * like executing clicking or focusing on an non clickable or non-focusable {@link UiElement UiElement}.
 * 
 * @author georgi.gaydarov
 * 
 */
public class InvalidElementActionException extends AtmosphereRuntimeException {

    /**
     * auto generated serialization id
     */
    private static final long serialVersionUID = 2374589821011286146L;

    public InvalidElementActionException() {
    }

    public InvalidElementActionException(String message) {
        super(message);
    }

    public InvalidElementActionException(String message, Throwable inner) {
        super(message, inner);
    }
}
