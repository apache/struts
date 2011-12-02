/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.UnknownHandlerConfig;
import com.opensymphony.xwork2.inject.Container;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * XWork configuration.
 *
 * @author Mike
 */
public interface Configuration extends Serializable {

    void rebuildRuntimeConfiguration();

    PackageConfig getPackageConfig(String name);

    Set<String> getPackageConfigNames();

    Map<String, PackageConfig> getPackageConfigs();

    /**
     * The current runtime configuration. Currently, if changes have been made to the Configuration since the last
     * time buildRuntimeConfiguration() was called, you'll need to make sure to.
     *
     * @return the current runtime configuration
     */
    RuntimeConfiguration getRuntimeConfiguration();

    void addPackageConfig(String name, PackageConfig packageConfig);

    /**
     * Removes a package from the the list of packages. Changes to the configuration won't take effect until buildRuntimeConfiguration
     * is called.
     * @param packageName the name of the package to remove
     * @return the package removed (if any)
     */
    PackageConfig removePackageConfig(String packageName);

    /**
     * Allow the Configuration to clean up any resources that have been used.
     */
    void destroy();

    /**
     * @deprecated Since 2.1
     * @param providers
     * @throws ConfigurationException
     */
    @Deprecated void reload(List<ConfigurationProvider> providers) throws ConfigurationException;
    
    /**
     * @since 2.1
     * @param containerProviders
     * @throws ConfigurationException
     */
    List<PackageProvider> reloadContainer(List<ContainerProvider> containerProviders) throws ConfigurationException;

    /**
     * @return the container
     */
    Container getContainer();

    Set<String> getLoadedFileNames();

    /**
     * @since 2.1
     * @return list of unknown handlers
     */
    List<UnknownHandlerConfig> getUnknownHandlerStack();

    /**
     * @since 2.1
     * @param unknownHandlerStack
     */
    void setUnknownHandlerStack(List<UnknownHandlerConfig> unknownHandlerStack);
}
