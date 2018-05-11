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
 * This exception is thrown when the Client tries to install application on a device, but a fatal error occurs and the
 * installation fails.
 * 
 * @author vladimir.vladimirov
 * 
 */
public class ApkInstallationFailedException extends AtmosphereRuntimeException {
    /**
     * auto generated serialization id
     */
    private static final long serialVersionUID = -4948287126155328678L;

    public ApkInstallationFailedException() {
    }

    public ApkInstallationFailedException(String message) {
        super(message);
    }

    public ApkInstallationFailedException(String message, Throwable inner) {
        super(message, inner);
    }
}
