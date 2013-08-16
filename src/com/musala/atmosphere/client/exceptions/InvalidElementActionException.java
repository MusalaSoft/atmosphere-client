package com.musala.atmosphere.client.exceptions;

import com.musala.atmosphere.client.UiElement;

/**
 * Thrown when an action is being executed on a {@link UiElement UiElement} instance if the action is inadequate for the
 * element.
 * 
 * @author georgi.gaydarov
 * 
 */
public class InvalidElementActionException extends RuntimeException
{

	/**
	 * auto generated serialization id
	 */
	private static final long serialVersionUID = 2374589821011286146L;

	public InvalidElementActionException()
	{
	}

	public InvalidElementActionException(String message)
	{
		super(message);
	}

	public InvalidElementActionException(String message, Throwable inner)
	{
		super(message, inner);
	}
}
