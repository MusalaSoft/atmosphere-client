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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.musala.atmosphere.commons.geometry.Bounds;
import com.musala.atmosphere.commons.geometry.Point;
import com.musala.atmosphere.commons.ui.selector.CssAttribute;
import com.musala.atmosphere.commons.ui.selector.UiElementSelectionOption;
import com.musala.atmosphere.commons.ui.selector.UiElementSelector;

/**
 * Tests {@link UiElementSelector}
 * 
 * @author boris.strandjev
 */
public class UiElementSelectorTest {
    private UiElementSelector uiElementSelector;

    @Before
    public void setUp() {
        uiElementSelector = new UiElementSelector();
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSelectionAttributeExceptionTest() {
        uiElementSelector.addSelectionAttribute(CssAttribute.INDEX, UiElementSelectionOption.EQUALS, "text");
    }

    @Test
    public void nodeAttributeMapConstructorTest() {
        Map<String, String> nodeAttributeMap = new HashMap<>();
        nodeAttributeMap.put("bounds", "[10,15][200,100]");
        nodeAttributeMap.put("index", "5");
        nodeAttributeMap.put("content-desc", "my-content");
        nodeAttributeMap.put("text", "my-text");
        nodeAttributeMap.put("long-clickable", "true");
        nodeAttributeMap.put("password", "false");
        // Empty strings should be skipped
        nodeAttributeMap.put("package", "");
        String[] expectedExpressions = {"[bounds=[10,15][200,100]]", "[index=5]", "[content-desc=my-content]",
                "[text=my-text]", "[long-clickable=true]", "[password=false]"};
        uiElementSelector = new UiElementSelector(nodeAttributeMap);
        String query = uiElementSelector.buildCssQuery();
        assertCssQuery(query, expectedExpressions);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nodeAttributeMapConstructorUnsupportedAttributeTest() {
        Map<String, String> nodeAttributeMap = new HashMap<>();
        nodeAttributeMap.put("my-very-weird-attribute", "value");
        uiElementSelector = new UiElementSelector(nodeAttributeMap);
    }

    @Test
    public void constructQueryTest() {
        String[] expectedExpressions = {"[bounds=[10,15][200,100]]", "[index=5]", "[content-desc*=my-content]",
                "[text~=my-text]", "[long-clickable=true]", "[password=false]"};
        Point upperLeft = new Point(10, 15);
        Point lowerRight = new Point(200, 100);
        Bounds bounds = new Bounds(upperLeft, lowerRight);
        uiElementSelector.addSelectionAttribute(CssAttribute.BOUNDS, UiElementSelectionOption.EQUALS, bounds);
        uiElementSelector.addSelectionAttribute(CssAttribute.INDEX, UiElementSelectionOption.EQUALS, 5);
        uiElementSelector.addSelectionAttribute(CssAttribute.CONTENT_DESCRIPTION,
                                                UiElementSelectionOption.CONTAINS,
                                                "my-content");
        uiElementSelector.addSelectionAttribute(CssAttribute.TEXT, UiElementSelectionOption.WORD_MATCH, "my-text");
        uiElementSelector.addSelectionAttribute(CssAttribute.LONG_CLICKABLE, UiElementSelectionOption.EQUALS, true);
        uiElementSelector.addSelectionAttribute(CssAttribute.PASSWORD, UiElementSelectionOption.EQUALS, false);
        // Empty strings should be skipped
        uiElementSelector.addSelectionAttribute(CssAttribute.PACKAGE_NAME, UiElementSelectionOption.EQUALS, "");
        String query = uiElementSelector.buildCssQuery();
        assertCssQuery(query, expectedExpressions);
    }

    @Test
    public void getBooleanValueTest() {
        assertNull("Expected the attribute to not be set", uiElementSelector.getBooleanValue(CssAttribute.PASSWORD));
        assertNull("Expected the attribute to not be set", uiElementSelector.getBooleanValue(CssAttribute.CHECKABLE));

        uiElementSelector.addSelectionAttribute(CssAttribute.CHECKABLE, UiElementSelectionOption.EQUALS, true);
        uiElementSelector.addSelectionAttribute(CssAttribute.PASSWORD, UiElementSelectionOption.EQUALS, false);

        assertTrue("Expected the attribute be set", uiElementSelector.getBooleanValue(CssAttribute.CHECKABLE));
        assertFalse("Expected the attribute be set", uiElementSelector.getBooleanValue(CssAttribute.PASSWORD));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getBooleanAttributeValueExceptionTest() {
        uiElementSelector.addSelectionAttribute(CssAttribute.CHECKABLE, UiElementSelectionOption.EQUALS, "true");
    }

    @Test
    public void getStringValueTest() {
        assertNull("Expected the attribute to not be set", uiElementSelector.getBooleanValue(CssAttribute.CLASS_NAME));

        uiElementSelector.addSelectionAttribute(CssAttribute.CLASS_NAME,
                                                UiElementSelectionOption.WORD_MATCH,
                                                "my-class-name");

        assertEquals("Expected the attribute be set and have expected value",
                     "my-class-name",
                     uiElementSelector.getStringValue(CssAttribute.CLASS_NAME));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getStringValueExceptionTest() {
        uiElementSelector.addSelectionAttribute(CssAttribute.CLASS_NAME, UiElementSelectionOption.EQUALS, true);
    }

    public void getIntegerAttributeValueTest() {
        assertNull("Expected the attribute to not be set", uiElementSelector.getBooleanValue(CssAttribute.INDEX));

        uiElementSelector.addSelectionAttribute(CssAttribute.INDEX, UiElementSelectionOption.EQUALS, 5);

        assertEquals("Expected the attribute be set and have expected value",
                     5,
                     uiElementSelector.getStringValue(CssAttribute.INDEX));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getIntegerValueExceptionTest() {
        uiElementSelector.addSelectionAttribute(CssAttribute.INDEX, UiElementSelectionOption.EQUALS, "text");
    }

    public void getBoundsAttributeValueTest() {
        Point upperLeft = new Point(10, 15);
        Point upperLeft2 = new Point(10, 15);
        Point lowerRight = new Point(200, 100);
        Point lowerRight2 = new Point(200, 100);
        Bounds bounds = new Bounds(upperLeft, lowerRight);
        Bounds bounds2 = new Bounds(upperLeft2, lowerRight2);
        assertNull("Expected the attribute to not be set", uiElementSelector.getBooleanValue(CssAttribute.BOUNDS));

        uiElementSelector.addSelectionAttribute(CssAttribute.BOUNDS, UiElementSelectionOption.EQUALS, bounds);

        assertEquals("Expected the attribute be set and have expected value",
                     bounds2,
                     uiElementSelector.getStringValue(CssAttribute.BOUNDS));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getBoundsValueExceptionTest() {
        uiElementSelector.addSelectionAttribute(CssAttribute.BOUNDS, UiElementSelectionOption.EQUALS, "text");
    }

    private void assertCssQuery(String query, String[] expectedExpressions) {
        assertNotNull("Expected the query to be not null", query);
        for (String expectedExpression : expectedExpressions) {
            assertTrue("Expected that the query" + query + " contains the expression " + expectedExpression,
                       query.contains(expectedExpression));
            int lengthBeforeReplacement = query.length();
            query = query.replace(expectedExpression, "");
            assertEquals("Expected the expression " + expectedExpression + " to occur exactly once",
                         expectedExpression.length(),
                         lengthBeforeReplacement - query.length());
        }
        assertTrue("Expected the query to contain nothing else but the expected expressions. However, remaining part: "
                + query, query.isEmpty());
    }

}
