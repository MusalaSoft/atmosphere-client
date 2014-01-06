package com.musala.atmosphere.client.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <i>@Server</i> annotation. It is used to annotate the user's test class with the IP and port for connection with the
 * server.
 * 
 * @author vladimir.vladimirov
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Server
{
	String ip();

	int port();

	int connectionRetryLimit();
}
