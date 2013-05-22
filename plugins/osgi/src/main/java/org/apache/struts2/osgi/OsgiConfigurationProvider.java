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
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.PackageProvider;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.osgi.host.OsgiHost;
import org.apache.struts2.osgi.loaders.VelocityBundleResourceLoader;
import org.apache.struts2.views.velocity.VelocityManager;
import org.apache.velocity.app.Velocity;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Struts package provider that starts the OSGi container and deelgates package loading
 */
public class OsgiConfigurationProvider implements PackageProvider, BundleListener {

    private static final Logger LOG = LoggerFactory.getLogger(OsgiConfigurationProvider.class);

    private Configuration configuration;
    private ObjectFactory objectFactory;
    private FileManagerFactory fileManagerFactory;

    private OsgiHost osgiHost;
    private BundleContext bundleContext;
    private BundleAccessor bundleAccessor;
    private boolean bundlesChanged = false;
    private ServletContext servletContext;

    public void init(Configuration configuration) throws ConfigurationException {
        osgiHost = (OsgiHost) servletContext.getAttribute(StrutsOsgiListener.OSGI_HOST);
        bundleContext = osgiHost.getBundleContext();
        bundleAccessor.setBundleContext(bundleContext);
        bundleAccessor.setOsgiHost(osgiHost);
        this.configuration = configuration;

        //this class loader interface can be used by other plugins to lookup resources
        //from the bundles. A temporary class loader interface is set during other configuration
        //loading as well
        servletContext.setAttribute(ClassLoaderInterface.CLASS_LOADER_INTERFACE, new BundleClassLoaderInterface());
    }

    public synchronized void loadPackages() throws ConfigurationException {
        if (LOG.isTraceEnabled())
            LOG.trace("Loading packages from XML and Convention on startup");                

        //init action context
        ActionContext ctx = ActionContext.getContext();
        if (ctx == null) {
            ctx = createActionContext();
            ActionContext.setContext(ctx);
        }

        Set<String> bundleNames = new HashSet<String>();

        //iterate over the bundles and load packages from them
        for (Bundle bundle : osgiHost.getBundles().values()) {
            String bundleName = bundle.getSymbolicName();
            if (shouldProcessBundle(bundle) && !bundleNames.contains(bundleName)) {
                bundleNames.add(bundleName);
                //load XML and Convention config
                loadConfigFromBundle(bundle);
            }
        }

        bundlesChanged = false;
        bundleContext.addBundleListener(this);
    }

    protected ActionContext createActionContext() {
        return new ActionContext(new HashMap<String, Object>());
    }

    /**
     * Loads XML config as well as Convention config from a bundle
     * Limitation: Constants and Beans are ignored on XML config
     */
    protected void loadConfigFromBundle(Bundle bundle) {
        String bundleName = bundle.getSymbolicName();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Loading packages from bundle [#0]", bundleName);
        }

        //init action context
        ActionContext ctx = ActionContext.getContext();
        if (ctx == null) {
            ctx = createActionContext();
            ActionContext.setContext(ctx);
        }

        try {
            //the Convention plugin will use BundleClassLoaderInterface from the ActionContext to find resources
            //and load classes
            ctx.put(ClassLoaderInterface.CLASS_LOADER_INTERFACE, new BundleClassLoaderInterface());
            ctx.put(BundleAccessor.CURRENT_BUNDLE_NAME, bundleName);

            if (LOG.isTraceEnabled()) {
                LOG.trace("Loading XML config from bundle [#0]", bundleName);
            }

            //XML config
            PackageLoader loader = new BundlePackageLoader();
            for (PackageConfig pkg : loader.loadPackages(bundle, bundleContext, objectFactory, fileManagerFactory, configuration.getPackageConfigs())) {
                configuration.addPackageConfig(pkg.getName(), pkg);
                bundleAccessor.addPackageFromBundle(bundle, pkg.getName());
            }

            //Convention
            //get the existing packages before reloading the provider (se we can figure out what are the new packages)
            Set<String> packagesBeforeLoading = new HashSet<String>(configuration.getPackageConfigNames());

            PackageProvider conventionPackageProvider = configuration.getContainer().getInstance(PackageProvider.class, "convention.packageProvider");
            if (conventionPackageProvider != null) {
                if (LOG.isTraceEnabled())
                    LOG.trace("Loading Convention config from bundle [#0]", bundleName);
                conventionPackageProvider.loadPackages();
            }

            Set<String> packagesAfterLoading = new HashSet<String>(configuration.getPackageConfigNames());
            packagesAfterLoading.removeAll(packagesBeforeLoading);
            if (!packagesAfterLoading.isEmpty()) {
                //add the new packages to the map of bundle -> package
                for (String packageName : packagesAfterLoading)
                    bundleAccessor.addPackageFromBundle(bundle, packageName);
            }

            if (this.configuration.getRuntimeConfiguration() != null) {
                //if there is a runtime config, it meas that this method was called froma bundle start event
                //instead of the initial load, in that case, reload the config
                this.configuration.rebuildRuntimeConfiguration();
            }
        } finally {
            ctx.put(BundleAccessor.CURRENT_BUNDLE_NAME, null);
            ctx.put(ClassLoaderInterface.CLASS_LOADER_INTERFACE, null);
        }
    }

