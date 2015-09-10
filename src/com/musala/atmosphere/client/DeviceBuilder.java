package com.musala.atmosphere.client;

import com.musala.atmosphere.commons.DeviceInformation;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.cs.clientdevice.IClientDevice;

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

    // TODO: Getting device screen through the communicator will be implemented in one of the next tasks;
    private Screen screen;

    public DeviceBuilder() {
    }

    public DeviceBuilder(IClientDevice clientDevice, long devicePasskey) {
        deviceCommunicator = new DeviceCommunicator(clientDevice, devicePasskey);
    }

    /**
     * Creates {@link Device} instance with the proper implementations for device specific operations, based on the
     * {@link DeviceInformation device information}.
     * 
     * @return {@link Device} instance
     */
    public Device build() {
        // TODO: Implement the logic for building device instance with the proper set of entities depending on the
        // device information received.
        populateDeviceInformation();
        return new Device(deviceCommunicator);
    }

    private void populateDeviceInformation() {
        deviceInformation = (DeviceInformation) deviceCommunicator.sendAction(RoutingAction.GET_DEVICE_INFORMATION);
    }
}
