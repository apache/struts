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
import org.apache.struts2.ActionSupport;
import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.mock.MockActionInvocation;
import org.apache.struts2.mock.MockActionProxy;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StrutsScopedConstantProviderTest extends XWorkTestCase {

    private ScopedConstantProvider provider;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loadConfigurationProviders(new StrutsXmlConfigurationProvider("org/apache/struts2/config/struts-scoped-constants-provider.xml"));
        provider = container.getInstance(ScopedConstantProvider.class);
    }

    public void testDefaultProviderIsStrutsImplementation() {
        assertTrue(provider instanceof StrutsScopedConstantProvider);
    }

    public void testPackageValueWinsInsideInvocation() {
        enterPackage("override");

        assertEquals("override", provider.getValue("sample.scopable"));
    }

    public void testInheritedValueResolvesForChildPackage() {
        enterPackage("child");

        assertEquals("base", provider.getValue("sample.scopable"));
    }

    public void testEmptyOverrideIsReturnedAsIs() {
        enterPackage("empty");

        assertEquals("", provider.getValue("sample.scopable"));
    }

    public void testGlobalValueWithoutOverride() {
        enterPackage("plain");

        assertEquals("global", provider.getValue("sample.scopable"));
    }

    public void testGlobalValueWithoutInvocation() {
        assertEquals("global", provider.getValue("sample.scopable"));
    }

    public void testGlobalValueWithoutActionContext() {
        ActionContext context = ActionContext.getContext();
        ActionContext.clear();
        try {
            assertEquals("global", provider.getValue("sample.scopable"));
        } finally {
            context.bind();
        }
    }

    public void testGlobalValueWhenInvocationHasNoProxy() {
        ActionContext.getContext().withActionInvocation(new MockActionInvocation());

        assertEquals("global", provider.getValue("sample.scopable"));
    }

    public void testGlobalValueWhenProxyHasNoConfig() {
        MockActionInvocation invocation = new MockActionInvocation();
        invocation.setProxy(new MockActionProxy());
        ActionContext.getContext().withActionInvocation(invocation);

        assertEquals("global", provider.getValue("sample.scopable"));
    }

    public void testGlobalValueWhenPackageIsUnknown() {
        enterPackage("missing");

        assertEquals("global", provider.getValue("sample.scopable"));
    }

    public void testUnsetConstantWithoutOverrideIsNull() {
        enterPackage("plain");

        assertNull(provider.getValue("sample.unset"));
    }

    public void testUnregisteredNameThrows() {
        assertThatThrownBy(() -> provider.getValue("sample.unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("[sample.unknown]");
    }

    public void testGlobalConstantUntouchedByScopedRead() {
        enterPackage("override");
        provider.getValue("sample.scopable");

        assertEquals("global", container.getInstance(String.class, "sample.scopable"));
    }

    private void enterPackage(String packageName) {
        MockActionProxy proxy = new MockActionProxy();
        proxy.setConfig(new ActionConfig.Builder(packageName, "test", ActionSupport.class.getName()).build());
        MockActionInvocation invocation = new MockActionInvocation();
        invocation.setProxy(proxy);
        ActionContext.getContext().withActionInvocation(invocation);
    }
}