    /**
     * Checks for "Struts2-Enabled" header in the bundle
     */
    protected boolean shouldProcessBundle(Bundle bundle) {
        // Cast to String is required on JDK7
        String strutsEnabled = (String) bundle.getHeaders().get(OsgiHost.OSGI_HEADER_STRUTS_ENABLED);

        return "true".equalsIgnoreCase(strutsEnabled);
    }

    public synchronized boolean needsReload() {
        return bundlesChanged;
    }

    @Inject
    public void setObjectFactory(ObjectFactory factory) {
        this.objectFactory = factory;
    }

    @Inject
    public void setBundleAccessor(BundleAccessor acc) {
        this.bundleAccessor = acc;
    }

    @Inject
    public void setVelocityManager(VelocityManager vm) {
        Properties props = new Properties();
        props.setProperty("osgi.resource.loader.description", "OSGI bundle loader");
        props.setProperty("osgi.resource.loader.class", VelocityBundleResourceLoader.class.getName());
        props.setProperty(Velocity.RESOURCE_LOADER, "strutsfile,strutsclass,osgi");
        vm.setVelocityProperties(props);
    }

    @Inject
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Inject
    public void setFileManagerFactory(FileManagerFactory fmFactory) {
        this.fileManagerFactory = fmFactory;
    }

    public void destroy() {
        try {
            osgiHost.destroy();
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Failed to stop OSGi container", e);
            }
        }
    }

    /**
     * Listens to bundle event to load/unload config
     */
    public void bundleChanged(BundleEvent bundleEvent) {
        Bundle bundle = bundleEvent.getBundle();
        String bundleName = bundle.getSymbolicName();
        if (bundleName != null && shouldProcessBundle(bundle)) {
            switch (bundleEvent.getType()) {
                case BundleEvent.STARTED:
                    if (LOG.isTraceEnabled())
                        LOG.trace("The bundlde [#0] has been activated and will be scanned for struts configuration", bundleName);
                    loadConfigFromBundle(bundle);
                    break;
                case BundleEvent.STOPPED:
                    onBundleStopped(bundle);
                    break;
            }
        }
    }

    /**
     * This method is called when a bundle is stopped, so the config that is related to it is removed
     *
     * @param bundle the bundle that stopped
     */
    protected void onBundleStopped(Bundle bundle) {
        Set<String> packages = bundleAccessor.getPackagesByBundle(bundle);
        if (!packages.isEmpty()) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("The bundle [#0] has been stopped. The packages [#1] will be disabled", bundle.getSymbolicName(), StringUtils.join(packages, ","));
            }
            for (String packageName : packages) {
                configuration.removePackageConfig(packageName);
            }
        }
    }

}
