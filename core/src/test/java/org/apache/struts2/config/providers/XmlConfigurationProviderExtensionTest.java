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
package org.apache.struts2.config.providers;

import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.apache.struts2.config.entities.ActionConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class XmlConfigurationProviderExtensionTest extends StrutsInternalTestCase {

    private static final String CONFIG = "org/apache/struts2/config/providers/struts-extension.xml";
    private static final String INVALID_CONFIG = "org/apache/struts2/config/providers/struts-extension-invalid.xml";

    public void testActionInheritsPackageChainExtension() {
        loadConfigurationProviders(new StrutsXmlConfigurationProvider(CONFIG));

        assertThat(actionConfig("/child", "inherits").getExtensions()).isEqualTo(List.of("html"));
    }

    public void testActionOverridesPackage() {
        loadConfigurationProviders(new StrutsXmlConfigurationProvider(CONFIG));

        assertThat(actionConfig("/child", "own").getExtensions()).isEqualTo(List.of("json", ""));
    }

    public void testUndeclaredIsNull() {
        loadConfigurationProviders(new StrutsXmlConfigurationProvider(CONFIG));

        assertThat(actionConfig("/plain", "undeclared").getExtensions()).isNull();
    }

    public void testEmptyAttributeMeansNoExtensionOnly() {
        loadConfigurationProviders(new StrutsXmlConfigurationProvider(CONFIG));

        assertThat(actionConfig("/plain", "none").getExtensions()).isEqualTo(List.of(""));
    }

    public void testWildcardMatchCopiesExtensions() {
        loadConfigurationProviders(new StrutsXmlConfigurationProvider(CONFIG));

        assertThat(actionConfig("/wild", "edit_user").getExtensions()).isEqualTo(List.of("html"));
    }

    public void testLeadingDotRejected() {
        StrutsXmlConfigurationProvider provider = new StrutsXmlConfigurationProvider(INVALID_CONFIG);
        assertThatThrownBy(() -> loadConfigurationProviders(provider))
            .isInstanceOf(ConfigurationException.class)
            .hasStackTraceContaining(".html");
    }

    private ActionConfig actionConfig(String namespace, String name) {
        return configuration.getRuntimeConfiguration().getActionConfig(namespace, name);
    }
}
