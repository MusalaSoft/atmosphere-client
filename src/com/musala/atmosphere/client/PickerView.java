package com.musala.atmosphere.client;

import java.util.Calendar;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import com.musala.atmosphere.client.exceptions.InvalidCssQueryException;
import com.musala.atmosphere.client.exceptions.UiElementFetchingException;

/**
 * A class representing the Android picker widgets.
 * 
 * @author delyan.dimitrov
 * 
 */
public abstract class PickerView {
    protected Screen screen;

    /**
     * @param communicator
     *        - the device communicator used to send actions to the device
     */
    public PickerView(Screen screen) {
        this.screen = screen;
    }

    /**
     * Sets the value in the passed {@link Calendar} in the picker it represents.
     * 
     * @param value
     *        - the value used to set the picker
     * 
     * @return true if the value was set successfully, false otherwise
     * @throws ParserConfigurationException
     *         if an error with internal XPath configuration occurs
     * @throws UiElementFetchingException
     *         if no elements or more than one are found for the passed query
     * @throws InvalidCssQueryException
     *         if the passed argument is invalid CSS query
     * @throws XPathExpressionException
     *         if the conversion from CSS to XPath is unsuccessful for some reason
     */
    public abstract boolean setValue(Calendar value)
        throws XPathExpressionException,
            InvalidCssQueryException,
            UiElementFetchingException,
            ParserConfigurationException;

    /**
     * Gets the value from the picker into a calendar object.
     * 
     * @note The calendar object returned will have only its time or date set, depending on the picker.
     * 
     * @return - a {@link Calendar} object with the value of the picker
     * @throws ParserConfigurationException
     *         if an error with internal XPath configuration occurs
     * @throws UiElementFetchingException
     *         if no elements or more than one are found for the passed query
     * @throws InvalidCssQueryException
     *         if the passed argument is invalid CSS query
     * @throws XPathExpressionException
     *         if the conversion from CSS to XPath is unsuccessful for some reason
     */
    public abstract Calendar getValue()
        throws XPathExpressionException,
            InvalidCssQueryException,
            UiElementFetchingException,
            ParserConfigurationException;

    /**
     * Gets the value from the picker into a String object.
     * 
     * @return String object with the picker's value
     * @throws ParserConfigurationException
     *         if an error with internal XPath configuration occurs
     * @throws UiElementFetchingException
     *         if no elements or more than one are found for the passed query
     * @throws InvalidCssQueryException
     *         if the passed argument is invalid CSS query
     * @throws XPathExpressionException
     *         if the conversion from CSS to XPath is unsuccessful for some reason
     */
    public abstract String getStringValue()
        throws XPathExpressionException,
            InvalidCssQueryException,
            UiElementFetchingException,
            ParserConfigurationException;
}
