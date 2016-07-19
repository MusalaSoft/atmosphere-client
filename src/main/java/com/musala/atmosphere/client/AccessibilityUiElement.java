package com.musala.atmosphere.client;

import java.util.List;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.entity.AccessibilityElementEntity;
import com.musala.atmosphere.client.entity.ImageEntity;
import com.musala.atmosphere.client.exceptions.InvalidCssQueryException;
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

    // TODO Remove the obsolete constructors when all entities are migrated to the Agent
    @Deprecated
    protected AccessibilityUiElement(AccessibilityElement properties,
            ImageEntity imageEntity,
            AccessibilityElementEntity elementEntity) {
        super(properties, imageEntity, elementEntity);
    }

    @Deprecated
    protected AccessibilityUiElement(AccessibilityElement properties,
            ImageEntity imageEntity,
            AccessibilityElementEntity elementEntity,
            DeviceCommunicator communicator) {
        super(properties, imageEntity, elementEntity);
        this.communicator = communicator;
    }

    @Deprecated
    AccessibilityUiElement(UiElement uiElement) {
        super(uiElement);
    }

    protected AccessibilityUiElement(AccessibilityElement properties, DeviceCommunicator communicator) {
        super(properties, communicator);
    }

    AccessibilityUiElement(UiElement uiElement, DeviceCommunicator communicator) {
        super(uiElement, communicator);
    }

    @Override
    public List<UiElement> getChildren(UiElementSelector childrenSelector) throws UiElementFetchingException {
        AccessibilityElement accessibilityElement = (AccessibilityElement) propertiesContainer;
        boolean directChildrenOnly = false;
        boolean visibleNodesOnly = true;
        List<AccessibilityElement> children = elementEntity.getChildren(accessibilityElement,
                                                                        childrenSelector,
                                                                        directChildrenOnly,
                                                                        visibleNodesOnly);

        return elementEntity.wrapAccessibilityElements(children);
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
        List<AccessibilityElement> children = elementEntity.getChildren(accessibilityElement,
                                                                        childrenSelector,
                                                                        directChildrenOnly,
                                                                        visibleNodesOnly);

        return elementEntity.wrapAccessibilityElements(children);
    }

    @Override
    public boolean revalidate() {
        return elementEntity.revalidate(propertiesContainer);
    }

    @Override
    public List<UiElement> getChildrenByXPath(String xpathQuery) throws UiElementFetchingException {
        return elementEntity.getChildrenByXPath(xpathQuery, true, propertiesContainer);
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
