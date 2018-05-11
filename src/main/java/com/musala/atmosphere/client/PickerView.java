// This file is part of the ATMOSPHERE mobile testing framework.
// Copyright (C) 2016 MusalaSoft
//
// ATMOSPHERE is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// ATMOSPHERE is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with ATMOSPHERE.  If not, see <http://www.gnu.org/licenses/>.

package com.musala.atmosphere.client;

import java.util.Calendar;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import com.musala.atmosphere.client.exceptions.InvalidCssQueryException;
import com.musala.atmosphere.client.exceptions.MultipleElementsFoundException;
import com.musala.atmosphere.commons.exceptions.UiElementFetchingException;

/**
 * A class representing the Android picker widgets.
 *
 * @author delyan.dimitrov
 *
 */
public abstract class PickerView {
    protected Screen screen;

    /**
     * @param screen
     *        - a instance of the {@link Screen screen} object
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
     *         if no elements are found for the passed query
     * @throws InvalidCssQueryException
     *         if the passed argument is invalid CSS query
     * @throws XPathExpressionException
     *         if the conversion from CSS to XPath is unsuccessful for some reason
     * @throws MultipleElementsFoundException
     *         if multiple elements are found for the passed query
     */
    public abstract boolean setValue(Calendar value)
        throws XPathExpressionException,
            InvalidCssQueryException,
            UiElementFetchingException,
            ParserConfigurationException,
            MultipleElementsFoundException;

    /**
     * Gets the value from the picker into a calendar object. The calendar object returned will have only its time or
     * date set, depending on the picker.
     *
     * @return - a {@link Calendar} object with the value of the picker
     * @throws ParserConfigurationException
     *         if an error with internal XPath configuration occurs
     * @throws UiElementFetchingException
     *         if no elements are found for the passed query
     * @throws InvalidCssQueryException
     *         if the passed argument is invalid CSS query
     * @throws XPathExpressionException
     *         if the conversion from CSS to XPath is unsuccessful for some reason
     * @throws MultipleElementsFoundException
     *         if multiple elements are found for the passed query
     */
    public abstract Calendar getValue()
        throws XPathExpressionException,
            InvalidCssQueryException,
            UiElementFetchingException,
            ParserConfigurationException,
            MultipleElementsFoundException;

    /**
     * Gets the value from the picker into a String object.
     *
     * @return String object with the picker's value
     * @throws ParserConfigurationException
     *         if an error with internal XPath configuration occurs
     * @throws UiElementFetchingException
     *         if no elements are found for the passed query
     * @throws InvalidCssQueryException
     *         if the passed argument is invalid CSS query
     * @throws XPathExpressionException
     *         if the conversion from CSS to XPath is unsuccessful for some reason
     * @throws MultipleElementsFoundException
     *         if more than one element is found for the passed query
     */
    public abstract String getStringValue()
        throws XPathExpressionException,
            InvalidCssQueryException,
            UiElementFetchingException,
            ParserConfigurationException,
            MultipleElementsFoundException;
}
