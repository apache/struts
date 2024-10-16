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
package org.apache.struts2.config.impl;

import org.apache.struts2.config.Configuration;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.config.ContainerProvider;
import org.apache.struts2.config.PackageProvider;
import org.apache.struts2.config.RuntimeConfiguration;
import org.apache.struts2.config.entities.PackageConfig;
import org.apache.struts2.config.entities.UnknownHandlerConfig;
import org.apache.struts2.config.providers.StrutsDefaultConfigurationProvider;
import org.apache.struts2.inject.Container;
import org.apache.struts2.inject.ContainerBuilder;
import org.apache.struts2.inject.Scope;
import org.apache.struts2.util.location.LocatableProperties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Simple configuration used for unit testing
 */
public class MockConfiguration implements Configuration {

    private final Map<String, PackageConfig> packages = new HashMap<>();
    private final Set<String> loadedFiles = new HashSet<>();
    private Container container;
    protected List<UnknownHandlerConfig> unknownHandlerStack;
    private final ContainerBuilder builder;

    public MockConfiguration() {
        builder = new ContainerBuilder();
    }

    public void selfRegister() {
        //this cannot be done in the constructor, as it causes an infinite loop
        builder.factory(Configuration.class, MockConfiguration.class, Scope.SINGLETON);
        LocatableProperties props = new LocatableProperties();
        new StrutsDefaultConfigurationProvider().register(builder, props);
        for (Map.Entry<String, Object> entry : DefaultConfiguration.BOOTSTRAP_CONSTANTS.entrySet()) {
            builder.constant(entry.getKey(), String.valueOf(entry.getValue()));
        }
        container = builder.create(true);
    }

    @Override
    public PackageConfig getPackageConfig(String name) {
        return packages.get(name);
    }

    @Override
    public Set<String> getPackageConfigNames() {
        return packages.keySet();
    }

    @Override
    public Map<String, PackageConfig> getPackageConfigs() {
        return packages;
    }

    @Override
    public RuntimeConfiguration getRuntimeConfiguration() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addPackageConfig(String name, PackageConfig packageContext) {
        packages.put(name, packageContext);
    }

    @Override
    public void destroy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void rebuildRuntimeConfiguration() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PackageConfig removePackageConfig(String name) {
        return packages.remove(name);
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public Set<String> getLoadedFileNames() {
        return loadedFiles;
    }

    @Override
    public List<PackageProvider> reloadContainer(List<ContainerProvider> containerProviders) throws ConfigurationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<UnknownHandlerConfig> getUnknownHandlerStack() {
        return unknownHandlerStack;
    }

    @Override
    public void setUnknownHandlerStack(List<UnknownHandlerConfig> unknownHandlerStack) {
        this.unknownHandlerStack = unknownHandlerStack;
    }

}
