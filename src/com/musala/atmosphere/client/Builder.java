package com.musala.atmosphere.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.exceptions.ServerConnectionFailedException;
import com.musala.atmosphere.client.util.ServerAnnotationProperties;
import com.musala.atmosphere.client.util.ServerConnectionProperties;
import com.musala.atmosphere.commons.cs.InvalidPasskeyException;
import com.musala.atmosphere.commons.cs.clientbuilder.DeviceAllocationInformation;
import com.musala.atmosphere.commons.cs.clientbuilder.DeviceParameters;
import com.musala.atmosphere.commons.cs.clientbuilder.IClientBuilder;
import com.musala.atmosphere.commons.cs.clientdevice.IClientDevice;
import com.musala.atmosphere.commons.util.Pair;

/**
 * Used by the user to get appropriate device in the server's pool.
 * 
 * @author vladimir.vladimirov
 */
public class Builder {
    private static final Logger LOGGER = Logger.getLogger(Builder.class.getCanonicalName());

    private static Map<ServerConnectionProperties, Builder> builders = new HashMap<ServerConnectionProperties, Builder>();

    private IClientBuilder clientBuilder;

    private Registry serverRmiRegistry;

    private Map<Device, DeviceAllocationInformation> deviceToDescriptor = new HashMap<Device, DeviceAllocationInformation>();

    private ServerConnectionHandler serverConnectionHandler;

    /**
     * Initializes {@link Builder} and connects to Server through given {@link ServerConnectionHandler}.
     * 
     * @param serverConnectionHandler
     *        - the given {@link ServerConnectionHandler}.
     */
    private Builder(ServerConnectionHandler serverConnectionHandler) {

        this.serverConnectionHandler = serverConnectionHandler;
        Pair<IClientBuilder, Registry> builderRegistryPair = serverConnectionHandler.connect();

        clientBuilder = builderRegistryPair.getKey();
        serverRmiRegistry = builderRegistryPair.getValue();
    }

    /**
     * Gets the {@link Builder Builder} instance for the annotated Server address.
     * 
     * @return {@link Builder Builder} instance for the annotated Server address.
     */
    public static Builder getInstance() {
        ServerAnnotationProperties serverAnnotationProperties = new ServerAnnotationProperties();
        Builder builder = builders.get(serverAnnotationProperties);

        if (builder == null) {
            synchronized (Builder.class) {
                builder = builders.get(serverAnnotationProperties);

                if (builder == null) {
                    ServerConnectionHandler serverConnectionHandler = new ServerConnectionHandler(serverAnnotationProperties);

                    builder = new Builder(serverConnectionHandler);
                    LOGGER.info("Builder instance has been created.");
                    builders.put(serverAnnotationProperties, builder);
                }

            }
        }

        return builder;
    }

    /**
     * Gets the {@link Builder Builder} instance for the given {@link ServerConnectionProperties}
     * 
     * @param serverConnectionProperties
     *        - the given server connection properties.
     * 
     * @return {@link Builder Builder} instance for the given server connection properties.
     */
    public static Builder getInstance(ServerConnectionProperties serverConnectionProperties) {
        Builder builder = builders.get(serverConnectionProperties);

        if (builder == null) {
            synchronized (Builder.class) {
                builder = builders.get(serverConnectionProperties);

                if (builder == null) {
                    ServerConnectionHandler serverConnectionHandler = new ServerConnectionHandler(serverConnectionProperties);

                    builder = new Builder(serverConnectionHandler);
                    LOGGER.info("Builder instance has been created.");
                    builders.put(serverConnectionProperties, builder);
                }
            }
        }

        return builder;
    }

    /**
     * Gets a {@link Device Device} instance with given {@link DeviceParameters DeviceParameters}.
     * 
     * @param deviceParameters
     *        - required {@link DeviceParameters} needed to construct new {@link Device Device} instance.
     * @return a {@link Device Device} instance with a given device parameters.
     */
    public Device getDevice(DeviceParameters deviceParameters) {
        try {
            DeviceAllocationInformation deviceDescriptor = clientBuilder.allocateDevice(deviceParameters);

            String deviceProxyRmiId = deviceDescriptor.getProxyRmiId();
            LOGGER.info("Fetched device with proxy RMI id: " + deviceProxyRmiId + ".");

            IClientDevice iClientDevice = (IClientDevice) serverRmiRegistry.lookup(deviceProxyRmiId);
            long passkey = deviceDescriptor.getProxyPasskey();

            Device device = new Device(iClientDevice, passkey, serverConnectionHandler);
            deviceToDescriptor.put(device, deviceDescriptor);
            return device;
        } catch (RemoteException | NotBoundException e) {
            LOGGER.error("Fetching Device failed (server connection failure).", e);
            throw new ServerConnectionFailedException("Fetching Device failed (server connection failure).", e);
        }
    }

    /**
     * Releases a given device.
     * 
     * @param device
     *        - device to be released.
     */
    public void releaseDevice(Device device) {
        DeviceAllocationInformation deviceDescriptor = deviceToDescriptor.get(device);
        String deviceRmiId = deviceDescriptor.getProxyRmiId();

        try {
            deviceToDescriptor.remove(device);
            device.release();
            clientBuilder.releaseDevice(deviceDescriptor);
        } catch (RemoteException e) {
            LOGGER.error("Could not release Device (connection failure).", e);
            throw new ServerConnectionFailedException("Could not release Device (connection failure).", e);
        } catch (InvalidPasskeyException e) {
            // We did not have the correct passkey. The device most likely timed out and got freed to be used by someone
            // else. So nothing to do here.
        }

        LOGGER.info(deviceRmiId + " is released.");
    }

    /**
     * Releases all allocated devices.
     */
    public void releaseAllDevices() {
        for (Device device : deviceToDescriptor.keySet()) {
            releaseDevice(device);
        }
    }

    /**
     * Gets the {@link ServerConnectionProperties} that are used for connection.
     * 
     * @return the {@link ServerConnectionProperties} that are used for connection.
     */
    public ServerConnectionProperties getServerConnectionProperties() {
        return serverConnectionHandler.getServerConnectionProperties();
    }

    @Override
    protected void finalize() {
        synchronized (Builder.class) {
            releaseAllDevices();
            builders.remove(getServerConnectionProperties());
        }
    }
}
