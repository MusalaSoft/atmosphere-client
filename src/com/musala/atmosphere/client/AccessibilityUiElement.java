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

    @Override
    public List<UiElement> getChildren(UiElementSelector childrenSelector) {
        AccessibilityElement accessibilityElement = (AccessibilityElement) propertiesContainer;
        boolean directChildrenOnly = false;
        boolean visibleNodesOnly = true;
        List<AccessibilityElement> children = getChildren(accessibilityElement,
                                                          childrenSelector,
                                                          directChildrenOnly,
                                                          visibleNodesOnly);

        return wrapAccessibilityElements(children);
    }

    @Override
    public List<UiElement> getDirectChildren() {
        return getDirectChildren(new UiElementSelector());
    }

    @Override
    public List<UiElement> getDirectChildren(UiElementSelector childrenSelector) {
        AccessibilityElement accessibilityElement = (AccessibilityElement) propertiesContainer;
        boolean directChildrenOnly = true;
        boolean visibleNodesOnly = true;
        List<AccessibilityElement> children = getChildren(accessibilityElement,
                                                          childrenSelector,
                                                          directChildrenOnly,
                                                          visibleNodesOnly);

        return wrapAccessibilityElements(children);
    }

    @Override
    public boolean revalidate() {

        return (boolean) communicator.sendAction(RoutingAction.CHECK_ELEMENT_PRESENCE, propertiesContainer, true);
    }

    @SuppressWarnings("unchecked")
    private List<AccessibilityElement> getChildren(AccessibilityElement accessibilityElement,
                                                   UiElementSelector selector,
                                                   boolean directChildrenOnly,
                                                   boolean visibleNodesOnly) {
        List<AccessibilityElement> children = (List<AccessibilityElement>) communicator.sendAction(RoutingAction.GET_CHILDREN,
                                                                                                   accessibilityElement,
                                                                                                   selector,
                                                                                                   directChildrenOnly,
                                                                                                   visibleNodesOnly);
        return children;
    }

    private List<UiElement> wrapAccessibilityElements(List<AccessibilityElement> accessibilityElements) {
        List<UiElement> wrappedElements = new ArrayList<UiElement>();

        for (AccessibilityElement element : accessibilityElements) {
            wrappedElements.add(new AccessibilityUiElement(element, onDevice));
        }

        return wrappedElements;
    }
}
