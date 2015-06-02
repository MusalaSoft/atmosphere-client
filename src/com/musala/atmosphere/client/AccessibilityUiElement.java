package com.musala.atmosphere.client;

import java.util.ArrayList;
import java.util.List;

import com.musala.atmosphere.commons.RoutingAction;
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

    @SuppressWarnings("unchecked")
    @Override
    public List<UiElement> getChildren(UiElementSelector childrenSelector) {
        AccessibilityElement accessibilityElement = (AccessibilityElement) propertiesContainer;
        List<AccessibilityElement> children = (List<AccessibilityElement>) communicator.sendAction(RoutingAction.GET_CHILDREN,
                                                                                                   accessibilityElement,
                                                                                                   childrenSelector,
                                                                                                   false,
                                                                                                   true);

        return wrapAccessibilityElements(children);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UiElement> getDirectChildren() {
        AccessibilityElement accessibilityElement = (AccessibilityElement) propertiesContainer;
        List<AccessibilityElement> children = (List<AccessibilityElement>) communicator.sendAction(RoutingAction.GET_CHILDREN,
                                                                                                   accessibilityElement,
                                                                                                   new UiElementSelector(),
                                                                                                   true,
                                                                                                   true);

        return wrapAccessibilityElements(children);
    }

    @Override
    public boolean revalidate() {
        // TODO Auto-generated method stub
        return false;
    }

    private List<UiElement> wrapAccessibilityElements(List<AccessibilityElement> accessibilityElements) {
        List<UiElement> wrappedElements = new ArrayList<UiElement>();

        for (AccessibilityElement element : accessibilityElements) {
            wrappedElements.add(new AccessibilityUiElement(element, onDevice));
        }

        return wrappedElements;
    }
}
