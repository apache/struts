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
package com.opensymphony.xwork2.spring;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Simple implementation of the ObjectFactory that makes use of Spring's application context if one has been configured,
 * before falling back on the default mechanism of instantiating a new class using the class name.
 * </p>
 *
 * <p>
 * In order to use this class in your application, you will need to instantiate a copy of this class and set it as XWork's ObjectFactory
 * before the xwork.xml file is parsed. In a servlet environment, this could be done using a ServletContextListener.
 * </p>
 *
 * @author Simon Stewart (sms@lateral.net)
 */
public class SpringObjectFactory extends ObjectFactory implements ApplicationContextAware {
    private static final Logger LOG = LogManager.getLogger(SpringObjectFactory.class);

    protected ApplicationContext appContext;
    protected AutowireCapableBeanFactory autoWiringFactory;
    protected int autowireStrategy = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;
    private final Map<String, Object> classes = new HashMap<>();
    private boolean useClassCache = true;
    private boolean alwaysRespectAutowireStrategy = false;
    /**
     * This is temporary solution, after validating can be removed
     * @since 2.3.18
     */
    @Deprecated
    private boolean enableAopSupport = false;

    public SpringObjectFactory() {
    }

    @Inject(value="applicationContextPath",required=false)
    public void setApplicationContextPath(String ctx) {
        if (ctx != null) {
            setApplicationContext(new ClassPathXmlApplicationContext(ctx));
        }
    }

    @Inject(value = "enableAopSupport", required = false)
    public void setEnableAopSupport(String enableAopSupport) {
        this.enableAopSupport = BooleanUtils.toBoolean(enableAopSupport);
    }

    /**
     * Set the Spring ApplicationContext that should be used to look beans up with.
     *
     * @param appContext The Spring ApplicationContext that should be used to look beans up with.
     */
    public void setApplicationContext(ApplicationContext appContext) throws BeansException {
        this.appContext = appContext;
        autoWiringFactory = findAutoWiringBeanFactory(this.appContext);
    }

    /**
     * Sets the autowiring strategy
     *
     * @param autowireStrategy the autowire strategy
     */
    public void setAutowireStrategy(int autowireStrategy) {
        switch (autowireStrategy) {
            case AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT:
                LOG.info("Setting autowire strategy to autodetect");
                this.autowireStrategy = autowireStrategy;
                break;
            case AutowireCapableBeanFactory.AUTOWIRE_BY_NAME:
                LOG.info("Setting autowire strategy to name");
                this.autowireStrategy = autowireStrategy;
                break;
            case AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE:
                LOG.info("Setting autowire strategy to type");
                this.autowireStrategy = autowireStrategy;
                break;
            case AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR:
                LOG.info("Setting autowire strategy to constructor");
                this.autowireStrategy = autowireStrategy;
                break;
            case AutowireCapableBeanFactory.AUTOWIRE_NO:
                LOG.info("Setting autowire strategy to none");
                this.autowireStrategy = autowireStrategy;
                break;
            default:
                throw new IllegalStateException("Invalid autowire type set");
        }
    }

    public int getAutowireStrategy() {
        return autowireStrategy;
    }


    /**
     * If the given context is assignable to AutowireCapbleBeanFactory or contains a parent or a factory that is, then
     * set the autoWiringFactory appropriately.
     *
     * @param context the application context
     *
     * @return the bean factory
     */
    protected AutowireCapableBeanFactory findAutoWiringBeanFactory(ApplicationContext context) {
        if (context instanceof AutowireCapableBeanFactory) {
            // Check the context
            return (AutowireCapableBeanFactory) context;
        } else if (context instanceof ConfigurableApplicationContext) {
            // Try and grab the beanFactory
            return ((ConfigurableApplicationContext) context).getBeanFactory();
        } else if (context.getParent() != null) {
            // And if all else fails, try again with the parent context
            return findAutoWiringBeanFactory(context.getParent());
        }
        return null;
    }

