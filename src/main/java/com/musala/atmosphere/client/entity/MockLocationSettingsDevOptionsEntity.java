package com.musala.atmosphere.client.entity;

import java.util.List;
import com.musala.atmosphere.client.DeviceCommunicator;
import com.musala.atmosphere.client.UiElement;
import com.musala.atmosphere.client.device.HardwareButton;
import com.musala.atmosphere.client.entity.annotations.Restriction;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.ScrollDirection;
import com.musala.atmosphere.commons.exceptions.UiElementFetchingException;
import com.musala.atmosphere.commons.ui.selector.CssAttribute;
import com.musala.atmosphere.commons.ui.selector.UiElementSelector;

/**
 * {@link MockLocationSettingsEntity} responsible for setting the Allow mock locations option on devices
 * which don't support setting it via the AppOpsManager (below API level 23).
 *
 * @see <a href="https://developer.android.com/reference/android/app/AppOpsManager.html">AppOpsManager</a>
 *
 * @author aleksander.ivanov
 *
 */
@Restriction(apiLevel = {17, 18, 19, 20, 21, 22})
public class MockLocationSettingsDevOptionsEntity extends MockLocationSettingsEntity {
    private static final String START_SETTINGS_ACTIVITY_SHELL_COMMAND = "am start com.android.settings/.DevelopmentSettings";

    private static final String GET_ALLOW_MOCK_LOCATIONS_VALUE_SHELL_COMMAND = "settings get secure mock_location";

    private static final String LIST_VIEW_CLASS = "android.widget.ListView";

    private static final String TEXT_VIEW_CLASS = "android.widget.TextView";

    private static final String LINEAR_LAYOUT_CLASS = "android.widget.LinearLayout";

    private static final String CHECKBOX_CLASS = "android.widget.CheckBox";

    private static final String SWITCH_CLASS = "android.widget.Switch";

    private static final String ALLOW_MOCK_LOCATIONS_TEXT = "Allow mock locations";

    private static final int MAX_SWIPES = 3;

    private static final int MAX_STEPS = 55;

    private static final int WAIT_FOR_ELEMENT_TIMEOUT = 1000;

    MockLocationSettingsDevOptionsEntity(DeviceCommunicator communicator,
            AccessibilityElementEntity elementEntity,
            HardwareButtonEntity hardwareButtonEntity) {
        super(communicator, elementEntity, hardwareButtonEntity);
    }

    @Override
    protected boolean setAllowMockLocationState(String packageName, boolean state) {
        if (isAllowMockLocationsEnabled() == state) {
            return true;
        }

        openDeveloperOptions();
        boolean result = false;
        UiElement allowMockLocationsSwitch = getAllowMockLocationsSwitch();
        if (allowMockLocationsSwitch != null) {
            result = allowMockLocationsSwitch.tap();
        }
        pressHardwareButton(HardwareButton.BACK);
        return result;
    }

    @Override
    public boolean isAllowMockLocationsEnabled() {
        String result = (String) communicator.sendAction(RoutingAction.EXECUTE_SHELL_COMMAND, GET_ALLOW_MOCK_LOCATIONS_VALUE_SHELL_COMMAND);

        // The result is expected to be in the following format: "<int>\r\n", so we get only the first character which represents the setting's value.
        return Character.getNumericValue(result.charAt(0)) > 0 ? true : false;
    }

    /**
     * Returns the Allow mock locations switch.
     *
     * @return an UiElement representing the Allow mock locations switch, or <code>null</code> if the switch could not be fetched.
     */
    private UiElement getAllowMockLocationsSwitch() {
        try {
            UiElement mockLocationsListView = getMockLocationsListView();

            UiElementSelector linearLayoutSelector = createUiElementSelectorByClass(LINEAR_LAYOUT_CLASS);
            UiElementSelector allowMockLocationsElementSelector = createAllowMockLocationsElementSelector();
            List<UiElement> linearLayouts = mockLocationsListView.getDirectChildren(linearLayoutSelector);
            for (UiElement linearLayout : linearLayouts) {
                try {
                    linearLayout.getChildren(allowMockLocationsElementSelector);
                    return getCheckBoxOrSwitch(linearLayout);

                } catch (UiElementFetchingException e) {
                    // The Allow mock locations entry cannot be found in this linear layout, so skip it and check the next one.
                }
            }
        } catch (UiElementFetchingException e) {
            LOGGER.error("Failed to fetch an UI element matching the given selector.",
                         e);
        }

        return null;
    }

