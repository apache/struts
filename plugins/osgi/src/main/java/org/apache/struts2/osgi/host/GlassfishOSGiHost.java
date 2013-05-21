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
package org.apache.struts2.osgi.host;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.felix.shell.ShellService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleReference;

import javax.servlet.ServletContext;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A glassfish implementation of an OsgiHost
 */
public class GlassfishOSGiHost extends BaseOsgiHost implements OsgiHost {

    private static final Logger LOG = LoggerFactory.getLogger(GlassfishOSGiHost.class);

    /**
     * Location inside the WAR where initial bundles are located.
     */
    private static final String BUNDLES_DIR = "/WEB-INF/classes/bundles/2/";

    private BundleContext bctx = null;

    @Override
    public void init(ServletContext servletContext) {
        this.servletContext = servletContext;

        installManagedBundles();

        addSpringOSGiSupport();

        // add the bundle context to the ServletContext
        servletContext.setAttribute(OSGI_BUNDLE_CONTEXT, bctx);
    }

    private void installManagedBundles() {
        try {

            // Obtaining BundleContext from ServletContext class which is loaded
            // by bundle class loader
            BundleReference ref = (BundleReference) servletContext.getClass()
                    .getClassLoader();
            bctx = ref.getBundle().getBundleContext();

            // installing managed bundles
            installBundles();

        } catch (Exception ex) {
            LOG.error("Installing Managed Bundles met a problem", ex);
        }
    }

    private void installBundles() throws Exception {
        ArrayList<Bundle> installed = new ArrayList<Bundle>();
        for (URL url : findBundles()) {
            LOG.debug("Installing bundle [" + url + "]");
            Bundle bundle = bctx.installBundle(url.toExternalForm());
            installed.add(bundle);
        }
        for (Bundle bundle : installed) {
            try {
                bundle.start();
            } catch (BundleException e) {
                e.printStackTrace();
                LOG.error("Failed to start " + bundle, e);
            }
        }

    }

    private List<URL> findBundles() throws Exception {
        ArrayList<URL> list = new ArrayList<URL>();
        for (Object o : this.servletContext.getResourcePaths(BUNDLES_DIR)) {
            String name = (String) o;
            if (name.endsWith(".jar")) {
                URL url = this.servletContext.getResource(name);
                if (url != null) {
                    list.add(url);
                }
            }
        }

        ProtectionDomain protectionDomain = ShellService.class.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URL loc = codeSource.getLocation();
        list.add(loc);

        return list;
    }

    @Override
    protected void addSpringOSGiSupport() {
        // see the javadoc for
        // org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext
        // for more details
        // OsgiBundleXmlWebApplicationContext expects the the BundleContext to
        // be set in the ServletContext under the attribute
        // OsgiBundleXmlWebApplicationContext.BUNDLE_CONTEXT_ATTRIBUTE
        try {
            Class<?> clazz = Class
                    .forName("org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext");
            String key = (String) clazz.getDeclaredField(
                    "BUNDLE_CONTEXT_ATTRIBUTE").get(null);
            servletContext.setAttribute(key, bctx);
        } catch (ClassNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Spring OSGi support is not enabled");
            }
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(
                        "The API of Spring OSGi has changed and the field [#0] is no longer available. The OSGi plugin needs to be updated",
                        e,
                        "org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext.BUNDLE_CONTEXT_ATTRIBUTE");
            }
        }
    }

    /**
     * This bundle map will not change, but the status of the bundles can change
     * over time. Use getActiveBundles() for active bundles
     */
    @Override
    public Map<String, Bundle> getBundles() {
        Map<String, Bundle> bundles = new HashMap<String, Bundle>();
        for (Bundle bundle : bctx.getBundles()) {
            bundles.put(bundle.getSymbolicName(), bundle);
        }

        return Collections.unmodifiableMap(bundles);
    }

    public Map<String, Bundle> getActiveBundles() {
        Map<String, Bundle> bundles = new HashMap<String, Bundle>();
        for (Bundle bundle : bctx.getBundles()) {
            if (bundle.getState() == Bundle.ACTIVE)
                bundles.put(bundle.getSymbolicName(), bundle);
        }

        return Collections.unmodifiableMap(bundles);
    }

    public BundleContext getBundleContext() {
        return bctx;
    }

    public void destroy() throws Exception {
    }

}