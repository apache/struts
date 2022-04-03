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
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
 * Struts package provider that starts the OSGi container and delegates package loading
 */
public class OsgiConfigurationProvider implements PackageProvider, BundleListener {

    private static final Logger LOG = LogManager.getLogger(OsgiConfigurationProvider.class);

    private Configuration configuration;
    private ObjectFactory objectFactory;
    private FileManagerFactory fileManagerFactory;

    private OsgiHost osgiHost;
    private BundleContext bundleContext;
    private BundleAccessor bundleAccessor;
    private boolean bundlesChanged = false;
    private ServletContext servletContext;

    @Override
    public void init(Configuration configuration) throws ConfigurationException {
        if (servletContext == null) {
            throw new IllegalStateException("ServletContext is null (may not have been injected).  Unable to initialize");  // Better than a NPE.
        }

        osgiHost = (OsgiHost) servletContext.getAttribute(StrutsOsgiListener.OSGI_HOST);

        if (osgiHost == null) {
            throw new IllegalStateException("Cannot locate Struts OsgiHost in servlet context (null).  Unable to initialize");  // Better than a NPE.
        }
        if (bundleAccessor == null) {
            throw new IllegalStateException("BundleAccessor is null (may not have been injected).  Unable to initialize");  // Better than a NPE.
        }

        bundleContext = osgiHost.getBundleContext();
        bundleAccessor.setBundleContext(bundleContext);
        bundleAccessor.setOsgiHost(osgiHost);
        this.configuration = configuration;

        if (bundleContext == null) {
            LOG.warn("Struts BundleContext is null at initialization, check OsgiHost configuration");
        }
        if (configuration == null) {
            LOG.warn("Struts OSGi configuration is null at initialization, check OsgiHost configuration");
        }

        if (bundleAccessor instanceof DefaultBundleAccessor) {
            LOG.debug("Struts OSGi Bundle Accessor is a DefaultBundleAccessor or descendant");
        } else {
            LOG.debug("Struts OSGi Bundle Accessor is a non-standard BundleAccessor");
        }

        //this class loader interface can be used by other plugins to lookup resources
        //from the bundles. A temporary class loader interface is set during other configuration
        //loading as well
        servletContext.setAttribute(ClassLoaderInterface.CLASS_LOADER_INTERFACE, new BundleClassLoaderInterface());
    }

    @Override
    public synchronized void loadPackages() throws ConfigurationException {
        LOG.trace("Loading packages from XML and Convention on startup");

        if (osgiHost == null) {
            throw new IllegalStateException("Cannot load packages, Struts OsgiHost is null");  // Better than a NPE.
        }
        if (bundleContext == null) {
            throw new IllegalStateException("Cannot load packages, Struts BundleContext is null");  // Better than a NPE.
        }

        //init action contect
        ActionContext ctx = ActionContext.getContext();
        if (ctx == null) {
            ctx = createActionContext();
        }

        Set<String> bundleNames = new HashSet<>();

        //iterate over the bundles and load packages from them
        osgiHost.getBundles().values().forEach(bundle -> {
            String bundleName = bundle.getSymbolicName();
            if (shouldProcessBundle(bundle) && !bundleNames.contains(bundleName)) {
                bundleNames.add(bundleName);
                //load XML and Convention config
                loadConfigFromBundle(bundle);
            }
        });

        bundlesChanged = false;
        bundleContext.addBundleListener(this);
    }

    /**
     * Creates a new empty ActionContext instance and binds it to the current thread.
     * 
     * @return 
     */
    protected ActionContext createActionContext() {
        return ActionContext.of(new HashMap<>()).bind();
    }

