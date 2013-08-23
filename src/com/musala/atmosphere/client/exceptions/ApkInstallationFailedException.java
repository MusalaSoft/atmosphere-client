package com.musala.atmosphere.client.exceptions;

/**
 * This exception is thrown when the Client tries to install application on a device, but a fatal error occurs and the
 * installation fails.
 * 
 * @author vladimir.vladimirov
 * 
 */
public class ApkInstallationFailedException extends RuntimeException
{
	/**
	 * auto generated serialization id
	 */
	private static final long serialVersionUID = -4948287126155328678L;

	public ApkInstallationFailedException()
	{
	}

	public ApkInstallationFailedException(String message)
	{
		super(message);
	}

	public ApkInstallationFailedException(String message, Throwable inner)
	{
		super(message, inner);
	}
}
