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
package org.apache.struts2;

import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.apache.struts2.mock.MockActionInvocation;

public class DefaultActionProxyTest extends StrutsInternalTestCase {

    private static final String CONFIG = "org/apache/struts2/config/providers/xwork-test-allowed-methods.xml";

    public void testThrowExceptionOnNotAllowedMethod() {
        loadConfigurationProviders(new StrutsXmlConfigurationProvider(CONFIG));
        DefaultActionProxy dap = new DefaultActionProxy(new MockActionInvocation(), "strict", "Default", "notAllowed", true, true);
        container.inject(dap);

        try {
            dap.prepare();
            fail("Must throw exception!");
        } catch (ConfigurationException e) {
            assertEquals("Method notAllowed for action Default is not allowed!", e.getMessage());
        }
    }

    public void testMethodSpecifiedWhenPassedExplicitly() {
        loadConfigurationProviders(new StrutsXmlConfigurationProvider(CONFIG));
        DefaultActionProxy dap = new DefaultActionProxy(new MockActionInvocation(), "default", "Default", "input", true, true);
        container.inject(dap);
        dap.prepare();

        assertTrue("Method should be specified when passed as constructor argument", dap.isMethodSpecified());
        assertEquals("input", dap.getMethod());
    }

    public void testMethodSpecifiedWhenResolvedFromConfig() {
        loadConfigurationProviders(new StrutsXmlConfigurationProvider(CONFIG));
        // ConfigMethod action has method="onPostOnly" in XML config, no method passed in constructor
        DefaultActionProxy dap = new DefaultActionProxy(new MockActionInvocation(), "default", "ConfigMethod", null, true, true);
        container.inject(dap);
        dap.prepare();

        assertTrue("Method should be specified when resolved from action config", dap.isMethodSpecified());
        assertEquals("onPostOnly", dap.getMethod());
    }

    public void testMethodNotSpecifiedWhenDefaultingToExecute() {
        loadConfigurationProviders(new StrutsXmlConfigurationProvider(CONFIG));
        // NoMethod action has no method in XML config and no method passed in constructor
        DefaultActionProxy dap = new DefaultActionProxy(new MockActionInvocation(), "default", "NoMethod", null, true, true);
        container.inject(dap);
        dap.prepare();

        assertFalse("Method should not be specified when defaulting to execute", dap.isMethodSpecified());
        assertEquals("execute", dap.getMethod());
    }

    public void testMethodSpecifiedWithWildcardAction() {
        loadConfigurationProviders(new StrutsXmlConfigurationProvider(CONFIG));
        // Wild-onPostOnly matches Wild-* with method="{1}" -> resolves to "onPostOnly"
        DefaultActionProxy dap = new DefaultActionProxy(new MockActionInvocation(), "default", "Wild-onPostOnly", null, true, true);
        container.inject(dap);
        dap.prepare();

        assertTrue("Method should be specified when resolved from wildcard config", dap.isMethodSpecified());
        assertEquals("onPostOnly", dap.getMethod());
    }
}