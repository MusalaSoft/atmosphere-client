package com.musala.atmosphere.client.device;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * This class simulates one-finger movements, more complex than dragging finger in straight line on the screen of the
 * device, like moving finger by some sort of curve. This is done by dividing the complex gesture in many small straight
 * line finger movements ( simple gestures ).
 * 
 * @author vladimir.vladimirov
 * 
 */
public class ComplexGesture
{
	private final static Logger LOGGER = Logger.getLogger(ComplexGesture.class.getCanonicalName());

	private List<SimpleGesture> simpleGestureList;

	/**
	 * Gets the simple gestures which compose the {@link ComplexGesture }
	 * 
	 * @return list of simple gestures which together forms the complex gesture.
	 * 
	 */
	public List<SimpleGesture> getSimpleGestureList()
	{
		return simpleGestureList;
	}

	/**
	 * Sets up a new complex gesture using list of simple straight-line finger movements ( simple gestures ).
	 * 
	 * @param simpleGestureList
	 */
	public void setSimpleGestureList(List<SimpleGesture> simpleGestureList)
	{
		this.simpleGestureList = simpleGestureList;
	}

	/**
	 * Adds new simple gesture to the list with gestures.
	 * 
	 * @param gesture
	 *        - {@link SimpleGesture} to be added in the end of the list
	 */
	public void addSimpleGestureToList(SimpleGesture gesture)
	{
		simpleGestureList.add(gesture);
	}
}
