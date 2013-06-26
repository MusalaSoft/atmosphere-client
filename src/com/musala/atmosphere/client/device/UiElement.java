package com.musala.atmosphere.client.device;

import java.util.List;

/**
 * The user can access and manipulate certain views on the testing device through this class, for example he can tap,
 * double-tap or hold finger on given widget or screen position.
 * 
 * @author vladimir.vladimirov
 * 
 */
public class UiElement
{
	// TODO add variable for the UiElement

	/**
	 * Simulates tapping over the UI Element
	 */
	public void tap()
	{
		// TODO implement uiElement.tap()
	}

	/**
	 * Used to get list of children of the given UI Element. Works only for ViewGroups.
	 * 
	 * @return List with all the UI elements that inherit from this UI element or empty List if such don't exist.
	 */
	public List<UiElement> getChildren()
	{
		// TODO implement uiElement.getChilder()
		return null;
	}

	/**
	 * Simulates holding finger on the screen.
	 */
	public void hold()
	{
		// TODO implement uiElement.hold()
	}

	/**
	 * Simulates double-tapping on the given UI element.
	 */
	public void doubleTap()
	{
		// TODO implement uiElement.doubleTap()
	}

	/**
	 * Simulates dragging the UI widget until his ( which corner exactly? ) upper-left corner stands at position
	 * (toX,toY) on the screen.
	 * 
	 * @param toX
	 * @param toY
	 */
	public void drag(int toX, int toY)
	{
		// TODO implement uiElement.drag()
	}

}
