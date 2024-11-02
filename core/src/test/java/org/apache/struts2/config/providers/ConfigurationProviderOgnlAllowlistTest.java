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

import org.apache.struts2.XWorkJUnit4TestCase;
import org.apache.struts2.config.ConfigurationProvider;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.apache.struts2.ognl.ProviderAllowlist;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationProviderOgnlAllowlistTest extends XWorkJUnit4TestCase {

    private final ConfigurationProvider testXml1 = new StrutsXmlConfigurationProvider("org/apache/struts2/config/providers/xwork-test-allowlist.xml");
    private final ConfigurationProvider testXml2 = new StrutsXmlConfigurationProvider("org/apache/struts2/config/providers/xwork-test-allowlist-2.xml");
    private ProviderAllowlist providerAllowlist;

    @Before
    public void setUp() throws Exception {
        loadConfigurationProviders(testXml1, testXml2);
        providerAllowlist = container.getInstance(ProviderAllowlist.class);
    }

    @Test
    public void allowList() throws Exception {
        loadConfigurationProviders(testXml1, testXml2);
        providerAllowlist = container.getInstance(ProviderAllowlist.class);

        assertThat(providerAllowlist.getProviderAllowlist()).containsExactlyInAnyOrder(
                Class.forName("org.apache.struts2.interceptor.ValidationAware"),
                Class.forName("org.apache.struts2.locale.LocaleProvider"),
                Class.forName("java.io.Serializable"),
                Class.forName("org.apache.struts2.mock.MockResult"),
                Class.forName("org.apache.struts2.interceptor.ConditionalInterceptor"),
                Class.forName("org.apache.struts2.ActionSupport"),
                Class.forName("org.apache.struts2.result.ActionChainResult"),
                Class.forName("org.apache.struts2.text.TextProvider"),
                Class.forName("org.apache.struts2.interceptor.NoOpInterceptor"),
                Class.forName("org.apache.struts2.interceptor.Interceptor"),
                Class.forName("java.lang.Object"),
                Class.forName("org.apache.struts2.Validateable"),
                Class.forName("org.apache.struts2.mock.MockInterceptor"),
                Class.forName("org.apache.struts2.action.Action"),
                Class.forName("org.apache.struts2.interceptor.AbstractInterceptor"),
                Class.forName("org.apache.struts2.result.Result"),
                Class.forName("org.apache.struts2.SimpleAction")
        );
    }

    @Test
    public void allowlist_1only() throws Exception {
        loadConfigurationProviders(testXml1);
        providerAllowlist = container.getInstance(ProviderAllowlist.class);

        assertThat(providerAllowlist.getProviderAllowlist()).containsExactlyInAnyOrder(
                Class.forName("org.apache.struts2.interceptor.ValidationAware"),
                Class.forName("org.apache.struts2.locale.LocaleProvider"),
                Class.forName("java.io.Serializable"),
                Class.forName("org.apache.struts2.mock.MockResult"),
                Class.forName("org.apache.struts2.interceptor.ConditionalInterceptor"),
                Class.forName("org.apache.struts2.ActionSupport"),
                Class.forName("org.apache.struts2.text.TextProvider"),
                Class.forName("org.apache.struts2.interceptor.Interceptor"),
                Class.forName("java.lang.Object"),
                Class.forName("org.apache.struts2.Validateable"),
                Class.forName("org.apache.struts2.mock.MockInterceptor"),
                Class.forName("org.apache.struts2.action.Action"),
                Class.forName("org.apache.struts2.interceptor.AbstractInterceptor"),
                Class.forName("org.apache.struts2.result.Result"),
                Class.forName("org.apache.struts2.SimpleAction")
        );
    }

    @Test
    public void allowlist_2only() throws Exception {
        loadConfigurationProviders(testXml2);
        providerAllowlist = container.getInstance(ProviderAllowlist.class);

        assertThat(providerAllowlist.getProviderAllowlist()).containsExactlyInAnyOrder(
                Class.forName("org.apache.struts2.interceptor.ValidationAware"),
                Class.forName("org.apache.struts2.locale.LocaleProvider"),
                Class.forName("java.io.Serializable"),
                Class.forName("org.apache.struts2.interceptor.ConditionalInterceptor"),
                Class.forName("org.apache.struts2.ActionSupport"),
                Class.forName("org.apache.struts2.result.ActionChainResult"),
                Class.forName("org.apache.struts2.text.TextProvider"),
                Class.forName("org.apache.struts2.interceptor.NoOpInterceptor"),
                Class.forName("org.apache.struts2.interceptor.Interceptor"),
                Class.forName("java.lang.Object"),
                Class.forName("org.apache.struts2.Validateable"),
                Class.forName("org.apache.struts2.action.Action"),
                Class.forName("org.apache.struts2.interceptor.AbstractInterceptor"),
                Class.forName("org.apache.struts2.result.Result")
        );
    }
}
