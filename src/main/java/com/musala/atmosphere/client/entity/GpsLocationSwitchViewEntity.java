package com.musala.atmosphere.client.entity;

import com.musala.atmosphere.client.DeviceCommunicator;
import com.musala.atmosphere.client.UiElement;
import com.musala.atmosphere.client.entity.annotations.Default;
import com.musala.atmosphere.client.exceptions.MultipleElementsFoundException;
import com.musala.atmosphere.commons.exceptions.UiElementFetchingException;
import com.musala.atmosphere.commons.ui.selector.CssAttribute;
import com.musala.atmosphere.commons.ui.selector.UiElementSelector;

/**
 * {@link GpsLocationEntity} responsible for all devices that are using switch widgets for setting the GPS location
 * state.
 *
 * @author yavor.stankov
 *
 */
@Default
public class GpsLocationSwitchViewEntity extends GpsLocationEntity {
    private static final String ANDROID_WIDGET_SWITCH_RESOURCE_ID = "com.android.settings:id/switch_widget";

    GpsLocationSwitchViewEntity(DeviceCommunicator communicator,
            AccessibilityElementEntity elementEntity,
            HardwareButtonEntity hardwareButtonEntity) {
        super(communicator, elementEntity, hardwareButtonEntity);
    }

    @Override
    protected UiElement getChangeStateWidget() throws MultipleElementsFoundException, UiElementFetchingException {
        UiElementSelector switchWidgetSelector = new UiElementSelector();
        switchWidgetSelector.addSelectionAttribute(CssAttribute.RESOURCE_ID, ANDROID_WIDGET_SWITCH_RESOURCE_ID);

        elementEntity.waitForElementExists(switchWidgetSelector, CHANGE_STATE_WIDGET_TIMEOUT);

        return elementEntity.getElement(switchWidgetSelector, true);
    }
}
