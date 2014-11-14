package com.musala.atmosphere.client.exceptions;

/**
 * Thrown when taking and fetching screenshot of the device fails.
 * 
 * @author denis.bialev
 * 
 */
public class GettingScreenshotFailedException extends Exception {

    private static final long serialVersionUID = -6326121434598197501L;

    public GettingScreenshotFailedException() {
        super();
    }

    public GettingScreenshotFailedException(String message) {
        super(message);
    }

    public GettingScreenshotFailedException(String message, Throwable inner) {
        super(message, inner);
    }

}
