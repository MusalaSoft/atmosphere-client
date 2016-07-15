package com.musala.atmosphere.client.entity;

import java.util.List;

import com.musala.atmosphere.client.DeviceCommunicator;
import com.musala.atmosphere.client.UiElement;
import com.musala.atmosphere.client.entity.annotations.Restriction;
import com.musala.atmosphere.client.exceptions.MultipleElementsFoundException;
import com.musala.atmosphere.commons.exceptions.UiElementFetchingException;
import com.musala.atmosphere.commons.ui.selector.CssAttribute;
import com.musala.atmosphere.commons.ui.selector.UiElementSelector;

/**
 * {@link GpsLocationEntity} responsible for setting the GPS location state on all Samsung devices.
 *
 * @author yavor.stankov
 *
 */
@Restriction(apiLevel = {17, 18})
public class GpsLocationCheckBoxEntity extends GpsLocationEntity {
    private static final String ANDROID_WIDGET_CHECK_BOX_CLASS_NAME = "android.widget.CheckBox";

    GpsLocationCheckBoxEntity(DeviceCommunicator communicator,
            AccessibilityElementEntity elementEntity) {
        super(communicator, elementEntity);
    }

    @Override
    protected UiElement getChangeStateWidget() throws MultipleElementsFoundException, UiElementFetchingException {
        UiElementSelector checkBoxWidgetSelector = new UiElementSelector();
        checkBoxWidgetSelector.addSelectionAttribute(CssAttribute.CLASS_NAME, ANDROID_WIDGET_CHECK_BOX_CLASS_NAME);

        elementEntity.waitForElementExists(checkBoxWidgetSelector, CHANGE_STATE_WIDGET_TIMEOUT);

        List<UiElement> widgetList = elementEntity.getElements(checkBoxWidgetSelector, true);

        if (!widgetList.isEmpty()) {
            // There are more than one check box on the screen, but only the first one is for setting the GPS location
            // state.
            return widgetList.get(0);
        }

        return null;
    }
}
