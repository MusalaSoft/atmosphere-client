package com.musala.atmosphere.client;

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
import com.musala.atmosphere.client.websocket.ClientDispatcher;
import com.musala.atmosphere.commons.cs.clientbuilder.DeviceAllocationInformation;
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

    // timeout between attempts to get a device
    private static final int RETRY_SLEEP_TIMEOUT = 1000;

    private static Map<ServerConnectionProperties, Builder> builders = new HashMap<>();

    private Map<Device, DeviceAllocationInformation> deviceToDescriptor = Collections.synchronizedMap(new HashMap<Device, DeviceAllocationInformation>());

    private ServerConnectionProperties serverConnectionProperties;

    // the count of the attempts to get a device
    private int allocateDeviceRetryCount = 300;

    private ScreenRecordingAnnotationProperties screenRecordingproperties;

    private LogcatAnnotationProperties logcatAnnotationProperties;

    private ClientDispatcher dispatcher = ClientDispatcher.getInstance();

    /**
     * Initializes {@link Builder} and connects to Server through given {@link ServerConnectionHandler}.
     *
     * @param serverConnectionHandler
     *        - the given {@link ServerConnectionHandler}.
     */
    private Builder(ServerConnectionProperties serverConnectionProperties) {
        // establish an WebSocket connection
        dispatcher.connectToServer(serverConnectionProperties);

        this.serverConnectionProperties = serverConnectionProperties;
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

        if (serverAnnotationProperties.isServerAnnotationExists()) {
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
                    builder = new Builder(serverConnectionProperties);
                    String message = "Builder instance has been created.";
                    LOGGER.info(message);
                    builders.put(serverConnectionProperties, builder);
                }
            }
        }

        return builder;
    }

    /**
     * Gets a {@link Device Device} instance with the given {@link DeviceSelector device characteristics}.
     *
     * @param deviceSelector
     *        - required {@link DeviceSelector parameters} needed to construct new {@link Device Device} instance.
     * @return a {@link Device Device} instance with the given device parameters.
     */
    public Device getDevice(DeviceSelector deviceSelector) {
        try {
            DeviceAllocationInformation deviceDescriptor = dispatcher.getDeviceDescriptor(deviceSelector,
                                                                                          allocateDeviceRetryCount);

            final String deviceId = deviceDescriptor.getDeviceId();
            LOGGER.info(String.format("Fetched device with ID: %s .", deviceId));

            long passkey = deviceDescriptor.getProxyPasskey();

            Device device = new Device(passkey, deviceId);
            deviceToDescriptor.put(device, deviceDescriptor);

            if (this.screenRecordingproperties.isEnabled()) {
                int duration = this.screenRecordingproperties.getDuration();
                device.startScreenRecording(duration, false);
            }
            if (this.logcatAnnotationProperties.isEnabled()) {
                device.clearLogcat();
            }

            ConfigurationPropertiesLoader.loadImplicitWait();

            return device;
        } catch (NoAvailableDeviceFoundException e) {
            String message = "No devices matching the requested parameters were found";
            LOGGER.error(message, e);
            throw new NoAvailableDeviceFoundException(message, e);
        }
    }

    /**
     * Gets a {@link Device Device} instance with the given {@link DeviceSelector device characteristics} and maximum
     * wait time for available device
     *
     * @param deviceSelector
     *        - required {@link DeviceSelector parameters} needed to construct new {@link Device Device} instance.
     * @param maxWaitTime
     *        - maximum wait time for available device
     * @return a {@link Device Device} instance with the given device parameters.
     */
    public Device getDevice(DeviceSelector deviceSelector, int maxWaitTime) {
        this.allocateDeviceRetryCount = maxWaitTime / RETRY_SLEEP_TIMEOUT;
        return getDevice(deviceSelector);
    }

    /**
     * Gets list with serial numbers and models of all available devices.
     *
     * @return list with serial numbers and models of all available devices
     */
    public List<Pair<String, String>> getAllAvailableDevices() {
        List<Pair<String, String>> deviceList = null;
        try {
            deviceList = dispatcher.getAllAvailableDevices();
        } catch (Exception e) {
            String message = "Failed to get the list with available devices (server connection failure).";
            LOGGER.error(message, e);
        }

        return deviceList;
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
        String deviceId = deviceDescriptor.getDeviceId();

        deviceToDescriptor.remove(device);

        if (this.screenRecordingproperties.isEnabled()) {
            device.stopScreenRecording();
        }
        if (this.logcatAnnotationProperties.isEnabled()) {
            device.getDeviceLog(logcatAnnotationProperties);
        }
        device.release();
        try {
            dispatcher.releaseDevice(deviceDescriptor);
        } catch (Exception e) {
            if (e instanceof InvalidPasskeyException) {
                // We did not have the correct passkey. The device most likely timed out and got freed to be used by
                // someone else. So nothing to do here.
            } else if (e instanceof DeviceNotFoundException) {
                throw (DeviceNotFoundException) e;
            } else if (e instanceof ServerConnectionFailedException) {
                String message = "Could not release Device (connection failure).";
                LOGGER.error(message, e);
                throw new ServerConnectionFailedException(message, e);
            }
        }

        String messageReleasedDevice = String.format("Released device with ID: %s .", deviceId);
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
        return serverConnectionProperties;
    }

    @Override
    protected void finalize() throws DeviceNotFoundException {
        synchronized (Builder.class) {
            releaseAllDevices();
            builders.remove(getServerConnectionProperties());
        }
    }
}