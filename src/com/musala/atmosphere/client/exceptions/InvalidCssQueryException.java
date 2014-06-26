package com.musala.atmosphere.client.exceptions;

import com.musala.atmosphere.client.uiutils.CssToXPathConverter;

/**
 * Thrown when the {@link CssToXPathConverter CssToXPathConverter} is given an invalid CssQuery.
 * 
 * @author simeon.ivanov
 * 
 */
public class InvalidCssQueryException extends Exception {
    private static final long serialVersionUID = 7849572600533114266L;

    public InvalidCssQueryException() {

    }

    public InvalidCssQueryException(String message) {
        super(message);
    }

    public InvalidCssQueryException(String message, Throwable inner) {
        super(message, inner);
    }
}
