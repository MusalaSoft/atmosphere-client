package com.musala.atmosphere.client.exceptions;

/**
 * Thrown when an action invocation failed.
 * 
 * @author georgi.gaydarov
 * 
 */
public class ActionFailedException extends RuntimeException
{

	/**
	 * auto generated serialization id
	 */
	private static final long serialVersionUID = 797241978901791497L;

	public ActionFailedException()
	{
	}

	public ActionFailedException(String message)
	{
		super(message);
	}

	public ActionFailedException(String message, Throwable inner)
	{
		super(message, inner);
	}
}
