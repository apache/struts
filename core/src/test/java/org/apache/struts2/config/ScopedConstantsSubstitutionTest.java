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
import org.apache.struts2.inject.Container;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ScopedConstantsSubstitutionTest {

    private static final String PROPERTY = "ww5767.value";

    private ConfigurationManager configurationManager;

    @Before
    public void setUp() {
        System.setProperty(PROPERTY, "from-system-property");
    }

    @After
    public void tearDown() {
        System.clearProperty(PROPERTY);
        if (configurationManager != null) {
            configurationManager.destroyConfiguration();
        }
        ActionContext.clear();
    }

    @Test
    public void valuesAreSubstitutedLikeGlobalConstants() {
        configurationManager = new ConfigurationManager(Container.DEFAULT_NAME);
        configurationManager.addContainerProvider(new DefaultPropertiesProvider());
        configurationManager.addContainerProvider(new StrutsXmlConfigurationProvider("struts-default.xml"));
        configurationManager.addContainerProvider(new StrutsXmlConfigurationProvider(
                "org/apache/struts2/config/struts-scoped-constants-substitution.xml"));
        configurationManager.addContainerProvider(new StrutsBeanSelectionProvider());

        Configuration configuration = configurationManager.getConfiguration();

        assertThat(configuration.getPackageConfig("substituted").getScopedConstants())
                .isEqualTo(Map.of("sample.scopable", "from-system-property", "sample.unset", "literal"));
    }
}
