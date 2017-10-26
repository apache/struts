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
package org.apache.struts2.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.config.entities.BeanConfig;
import org.apache.struts2.config.entities.ConstantConfig;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.UnknownHandlerConfig;
import com.opensymphony.xwork2.config.impl.LocatableFactory;
import com.opensymphony.xwork2.config.providers.ValueSubstitutor;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationUtils;

public class StrutsJavaConfigurationProvider implements ConfigurationProvider {
    private static final Logger LOG = LogManager.getLogger(StrutsJavaConfigurationProvider.class);

    private final StrutsJavaConfiguration javaConfig;
    private Configuration configuration;
    private boolean throwExceptionOnDuplicateBeans = true;
    private ValueSubstitutor valueSubstitutor;

    public StrutsJavaConfigurationProvider(StrutsJavaConfiguration javaConfig) {
        this.javaConfig = javaConfig;
    }

    public void setThrowExceptionOnDuplicateBeans(boolean val) {
        this.throwExceptionOnDuplicateBeans = val;
    }

    @Inject(required = false)
    public void setValueSubstitutor(ValueSubstitutor valueSubstitutor) {
        this.valueSubstitutor = valueSubstitutor;
    }

    @Override
    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
        Map<String, Object> loadedBeans = new HashMap<>();

        // bean
        List<BeanConfig> beanConfigs = javaConfig.beans();
        if (beanConfigs != null) {
            for (BeanConfig bc : beanConfigs) {
                if (bc != null) {
                    registerBean(loadedBeans, builder, bc);
                }
            }
        }

        // constant
        List<ConstantConfig> constantConfigList = javaConfig.constants();
        if (constantConfigList != null) {
            for (ConstantConfig constantConf : constantConfigList) {
                if (constantConf != null) {
                    Map<String, String> constantMap = constantConf.getAllAsStringsMap();
                    for (Entry<String, String> entr : constantMap.entrySet()) {
                        if (entr.getKey() != null && entr.getValue() != null) {
                            registerConstant(props, entr.getKey(), entr.getValue());
                        }
                    }
                }
            }
        }

        // unknown-handler-stack
        List<String> unknownHandlers = javaConfig.unknownHandlerStack();
        if (unknownHandlers != null) {
            List<UnknownHandlerConfig> unknownHandlerStack = new ArrayList<>();
            for (String unknownHandler : unknownHandlers) {
                Location location = LocationUtils.getLocation(unknownHandler);
                unknownHandlerStack.add(new UnknownHandlerConfig(unknownHandler, location));
            }

            if (!unknownHandlerStack.isEmpty()) {
                configuration.setUnknownHandlerStack(unknownHandlerStack);
            }
        }
    }

    private void registerConstant(LocatableProperties props, String key, String value) {
        if (valueSubstitutor != null) {
            LOG.debug("Substituting value [{}] using [{}]", value, valueSubstitutor.getClass().getName());
            value = valueSubstitutor.substitute(value);
        }

        props.setProperty(key, value, javaConfig);
    }

    private void registerBean(Map<String, Object> loadedBeans, ContainerBuilder containerBuilder, BeanConfig beanConf) {
        try {
            if (beanConf.isOnlyStatic()) {
                // Force loading of class to detect no class def found
                // exceptions
                beanConf.getClazz().getDeclaredClasses();
                containerBuilder.injectStatics(beanConf.getClazz());
            } else {
                if (containerBuilder.contains(beanConf.getType(), beanConf.getName())) {
                    Location loc = LocationUtils
                            .getLocation(loadedBeans.get(beanConf.getType().getName() + beanConf.getName()));
                    if (throwExceptionOnDuplicateBeans) {
                        throw new ConfigurationException("Bean type " + beanConf.getType() + " with the name "
                                + beanConf.getName() + " has already been loaded by " + loc, javaConfig);
                    }
                }

                // Force loading of class to detect no class def found
                // exceptions
                beanConf.getClazz().getDeclaredConstructors();

                LOG.debug("Loaded type: {} name: {} clazz: {}", beanConf.getType(), beanConf.getName(),
                        beanConf.getClazz());
                containerBuilder.factory(
                        beanConf.getType(), beanConf.getName(), new LocatableFactory(beanConf.getName(),
                                beanConf.getType(), beanConf.getClazz(), beanConf.getScope(), javaConfig),
                        beanConf.getScope());
            }
            loadedBeans.put(beanConf.getType().getName() + beanConf.getName(), javaConfig);
        } catch (Throwable ex) {
            if (!beanConf.isOptional()) {
                throw new ConfigurationException(
                        "Unable to load bean: type:" + beanConf.getType() + " class:" + beanConf.getClazz(), ex);
            } else {
                LOG.debug("Unable to load optional class: {}", beanConf.getClazz());
            }
        }
    }

    @Override
    public void init(Configuration configuration) throws ConfigurationException {
        this.configuration = configuration;
    }

    @Override
    public boolean needsReload() {
        return false;
    }

    @Override
    public void loadPackages() throws ConfigurationException {
    }

    @Override
    public void destroy() {
    }
}
