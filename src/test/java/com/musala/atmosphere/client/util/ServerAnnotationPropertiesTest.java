package com.musala.atmosphere.client.util;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.musala.atmosphere.client.exceptions.MissingServerAnnotationException;

/**
 * 
 * @author yordan.petrov
 * 
 */
public class ServerAnnotationPropertiesTest {
    private static AnnotatedClass annotatedClass;

    private static ServerAnnotationProperties serverAnnotationProperties;

    private static final String SERVER_IP = "localhost";

    private static final int SERVER_PORT = 69;

    private static final int SERVER_CONNECTION_RETRY_LIMIT = 42;

    @Server(ip = SERVER_IP, port = SERVER_PORT, connectionRetryLimit = SERVER_CONNECTION_RETRY_LIMIT)
    static class AnnotatedClass {
        ServerAnnotationProperties serverAnnotationProperties;

        public AnnotatedClass() {
            serverAnnotationProperties = new ServerAnnotationProperties();
        }

        public ServerAnnotationProperties getServerAnnotationProperties() {
            return serverAnnotationProperties;
        }
    }

    class UnannotatedClass {
        ServerAnnotationProperties serverAnnotationProperties;

        public UnannotatedClass() {
            serverAnnotationProperties = new ServerAnnotationProperties();
        }

        public ServerAnnotationProperties getServerAnnotationProperties() {
            return serverAnnotationProperties;
        }
    }

    @BeforeClass
    public static void setUp() {
        annotatedClass = new AnnotatedClass();
        serverAnnotationProperties = annotatedClass.getServerAnnotationProperties();
    }

    @Test
    public void testGetAnnotatedClass() {
        Class<?> annotatedClass = serverAnnotationProperties.getAnnotatedClass();
        assertEquals("The obtained annotated class does not match the annotated class.",
                     annotatedClass,
                     AnnotatedClass.class);
    }

    @Test
    public void testGetIp() {
        String serverIp = serverAnnotationProperties.getIp();
        assertEquals("The obtained server IP address does not match the annotated one.", SERVER_IP, serverIp);
    }

    @Test
    public void testGetPort() {
        int serverPort = serverAnnotationProperties.getPort();
        assertEquals("The obtained server port does not match the annotated one.", SERVER_PORT, serverPort);
    }

    @Test
    public void testGetConnectionRetryLimit() {
        int connectionRetryLimit = serverAnnotationProperties.getConnectionRetryLimit();
        assertEquals("The obtained server connection retry limit does not match the annotated one.",
                     SERVER_CONNECTION_RETRY_LIMIT,
                     connectionRetryLimit);
    }

    @Test(expected = MissingServerAnnotationException.class)
    public void testThrowExceptionWhenNoAnnotationPresent() {
        UnannotatedClass unannotatedClass = new UnannotatedClass();
        unannotatedClass.getServerAnnotationProperties();
    }

}
