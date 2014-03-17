package com.musala.atmosphere.client;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.exceptions.DeviceInvocationRejectedException;
import com.musala.atmosphere.client.exceptions.DeviceReleasedException;
import com.musala.atmosphere.client.exceptions.ServerConnectionFailedException;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.cs.InvalidPasskeyException;
import com.musala.atmosphere.commons.cs.clientdevice.IClientDevice;
import com.musala.atmosphere.commons.exceptions.CommandFailedException;

/**
 * Class used for handling all requests to the remote devices and the possible exceptions.
 * 
 * @author georgi.gaydarov
 * 
 */
public class DeviceCommunicator {
    private long invocationPasskey;

    private IClientDevice wrappedClientDevice;

    private static final Logger LOGGER = Logger.getLogger(DeviceCommunicator.class.getCanonicalName());

    public static final Object VOID_SUCCESS = new Object();

    private CommandFailedException lastSentActionException;

    /**
     * Creates an instance for specified client device.
     * 
     * @param wrappedDevice
     *        - the {@link IClientDevice} instance which this instance will communicate with.
     * @param passkey
     *        - the invocation passkey for the client device instance.
     */
    DeviceCommunicator(IClientDevice wrappedDevice, long passkey) {
        wrappedClientDevice = wrappedDevice;
        invocationPasskey = passkey;
    }

    /**
     * Replaces the underlying client device so no further invocation can be possible.
     */
    public void release() {
        wrappedClientDevice = new ReleasedClientDevice();
    }

    /**
     * @return the {@link CommandFailedException} instance that was thrown during the last action if such exception
     *         occured, <code>null</code> otherwise.
     */
    public CommandFailedException getLastException() {
        return lastSentActionException;
    }

    /**
     * Requests an action invocation on the device wrapper.
     * 
     * @param invocationPasskey
     *        - the passkey that authorizes this invocation.
     * @param action
     *        - a {@link RoutingAction} instance that specifies the action to be invoked.
     * @param args
     *        - the action parameters (if required).
     * @return the result from the action invocation.
     */
    public Object sendAction(RoutingAction action, Object... args) {
        lastSentActionException = null;
        try {
            Object response = wrappedClientDevice.route(invocationPasskey, action, args);
            if (response == null) {
                response = VOID_SUCCESS;
            }
            return response;
        } catch (RemoteException e) {
            LOGGER.error("Executing action failed.", e);
            handleLostConnection();
        } catch (CommandFailedException e) {
            LOGGER.error("Executing action failed.", e);
            lastSentActionException = e;
        } catch (InvalidPasskeyException e) {
            LOGGER.error("Executing action was rejected by the server.", e);
            throw new DeviceInvocationRejectedException(e);
        }
        return null;
    }

    /**
     * Attempts to reconnect to the ATMOSPHERE server.
     * 
     * @throws ServerConnectionFailedException
     * @throws DeviceReleasedException
     */
    private void handleLostConnection() {
        // try {
        // serverConnectionHandler.connect();
        // } catch (ServerConnectionFailedException e) {
        // throw e;
        // }
        // FIXME handle lost connection

        // this message is disinforming and points to an error, which is not occuring!
        // String message = "Reconnecting to server succeeded, but the device was already released.";

        LOGGER.fatal("Lost connection handler reached. This means either a device timed out or a RemoteException was thrown.");
        // throw new DeviceReleasedException(message);

        String message = "Reconnecting to server succeeded, but the device was already released.";

        LOGGER.fatal(message);
        throw new DeviceReleasedException(message);
    }
}
