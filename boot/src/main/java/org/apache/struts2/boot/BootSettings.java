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
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;

public final class BootSettings {

    private static final String PREFIX = "struts.boot.";
    private static final String PORT_KEY = PREFIX + "port";
    private static final String CONTEXT_PATH_KEY = PREFIX + "context-path";
    private static final String ADDRESS_KEY = PREFIX + "address";
    private static final String SESSION_COOKIE_SECURE_KEY = PREFIX + "session.cookie.secure";
    private static final String SESSION_COOKIE_SAME_SITE_KEY = PREFIX + "session.cookie.sameSite";
    private static final List<String> SAME_SITE_VALUES = List.of("lax", "strict", "none", "unset");
    private static final String ARGUMENT_PREFIX = "--";

    private final int port;
    private final String contextPath;
    private final String address;
    private final boolean sessionCookieSecure;
    private final String sessionCookieSameSite;

    private BootSettings(int port, String contextPath, String address, boolean sessionCookieSecure, String sessionCookieSameSite) {
        this.port = port;
        this.contextPath = contextPath;
        this.address = address;
        this.sessionCookieSecure = sessionCookieSecure;
        this.sessionCookieSameSite = sessionCookieSameSite;
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
                parsePort(merged.getProperty(PORT_KEY, "8080")),
                normaliseContextPath(merged.getProperty(CONTEXT_PATH_KEY, "")),
                blankToNull(merged.getProperty(ADDRESS_KEY)),
                parseSecure(merged.getProperty(SESSION_COOKIE_SECURE_KEY, "false")),
                parseSameSite(merged.getProperty(SESSION_COOKIE_SAME_SITE_KEY, "lax")));
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

    /** Whether {@code Secure} is forced on the session cookie; {@code false} unless configured. */
    public boolean sessionCookieSecure() {
        return sessionCookieSecure;
    }

    /**
     * The {@code SameSite} value, in lower case, the container adds to the session cookie and to any
     * other cookie without a {@code SameSite} attribute of its own: {@code lax} (the default),
     * {@code strict}, {@code none}, or {@code unset} to add none.
     */
    public String sessionCookieSameSite() {
        return sessionCookieSameSite;
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
            throw new ConfigurationException("Invalid " + PORT_KEY + ": '" + value + "' is not a number", e);
        }
    }

    private static boolean parseSecure(String value) {
        String normalised = value.trim().toLowerCase(Locale.ROOT);
        if (normalised.equals("true")) {
            return true;
        }
        if (normalised.equals("false")) {
            return false;
        }
        throw new ConfigurationException("Invalid " + SESSION_COOKIE_SECURE_KEY + ": '" + value + "' is not true or false");
    }

    private static String parseSameSite(String value) {
        String normalised = value.trim().toLowerCase(Locale.ROOT);
        if (!SAME_SITE_VALUES.contains(normalised)) {
            throw new ConfigurationException("Invalid " + SESSION_COOKIE_SAME_SITE_KEY + ": '" + value
                    + "' is not one of " + String.join(", ", SAME_SITE_VALUES));
        }
        return normalised;
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
