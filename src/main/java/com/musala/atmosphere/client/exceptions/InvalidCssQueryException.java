// This file is part of the ATMOSPHERE mobile testing framework.
// Copyright (C) 2016 MusalaSoft
//
// ATMOSPHERE is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// ATMOSPHERE is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with ATMOSPHERE.  If not, see <http://www.gnu.org/licenses/>.

package com.musala.atmosphere.client.exceptions;

import com.musala.atmosphere.client.uiutils.CssToXPathConverter;

/**
 * Thrown when the {@link CssToXPathConverter CssToXPathConverter} is given an invalid CssQuery or the given CssQuery
 * does not contains the needed requirements for the regex to work.
 * 
 * @author simeon.ivanov
 * 
 */
public class InvalidCssQueryException extends RuntimeException {
    /**
     * auto generated serialization id
     */
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
