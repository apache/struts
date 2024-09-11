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

import org.apache.struts2.config.impl.DefaultConfiguration;
import org.apache.struts2.config.providers.StrutsDefaultConfigurationProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.struts2.StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD;


/**
 * ConfigurationManager - central for XWork Configuration management, including
 * its ConfigurationProvider.
 *
 * @author Jason Carreira
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class ConfigurationManager {

    protected static final Logger LOG = LogManager.getLogger(ConfigurationManager.class);
    protected Configuration configuration;
    private List<ContainerProvider> containerProviders = new ArrayList<>();
    private List<PackageProvider> packageProviders = new ArrayList<>();
    protected String defaultFrameworkBeanName;
    private boolean providersChanged = true;
    private boolean alwaysReloadConfigs = false;

    public ConfigurationManager(String name) {
        this.defaultFrameworkBeanName = name;
    }

    /**
     * @return the current XWork configuration object. By default an instance of DefaultConfiguration will be returned
     *
     * @see org.apache.struts2.config.impl.DefaultConfiguration
     */
    public synchronized Configuration getConfiguration() {
        if (wasConfigInitialised()) {
            conditionalReload();
        }
        return configuration;
    }

    /**
     * @return whether configuration was initialised (was null)
     */
    private boolean wasConfigInitialised() {
        if (configuration == null) {
            initialiseConfiguration();
            return false;
        }
        return true;
    }

    protected void initialiseConfiguration() {
        if (containerProviders.isEmpty()) {
            addDefaultContainerProviders();
        }
        configuration = createConfiguration(defaultFrameworkBeanName);
        try {
            reload();
        } catch (ConfigurationException e) {
            configuration.destroy();
            configuration = null;
            providersChanged = true;
            throw new ConfigurationException("Unable to load configuration.", e);
        }
    }

    protected void addDefaultContainerProviders() {
        containerProviders.add(new StrutsDefaultConfigurationProvider());
    }

    protected Configuration createConfiguration(String beanName) {
        return new DefaultConfiguration(beanName);
    }

    /**
     * Clear all container providers and destroy managing Configuration instance
     */
    public synchronized void destroyConfiguration() {
        clearContainerProviders();
        if (configuration != null) {
            configuration.destroy();
            configuration = null;
        }
    }

    public synchronized void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Get the current list of ConfigurationProviders.
     *
     * @return the list of registered ConfigurationProvider objects
     * @see ConfigurationProvider
     */
    public synchronized List<ContainerProvider> getContainerProviders() {
        return new ArrayList<>(containerProviders);
    }

    /**
     * Set the list of configuration providers
     *
     * @param containerProviders list of {@link ConfigurationProvider} to be set
     */
    public synchronized void setContainerProviders(List<ContainerProvider> containerProviders) {
        this.containerProviders = new ArrayList<>(containerProviders);
        providersChanged = true;
    }

    /**
     * adds a configuration provider to the List of ConfigurationProviders.  a given ConfigurationProvider may be added
     * more than once
     *
     * @param provider the ConfigurationProvider to register
     */
    public synchronized void addContainerProvider(ContainerProvider provider) {
        if (!containerProviders.contains(provider)) {
            containerProviders.add(provider);
            providersChanged = true;
        }
    }

    public synchronized void removeContainerProvider(ContainerProvider provider) {
        if (containerProviders.remove(provider)) {
            destroyContainerProvider(provider);
            providersChanged = true;
        }
    }

    public synchronized void clearContainerProviders() {
        destroyContainerProviders();
        containerProviders.clear();
        providersChanged = true;
    }

    private void destroyContainerProviders() {
        LOG.debug("Destroying all providers.");
        containerProviders.forEach(this::destroyContainerProvider);
    }

    private void destroyContainerProvider(ContainerProvider containerProvider) {
        try {
            containerProvider.destroy();
        } catch (Exception e) {
            LOG.warn("Error while destroying container provider [{}]", containerProvider.toString(), e);
        }
    }

    /**
     * Reloads the Configuration files if the configuration files indicate that they need to be reloaded.
     */
    public synchronized void conditionalReload() {
        if (alwaysReloadConfigs || providersChanged) {
            LOG.debug("Checking ConfigurationProviders for reload.");
            if (needReloadContainerProviders() || needReloadPackageProviders()) {
                destroyAndReload();
            }
            providersChanged = false;
        }
    }

    private void updateAlwaysReloadFlag() {
        boolean newValue = Boolean.parseBoolean(configuration.getContainer()
                .getInstance(String.class, STRUTS_CONFIGURATION_XML_RELOAD));
        if (alwaysReloadConfigs != newValue) {
            LOG.debug(
                    "Updating [{}], current value is [{}], new value [{}]",
                    STRUTS_CONFIGURATION_XML_RELOAD,
                    String.valueOf(alwaysReloadConfigs),
                    String.valueOf(newValue));
            alwaysReloadConfigs = newValue;
        }
    }

    private boolean needReloadPackageProviders() {
        Optional<PackageProvider> provider = packageProviders.stream().filter(PackageProvider::needsReload).findAny();
        if (provider.isPresent()) {
            LOG.info("Detected package provider [{}] needs to be reloaded.", provider.get());
            return true;
        }
        return false;
    }

    private boolean needReloadContainerProviders() {
        Optional<ContainerProvider> provider = containerProviders.stream().filter(ContainerProvider::needsReload).findAny();
        if (provider.isPresent()) {
            LOG.info("Detected container provider [{}] needs to be reloaded.", provider.get());
            return true;
        }
        return false;
    }

    public synchronized void destroyAndReload() {
        destroyContainerProviders();
        reload();
    }

    public synchronized void reload() {
        if (wasConfigInitialised()) {
            LOG.debug("Reloading all providers.");
            packageProviders = configuration.reloadContainer(containerProviders);
            providersChanged = false;
            updateAlwaysReloadFlag();
        }
    }
}