    /**
     * Looks up beans using Spring's application context before falling back to the method defined in the {@link
     * ObjectFactory}.
     *
     * @param beanName     The name of the bean to look up in the application context
     * @param extraContext additional context parameters
     * @return A bean from Spring or the result of calling the overridden
     *         method.
     * @throws Exception in case of any errors
     */
    @Override
    public Object buildBean(String beanName, Map<String, Object> extraContext, boolean injectInternal) throws Exception {
        Object o;
        
        if (appContext.containsBean(beanName)) {
            o = appContext.getBean(beanName);
        } else {
            Class beanClazz = getClassInstance(beanName);
            o = buildBean(beanClazz, extraContext);
        }
        if (injectInternal) {
            injectInternalBeans(o);
        }
        return o;
    }

    /**
     * @param clazz class of bean
     * @param extraContext additional context parameters
     * @return bean
     * @throws Exception in case of any errors
     */
    @Override
    public Object buildBean(Class clazz, Map<String, Object> extraContext) throws Exception {
        Object bean;

        try {
            // Decide to follow autowire strategy or use the legacy approach which mixes injection strategies
            if (alwaysRespectAutowireStrategy) {
                // Leave the creation up to Spring
                bean = autoWiringFactory.createBean(clazz, autowireStrategy, false);
                injectApplicationContext(bean);
                return injectInternalBeans(bean);
            } else if (enableAopSupport) {
                bean = autoWiringFactory.createBean(clazz, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false);
                bean = autoWireBean(bean, autoWiringFactory);
                bean = autoWiringFactory.initializeBean(bean, bean.getClass().getName());
                return bean;
            } else {
                bean = autoWiringFactory.autowire(clazz, AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, false);
                bean = autoWiringFactory.initializeBean(bean, bean.getClass().getName());
                return autoWireBean(bean, autoWiringFactory);
            }
        } catch (UnsatisfiedDependencyException e) {
            LOG.error("Error building bean", e);
            // Fall back
            return autoWireBean(super.buildBean(clazz, extraContext), autoWiringFactory);
        }
    }

    public Object autoWireBean(Object bean) {
        return autoWireBean(bean, autoWiringFactory);
    }

    /**
     * @param bean the bean to be autowired
     * @param autoWiringFactory the autowiring factory
     *
     * @return bean
     */
    public Object autoWireBean(Object bean, AutowireCapableBeanFactory autoWiringFactory) {
        if (autoWiringFactory != null) {
            autoWiringFactory.autowireBeanProperties(bean, autowireStrategy, false);
        }
        injectApplicationContext(bean);

        injectInternalBeans(bean);

        return bean;
    }

    private void injectApplicationContext(Object bean) {
        if (bean instanceof ApplicationContextAware) {
            ((ApplicationContextAware) bean).setApplicationContext(appContext);
        }
    }

    public Class getClassInstance(String className) throws ClassNotFoundException {
        Class clazz = null;
        if (useClassCache) {
            synchronized(classes) {
                // this cache of classes is needed because Spring sucks at dealing with situations where the
                // class instance changes
                clazz = (Class) classes.get(className);
            }
        }

        if (clazz == null) {
            if (appContext.containsBean(className)) {
                clazz = appContext.getBean(className).getClass();
            } else {
                clazz = super.getClassInstance(className);
            }

            if (useClassCache) {
                synchronized(classes) {
                    classes.put(className, clazz);
                }
            }
        }

        return clazz;
    }

    /**
     * Allows for ObjectFactory implementations that support
     * Actions without no-arg constructors.
     *
     * @return false
     */
    @Override
    public boolean isNoArgConstructorRequired() {
        return false;
    }

    /**
     *  Enable / disable caching of classes loaded by Spring.
     *
     * @param useClassCache enable / disable class cache
     */
    public void setUseClassCache(boolean useClassCache) {
        this.useClassCache = useClassCache;
    }

    /**
     * Determines if the autowire strategy is always followed when creating beans
     *
     * @param alwaysRespectAutowireStrategy True if the strategy is always used
     */
    public void setAlwaysRespectAutowireStrategy(boolean alwaysRespectAutowireStrategy) {
        this.alwaysRespectAutowireStrategy = alwaysRespectAutowireStrategy;
    }
}
