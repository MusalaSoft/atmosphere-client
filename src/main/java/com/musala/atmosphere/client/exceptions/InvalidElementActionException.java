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

import com.musala.atmosphere.client.UiElement;
import com.musala.atmosphere.commons.exceptions.AtmosphereRuntimeException;

/**
 * Thrown when we want to execute an action on a {@link UiElement UiElement} instance that is inadequate for the element
 * like executing clicking or focusing on an non clickable or non-focusable {@link UiElement UiElement}.
 * 
 * @author georgi.gaydarov
 * 
 */
public class InvalidElementActionException extends AtmosphereRuntimeException {

    /**
     * auto generated serialization id
     */
    private static final long serialVersionUID = 2374589821011286146L;

    public InvalidElementActionException() {
    }

    public InvalidElementActionException(String message) {
        super(message);
    }

    public InvalidElementActionException(String message, Throwable inner) {
        super(message, inner);
    }
}
