package com.musala.atmosphere.client.exceptions;

/**
 * Thrown when test class of the QA doesn't have the <i>@Server</i> annotation.
 * 
 * @author vladimir.vladimirov
 * 
 */
public class MissingServerAnnotationException extends RuntimeException
{
	private static final long serialVersionUID = 6402529875524928891L;

	public MissingServerAnnotationException()
	{
	}

	public MissingServerAnnotationException(String message)
	{
		super(message);
	}

	public MissingServerAnnotationException(String message, Throwable inner)
	{
		super(message, inner);
	}
}
