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

package org.apache.struts2.osgi;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.osgi.host.OsgiHost;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper class that find resources and loads classes from the list of bundles
 */
public class DefaultBundleAccessor implements BundleAccessor {

    private static DefaultBundleAccessor self;
    private static final Logger LOG = LoggerFactory.getLogger(DefaultBundleAccessor.class);

    private BundleContext bundleContext;
    private Map<String, String> packageToBundle = new HashMap<String, String>();
    private Map<Bundle, Set<String>> packagesByBundle = new HashMap<Bundle, Set<String>>();
    private OsgiHost osgiHost;

    public DefaultBundleAccessor() {
        self = this;
    }

    public static DefaultBundleAccessor getInstance() {
        return self;
    }

    public Object getService(ServiceReference ref) {
        return bundleContext != null ? bundleContext.getService(ref) : null;
    }

    public ServiceReference getServiceReference(String className) {
        return bundleContext != null ? bundleContext.getServiceReference(className) : null;
    }

    public ServiceReference[] getAllServiceReferences(String className) {
        if (bundleContext != null) {
            try {
                return bundleContext.getServiceReferences(className, null);
            } catch (InvalidSyntaxException e) {
                //cannot happen we are passing null as the param
                if (LOG.isErrorEnabled())
                    LOG.error("Invalid syntax for service lookup", e);
            }
        }

        return null;
    }

    public ServiceReference[] getServiceReferences(String className, String params) throws InvalidSyntaxException {
        return bundleContext != null ? bundleContext.getServiceReferences(className, params) : null;
    }

    /**
     *  Add as Bundle -> Package mapping 
     * @param bundle the bundle where the package was loaded from
     * @param packageName the anme of the loaded package
     */
    public void addPackageFromBundle(Bundle bundle, String packageName) {
        this.packageToBundle.put(packageName, bundle.getSymbolicName());
        Set<String> pkgs = packagesByBundle.get(bundle);
        if (pkgs == null) {
            pkgs = new HashSet<String>();
            packagesByBundle.put(bundle, pkgs);
        }
        pkgs.add(packageName);
    }

    public Class<?> loadClass(String className) throws ClassNotFoundException {
        Bundle bundle = getCurrentBundle();
        if (bundle != null) {
            Class cls = bundle.loadClass(className);
            if (LOG.isTraceEnabled())
                LOG.trace("Located class [#0] in bundle [#1]", className, bundle.getSymbolicName());
            return cls;
        }

        throw new ClassNotFoundException("Unable to find class " + className);
    }

    private Bundle getCurrentBundle() {
        ActionContext ctx = ActionContext.getContext();
        String bundleName = (String) ctx.get(CURRENT_BUNDLE_NAME);
        if (bundleName == null) {
            ActionInvocation inv = ctx.getActionInvocation();
            ActionProxy proxy = inv.getProxy();
            ActionConfig actionConfig = proxy.getConfig();
            bundleName = packageToBundle.get(actionConfig.getPackageName());
        }
        if (bundleName != null) {
            return osgiHost.getActiveBundles().get(bundleName);
        }
        return null;
    }

    public List<URL> loadResources(String name) throws IOException {
        return loadResources(name, false);
    }

    public List<URL> loadResources(String name, boolean translate) throws IOException {
        Bundle bundle = getCurrentBundle();
        if (bundle != null) {
            List<URL> resources = new ArrayList<URL>();
            Enumeration e = bundle.getResources(name);
            if (e != null) {
                while (e.hasMoreElements()) {
                    resources.add(translate ? OsgiUtil.translateBundleURLToJarURL((URL) e.nextElement(), getCurrentBundle()) : (URL) e.nextElement());
                }
            }
            return resources;
        }

        return null;
    }

    public URL loadResourceFromAllBundles(String name) throws IOException {
        for (Map.Entry<String, Bundle> entry : osgiHost.getActiveBundles().entrySet()) {
            Enumeration e = entry.getValue().getResources(name);
            if (e != null && e.hasMoreElements()) {
                return (URL) e.nextElement();
            }
        }

        return null;
    }

    public InputStream loadResourceFromAllBundlesAsStream(String name) throws IOException {
        URL url = loadResourceFromAllBundles(name);
        if (url != null) {
            return url.openStream();
        }
        return null;
    }

    public URL loadResource(String name) {
        return loadResource(name, false);
    }

    public URL loadResource(String name, boolean translate) {
        Bundle bundle = getCurrentBundle();
        if (bundle != null) {
            URL url = bundle.getResource(name);
            try {
                return translate ? OsgiUtil.translateBundleURLToJarURL(url, getCurrentBundle()) : url;
            } catch (Exception e) {
                if (LOG.isErrorEnabled()) {
                    LOG.error("Unable to translate bundle URL to jar URL", e);
                }

                return null;
            }
        }

        return null;
    }

    public Set<String> getPackagesByBundle(Bundle bundle) {
        return packagesByBundle.get(bundle);
    }

    public InputStream loadResourceAsStream(String name) throws IOException {
        URL url = loadResource(name);
        if (url != null) {
            return url.openStream();
        }
        return null;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void setOsgiHost(OsgiHost osgiHost) {
        this.osgiHost = osgiHost;
    }

}
