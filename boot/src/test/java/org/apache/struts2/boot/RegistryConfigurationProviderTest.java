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

import org.apache.struts2.config.Configuration;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.config.ConfigurationManager;
import org.apache.struts2.config.DefaultPropertiesProvider;
import org.apache.struts2.config.StrutsBeanSelectionProvider;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.config.entities.PackageConfig;
import org.apache.struts2.config.entities.ResultConfig;
import org.apache.struts2.boot.fixture.UserAction;
import org.apache.struts2.inject.Container;
import org.apache.struts2.ActionContext;
import org.junit.After;
import org.junit.Test;

import static org.apache.struts2.boot.HttpMethods.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RegistryConfigurationProviderTest {

    private ConfigurationManager configurationManager;

    @After
    public void tearDown() {
        if (configurationManager != null) {
            configurationManager.destroyConfiguration();
        }
        ActionContext.clear();
    }

    private Configuration load(ActionRegistry registry, String... extraXml) {
        configurationManager = new ConfigurationManager(Container.DEFAULT_NAME);
        configurationManager.addContainerProvider(new DefaultPropertiesProvider());
        configurationManager.addContainerProvider(new StrutsXmlConfigurationProvider("struts-default.xml"));
        for (String xml : extraXml) {
            configurationManager.addContainerProvider(new StrutsXmlConfigurationProvider(xml));
        }
        configurationManager.addContainerProvider(new StrutsBeanSelectionProvider());
        configurationManager.addContainerProvider(new RegistryConfigurationProvider(registry));
        return configurationManager.getConfiguration();
    }

    private ActionRegistry usersRegistry() {
        ActionRegistry registry = new ActionRegistry();
        registry.namespace("/admin", ns -> ns.action("users", UserAction.class).success("admin/users.ftl"));
        return registry;
    }

    private ActionConfig action(Configuration configuration, String pkg, String name) {
        return configuration.getPackageConfig(pkg).getActionConfigs().get(name);
    }

    private String resultType(Configuration configuration, String type) {
        return configuration.getPackageConfig("struts-default").getAllResultTypeConfigs().get(type).getClassName();
    }

    @Test
    public void testPackageExtendsStrutsDefaultWithNamespace() {
        Configuration configuration = load(usersRegistry());

        PackageConfig pkg = configuration.getPackageConfig("struts-boot/admin");

        assertThat(pkg.getNamespace()).isEqualTo("/admin");
        assertThat(pkg.getParents()).extracting(PackageConfig::getName).containsExactly("struts-default");
        assertThat(pkg.isStrictMethodInvocation()).isTrue();
    }

    @Test
    public void testRootNamespacePackageName() {
        ActionRegistry registry = new ActionRegistry();
        registry.action("hello", UserAction.class).success("hello.ftl");

        Configuration configuration = load(registry);

        assertThat(configuration.getPackageConfig("struts-boot").getNamespace()).isEmpty();
    }

    @Test
    public void testMethodIsAddedToAllowedMethods() {
        ActionRegistry registry = new ActionRegistry();
        registry.namespace("/admin", ns -> ns.action("users", UserAction.class).method("list").success("admin/users.ftl"));

        ActionConfig action = action(load(registry), "struts-boot/admin", "users");

        assertThat(action.getMethodName()).isEqualTo("list");
        assertThat(action.getAllowedMethods()).contains("list").doesNotContain("delete");
    }

    @Test
    public void testTemplateResultIsAbsoluteFreemarker() {
        Configuration configuration = load(usersRegistry());

        ResultConfig result = action(configuration, "struts-boot/admin", "users").getResults().get("success");

        assertThat(result.getClassName()).isEqualTo(resultType(configuration, "freemarker"));
        assertThat(result.getParams()).containsEntry("location", "/templates/admin/users.ftl");
    }

    @Test
    public void testRedirectActionResult() {
        ActionRegistry registry = new ActionRegistry();
        registry.namespace("/admin", ns -> ns.action("save", UserAction.class).method("save").redirectAction("success", "users"));
        Configuration configuration = load(registry);

        ResultConfig result = action(configuration, "struts-boot/admin", "save").getResults().get("success");

        assertThat(result.getClassName()).isEqualTo(resultType(configuration, "redirectAction"));
        assertThat(result.getParams()).containsEntry("actionName", "users");
    }

    @Test
    public void testHttpMethodsCarried() {
        ActionRegistry registry = new ActionRegistry();
        registry.namespace("/admin", ns -> ns.action("save", UserAction.class).method("save").httpMethods(POST).success("admin/users.ftl"));

        ActionConfig action = action(load(registry), "struts-boot/admin", "save");

        assertThat(action.getHttpMethods()).containsExactly(POST);
    }

    @Test
    public void testNoStackMeansPackageDefault() throws Exception {
        Configuration configuration = load(usersRegistry());

        assertThat(action(configuration, "struts-boot/admin", "users").getInterceptors()).isEmpty();
        assertThat(configuration.getRuntimeConfiguration().getActionConfig("/admin", "users").getInterceptors()).isNotEmpty();
    }

    @Test
    public void testNamedStackResolved() {
        ActionRegistry registry = new ActionRegistry();
        registry.namespace("/admin", ns -> ns.action("users", UserAction.class).interceptors("basicStack").success("admin/users.ftl"));

        ActionConfig action = action(load(registry), "struts-boot/admin", "users");

        assertThat(action.getInterceptors()).isNotEmpty();
    }

    @Test
    public void testNamespaceStackAppliesToActions() {
        ActionRegistry registry = new ActionRegistry();
        registry.namespace("/admin", ns -> {
            ns.interceptors("basicStack");
            ns.action("users", UserAction.class).success("admin/users.ftl");
        });

        assertThat(action(load(registry), "struts-boot/admin", "users").getInterceptors()).isNotEmpty();
    }

    @Test
    public void testUnknownStackThrows() {
        ActionRegistry registry = new ActionRegistry();
        registry.namespace("/admin", ns -> ns.action("users", UserAction.class).interceptors("noSuchStack").success("admin/users.ftl"));

        assertThatThrownBy(() -> load(registry)).isInstanceOf(ConfigurationException.class);
    }

    @Test
    public void testMissingTemplateThrows() {
        ActionRegistry registry = new ActionRegistry();
        registry.namespace("/admin", ns -> ns.action("users", UserAction.class).success("missing.ftl"));

        assertThatThrownBy(() -> load(registry))
                .isInstanceOf(ConfigurationException.class)
                .rootCause().hasMessageContaining("templates/missing.ftl");
    }

    @Test
    public void testClashWithXmlThrows() {
        assertThatThrownBy(() -> load(usersRegistry(), "struts-boot-clash.xml"))
                .isInstanceOf(ConfigurationException.class)
                .rootCause().hasMessageContaining("xml-pkg").hasMessageContaining("users");
    }

    @Test
    public void testSameNamespaceAsXmlWithDistinctNamesLoads() throws Exception {
        ActionRegistry registry = new ActionRegistry();
        registry.namespace("/admin", ns -> ns.action("list", UserAction.class).method("list").success("admin/users.ftl"));

        Configuration configuration = load(registry, "struts-boot-clash.xml");

        assertThat(configuration.getRuntimeConfiguration().getActionConfig("/admin", "users")).isNotNull();
        assertThat(configuration.getRuntimeConfiguration().getActionConfig("/admin", "list")).isNotNull();
    }
}
