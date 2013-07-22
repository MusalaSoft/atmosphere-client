package com.musala.atmosphere.client.exceptions;

/**
 * Thrown when an Activity failed to start for some reason.
 * 
 * @author georgi.gaydarov
 * 
 */
public class ActivityStartingException extends Exception
{

	/**
	 * auto generated serialization vesion id
	 */
	private static final long serialVersionUID = 1928725919257296196L;

	public ActivityStartingException()
	{
	}

	public ActivityStartingException(String message)
	{
		super(message);
	}

	public ActivityStartingException(String message, Throwable inner)
	{
		super(message, inner);
	}
}
