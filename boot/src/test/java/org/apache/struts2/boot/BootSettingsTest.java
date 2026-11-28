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
import org.junit.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BootSettingsTest {

    @Test
    public void testDefaults() {
        BootSettings settings = BootSettings.load(new Properties(), new String[0]);

        assertThat(settings.port()).isEqualTo(8080);
        assertThat(settings.contextPath()).isEmpty();
    }

    @Test
    public void testFileValue() {
        Properties file = new Properties();
        file.setProperty("struts.boot.port", "8081");

        assertThat(BootSettings.load(file, new String[0]).port()).isEqualTo(8081);
    }

    @Test
    public void testArgumentBeatsFile() {
        Properties file = new Properties();
        file.setProperty("struts.boot.port", "8081");

        BootSettings settings = BootSettings.load(file, new String[]{"--struts.boot.port=9090"});

        assertThat(settings.port()).isEqualTo(9090);
    }

    @Test
    public void testUnknownArgumentsIgnored() {
        BootSettings settings = BootSettings.load(new Properties(), new String[]{"--foo=bar", "positional"});

        assertThat(settings.port()).isEqualTo(8080);
        assertThat(settings.contextPath()).isEmpty();
    }

    @Test
    public void testContextPathFromArgs() {
        BootSettings settings = BootSettings.load(new Properties(), new String[]{"--struts.boot.context-path=/ctx"});

        assertThat(settings.contextPath()).isEqualTo("/ctx");
    }

    @Test
    public void testAddressDefaultsToAllInterfaces() {
        assertThat(BootSettings.load(new Properties(), new String[0]).address()).isEmpty();
    }

    @Test
    public void testAddressFromFile() {
        Properties file = new Properties();
        file.setProperty("struts.boot.address", "127.0.0.1");

        assertThat(BootSettings.load(file, new String[0]).address()).contains("127.0.0.1");
    }

    @Test
    public void testAddressArgumentBeatsFile() {
        Properties file = new Properties();
        file.setProperty("struts.boot.address", "0.0.0.0");

        BootSettings settings = BootSettings.load(file, new String[]{"--struts.boot.address=127.0.0.1"});

        assertThat(settings.address()).contains("127.0.0.1");
    }

    @Test
    public void testBlankAddressMeansAllInterfaces() {
        assertThat(BootSettings.load(new Properties(), new String[]{"--struts.boot.address= "}).address()).isEmpty();
    }

    @Test
    public void testContextPathNormalised() {
        assertThat(contextPath("ctx")).isEqualTo("/ctx");
        assertThat(contextPath("/")).isEmpty();
        assertThat(contextPath("")).isEmpty();
        assertThat(contextPath("  ")).isEmpty();
        assertThat(contextPath("/ctx/")).isEqualTo("/ctx");
        assertThat(contextPath(" /ctx ")).isEqualTo("/ctx");
        assertThat(contextPath("/a/b")).isEqualTo("/a/b");
    }

    private static String contextPath(String value) {
        return BootSettings.load(new Properties(), new String[]{"--struts.boot.context-path=" + value}).contextPath();
    }

    @Test
    public void testNonNumericPortThrows() {
        Properties fileProperties = new Properties();
        String[] args = {"--struts.boot.port=abc"};
        assertThatThrownBy(() -> BootSettings.load(fileProperties, args))
                .isInstanceOf(ConfigurationException.class);
    }

    @Test
    public void testSessionCookieDefaults() {
        BootSettings settings = BootSettings.load(new Properties(), new String[0]);

        assertThat(settings.sessionCookieSecure()).isFalse();
        assertThat(settings.sessionCookieSameSite()).isEqualTo("lax");
    }

    @Test
    public void testSessionCookieSecureFromFile() {
        Properties file = new Properties();
        file.setProperty("struts.boot.session.cookie.secure", "true");

        assertThat(BootSettings.load(file, new String[0]).sessionCookieSecure()).isTrue();
    }

    @Test
    public void testSessionCookieSecureArgumentBeatsFile() {
        Properties file = new Properties();
        file.setProperty("struts.boot.session.cookie.secure", "true");

        BootSettings settings = BootSettings.load(file, new String[]{"--struts.boot.session.cookie.secure=false"});

        assertThat(settings.sessionCookieSecure()).isFalse();
    }

    @Test
    public void testSessionCookieSecureIgnoresCaseAndSpaces() {
        assertThat(sessionCookieSecure(" TRUE ")).isTrue();
        assertThat(sessionCookieSecure("False")).isFalse();
    }

    @Test
    public void testSessionCookieSameSiteValues() {
        assertThat(sessionCookieSameSite("lax")).isEqualTo("lax");
        assertThat(sessionCookieSameSite("strict")).isEqualTo("strict");
        assertThat(sessionCookieSameSite("none")).isEqualTo("none");
        assertThat(sessionCookieSameSite("unset")).isEqualTo("unset");
        assertThat(sessionCookieSameSite(" Strict ")).isEqualTo("strict");
        assertThat(sessionCookieSameSite("NONE")).isEqualTo("none");
    }

    @Test
    public void testSessionCookieSameSiteFromFile() {
        Properties file = new Properties();
        file.setProperty("struts.boot.session.cookie.sameSite", "unset");

        assertThat(BootSettings.load(file, new String[0]).sessionCookieSameSite()).isEqualTo("unset");
    }

    @Test
    public void testSessionCookieSameSiteArgumentBeatsFile() {
        Properties file = new Properties();
        file.setProperty("struts.boot.session.cookie.sameSite", "strict");

        BootSettings settings = BootSettings.load(file, new String[]{"--struts.boot.session.cookie.sameSite=none"});

        assertThat(settings.sessionCookieSameSite()).isEqualTo("none");
    }

    private static boolean sessionCookieSecure(String value) {
        return BootSettings.load(new Properties(), new String[]{"--struts.boot.session.cookie.secure=" + value})
                .sessionCookieSecure();
    }

    private static String sessionCookieSameSite(String value) {
        return BootSettings.load(new Properties(), new String[]{"--struts.boot.session.cookie.sameSite=" + value})
                .sessionCookieSameSite();
    }

    @Test
    public void testInvalidSessionCookieSecureThrows() {
        for (String value : new String[]{"yes", "1", "", " "}) {
            String[] args = {"--struts.boot.session.cookie.secure=" + value};
            assertThatThrownBy(() -> BootSettings.load(new Properties(), args))
                    .isInstanceOf(ConfigurationException.class)
                    .hasMessageContaining("struts.boot.session.cookie.secure")
                    .hasMessageContaining("'" + value + "'");
        }
    }

    @Test
    public void testInvalidSessionCookieSameSiteThrows() {
        for (String value : new String[]{"bogus", "true", "", " "}) {
            String[] args = {"--struts.boot.session.cookie.sameSite=" + value};
            assertThatThrownBy(() -> BootSettings.load(new Properties(), args))
                    .isInstanceOf(ConfigurationException.class)
                    .hasMessageContaining("struts.boot.session.cookie.sameSite")
                    .hasMessageContaining("'" + value + "'");
        }
    }

    @Test
    public void testInvalidSessionCookieSecureFromFileThrows() {
        Properties file = new Properties();
        file.setProperty("struts.boot.session.cookie.secure", "on");

        assertThatThrownBy(() -> BootSettings.load(file, new String[0]))
                .isInstanceOf(ConfigurationException.class)
                .hasMessageContaining("struts.boot.session.cookie.secure")
                .hasMessageContaining("'on'");
    }

    @Test
    public void testInvalidSessionCookieSameSiteFromFileThrows() {
        Properties file = new Properties();
        file.setProperty("struts.boot.session.cookie.sameSite", "relaxed");

        assertThatThrownBy(() -> BootSettings.load(file, new String[0]))
                .isInstanceOf(ConfigurationException.class)
                .hasMessageContaining("struts.boot.session.cookie.sameSite")
                .hasMessageContaining("'relaxed'");
    }
}
