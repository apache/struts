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

import jakarta.servlet.ServletContext;
import org.apache.struts2.ObjectFactory;
import org.apache.struts2.config.Configuration;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.config.ConfigurationUtil;
import org.apache.struts2.config.ServletContextAwareConfigurationProvider;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.config.entities.PackageConfig;
import org.apache.struts2.config.entities.ResultConfig;
import org.apache.struts2.config.providers.InterceptorBuilder;
import org.apache.struts2.inject.ContainerBuilder;
import org.apache.struts2.ognl.ProviderAllowlist;
import org.apache.struts2.util.ClassLoaderUtil;
import org.apache.struts2.util.location.LocatableProperties;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class RegistryConfigurationProvider implements ServletContextAwareConfigurationProvider {

    private static final String PACKAGE_PREFIX = "struts-boot";
    private static final String TEMPLATE_ROOT = "templates/";

    private ActionRegistry registry;
    private Configuration configuration;
    private ProviderAllowlist providerAllowlist;

    public RegistryConfigurationProvider() {
    }

    RegistryConfigurationProvider(ActionRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void initWithContext(ServletContext servletContext) {
        Object attribute = servletContext.getAttribute(ActionRegistry.SERVLET_CONTEXT_ATTRIBUTE);
        if (!(attribute instanceof ActionRegistry actionRegistry)) {
            throw new ConfigurationException("No ActionRegistry found in the servlet context attribute "
                    + ActionRegistry.SERVLET_CONTEXT_ATTRIBUTE);
        }
        this.registry = actionRegistry;
    }

    @Override
    public void init(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void register(ContainerBuilder builder, LocatableProperties props) {
        // the registry contributes packages only, no beans or constants
    }

    @Override
    public boolean needsReload() {
        return false;
    }

    @Override
    public void destroy() {
        if (providerAllowlist != null) {
            providerAllowlist.clearAllowlist(this);
        }
    }

    @Override
    public void loadPackages() {
        PackageConfig strutsDefault = configuration.getPackageConfig("struts-default");
        ObjectFactory objectFactory = configuration.getContainer().getInstance(ObjectFactory.class);
        String freemarker = strutsDefault.getAllResultTypeConfigs().get("freemarker").getClassName();
        String redirectAction = strutsDefault.getAllResultTypeConfigs().get("redirectAction").getClassName();
        List<PackageConfig> existing = new ArrayList<>(configuration.getPackageConfigs().values());
        Set<Class<?>> allowlist = new HashSet<>();

        for (NamespaceActions namespace : registry.namespaces()) {
            String packageName = namespace.namespace().isEmpty() ? PACKAGE_PREFIX : PACKAGE_PREFIX + namespace.namespace();
            PackageConfig.Builder pkg = new PackageConfig.Builder(packageName)
                    .namespace(namespace.namespace())
                    .addParent(strutsDefault);

            for (ActionDefinition definition : namespace.actions()) {
                assertNoClash(existing, namespace.namespace(), definition.name());
                if (objectFactory.isNoArgConstructorRequired()) {
                    verifyActionClass(namespace.namespace(), definition);
                }
                allowlist.addAll(ConfigurationUtil.getAllClassTypes(definition.actionClass()));
                pkg.addActionConfig(definition.name(),
                        actionConfig(packageName, namespace, definition, pkg, objectFactory, freemarker, redirectAction));
            }
            configuration.addPackageConfig(packageName, pkg.build());
        }
        providerAllowlist = configuration.getContainer().getInstance(ProviderAllowlist.class);
        providerAllowlist.registerAllowlist(this, allowlist);
    }

    private ActionConfig actionConfig(String packageName, NamespaceActions namespace, ActionDefinition definition,
                                      PackageConfig.Builder pkg, ObjectFactory objectFactory,
                                      String freemarker, String redirectAction) {
        ActionConfig.Builder action = new ActionConfig.Builder(packageName, definition.name(), definition.actionClass().getName())
                .methodName(definition.methodName())
                .addAllowedMethod(definition.methodName());
        if (!definition.httpMethodSet().isEmpty()) {
            action.addHttpMethods(definition.httpMethodSet());
        }
        Optional<String> stack = definition.interceptorStack().or(namespace::interceptorStack);
        if (stack.isPresent()) {
            action.addInterceptors(InterceptorBuilder.constructInterceptorReference(
                    pkg, stack.get(), Map.of(), null, objectFactory));
        }
        definition.results().forEach((resultName, result) ->
                action.addResultConfig(resultConfig(definition, resultName, result, freemarker, redirectAction)));
        return action.build();
    }

    private ResultConfig resultConfig(ActionDefinition definition, String resultName,
                                      ActionDefinition.ResultDefinition result, String freemarker, String redirectAction) {
        return switch (result.kind()) {
            case TEMPLATE -> {
                String resource = TEMPLATE_ROOT + result.value();
                if (ClassLoaderUtil.getResource(resource, RegistryConfigurationProvider.class) == null) {
                    throw new ConfigurationException("Action '" + definition.name() + "' result '" + resultName
                            + "' refers to the template '" + resource + "' which is not on the classpath");
                }
                yield new ResultConfig.Builder(resultName, freemarker).addParam("location", "/" + resource).build();
            }
            case REDIRECT_ACTION -> new ResultConfig.Builder(resultName, redirectAction)
                    .addParam("actionName", result.value()).build();
        };
    }

    private static void verifyActionClass(String namespace, ActionDefinition definition) {
        Class<?> actionClass = definition.actionClass();
        String subject = "Action class [" + actionClass.getName() + "] of action '" + definition.name()
                + "' in namespace '" + namespace + "'";
        if (!Modifier.isPublic(actionClass.getModifiers())) {
            throw new ConfigurationException(subject + " is not public");
        }
        try {
            actionClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new ConfigurationException(subject + " does not have a public no-arg constructor", e);
        }
    }

    private void assertNoClash(List<PackageConfig> existing, String namespace, String actionName) {
        for (PackageConfig other : existing) {
            if (!other.isAbstract() && namespace.equals(other.getNamespace())
                    && other.getActionConfigs().containsKey(actionName)) {
                throw new ConfigurationException("Action '" + actionName + "' in namespace '" + namespace
                        + "' is declared both in the action registry and in the XML package '" + other.getName() + "'");
            }
        }
    }
}
