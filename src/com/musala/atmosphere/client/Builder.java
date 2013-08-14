package com.musala.atmosphere.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.exceptions.MissingServerAnnotationException;
import com.musala.atmosphere.client.exceptions.ServerConnectionFailedException;
import com.musala.atmosphere.client.util.Server;
import com.musala.atmosphere.commons.Pair;
import com.musala.atmosphere.commons.cs.InvalidPasskeyException;
import com.musala.atmosphere.commons.cs.RmiStringConstants;
import com.musala.atmosphere.commons.cs.clientbuilder.DeviceAllocationInformation;
import com.musala.atmosphere.commons.cs.clientbuilder.DeviceParameters;
import com.musala.atmosphere.commons.cs.clientbuilder.IClientBuilder;
import com.musala.atmosphere.commons.cs.clientdevice.IClientDevice;

/**
 * Used by the user to get appropriate device in the server's pool.
 * 
 * @author vladimir.vladimirov
 */
public class Builder
{
	private static final Logger LOGGER = Logger.getLogger(Builder.class.getCanonicalName());

	private static Builder builder = null;

	private static String serverIp;

	private static int serverRmiPort;

	private IClientBuilder clientBuilder;

	private Registry serverRmiRegistry;

	private Map<Device, DeviceAllocationInformation> deviceToDescriptor = new HashMap<Device, DeviceAllocationInformation>();

	/**
	 * Connects to Server through given IP and rmiPort.
	 * 
	 * @param annotationServerIp
	 * @param annotationRmiPort
	 */
	private Builder(String annotationServerIp, int annotationRmiPort)
	{
		serverIp = annotationServerIp;
		serverRmiPort = annotationRmiPort;

		try
		{
			serverRmiRegistry = LocateRegistry.getRegistry(annotationServerIp, annotationRmiPort);
			clientBuilder = (IClientBuilder) serverRmiRegistry.lookup(RmiStringConstants.POOL_MANAGER.toString());
		}
		catch (RemoteException e)
		{
			LOGGER.fatal("Getting the server's pool manager RMI stub resulted in exception.", e);
			throw new ServerConnectionFailedException(	"Getting the server's pool manager RMI stub resulted in exception.",
														e);
		}
		catch (NotBoundException e)
		{
			LOGGER.fatal(	"The required Server stubs are not available in the target RMI registry. The target is most likely not an ATMOSPHERE Server.",
							e);
			throw new ServerConnectionFailedException("The required Server stubs are not available in the target RMI registry. The target is most likely not an ATMOSPHERE Server.");
		}

		LOGGER.info("Builder has connected to the server's device pool manager.");
	}

	/**
	 * Gets server IP and Port from the <i>@Server</i> annotation of the test class or throws
	 * MissingServerAnnotationException at runtime.
	 * 
	 * @return Pair of type (String, Integer) in the context of (IP, port)
	 */
	private static Pair<String, Integer> reflectServerAnnotationValues()
	{
		String serverIp = null;
		Integer serverPort = null;

		Exception exception = new Exception();
		StackTraceElement[] callerMethods = exception.getStackTrace();

		for (StackTraceElement callerMethod : callerMethods)
		{
			// going up in the stack trace to see which class has annotation Server
			Class<?> callerClass = null;
			try
			{
				callerClass = Class.forName(callerMethod.getClassName());
				if (callerClass.isAnnotationPresent(Server.class))
				{
					Server serverAnnotation = (Server) callerClass.getAnnotation(Server.class);
					serverIp = serverAnnotation.ip();
					serverPort = serverAnnotation.port();
					break;
				}
			}
			catch (ClassNotFoundException e)
			{
				LOGGER.error("Could not find class with name: " + callerMethod.getClassName(), e);
			}

		}

		if (serverIp == null || serverPort == null)
		{
			LOGGER.fatal("The invoking class is missing a @Server annotation.");
			throw new MissingServerAnnotationException("The invoking class is missing a @Server annotation.");
		}

		Pair<String, Integer> annotationValues = new Pair<String, Integer>(serverIp, serverPort);
		return annotationValues;
	}

	/**
	 * Gets the {@link Builder Builder} instance for the anotated Server address.
	 * 
	 * @return {@link Builder Builder} instance.
	 */
	public static Builder getInstance()
	{
		if (builder == null)
		{
			synchronized (Builder.class)
			{
				// Getting the server IP/port from the annotation
				Pair<String, Integer> annotationPair = reflectServerAnnotationValues();

				String reflectedServerIp = annotationPair.getKey();
				Integer reflectedRmiPort = annotationPair.getValue();

				if (builder == null)
				{
					builder = new Builder(reflectedServerIp, reflectedRmiPort);
					LOGGER.info("Builder instance has been created.");
				}

			}
		}

		return builder;
	}

	/**
	 * Gets a {@link Device Device} instance with given {@link DeviceParameters DeviceParameters}.
	 * 
	 * @param deviceParameters
	 *        - required device parameters.
	 * @return a {@link Device Device} instance.
	 */
	public Device getDevice(DeviceParameters deviceParameters)
	{
		try
		{
			DeviceAllocationInformation deviceDescriptor = clientBuilder.allocateDevice(deviceParameters);

			String deviceProxyRmiId = deviceDescriptor.getProxyRmiId();
			LOGGER.info("Fetched device with proxy RMI id: " + deviceProxyRmiId + ".");

			IClientDevice iClientDevice = (IClientDevice) serverRmiRegistry.lookup(deviceProxyRmiId);
			long passkey = deviceDescriptor.getProxyPasskey();

			Device device = new Device(iClientDevice, passkey);
			deviceToDescriptor.put(device, deviceDescriptor);
			return device;
		}
		catch (RemoteException | NotBoundException e)
		{
			LOGGER.error("Fetching Device failed (server connection failure).", e);
			throw new ServerConnectionFailedException("Fetching Device failed (server connection failure).", e);
		}
	}

	public String getServerIp()
	{
		return serverIp;
	}

	public int getServerRmiPort()
	{
		return serverRmiPort;
	}

	/**
	 * Releases previously allocated to a device.
	 * 
	 * @param device
	 *        - device to be released.
	 */
	public void releaseDevice(Device device)
	{
		DeviceAllocationInformation deviceDescriptor = deviceToDescriptor.get(device);
		String deviceRmiId = deviceDescriptor.getProxyRmiId();

		try
		{
			deviceToDescriptor.remove(device);
			device.release();
			clientBuilder.releaseDevice(deviceDescriptor);
		}
		catch (RemoteException e)
		{
			LOGGER.error("Could not release Device (connection failure).", e);
			throw new ServerConnectionFailedException("Could not release Device (connection failure).", e);
		}
		catch (InvalidPasskeyException e)
		{
			// We did not have the correct passkey. The device most likely timed out and got freed to be used by someone
			// else. So nothing to do here.
		}

		LOGGER.info(deviceRmiId + " is released.");
	}

	/**
	 * Releases all allocated devices.
	 */
	public void releaseAllDevices()
	{
		for (Device device : deviceToDescriptor.keySet())
		{
			releaseDevice(device);
		}
	}

	@Override
	protected void finalize()
	{
		releaseAllDevices();
		builder = null;
	}
}
