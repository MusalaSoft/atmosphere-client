package com.musala.atmosphere.client.uiutils;

import com.musala.atmosphere.commons.Pair;

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
	 * Pair&lt;Pair&lt;Integer, Integer&gt;, Pair&lt;Integer, Integer&gt;&gt; format.
	 * 
	 * @param bounds
	 *        String to be parsed.
	 * @return bounds pair.
	 */
	public static Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> parse(String bounds)
	{
		String[] pairs = bounds.split("\\]\\[");
		String firstPair = pairs[0].substring(1); // was in the format "[startX,startY"
		String secondPair = pairs[1].substring(0, pairs[1].length() - 1); // was in the format "endX,endY]"

		String[] firstPairSplit = firstPair.split(",");
		String firstPairFirst = firstPairSplit[0];
		String firstPairSecond = firstPairSplit[1];
		int firstPairFirstInt = Integer.parseInt(firstPairFirst);
		int firstPairSecondInt = Integer.parseInt(firstPairSecond);
		Pair<Integer, Integer> first = new Pair<Integer, Integer>(firstPairFirstInt, firstPairSecondInt);

		String[] secondPairSplit = secondPair.split(",");
		String secondPairFirst = secondPairSplit[0];
		String secondPairSecond = secondPairSplit[1];
		int secondPairFirstInt = Integer.parseInt(secondPairFirst);
		int secondPairSecondInt = Integer.parseInt(secondPairSecond);
		Pair<Integer, Integer> second = new Pair<Integer, Integer>(secondPairFirstInt, secondPairSecondInt);

		Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> result = new Pair<Pair<Integer, Integer>, Pair<Integer, Integer>>(	first,
																																second);
		return result;
	}
}
