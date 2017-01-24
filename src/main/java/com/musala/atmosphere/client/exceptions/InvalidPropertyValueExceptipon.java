package com.musala.atmosphere.client.exceptions;

import com.musala.atmosphere.commons.exceptions.AtmosphereRuntimeException;

/**
 * Thrown when a properties value from the configuration properties file is invalid.
 *
 * @author dimcho.nedev
 *
 */
public class InvalidPropertyValueExceptipon extends AtmosphereRuntimeException {
    private static final long serialVersionUID = 8700191687913165861L;

    public InvalidPropertyValueExceptipon() {
    }

    public InvalidPropertyValueExceptipon(String message) {
        super(message);
    }

    public InvalidPropertyValueExceptipon(String message, Throwable inner) {
        super(message, inner);
    }

}
