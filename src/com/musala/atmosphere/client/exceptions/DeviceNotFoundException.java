package com.musala.atmosphere.client.exceptions;

/**
 * This exception will be thrown when the Client tries to get device, which DeviceProxyRmiID is for some reason not in
 * the Pool's RMI registry.
 * 
 * @author vladimir.vladimirov
 * 
 */
public class DeviceNotFoundException extends RuntimeException
{
	private static final long serialVersionUID = -1340537020745912140L;

	public DeviceNotFoundException()
	{
	}

	public DeviceNotFoundException(String message)
	{
		super(message);
	}

	public DeviceNotFoundException(String message, Throwable inner)
	{
		super(message, inner);
	}
}
