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
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;

public class DefaultActionProxyTest extends StrutsInternalTestCase {

    public void testThrowExceptionOnNotAllowedMethod() {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-allowed-methods.xml";
        loadConfigurationProviders(new StrutsXmlConfigurationProvider(filename));
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
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-allowed-methods.xml";
        loadConfigurationProviders(new StrutsXmlConfigurationProvider(filename));
        DefaultActionProxy dap = new DefaultActionProxy(new MockActionInvocation(), "", "NoMethod", "onPostOnly", true, true);
        container.inject(dap);
        dap.prepare();

        assertTrue("Method passed explicitly should be marked as specified", dap.isMethodSpecified());
        assertEquals("onPostOnly", dap.getMethod());
    }

    public void testMethodSpecifiedWhenResolvedFromConfig() {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-allowed-methods.xml";
        loadConfigurationProviders(new StrutsXmlConfigurationProvider(filename));
        DefaultActionProxy dap = new DefaultActionProxy(new MockActionInvocation(), "", "ConfigMethod", null, true, true);
        container.inject(dap);
        dap.prepare();

        assertTrue("Method resolved from action config should be marked as specified", dap.isMethodSpecified());
        assertEquals("onPostOnly", dap.getMethod());
    }

    public void testMethodNotSpecifiedWhenDefaultingToExecute() {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-allowed-methods.xml";
        loadConfigurationProviders(new StrutsXmlConfigurationProvider(filename));
        DefaultActionProxy dap = new DefaultActionProxy(new MockActionInvocation(), "", "NoMethod", null, true, true);
        container.inject(dap);
        dap.prepare();

        assertFalse("Method defaulting to execute should not be marked as specified", dap.isMethodSpecified());
        assertEquals("execute", dap.getMethod());
    }

    public void testMethodSpecifiedWithWildcardAction() {
        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-allowed-methods.xml";
        loadConfigurationProviders(new StrutsXmlConfigurationProvider(filename));
        DefaultActionProxy dap = new DefaultActionProxy(new MockActionInvocation(), "", "Wild-onPostOnly", null, true, true);
        container.inject(dap);
        dap.prepare();

        assertTrue("Method resolved from wildcard should be marked as specified", dap.isMethodSpecified());
        assertEquals("onPostOnly", dap.getMethod());
    }
}