package com.musala.atmosphere.client.exceptions;

import com.musala.atmosphere.commons.exceptions.AtmosphereRuntimeException;

/**
 * This exception is thrown when the Client cannot connect to the Server for some reason - wrong server IP or port,
 * Server is not present on the given IP and port, or the Client is trying to connect to something that is not Server (
 * it doesn't have PoolManager in it's RMI registry ).
 * 
 * @author vladimir.vladimirov
 * 
 */
public class ServerConnectionFailedException extends AtmosphereRuntimeException {
    /**
     * auto generated serialization id
     */
    private static final long serialVersionUID = -4528263673224194846L;

    public ServerConnectionFailedException() {
    }

    public ServerConnectionFailedException(String message) {
        super(message);
    }

    public ServerConnectionFailedException(String message, Throwable inner) {
        super(message, inner);
    }
}
