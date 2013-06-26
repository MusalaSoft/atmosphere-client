package com.musala.atmosphere.client.device;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * With this class the user can define his own multi-finger gestures on the device screen. For example: one can use this
 * class to create scenario where two fingers start random movements on the screen device while third finger is hold on
 * to the screen.
 * 
 * @author vladimir.vladimirov
 * 
 */
public class TouchGesture
{
	private static final Logger LOGGER = Logger.getLogger(TouchGesture.class.getCanonicalName());

	/**
	 * Every finger is assigned a ComplexGesture instance for its movement.
	 */
	private List<ComplexGesture> complexGestureList;

	/**
	 * @return the list with {@link ComplexGesture ComplexGestures} for this custom TouchGesture
	 */
	public List<ComplexGesture> getComplexGestureList()
	{
		return complexGestureList;
	}

	/**
	 * @param complexGestureList
	 *        - the complexGestureList to set
	 */
	public void setComplexGestureList(List<ComplexGesture> complexGestureList)
	{
		this.complexGestureList = complexGestureList;
	}

	/**
	 * Adds new complex gesture in the list of gestures.
	 * 
	 * @param gesture
	 *        - new {@link ComplexGesture} to be added in the end of the list
	 */
	public void addComplexGestureToList(ComplexGesture gesture)
	{
		complexGestureList.add(gesture);
	}

}
