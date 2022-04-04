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

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.PackageProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.struts2.util.ObjectFactoryDestroyable;

import java.util.Map;

public class DelegatingObjectFactory extends ObjectFactory implements ObjectFactoryDestroyable {

    private ObjectFactory delegateObjectFactory;
    private BundleAccessor bundleResourceLoader;
    private OsgiConfigurationProvider osgiConfigurationProvider;

    @Inject
    public void setDelegateObjectFactory(@Inject Container container,
                                         @Inject("struts.objectFactory.delegate") String delegate) {
        if (delegate == null) {
            delegate = "struts";
        }
        delegateObjectFactory = container.getInstance(ObjectFactory.class, delegate);
    }

    @Inject
    public void setBundleResourceLoader(BundleAccessor rl) {
        this.bundleResourceLoader = rl;
    }


    public boolean isNoArgConstructorRequired() {
        return delegateObjectFactory.isNoArgConstructorRequired();
    }

    public Object buildBean(Class clazz, Map extraContext) throws Exception {
        return delegateObjectFactory.buildBean(clazz, extraContext);
    }

    public Object buildBean(String className, Map<String, Object> extraContext, boolean injectInternal) throws Exception {
        try {
            return delegateObjectFactory.buildBean(className, extraContext, injectInternal);
        } catch (Exception e) {
            Object object = bundleResourceLoader.loadClass(className).newInstance();
            if (injectInternal)
                injectInternalBeans(object);
            return object;
        }
    }

    @Override
    public Class getClassInstance(String className) throws ClassNotFoundException {
        try {
            return delegateObjectFactory.getClassInstance(className);
        }
        catch (Exception e) {
            return bundleResourceLoader.loadClass(className);
        }
    }

    public void destroy() {
        if (osgiConfigurationProvider != null) {
            osgiConfigurationProvider.destroy();
        }
    }

    @Inject("osgi")
    public void setOsgiConfigurationProvider(PackageProvider osgiConfigurationProvider) {
        this.osgiConfigurationProvider = (OsgiConfigurationProvider) osgiConfigurationProvider;
    }

}
