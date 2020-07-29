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
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.Container;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.util.List;
import java.util.Map;

/**
 * Implementations of this interface can load packages from a Bundle
 */
public interface PackageLoader {

    /**
     * This method loads all of the package configurations for the specified Bundle and BundleContext.
     * 
     * This method signature is retained for ensuring backwards-compatibility Interface-wise, but is Deprecated as it
     * cannot provide the now-necessary Container reference.
     * 
     * @param bundle  The OSGi bundle.
     * @param bundleContext  The OSGi bundle BundleContext.
     * @param objectFactory  The ObjectFactory to create object instances.
     * @param fileManagerFactory  The FileManagerFactory to access files.
     * @param map   The Map of PackageConfigs (by String name).
     * @return  A List of PackageConfigs that contains the packages loaded for the bundle.
     * @throws ConfigurationException  When the package load operation fails due to configuration errors.
     */
    @Deprecated
    List<PackageConfig> loadPackages(Bundle bundle, BundleContext bundleContext, ObjectFactory objectFactory,
                                     FileManagerFactory fileManagerFactory,
                                     Map<String, PackageConfig> map) throws ConfigurationException;

    /**
     * This method loads all of the package configurations for the specified Bundle and BundleContext.
     * 
     * Introduces a new loadPackages method signature including a Container reference.  This is necessary as the FileManagerFactory depends on
     * access to an initialized Container reference to function properly.
     * 
     * @param container  The Container from the ActionContext (or another context).
     * @param bundle  The OSGi bundle.
     * @param bundleContext  The OSGi bundle BundleContext.
     * @param objectFactory  The ObjectFactory to create object instances.
     * @param fileManagerFactory  The FileManagerFactory to access files.
     * @param map   The Map of PackageConfigs (by String name).
     * @return  A List of PackageConfigs that contains the packages loaded for the bundle.
     * @throws ConfigurationException  When the package load operation fails due to configuration errors.
     */
    List<PackageConfig> loadPackages(Container container, Bundle bundle, BundleContext bundleContext, ObjectFactory objectFactory, FileManagerFactory fileManagerFactory,
                                     Map<String, PackageConfig> map) throws ConfigurationException;

}
