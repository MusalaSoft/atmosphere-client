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

    private final static String VALID_CSS_QUERY = "[resource_id=12321][checkable=false][content-desc~=word][class=com.musala.bam][class*=com.musala][bounds=[0,2][4,5]][index=2]";

    private final static String EXPECTED_XPATH_QUERY = "//*[@checkable='false'][contains(concat(' ', @content-desc, ' '), ' word ')][@class='com.musala.bam'][contains(@class,'com.musala')][@bounds='[0,2][4,5]'][@index='2']";

    private final static String INVALID_CSS_QUERY = "[resource-id=2]/s[index=1]";

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
