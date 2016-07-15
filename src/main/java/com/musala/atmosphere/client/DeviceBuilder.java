package com.musala.atmosphere.client;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.musala.atmosphere.client.entity.AccessibilityElementEntity;
import com.musala.atmosphere.client.entity.DeviceSettingsEntity;
import com.musala.atmosphere.client.entity.EntityTypeResolver;
import com.musala.atmosphere.client.entity.GestureEntity;
import com.musala.atmosphere.client.entity.GpsLocationEntity;
import com.musala.atmosphere.client.entity.ImageEntity;
import com.musala.atmosphere.client.entity.ImeEntity;
import com.musala.atmosphere.client.exceptions.UnresolvedEntityTypeException;
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

        EntityTypeResolver typeResolver = new EntityTypeResolver(deviceInformation);
        Device device = new Device(deviceCommunicator);

        try {
            Constructor<?> gestureEntitiyConstructor = GestureEntity.class.getDeclaredConstructor(DeviceCommunicator.class,
                                                                                                  DeviceInformation.class);
            gestureEntitiyConstructor.setAccessible(true);
            GestureEntity gestureEntity = (GestureEntity) gestureEntitiyConstructor.newInstance(new Object[] {
                    deviceCommunicator, deviceInformation});
            device.setGestureEntity(gestureEntity);

            Constructor<?> imeEntitiyConstructor = ImeEntity.class.getDeclaredConstructor(DeviceCommunicator.class);
            imeEntitiyConstructor.setAccessible(true);
            ImeEntity imeEntity = (ImeEntity) imeEntitiyConstructor.newInstance(new Object[] {deviceCommunicator});
            device.setImeEntity(imeEntity);

            Constructor<?> settingsEntitiyConstructor = DeviceSettingsEntity.class.getDeclaredConstructor(DeviceCommunicator.class,
                                                                                                          DeviceInformation.class);
            settingsEntitiyConstructor.setAccessible(true);
            DeviceSettingsEntity settingsEntity = (DeviceSettingsEntity) settingsEntitiyConstructor.newInstance(new Object[] {
                    deviceCommunicator, deviceInformation});
            device.setSettingsEntity(settingsEntity);

            Constructor<?> imageEntityConstructor = ImageEntity.class.getDeclaredConstructor(DeviceCommunicator.class,
                                                                                             DeviceSettingsEntity.class);
            imageEntityConstructor.setAccessible(true);
            ImageEntity imageEntity = (ImageEntity) imageEntityConstructor.newInstance(new Object[] {deviceCommunicator,
                    settingsEntity});
            device.setImageEntity(imageEntity);

            Constructor<?> accessibilityElementEntityConstructor = AccessibilityElementEntity.class.getDeclaredConstructor(DeviceCommunicator.class,
                                                                                                                           GestureEntity.class,
                                                                                                                           ImeEntity.class,
                                                                                                                           DeviceSettingsEntity.class,
                                                                                                                           ImageEntity.class);
            accessibilityElementEntityConstructor.setAccessible(true);
            AccessibilityElementEntity accessibilityElementEntity = (AccessibilityElementEntity) accessibilityElementEntityConstructor.newInstance(new Object[] {
                    deviceCommunicator, gestureEntity, imeEntity, settingsEntity, imageEntity});
            device.setAccessibilityElementEntity(accessibilityElementEntity);

            Class<?> locationEntityClass = typeResolver.getEntityClass(GpsLocationEntity.class);
            Constructor<?> locationEntityConstructor = locationEntityClass.getDeclaredConstructor(DeviceCommunicator.class,
                                                                                                  AccessibilityElementEntity.class);
            locationEntityConstructor.setAccessible(true);
            device.setGpsLocationEntity((GpsLocationEntity) locationEntityConstructor.newInstance(new Object[] {
                    deviceCommunicator, accessibilityElementEntity}));
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
