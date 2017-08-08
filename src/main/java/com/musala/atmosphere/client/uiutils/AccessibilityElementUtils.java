package com.musala.atmosphere.client.uiutils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.AccessibilityUiElement;
import com.musala.atmosphere.client.DeviceCommunicator;
import com.musala.atmosphere.client.UiElement;
import com.musala.atmosphere.client.exceptions.MultipleElementsFoundException;
import com.musala.atmosphere.client.util.ConfigurationPropertiesLoader;
import com.musala.atmosphere.commons.RoutingAction;
import com.musala.atmosphere.commons.exceptions.UiElementFetchingException;
import com.musala.atmosphere.commons.ui.UiElementPropertiesContainer;
import com.musala.atmosphere.commons.ui.selector.UiElementSelector;
import com.musala.atmosphere.commons.ui.tree.AccessibilityElement;

/**
 * A utility class for operations related to {@link AccessibilityUiElement}.
 *
 * @author yavor.stankov
 *
 */
public class AccessibilityElementUtils {
    private static final Logger LOGGER = Logger.getLogger(AccessibilityElementUtils.class);

    private DeviceCommunicator communicator;

    public AccessibilityElementUtils(DeviceCommunicator communicator) {
        this.communicator = communicator;
    }

    /**
     * Gets a list with all UI elements matching the given selector.
     *
     * @param selector
     *        - contains the matching criteria
     * @param visibleOnly
     *        - <code>true</code> to search for visible elements only and <code>false</code> to search all elements
     * @return list with all UI elements present on the screen and matching the given selector
     * @throws UiElementFetchingException
     *         if no element was found matching the given selector
     */
    @SuppressWarnings("unchecked")
    public List<UiElement> getElements(UiElementSelector selector, Boolean visibleOnly)
            throws UiElementFetchingException {

        int implicitWaitTimeout = ConfigurationPropertiesLoader.getImplicitWaitTimeout();
        if (implicitWaitTimeout != 0) {
            waitForElementExists(selector, implicitWaitTimeout);
        }

        List<AccessibilityElement> foundElements = (List<AccessibilityElement>) communicator.sendAction(RoutingAction.GET_UI_ELEMENTS,
                                                                                                        selector,
                                                                                                        visibleOnly);
        if (foundElements.isEmpty()) {
            throw new UiElementFetchingException("No elements found matching the given selector.");
        }

        return wrapAccessibilityElements(foundElements);
    }

    /**
     * Gets a {@link UiElement} matching the given selector.
     *
     * @param selector
     *        - contains the matching criteria
     * @param visibleOnly
     *        - <code>true</code> to search for visible elements only and <code>false</code> to search all elements
     * @return {@link UiElement} present on the screen and matching the given selector
     * @throws UiElementFetchingException
     *         if no element was found matching the given selector
     * @throws MultipleElementsFoundException
     *         if more than one elements are found matching the given selector
     */
    public UiElement getElement(UiElementSelector selector, Boolean visibleOnly)
            throws UiElementFetchingException,
            MultipleElementsFoundException {
        List<UiElement> uiElements = getElements(selector, visibleOnly);

        if (uiElements.size() > 1) {
            throw new MultipleElementsFoundException("More than one elements are found matching the given selector.");
        }

        return uiElements.get(0);
    }

    /**
     * Checks if the current element is still valid (on the screen) and updates it's attributes container. This is
     * executed before each operation that requires the element to be still present on the screen.
     *
     * @param propertiesContainer
     *        - a container of properties
     * @return true if the current element is still valid, false otherwise
     */
    public boolean revalidate(UiElementPropertiesContainer propertiesContainer) {
        return (boolean) communicator.sendAction(RoutingAction.CHECK_ELEMENT_PRESENCE, propertiesContainer, true);
    }

