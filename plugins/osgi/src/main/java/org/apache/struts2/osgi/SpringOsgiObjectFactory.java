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
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.inject.Inject;
import org.osgi.framework.ServiceReference;

import java.util.Map;

/**
 * This Object factory uses the ActionContext(s) published by Spring OSGi
 * to lookup beans
 */
public class SpringOsgiObjectFactory extends ObjectFactory {

    private final static String SPRING_SERVICE_NAME = "org.springframework.context.ApplicationContext";

    private BundleAccessor bundleAccessor;

    public SpringOsgiObjectFactory() {
    }

    public Object buildBean(String className, Map<String, Object> extraContext, boolean injectInternal) throws Exception {
        if (containsBean(className))
            return getBean(className);
        else {
            Class clazz = ClassLoaderUtil.loadClass(className, SpringOsgiObjectFactory.class);
            Object object = clazz.newInstance();
            if (injectInternal)
                injectInternalBeans(object);
            return object;
        }

    }

    public Object buildBean(Class clazz, Map<String, Object> extraContext) throws Exception {
        return clazz.newInstance();
    }

    public Class getClassInstance(String className) throws ClassNotFoundException {
        return containsBean(className) ? getBean(className).getClass() :  ClassLoaderUtil.loadClass(className, SpringOsgiObjectFactory.class);
    }

    protected Object getBean(String beanName) {
        ServiceReference[] refs = bundleAccessor.getAllServiceReferences(SPRING_SERVICE_NAME);
        if (refs != null) {
            for (ServiceReference ref : refs) {
                Object context = bundleAccessor.getService(ref);
                if (OsgiUtil.containsBean(context, beanName))
                    return OsgiUtil.getBean(context, beanName);
            }
        }

        return null;
    }

    protected boolean containsBean(String beanName) {
        ServiceReference[] refs = bundleAccessor.getAllServiceReferences(SPRING_SERVICE_NAME);
        if (refs != null) {
            for (ServiceReference ref : refs) {
                Object context = bundleAccessor.getService(ref);
                if (OsgiUtil.containsBean(context, beanName))
                    return true;
            }
        }

        return false;
    }

    @Inject
    public void setBundleAccessor(BundleAccessor bundleAccessor) {
        this.bundleAccessor = bundleAccessor;
    }

}
