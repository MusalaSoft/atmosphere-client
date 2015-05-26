package com.musala.atmosphere.client;

import java.util.List;

import com.musala.atmosphere.commons.ui.selector.UiElementSelector;
import com.musala.atmosphere.commons.ui.tree.AccessibilityElement;

/**
 * {@link UiElement} represented as {@link AccessibilityElement} structure. Containing methods operating with
 * {@link AccessibilityNodeInfo}.
 * 
 * @author denis.bialev
 *
 */
public class AccessibilityUiElement extends UiElement {

    protected AccessibilityUiElement(AccessibilityElement properties, Device device) {
        super(properties, device);
    }

    @Override
    public List<UiElement> getChildren(UiElementSelector childrenSelector) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<UiElement> getDirectChildren() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean revalidate() {
        // TODO Auto-generated method stub
        return false;
    }

}
