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

package com.musala.atmosphere.client.uiutils;

import static com.musala.atmosphere.client.uiutils.CssToXPathConverter.convertCssToXPath;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.musala.atmosphere.client.exceptions.InvalidCssQueryException;

/**
 * Tests {@link CssToXPathConverter}
 * 
 * @author simeon.ivanov
 */
public class CssToXPathConverterTest {

    private final static String VALID_CSS_QUERY = "[resour_id=12321][checkable=false][contentDesc~=word][className=com.musala.bam][className*=com.musala][bounds=[0,2][4,5]][index=2]";

    private final static String EXPECTED_XPATH_QUERY = "//*[@checkable='false'][contains(concat(' ', @contentDesc, ' '), ' word ')][@className='com.musala.bam'][contains(@className,'com.musala')][@bounds='[0,2][4,5]'][@index='2']";

    private final static String INVALID_CSS_QUERY = "[resourceId=2]/s[index=1]";

    @Test
    public void testConverterWithValidCssQuery() throws InvalidCssQueryException {
        String convertedXPathQuery = convertCssToXPath(VALID_CSS_QUERY);
        assertEquals("The converter did not return the right XPath query", EXPECTED_XPATH_QUERY, convertedXPathQuery);
    }

    @Test(expected = InvalidCssQueryException.class)
    public void testConverterWithInvalidCssQuery() throws InvalidCssQueryException {
        convertCssToXPath(INVALID_CSS_QUERY);
    }
}
