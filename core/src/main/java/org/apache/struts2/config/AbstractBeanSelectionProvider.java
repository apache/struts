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

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.BeanSelectionProvider;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.*;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

/**
 * TODO lukaszlenart: write a JavaDoc
 */
public abstract class AbstractBeanSelectionProvider implements BeanSelectionProvider {

    private static final Logger LOG = LogManager.getLogger(AbstractBeanSelectionProvider.class);

    public static final String DEFAULT_BEAN_NAME = "struts";

    public void destroy() {
        // NO-OP
    }

    public void loadPackages() throws ConfigurationException {
        // NO-OP
    }

    public void init(Configuration configuration) throws ConfigurationException {
        // NO-OP
    }

    public boolean needsReload() {
        return false;
    }

    protected void alias(Class type, String key, ContainerBuilder builder, Properties props) {
        alias(type, key, builder, props, Scope.SINGLETON);
    }

    protected void alias(Class type, String key, ContainerBuilder builder, Properties props, Scope scope) {
        if (!builder.contains(type, Container.DEFAULT_NAME)) {
            String foundName = props.getProperty(key, DEFAULT_BEAN_NAME);
            if (builder.contains(type, foundName)) {
                LOG.trace("Choosing bean ({}) for ({})", foundName, type.getName());
                builder.alias(type, foundName, Container.DEFAULT_NAME);
            } else {
                try {
                    Class cls = ClassLoaderUtil.loadClass(foundName, this.getClass());
                    LOG.trace("Choosing bean ({}) for ({})", cls.getName(), type.getName());
                    builder.factory(type, cls, scope);
                } catch (ClassNotFoundException ex) {
                    // Perhaps a spring bean id, so we'll delegate to the object factory at runtime
                    LOG.trace("Choosing bean ({}) for ({}) to be loaded from the ObjectFactory", foundName, type.getName());
                    if (DEFAULT_BEAN_NAME.equals(foundName)) {
                        // Probably an optional bean, will ignore
                    } else {
                        if (ObjectFactory.class != type) {
                            builder.factory(type, new ObjectFactoryDelegateFactory(foundName, type), scope);
                        } else {
                            throw new ConfigurationException("Cannot locate the chosen ObjectFactory implementation: " + foundName);
                        }
                    }
                }
            }
        } else {
            LOG.warn("Unable to alias bean type ({}), default mapping already assigned.", type.getName());
        }
    }

    protected void convertIfExist(LocatableProperties props, String fromKey, String toKey) {
        if (props.containsKey(fromKey)) {
            props.setProperty(toKey, props.getProperty(fromKey));
        }
    }

    static class ObjectFactoryDelegateFactory implements Factory {

        String name;
        Class type;

        ObjectFactoryDelegateFactory(String name, Class type) {
            this.name = name;
            this.type = type;
        }

        public Object create(Context context) throws Exception {
            ObjectFactory objFactory = context.getContainer().getInstance(ObjectFactory.class);
            try {
                return objFactory.buildBean(name, null, true);
            } catch (ClassNotFoundException ex) {
                throw new ConfigurationException("Unable to load bean "+type.getName()+" ("+name+")");
            }
        }

        @Override
        public Class type() {
            return type;
        }
    }
}
