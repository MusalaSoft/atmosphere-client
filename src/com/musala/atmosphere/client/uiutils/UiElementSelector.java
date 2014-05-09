package com.musala.atmosphere.client.uiutils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.musala.atmosphere.client.geometry.Bounds;
import com.musala.atmosphere.commons.util.Pair;

/**
 * Selector class for screen UI elements, used to search for a specific element with given attributes.
 * 
 * @author georgi.gaydarov
 * 
 */
public class UiElementSelector {
    private Map<CssAttribute, Pair<Object, UiElementSelectionOption>> attributeProjectionMap;

    public UiElementSelector() {
        this.attributeProjectionMap = new HashMap<CssAttribute, Pair<Object, UiElementSelectionOption>>();
    }

    /**
     * Constructs ui element selector out of node attribute map.
     * 
     * This is auxiliary constructor needed for some parts of the system. PLease prefer to use
     * {@link #UiElementSelector()}.
     * 
     * @param nodeAttributeMap
     *        a map between the html attribute names and their corresponding values
     * @throws IllegalArgumentException
     *         In case the node attribute map contains an entry with no matching {@link CssAttribute} or if the matching
     *         attribute is of type not handled in {@link #determineAttributeValue(CssAttribute, String)}.
     */
    public UiElementSelector(Map<String, String> nodeAttributeMap) throws IllegalArgumentException {
        this();
        for (Entry<String, String> nodeAttributeEntry : nodeAttributeMap.entrySet()) {
            boolean attributeFound = false;
            for (CssAttribute cssAttribute : CssAttribute.values()) {
                if (cssAttribute.getHtmlAttributeName().equals(nodeAttributeEntry.getKey())) {
                    Object attributeValue = determineAttributeValue(cssAttribute, nodeAttributeEntry.getValue());
                    if (!shouldSkipAttribute(cssAttribute, attributeValue)) {
                        addSelectionAttribute(cssAttribute, UiElementSelectionOption.EQUALS, attributeValue);
                    }
                    attributeFound = true;
                    break;
                }
            }
            if (!attributeFound) {
                throw new IllegalArgumentException("Unsupported attribute passed in to ui element selector constructor");
            }
        }
    }

    /**
     * Adds new selection argument for this ui element selector
     * <p>
     * Example usage would be:
     * <p>
     * <code>
     * uiElementSelector.addSelectionAttribute(SupportedCssAttribute.CHECKABLE, UiElementSelectionOption.EQUALS, true);
     * </code>
     * 
     * @param attribute
     *        The attribute for which to add selection expression. If selection expression already existed for the
     *        attribute it will be replaced.
     * @param selectionOption
     *        The selection option. One of the {@link UiElementSelectionOption}.
     * @param value
     *        The value to be used in the selection expression. Empty strings will not be added as expressions.
     * @throws IllegalArgumentException
     *         In case the supplied value does not match the type of the specified attribute.
     */
    public void addSelectionAttribute(CssAttribute attribute, UiElementSelectionOption selectionOption, Object value)
        throws IllegalArgumentException {
        if (!attribute.isObjectOfAppropriateType(value)) {
            throw new IllegalArgumentException("Invalid attribute value for attribute: " + attribute + " expected "
                    + attribute.getAttributeType() + " but was " + value.getClass());
        }
        if (!shouldSkipAttribute(attribute, value)) {
            attributeProjectionMap.put(attribute, new Pair<Object, UiElementSelectionOption>(value, selectionOption));
        }
    }

    /**
     * Adds new selection argument for this ui element selector with selection option that is
     * {@link UiElementSelectionOption#EQUALS}.
     * <p>
     * Example usage would be:
     * <p>
     * <code>
     * uiElementSelector.addSelectionAttribute(SupportedCssAttribute.CHECKABLE, true);
     * </code>
     *
     * @param attribute
     *        The attribute for which to add selection expression. If selection expression already existed for the
     *        attribute it will be replaced.
     * @param value
     *        The value to be used in the selection expression. Empty strings will not be added as expressions.
     * @throws IllegalArgumentException
     *         In case the supplied value does not match the type of the specified attribute.
     */
    public void addSelectionAttribute(CssAttribute attribute, Object value) {
        addSelectionAttribute(attribute, UiElementSelectionOption.EQUALS, value);
    }

