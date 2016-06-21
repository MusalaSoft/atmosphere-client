package com.musala.atmosphere.client.exceptions;

import com.musala.atmosphere.client.Screen;
import com.musala.atmosphere.client.UiElement;
import com.musala.atmosphere.commons.ui.selector.UiElementSelector;

/**
 * 
 * Thrown when a method that attempts to fetch a single {@link UiElement UiElement} finds multiple {@link UiElement
 * elements} on the current {@link Screen screen} which are matching the requested {@link UiElementSelector selector}.
 * 
 * 
 * @author filareta.yordanova
 *
 */
public class MultipleElementsFoundException extends Exception {
    private static final long serialVersionUID = -3994047180774883723L;

    /**
     * Creates new {@link MultipleElementsFoundException MultipleElementsFoundException}.
     */
    public MultipleElementsFoundException() {
        super();
    }

    /**
     * Creates new {@link MultipleElementsFoundException MultipleElementsFoundException} with the given message.
     * 
     * @param message
     *        - message representing the error that occurred
     */
    public MultipleElementsFoundException(String message) {
        super(message);
    }

    /**
     * Creates new {@link MultipleElementsFoundException MultipleElementsFoundException} with the given message and the
     * {@link Throwable cause} for the exception.
     * 
     * @param message
     *        - message representing the error that occurred
     * @param throwable
     *        - the cause for the exception
     */
    public MultipleElementsFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
