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
package org.apache.struts2.osgi;

import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.fs.DefaultFileManagerFactory;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Package loader implementation that loads resources from a bundle
 */
public class BundlePackageLoader implements PackageLoader {

    private static final Logger LOG = LogManager.getLogger(BundlePackageLoader.class);

    private Container contextContainer = null;

    @Deprecated
    @Override
    public List<PackageConfig> loadPackages(Bundle bundle, BundleContext bundleContext, ObjectFactory objectFactory,
                                            FileManagerFactory fileManagerFactory, Map<String, PackageConfig> pkgConfigs) throws ConfigurationException {
        if (pkgConfigs == null) {
            throw new IllegalArgumentException("Cannot load packages from a null package configuration");  // Better than a NPE.
        }

        Configuration config = new DefaultConfiguration("struts.xml");

        LOG.trace("LoadPackages - After config constructed.  Before BundleConfigurationProvider constructed");

        BundleConfigurationProvider prov = new BundleConfigurationProvider("struts.xml", bundle, bundleContext);

        LOG.trace("LoadPackages - After BundleConfigurationProvider constructed.  Before config.addPackageConfig loop");

        pkgConfigs.values().forEach(pkg -> {
            config.addPackageConfig(pkg.getName(), pkg);
        });

        LOG.trace("LoadPackages - After config.addPackageConfig loop.  Before prov.setObjectFactory()");

        prov.setObjectFactory(objectFactory);

        if (fileManagerFactory == null || fileManagerFactory.getFileManager() == null) {
            LOG.warn("LoadPackages - FileManagerFactory parameter is null or produces a null FileManager, replacing with a new DefaultFileManagerFactory instance");

            final DefaultFileManagerFactory defaultFileManagerFactory = new DefaultFileManagerFactory();
            final Container container = getContextContainer();

            if (container == null) {
                LOG.warn("LoadPackages - Config Container is null.  May cause a NPE to be thrown");
            } else {
                container.inject(defaultFileManagerFactory);  // Apply configuration (including the container reference) to the DefaultFileManagerFactory instance.
            }

            prov.setFileManagerFactory(defaultFileManagerFactory);
        } else {
            prov.setFileManagerFactory(fileManagerFactory);
        }

        LOG.trace("LoadPackages - After prov.setFileManagerFactory().  Before init()");

        prov.init(config);

        LOG.trace("LoadPackages - After prov.init().  Before loadPackages()");

        prov.loadPackages();

        LOG.trace("LoadPackages - After prov.loadPackages().  Before config.getPackageConfigs().values()");

        List<PackageConfig> list = new ArrayList<>(config.getPackageConfigs().values());

        LOG.trace("LoadPackages - After config.getPackageConfigs().  Before pkgConfigs.values()");

        list.removeAll(pkgConfigs.values());

        return list;
    }

    @Override
    public List<PackageConfig> loadPackages(Container container, Bundle bundle, BundleContext bundleContext, ObjectFactory objectFactory,
                                        FileManagerFactory fileManagerFactory, Map<String, PackageConfig> pkgConfigs) throws ConfigurationException {
        setContextContainer(container);  // Prepare Container state for standard signature call.
        return loadPackages(bundle, bundleContext, objectFactory, fileManagerFactory, pkgConfigs);
    }

    /**
     * Method to get the ActionContext (or other) Container instance required for the FileManagerFactory to work.
     * 
     * Cheap workaround for keeping the deprecated signature version of loadPackages() around.  Used by the new signature and can
     * be used standalone with the old signature (developer's choice).
     * 
     * @return  The Container associated with this PackageLoader.
     */
    protected Container getContextContainer() {
        return contextContainer;
    }

    /**
     * Method to set the ActionContext (or other) Container instance required for the FileManagerFactory to work.
     * 
     * Cheap workaround for keeping the deprecated signature version of loadPackages() around.  Used by the new signature and can
     * be used standalone with the old signature (developer's choice).
     * 
     * @param contextContainer  The Container associated with this PackageLoader.
     */
    protected void setContextContainer(Container contextContainer) {
        this.contextContainer = contextContainer;
    }

    static class BundleConfigurationProvider extends StrutsXmlConfigurationProvider {
        private Bundle bundle;
        private BundleContext bundleContext;

        public BundleConfigurationProvider(String filename, Bundle bundle, BundleContext bundleContext) {
            super(filename, false, null);  // Currently no dependency on ServletContext
            this.bundle = bundle;
            this.bundleContext = bundleContext;
        }

        public BundleConfigurationProvider(String filename) {
            super(filename, false, null);  // Currently no dependency on ServletContext
        }

        @Override
        protected Iterator<URL> getConfigurationUrls(String fileName) throws IOException {
            if (bundle == null) {
                throw new IllegalStateException("Unable to get configuration URLs, current Bundle reference is null");  // Better than a NPE.
            }

            Enumeration<URL> e = bundle.getResources("struts.xml");
            return e.hasMoreElements() ? new EnumeratorIterator<>(e) : null;
        }
    }

    static class EnumeratorIterator<E> implements Iterator<E> {
        Enumeration<E> e = null;

        public EnumeratorIterator(Enumeration<E> e) {
            this.e = e;
        }

        @Override
        public boolean hasNext() {
            return e.hasMoreElements();
        }

        @Override
        public E next() {
            return e.nextElement();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
