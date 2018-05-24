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
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import com.opensymphony.xwork2.config.providers.XWorkConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


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
    protected Lock providerLock = new ReentrantLock();
    private List<ContainerProvider> containerProviders = new CopyOnWriteArrayList<>();
    private List<PackageProvider> packageProviders = new CopyOnWriteArrayList<>();
    protected String defaultFrameworkBeanName;
    private boolean providersChanged = false;
    private boolean reloadConfigs = true; // for the first time

    public ConfigurationManager(String name) {
        this.defaultFrameworkBeanName = name;
    }

    /**
     * @return the current XWork configuration object. By default an instance of DefaultConfiguration will be returned
     *
     * @see com.opensymphony.xwork2.config.impl.DefaultConfiguration
     */
    public synchronized Configuration getConfiguration() {
        if (configuration == null) {
            setConfiguration(createConfiguration(defaultFrameworkBeanName));
            try {
                configuration.reloadContainer(getContainerProviders());
            } catch (ConfigurationException e) {
                setConfiguration(null);
                throw new ConfigurationException("Unable to load configuration.", e);
            }
        } else {
            conditionalReload();
        }

        return configuration;
    }

    protected Configuration createConfiguration(String beanName) {
        return new DefaultConfiguration(beanName);
    }

    public synchronized void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * <p>
     * Get the current list of ConfigurationProviders. If no custom ConfigurationProviders have been added, this method
     * will return a list containing only the default ConfigurationProvider, XMLConfigurationProvider. If a custom
     * ConfigurationProvider has been added, then the XmlConfigurationProvider must be added by hand.
     * </p>
     *
     * <p>
     * TODO: The lazy instantiation of XmlConfigurationProvider should be refactored to be elsewhere. The behavior described above seems unintuitive.
     * </p>
     *
     * @return the list of registered ConfigurationProvider objects
     * @see ConfigurationProvider
     */
    public List<ContainerProvider> getContainerProviders() {
        providerLock.lock();
        try {
            if (containerProviders.size() == 0) {
                containerProviders.add(new XWorkConfigurationProvider());
                containerProviders.add(new XmlConfigurationProvider("xwork.xml", false));
            }

            return containerProviders;
        } finally {
            providerLock.unlock();
        }
    }

    /**
     * Set the list of configuration providers
     *
     * @param containerProviders list of {@link ConfigurationProvider} to be set
     */
    public void setContainerProviders(List<ContainerProvider> containerProviders) {
        providerLock.lock();
        try {
            this.containerProviders = new CopyOnWriteArrayList<>(containerProviders);
            providersChanged = true;
        } finally {
            providerLock.unlock();
        }
    }

    /**
     * adds a configuration provider to the List of ConfigurationProviders.  a given ConfigurationProvider may be added
     * more than once
     *
     * @param provider the ConfigurationProvider to register
     */
    public void addContainerProvider(ContainerProvider provider) {
        if (!containerProviders.contains(provider)) {
            containerProviders.add(provider);
            providersChanged = true;
        }
    }

    public void clearContainerProviders() {
        for (ContainerProvider containerProvider : containerProviders) {
            clearContainerProvider(containerProvider);
        }
        containerProviders.clear();
        providersChanged = true;
    }

    private void clearContainerProvider(ContainerProvider containerProvider) {
        try {
            containerProvider.destroy();
        } catch (Exception e) {
            LOG.warn("Error while destroying container provider [{}]", containerProvider.toString(), e);
        }
    }

    /**
     * Destroy its managing Configuration instance
     */
    public synchronized void destroyConfiguration() {
        clearContainerProviders(); // let's destroy the ConfigurationProvider first
        containerProviders = new CopyOnWriteArrayList<ContainerProvider>();
        if (configuration != null)
            configuration.destroy(); // let's destroy it first, before nulling it.
        configuration = null;
    }


    /**
     * Reloads the Configuration files if the configuration files indicate that they need to be reloaded.
     */
    public synchronized void conditionalReload() {
        if (reloadConfigs || providersChanged) {
            LOG.debug("Checking ConfigurationProviders for reload.");
            List<ContainerProvider> providers = getContainerProviders();
            boolean reload = needReloadContainerProviders(providers);
            if (!reload) {
                reload = needReloadPackageProviders();
            }
            if (reload) {
                reloadProviders(providers);
            }
            updateReloadConfigsFlag();
            providersChanged = false;
        }
    }

    private void updateReloadConfigsFlag() {
        reloadConfigs = Boolean.parseBoolean(configuration.getContainer().getInstance(String.class, StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD));
        if (LOG.isDebugEnabled()) {
            LOG.debug("Updating [{}], current value is [{}], new value [{}]",
                    StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD, String.valueOf(reloadConfigs), String.valueOf(reloadConfigs));
        }
    }

    private boolean needReloadPackageProviders() {
        if (packageProviders != null) {
            for (PackageProvider provider : packageProviders) {
                if (provider.needsReload()) {
                    LOG.info("Detected package provider [{}] needs to be reloaded. Reloading all providers.", provider);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean needReloadContainerProviders(List<ContainerProvider> providers) {
        for (ContainerProvider provider : providers) {
            if (provider.needsReload()) {
                LOG.info("Detected container provider [{}] needs to be reloaded. Reloading all providers.", provider);
                return true;
            }
        }
        return false;
    }

    private void reloadProviders(List<ContainerProvider> providers) {
        for (ContainerProvider containerProvider : containerProviders) {
            try {
                containerProvider.destroy();
            } catch (Exception e) {
                LOG.warn("error while destroying configuration provider [{}]", containerProvider, e);
            }
        }
        packageProviders = this.configuration.reloadContainer(providers);
    }

    public synchronized void reload() {
        packageProviders = getConfiguration().reloadContainer(getContainerProviders());
    }

}
