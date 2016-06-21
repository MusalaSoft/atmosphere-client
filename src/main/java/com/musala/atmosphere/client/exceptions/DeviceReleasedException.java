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
