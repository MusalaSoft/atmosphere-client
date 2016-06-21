package com.musala.atmosphere.client.uiutils;

import com.musala.atmosphere.commons.ui.UiElementPropertiesContainer;
import com.musala.atmosphere.commons.ui.selector.CssAttribute;
import com.musala.atmosphere.commons.ui.selector.UiElementSelector;

/**
 * Manages the extracting of the attributes from a UiElementPropertiesContainer instance to a UiElementSelector.
 * 
 * @author georgi.gaydarov
 * 
 */
public class UiElementAttributeExtractor {
    /**
     * Extracts the attributes from a {@link UiElementPropertiesContainer} instance to a {@link UiElementSelector}.
     * 
     * @param propertiesContainer
     *        - {@link UiElementPropertiesContainer} instance from which we get the attributes
     * @return a {@link UiElementSelector} instance containing the {@link UiElementPropertiesContainer} attributes
     */
    public static UiElementSelector extract(UiElementPropertiesContainer propertiesContainer) {
        UiElementSelector elementSelector = new UiElementSelector();
        elementSelector.addSelectionAttribute(CssAttribute.CHECKED, propertiesContainer.isChecked());
        elementSelector.addSelectionAttribute(CssAttribute.CHECKABLE, propertiesContainer.isCheckable());
        elementSelector.addSelectionAttribute(CssAttribute.CLASS_NAME, propertiesContainer.getClassName());
        elementSelector.addSelectionAttribute(CssAttribute.CLICKABLE, propertiesContainer.isClickable());
        elementSelector.addSelectionAttribute(CssAttribute.CONTENT_DESCRIPTION, propertiesContainer.getContentDescriptor());
        elementSelector.addSelectionAttribute(CssAttribute.ENABLED, propertiesContainer.isEnabled());
        elementSelector.addSelectionAttribute(CssAttribute.FOCUSABLE, propertiesContainer.isFocusable());
        elementSelector.addSelectionAttribute(CssAttribute.FOCUSED, propertiesContainer.isFocused());
        elementSelector.addSelectionAttribute(CssAttribute.INDEX, propertiesContainer.getIndex());
        elementSelector.addSelectionAttribute(CssAttribute.CLICKABLE, propertiesContainer.isLongClickable());
        elementSelector.addSelectionAttribute(CssAttribute.PACKAGE_NAME, propertiesContainer.getPackageName());
        elementSelector.addSelectionAttribute(CssAttribute.SCROLLABLE, propertiesContainer.isScrollable());
        elementSelector.addSelectionAttribute(CssAttribute.SELECTED, propertiesContainer.isSelected());
        elementSelector.addSelectionAttribute(CssAttribute.TEXT, propertiesContainer.getText());
        elementSelector.addSelectionAttribute(CssAttribute.RESOURCE_ID, propertiesContainer.getResourceId());

        return elementSelector;
    }
}
