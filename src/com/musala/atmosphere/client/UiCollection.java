package com.musala.atmosphere.client;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * {@link UiCollection} is more complex {@link UiElement} that have children in the screen's hierarchical structure. The
 * android widgets that should be represented with this class should extend {@link android.view.ViewGroup}.
 * 
 * @author vladimir.vladimirov
 * 
 */
public class UiCollection extends UiElement {

    UiCollection(Node representingNode, Device onDevice) {
        super(representingNode, onDevice);
    }

    UiCollection(UiElement uiElement) {
        super(uiElement);
    }

    /**
     * Used to get list of children of the given UI Element.
     * 
     * @return List with all the UI elements that inherit from this UI element or empty List if such elements don't
     *         exist.
     */
    public List<UiElement> getDirectChildren() {
        return getChildrenForXPath();
    }

    /**
     * Gets all children of {@link UiCollection}, represented by XPath node.
     * 
     * @return list, containing all {@link UiElements} that directly ascend the current {@link UiCollection}.
     */
    private List<UiElement> getChildrenForXPath() {
        NodeList nodeChildren = representedNodeXPath.getChildNodes();
        List<UiElement> result = new LinkedList<UiElement>();

        for (int i = 0; i < nodeChildren.getLength(); i++) {
            Node childNode = nodeChildren.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                // our node is 'fake' and it's not used in the screen's xml
                continue;
            }
            UiElement childElement = new UiElement(childNode, onDevice);
            result.add(childElement);
        }

        return result;
    }
}
