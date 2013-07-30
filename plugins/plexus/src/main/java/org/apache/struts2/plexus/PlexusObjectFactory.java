/*
 * $Id$
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

package org.apache.struts2.plexus;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import com.opensymphony.xwork2.validator.Validator;
import org.codehaus.plexus.PlexusContainer;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

/**
 * Plexus integartion. You need three optional files: plexus-request.xml, plexus-session.xml, and
 * plexus-application.xml.
 * <p/>
 * The syntax of these files is:
 * <p/>
 * <pre>
 * &lt;plexus&gt;
 * &lt;components&gt;
 *  &lt;component&gt;
 *      &lt;role&gt;com.acme.MyBean&lt;/role&gt;
 *      &lt;implementation&gt;com.acme.MyBean|com.acme.MyBeanImpl&lt;/implementation&gt;
 *      &lt;componentComposer&gt;field|setter|?&lt;/componentComposer&gt;
 *      &lt;requirements&gt;
 *          &lt;requirement&gt;
 *              &lt;role&gt;com.acme.MyOtherBean&lt;/role&gt;
 *          &lt;/requirement&gt;
 *      &lt;/requirements&gt;
 *      &lt;configuration&gt;
 *          &lt;foo&gt;123&lt;/foo&gt;
 *          &lt;bar&gt;hello, world&lt;/bar&gt;
 *      &lt;/configuration&gt;
 *      &lt;/component&gt;
 *  &lt;/components&gt;
 * &lt;/plexus&gt;
 * </pre>
 *
 * @author <a href="mailto:evenisse@apache.org">Emmanuel Venisse</a>
 */
