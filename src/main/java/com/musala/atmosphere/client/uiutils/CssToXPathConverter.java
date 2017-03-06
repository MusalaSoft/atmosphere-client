package com.musala.atmosphere.client.uiutils;

import static com.musala.atmosphere.client.uiutils.XPathAttribute.isAttributeStringOfTheEnumeration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableMap;
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

    private final static String XPATH_QUERY_FORMATTER = "//*%s";

    private final static String SPLIT_NODE_REGEX = " > ";

    private final static String INITIAL_NODE_FORMATTER = "%s/%s";

    private static final Logger LOGGER = Logger.getLogger(CssToXPathConverter.class);

    private static final Map<String, String> cssToXPathAttributeConversionMap = ImmutableMap.of("class", "className",
                                                                                                "content-desc", "contentDesc",
                                                                                                "long-clickable", "longClickable",
                                                                                                "resource-id", "resourceId");

    /**
     * Divides the CSS Query to property selectors
     *
     * @param cssQuery
     *        - a CSS query to be divided.
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
     *        - a CssQuery which will be checked if it is valid.
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
     * Validates the provided attribute name and returns the correct value.
     *
     * @param attributeName
     *        - the attribute name which will be validated
     * @return the validated attribute name
     * @throws InvalidCssQueryException
     *         if the given attribute name is invalid
     */
    private static String validateXPathAttributeName(String attributeName) {
        if (isAttributeStringOfTheEnumeration(attributeName)) {
            return attributeName;
        }

        if (!cssToXPathAttributeConversionMap.containsKey(attributeName)) {
            throw new InvalidCssQueryException("The provided attribute name is not valid.");
        }

        return cssToXPathAttributeConversionMap.get(attributeName);
    }

    /**
     * Converts the initial node query of a CSS query to that of an XPath query
     *
     * @param separatedInitialNode
     *        - the separated initial node that needs to be converted
     * @return the converted XPath initial node
     */
    public static String convertInitialNode(String separatedInitialNode) {
        if (separatedInitialNode.length() == 0)
            return "";

        String[] initialNodeParts = separatedInitialNode.split(SPLIT_NODE_REGEX);
        String initialNode = "/";
        for (String initialNodePart : initialNodeParts) {
            initialNode = String.format(INITIAL_NODE_FORMATTER, initialNode, initialNodePart);
        }

        return initialNode;
    }

    /**
     * Converts the provided CSS selector to XPath and returns the XPath query with the new selector added.
     *
     * @param attributeNameAndSelectionExpression
     *        - an array of the selector's attribute name and selection expression
     * @param formatter
     *        - the formatter to be used with the new selector
     * @param xpathQuery
     *        - the XPath query to which the new selector should be added to
     * @return the XPath query with the new selector added if the selector's attribute name was valid, or the provided XPath query
     *         without modification otherwise
     */
    private static String addSelector(String[] attributeNameAndSelectionExpression, String formatter, String xpathQuery) {
        try {
            String attributeName = validateXPathAttributeName(attributeNameAndSelectionExpression[0]);
            String selectionExpression = attributeNameAndSelectionExpression[1];
            xpathQuery = String.format(formatter, xpathQuery, attributeName, selectionExpression);
        } catch (InvalidCssQueryException e) {
            // Skip this attribute
        }

        return xpathQuery;
    }

    /**
     * The method converts a given CSS query to an equivalent XPath query
     *
     * @param cssQuery
     *        - a CSS Query which will be converted into XPath query.
     * @return An XPath query resulted from the conversion of the CSS query
     * @throws InvalidCssQueryException
     *         if the given CssQuery is invalid or does not contains the needed requirements for the regex to work.
     */
    public static String convertCssToXPath(String cssQuery) throws InvalidCssQueryException {
        // separates the initial node from the attributes query
        String separatedInitialNode = null;
        int indexOfSeparation;
        for (indexOfSeparation = 0; indexOfSeparation < cssQuery.length(); indexOfSeparation++) {
            if (cssQuery.charAt(indexOfSeparation) == '[') {
                break;
            }
        }
        separatedInitialNode = cssQuery.substring(0, indexOfSeparation);
        cssQuery = cssQuery.substring(indexOfSeparation, cssQuery.length());

        if (!isCssQueryValid(cssQuery)) {
            throw new InvalidCssQueryException("The given CSS query is not valid.");
        }

        String xpathQuery = convertInitialNode(separatedInitialNode);
        List<String> dividedCssQuery = divideCssQuery(cssQuery);
        String[] attributeNameAndSelectionExpression = new String[2];

        for (String partOfCssQuery : dividedCssQuery) {
            if (partOfCssQuery.matches(REGEX_FOR_CSS_QUERY_WITH_CONTAINS)) {
                attributeNameAndSelectionExpression = partOfCssQuery.split("\\*=");
                xpathQuery = addSelector(attributeNameAndSelectionExpression, CONTAINS_FORMATTER, xpathQuery);
            } else if (partOfCssQuery.matches(REGEX_FOR_CSS_QUERY_WITH_WORD_MATCH)) {
                attributeNameAndSelectionExpression = partOfCssQuery.split("~=");
                xpathQuery = addSelector(attributeNameAndSelectionExpression, WORD_MATCH_FORMATTER, xpathQuery);
            } else if (partOfCssQuery.matches(REGEX_FOR_CSS_QUERY_WITH_EQUAL)) {
                attributeNameAndSelectionExpression = partOfCssQuery.split("=");
                xpathQuery = addSelector(attributeNameAndSelectionExpression, EQUAL_FORMATTER, xpathQuery);
            } else {
                String message = "Converting Css to xPath query failed.";
                LOGGER.error(message);
                throw new InvalidCssQueryException(message);
            }
        }

        if (!xpathQuery.isEmpty() && xpathQuery.charAt(0) == '[')
            xpathQuery = String.format(XPATH_QUERY_FORMATTER, xpathQuery);

        return xpathQuery.toString();
    }
}
