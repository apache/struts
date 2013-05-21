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
package org.apache.struts2.osgi.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.osgi.host.OsgiHost;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import javax.servlet.ServletContext;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * If a class implements BundleContextAware, this interceptor will call the setBundleContext(BundleContext)
 * method on it. If a class implements ServiceAware<T>, this interceptor will call setService(List<T>)
 */
public class OsgiInterceptor extends AbstractInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(OsgiInterceptor.class);

    private BundleContext bundleContext;

    public String intercept(ActionInvocation invocation) throws Exception {
        if (bundleContext != null) {
            Object action = invocation.getAction();

            //inject BundleContext
            if (action instanceof BundleContextAware)
                ((BundleContextAware)action).setBundleContext(bundleContext);

            //inject service implementations
            if (action instanceof ServiceAware) {
                Type[] types = action.getClass().getGenericInterfaces();
                if (types != null) {
                    for (Type type : types) {
                        if (type instanceof ParameterizedType) {
                            ParameterizedType parameterizedType = (ParameterizedType) type;
                            if (parameterizedType.getRawType() instanceof Class) {
                                Class clazz = (Class) parameterizedType.getRawType();
                                if (ServiceAware.class.equals(clazz)) {
                                    Class serviceClass = (Class) parameterizedType.getActualTypeArguments()[0];
                                    ServiceReference[] refs = bundleContext.getAllServiceReferences(serviceClass.getName(), null);
                                    //get the services
                                    if (refs != null) {
                                        List services = new ArrayList(refs.length);
                                        for (ServiceReference ref : refs) {
                                            Object service = bundleContext.getService(ref);
                                            //wow, that's a lot of nested ifs
                                            if (service != null)
                                                services.add(service);
                                        }

                                        if (!services.isEmpty())
                                            ((ServiceAware)action).setServices(services);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (LOG.isWarnEnabled()){
            LOG.warn("The OSGi interceptor was not able to find the BundleContext in the ServletContext");          
        }

        return invocation.invoke();
    }

    @Inject
    public void setServletContext(ServletContext servletContext) {
        this.bundleContext = (BundleContext) servletContext.getAttribute(OsgiHost.OSGI_BUNDLE_CONTEXT);
    }

}
