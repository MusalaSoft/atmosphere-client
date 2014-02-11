package com.musala.atmosphere.client.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.musala.atmosphere.client.geometry.Bounds;
import com.musala.atmosphere.client.geometry.Point;

/**
 * Houses {@link #parse(String) parse(elementBounds)}.
 *
 * @author georgi.gaydarov
 *
 */
public class UiElementBoundsParser
{
	/**
	 * Converts a UI element bounds in the format <b>[startX,startY][endX,endY]</b> (fetched from the UI XML file) to a
	 * Pair&lt;Point, Point;&gt; format.
	 *
	 * @param bounds
	 *        String to be parsed.
	 * @return bounds pair.
	 */
	public static Bounds parse(String bounds)
	{
		final String BOUNDS_STRING_PATTERN = "\\[(-*\\d+),(-*\\d+)\\]\\[(-*\\d+),(-*\\d+)\\]";
		final Pattern BOUNDS_PATTERN = Pattern.compile(BOUNDS_STRING_PATTERN);
		final Matcher BOUNDS_MATCHER = BOUNDS_PATTERN.matcher(bounds);

		if (BOUNDS_MATCHER.find())
		{
			int firstPointX = Integer.parseInt(BOUNDS_MATCHER.group(1));
			int firstPointY = Integer.parseInt(BOUNDS_MATCHER.group(2));
			Point first = new Point(firstPointX, firstPointY);

			int secondPointX = Integer.parseInt(BOUNDS_MATCHER.group(3));
			int secondPointY = Integer.parseInt(BOUNDS_MATCHER.group(4));
			Point second = new Point(secondPointX, secondPointY);

			Bounds result = new Bounds(first, second);
			return result;
		}
		else
		{
			throw new IllegalArgumentException("'" + bounds + "' is not in the required format.");
		}
	}
}
