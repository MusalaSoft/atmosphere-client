package com.musala.atmosphere.client.entity;

import com.musala.atmosphere.client.DeviceCommunicator;
import com.musala.atmosphere.client.entity.annotations.Default;

@Default
public class MockLocationSettingsAppOpsManagerEntity extends MockLocationSettingsEntity {

    MockLocationSettingsAppOpsManagerEntity(DeviceCommunicator communicator,
            AccessibilityElementEntity elementEntity,
            HardwareButtonEntity hardwareButtonEntity) {
        super(communicator, elementEntity, hardwareButtonEntity);
    }

    @Override
    protected boolean setAllowMockLocationState(String packageName, boolean state) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isAllowMockLocationsEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

}
