package com.musala.atmosphere.client.entity;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.DeviceCommunicator;

/**
 * Base entity responsible for changing the Allow mock locations option in Developer options on the device.
 *
 * @author aleksander.ivanov
 *
 */
public abstract class MockLocationSettingsEntity {
    protected static final Logger LOGGER = Logger.getLogger(MockLocationSettingsEntity.class.getCanonicalName());

    protected AccessibilityElementEntity elementEntity;

    protected DeviceCommunicator communicator;

    protected HardwareButtonEntity hardwareButtonEntity;

    MockLocationSettingsEntity(DeviceCommunicator communicator,
            AccessibilityElementEntity elementEntity,
            HardwareButtonEntity hardwareButtonEntity) {
        this.communicator = communicator;
        this.elementEntity = elementEntity;
        this.hardwareButtonEntity = hardwareButtonEntity;
    }

    protected abstract boolean setAllowMockLocationState(String packageName, boolean state);

    /**
     * Enables mocking the location in Developer options.
     *
     * @param packageName
     *        - the package name of the application that should be allowed to mock the location
     * @return <code>true</code> if enabled successfully, <code>false</code> otherwise
     */
    public boolean enableMockLocations(String packageName) {
        return setAllowMockLocationState(packageName, true);
    }

    /**
     * Disables mocking the location in Developer options.
     *
     * @param packageName
     *        - the package name of the application that should be disallowed to mock the location
     * @return <code>true</code> if disabled successfully, <code>false</code> otherwise
     */
    public boolean disableMockLocations(String packageName) {
        return setAllowMockLocationState(packageName, false);
    }

    /**
     * Checks if Allow mock locations is enabled on this device.
     *
     * @return <code>true</code> if it is enabled, <code>false</code> otherwise
     */
    public abstract boolean isAllowMockLocationsEnabled();
}
