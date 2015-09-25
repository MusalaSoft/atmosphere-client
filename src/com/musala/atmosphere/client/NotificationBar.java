package com.musala.atmosphere.client;

import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.device.HardwareButton;
import com.musala.atmosphere.client.exceptions.InvalidCssQueryException;
import com.musala.atmosphere.client.exceptions.MultipleElementsFoundException;
import com.musala.atmosphere.client.uiutils.CssToXPathConverter;
import com.musala.atmosphere.commons.exceptions.UiElementFetchingException;
import com.musala.atmosphere.commons.ui.selector.CssAttribute;
import com.musala.atmosphere.commons.ui.selector.UiElementSelector;

/**
 * A class representing a notification bar used to find and interact with notifications.
 * 
 * @author simeon.ivanov
 */
public class NotificationBar {

    private static final String NOTIFICATION_BAR_XPATH_QUERY = "//*[@resourceId='com.android.systemui:id/notification_panel']";

    private static final String CLEAR_ALL_NOTIFICATIONS_BUTTON_XPATH_QUERY = "//*[@contentDesc='Clear all notifications.']";

    private static final String NOTIFICATIONS_RESOURCE_ID_XPATH_QUERY = "//*[@resourceId='android:id/status_bar_latest_event_content']";

    private static final String NO_NOTIFICATION_MESSAGE = "No notification matched the passes XPath query: %s";

    private UiElementSelector notificationBarSelector;

    private static final String NOTIFICATION_BAR_RESOURCE_ID = "com.android.systemui:id/notification_panel";

    private static final Logger LOGGER = Logger.getLogger(NotificationBar.class);

    private static final int OPEN_NOTIFICATION_BAR_TIMEOUT = 5_000;

    private Device onDevice = null;

    public NotificationBar(Device onDevice) throws UiElementFetchingException {
        this.onDevice = onDevice;
        notificationBarSelector = new UiElementSelector();
        notificationBarSelector.addSelectionAttribute(CssAttribute.RESOURCE_ID, NOTIFICATION_BAR_RESOURCE_ID);
    }

    /**
     * Opens the notification bar represented by the {@link NotificationBar} class. This function can only be used on a
     * device with API 18 or higher.
     * 
     * @return true if the opening of the notification bar was successful, false otherwise
     */
    public boolean open() {
        onDevice.openNotificationBar();
        Screen deviceActiveScreen = onDevice.getActiveScreen();
        return deviceActiveScreen.waitForElementExists(notificationBarSelector, OPEN_NOTIFICATION_BAR_TIMEOUT);
    }

    /**
     * Closes the notification bar represented by the {@link NotificationBar} class. This is the only function that can
     * be used on a device with API lower than 18.
     * 
     * @return true if the notification bar was closed successfully, false otherwise
     */
    public boolean close() {
        return onDevice.pressButton(HardwareButton.BACK);
    }

    /**
     * Clears all notifications in the notification bar represented by the {@link NotificationBar} class. This function
     * can only be used on a device with API 18 or higher.
     * 
     * @return true if the clearing of the notifications was successful, false otherwise
     * @throws UiElementFetchingException
     *         if element could not be found
     * @throws XPathExpressionException
     *         if element is searched by invalid XPath query
     */
    public boolean clearAllNotifications() throws UiElementFetchingException {
        open();
        Screen deviceActiveScreen = onDevice.getActiveScreen();

        try {
            UiElement clearAllNotificationsButton = deviceActiveScreen.getElementByXPath(CLEAR_ALL_NOTIFICATIONS_BUTTON_XPATH_QUERY);
            return clearAllNotificationsButton.tap();
        } catch (MultipleElementsFoundException e) {
            LOGGER.error(String.format("Clearing all notifications failed, because multiple elements were found for the XPath query %s",
                                       CLEAR_ALL_NOTIFICATIONS_BUTTON_XPATH_QUERY),
                         e);
            return false;
        }
    }

