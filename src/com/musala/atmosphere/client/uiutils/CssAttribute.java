package com.musala.atmosphere.client.uiutils;

import com.musala.atmosphere.client.geometry.Bounds;

/**
 * Lists all the CSS attributes the system currently supports.
 * <p>
 * If you need to add a new attribute, you just need to add its value and specify the corresponding data type in the
 * enum.
 *
 * @author boris.strandjev
 */
public enum CssAttribute
{
	BOUNDS(Bounds.class, "bounds"),
	CHECKABLE(Boolean.class, "checkable"),
	CHECKED(Boolean.class, "checked"),
	CLASS_NAME(String.class, "class"),
	CLICKABLE(Boolean.class, "clickable"),
	CONTENT_DESCRIPTION(String.class, "content-desc"),
	ENABLED(Boolean.class, "enabled"),
	FOCUSABLE(Boolean.class, "focusable"),
	FOCUSED(Boolean.class, "focused"),
	INDEX(Integer.class, "index"),
	LONG_CLICKABLE(Boolean.class, "long-clickable"),
	PACKAGE_NAME(String.class, "package"),
	PASSWORD(Boolean.class, "password"),
	SCROLLABLE(Boolean.class, "scrollable"),
	SELECTED(Boolean.class, "selected"),
	TEXT(String.class, "text");

	/** The attribute values will be checked against this type and in case of discrepancy an exception will be thrown */
	private Class<?> attributeType;

	/** The name of the html attribute corresponding to this attribute. */
	private String htmlAttributeName;

	private CssAttribute(Class<?> attributeType, String htmlAttributeName)
	{
		this.attributeType = attributeType;
		this.htmlAttributeName = htmlAttributeName;
	}

	/**
	 * Checks if the given value is appropriate for the given ui attribute value.
	 */
	public boolean isObjectOfAppropriateType(Object object)
	{
		return object == null || attributeType.isInstance(object);
	}

	/** This method is intentionally package protected. */
	String getHtmlAttributeName()
	{
		return htmlAttributeName;
	}

	/** This method is intentionally package protected. */
	Class<?> getAttributeType() {
		return attributeType;
	}
}