public class PlexusObjectFactory extends ObjectFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PlexusObjectFactory.class);

    private static final String PLEXUS_COMPONENT_TYPE = "plexus.component.type";

    private PlexusContainer base;
    private ReflectionProvider reflectionProvider;

    @Inject
    public void setReflectionProvider(ReflectionProvider reflectionProvider) {
        this.reflectionProvider = reflectionProvider;
    }

    @Inject
    public void setServletConfig(ServletContext servletContext) {
        if (!PlexusLifecycleListener.isLoaded() || !PlexusFilter.isLoaded()) {
            // uh oh! looks like the lifecycle listener wasn't installed. Let's inform the user
            String message = "********** FATAL ERROR STARTING UP PLEXUS-STRUTS INTEGRATION **********\n" +
                    "Looks like the Plexus listener was not configured for your web app! \n" +
                    "You need to add the following to web.xml: \n" +
                    "\n" +
                    "    <!-- this should be before the Struts filter -->\n" +
                    "    <filter>\n" +
                    "        <filter-name>plexus</filter-name>\n" +
                    "        <filter-class>org.apache.struts2.plexus.PlexusFilter</filter-class>\n" +
                    "    </filter>\n" +
                    "\n" +
                    "...\n" +
                    "\n" +
                    "    <!-- this should be before the Struts filter -->\n" +
                    "    <filter-mapping>\n" +
                    "        <filter-name>plexus</filter-name>\n" +
                    "        <url-pattern>/*</url-pattern>\n" +
                    "    </filter-mapping>\n" +
                    "\n" +
                    "...\n" +
                    "\n" +
                    "    <listener>\n" +
                    "        <listener-class>org.apache.struts2.plexus.PlexusLifecycleListener</listener-class>\n" +
                    "    </listener>";
            LOG.fatal(message);
            return;
        }

        base = (PlexusContainer) servletContext.getAttribute(PlexusLifecycleListener.KEY);
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.ObjectFactory#buildAction(java.lang.String, java.lang.String, com.opensymphony.xwork2.config.entities.ActionConfig, java.util.Map)
     */
    public Object buildAction(String actionName, String namespace, ActionConfig config, Map extraContext)
            throws Exception {
        if (extraContext == null) {
            extraContext = new HashMap();
        }

        extraContext.put(PLEXUS_COMPONENT_TYPE, Action.class.getName());

        return super.buildAction(actionName, namespace, config, extraContext);
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.ObjectFactory#buildInterceptor(com.opensymphony.xwork2.config.entities.InterceptorConfig, java.util.Map)
     */
    public Interceptor buildInterceptor(InterceptorConfig interceptorConfig, Map interceptorRefParams)
            throws ConfigurationException {
        String interceptorClassName = interceptorConfig.getClassName();
        Map thisInterceptorClassParams = interceptorConfig.getParams();
        Map params = (thisInterceptorClassParams == null) ? new HashMap() : new HashMap(thisInterceptorClassParams);
        params.putAll(interceptorRefParams);

        String message;
        Throwable cause;

        try {
            Map extraContext = new HashMap();
            extraContext.put(PLEXUS_COMPONENT_TYPE, Interceptor.class.getName());
            Interceptor interceptor = (Interceptor) buildBean(interceptorClassName, extraContext);
            reflectionProvider.setProperties(params, interceptor);
            interceptor.init();

            return interceptor;
        }
        catch (InstantiationException e) {
            cause = e;
            message = "Unable to instantiate an instance of Interceptor class [" + interceptorClassName + "].";
        }
        catch (IllegalAccessException e) {
            cause = e;
            message = "IllegalAccessException while attempting to instantiate an instance of Interceptor class [" + interceptorClassName + "].";
        }
        catch (ClassCastException e) {
            cause = e;
            message = "Class [" + interceptorClassName + "] does not implement com.opensymphony.xwork2.interceptor.Interceptor";
        }
        catch (Exception e) {
            cause = e;
            message = "Caught Exception while registering Interceptor class " + interceptorClassName;
        }
        catch (NoClassDefFoundError e) {
            cause = e;
            message = "Could not load class " + interceptorClassName + ". Perhaps it exists but certain dependencies are not available?";
        }

        throw new ConfigurationException(message, cause);
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.ObjectFactory#buildResult(com.opensymphony.xwork2.config.entities.ResultConfig, java.util.Map)
     */
    public Result buildResult(ResultConfig resultConfig, Map extraContext)
            throws Exception {
        if (extraContext == null) {
            extraContext = new HashMap();
        }

        extraContext.put(PLEXUS_COMPONENT_TYPE, Result.class.getName());

        return super.buildResult(resultConfig, extraContext);
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.ObjectFactory#buildValidator(java.lang.String, java.util.Map, java.util.Map)
     */
    public Validator buildValidator(String className, Map params, Map extraContext)
            throws Exception {
        Map context = new HashMap();
        context.put(PLEXUS_COMPONENT_TYPE, Validator.class.getName());
        Validator validator = (Validator) buildBean(className, context);
        reflectionProvider.setProperties(params, validator);

        return validator;
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.ObjectFactory#buildBean(java.lang.Class, java.util.Map)
     */
    public Object buildBean(Class clazz, Map extraContext)
            throws Exception {
        try {
            return lookup(clazz.getName(), extraContext);
        }
        catch (Exception e) {
            if (extraContext != null) {
                String type = (String) extraContext.get(PLEXUS_COMPONENT_TYPE);

                if (type != null) {
                    return lookup(type, clazz.getName(), extraContext);
                }
            }

            throw e;
        }
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.ObjectFactory#getClassInstance(java.lang.String)
     */
    public Class getClassInstance(String className)
            throws ClassNotFoundException {
        PlexusContainer pc = PlexusThreadLocal.getPlexusContainer();

        if (pc == null) {
            pc = base;
        }

        try {
            return pc.lookup(className).getClass();
        }
        catch (Exception e1) {
            try {
                return pc.lookup(Action.class.getName(), className).getClass();
            }
            catch (Exception e2) {
                try {
                    return pc.lookup(Interceptor.class.getName(), className).getClass();
                }
                catch (Exception e3) {
                    try {
                        return pc.lookup(Validator.class.getName(), className).getClass();
                    }
                    catch (Exception e4) {
                        try {
                            return pc.lookup(Result.class.getName(), className).getClass();
                        }
                        catch (Exception e5) {
                            return super.getClassInstance(className);
                        }
                    }
                }
            }
        }
    }

    /**
     * Looks up an object
     *
     * @param role The role name
     * @param extraContext The extra context
     * @return The object
     * @throws Exception If the lookup fails
     */
    private Object lookup(String role, Map extraContext)
            throws Exception {
        return lookup(role, null, extraContext);
    }

    /**
     * Looks up an object
     *
     * @param role The role name
     * @param roleHint The role hint
     * @param extraContext The extra context
     * @return The object
     * @throws Exception If the lookup fails
     */
    private Object lookup(String role, String roleHint, Map extraContext)
            throws Exception {
        PlexusContainer pc = PlexusThreadLocal.getPlexusContainer();

        if (pc == null) {
            pc = base;
        }

        try {
            return pc.lookup(role, roleHint);
        }
        catch (Exception e) {
            if (LOG.isDebugEnabled()) {
        	LOG.debug("Can't load component (" + role + "/" + roleHint + ") with plexus, try now with struts.", e);
            }
            Object o = super.buildBean(super.getClassInstance(role), extraContext);
            pc.autowire(o);
            return o;
        }
    }
}
