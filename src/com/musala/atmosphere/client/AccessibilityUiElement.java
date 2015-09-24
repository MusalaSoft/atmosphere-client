package com.musala.atmosphere.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import com.musala.atmosphere.client.entity.DeviceSettingsEntity;
import com.musala.atmosphere.client.entity.GestureEntity;
import com.musala.atmosphere.client.entity.ImeEntity;
import com.musala.atmosphere.client.exceptions.InvalidCssQueryException;
import com.musala.atmosphere.client.uiutils.CssToXPathConverter;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.exceptions.UiElementFetchingException;
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
    private static final Logger LOGGER = Logger.getLogger(AccessibilityUiElement.class);

    protected AccessibilityUiElement(AccessibilityElement properties,
            Device device,
            GestureEntity gestureEntity,
            ImeEntity imeEntity,
            DeviceSettingsEntity settingsEntity) {
        super(properties, device, gestureEntity, imeEntity, settingsEntity);
    }

    AccessibilityUiElement(UiElement uiElement) {
        super(uiElement);
    }

    @Override
    public List<UiElement> getChildren(UiElementSelector childrenSelector) throws UiElementFetchingException {
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
    public List<UiElement> getDirectChildren() throws UiElementFetchingException {
        return getDirectChildren(new UiElementSelector());
    }

    @Override
    public List<UiElement> getDirectChildren(UiElementSelector childrenSelector) throws UiElementFetchingException {
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
                                                   boolean visibleNodesOnly) throws UiElementFetchingException {
        List<AccessibilityElement> children = (List<AccessibilityElement>) communicator.sendAction(RoutingAction.GET_CHILDREN,
                                                                                                   accessibilityElement,
                                                                                                   selector,
                                                                                                   directChildrenOnly,
                                                                                                   visibleNodesOnly);

        if (children.isEmpty()) {
            throw new UiElementFetchingException("No elements found matching the given selector.");
        }

        return children;
    }

    /**
     * Gets a list with all UI element's children present on the {@link Screen active screen} and matching the given
     * xpath query.
     *
     * @param xpathQuery
     *        - contains the matching criteria
     * @param visibleOnly
     *        - <code>true</code> to search for visible elements only and <code>false</code> to search all elements
     * @return list with all UI element's children present on the screen and matching the given xpath query
     * @throws UiElementFetchingException
     *         if no children matching the given xpath query are found
     */
    @SuppressWarnings("unchecked")
    private List<UiElement> getChildrenByXPath(String xpathQuery, boolean visibleOnly)
        throws UiElementFetchingException {
        AccessibilityElement accessibilityElement = (AccessibilityElement) propertiesContainer;

        List<AccessibilityElement> children = (List<AccessibilityElement>) communicator.sendAction(RoutingAction.EXECUTE_XPATH_QUERY_ON_LOCAL_ROOT,
                                                                                                   xpathQuery,
                                                                                                   visibleOnly,
                                                                                                   accessibilityElement);
        if (children.isEmpty()) {
            throw new UiElementFetchingException("No elements found matching the given xpath query.");
        }

        return wrapAccessibilityElements(children);
    }

    @Override
    public List<UiElement> getChildrenByXPath(String xpathQuery) throws UiElementFetchingException {
        return getChildrenByXPath(xpathQuery, true);
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

    private List<UiElement> wrapAccessibilityElements(List<AccessibilityElement> accessibilityElements) {
        List<UiElement> wrappedElements = new ArrayList<UiElement>();

        for (AccessibilityElement element : accessibilityElements) {
            wrappedElements.add(new AccessibilityUiElement(element,
                                                           onDevice,
                                                           gestureEntity,
                                                           imeEntity,
                                                           settingsEntity));
        }

        return wrappedElements;
    }
}
