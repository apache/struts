/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.boot;

import org.apache.struts2.config.ConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public final class BootSettings {

    private static final String PREFIX = "struts.boot.";
    private static final String PORT = PREFIX + "port";
    private static final String CONTEXT_PATH = PREFIX + "context-path";
    private static final String ADDRESS = PREFIX + "address";
    private static final String ARGUMENT_PREFIX = "--";

    private final int port;
    private final String contextPath;
    private final String address;

    private BootSettings(int port, String contextPath, String address) {
        this.port = port;
        this.contextPath = contextPath;
        this.address = address;
    }

    public static BootSettings load(String[] args) {
        return load(readStrutsProperties(), args);
    }

    public static BootSettings load(Properties fileProperties, String[] args) {
        Properties merged = new Properties();
        merged.putAll(fileProperties);
        for (String arg : args) {
            applyArgument(merged, arg);
        }
        return new BootSettings(
                parsePort(merged.getProperty(PORT, "8080")),
                normaliseContextPath(merged.getProperty(CONTEXT_PATH, "")),
                blankToNull(merged.getProperty(ADDRESS)));
    }

    public int port() {
        return port;
    }

    public String contextPath() {
        return contextPath;
    }

    public Optional<String> address() {
        return Optional.ofNullable(address);
    }

    private static void applyArgument(Properties target, String arg) {
        if (!arg.startsWith(ARGUMENT_PREFIX + PREFIX)) {
            return;
        }
        int equals = arg.indexOf('=');
        if (equals < 0) {
            return;
        }
        target.setProperty(arg.substring(ARGUMENT_PREFIX.length(), equals), arg.substring(equals + 1));
    }

    private static String normaliseContextPath(String value) {
        String path = value.trim();
        while (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        if (path.isEmpty()) {
            return "";
        }
        return path.startsWith("/") ? path : "/" + path;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static int parsePort(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Invalid " + PORT + ": '" + value + "' is not a number", e);
        }
    }

    private static Properties readStrutsProperties() {
        Properties properties = new Properties();
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("struts.properties")) {
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException e) {
            throw new ConfigurationException("Cannot read struts.properties", e);
        }
        return properties;
    }
}
