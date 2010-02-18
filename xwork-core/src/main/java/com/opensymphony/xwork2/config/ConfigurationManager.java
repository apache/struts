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

import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import com.opensymphony.xwork2.config.providers.XWorkConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.util.FileManager;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.util.ArrayList;
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

    protected static final Logger LOG = LoggerFactory.getLogger(ConfigurationManager.class);
    protected Configuration configuration;
    protected Lock providerLock = new ReentrantLock();
    private List<ContainerProvider> containerProviders = new CopyOnWriteArrayList<ContainerProvider>();
    private List<PackageProvider> packageProviders = new CopyOnWriteArrayList<PackageProvider>();
    protected String defaultFrameworkBeanName;

    public ConfigurationManager() {
        this("xwork");
    }
    
    public ConfigurationManager(String name) {
        this.defaultFrameworkBeanName = name;
    }

    /**
     * Get the current XWork configuration object.  By default an instance of DefaultConfiguration will be returned
     *
     * @see com.opensymphony.xwork2.config.impl.DefaultConfiguration
     */
    public synchronized Configuration getConfiguration() {
        if (configuration == null) {
            setConfiguration(new DefaultConfiguration(defaultFrameworkBeanName));
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

    public synchronized void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
    
    /**
     * Get the current list of ConfigurationProviders. If no custom ConfigurationProviders have been added, this method
     * will return a list containing only the default ConfigurationProvider, XMLConfigurationProvider.  if a custom
     * ConfigurationProvider has been added, then the XmlConfigurationProvider must be added by hand.
     * </p>
     * <p/>
     * WARNING: This returns only ContainerProviders that can be cast into ConfigurationProviders
     *
     * @return the list of registered ConfigurationProvider objects
     * @see ConfigurationProvider
     * @deprecated Since 2.1, use {@link #getContainerProviders()}
     */
    @Deprecated public List<ConfigurationProvider> getConfigurationProviders() {
        List<ContainerProvider> contProviders = getContainerProviders();
        List<ConfigurationProvider> providers = new ArrayList<ConfigurationProvider>();
        for (ContainerProvider prov : contProviders) {
            if (prov instanceof ConfigurationProvider) {
                providers.add((ConfigurationProvider) prov);
            }
        }
        return providers;
    }

    /**
     * Get the current list of ConfigurationProviders. If no custom ConfigurationProviders have been added, this method
     * will return a list containing only the default ConfigurationProvider, XMLConfigurationProvider.  if a custom
     * ConfigurationProvider has been added, then the XmlConfigurationProvider must be added by hand.
     * </p>
     * <p/>
     * TODO: the lazy instantiation of XmlConfigurationProvider should be refactored to be elsewhere.  the behavior described above seems unintuitive.
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
     * @param configurationProviders
     * @deprecated Since 2.1, use {@link #setContainerProvider()}
     */
    @Deprecated public void setConfigurationProviders(List<ConfigurationProvider> configurationProviders) {
        // Silly copy necessary due to lack of ability to cast generic lists
        List<ContainerProvider> contProviders = new ArrayList<ContainerProvider>();
        contProviders.addAll(configurationProviders);
        
        setContainerProviders(contProviders);
    }
        
    /**
     * Set the list of configuration providers
     *
     * @param containerProviders
     */
    public void setContainerProviders(List<ContainerProvider> containerProviders) {
        providerLock.lock();
        try {
            this.containerProviders = new CopyOnWriteArrayList<ContainerProvider>(containerProviders);
        } finally {
            providerLock.unlock();
        }
    }

    /**
     * adds a configuration provider to the List of ConfigurationProviders.  a given ConfigurationProvider may be added
     * more than once
     *
     * @param provider the ConfigurationProvider to register
     * @deprecated Since 2.1, use {@link #addContainerProvider()}
     */
    @Deprecated public void addConfigurationProvider(ConfigurationProvider provider) {
        addContainerProvider(provider);
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
        }
    }
    
    /**
     * clears the registered ConfigurationProviders.  this method will call destroy() on each of the registered
     * ConfigurationProviders
     *
     * @see com.opensymphony.xwork2.config.ConfigurationProvider#destroy
     * @deprecated Since 2.1, use {@link #clearContainerProviders()}
     */
    @Deprecated public void clearConfigurationProviders() {
        clearContainerProviders();
    }
    
    public void clearContainerProviders() {
        for (ContainerProvider containerProvider : containerProviders) {
            try {
                containerProvider.destroy();
            }
            catch(Exception e) {
                LOG.warn("error while destroying container provider ["+containerProvider+"]", e);
            }
        }
        containerProviders.clear();
    }

    /**
     * Destroy its managing Configuration instance
     */
    public synchronized void destroyConfiguration() {
        clearConfigurationProviders(); // let's destroy the ConfigurationProvider first
        containerProviders = new CopyOnWriteArrayList<ContainerProvider>();
        if (configuration != null)
            configuration.destroy(); // let's destroy it first, before nulling it.
        configuration = null;
    }


    /**
     * Reloads the Configuration files if the configuration files indicate that they need to be reloaded.
     */
    public synchronized void conditionalReload() {
        if (FileManager.isReloadingConfigs()) {
            boolean reload;

            if (LOG.isDebugEnabled()) {
                LOG.debug("Checking ConfigurationProviders for reload.");
            }

            reload = false;

            List<ContainerProvider> providers = getContainerProviders();
            for (ContainerProvider provider : providers) {
                if (provider.needsReload()) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Detected container provider "+provider+" needs to be reloaded.  Reloading all providers.");
                    }
                    reload = true;

                    //break;
                }
            }
            
            if (packageProviders != null && reload) {
                for (PackageProvider provider : packageProviders) {
                    if (provider.needsReload()) {
                        if (LOG.isInfoEnabled()) {
                            LOG.info("Detected package provider "+provider+" needs to be reloaded.  Reloading all providers.");
                        }
                        reload = true;
    
                        //break;
                    }
                }
            }

            if (reload) {
            	for (ContainerProvider containerProvider : containerProviders) {
                	try {
                		containerProvider.destroy();
                	}
                	catch(Exception e) {
                		LOG.warn("error while destroying configuration provider ["+containerProvider+"]", e);
                	}
                }
                packageProviders = configuration.reloadContainer(providers);
            }
        }
    }
    
    public synchronized void reload() {
        packageProviders = getConfiguration().reloadContainer(getContainerProviders());
    }
}
