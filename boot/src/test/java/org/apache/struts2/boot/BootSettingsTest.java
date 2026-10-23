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
    public void testNonNumericPortThrows() {
        assertThatThrownBy(() -> BootSettings.load(new Properties(), new String[]{"--struts.boot.port=abc"}))
                .isInstanceOf(ConfigurationException.class);
    }
}
