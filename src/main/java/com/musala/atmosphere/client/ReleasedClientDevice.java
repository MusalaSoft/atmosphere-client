package com.musala.atmosphere.client;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.exceptions.DeviceReleasedException;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.cs.clientdevice.IClientDevice;
import com.musala.atmosphere.commons.cs.exception.InvalidPasskeyException;
import com.musala.atmosphere.commons.exceptions.CommandFailedException;

/**
 * An instance of this class is used when a deivce allocated to a Client is released. The methods in this class throw
 * {@link DeviceReleasedException DeviceReleasedException} whenever some of them are called and thus notify the user
 * that he or she is trying to use a device that has been released (and can not be used anymore).
 * 
 * @author valyo.yolovski
 * 
 */
class ReleasedClientDevice implements IClientDevice {
    private static final Logger LOGGER = Logger.getLogger(ReleasedClientDevice.class);

    @Override
    public Object route(long invocationPasskey, RoutingAction action, Object... args)
        throws RemoteException,
            CommandFailedException,
            InvalidPasskeyException {
        String message = "Device has been released.";
        LOGGER.fatal(message);
        throw new DeviceReleasedException(message);
    }
}
