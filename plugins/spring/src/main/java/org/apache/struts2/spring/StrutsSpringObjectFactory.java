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

package org.apache.struts2.spring;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.spring.SpringObjectFactory;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.StrutsConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;



/**
 * Struts object factory that integrates with Spring.
 * <p/>
 * Spring should be loaded using a web context listener
 * <code>org.springframework.web.context.ContextLoaderListener</code> defined in <code>web.xml</code>.
 *
 */
public class StrutsSpringObjectFactory extends SpringObjectFactory {
    private static final Logger LOG = LoggerFactory.getLogger(StrutsSpringObjectFactory.class);

    //@Inject
    //public StrutsSpringObjectFactory(
    //        @Inject(value=StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_AUTOWIRE,required=false) String autoWire,
    //        @Inject(value=StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_USE_CLASS_CACHE,required=false) String useClassCacheStr,
    //        @Inject ServletContext servletContext) {
    //    this(autoWire, "false", useClassCacheStr, servletContext);
    //}

    /**
     * Constructs the spring object factory
     * @param autoWire The type of autowiring to use
     * @param alwaysAutoWire Whether to always respect the autowiring or not
     * @param useClassCacheStr Whether to use the class cache or not
     * @param servletContext The servlet context
     * @since 2.1.3
     */
    @Inject
    public StrutsSpringObjectFactory(
            @Inject(value=StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_AUTOWIRE,required=false) String autoWire,
            @Inject(value=StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_AUTOWIRE_ALWAYS_RESPECT,required=false) String alwaysAutoWire,
            @Inject(value=StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_USE_CLASS_CACHE,required=false) String useClassCacheStr,
            @Inject(value=StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_ENABLE_AOP_SUPPORT,required=false) String enableAopSupport,
            @Inject ServletContext servletContext,
            @Inject(StrutsConstants.STRUTS_DEVMODE) String devMode,
            @Inject Container container) {
          
        super();
        boolean useClassCache = "true".equals(useClassCacheStr);
        if (LOG.isInfoEnabled()) {
            LOG.info("Initializing Struts-Spring integration...");
        }

        Object rootWebApplicationContext =  servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        if(rootWebApplicationContext instanceof RuntimeException){
            RuntimeException runtimeException = (RuntimeException)rootWebApplicationContext;
            LOG.fatal(runtimeException.getMessage());
            return;
        }

        ApplicationContext appContext = (ApplicationContext) rootWebApplicationContext;
        if (appContext == null) {
            // uh oh! looks like the lifecycle listener wasn't installed. Let's inform the user
            String message = "********** FATAL ERROR STARTING UP STRUTS-SPRING INTEGRATION **********\n" +
                    "Looks like the Spring listener was not configured for your web app! \n" +
                    "Nothing will work until WebApplicationContextUtils returns a valid ApplicationContext.\n" +
                    "You might need to add the following to web.xml: \n" +
                    "    <listener>\n" +
                    "        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>\n" +
                    "    </listener>";
            LOG.fatal(message);
            return;
        }
        
        String watchList = container.getInstance(String.class, "struts.class.reloading.watchList");
        String acceptClasses = container.getInstance(String.class, "struts.class.reloading.acceptClasses");
        String reloadConfig = container.getInstance(String.class, "struts.class.reloading.reloadConfig");

        if ("true".equals(devMode)
                && StringUtils.isNotBlank(watchList)
                && appContext instanceof ClassReloadingXMLWebApplicationContext) {
            //prevent class caching
            useClassCache = false;

            ClassReloadingXMLWebApplicationContext reloadingContext = (ClassReloadingXMLWebApplicationContext) appContext;
            reloadingContext.setupReloading(watchList.split(","), acceptClasses, servletContext, "true".equals(reloadConfig));
            if (LOG.isInfoEnabled()) {
        	LOG.info("Class reloading is enabled. Make sure this is not used on a production environment!", watchList);
            }

            setClassLoader(reloadingContext.getReloadingClassLoader());

            //we need to reload the context, so our isntance of the factory is picked up
            reloadingContext.refresh();
        }

        this.setApplicationContext(appContext);

        int type = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;   // default
        if ("name".equals(autoWire)) {
            type = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;
        } else if ("type".equals(autoWire)) {
            type = AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;
        } else if ("auto".equals(autoWire)) {
            type = AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT;
        } else if ("constructor".equals(autoWire)) {
            type = AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR;
        } else if ("no".equals(autoWire)) {
            type = AutowireCapableBeanFactory.AUTOWIRE_NO;
        }
        this.setAutowireStrategy(type);

        this.setUseClassCache(useClassCache);

        this.setAlwaysRespectAutowireStrategy("true".equalsIgnoreCase(alwaysAutoWire));

        this.setEnableAopSupport(enableAopSupport);

        if (LOG.isInfoEnabled()) {
            LOG.info("... initialized Struts-Spring integration successfully");
        }
    }
}
