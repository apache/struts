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
package com.opensymphony.xwork2.spring;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SpringProxyableObjectFactory.
 *
 * @author Jason Carreira
 */
public class SpringProxyableObjectFactory extends SpringObjectFactory {
    
    private static final Logger LOG = LoggerFactory.getLogger(SpringProxyableObjectFactory.class);

    private List<String> skipBeanNames = new ArrayList<String>();

    @Override
    public Object buildBean(String beanName, Map<String, Object> extraContext) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Building bean for name " + beanName);
        }
        if (!skipBeanNames.contains(beanName)) {
            ApplicationContext anAppContext = getApplicationContext(extraContext);
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Trying the application context... appContext = " + anAppContext + ",\n bean name = " + beanName);
                }
                return anAppContext.getBean(beanName);
            } catch (NoSuchBeanDefinitionException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Did not find bean definition for bean named " + beanName + ", creating a new one...");
                }
                if (autoWiringFactory instanceof BeanDefinitionRegistry) {
                    try {
                        Class clazz = Class.forName(beanName);
                        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) autoWiringFactory;
                        RootBeanDefinition def = new RootBeanDefinition(clazz, autowireStrategy);
                        def.setSingleton(false);
                         if (LOG.isDebugEnabled()) {
                            LOG.debug("Registering a new bean definition for class " + beanName);
                        }
                        registry.registerBeanDefinition(beanName,def);
                        try {
                            return anAppContext.getBean(beanName);
                        } catch (NoSuchBeanDefinitionException e2) {
                            if (LOG.isWarnEnabled()) {
                        	LOG.warn("Could not register new bean definition for bean " + beanName);
                            }
                            skipBeanNames.add(beanName);
                        }
                    } catch (ClassNotFoundException e1) {
                        skipBeanNames.add(beanName);
                    }
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Returning autowired instance created by default ObjectFactory");
        }
        return autoWireBean(super.buildBean(beanName, extraContext), autoWiringFactory);
    }

    /**
     * Subclasses may override this to return a different application context.
     * Note that this application context should see any changes made to the
     * <code>autoWiringFactory</code>, so the application context should be either
     * the original or a child context of the original.
     *
     * @param context  provided context.
     */
    protected ApplicationContext getApplicationContext(Map<String, Object> context) {
        return appContext;
    }
}

