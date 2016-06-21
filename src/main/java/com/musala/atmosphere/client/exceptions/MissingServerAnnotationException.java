package com.musala.atmosphere.client.exceptions;

import com.musala.atmosphere.commons.exceptions.AtmosphereRuntimeException;

/**
 * Thrown when test class of the user doesn't have the <i>@Server</i> annotation like when an annotated class can not be
 * found or the invoking class is missing a <i>@Server</i> annotation.
 * 
 * @author vladimir.vladimirov
 * 
 */
public class MissingServerAnnotationException extends AtmosphereRuntimeException {
    /**
     * auto generated serialization id
     */
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
