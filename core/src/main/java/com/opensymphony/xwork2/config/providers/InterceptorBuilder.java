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
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.InterceptorLocator;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.InterceptorStackConfig;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.util.location.Location;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Builds a list of interceptors referenced by the refName in the supplied PackageConfig.
 *
 * @author Mike
 * @author Rainer Hermanns
 * @author tmjee
 */
public class InterceptorBuilder {

    private static final Logger LOG = LogManager.getLogger(InterceptorBuilder.class);

    /**
     * Builds a list of interceptors referenced by the refName in the supplied PackageConfig (InterceptorMapping object).
     *
     * @param interceptorLocator interceptor locator
     * @param refName reference name
     * @param refParams reference parameters
     * @param location location
     * @param objectFactory object factory
     * @return list of interceptors referenced by the refName in the supplied PackageConfig (InterceptorMapping object).
     * @throws ConfigurationException in case of any configuration errors
     */
    public static List<InterceptorMapping> constructInterceptorReference(InterceptorLocator interceptorLocator,
                                                                         String refName, Map<String,String> refParams, Location location, ObjectFactory objectFactory) throws ConfigurationException {
        Object referencedConfig = interceptorLocator.getInterceptorConfig(refName);
        List<InterceptorMapping> result = new ArrayList<>();

        if (referencedConfig == null) {
            throw new ConfigurationException("Unable to find interceptor class referenced by ref-name " + refName, location);
        } else {
            if (referencedConfig instanceof InterceptorConfig) {
                InterceptorConfig config = (InterceptorConfig) referencedConfig;
                Interceptor inter;
                try {
                    inter = objectFactory.buildInterceptor(config, refParams);
                    result.add(new InterceptorMapping(refName, inter, refParams));
                } catch (ConfigurationException ex) {
              	    LOG.warn(new ParameterizedMessage("Unable to load config class {} at {} probably due to a missing jar, which might be fine if you never plan to use the {} interceptor",
                            config.getClassName(), ex.getLocation(), config.getName()), ex);
                }

            } else if (referencedConfig instanceof InterceptorStackConfig) {
                InterceptorStackConfig stackConfig = (InterceptorStackConfig) referencedConfig;

                if ((refParams != null) && (refParams.size() > 0)) {
                    result = constructParameterizedInterceptorReferences(interceptorLocator, stackConfig, refParams, objectFactory);
                } else {
                    result.addAll(stackConfig.getInterceptors());
                }

            } else {
                LOG.error("Got unexpected type for interceptor {}. Got {}", refName, referencedConfig);
            }
        }

        return result;
    }

    /**
     * Builds a list of interceptors referenced by the refName in the supplied PackageConfig overriding the properties
     * of the referenced interceptor with refParams.
     *
     * @param interceptorLocator interceptor locator
     * @param stackConfig interceptor stack configuration
     * @param refParams The overridden interceptor properties
     * @return list of interceptors referenced by the refName in the supplied PackageConfig overridden with refParams.
     */
    private static List<InterceptorMapping> constructParameterizedInterceptorReferences(
            InterceptorLocator interceptorLocator, InterceptorStackConfig stackConfig, Map<String,String> refParams,
            ObjectFactory objectFactory) {
        List<InterceptorMapping> result;
        Map<String, Map<String, String>> params = new LinkedHashMap<>();

        /*
         * We strip
         *
         * <interceptor-ref name="someStack">
         *    <param name="interceptor1.param1">someValue</param>
         *    <param name="interceptor1.param2">anotherValue</param>
         * </interceptor-ref>
         *
         * down to map
         *  interceptor1 -> [param1 -> someValue, param2 -> anotherValue]
         *
         * or
         * <interceptor-ref name="someStack">
         *    <param name="interceptorStack1.interceptor1.param1">someValue</param>
         *    <param name="interceptorStack1.interceptor1.param2">anotherValue</param>
         * </interceptor-ref>
         *
         * down to map
         *  interceptorStack1 -> [interceptor1.param1 -> someValue, interceptor1.param2 -> anotherValue]
         *
         */
        for (Map.Entry<String, String> entry : refParams.entrySet()) {
            String key = entry.getKey();

            try {
                String name = key.substring(0, key.indexOf('.'));
                key = key.substring(key.indexOf('.') + 1);

                Map<String, String> map;
                if (params.containsKey(name)) {
                    map = params.get(name);
                } else {
                    map = new LinkedHashMap<>();
                }

                map.put(key, entry.getValue());
                params.put(name, map);

            } catch (Exception e) {
                LOG.warn("No interceptor found for name = {}", key);
            }
        }

        result = new ArrayList<>(stackConfig.getInterceptors());

        for (Map.Entry<String, Map<String, String>> entry : params.entrySet()) {
            String key = entry.getKey();
            Map<String, String> map = entry.getValue();


            Object interceptorCfgObj = interceptorLocator.getInterceptorConfig(key);

            /*
             * Now we attempt to separate out param that refers to Interceptor
             * and Interceptor stack, eg.
             *
             * <interceptor-ref name="someStack">
             *    <param name="interceptor1.param1">someValue</param>
             *    ...
             * </interceptor-ref>
             *
             *  vs
             *
             *  <interceptor-ref name="someStack">
             *    <param name="interceptorStack1.interceptor1.param1">someValue</param>
             *    ...
             *  </interceptor-ref>
             */
            if (interceptorCfgObj instanceof InterceptorConfig) {  //  interceptor-ref param refer to an interceptor
                InterceptorConfig cfg = (InterceptorConfig) interceptorCfgObj;
                Interceptor interceptor = objectFactory.buildInterceptor(cfg, map);

                InterceptorMapping mapping = new InterceptorMapping(key, interceptor);
                if (result.contains(mapping)) {
                    for (int index = 0; index < result.size(); index++) {
                        InterceptorMapping interceptorMapping = result.get(index);
                        if (interceptorMapping.getName().equals(key)) {
                            LOG.debug("Overriding interceptor config [{}] with new mapping {} using new params {}", key, interceptorMapping, map);
                            result.set(index, mapping);
                        }
                    }
                } else {
                    result.add(mapping);
                }
            } else
            if (interceptorCfgObj instanceof InterceptorStackConfig) {  // interceptor-ref param refer to an interceptor stack

                // If its an interceptor-stack, we call this method recursively until,
                // all the params (eg. interceptorStack1.interceptor1.param etc.)
                // are resolved down to a specific interceptor.

                InterceptorStackConfig stackCfg = (InterceptorStackConfig) interceptorCfgObj;
                List<InterceptorMapping> tmpResult = constructParameterizedInterceptorReferences(interceptorLocator, stackCfg, map, objectFactory);
                for (InterceptorMapping tmpInterceptorMapping : tmpResult) {
                    if (result.contains(tmpInterceptorMapping)) {
                        int index = result.indexOf(tmpInterceptorMapping);
                        result.set(index, tmpInterceptorMapping);
                    } else {
                        result.add(tmpInterceptorMapping);
                    }
                }
            }
        }

        return result;
    }
}
