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
package org.apache.struts2.osgi.host;

import com.opensymphony.xwork2.config.ConfigurationException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.felix.main.AutoProcessor;
import org.apache.felix.main.Main;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Apache felix implementation of an OsgiHost
 * See http://felix.apache.org/site/apache-felix-framework-launching-and-embedding.html
 * <br>
 * Servlet config params:
 * <p>struts.osgi.clearBundleCache: Defaults to "true" delete installed bundles when the comntainer starts</p>
 * <p>struts.osgi.logLevel: Defaults to "1". Felix log level. 1 = error, 2 = warning, 3 = information, and 4 = debug </p>
 * <p>struts.osgi.runLevel: Defaults to "3". Run level to start the container.</p>
 */
public class FelixOsgiHost extends BaseOsgiHost {

    private static final Logger LOG = LogManager.getLogger(FelixOsgiHost.class);

    protected Felix felix;

    protected void startFelix() {
        //load properties from felix embedded file
        Properties configProps = getProperties("default.properties");

        // Copy framework properties from the system properties.
        Main.copySystemProperties(configProps);
        replaceSystemPackages(configProps);

        //struts, xwork and felix exported packages
        Properties strutsConfigProps = getProperties("struts-osgi.properties");
        addExportedPackages(strutsConfigProps, configProps);

        //find bundles and adde em to autostart property
        addAutoStartBundles(configProps);

        // Bundle cache
        String storageDir = System.getProperty("java.io.tmpdir") + ".felix-cache";
        configProps.setProperty(Constants.FRAMEWORK_STORAGE, storageDir);
        LOG.debug("Storing bundles at [{}]", storageDir);

        String cleanBundleCache = getServletContextParam("struts.osgi.clearBundleCache", "true");
        if ("true".equalsIgnoreCase(cleanBundleCache)) {
            LOG.debug("Clearing bundle cache");
            configProps.put(FelixConstants.FRAMEWORK_STORAGE_CLEAN, FelixConstants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);
        }

        //other properties
        configProps.put(FelixConstants.SERVICE_URLHANDLERS_PROP, "false");
        configProps.put(FelixConstants.LOG_LEVEL_PROP, getServletContextParam("struts.osgi.logLevel", "1"));
        configProps.put(FelixConstants.BUNDLE_CLASSPATH, ".");
        configProps.put(FelixConstants.FRAMEWORK_BEGINNING_STARTLEVEL, getServletContextParam("struts.osgi.runLevel", "3"));

        try {
            felix = new Felix(configProps);
            felix.init();
            AutoProcessor.process(configProps, felix.getBundleContext());
            felix.start();

            LOG.trace("Apache Felix is running");
        }
        catch (Exception ex) {
            throw new ConfigurationException("Couldn't start Apache Felix", ex);
        }

        addSpringOSGiSupport();

        //add the bundle context to the ServletContext
        servletContext.setAttribute(OSGI_BUNDLE_CONTEXT, felix.getBundleContext());
    }

    @Override
    public void init(ServletContext servletContext) {
        this.servletContext = servletContext;
        startFelix();
    }

    @Override
    public Map<String, Bundle> getBundles() {
        Map<String, Bundle> bundles = new HashMap<String, Bundle>();
        for (Bundle bundle : felix.getBundleContext().getBundles()) {
            bundles.put(bundle.getSymbolicName(), bundle);
        }

        return Collections.unmodifiableMap(bundles);
    }

    @Override
    public Map<String, Bundle> getActiveBundles() {
        Map<String, Bundle> bundles = new HashMap<String, Bundle>();
        for (Bundle bundle : felix.getBundleContext().getBundles()) {
            if (bundle.getState() == Bundle.ACTIVE) {
                bundles.put(bundle.getSymbolicName(), bundle);
            }
        }
        return Collections.unmodifiableMap(bundles);
    }

    @Override
    public BundleContext getBundleContext() {
        return felix.getBundleContext();
    }

    @Override
    public void destroy() throws Exception {
        felix.stop();
        LOG.trace("Apache Felix has stopped");
    }

    @Override
    protected void addSpringOSGiSupport() {
        // see the javadoc for org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext for more details
        // OsgiBundleXmlWebApplicationContext expects the the BundleContext to be set in the ServletContext under the attribute
        // OsgiBundleXmlWebApplicationContext.BUNDLE_CONTEXT_ATTRIBUTE
        try {
            Class clazz = Class.forName("org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext");
            String key = (String) clazz.getDeclaredField("BUNDLE_CONTEXT_ATTRIBUTE").get(null);
            servletContext.setAttribute(key, felix.getBundleContext());
        } catch (ClassNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Spring OSGi support is not enabled");
            }
        } catch (Exception e) {
            LOG.error("The API of Spring OSGi has changed and the field [{}] is no longer available. The OSGi plugin needs to be updated",
                        "org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext.BUNDLE_CONTEXT_ATTRIBUTE", e);
        }
    }
}
