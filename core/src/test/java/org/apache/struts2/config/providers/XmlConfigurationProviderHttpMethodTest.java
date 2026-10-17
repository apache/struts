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
import org.apache.struts2.config.impl.DefaultConfiguration;
import org.apache.struts2.test.LogCapture;

import static org.apache.struts2.interceptor.httpmethod.HttpMethod.GET;
import static org.apache.struts2.interceptor.httpmethod.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class XmlConfigurationProviderHttpMethodTest extends StrutsInternalTestCase {

    private static final String CONFIG = "org/apache/struts2/config/providers/struts-http-method.xml";
    private static final String INVALID_CONFIG = "org/apache/struts2/config/providers/struts-http-method-invalid.xml";

    public void testSingleHttpMethod() {
        loadConfigurationProviders(new StrutsXmlConfigurationProvider(CONFIG));

        assertThat(actionConfig("", "PostOnly").getHttpMethods()).containsExactly(POST);
    }

    public void testHttpMethodListIsTrimmedAndCaseInsensitive() {
        loadConfigurationProviders(new StrutsXmlConfigurationProvider(CONFIG));

        assertThat(actionConfig("", "GetOrPost").getHttpMethods()).containsExactlyInAnyOrder(GET, POST);
    }

    public void testNoHttpMethodMeansUnrestricted() {
        loadConfigurationProviders(new StrutsXmlConfigurationProvider(CONFIG));

        assertThat(actionConfig("", "Unrestricted").getHttpMethods()).isEmpty();
    }

    public void testWildcardActionKeepsHttpMethod() {
        loadConfigurationProviders(new StrutsXmlConfigurationProvider(CONFIG));

        ActionConfig resolved = actionConfig("", "PostWild-execute");

        assertThat(resolved.getName()).isEqualTo("PostWild-*");
        assertThat(resolved.getMethodName()).isEqualTo("execute");
        assertThat(resolved.getHttpMethods()).containsExactly(POST);
    }

    public void testInvalidHttpMethodFailsConfiguration() {
        StrutsXmlConfigurationProvider provider = new StrutsXmlConfigurationProvider(INVALID_CONFIG);
        assertThatThrownBy(() -> loadConfigurationProviders(provider))
            .isInstanceOf(ConfigurationException.class)
            .hasStackTraceContaining("Invalid http-method [fetch] on action [Fetch]");
    }

    public void testWarnsWhenStackHasNoHttpMethodInterceptor() {
        try (LogCapture log = new LogCapture(DefaultConfiguration.class)) {
            loadConfigurationProviders(new StrutsXmlConfigurationProvider(CONFIG));

            assertThat(log.messages())
                .anyMatch(message -> message.contains("[PostOnlyWithoutInterceptor]") && message.contains("http-method"))
                .anyMatch(message -> message.contains("[PostOnlyWithoutStack]") && message.contains("http-method"))
                .noneMatch(message -> message.contains("[PostOnly]"))
                .noneMatch(message -> message.contains("[Unrestricted]"));
        }
    }

    private ActionConfig actionConfig(String namespace, String name) {
        return configuration.getRuntimeConfiguration().getActionConfig(namespace, name);
    }
}
