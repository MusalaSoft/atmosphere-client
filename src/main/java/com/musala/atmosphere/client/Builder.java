package com.musala.atmosphere.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.exceptions.ServerConnectionFailedException;
import com.musala.atmosphere.client.util.ConfigurationPropertiesLoader;
import com.musala.atmosphere.client.util.LogcatAnnotationProperties;
import com.musala.atmosphere.client.util.ScreenRecordingAnnotationProperties;
import com.musala.atmosphere.client.util.ServerAnnotationProperties;
import com.musala.atmosphere.client.util.ServerConnectionProperties;
import com.musala.atmosphere.client.websocket.ClientServerWebSocketCommunicator;
import com.musala.atmosphere.commons.cs.clientbuilder.DeviceAllocationInformation;
import com.musala.atmosphere.commons.cs.clientbuilder.IClientBuilder;
import com.musala.atmosphere.commons.cs.clientdevice.IClientDevice;
import com.musala.atmosphere.commons.cs.deviceselection.DeviceSelector;
import com.musala.atmosphere.commons.cs.exception.DeviceNotFoundException;
import com.musala.atmosphere.commons.cs.exception.InvalidPasskeyException;
import com.musala.atmosphere.commons.exceptions.NoAvailableDeviceFoundException;
import com.musala.atmosphere.commons.util.Pair;

/**
 * Used by the user to get appropriate device in the server's pool.
 *
 * @author vladimir.vladimirov
 */
public class Builder {
    private static final Logger LOGGER = Logger.getLogger(Builder.class.getCanonicalName());

    private static Map<ServerConnectionProperties, Builder> builders = new HashMap<>();

    private static final int ALLOCATE_DEVICE_RETRY_TIMEOUT = 300_000; // 5 minutes

    private IClientBuilder clientBuilder;

    private Registry serverRmiRegistry;

    private ClientServerWebSocketCommunicator websocketCommunicator;

    private Map<Device, DeviceAllocationInformation> deviceToDescriptor = Collections.synchronizedMap(new HashMap<Device, DeviceAllocationInformation>());

    private ServerConnectionHandler serverConnectionHandler;

    private ScreenRecordingAnnotationProperties screenRecordingproperties;

    private LogcatAnnotationProperties logcatAnnotationProperties;

    /**
     * Initializes {@link Builder} and connects to Server through given {@link ServerConnectionHandler}.
     *
     * @param serverConnectionHandler
     *        - the given {@link ServerConnectionHandler}.
     */
    private Builder(ServerConnectionHandler serverConnectionHandler) {

        this.serverConnectionHandler = serverConnectionHandler;
        Pair<IClientBuilder, Registry> builderRegistryPair = serverConnectionHandler.connect();

        websocketCommunicator = new ClientServerWebSocketCommunicator();

        clientBuilder = builderRegistryPair.getKey();
        serverRmiRegistry = builderRegistryPair.getValue();

        this.screenRecordingproperties = new ScreenRecordingAnnotationProperties();
        this.logcatAnnotationProperties = new LogcatAnnotationProperties();
    }

