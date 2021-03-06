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

import com.musala.atmosphere.client.exceptions.InvalidCssQueryException;
import com.musala.atmosphere.client.uiutils.CssToXPathConverter;
import com.musala.atmosphere.commons.webelement.selection.WebElementSelectionCriterion;

/**
 * Basic utilities for conversions between different {@link WebElementSelectionCriterion criterion types} .
 * 
 * @author filareta.yordanova
 *
 */
public class WebElementSelectionCriterionConverter {
    private static final String PARTIAL_LINK_PATTERN = "//*[contains(@%s, '%s')]";

    private static final String ATTRIBUTE_PATTERN = "//*[@%s='%s']";

    private static final String TAG_PATTERN = "//%s";

    private static final String ELEMENT_AT_INDEX_PATTERN = "(%s)[%d]";

    /**
     * Converts the given {@link WebElementSelectionCriterion selection criterion} to xpath query, appending [index] to
     * the result query.
     * 
     * @param selectionCriterion
     *        - type of the selection criterion
     * @param criterionValue
     *        - value that is used for matching
     * @param index
     *        - index of the element in the list of all available elements in the DOM that can be selected by the given
     *        criterion
     * @return the converted xpath query
     * @throws InvalidCssQueryException
     *         if {@link WebElementSelectionCriterion selection criterion} is set to CSS_SELECTOR and the query is
     *         invalid
     */
    public static String convertToXpathQuery(WebElementSelectionCriterion selectionCriterion,
                                             String criterionValue,
                                             int index) throws InvalidCssQueryException {
        String xpathQuery = convertToXpathQuery(selectionCriterion, criterionValue);

        return xpathQuery != null ? String.format(ELEMENT_AT_INDEX_PATTERN, xpathQuery, index) : null;
    }

    /**
     * Converts the given {@link WebElementSelectionCriterion selection criterion} to xpath query.
     * 
     * @param selectionCriterion
     *        - type of the selection criterion
     * @param criterionValue
     *        - value that is used for matching
     * @return the converted xpath query
     * @throws InvalidCssQueryException
     *         if {@link WebElementSelectionCriterion selection criterion} is set to CSS_SELECTOR and the query is
     *         invalid
     */
    public static String convertToXpathQuery(WebElementSelectionCriterion selectionCriterion, String criterionValue) {
        switch (selectionCriterion) {
            case TAG:
                return String.format(TAG_PATTERN, criterionValue);
            case ID:
            case NAME:
            case CLASS:
            case LINK:
                return String.format(ATTRIBUTE_PATTERN, selectionCriterion.getName(), criterionValue);
            case PARTIAL_LINK:
                return String.format(PARTIAL_LINK_PATTERN, selectionCriterion.getName(), criterionValue);
            case CSS_SELECTOR:
                return CssToXPathConverter.convertCssToXPath(criterionValue);
            case XPATH:
                return criterionValue;
            default:
                return null;
        }
    }
}
