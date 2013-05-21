/*
 * $Id$
 *
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
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(BundlePackageLoader.class);

    public List<PackageConfig> loadPackages(Bundle bundle, BundleContext bundleContext, ObjectFactory objectFactory,
                                            FileManagerFactory fileManagerFactory, Map<String, PackageConfig> pkgConfigs) throws ConfigurationException {
        Configuration config = new DefaultConfiguration("struts.xml");
        BundleConfigurationProvider prov = new BundleConfigurationProvider("struts.xml", bundle, bundleContext);
        for (PackageConfig pkg : pkgConfigs.values()) {
            config.addPackageConfig(pkg.getName(), pkg);
        }
        prov.setObjectFactory(objectFactory);
        prov.setFileManagerFactory(fileManagerFactory);
        prov.init(config);
        prov.loadPackages();

        List<PackageConfig> list = new ArrayList<PackageConfig>(config.getPackageConfigs().values());
        list.removeAll(pkgConfigs.values());

        return list;
    }

    static class BundleConfigurationProvider extends XmlConfigurationProvider {
        private Bundle bundle;
        private BundleContext bundleContext;

        public BundleConfigurationProvider(String filename, Bundle bundle, BundleContext bundleContext) {
            super(filename, false);
            this.bundle = bundle;
            this.bundleContext = bundleContext;
        }

        public BundleConfigurationProvider(String filename) {
            super(filename);
        }

        @Override
        protected Iterator<URL> getConfigurationUrls(String fileName) throws IOException {
            Enumeration<URL> e = bundle.getResources("struts.xml");
            return e.hasMoreElements() ? new EnumeratorIterator<URL>(e) : null;
        }
    }

    static class EnumeratorIterator<E> implements Iterator<E> {
        Enumeration<E> e = null;

        public EnumeratorIterator(Enumeration<E> e) {
            this.e = e;
        }

        public boolean hasNext() {
            return e.hasMoreElements();
        }

        public E next() {
            return e.nextElement();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
