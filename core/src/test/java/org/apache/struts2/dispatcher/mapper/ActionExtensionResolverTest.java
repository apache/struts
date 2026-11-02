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
package org.apache.struts2.dispatcher.mapper;

import org.apache.struts2.ActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;

import static org.assertj.core.api.Assertions.assertThat;

public class ActionExtensionResolverTest extends StrutsInternalTestCase {

    private static final String CONFIG = "org/apache/struts2/config/providers/struts-extension.xml";

    private ActionExtensionResolver resolver;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loadConfigurationProviders(new StrutsXmlConfigurationProvider(CONFIG));
        resolver = new ActionExtensionResolver();
        resolver.setConfiguration(configuration);
        resolver.setExtensions("action,");
    }

    public void testDeclaredTargetUsesFirstWhenCurrentNotAllowed() {
        withCurrentExtension("action");

        assertThat(resolve("/child", "inherits")).isEqualTo("html");
    }

    public void testDeclaredTargetKeepsAllowedCurrent() {
        withCurrentExtension("json");

        assertThat(resolve("/child", "own")).isEqualTo("json");
    }

    public void testUndeclaredTargetFromDeclaredOnlyPage() {
        withCurrentExtension("html");

        assertThat(resolve("/plain", "undeclared")).isEqualTo("action");
    }

    public void testUndeclaredTargetKeepsGlobalCurrent() {
        withCurrentExtension("");

        assertThat(resolve("/plain", "undeclared")).isEmpty();
    }

    public void testExplicitExtensionUntouched() {
        withCurrentExtension("action");
        ActionMapping target = new ActionMapping("inherits", "/child", null, null);
        target.setExtension("");

        resolver.applyTo(target);

        assertThat(target.getExtension()).isEmpty();
    }

    public void testUnknownActionUntouched() {
        withCurrentExtension("action");

        assertThat(resolve("/nowhere", "x")).isNull();
    }

    public void testDmiNameResolvesActionConfig() {
        withCurrentExtension("action");

        assertThat(resolve("/child", "inherits!foo")).isEqualTo("html");
    }

    public void testUnknownNameResolvedOnlyThroughDeclaringDefaultActionUntouched() {
        loadConfigurationProviders(new StrutsXmlConfigurationProvider("org/apache/struts2/config/providers/struts-extension-default-action.xml"));
        resolver.setConfiguration(configuration);
        withCurrentExtension("action");

        assertThat(resolve("", "manual")).isNull();
        assertThat(resolve("", "index")).isEqualTo("html");
    }

    public void testWithoutCurrentMappingUsesFirstAllowed() {
        assertThat(resolve("/child", "inherits")).isEqualTo("html");
    }

    public void testRequestWithoutExtensionUsesGlobalDefault() {
        withCurrentExtension(null);

        assertThat(resolve("/plain", "undeclared")).isEqualTo("action");
    }

    public void testNoDeclaredAndNoGlobalListUntouched() {
        resolver.setExtensions("");
        withCurrentExtension("action");

        assertThat(resolve("/plain", "undeclared")).isNull();
    }

    public void testNoDeclarationsAnywhereLeavesMappingUntouched() {
        loadConfigurationProviders(new StrutsXmlConfigurationProvider("org/apache/struts2/config/providers/xwork-test-actions.xml"));
        resolver.setConfiguration(configuration);
        withCurrentExtension("xyz");

        assertThat(resolve("", "Bar")).isNull();
    }

    private String resolve(String namespace, String name) {
        ActionMapping target = new ActionMapping(name, namespace, null, null);
        resolver.applyTo(target);
        return target.getExtension();
    }

    private void withCurrentExtension(String extension) {
        ActionMapping current = new ActionMapping("current", "/", null, null);
        current.setExtension(extension);
        ActionContext.getContext().withActionMapping(current);
    }
}
