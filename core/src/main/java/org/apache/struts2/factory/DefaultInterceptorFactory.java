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
package org.apache.struts2.factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ObjectFactory;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.config.entities.InterceptorConfig;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.interceptor.Interceptor;
import org.apache.struts2.interceptor.WithLazyParams;
import org.apache.struts2.util.reflection.ReflectionProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation
 */
public class DefaultInterceptorFactory implements InterceptorFactory {

    private static final Logger LOG = LogManager.getLogger(DefaultInterceptorFactory.class);

    private ObjectFactory objectFactory;
    private ReflectionProvider reflectionProvider;

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Inject
    public void setReflectionProvider(ReflectionProvider reflectionProvider) {
        this.reflectionProvider = reflectionProvider;
    }

    public Interceptor buildInterceptor(InterceptorConfig interceptorConfig, Map<String, String> interceptorRefParams) throws ConfigurationException {
        String interceptorClassName = interceptorConfig.getClassName();
        Map<String, String> thisInterceptorClassParams = interceptorConfig.getParams();
        Map<String, String> params = (thisInterceptorClassParams == null) ? new HashMap<>() : new HashMap<>(thisInterceptorClassParams);
        params.putAll(interceptorRefParams);

        String message;
        Throwable cause;

        try {
            // interceptor instances are long-lived and used across user sessions, so don't try to pass in any extra context
            Object o = objectFactory.buildBean(interceptorClassName, null);

            if (!(o instanceof Interceptor interceptor)) {
                throw new ConfigurationException("Class [" + interceptorClassName + "] does not implement Interceptor", interceptorConfig);
            }

            if (interceptor instanceof WithLazyParams) {
                LOG.debug("Interceptor {} is marked with interface {} and params will be set during action invocation",
                        interceptorClassName, WithLazyParams.class.getName());
            } else {
                reflectionProvider.setProperties(params, interceptor);
            }

            interceptor.init();
            return interceptor;
        } catch (InstantiationException e) {
            cause = e;
            message = "Unable to instantiate an instance of Interceptor class [" + interceptorClassName + "].";
        } catch (IllegalAccessException e) {
            cause = e;
            message = "IllegalAccessException while attempting to instantiate an instance of Interceptor class [" + interceptorClassName + "].";
        } catch (ClassCastException e) {
            cause = e;
            message = "Class [" + interceptorClassName + "] does not implement " + Interceptor.class.getName();
        } catch (Exception e) {
            cause = e;
            message = "Caught Exception while registering Interceptor class " + interceptorClassName;
        } catch (NoClassDefFoundError e) {
            cause = e;
            message = "Could not load class " + interceptorClassName + ". Perhaps it exists but certain dependencies are not available?";
        }

        throw new ConfigurationException(message, cause, interceptorConfig);
    }

}
