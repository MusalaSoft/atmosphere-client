package com.musala.atmosphere.client.exceptions;

/**
 * This exception will be thrown when the Client tries to install application on a device, but then fatal error occurs
 * on Agent or Server that breaks the process of installation.
 * 
 * @author vladimir.vladimirov
 * 
 */
public class ApkInstallationFailedException extends RuntimeException
{

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