    /**
     * Loads XML config as well as Convention config from a bundle
     * Limitation: Constants and Beans are ignored on XML config
     *
     * @param bundle the bundle
     */
    protected void loadConfigFromBundle(Bundle bundle) {
        if (bundle == null) {
            throw new IllegalArgumentException("Cannot load configuration from a null bundle");  // Better than a NPE.
        }
        if (configuration == null) {
            throw new IllegalStateException("Struts OSGi configuration is null.  Cannot load bundle configuration");  // Better than a NPE.
        }
        if (bundleAccessor == null) {
            LOG.warn("BundleAccessor is null (may not have been injected).  May cause NPE to be thrown");
        }
        if (fileManagerFactory == null) {
            LOG.warn("FileManagerFactory is null, FileManagerFactory may not have been set.  May cause NPE to be thrown");
        }

        String bundleName = bundle.getSymbolicName();
        LOG.debug("Loading packages from bundle [{}]", bundleName);

        //init action context
        ActionContext ctx = ActionContext.getContext();
        if (ctx == null) {
            ctx = createActionContext();
        }

        try {
            //the Convention plugin will use BundleClassLoaderInterface from the ActionContext to find resources
            //and load classes
            ctx.put(ClassLoaderInterface.CLASS_LOADER_INTERFACE, new BundleClassLoaderInterface());
            ctx.put(BundleAccessor.CURRENT_BUNDLE_NAME, bundleName);

            LOG.trace("Loading XML config from bundle [{}]", bundleName);

            //XML config
            PackageLoader loader = new BundlePackageLoader();
            loader.loadPackages(bundle, bundleContext, objectFactory, fileManagerFactory, configuration.getPackageConfigs()).stream().map(pkg -> {
                configuration.addPackageConfig(pkg.getName(), pkg);
                return pkg;
            }).forEachOrdered(pkg -> {
                bundleAccessor.addPackageFromBundle(bundle, pkg.getName());
            });

            //Convention
            //get the existing packages before reloading the provider (se we can figure out what are the new packages)
            Set<String> packagesBeforeLoading = new HashSet<>(configuration.getPackageConfigNames());

            PackageProvider conventionPackageProvider = configuration.getContainer().getInstance(PackageProvider.class, "convention.packageProvider");
            if (conventionPackageProvider != null) {
                LOG.trace("Loading Convention config from bundle [{}]", bundleName);
                conventionPackageProvider.loadPackages();
            }

            Set<String> packagesAfterLoading = new HashSet<>(configuration.getPackageConfigNames());
            packagesAfterLoading.removeAll(packagesBeforeLoading);
            if (!packagesAfterLoading.isEmpty()) {
                //add the new packages to the map of bundle -> package
                packagesAfterLoading.forEach(packageName -> {
                    bundleAccessor.addPackageFromBundle(bundle, packageName);
                });
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
     *
     * @param bundle the bundle
     *
     * @return true is struts2 enabled
     */
    protected boolean shouldProcessBundle(Bundle bundle) {
        if (bundle == null) {
            throw new IllegalArgumentException("Cannot check if Struts OSGi Plugin should process a null bundle");  // Better than a NPE.
        }

        // Cast to String is required on JDK7
        String strutsEnabled = (String) bundle.getHeaders().get(OsgiHost.OSGI_HEADER_STRUTS_ENABLED);

        return "true".equalsIgnoreCase(strutsEnabled);
    }

    @Override
    public synchronized boolean needsReload() {
        return bundlesChanged;
    }

    @Inject
    public void setObjectFactory(ObjectFactory factory) {
        LOG.trace("OSGi ConfigurationProvider - setObjectFactory() called - ObjectFactory: [{}]", factory);

        this.objectFactory = factory;
    }

    @Inject
    public void setBundleAccessor(BundleAccessor acc) {
        LOG.trace("OSGi ConfigurationProvider - setBundleAccessor() called - BundleAccessor: [{}]", acc);

        this.bundleAccessor = acc;
    }

    @Inject
    public void setVelocityManager(VelocityManager vm) {
        LOG.trace("OSGi ConfigurationProvider - setVelocityManager() called - VelocityManager: [{}]", vm);

        Properties props = new Properties();
        props.setProperty("osgi.resource.loader.description", "OSGI bundle loader");
        props.setProperty("osgi.resource.loader.class", VelocityBundleResourceLoader.class.getName());
        props.setProperty(Velocity.RESOURCE_LOADER, "strutsfile,strutsclass,osgi");
        vm.setVelocityProperties(props);
    }

    @Inject
    public void setServletContext(ServletContext servletContext) {
        LOG.trace("OSGi ConfigurationProvider - setServletContext() called - ServletContext: [{}]", servletContext);

        this.servletContext = servletContext;
    }

    @Inject
    public void setFileManagerFactory(FileManagerFactory fmFactory) {
        LOG.trace("OSGi ConfigurationProvider - setFileManagerFactory() called - FileManagerFactory: [{}]", fmFactory);

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
     *
     * @param bundleEvent the bundle event
     */
    @Override
    public void bundleChanged(BundleEvent bundleEvent) {
        if (bundleEvent == null) {
            throw new IllegalArgumentException("Cannot check if bundle changed for a null BundleEvent");  // Better than a NPE.
        }

        Bundle bundle = bundleEvent.getBundle();

        if (bundle == null) {
            throw new IllegalArgumentException("Cannot check if bundle changed for a null Bundle within the BundleEvent");  // Better than a NPE.
        }

        String bundleName = bundle.getSymbolicName();
        if (bundleName != null && shouldProcessBundle(bundle)) {
            switch (bundleEvent.getType()) {
                case BundleEvent.STARTED:
                    LOG.trace("The bundle [{}] has been activated and will be scanned for struts configuration", bundleName);
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
        if (bundle == null) {
            throw new IllegalArgumentException("Cannot process bundle stopped event on a null Bundle");  // Better than a NPE.
        }
        if (bundleAccessor == null) {
            throw new IllegalStateException("BundleAccessor is null.  Cannot process bundle stopped event");  // Better than a NPE.
        }

        Set<String> packages = bundleAccessor.getPackagesByBundle(bundle);
        if (!packages.isEmpty()) {
            if (LOG.isTraceEnabled()) {  // Avoid packages join cost, except when trace level logging is enabled.
                LOG.trace("The bundle [{}] has been stopped. The packages [{}] will be disabled", bundle.getSymbolicName(), StringUtils.join(packages, ","));
            }
            packages.forEach(packageName -> {
                configuration.removePackageConfig(packageName);
            });
        }
    }

}
