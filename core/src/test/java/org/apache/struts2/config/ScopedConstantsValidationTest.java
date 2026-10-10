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
package org.apache.struts2.config;

import org.apache.struts2.ActionContext;
import org.apache.struts2.config.providers.StrutsDefaultConfigurationProvider;
import org.apache.struts2.inject.Container;
import org.junit.After;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ScopedConstantsValidationTest {

    private ConfigurationManager configurationManager;

    @After
    public void tearDown() {
        if (configurationManager != null) {
            configurationManager.destroyConfiguration();
        }
        ActionContext.clear();
    }

    @Test
    public void registeredNameLoads() {
        Configuration configuration = load("scoped-constants-registered.xml");

        assertThat(configuration.getPackageConfig("registered").getScopedConstants())
                .isEqualTo(Map.of("sample.scopable", "x"));
    }

    @Test
    public void unregisteredNameFails() {
        assertThatThrownBy(() -> load("scoped-constants-unregistered.xml"))
                .isInstanceOf(ConfigurationException.class)
                .hasStackTraceContaining("Package [unregistered] declares scoped constant [sample.unknown], which is not scopable. "
                        + "Scopable constants: [sample.scopable, sample.unset].");
    }

    @Test
    public void abstractPackageIsValidatedToo() {
        assertThatThrownBy(() -> load("scoped-constants-abstract-unregistered.xml"))
                .isInstanceOf(ConfigurationException.class)
                .hasStackTraceContaining("Package [abstractBase] declares scoped constant [sample.unknown], which is not scopable.");
    }

    @Test
    public void anyScopedConstantFailsWhenNothingIsScopable() {
        assertThatThrownBy(() -> load("scoped-constants-no-registry.xml"))
                .isInstanceOf(ConfigurationException.class)
                .hasStackTraceContaining("Package [unregistered] declares scoped constant [sample.scopable], which is not scopable. "
                        + "No constants are scopable in this configuration.");
    }

    @Test
    public void namesFromAllBeansAreScopable() {
        Configuration configuration = load("scoped-constants-two-beans.xml");

        assertThat(configuration.getPackageConfig("both").getScopedConstants())
                .isEqualTo(Map.of("sample.scopable", "x", "second.scopable", "y"));
    }

    private Configuration load(String fixture) {
        configurationManager = new ConfigurationManager(Container.DEFAULT_NAME);
        configurationManager.addContainerProvider(new StrutsDefaultConfigurationProvider());
        configurationManager.addContainerProvider(new StrutsXmlConfigurationProvider("org/apache/struts2/config/" + fixture));
        return configurationManager.getConfiguration();
    }
}
