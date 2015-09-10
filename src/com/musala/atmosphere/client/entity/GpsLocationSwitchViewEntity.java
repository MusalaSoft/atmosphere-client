package com.musala.atmosphere.client.entity;

import com.musala.atmosphere.client.DeviceCommunicator;
import com.musala.atmosphere.client.Screen;
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
    private static final String ANDROID_WIDGET_SWITCH_CLASS_NAME = "android.widget.Switch";

    GpsLocationSwitchViewEntity(Screen screen, DeviceCommunicator communicator) {
        super(screen, communicator);
    }

    @Override
    protected UiElement getChangeStateWidget() throws MultipleElementsFoundException, UiElementFetchingException {
        UiElementSelector switchWidgetSelector = new UiElementSelector();
        switchWidgetSelector.addSelectionAttribute(CssAttribute.CLASS_NAME, ANDROID_WIDGET_SWITCH_CLASS_NAME);

        return screen.getElement(switchWidgetSelector);
    }
}
