package com.musala.atmosphere.client.entity;

import com.musala.atmosphere.client.DeviceCommunicator;
import com.musala.atmosphere.client.entity.annotations.Default;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.exceptions.CommandFailedException;

/**
 * Entity responsible for setting the Allow mock locations option on devices
 * which support setting it via the AppOpsManager (above API level 22).
 *
 * @see <a href="https://developer.android.com/reference/android/app/AppOpsManager.html">AppOpsManager</a>
 *
 * @author dimcho.nedev
 *
 */
@Default
public class MockLocationSettingsAppOpsManagerEntity extends MockLocationSettingsEntity {
    private static final String SET_LOCATION_COMMAND = "appops set %s android:mock_location %s";

    private static final String ALLOW_MOCK = "allow";

    private static final String DENY_MOCK = "deny";

    MockLocationSettingsAppOpsManagerEntity(DeviceCommunicator communicator,
            AccessibilityElementEntity elementEntity,
            HardwareButtonEntity hardwareButtonEntity) {
        super(communicator, elementEntity, hardwareButtonEntity);
    }

    @Override
    protected boolean setAllowMockLocationState(String packageName, boolean state) {
        try {
            if (state) {
                this.allowMockLocations(packageName);
            } else {
                this.denyMockLocations(packageName);
            }
        } catch (CommandFailedException e) {
            LOGGER.error("Mocking action failed.", e);
            return false;
        }

        return true;
    }

    @Override
    public boolean isAllowMockLocationsEnabled() {
        // TODO: This method is still not implemented for API level > 22. Add implementation here.
        throw new UnsupportedOperationException("The required operation is not supported.");
    }

    /**
     * Sends a request to the ATMOSPHERE service to allow mock location for a specific app with the given package name.
     *
     * @param args
     *        - a {@link String} representing the package name
     * @throws CommandFailedException
     *         thrown when allowing the mock fails, e.g. communication with the service fails
     */
    private void allowMockLocations(String appPackageName) throws CommandFailedException {
        communicator.sendAction(RoutingAction.EXECUTE_SHELL_COMMAND,
                                String.format(SET_LOCATION_COMMAND, appPackageName, ALLOW_MOCK));
    }

    /**
     * Sends a request to the ATMOSPHERE service to deny mock location for a specific app with the given package name.
     *
     * @param args
     *        - a {@link String} representing the package name
     * @throws CommandFailedException
     *         thrown when deny the mock fails, e.g. communication with the service fails
     */
    private void denyMockLocations(String appPackageName) throws CommandFailedException {
        communicator.sendAction(RoutingAction.EXECUTE_SHELL_COMMAND,
                                String.format(SET_LOCATION_COMMAND, appPackageName, DENY_MOCK));
    }
}