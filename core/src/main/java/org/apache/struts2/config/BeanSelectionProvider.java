/*
 * $Id:  $
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
package org.apache.struts2.config;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.multipart.MultiPartRequest;
import org.apache.struts2.views.freemarker.FreemarkerManager;
import org.apache.struts2.views.velocity.VelocityManager;

import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Context;
import com.opensymphony.xwork2.inject.Factory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.inject.Scope;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.ObjectTypeDeterminer;
import com.opensymphony.xwork2.util.ObjectTypeDeterminerFactory;

public class BeanSelectionProvider implements ConfigurationProvider {
    public static final String DEFAULT_BEAN_NAME = "struts";
    private static final Log LOG = LogFactory.getLog(BeanSelectionProvider.class);
    
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

    public void register(ContainerBuilder builder, Properties props) {
        alias(ObjectFactory.class, StrutsConstants.STRUTS_OBJECTFACTORY, builder, props);
        alias(ActionProxyFactory.class, StrutsConstants.STRUTS_ACTIONPROXYFACTORY, builder, props);
        alias(ObjectTypeDeterminer.class, StrutsConstants.STRUTS_OBJECTTYPEDETERMINER, builder, props);
        alias(ActionMapper.class, StrutsConstants.STRUTS_MAPPER_CLASS, builder, props);
        alias(MultiPartRequest.class, StrutsConstants.STRUTS_MULTIPART_PARSER, builder, props, Scope.DEFAULT);
        alias(FreemarkerManager.class, StrutsConstants.STRUTS_FREEMARKER_MANAGER_CLASSNAME, builder, props);
        alias(VelocityManager.class, StrutsConstants.STRUTS_VELOCITY_MANAGER_CLASSNAME, builder, props);
        
        if ("true".equalsIgnoreCase(props.getProperty(StrutsConstants.STRUTS_DEVMODE))) {
            props.setProperty(StrutsConstants.STRUTS_I18N_RELOAD, "true");
            props.setProperty(StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD, "true");
        }
    }
    
    void alias(Class type, String key, ContainerBuilder builder, Properties props) {
        alias(type, key, builder, props, Scope.SINGLETON);
    }
    
    void alias(Class type, String key, ContainerBuilder builder, Properties props, Scope scope) {
        if (!builder.contains(type)) {
            String foundName = props.getProperty(key, DEFAULT_BEAN_NAME);
            if (builder.contains(type, foundName)) {
                if (LOG.isDebugEnabled()) {
                    LOG.info("Choosing bean ("+foundName+") for "+type);
                }
                builder.alias(type, foundName, Container.DEFAULT_NAME);
            } else {
                try {
                    Class cls = ClassLoaderUtil.loadClass(foundName, this.getClass());
                    if (LOG.isDebugEnabled()) {
                        LOG.info("Choosing bean ("+cls+") for "+type);
                    }
                    builder.factory(type, cls, scope);
                } catch (ClassNotFoundException ex) {
                    // Perhaps a spring bean id, so we'll delegate to the object factory at runtime
                    if (LOG.isDebugEnabled()) {
                        LOG.info("Choosing bean ("+foundName+") for "+type+" to be loaded from the ObjectFactory");
                    }
                    builder.factory(type, new ObjectFactoryDelegateFactory(foundName), scope);
                }
            }
        } else {
            LOG.warn("Unable to alias bean type "+type+", default mapping already assigned.");
        }
    }
    
    class ObjectFactoryDelegateFactory implements Factory {
        String name;
        ObjectFactoryDelegateFactory(String name) {
            this.name = name;
        }
        
        public Object create(Context context) throws Exception {
            ObjectFactory objFactory = context.getContainer().getInstance(ObjectFactory.class);
            return objFactory.buildBean(name, null, false);
        }
    }
}
