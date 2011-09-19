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

package org.apache.struts2.cdi;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CdiObjectFactory allows Struts 2 managed objects, like Actions, Interceptors or Results, to be injected by a Contexts
 * and Dependency Injection container (JSR299 / WebBeans).
 */
public class CdiObjectFactory extends ObjectFactory {

    private static final Logger LOG = LoggerFactory.getLogger(CdiObjectFactory.class);

    /**
     * The key under which the BeanManager can be found according to CDI API docs
     */
    public static final String CDI_JNDIKEY_BEANMANAGER_COMP = "java:comp/BeanManager";
    /**
     * The key under which the BeanManager can be found according to JBoss Weld docs
     */
    public static final String CDI_JNDIKEY_BEANMANAGER_APP = "java:app/BeanManager";

    protected BeanManager beanManager;
    protected CreationalContext ctx;

    Map<Class<?>, InjectionTarget<?>> injectionTargetCache = new ConcurrentHashMap<Class<?>, InjectionTarget<?>>();

    public CdiObjectFactory() {
        super();
        LOG.info("Initializing Struts2 CDI integration...");
        this.beanManager = findBeanManager();
        if (beanManager != null) {
            this.ctx = buildNonContextualCreationalContext(beanManager);
            LOG.info("Struts2 CDI integration initialized.");
        } else {
            LOG.error("Struts2 CDI integration could not be initialized.");
        }
    }

    /**
     * Try to find the CDI BeanManager from JNDI context. First the key {@link #CDI_JNDIKEY_BEANMANAGER_COMP} will be
     * tested. If nothing is found there, the key {@link #CDI_JNDIKEY_BEANMANAGER_APP} will be checked.
     *
     * @return the BeanManager, if found. <tt>null</tt> otherwise.
     */
    protected BeanManager findBeanManager() {
        BeanManager bm;
        try {
            Context initialContext = new InitialContext();
            LOG.info("[findBeanManager]: Checking for BeanManager under JNDI key " + CDI_JNDIKEY_BEANMANAGER_COMP);
            try {
                bm = (BeanManager) initialContext.lookup(CdiObjectFactory.CDI_JNDIKEY_BEANMANAGER_COMP);
            } catch (NamingException e) {
                LOG.warn("[findBeanManager]: Lookup failed.", e);
                LOG.info("[findBeanManager]: Checking for BeanManager under JNDI key " + CDI_JNDIKEY_BEANMANAGER_APP);
                bm = (BeanManager) initialContext.lookup(CdiObjectFactory.CDI_JNDIKEY_BEANMANAGER_APP);
            }
            LOG.info("[findBeanManager]: BeanManager found.");
            return bm;
        } catch (NamingException e) {
            LOG.error("Could not get BeanManager from JNDI context", e);
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object buildBean(String className, Map<String, Object> extraContext, boolean injectInternal)
            throws Exception {

        Class<?> clazz = getClassInstance(className);
        InjectionTarget injectionTarget = getInjectionTarget(clazz);

        Object o = injectionTarget.produce(ctx);
        injectionTarget.inject(o, ctx);
        injectionTarget.postConstruct(o);

        if (injectInternal) {
            injectInternalBeans(o);
        }

        return o;
    }

    /**
     * Get a InjectionTarget instance for a given class. If the appropriate target is not found in cache, a nw instance
     * will be created.
     *
     * @param clazz The class to get a InjectionTarget instance for.
     * @return if found in cache, an existing instance. A new instance otherwise.
     */
    protected InjectionTarget<?> getInjectionTarget(Class<?> clazz) {
        InjectionTarget<?> result;
        result = injectionTargetCache.get(clazz);
        if (result == null) {
            result = beanManager.createInjectionTarget(beanManager.createAnnotatedType(clazz));
            injectionTargetCache.put(clazz, result);
        }

        return result;
    }

    /**
     * Simple wrapper for CreationalContext creation.
     *
     * @param beanManager the BeanManager to use for creating the context.
     * @return the context to use, if given BeanManager was not <tt>null</tt>. <tt>null</tt> otherwise.
     */
    @SuppressWarnings("unchecked")
    protected CreationalContext buildNonContextualCreationalContext(BeanManager beanManager) {
        return beanManager != null ? beanManager.createCreationalContext(null) : null;
    }
}
