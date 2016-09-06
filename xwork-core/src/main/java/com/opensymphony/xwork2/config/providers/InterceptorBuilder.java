/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

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
 * @version $Date$ $Id$
 */
public class InterceptorBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(InterceptorBuilder.class);


    /**
     * Builds a list of interceptors referenced by the refName in the supplied PackageConfig (InterceptorMapping object).
     *
     * @param interceptorLocator
     * @param refName
     * @param refParams
     * @return list of interceptors referenced by the refName in the supplied PackageConfig (InterceptorMapping object).
     * @throws ConfigurationException
     */
    public static List<InterceptorMapping> constructInterceptorReference(InterceptorLocator interceptorLocator,
                                                                         String refName, Map<String,String> refParams, Location location, ObjectFactory objectFactory) throws ConfigurationException {
        Object referencedConfig = interceptorLocator.getInterceptorConfig(refName);
        List<InterceptorMapping> result = new ArrayList<InterceptorMapping>();

        if (referencedConfig == null) {
            throw new ConfigurationException("Unable to find interceptor class referenced by ref-name " + refName, location);
        } else {
            if (referencedConfig instanceof InterceptorConfig) {
                InterceptorConfig config = (InterceptorConfig) referencedConfig;
                Interceptor inter = null;
                try {

                    inter = objectFactory.buildInterceptor(config, refParams);
                    result.add(new InterceptorMapping(refName, inter));
                } catch (ConfigurationException ex) {
                    if (LOG.isWarnEnabled()) {
                	    LOG.warn("Unable to load config class #0 at #1 probably due to a missing jar, which might be fine if you never plan to use the #2 interceptor",
                            config.getClassName(), ex.getLocation().toString(), config.getName());
                    }
                    LOG.error("Actual exception", ex);
                }

            } else if (referencedConfig instanceof InterceptorStackConfig) {
                InterceptorStackConfig stackConfig = (InterceptorStackConfig) referencedConfig;

                if ((refParams != null) && (refParams.size() > 0)) {
                    result = constructParameterizedInterceptorReferences(interceptorLocator, stackConfig, refParams, objectFactory);
                } else {
                    result.addAll(stackConfig.getInterceptors());
                }

            } else {
                LOG.error("Got unexpected type for interceptor " + refName + ". Got " + referencedConfig);
            }
        }

        return result;
    }

    /**
     * Builds a list of interceptors referenced by the refName in the supplied PackageConfig overriding the properties
     * of the referenced interceptor with refParams.
     *
     * @param interceptorLocator
     * @param stackConfig
     * @param refParams          The overridden interceptor properies
     * @return list of interceptors referenced by the refName in the supplied PackageConfig overridden with refParams.
     */
    private static List<InterceptorMapping> constructParameterizedInterceptorReferences(
            InterceptorLocator interceptorLocator, InterceptorStackConfig stackConfig, Map<String,String> refParams,
            ObjectFactory objectFactory) {
        List<InterceptorMapping> result;
        Map<String, Map<String, String>> params = new LinkedHashMap<String, Map<String, String>>();

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
        for (String key : refParams.keySet()) {
            String value = refParams.get(key);

            try {
                String name = key.substring(0, key.indexOf('.'));
                key = key.substring(key.indexOf('.') + 1);

                Map<String, String> map;
                if (params.containsKey(name)) {
                    map = params.get(name);
                } else {
                    map = new LinkedHashMap<String, String>();
                }

                map.put(key, value);
                params.put(name, map);

            } catch (Exception e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("No interceptor found for name = #0", key);
                }
            }
        }

        result = new ArrayList<InterceptorMapping>(stackConfig.getInterceptors());

        for (String key : params.keySet()) {

            Map<String, String> map = params.get(key);


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
                            LOG.debug("Overriding interceptor config [#0] with new mapping #1 using new params #2", key, interceptorMapping, map);
                            result.set(index, mapping);
                        }
                    }
                } else {
                    result.add(mapping);
                }
            } else
            if (interceptorCfgObj instanceof InterceptorStackConfig) {  // interceptor-ref param refer to an interceptor stack

                // If its an interceptor-stack, we call this method recursively untill,
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