    /**
     * Finds a notification in the notification bar that matches the given XPath query. This function can only be used
     * on a device with API 18 or higher.
     * 
     * @param xPathQuery
     *        - the given XPath query that needs to match a notification
     * @return UiElement that matches the found notification
     * @throws UiElementFetchingException
     *         - if element could not be found
     * @throws MultipleElementsFoundException
     *         if multiple elements are matching the given query
     */
    public UiElement getNotificationByXPath(String xPathQuery)
        throws MultipleElementsFoundException,
            UiElementFetchingException {
        open();

        // The method first check if there is more than one notification that contains an UI element corresponding to
        // the XPath query, then it finds the the specific full size notification that contains that UI element and
        // returns it. The full size notification contains all UI elements inside it and the UI element we searched for
        // is guaranteed to be in the its children.
        String missingNotificationError = String.format(NO_NOTIFICATION_MESSAGE, xPathQuery);

        try {
            Screen deviceActiveScreen = onDevice.getActiveScreen();
            UiElement notificationBarElement = deviceActiveScreen.getElementByXPath(NOTIFICATION_BAR_XPATH_QUERY);
            List<UiElement> childrenNotifications = notificationBarElement.getChildrenByXPath(xPathQuery);

            if (childrenNotifications.size() > 1) {
                String message = String.format("More than one notification matched the passed XPath query %s.",
                                               xPathQuery);
                LOGGER.error(message);
                throw new MultipleElementsFoundException(message);
            }

            List<UiElement> allNotifications = deviceActiveScreen.getAllElementsByXPath(NOTIFICATIONS_RESOURCE_ID_XPATH_QUERY);
            for (UiElement currentNotification : allNotifications) {
                currentNotification.getChildrenByXPath(xPathQuery);

                return currentNotification;
            }
            LOGGER.error(missingNotificationError);
            throw new UiElementFetchingException(missingNotificationError);
        } catch (UiElementFetchingException e) {
            LOGGER.error(missingNotificationError, e);
            throw new UiElementFetchingException(missingNotificationError, e);
        }
    }

    /**
     * Finds a notification in the notification bar that matches the given CSS query. This function can only be used on
     * a device with API 18 or higher.
     * 
     * @param cssQuery
     *        - the given CSS query that needs to match a notification
     * @return UiElement that matches the found notification
     * @throws InvalidCssQueryException
     *         - if element is searched by invalid CSS query
     * @throws UiElementFetchingException
     *         - if element could not be found
     * @throws MultipleElementsFoundException
     *         if multiple elements are found for the given query
     */
    public UiElement getNotificationByCssQuery(String cssQuery)
        throws InvalidCssQueryException,
            MultipleElementsFoundException,
            UiElementFetchingException {
        String xPathQuery = CssToXPathConverter.convertCssToXPath(cssQuery);

        return getNotificationByXPath(xPathQuery);
    }

    /**
     * Finds a notification in the notification bar that matches the given selector. This function can only be used on a
     * device with API 18 or higher.
     * 
     * @param selector
     *        - an object of type {@link UiElementSelector}
     * @return UiElement that matches the found notification
     * @throws UiElementFetchingException
     *         - if element could not be found
     * @throws MultipleElementsFoundException
     *         if multiple elements are matching the given selector
     */
    public UiElement getNotificationBySelector(UiElementSelector selector)
        throws MultipleElementsFoundException,
            UiElementFetchingException {
        Screen activeScreen = onDevice.getActiveScreen();

        return activeScreen.getElement(selector);
    }

    /**
     * Finds the notifications in the notification bar that are matching the given selector. This function can only be
     * used on a device with API 18 or higher.
     * 
     * @param selector
     *        - an object of type {@link UiElementSelector}
     * @return UiElements that are matching the found notifications
     * @throws UiElementFetchingException
     *         if element could not be found
     */
    public List<UiElement> getNotificationsBySelector(UiElementSelector selector) throws UiElementFetchingException {
        Screen activeScreen = onDevice.getActiveScreen();

        return activeScreen.getElements(selector);
    }

    /**
     * Finds a notification in the notification bar that matches the given text. This function can only be used on a
     * device with API 18 or higher.
     * 
     * @param text
     *        - the given text that needs to match a notification
     * @return UiElement that matches the found notification
     * @throws UiElementFetchingException
     *         - if element could not be found
     * @throws MultipleElementsFoundException
     *         if multiple elements are found by the given text
     */
    public UiElement getNotificationByText(String text)
        throws MultipleElementsFoundException,
            UiElementFetchingException {
        UiElementSelector textSelector = new UiElementSelector();
        textSelector.addSelectionAttribute(CssAttribute.TEXT, text);

        return getNotificationBySelector(textSelector);
    }
}
