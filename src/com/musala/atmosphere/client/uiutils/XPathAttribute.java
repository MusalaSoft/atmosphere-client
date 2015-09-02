package com.musala.atmosphere.client.uiutils;

import com.musala.atmosphere.commons.geometry.Bounds;

/**
 * Lists all the XPath attributes the system currently supports.
 * <p>
 * If you need to add a new attribute, you just need to add its value and specify the corresponding data type in the
 * enum.
 * 
 * @author simeon.ivanov
 */
public enum XPathAttribute {
    /** Value denoting the XPath 'bounds' attribute. Type of needed value {@link Bounds}. */
    BOUNDS(Bounds.class, "bounds"),
    /** Value denoting the XPath 'checkable' attribute. Type of needed value Boolean. */
    CHECKABLE(Boolean.class, "checkable"),
    /** Value denoting the XPath 'checked' attribute. Type of needed value Boolean. */
    CHECKED(Boolean.class, "checked"),
    /** Value denoting the XPath 'class' attribute. Type of needed value String. */
    CLASS_NAME(String.class, "class"),
    /** Value denoting the XPath 'clickable' attribute. Type of needed value Boolean. */
    CLICKABLE(Boolean.class, "clickable"),
    /** Value denoting the XPath 'content-desc' attribute. Type of needed value String. */
    CONTENT_DESCRIPTION(String.class, "content-desc"),
    /** Value denoting the XPath 'enabled' attribute. Type of needed value Boolean. */
    ENABLED(Boolean.class, "enabled"),
    /** Value denoting the XPath 'focusable' attribute. Type of needed value Boolean. */
    FOCUSABLE(Boolean.class, "focusable"),
    /** Value denoting the XPath 'focused' attribute. Type of needed value Boolean. */
    FOCUSED(Boolean.class, "focused"),
    /** Value denoting the XPath 'index' attribute. Type of needed value Integer. */
    INDEX(Integer.class, "index"),
    /** Value denoting the XPath 'long-clickable' attribute. Type of needed value Boolean. */
    LONG_CLICKABLE(Boolean.class, "long-clickable"),
    /** Value denoting the XPath 'package' attribute. Type of needed value String. */
    PACKAGE_NAME(String.class, "package"),
    /** Value denoting the XPath 'password' attribute. Type of needed value Boolean. */
    PASSWORD(Boolean.class, "password"),
    /** Value denoting the CSS 'resource-id' attribute. Type of needed value String. */
    RESOURCE_ID(String.class, "resource-id"),
    /** Value denoting the XPath 'scrollable' attribute. Type of needed value Boolean. */
    SCROLLABLE(Boolean.class, "scrollable"),
    /** Value denoting the XPath 'selected' attribute. Type of needed value Boolean. */
    SELECTED(Boolean.class, "selected"),
    /** Value denoting the XPath 'text' attribute. Type of needed value String. */
    TEXT(String.class, "text"),
    /** Value denoting the XPath 'id' attribute. Type of needed value String. */
    ID(String.class, "id"),
    /** Value denoting the XPath 'href' attribute. Type of needed value String. */
    HREF(String.class, "href"),
    /** Value denoting the XPath 'title' attribute. Type of needed value String. */
    TITLE(String.class, "title"),
    /** Value denoting the XPath 'class' attribute. Type of needed value String. */
    CLASS(String.class, "class"),
    /** Value denoting the XPath 'type' attribute. Type of needed value String. */
    TYPE(String.class, "type"),
    /** Value denoting the XPath 'src' attribute. Type of needed value String. */
    SRC(String.class, "src");

    /** The attribute values will be checked against this type and in case of discrepancy an exception will be thrown */
    private Class<?> attributeType;

    /** The name of the xml attribute corresponding to this attribute. */
    private String xmlAttributeName;

    private XPathAttribute(Class<?> attributeType, String xmlAttributeName) {
        this.attributeType = attributeType;
        this.xmlAttributeName = xmlAttributeName;
    }

    /**
     * Checks if a given object is of the expected type for this attribute
     * 
     * @param object
     *        - object to be checked
     * @return true if the object is of the expected type for this attribute, false if it is not
     */
    public boolean isObjectOfExpectedAttributeType(Object object) {
        return object == null || attributeType.isInstance(object);
    }

    /** This method is intentionally package protected. */
    String getXmlAttributeName() {
        return xmlAttributeName;
    }

    /** This method is intentionally package protected. */
    Class<?> getAttributeType() {
        return attributeType;
    }

    /**
     * Checks if a given attribute name is part of the enumeration in XPathAttribute
     * 
     * @param attributeName
     *        - an attribute name to be checked
     * @return true if the attribute name is in the enumeration, false if it is not
     */
    public static boolean isAttributeStringOfTheEnumeration(String attributeName) {
        for (XPathAttribute attribute : XPathAttribute.values()) {
            if (attribute.xmlAttributeName.equals(attributeName))
                return true;
        }

        return false;
    }
}
