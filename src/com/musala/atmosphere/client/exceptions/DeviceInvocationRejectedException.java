package com.musala.atmosphere.client.exceptions;

/**
 * Thrown when the invocation on the server's device representation object was rejected.
 * 
 * @author georgi.gaydarov
 * 
 */
public class DeviceInvocationRejectedException extends RuntimeException
{

	/**
	 * auto generated serialization id
	 */
	private static final long serialVersionUID = -3193659488917500184L;

	public DeviceInvocationRejectedException()
	{
	}

	public DeviceInvocationRejectedException(String message)
	{
		super(message);
	}

	public DeviceInvocationRejectedException(String message, Throwable inner)
	{
		super(message, inner);
	}

	public DeviceInvocationRejectedException(Throwable inner)
	{
		super("Method invocation was rejected. The device allocation lock could have timed out.", inner);
	}
}
