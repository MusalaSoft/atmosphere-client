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

package com.musala.atmosphere.client.util.webview;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.musala.atmosphere.commons.webelement.selection.WebElementSelectionCriterion;

/**
 * Tests {@link WebElementSelectionCriterionConverter}
 * 
 * @author filareta.yordanova
 *
 */
public class WebElementSelectionCriterionConverterTest {
    private static final String XPATH_QUERY_MISSMATCH_ERROR_MESSAGE = "Expected XPath query does not match the converted one.";

    @Test
    public void testConvertTagCriterionToXpath() throws Exception {
        String expectedXpathQuery = "(//button)[2]";
        String actualXPathQuery = WebElementSelectionCriterionConverter.convertToXpathQuery(WebElementSelectionCriterion.TAG,
                                                                                            "button",
                                                                                            2);

        assertEquals(XPATH_QUERY_MISSMATCH_ERROR_MESSAGE, expectedXpathQuery, actualXPathQuery);
    }

    @Test
    public void testConvertNameAttributeCriterionToXpath() throws Exception {
        String expectedXpathQuery = "(//*[@name='button'])[2]";
        String actualXPathQuery = WebElementSelectionCriterionConverter.convertToXpathQuery(WebElementSelectionCriterion.NAME,
                                                                                            "button",
                                                                                            2);

        assertEquals(XPATH_QUERY_MISSMATCH_ERROR_MESSAGE, expectedXpathQuery, actualXPathQuery);

    }

    @Test
    public void testConvertLinkAttributeCriterionToXpath() throws Exception {
        String expectedXpathQuery = "(//*[@href='https://www.google.bg/'])[2]";
        String actualXPathQuery = WebElementSelectionCriterionConverter.convertToXpathQuery(WebElementSelectionCriterion.LINK,
                                                                                            "https://www.google.bg/",
                                                                                            2);

        assertEquals(XPATH_QUERY_MISSMATCH_ERROR_MESSAGE, expectedXpathQuery, actualXPathQuery);
    }

    @Test
    public void testConvertIdAttributeCriterionToXpath() throws Exception {
        String expectedXpathQuery = "(//*[@id='btn'])[1]";
        String actualXPathQuery = WebElementSelectionCriterionConverter.convertToXpathQuery(WebElementSelectionCriterion.ID,
                                                                                            "btn",
                                                                                            1);

        assertEquals(XPATH_QUERY_MISSMATCH_ERROR_MESSAGE, expectedXpathQuery, actualXPathQuery);
    }

    @Test
    public void testConvertClassAttributeCriterionToXpath() throws Exception {
        String expectedXpathQuery = "(//*[@class='clicked'])[1]";
        String actualXPathQuery = WebElementSelectionCriterionConverter.convertToXpathQuery(WebElementSelectionCriterion.CLASS,
                                                                                            "clicked",
                                                                                            1);

        assertEquals(XPATH_QUERY_MISSMATCH_ERROR_MESSAGE, expectedXpathQuery, actualXPathQuery);
    }

    @Test
    public void testConvertPartialLinkCriterion() throws Exception {
        String expectedXpathQuery = "(//*[contains(@href, 'android')])[1]";
        String actualXPathQuery = WebElementSelectionCriterionConverter.convertToXpathQuery(WebElementSelectionCriterion.PARTIAL_LINK,
                                                                                            "android",
                                                                                            1);

        assertEquals(XPATH_QUERY_MISSMATCH_ERROR_MESSAGE, expectedXpathQuery, actualXPathQuery);
    }

    @Test
    public void testConvertXpathCriterion() throws Exception {
        String expectedXpathQuery = "(//button[@class='btn'])[1]";
        String actualXPathQuery = WebElementSelectionCriterionConverter.convertToXpathQuery(WebElementSelectionCriterion.XPATH,
                                                                                            "//button[@class='btn']",
                                                                                            1);

        assertEquals(XPATH_QUERY_MISSMATCH_ERROR_MESSAGE, expectedXpathQuery, actualXPathQuery);
    }

    @Test
    public void testConvertCssSelectorCriterionToXpath() throws Exception {
        String expectedXpathQuery = "(//a[@enabled='true'][contains(@href,'https')][@class='info'])[1]";
        String actualXPathQuery = WebElementSelectionCriterionConverter.convertToXpathQuery(WebElementSelectionCriterion.CSS_SELECTOR,
                                                                                            "a[enabled=true][href*=https][class=info]",
                                                                                            1);

        assertEquals(XPATH_QUERY_MISSMATCH_ERROR_MESSAGE, expectedXpathQuery, actualXPathQuery);
    }
}