    /**
     * Gets the {@link Builder Builder} instance for the annotated Server address.
     *
     * @return {@link Builder Builder} instance for the annotated Server address.
     */
    public static Builder getInstance() {
        ServerAnnotationProperties serverAnnotationProperties = new ServerAnnotationProperties();

        if(serverAnnotationProperties.isServerAnnotationExists()) {
            return getInstance(serverAnnotationProperties);
        }

        String serverIp = ConfigurationPropertiesLoader.getServerIp();
        int serverPort = ConfigurationPropertiesLoader.getServerPort();
        int connectionRetryLimit = ConfigurationPropertiesLoader.getConnectionRetries();

        ServerConnectionProperties serverConnectionProperties = new ServerConnectionProperties(serverIp,
                                                                                               serverPort,
                                                                                               connectionRetryLimit);

        return getInstance(serverConnectionProperties);
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
                    String message = "Builder instance has been created.";
                    LOGGER.info(message);
                    builders.put(serverConnectionProperties, builder);
                }
            }
        }

        return builder;
    }

    /**
     * Gets a {@link DeviceAllocationInformation} instance with the given {@link DeviceSelector device characteristics}.
     *
     * @param deviceSelector
     *        - required {@link DeviceSelector parameters} needed to construct new {@link DeviceAllocationInformation}
     *        instance
     * @param maxWaitTime
     *        - the time in milliseconds the builder should wait if a requested device is present on the server, but
     *        currently not available (allocated by another client)
     * @return a {@link DeviceAllocationInformation} instance with the given device selector
     * @throws NoAvailableDeviceFoundException
     */
    private DeviceAllocationInformation getDeviceDescriptor(DeviceSelector deviceSelector, int maxWaitTime) {
        DeviceAllocationInformation deviceInformation = websocketCommunicator.getDeviceDescriptor(deviceSelector, maxWaitTime);
        return deviceInformation;
    }

    /**
     * Gets a {@link Device Device} instance with the given {@link DeviceSelector device characteristics}.
     *
     * @param deviceSelector
     *        - required {@link DeviceSelector parameters} needed to construct new {@link Device Device} instance.
     * @return a {@link Device Device} instance with the given device parameters.
     */
    public Device getDevice(DeviceSelector deviceSelector) {
        return getDevice(deviceSelector, ALLOCATE_DEVICE_RETRY_TIMEOUT);
    }

    /**
     * Gets a {@link Device Device} instance with the given {@link DeviceSelector device characteristics} and maximum
     * wait time for available device
     *
     * @param deviceSelector
     *        - required {@link DeviceSelector parameters} needed to construct new {@link Device Device} instance.
     * @param maxWaitTime
     *        - the time in seconds the builder should wait if a requested device is present on the server, but
     *        currently not available (allocated by another client)
     * @return a {@link Device Device} instance with the given device parameters.
     */
    public Device getDevice(DeviceSelector deviceSelector, int maxWaitTime) {
        try {
            DeviceAllocationInformation deviceDescriptor = getDeviceDescriptor(deviceSelector, maxWaitTime);

            String deviceProxyRmiId = deviceDescriptor.getProxyRmiId();
            String messageReleasedDevice = String.format("Fetched device with proxy RMI ID: %s .", deviceProxyRmiId);
            LOGGER.info(messageReleasedDevice);

            IClientDevice iClientDevice = (IClientDevice) serverRmiRegistry.lookup(deviceProxyRmiId);
            long passkey = deviceDescriptor.getProxyPasskey();

            Device device = new DeviceBuilder(iClientDevice, passkey).build();
            deviceToDescriptor.put(device, deviceDescriptor);

            if (this.screenRecordingproperties.isEnabled()) {
                int duration = this.screenRecordingproperties.getDuration();
                device.startScreenRecording(duration, false);
            }
            if (this.logcatAnnotationProperties.isEnabled()) {
                device.clearLogcat();
            }

            return device;
        } catch (NoAvailableDeviceFoundException e) {
            String message = "No devices matching the requested parameters were found.";
            LOGGER.error(message, e);
            throw new NoAvailableDeviceFoundException(message, e);
        } catch (RemoteException | NotBoundException e) {
            String message = "Fetching device failed (server connection failure).";
            LOGGER.error(message, e);
            throw new ServerConnectionFailedException(message, e);
        }
    }

    /**
     * Gets list with serial numbers and models of all available devices.
     *
     * @return list with serial numbers and models of all available devices
     */
    public List<Pair<String, String>> getAllAvailableDevices() {
        try {
            return clientBuilder.getAllAvailableDevices();
        } catch (RemoteException e) {
            String message = "Failed to get the list with available devices (server connection failure).";
            LOGGER.error(message, e);
            throw new ServerConnectionFailedException(message, e);
        }
    }

    /**
     * Releases a given device.
     *
     * @param device
     *        - device to be released.
     * @throws DeviceNotFoundException
     *         if failed to find the device
     */
    public void releaseDevice(Device device) throws DeviceNotFoundException {
        DeviceAllocationInformation deviceDescriptor = deviceToDescriptor.get(device);
        String deviceRmiId = deviceDescriptor.getProxyRmiId();

        deviceToDescriptor.remove(device);
        try {
            if (this.screenRecordingproperties.isEnabled()) {
                device.stopScreenRecording();
            }
            if (this.logcatAnnotationProperties.isEnabled()) {
                device.getDeviceLog(logcatAnnotationProperties);
            }
            device.release();
            clientBuilder.releaseDevice(deviceDescriptor);
        } catch (RemoteException e) {
            String message = "Could not release Device (connection failure).";
            LOGGER.error(message, e);
            throw new ServerConnectionFailedException(message, e);
        } catch (InvalidPasskeyException e) {
            // We did not have the correct passkey. The device most likely timed out and got freed to be used by someone
            // else. So nothing to do here.
        }
        String messageReleasedDevice = String.format("Released device with proxy RMI ID: %s .", deviceRmiId);
        LOGGER.info(messageReleasedDevice);
    }

    /**
     * Releases all allocated devices.
     *
     * @throws DeviceNotFoundException
     *         if failed to find the device
     */
    public void releaseAllDevices() throws DeviceNotFoundException {
        Set<Device> devicesToRelease = new HashSet<>(deviceToDescriptor.keySet());
        for (Device device : devicesToRelease) {
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
    protected void finalize() throws DeviceNotFoundException {
        synchronized (Builder.class) {
            releaseAllDevices();
            builders.remove(getServerConnectionProperties());
        }
    }
}