    /**
     * Returns the ListView that contains the Allow mock locations entry.
     *
     * @return an UiElement that represents the ListView containing the Allow mock locations entry.
     * @throws UiElementFetchingException
     */
    private UiElement getMockLocationsListView() throws UiElementFetchingException {
        UiElementSelector mockLocationsListViewSelector = createUiElementSelectorByClass(LIST_VIEW_CLASS);
        UiElementSelector debuggingTextViewSelector = createUiElementSelectorByClass(TEXT_VIEW_CLASS);
        UiElementSelector allowMockLocationsSelector = createAllowMockLocationsElementSelector();
        debuggingTextViewSelector.addSelectionAttribute(CssAttribute.TEXT, "Debugging");

        waitForElementLoading();
        UiElement mockLocationsListView = elementEntity.getElements(mockLocationsListViewSelector, true).get(0);

        // Scroll the ListView until the Allow mock locations entry is visible
        for (int i = 0; i < MAX_SWIPES; i++) {
            try {
                mockLocationsListView.getChildren(allowMockLocationsSelector);
                break;
            } catch (UiElementFetchingException e) {
                communicator.sendAction(RoutingAction.SCROLL_TO_DIRECTION,
                                        ScrollDirection.SCROLL_FORWARD,
                                        mockLocationsListViewSelector,
                                        MAX_SWIPES,
                                        MAX_STEPS,
                                        true);
            }
        }

        return mockLocationsListView;
    }

    /**
     * Checks the input LinearLayout for CheckBox or Switch elements and returns the first one found.
     * Note: It is expected only one kind of elements to be found inside the LinearLayout. If there are both
     * CheckBox and Switch elements, the first CheckBox element will always be returned.
     *
     * @param linearLayout
     *        - the LinearLayout that should be examined
     * @return a UiElement representing the CheckBox or Switch element
     * @throws UiElementFetchingException
     */
    private UiElement getCheckBoxOrSwitch(UiElement linearLayout) throws UiElementFetchingException {
        // Check if a CheckBox element is present
        UiElementSelector allowMockLocationsCheckBoxSelector = createUiElementSelectorByClass(CHECKBOX_CLASS);
        try {
            List<UiElement> linearLayoutCheckBoxChildren = linearLayout.getChildren(allowMockLocationsCheckBoxSelector);
            return linearLayoutCheckBoxChildren.get(0);
        } catch (UiElementFetchingException e) {
            // CheckBox element cannot be found, so skip this step.
        }

        // If there is no CheckBox element, check for a Switch element
        UiElementSelector allowMockLocationsSwitchSelector = createUiElementSelectorByClass(SWITCH_CLASS);
        try {
            List<UiElement> linearLayoutSwitchChildren = linearLayout.getChildren(allowMockLocationsSwitchSelector);
            return linearLayoutSwitchChildren.get(0);
        } catch (UiElementFetchingException e) {
            // Switch element also cannot be found.
        }

        return null;
    }

    private UiElementSelector createUiElementSelectorByClass(String className) {
        UiElementSelector uiElementSelector = new UiElementSelector();
        uiElementSelector.addSelectionAttribute(CssAttribute.CLASS_NAME, className);
        return uiElementSelector;
    }

    private UiElementSelector createAllowMockLocationsElementSelector() {
        UiElementSelector allowMockLocationsElementSelector = new UiElementSelector();
        allowMockLocationsElementSelector.addSelectionAttribute(CssAttribute.TEXT, ALLOW_MOCK_LOCATIONS_TEXT);
        return allowMockLocationsElementSelector;
    }

    private void openDeveloperOptions() {
        communicator.sendAction(RoutingAction.EXECUTE_SHELL_COMMAND, START_SETTINGS_ACTIVITY_SHELL_COMMAND);
    }

    private void pressHardwareButton(HardwareButton button) {
        int keyCode = button.getKeycode();

        hardwareButtonEntity.pressButton(keyCode);
    }

    private void waitForElementLoading() {
        try {
            Thread.sleep(WAIT_FOR_ELEMENT_TIMEOUT);
        } catch (InterruptedException e) {
            // Nothing to do here.
        }
    }
}
