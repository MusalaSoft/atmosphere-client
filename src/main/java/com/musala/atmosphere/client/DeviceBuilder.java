package com.musala.atmosphere.client;

import com.musala.atmosphere.commons.DeviceInformation;
import com.musala.atmosphere.commons.RoutingAction;

/**
 * Class responsible for creating {@link Device} instance with the suitable implementations for device specific
 * operations, based on the {@link DeviceInformation device information}.
 *
 * @author filareta.yordanova
 *
 */
public class DeviceBuilder {
    private DeviceInformation deviceInformation;

    private DeviceCommunicator deviceCommunicator;

    // TODO: Use UiElementSelectionEntity here for getting elements from device screen. This must be fixed in
    // the GpsLocationEntitiy before it is used in Device;
    private Screen screen;

    public DeviceBuilder() {
    }

    public DeviceBuilder(long devicePasskey, String deviceId) {
        deviceCommunicator = new DeviceCommunicator(devicePasskey, deviceId);
    }

    /**
     * Creates {@link Device} instance with the proper implementations for device specific operations, based on the
     * {@link DeviceInformation device information}.
     *
     * @return {@link Device} instance
     */
    public Device build() {
        populateDeviceInformation();

        Device device = new Device(deviceCommunicator);

        return device;
    }

    private void populateDeviceInformation() {
        deviceInformation = (DeviceInformation) deviceCommunicator.sendAction(RoutingAction.GET_DEVICE_INFORMATION);
    }
}
