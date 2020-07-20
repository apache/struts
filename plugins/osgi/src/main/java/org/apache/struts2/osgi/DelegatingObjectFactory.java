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

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.PackageProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.struts2.util.ObjectFactoryDestroyable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class DelegatingObjectFactory extends ObjectFactory implements ObjectFactoryDestroyable {

    private static final Logger LOG = LogManager.getLogger(DelegatingObjectFactory.class);

    private ObjectFactory delegateObjectFactory;
    private BundleAccessor bundleResourceLoader;
    private OsgiConfigurationProvider osgiConfigurationProvider;

    /**
     * Create a DelegatingObjectFactory instance for constructing objects needed by the OSGi plugin.
     * 
     * Note:  Due to some Core components (e.g. DefaultTypeConverterCreator) calling on the object factory before the OSGi plugin can be initialized,
     *        we need something like a default delegate factory on construction.  The Struts object factory would be the default delegate factory, but
     *        unfortunately the ObjectFactory hierarchy does not allow us to access the ancestor container or classloader.  The best we can do is
     *        call the ancestor in the event no delegate has been set.
     */
    public DelegatingObjectFactory() {
        super();

        LOG.trace("OSGi Plugin - DelegatingObjectFactory() constructor called.");
    }

    @Inject
    public void setDelegateObjectFactory(@Inject Container container,
                                         @Inject("struts.objectFactory.delegate") String delegate) {
        LOG.trace("OSGi Plugin - setDelegateObjectFactory() call - Delegate: [{}], Container: [{}]", delegate, container);

        if (delegate == null) {
            delegate = "struts";
        }
        delegateObjectFactory = container.getInstance(ObjectFactory.class, delegate);

        LOG.trace("OSGi Plugin - setDelegateObjectFactory() call - Result: [{}]", delegateObjectFactory);
    }

    @Inject
    public void setBundleResourceLoader(BundleAccessor rl) {
        LOG.trace("OSGi Plugin - Set BundleResourceLoader() call - BundleAccessor: [{}]", rl);

        this.bundleResourceLoader = rl;
    }


    @Override
    public boolean isNoArgConstructorRequired() {
        if (delegateObjectFactory == null) {
            throw new IllegalStateException("Cannot check if no-argument constructor required, delegate object factory is null");  // Better than a NPE.
        }

        return delegateObjectFactory.isNoArgConstructorRequired();
    }

    @Override
    public Object buildBean(Class clazz, Map extraContext) throws Exception {
        if (delegateObjectFactory == null) {
            LOG.trace("OSGi Plugin - buildbean(Class, Map) call when delegateObjectFactory is NULL.  NPE would result, calling ancestor (fallback)");

            return super.buildBean(clazz, extraContext);
        }

        return delegateObjectFactory.buildBean(clazz, extraContext);
    }

    @Override
    public Object buildBean(String className, Map<String, Object> extraContext, boolean injectInternal) throws Exception {
        if (delegateObjectFactory == null) {
            LOG.trace("OSGi Plugin - buildbean(Class, Map, boolean) call when delegateObjectFactory is NULL.  NPE would result, calling ancestor (fallback)");

            return super.buildBean(className, extraContext, injectInternal);
        }

        try {
            return delegateObjectFactory.buildBean(className, extraContext, injectInternal);
        } catch (Exception e) {
            if (bundleResourceLoader == null) {
                throw new IllegalStateException("Cannot build bean, bundle resource loader is null");  // Better than a NPE.
            }

            Object object = bundleResourceLoader.loadClass(className).newInstance();
            if (injectInternal) {
                injectInternalBeans(object);
            }
            return object;
        }
    }

    @Override
    public Class getClassInstance(String className) throws ClassNotFoundException {
        if (delegateObjectFactory == null) {
            LOG.trace("OSGi Plugin - buildbean(Class, Map, boolean) call when delegateObjectFactory is NULL.  NPE would result, calling ancestor (fallback)");

            return super.getClassInstance(className);
        }

        try {
            return delegateObjectFactory.getClassInstance(className);
        }
        catch (Exception e) {
            if (bundleResourceLoader == null) {
                throw new IllegalStateException("Cannot get class instance, bundle resource loader is null");  // Better than a NPE.
            }

            return bundleResourceLoader.loadClass(className);
        }
    }

    @Override
    public void destroy() {
        if (osgiConfigurationProvider != null) {
            osgiConfigurationProvider.destroy();
        }
    }

    @Inject("osgi")
    public void setOsgiConfigurationProvider(PackageProvider osgiConfigurationProvider) {
        LOG.trace("OSGi Plugin - setOsgiConfigurationProvider() call - PackageProvider: [{}]", osgiConfigurationProvider);

        this.osgiConfigurationProvider = (OsgiConfigurationProvider) osgiConfigurationProvider;
    }

}
