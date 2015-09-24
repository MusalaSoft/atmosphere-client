package com.musala.atmosphere.client;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.musala.atmosphere.client.entity.DeviceSettingsEntity;
import com.musala.atmosphere.client.entity.EntityTypeResolver;
import com.musala.atmosphere.client.entity.GestureEntity;
import com.musala.atmosphere.client.entity.GpsLocationEntity;
import com.musala.atmosphere.client.entity.HardwareButtonEntity;
import com.musala.atmosphere.client.entity.ImeEntity;
import com.musala.atmosphere.client.exceptions.UnresolvedEntityTypeException;
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

    // TODO: Use UiElementSelectionEntity here for getting elements from device screen. This must be fixed in
    // the GpsLocationEntitiy before it is used in Device;
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
        populateDeviceInformation();

        EntityTypeResolver typeResolver = new EntityTypeResolver(deviceInformation);
        Device device = new Device(deviceCommunicator);

        try {
            Constructor<?> hardwareButtonEntityConstructor = HardwareButtonEntity.class.getDeclaredConstructor(DeviceCommunicator.class);
            hardwareButtonEntityConstructor.setAccessible(true);
            device.setHardwareButtonEntity((HardwareButtonEntity) hardwareButtonEntityConstructor.newInstance(new Object[] {
                    deviceCommunicator}));

            Constructor<?> gestureEntitiyConstructor = GestureEntity.class.getDeclaredConstructor(DeviceCommunicator.class,
                                                                                                  DeviceInformation.class);
            gestureEntitiyConstructor.setAccessible(true);
            device.setGestureEntity((GestureEntity) gestureEntitiyConstructor.newInstance(new Object[] {
                    deviceCommunicator, deviceInformation}));

            Constructor<?> imeEntitiyConstructor = ImeEntity.class.getDeclaredConstructor(DeviceCommunicator.class);
            imeEntitiyConstructor.setAccessible(true);
            device.setImeEntity((ImeEntity) imeEntitiyConstructor.newInstance(new Object[] {deviceCommunicator}));

            Constructor<?> settingsEntitiyConstructor = DeviceSettingsEntity.class.getDeclaredConstructor(DeviceCommunicator.class,
                                                                                                          DeviceInformation.class);
            settingsEntitiyConstructor.setAccessible(true);
            device.setSettingsEntity((DeviceSettingsEntity) settingsEntitiyConstructor.newInstance(new Object[] {
                    deviceCommunicator, deviceInformation}));

            Class<?> locationEntityClass = typeResolver.getEntityClass(GpsLocationEntity.class);
            Constructor<?> locationEntityConstructor = locationEntityClass.getDeclaredConstructor(Screen.class,
                                                                                                  DeviceCommunicator.class);
            locationEntityConstructor.setAccessible(true);
            device.setGpsLocationEntity((GpsLocationEntity) locationEntityConstructor.newInstance(new Object[] {screen,
                    deviceCommunicator}));
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            throw new UnresolvedEntityTypeException("Failed to find the correct set of entities implmentations matching the given device information.",
                                                    e);
        }

        return device;
    }

    private void populateDeviceInformation() {
        deviceInformation = (DeviceInformation) deviceCommunicator.sendAction(RoutingAction.GET_DEVICE_INFORMATION);
    }
}
