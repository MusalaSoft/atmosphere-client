package com.musala.atmosphere.client.uiutils;

import com.musala.atmosphere.client.geometry.Bounds;
import com.musala.atmosphere.client.geometry.Point;
import com.musala.atmosphere.commons.Pair;

/**
 * Selector class for screen UI elements, used to search for a specific element with given attributes.
 * 
 * @author georgi.gaydarov
 * @author vladimir.vladimirov
 * 
 */
public class UiElementSelector
{
	private Bounds bounds;

	private Boolean selected;

	private Boolean password;

	private Boolean longClickable;

	private Boolean scrollable;

	private Boolean focused;

	private Boolean focusable;

	private Boolean enabled;

	private Boolean clickable;

	private Boolean checked;

	private Boolean checkable;

	private String contentDescription;

	private String packageName;

	private String className;

	private String text;

	private Integer index;

	public Bounds getBounds()
	{
		return bounds;
	}

	public void setBounds(Bounds bounds)
	{
		this.bounds = bounds;
	}

	public Boolean isSelected()
	{
		return selected;
	}

	public void setSelected(Boolean selected)
	{
		this.selected = selected;
	}

	public Boolean isPassword()
	{
		return password;
	}

	public void setPassword(Boolean password)
	{
		this.password = password;
	}

	public Boolean isLongClickable()
	{
		return longClickable;
	}

	public void setLongClickable(Boolean longClickable)
	{
		this.longClickable = longClickable;
	}

	public Boolean isScrollable()
	{
		return scrollable;
	}

	public void setScrollable(Boolean scrollable)
	{
		this.scrollable = scrollable;
	}

	public Boolean isFocused()
	{
		return focused;
	}

	public void setFocused(Boolean focused)
	{
		this.focused = focused;
	}

	public Boolean isFocusable()
	{
		return focusable;
	}

	public void setFocusable(Boolean focusable)
	{
		this.focusable = focusable;
	}

	public Boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(Boolean enabled)
	{
		this.enabled = enabled;
	}

	public Boolean isClickable()
	{
		return clickable;
	}

	public void setClickable(Boolean clickable)
	{
		this.clickable = clickable;
	}

	public Boolean isChecked()
	{
		return checked;
	}

	public void setChecked(Boolean checked)
	{
		this.checked = checked;
	}

	public Boolean isCheckable()
	{
		return checkable;
	}

	public void setCheckable(Boolean checkable)
	{
		this.checkable = checkable;
	}

	public String getContentDescription()
	{
		return contentDescription;
	}

	public void setContentDescription(String contentDescription)
	{
		this.contentDescription = contentDescription;
	}

	public String getPackageName()
	{
		return packageName;
	}

	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}

	public String getClassName()
	{
		return className;
	}

	public void setClassName(String className)
	{
		this.className = className;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public Integer getIndex()
	{
		return index;
	}

	public void setIndex(Integer index)
	{
		this.index = index;
	}

	/**
	 * Builds a CSS select element query based on the contents of this selector.
	 * 
	 * @return the built CSS query.
	 */
	public String buildCssQuery()
	{
		StringBuilder builder = new StringBuilder();

		if (className != null)
		{
			builder.append("[class=" + className + "]");
		}
		if (contentDescription != null)
		{
			builder.append("[content-desc=" + contentDescription + "]");
		}
		if (packageName != null)
		{
			builder.append("[package=" + packageName + "]");
		}
		if (bounds != null)
		{
			Point firstBound = bounds.getUpperLeftCorner();
			Point secondBound = bounds.getLowerRightCorner();
			String boundsString = String.format("[%d,%d][%d,%d]",
												firstBound.getX(),
												secondBound.getX(),
												firstBound.getY(),
												secondBound.getY());
			builder.append("[bounds=" + boundsString + "]");
		}
		if (text != null)
		{
			builder.append("[text=" + text + "]");
		}
		if (index != null)
		{
			builder.append("[index=" + index + "]");
		}
		if (checkable != null)
		{
			builder.append("[checkable=" + checkable + "]");
		}
		if (checked != null)
		{
			builder.append("[checked=" + checked + "]");
		}
		if (clickable != null)
		{
			builder.append("[clickable=" + clickable + "]");
		}
		if (enabled != null)
		{
			builder.append("[enabled=" + enabled + "]");
		}
		if (focusable != null)
		{
			builder.append("[focusable=" + focusable + "]");
		}
		if (focused != null)
		{
			builder.append("[focused=" + focused + "]");
		}
		if (longClickable != null)
		{
			builder.append("[long-clickable=" + longClickable + "]");
		}
		if (password != null)
		{
			builder.append("[password=" + password + "]");
		}
		if (scrollable != null)
		{
			builder.append("[scrollable=" + scrollable + "]");
		}
		if (selected != null)
		{
			builder.append("[selected=" + selected + "]");
		}

		String query = builder.toString();
		return query;
	}
}
