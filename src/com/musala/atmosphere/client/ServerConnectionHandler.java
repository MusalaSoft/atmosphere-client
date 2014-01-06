package com.musala.atmosphere.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.exceptions.ServerConnectionFailedException;
import com.musala.atmosphere.client.util.ServerConnectionProperties;
import com.musala.atmosphere.commons.cs.RmiStringConstants;
import com.musala.atmosphere.commons.cs.clientbuilder.IClientBuilder;
import com.musala.atmosphere.commons.util.Pair;

/**
 * Handles basic commands related to connection to server.
 * 
 * @author yordan.petrov
 * 
 */
class ServerConnectionHandler
{
	private static final Logger LOGGER = Logger.getLogger(ServerConnectionHandler.class.getCanonicalName());

	private ServerConnectionProperties serverConnectionProperties;

	private Registry serverRmiRegistry;

	/**
	 * Creates a new {@link ServerConnectionHandler} instance by given server IP address, port and a connection retry
	 * limit.
	 * 
	 * @param serverIp
	 *        - the IP address of the server.
	 * @param serverPort
	 *        - the port of the server.
	 * @param connectionRetryLimit
	 *        - the connection retry limit, that will be used when trying to reconnect.
	 */
	ServerConnectionHandler(ServerConnectionProperties serverConnectionProperties)
	{
		this.serverConnectionProperties = serverConnectionProperties;
	}

	/**
	 * Connects to server and returns a pair of client builder and server RMI registry.
	 * 
	 * @return pair of client builder and server RMI registry.
	 */
	public Pair<IClientBuilder, Registry> connect()
	{
		LOGGER.info("Connecting to server...");

		int connectionAttemptCounter = serverConnectionProperties.getConnectionRetryLimit();

		Exception innerException = null;
		do
		{
			try
			{
				Pair<IClientBuilder, Registry> clientBuilderRegistryPair = getClientBuilderRegistryPair();

				LOGGER.info("Now connected to the server's device pool manager.");

				return clientBuilderRegistryPair;
			}
			catch (ServerConnectionFailedException e)
			{
				innerException = e;

				connectionAttemptCounter--;
			}

			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
			}

		} while (connectionAttemptCounter > 0);

		LOGGER.error("Connecting to server failed!", innerException);
		throw new ServerConnectionFailedException("Connection retry limit reached.", innerException);
	}

	private Pair<IClientBuilder, Registry> getClientBuilderRegistryPair()
	{

		try
		{
			if (serverRmiRegistry == null)
			{
				serverRmiRegistry = LocateRegistry.getRegistry(	serverConnectionProperties.getIp(),
																serverConnectionProperties.getPort());
			}

			IClientBuilder clientBuilder = (IClientBuilder) serverRmiRegistry.lookup(RmiStringConstants.POOL_MANAGER.toString());
			return new Pair<IClientBuilder, Registry>(clientBuilder, serverRmiRegistry);
		}
		catch (RemoteException e)
		{
			String message = "Getting the server's pool manager RMI stub resulted in exception.";

			LOGGER.fatal(message, e);
			throw new ServerConnectionFailedException(message, e);
		}
		catch (NotBoundException e)
		{
			String message = "The required Server stubs are not available in the target RMI registry. The target is most likely not an ATMOSPHERE Server.";

			LOGGER.fatal(message, e);
			throw new ServerConnectionFailedException(message);
		}
	}

	/**
	 * Returns the server {@link ServerConnectionProperties} that is being used.
	 * 
	 * @return the server {@link ServerConnectionProperties} that is being used.
	 */
	public ServerConnectionProperties getServerConnectionProperties()
	{
		return serverConnectionProperties;
	}

}
