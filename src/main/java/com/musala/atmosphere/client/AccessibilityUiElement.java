package com.musala.atmosphere.client;

import java.util.List;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.exceptions.InvalidCssQueryException;
import com.musala.atmosphere.client.uiutils.AccessibilityElementUtils;
import com.musala.atmosphere.client.uiutils.CssToXPathConverter;
import com.musala.atmosphere.commons.exceptions.UiElementFetchingException;
import com.musala.atmosphere.commons.ui.selector.UiElementSelector;
import com.musala.atmosphere.commons.ui.tree.AccessibilityElement;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * {@link UiElement} represented as {@link AccessibilityElement} structure. Containing methods operating with
 * {@link android.view.accessibility.AccessibilityNodeInfo}.
 *
 * @author denis.bialev
 *
 */
public class AccessibilityUiElement extends UiElement {
    private static final Logger LOGGER = Logger.getLogger(AccessibilityUiElement.class);

    protected AccessibilityUiElement(AccessibilityElement properties, AccessibilityElementUtils elementUtils, DeviceCommunicator communicator) {
        super(properties, elementUtils, communicator);
    }

    AccessibilityUiElement(UiElement uiElement, AccessibilityElementUtils elementUtils, DeviceCommunicator communicator) {
        super(uiElement, elementUtils, communicator);
    }

    @Override
    public List<UiElement> getChildren(UiElementSelector childrenSelector) throws UiElementFetchingException {
        AccessibilityElement accessibilityElement = (AccessibilityElement) propertiesContainer;
        boolean directChildrenOnly = false;
        boolean visibleNodesOnly = true;
        List<AccessibilityElement> children = elementUtils.getChildren(accessibilityElement,
                                                                       childrenSelector,
                                                                       directChildrenOnly,
                                                                       visibleNodesOnly);

        return elementUtils.wrapAccessibilityElements(children);
    }

    @Override
    public List<UiElement> getDirectChildren() throws UiElementFetchingException {
        return getDirectChildren(new UiElementSelector());
    }

    @Override
    public List<UiElement> getDirectChildren(UiElementSelector childrenSelector) throws UiElementFetchingException {
        AccessibilityElement accessibilityElement = (AccessibilityElement) propertiesContainer;
        boolean directChildrenOnly = true;
        boolean visibleNodesOnly = true;
        List<AccessibilityElement> children = elementUtils.getChildren(accessibilityElement,
                                                                       childrenSelector,
                                                                       directChildrenOnly,
                                                                       visibleNodesOnly);

        return elementUtils.wrapAccessibilityElements(children);
    }

    @Override
    public boolean revalidate() {
        return elementUtils.revalidate(propertiesContainer);
    }

    @Override
    public List<UiElement> getChildrenByXPath(String xpathQuery) throws UiElementFetchingException {
        return elementUtils.getChildrenByXPath(xpathQuery, true, propertiesContainer);
    }

    @Override
    public List<UiElement> getChildrenByCssQuery(String cssQuery) throws UiElementFetchingException {
        String childRetrievalErrorMessage = String.format("Failed attempt to retrieve children from %s.",
                                                          propertiesContainer.getPackageName());
        String convertedXPathQuery = null;
        try {
            convertedXPathQuery = CssToXPathConverter.convertCssToXPath(cssQuery);
        } catch (InvalidCssQueryException e) {
            LOGGER.error(childRetrievalErrorMessage, e);
        }

        return getChildrenByXPath(convertedXPathQuery);
    }
}
