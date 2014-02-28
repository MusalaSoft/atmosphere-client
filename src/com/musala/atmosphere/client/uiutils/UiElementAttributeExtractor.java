package com.musala.atmosphere.client.uiutils;

import com.musala.atmosphere.commons.ui.UiElementDescriptor;

/**
 * Houses {@link #extract(UiElementSelectorTest)}.
 * 
 * @author georgi.gaydarov
 * 
 */
public class UiElementAttributeExtractor {
    /**
     * Extracts the attributes from a {@link UiElementSelector} instance to a simpler data container - the
     * {@link UiElementDescriptor}.
     * 
     * @param selector
     *        - selector instance.
     * @return a populated with the selector's attributes {@link UiElementDescriptor} instance.
     */
    public static UiElementDescriptor extract(UiElementSelector selector) {
        UiElementDescriptor descriptor = new UiElementDescriptor();
        descriptor.setCheckable(selector.getBooleanValue(CssAttribute.CHECKABLE));
        descriptor.setChecked(selector.getBooleanValue(CssAttribute.CHECKED));
        descriptor.setClassName(selector.getStringValue(CssAttribute.CLASS_NAME));
        descriptor.setClickable(selector.getBooleanValue(CssAttribute.CLICKABLE));
        descriptor.setContentDescription(selector.getStringValue(CssAttribute.CONTENT_DESCRIPTION));
        descriptor.setEnabled(selector.getBooleanValue(CssAttribute.ENABLED));
        descriptor.setFocusable(selector.getBooleanValue(CssAttribute.FOCUSABLE));
        descriptor.setFocused(selector.getBooleanValue(CssAttribute.FOCUSED));
        descriptor.setIndex(selector.getIntegerValue(CssAttribute.INDEX));
        descriptor.setLongClickable(selector.getBooleanValue(CssAttribute.LONG_CLICKABLE));
        descriptor.setPackageName(selector.getStringValue(CssAttribute.PACKAGE_NAME));
        descriptor.setScrollable(selector.getBooleanValue(CssAttribute.SCROLLABLE));
        descriptor.setSelected(selector.getBooleanValue(CssAttribute.SELECTED));
        descriptor.setText(selector.getStringValue(CssAttribute.TEXT));
        return descriptor;
    }
}
