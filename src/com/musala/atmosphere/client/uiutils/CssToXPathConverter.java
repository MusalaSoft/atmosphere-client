package com.musala.atmosphere.client.uiutils;

import static com.musala.atmosphere.client.uiutils.XPathAttribute.isAttributeStringOfTheEnumeration;

import java.util.ArrayList;
import java.util.List;

import com.musala.atmosphere.client.exceptions.InvalidCssQueryException;

/**
 * A class for converting a CSS query to XPath query
 * 
 * @author simeon.ivanov
 * 
 */
public class CssToXPathConverter {

    // TODO the selection options can be later extracted to a new Enumeration class or added to an existing one if they
    // become too many or are needed in a class different from the converter

    private final static String REGEX_FOR_CSS_QUERY_WITH_EQUAL = ".+=.+";

    private final static String REGEX_FOR_CSS_QUERY_WITH_CONTAINS = ".+\\*=.+";

    private final static String REGEX_FOR_CSS_QUERY_WITH_WORD_MATCH = ".+~=.+";

    private final static String CONTAINS_FORMATTER = "%s[contains(@%s,'%s')]";

    private final static String WORD_MATCH_FORMATTER = "%s[contains(concat(' ', @%s, ' '), ' %s ')]";

    private final static String EQUAL_FORMATTER = "%s[@%s='%s']";

    /**
     * Divides the CSS Query to property selectors
     * 
     * @param cssQuery
     *        - a CSS query
     * @return A list consisting of the divided CSS Query
     */
    private static List<String> divideCssQuery(String cssQuery) {
        // TODO there must be a better implementation with regex, this should be researched
        int numberOfBrackets = 0, lastDivisionIndex = -1;
        List<String> dividedCssQuery = new ArrayList<String>();

        for (int i = 0; i < cssQuery.length(); i++) {
            if (cssQuery.charAt(i) == '[')
                numberOfBrackets++;
            else if (cssQuery.charAt(i) == ']') {
                numberOfBrackets--;

                if (numberOfBrackets == 0) {
                    dividedCssQuery.add(cssQuery.substring(lastDivisionIndex + 2, i));
                    lastDivisionIndex = i;
                }
            }
        }

        return dividedCssQuery;
    }

    /**
     * Check if the given CSS Query is valid
     * 
     * @param cssQuery
     *        - a CSS query
     * @return true if the CSS query is valid, false if it is not
     */
    private static boolean isCssQueryValid(String cssQuery) {
        // TODO there must be a better implementation with regex, this should be researched
        int numberOfBrackets = 0;
        for (int i = 0; i < cssQuery.length(); i++) {
            if (numberOfBrackets == 0 && cssQuery.charAt(i) != '[')
                return false;
            if (cssQuery.charAt(i) == '[')
                numberOfBrackets++;
            else if (cssQuery.charAt(i) == ']')
                numberOfBrackets--;
        }

        return true;
    }

    /**
     * The method converts a given CSS query to an equivalent XPath query
     * 
     * @param cssQuery
     *        - a CSS Query
     * @return An XPath query resulted from the conversion of the CSS query
     * @throws InvalidCssQueryException
     */
    public static String convertCssToXPath(String cssQuery) throws InvalidCssQueryException {
        if (!isCssQueryValid(cssQuery)) {
            throw new InvalidCssQueryException("The given CSS query is not valid.");
        }

        String xpathQuery = "";

        List<String> dividedCssQuery = divideCssQuery(cssQuery);
        String[] attributeNameAndSelectionExpression = new String[2];
        String attributeName = null;
        String selectionExpression = null;

        for (String partOfCssQuery : dividedCssQuery) {
            if (partOfCssQuery.matches(REGEX_FOR_CSS_QUERY_WITH_CONTAINS)) {
                attributeNameAndSelectionExpression = partOfCssQuery.split("\\*=");

                attributeName = attributeNameAndSelectionExpression[0];
                selectionExpression = attributeNameAndSelectionExpression[1];

                if (isAttributeStringOfTheEnumeration(attributeName)) {
                    xpathQuery = String.format(CONTAINS_FORMATTER, xpathQuery, attributeName, selectionExpression);
                }

            } else if (partOfCssQuery.matches(REGEX_FOR_CSS_QUERY_WITH_WORD_MATCH)) {
                attributeNameAndSelectionExpression = partOfCssQuery.split("~=");

                attributeName = attributeNameAndSelectionExpression[0];
                selectionExpression = attributeNameAndSelectionExpression[1];

                if (isAttributeStringOfTheEnumeration(attributeName)) {
                    xpathQuery = String.format(WORD_MATCH_FORMATTER, xpathQuery, attributeName, selectionExpression);
                }

            } else if (partOfCssQuery.matches(REGEX_FOR_CSS_QUERY_WITH_EQUAL)) {
                attributeNameAndSelectionExpression = partOfCssQuery.split("=");

                attributeName = attributeNameAndSelectionExpression[0];
                selectionExpression = attributeNameAndSelectionExpression[1];

                if (isAttributeStringOfTheEnumeration(attributeName)) {
                    xpathQuery = String.format(EQUAL_FORMATTER, xpathQuery, attributeName, selectionExpression);
                }
            } else {
                throw new InvalidCssQueryException();
            }
        }

        return xpathQuery.toString();
    }
}
