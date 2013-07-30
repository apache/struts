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

// Copyright 2006 Google Inc. All Rights Reserved.

package org.apache.struts2.impl;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Instead of overriding the whole class a new {@link com.opensymphony.xwork2.factory.InterceptorFactory}
 * should be defined, thus should be solved in Struts 2.5
 *
 * @deprecated since version 2.3.16
 */
@Deprecated
public class StrutsObjectFactory extends ObjectFactory {

    private ReflectionProvider reflectionProvider;

    @Inject
    public void setReflectionProvider(ReflectionProvider reflectionProvider) {
        this.reflectionProvider = reflectionProvider;
    }

    public Interceptor buildInterceptor(InterceptorConfig interceptorConfig, Map refParams)
            throws ConfigurationException {
        String className = interceptorConfig.getClassName();

        Map<String, String> params = new HashMap<String, String>();
        Map typeParams = interceptorConfig.getParams();
        if (typeParams != null && !typeParams.isEmpty())
            params.putAll(typeParams);
        if (refParams != null && !refParams.isEmpty())
            params.putAll(refParams);

        try {
            // interceptor instances are long-lived and used across user sessions, so don't try to pass in any extra
            // context
            Object o = buildBean(className, null);
            reflectionProvider.setProperties(params, o);

            if (o instanceof Interceptor) {
                Interceptor interceptor = (Interceptor) o;
                interceptor.init();
                return interceptor;
            }

// This is for the new API:
//            if (o instanceof org.apache.struts2.spi.Interceptor)
//                return new InterceptorAdapter((org.apache.struts2.spi.Interceptor) o);

            throw new ConfigurationException(
                    "Class [" + className + "] does not implement Interceptor", interceptorConfig);
        } catch (InstantiationException e) {
            throw new ConfigurationException(
                    "Unable to instantiate an instance of Interceptor class [" + className + "].",
                    e, interceptorConfig);
        } catch (IllegalAccessException e) {
            throw new ConfigurationException(
                    "IllegalAccessException while attempting to instantiate an instance of Interceptor class ["
                            + className + "].",
                    e, interceptorConfig);
        } catch (Exception e) {
            throw new ConfigurationException(
                    "Caught Exception while registering Interceptor class " + className,
                    e, interceptorConfig);
        } catch (NoClassDefFoundError e) {
            throw new ConfigurationException(
                    "Could not load class " + className
                            + ". Perhaps it exists but certain dependencies are not available?",
                    e, interceptorConfig);
        }
    }

}
