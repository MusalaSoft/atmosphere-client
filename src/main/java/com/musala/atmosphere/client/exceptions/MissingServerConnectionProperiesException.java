package com.musala.atmosphere.client.exceptions;

import com.musala.atmosphere.commons.exceptions.AtmosphereRuntimeException;

/**
 * Thrown when the server connection properties are missing - the user doesn't have the <i>@Server</i> annotation or
 * missing configuration properties file in the test project working directory.
 *
 * @author vladimir.vladimirov
 *
 */
public class MissingServerConnectionProperiesException extends AtmosphereRuntimeException {
    private static final long serialVersionUID = 6402529875524928891L;

    public MissingServerConnectionProperiesException() {
    }

    public MissingServerConnectionProperiesException(String message) {
        super(message);
    }

    public MissingServerConnectionProperiesException(String message, Throwable inner) {
        super(message, inner);
    }

}
