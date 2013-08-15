package com.musala.atmosphere.client;

import java.util.Map;

import com.musala.atmosphere.client.geometry.Bounds;
import com.musala.atmosphere.client.ui.UiElementBoundsParser;

public class UiElementAttributes
{
	private Bounds bounds;

	private boolean selected;

	private boolean password;

	private boolean longClickable;

	private boolean scrollable;

	private boolean focused;

	private boolean focusable;

	private boolean enabled;

	private boolean clickable;

	private boolean checked;

	private boolean checkable;

	private String contentDescription;

	private String packageName;

	private String className;

	private String text;

	private int index;

	UiElementAttributes(Map<String, String> nodeAttributeMap)
	{
		packageName = nodeAttributeMap.get("package");
		className = nodeAttributeMap.get("class");
		contentDescription = nodeAttributeMap.get("content-desc");
		text = nodeAttributeMap.get("text");

		checked = nodeAttributeMap.get("checked").equals("true");
		checkable = nodeAttributeMap.get("checkable").equals("true");
		clickable = nodeAttributeMap.get("clickable").equals("true");
		enabled = nodeAttributeMap.get("enabled").equals("true");
		focusable = nodeAttributeMap.get("focusable").equals("true");
		focused = nodeAttributeMap.get("focused").equals("true");
		longClickable = nodeAttributeMap.get("long-clickable").equals("true");
		password = nodeAttributeMap.get("password").equals("true");
		scrollable = nodeAttributeMap.get("scrollable").equals("true");
		selected = nodeAttributeMap.get("selected").equals("true");

		index = Integer.parseInt(nodeAttributeMap.get("index"));

		String elementBoundsString = nodeAttributeMap.get("bounds");
		bounds = UiElementBoundsParser.parse(elementBoundsString);
	}

	public Bounds getBounds()
	{
		return bounds;
	}

	public boolean isSelected()
	{
		return selected;
	}

	public boolean isPassword()
	{
		return password;
	}

	public boolean isLongClickable()
	{
		return longClickable;
	}

	public boolean isScrollable()
	{
		return scrollable;
	}

	public boolean isFocused()
	{
		return focused;
	}

	public boolean isFocusable()
	{
		return focusable;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public boolean isClickable()
	{
		return clickable;
	}

	public boolean isChecked()
	{
		return checked;
	}

	public boolean isCheckable()
	{
		return checkable;
	}

	public String getContentDescription()
	{
		return contentDescription;
	}

	public String getPackageName()
	{
		return packageName;
	}

	public String getClassName()
	{
		return className;
	}

	public String getText()
	{
		return text;
	}

	public int getIndex()
	{
		return index;
	}
}
