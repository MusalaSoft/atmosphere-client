package com.musala.atmosphere.client;

import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import com.musala.atmosphere.client.device.HardwareButton;
import com.musala.atmosphere.client.exceptions.InvalidCssQueryException;
import com.musala.atmosphere.client.exceptions.UiElementFetchingException;
import com.musala.atmosphere.client.uiutils.CssAttribute;
import com.musala.atmosphere.client.uiutils.CssToXPathConverter;
import com.musala.atmosphere.client.uiutils.UiElementSelector;

/**
 * A class representing a notification bar used to find and interact with notifications.
 * 
 * @author simeon.ivanov
 */
public class NotificationBar {

    private static final String NOTIFICATION_BAR_XPATH_QUERY = "//*[@resource-id='com.android.systemui:id/notification_panel']";

    private static final String CLEAR_ALL_NOTIFICATIONS_BUTTON_XPATH_QUERY = "//*[@content-desc='Clear all notifications.']";

    private Device onDevice = null;

    public NotificationBar(Device onDevice) throws XPathExpressionException, UiElementFetchingException {
        this.onDevice = onDevice;
    }

    /**
     * Opens the notification bar represented by the {@link NotificationBar} class.
     * 
     * @return true if the opening of the notification bar was successful, false otherwise
     */
    public boolean open() {
        return onDevice.openNotificationBar();
    }

    /**
     * Clears all notifications in the notification bar represented by the {@link NotificationBar} class.
     * 
     * @return true if the clearing of the notifications was successful, false otherwise
     * @throws XPathExpressionException
     * @throws UiElementFetchingException
     */
    public boolean clearAllNotifications() throws XPathExpressionException, UiElementFetchingException {
        open();
        Screen deviceActiveScreen = onDevice.getActiveScreen();
        try {
            UiElement clearAllNotificationsButton = deviceActiveScreen.getElementByXPath(CLEAR_ALL_NOTIFICATIONS_BUTTON_XPATH_QUERY);
            return clearAllNotificationsButton.tap();
        } catch (UiElementFetchingException e) {
            onDevice.pressButton(HardwareButton.BACK);
            return true;
        }
    }

    /**
     * Finds a notification in the notification bar that matches the given XPath query.
     * 
     * @param xPathQuery
     *        - the given XPath query that needs to match a notification
     * @return UiElement that matches the found notification
     * @throws XPathExpressionException
     * @throws UiElementFetchingException
     * @throws ParserConfigurationException
     */
    public UiElement getNotificationByXPath(String xPathQuery)
        throws XPathExpressionException,
            UiElementFetchingException,
            ParserConfigurationException {
        open();
        Screen deviceActiveScreen = onDevice.getActiveScreen();
        UiElement notificationBarElement = deviceActiveScreen.getElementByXPath(NOTIFICATION_BAR_XPATH_QUERY);
        List<UiElement> childrenNotifications = notificationBarElement.getChildren(xPathQuery);
        if (childrenNotifications.size() == 0) {
            throw new UiElementFetchingException("No notification matched the passed XPath query.");
        }
        if (childrenNotifications.size() > 1) {
            throw new UiElementFetchingException("More than one notification matched the passed XPath query.");
        }

        return childrenNotifications.get(0);
    }

    /**
     * Finds a notification in the notification bar that matches the given CSS query.
     * 
     * @param cssQuery
     *        - the given CSS query that needs to match a notification
     * @return UiElement that matches the found notification
     * @throws XPathExpressionException
     * @throws InvalidCssQueryException
     * @throws UiElementFetchingException
     * @throws ParserConfigurationException
     */
    public UiElement getNotificationByCssQuery(String cssQuery)
        throws XPathExpressionException,
            InvalidCssQueryException,
            UiElementFetchingException,
            ParserConfigurationException {
        String xPathQuery = CssToXPathConverter.convertCssToXPath(cssQuery);

        return getNotificationByXPath(xPathQuery);
    }

    /**
     * Finds a notification in the notification bar that matches the given selector.
     * 
     * @param selector
     *        - an object of type {@link UiElementSelector} that corresponds to the given CSS selector
     * @return UiElement that matches the found notification
     * @throws XPathExpressionException
     * @throws InvalidCssQueryException
     * @throws UiElementFetchingException
     * @throws ParserConfigurationException
     */
    public UiElement getNotificationBySelector(UiElementSelector selector)
        throws XPathExpressionException,
            InvalidCssQueryException,
            UiElementFetchingException,
            ParserConfigurationException {
        String cssQuery = selector.buildCssQuery();

        return getNotificationByCssQuery(cssQuery);
    }

    /**
     * Finds a notification in the notification bar that matches the given text.
     * 
     * @param text
     *        - the given text that needs to match a notification
     * @return UiElement that matches the found notification
     * @throws XPathExpressionException
     * @throws InvalidCssQueryException
     * @throws UiElementFetchingException
     * @throws ParserConfigurationException
     */
    public UiElement getNotificationByText(String text)
        throws XPathExpressionException,
            InvalidCssQueryException,
            UiElementFetchingException,
            ParserConfigurationException {
        UiElementSelector textSelector = new UiElementSelector();
        textSelector.addSelectionAttribute(CssAttribute.TEXT, text);

        return getNotificationBySelector(textSelector);
    }
}