    /**
     * Gets all child UiElements that match the given {@link UiElementSelector}.
     *
     * @param accessibilityElement
     *        - the parent {@link AccessibilityElement}
     * @param selector
     *        - an object of type {@link UiElementSelector} that needs to match child UI elements
     * @param directChildrenOnly
     *        - <code>true</code> to search for direct children only and <code>false</code> to search all child elements
     * @param visibleNodesOnly
     *        - <code>true</code> to search for visible elements only and <code>false</code> to search all elements
     * @return a list of {@link UiElement} children that match the given selector
     * @throws UiElementFetchingException
     *         if no children matching the given selector are found
     */
    @SuppressWarnings("unchecked")
    public List<AccessibilityElement> getChildren(AccessibilityElement accessibilityElement,
                                                         UiElementSelector selector,
                                                         boolean directChildrenOnly,
                                                         boolean visibleNodesOnly)
                                                                 throws UiElementFetchingException {
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
     * Gets a list with all UI element's children matching the given xpath query.
     *
     * @param xpathQuery
     *        - contains the matching criteria
     * @param visibleOnly
     *        - <code>true</code> to search for visible elements only and <code>false</code> to search all elements
     * @param propertiesContainer
     *        - a container of properties
     * @return list with all UI element's children present on the screen and matching the given xpath query
     * @throws UiElementFetchingException
     *         if no children matching the given xpath query are found
     */
    @SuppressWarnings("unchecked")
    public List<UiElement> getChildrenByXPath(String xpathQuery,
                                              boolean visibleOnly,
                                              UiElementPropertiesContainer propertiesContainer)
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

    /**
     * Wraps all {@link AccessibilityElement accessibility elements} of the given list in {@link UiElement UI elements}.
     *
     * @param accessibilityElements
     *        - the list of elements to be wrapped
     * @return list of {@link UiElement}
     */
    public List<UiElement> wrapAccessibilityElements(List<AccessibilityElement> accessibilityElements) {
        List<UiElement> wrappedElements = new ArrayList<>();

        for (AccessibilityElement element : accessibilityElements) {
            // TODO : After removing the screen move the entities in one package with the AccessibilityUiElement and use
            // directly the constructor.
            try {
                Constructor<?> accessibilityUiElementConstructor = AccessibilityUiElement.class.getDeclaredConstructor(AccessibilityElement.class,
                                                                                                                       AccessibilityElementUtils.class,
                                                                                                                       DeviceCommunicator.class);
                accessibilityUiElementConstructor.setAccessible(true);

                wrappedElements.add((AccessibilityUiElement) accessibilityUiElementConstructor.newInstance(new Object[] {
                        element, this, communicator}));
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                LOGGER.error("Failed to access the AccessibilityUiElement constructor, or the parameters passed to the constructor are illegal"
                        + e);
            }
        }

        return wrappedElements;
    }

    /**
     * Waits for the existence of a given UiElement with a given timeout.
     *
     * @param selector
     *        - the selector of the given UI element.
     * @param timeout
     *        - the given timeout.
     * @return boolean indicating if this action was successful.
     */
    public boolean waitForElementExists(UiElementSelector selector, Integer timeout) {
        boolean response = (boolean) communicator.sendAction(RoutingAction.WAIT_FOR_EXISTS, selector, timeout);

        return response;
    }

    /**
     * Waits until a given UiElement disappears with a given timeout.
     *
     * @param selector
     *        - the selector of the given UI element.
     * @param timeout
     *        - the given timeout.
     * @return boolean indicating if this action was successful.
     */
    public boolean waitUntilElementGone(UiElementSelector selector, Integer timeout) {
        boolean response = (boolean) communicator.sendAction(RoutingAction.WAIT_UNTIL_GONE, selector, timeout);

        return response;
    }

    /**
     * Waits for a window content update event to occur. If a package name for the window is specified, but the current
     * window does not have the same package name, the function returns immediately. The behavior of this method depends
     * on the application that it is used on.
     *
     * @param packageName
     *        - the specified window package name (can be null). If null, a window update from any front-end window will
     *        end the wait
     * @param timeout
     *        - the timeout of the operation
     * @return <code>true</code> if a window update occurred, <code>false</code> if timeout has elapsed or if the
     *         current window does not have the specified package name
     */
    public boolean waitForWindowUpdate(String packageName, int timeout) {
        boolean response = (boolean) communicator.sendAction(RoutingAction.WAIT_FOR_WINDOW_UPDATE,
                                                             packageName,
                                                             timeout);

        return response;
    }
}
