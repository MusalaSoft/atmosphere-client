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
 * When a user uses the Builder class to get a device, the device is allocated to that user. When the device is no
 * longer need it is released and returned to the device pool and the instance of the device shouldn't be used anymore.
 * This exception is thrown whenever someone tries to use an instance of a released device.
 * 
 * @author valyo.yolovski
 * 
 */
public class DeviceReleasedException extends AtmosphereRuntimeException {
    /**
	 *
	 */
    private static final long serialVersionUID = 842392675910566350L;

    public DeviceReleasedException() {

    }

    public DeviceReleasedException(String message) {
        super(message);
    }

    public DeviceReleasedException(String message, Throwable inner) {
        super(message, inner);
    }
}