    /**
     * Use the method to get the value of boolean attribute selection argument
     * 
     * @param attribute
     *        The attribute for which to get the selection argument
     * @return The boolean value or null if no selection was specified.
     * @throws IllegalArgumentException
     *         If the attribute selection requested is not for boolean attribute.
     */
    public Boolean getBooleanValue(CssAttribute attribute) {
        return (Boolean) getGenericValue(attribute, Boolean.class);
    }

    /**
     * Use the method to get the value of string attribute selection argument
     * 
     * @param attribute
     *        The attribute for which to get the selection argument
     * @return The string value or null if no selection was specified.
     * @throws IllegalArgumentException
     *         If the attribute selection requested is not for string attribute.
     */
    public String getStringValue(CssAttribute attribute) {
        return (String) getGenericValue(attribute, String.class);
    }

    /**
     * Use the method to get the value of integer attribute selection argument
     * 
     * @param attribute
     *        The attribute for which to get the selection argument
     * @return The integer value or null if no selection was specified.
     * @throws IllegalArgumentException
     *         If the attribute selection requested is not for integer attribute.
     */
    public Integer getIntegerValue(CssAttribute attribute) {
        return (Integer) getGenericValue(attribute, Integer.class);
    }

    /**
     * Use the method to get the value of {@link Bounds} attribute selection argument
     * 
     * @param attribute
     *        The attribute for which to get the selection argument
     * @return The {@link Bounds} value or null if no selection was specified.
     * @throws IllegalArgumentException
     *         If the attribute selection requested is not for {@link Bounds} attribute.
     */
    public Bounds getBoundsValue(CssAttribute attribute) {
        return (Bounds) getGenericValue(attribute, Bounds.class);
    }

    /**
     * Builds a CSS select element query based on the contents of this selector.
     * 
     * @return the built CSS query.
     */
    public String buildCssQuery() {
        StringBuilder builder = new StringBuilder();

        for (Entry<CssAttribute, Pair<Object, UiElementSelectionOption>> attributeProjectionMapEntry : attributeProjectionMap.entrySet()) {
            Object selectionExpression = attributeProjectionMapEntry.getValue().getKey();
            if (selectionExpression != null) {
                CssAttribute attribute = attributeProjectionMapEntry.getKey();
                UiElementSelectionOption selectionOption = attributeProjectionMapEntry.getValue().getValue();
                builder.append(selectionOption.constructAttributeSelector(attribute, selectionExpression));
            }
        }

        return builder.toString();
    }

    private boolean shouldSkipAttribute(CssAttribute attribute, Object value) {
        // Apparently empty strings cause crashes
        return attribute.getAttributeType().equals(String.class) && ((String) value).isEmpty();
    }

    private Object getGenericValue(CssAttribute attribute, Class<?> clazz) {
        if (!attributeProjectionMap.containsKey(attribute)) {
            return null;
        }
        if (attribute.getAttributeType().equals(clazz)) {
            return attributeProjectionMap.get(attribute).getKey();
        } else {
            throw new IllegalArgumentException("Trying to get boolean value of non-boolean attribute " + attribute);
        }
    }

    private Object determineAttributeValue(CssAttribute cssAttribute, String value) {
        if (cssAttribute.getAttributeType().equals(String.class)) {
            return value;
        }
        if (cssAttribute.getAttributeType().equals(Integer.class)) {
            return Integer.parseInt(value);
        }
        if (cssAttribute.getAttributeType().equals(Boolean.class)) {
            return value.equals("true");
        }
        if (cssAttribute.getAttributeType().equals(Bounds.class)) {
            return UiElementBoundsParser.parse(value);
        }
        throw new IllegalArgumentException("Constructing ui element selector with attribute of unsupported type "
                + cssAttribute.getAttributeType());
    }
}
