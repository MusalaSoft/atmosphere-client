package com.musala.atmosphere.client.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <i>@Server</i> annotation. It is used to annotate the user's test class with the IP and port for connection with the
 * server, and retry limit to connect.
 * 
 * @author vladimir.vladimirov
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Server
{
	/**
	 * IP address of the server.
	 */
	String ip();

	/**
	 * Port of the server.
	 */
	int port();

	/**
	 * Maximum attempts to connect to server. Used on initial connect to server and when link to the server was lost. If
	 * zero or negative, then only one attempt is made to connect.
	 */
	int connectionRetryLimit();
}