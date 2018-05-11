// This file is part of the ATMOSPHERE mobile testing framework.
// Copyright (C) 2016 MusalaSoft
//
// ATMOSPHERE is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// ATMOSPHERE is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with ATMOSPHERE.  If not, see <http://www.gnu.org/licenses/>.

package com.musala.atmosphere.client.util;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.musala.atmosphere.client.exceptions.MissingServerConnectionProperiesException;;

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

    @Test(expected = MissingServerConnectionProperiesException.class)
    public void testThrowExceptionWhenNoServerConnectionPropertiesPresent() {
        UnannotatedClass unannotatedClass = new UnannotatedClass();
        unannotatedClass.getServerAnnotationProperties();
    }

}
