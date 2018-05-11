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
