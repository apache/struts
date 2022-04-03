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
package com.opensymphony.xwork2.config.impl;

import com.opensymphony.xwork2.config.*;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.UnknownHandlerConfig;
import com.opensymphony.xwork2.config.providers.StrutsDefaultConfigurationProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import org.apache.struts2.StrutsConstants;

import java.util.*;


/**
 * Simple configuration used for unit testing
 */
public class MockConfiguration implements Configuration {

    private Map<String, PackageConfig> packages = new HashMap<>();
    private Set<String> loadedFiles = new HashSet<>();
    private Container container;
    protected List<UnknownHandlerConfig> unknownHandlerStack;
    private ContainerBuilder builder;

    public MockConfiguration() {
        builder = new ContainerBuilder();
    }

    public void selfRegister() {
        //this cannot be done in the constructor, as it causes an infinite loop
        builder.factory(Configuration.class, MockConfiguration.class, Scope.SINGLETON);
        LocatableProperties props = new LocatableProperties();
        new StrutsDefaultConfigurationProvider().register(builder, props);
        builder.constant(StrutsConstants.STRUTS_DEVMODE, "false");
        builder.constant(StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD, "true");
        builder.constant(StrutsConstants.STRUTS_OGNL_ENABLE_EXPRESSION_CACHE, "true");
        builder.constant(StrutsConstants.STRUTS_ENABLE_DYNAMIC_METHOD_INVOCATION, "false");
        container = builder.create(true);
    }

    public PackageConfig getPackageConfig(String name) {
        return packages.get(name);
    }

    public Set<String> getPackageConfigNames() {
        return packages.keySet();
    }

    public Map<String, PackageConfig> getPackageConfigs() {
        return packages;
    }

    public RuntimeConfiguration getRuntimeConfiguration() {
        throw new UnsupportedOperationException();
    }

    public void addPackageConfig(String name, PackageConfig packageContext) {
        packages.put(name, packageContext);
    }

    public void buildRuntimeConfiguration() {
        throw new UnsupportedOperationException();
    }

    public void destroy() {
        throw new UnsupportedOperationException();
    }

    public void rebuildRuntimeConfiguration() {
        throw new UnsupportedOperationException();
    }

    public PackageConfig removePackageConfig(String name) {
        return packages.remove(name);
    }

    public Container getContainer() {
        return container;
    }

    public Set<String> getLoadedFileNames() {
        return loadedFiles;
    }

    public List<PackageProvider> reloadContainer(
            List<ContainerProvider> containerProviders)
            throws ConfigurationException {
        throw new UnsupportedOperationException();
    }

    public List<UnknownHandlerConfig> getUnknownHandlerStack() {
        return unknownHandlerStack;
    }

    public void setUnknownHandlerStack(List<UnknownHandlerConfig> unknownHandlerStack) {
        this.unknownHandlerStack = unknownHandlerStack;
    }

}
