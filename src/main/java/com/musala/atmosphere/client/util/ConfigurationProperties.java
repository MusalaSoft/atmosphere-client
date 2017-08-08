package com.musala.atmosphere.client.util;

/**
 * Enumeration class containing a configuration properties for a test project.
 *
 * @author dimcho.nedev
 *
 */
public enum ConfigurationProperties {
    SERVER_IP("server.ip"),
    SERVER_PORT("server.port"),
    SERVER_CONNECTION_RETRIES("server.connectionRetries"),
    FTP_SERVER("ftp.server"),
    FTP_DIR("ftp.dir"),
    IMPLICIT_WAIT_TIMEOUT("implicit.wait.timeout");

    private String value;

    private ConfigurationProperties(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
