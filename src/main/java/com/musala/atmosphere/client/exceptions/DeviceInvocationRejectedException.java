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
 * Thrown when the invocation on the server's device representation object was rejected. This exception is usually
 * thrown when invalid passKey is given or the server release the device.
 * 
 * @author georgi.gaydarov
 * 
 */
public class DeviceInvocationRejectedException extends AtmosphereRuntimeException {

    /**
     * auto generated serialization id
     */
    private static final long serialVersionUID = -3193659488917500184L;

    public DeviceInvocationRejectedException() {
    }

    public DeviceInvocationRejectedException(String message) {
        super(message);
    }

    public DeviceInvocationRejectedException(String message, Throwable inner) {
        super(message, inner);
    }

    public DeviceInvocationRejectedException(Throwable inner) {
        super("Method invocation was rejected. The device allocation lock could have timed out.", inner);
    }
}
