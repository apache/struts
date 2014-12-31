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
import com.opensymphony.xwork2.inject.Inject;
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
 * The BeanManager instance will be searched in the container's JNDI context, according to following algorithm:
 * <ul>
 *     <li>if a value for configuration constant <code>struts.objectFactory.cdi.jndiKey</code> is given, this key will be looked up</li>
 *     <li>if no BeanManager found so far, look under {@link #CDI_JNDIKEY_BEANMANAGER_COMP}</li>
 *     <li>if no BeanManager found so far, look under {@link #CDI_JNDIKEY_BEANMANAGER_APP}</li>
 *     <li>if no BeanManager found so far, look under {@link #CDI_JNDIKEY_BEANMANAGER_COMP_ENV}</li>
 * </ul>
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
	/**
	 * The key under which the BeanManager can be found in pure Servlet containers according to JBoss Weld docs.
	 */
	public static final String CDI_JNDIKEY_BEANMANAGER_COMP_ENV = "java:comp/env/BeanManager";


	private String jndiKey;

	@Inject(value = "struts.objectFactory.cdi.jndiKey", required = false)
	public void setJndiKey( String jndiKey ) {
		this.jndiKey = jndiKey;
	}

	protected BeanManager beanManager;

    Map<Class<?>, InjectionTarget<?>> injectionTargetCache = new ConcurrentHashMap<Class<?>, InjectionTarget<?>>();

    public CdiObjectFactory() {
        super();
        LOG.info("Initializing Struts2 CDI integration...");
        this.beanManager = findBeanManager();
        if (beanManager != null) {
            LOG.info("Struts2 CDI integration initialized.");
        } else {
            LOG.error("Struts2 CDI integration could not be initialized.");
        }
    }

	/**
	 * Try to find the CDI BeanManager from JNDI context. First, if provided, the key given by
	 * struts.objectFactory.cdi.jndiKey will be checked. Then, if nothing was found or no explicit configuration was
	 * given, the key {@link #CDI_JNDIKEY_BEANMANAGER_COMP} will be tested. If nothing is found there, the key {@link
	 * #CDI_JNDIKEY_BEANMANAGER_APP} will be checked. If still nothing is found there, the key {@link
	 * #CDI_JNDIKEY_BEANMANAGER_COMP_ENV} will be checked.
	 *
	 * @return the BeanManager, if found. <tt>null</tt> otherwise.
	 */
	protected BeanManager findBeanManager() {
		BeanManager bm = null;
		try {
			Context initialContext = new InitialContext();
			if (jndiKey != null && jndiKey.trim().length() > 0) {
				// Check explicit configuration first, if given
				bm = lookup(initialContext, jndiKey);
			}
			if (bm == null) {
				// Check CDI default
				bm = lookup(initialContext, CDI_JNDIKEY_BEANMANAGER_COMP);
			}
			if (bm == null) {
				// Check WELD default
				bm = lookup(initialContext, CDI_JNDIKEY_BEANMANAGER_APP);
			}
			if (bm == null) {
				// Check Tomcat / Jetty default
				bm = lookup(initialContext, CDI_JNDIKEY_BEANMANAGER_COMP_ENV);
			}
			if (bm == null) {
				if (LOG.isErrorEnabled()) {
					LOG.error("[findBeanManager]: Could not find BeanManager instance for any given JNDI key, giving up");
				}
			}
		} catch ( NamingException e ) {
			if (LOG.isErrorEnabled()) {
				LOG.error("[findBeanManager]: Unable to get InitialContext for BeanManager lookup", e);
			}
		}
		return bm;
	}

	/**
	 * Lookup the given JNDI key in the given context.
	 *
	 * @param context the context to use for lookup.
	 * @param jndiKeyToCheck the key to lookup.
	 *
	 * @return the BeanManager, if found; <tt>null</tt> if not found or {@link javax.naming.NamingException} was thrown.
	 */
	protected BeanManager lookup( Context context, String jndiKeyToCheck ) {
		if (LOG.isInfoEnabled()) {
			LOG.info("[lookup]: Checking for BeanManager under JNDI key " + jndiKeyToCheck);
		}
		BeanManager result = null;
		try {
			result = (BeanManager) context.lookup(jndiKeyToCheck);
		} catch ( NamingException e ) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("[lookup]: BeanManager lookup failed for JNDI key " + jndiKeyToCheck, e);
			}
		}
		return result;
	}

	@Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Object buildBean(String className, Map<String, Object> extraContext, boolean injectInternal)
            throws Exception {

        Class<?> clazz = getClassInstance(className);
        InjectionTarget injectionTarget = getInjectionTarget(clazz);

        // a separate CreationalContext is required for every bean
        final CreationalContext ctx = buildNonContextualCreationalContext(beanManager);

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

    /**
     * Allow constructor injection
     */
    @Override
    public boolean isNoArgConstructorRequired() {
        return false;
    }

}
