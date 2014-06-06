package com.musala.atmosphere.client.exceptions;

import com.musala.atmosphere.commons.exceptions.AtmosphereRuntimeException;

/**
 * Thrown when test class of the QA doesn't have the <i>@Server</i> annotation.
 * 
 * @author vladimir.vladimirov
 * 
 */
public class MissingServerAnnotationException extends AtmosphereRuntimeException {
    private static final long serialVersionUID = 6402529875524928891L;

    public MissingServerAnnotationException() {
    }

    public MissingServerAnnotationException(String message) {
        super(message);
    }

    public MissingServerAnnotationException(String message, Throwable inner) {
        super(message, inner);
    }
}
