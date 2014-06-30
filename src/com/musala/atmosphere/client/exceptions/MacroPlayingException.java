package com.musala.atmosphere.client.exceptions;

/**
 * Thrown when playing a macro file fails for some reason.
 * 
 * @author georgi.gaydarov
 * 
 */
public class MacroPlayingException extends Exception {

    /**
     * auto generated serialization id
     */
    private static final long serialVersionUID = -2171615140809850596L;

    public MacroPlayingException() {
    }

    public MacroPlayingException(String message) {
        super(message);
    }

    public MacroPlayingException(String message, Throwable inner) {
        super(message, inner);
    }
}
