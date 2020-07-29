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
import org.apache.struts2.osgi.OsgiUtil;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.felix.main.AutoProcessor;
import org.apache.felix.main.Main;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Apache felix implementation of an OsgiHost
 * See http://felix.apache.org/site/apache-felix-framework-launching-and-embedding.html
 * <br>
 * Servlet config params:
 * <p>struts.osgi.clearBundleCache: Defaults to "true" delete installed bundles when the container starts</p>
 * <p>struts.osgi.logLevel: Defaults to "1". Felix log level. 1 = error, 2 = warning, 3 = information, and 4 = debug </p>
 * <p>struts.osgi.runLevel: Defaults to "3". Run level to start the container.</p>
 * <p>struts.osgi.felixCacheLocking: Defaults to "true". Set to true to enable Felix cache locking, false to disable it.</p>
 * <p>struts.osgi.searchForPropertiesFilesInRelativePath: Defaults to "false".  Set to "true" for fallback search for properties files in relative path (e.g. for unit testing).</p>
 * <p>struts.osgi.felixPropertiesPath: Defaults to "default.properties". Path to Felix properties (prefix "/" will be prepended).</p>
 * <p>struts.osgi.strutsOSGiPropertiesPath: Defaults to "struts-osgi.properties". Path to Struts OSGi properties (prefix "/" will be prepended).</p>
 */
public class FelixOsgiHost extends BaseOsgiHost {

    private static final Logger LOG = LogManager.getLogger(FelixOsgiHost.class);

    protected static final String FELIX_JRE_DETECT_JAVA_SPECIFICATION_VERSION = "${jre-${felix.detect.java.specification.version}}";
    protected static final String FELIX_JRE_DETECT_JPMS = "${jre-${felix.detect.jpms}}";
    protected static final String FELIX_DETECT_JAVA_VERSION = "${felix.detect.java.version}";
    protected static final String FELIX_JRE_BASE_PREFIX = "${jre-base";
    protected static final String FELIX_JRE_BASE_SUFFIX = "}";
    protected static final String OSGI_FRAMEWORK_SYSTEM_CAPABILITIES = "org.osgi.framework.system.capabilities";
    protected static final String OSGI_FRAMEWORK_EXECUTIONENVIRONMENT = "org.osgi.framework.executionenvironment";
    protected static final String FELIX_SERVICE_CAPS_KEY = "felix.service.caps";
    protected static final String FELIX_SERVICE_CAPS = "${" + FELIX_SERVICE_CAPS_KEY + "}";
    protected static final String FELIX_EE_CAP_JAVA_SPECIFICATION_VERSION = "${eecap-${java.specification.version}}";
    protected static final String FELIX_EE_CAP_DETECT_JPMS = "${eecap-${felix.detect.jpms}}";
    protected static final String FELIX_EE_JAVA_SPECIFICATION_VERSION = "${ee-${java.specification.version}}";
    protected static final String FELIX_EE_FELIX_DETECT_JPMS = "${ee-${felix.detect.jpms}}";

    protected Felix felix;

    protected void startFelix() {
        // Retrieve Felix properties path from ServletContext (default to "default.properties" if not present).
        String felixPropertiesPath = getServletContextParam("struts.osgi.felixPropertiesPath", "default.properties");
        felixPropertiesPath = felixPropertiesPath.trim();
        if (felixPropertiesPath.startsWith("/")) {
            felixPropertiesPath = felixPropertiesPath.substring(1);  // Must strip leading "/" if present.
        }

        // Load properties from felix embedded file or value specified in servlet init parameter "struts.osgi.felixPropertiesPath".
        final Properties configProps = getProperties(felixPropertiesPath);

        // Copy framework properties from the system properties.
        Main.copySystemProperties(configProps);
        replaceSystemPackages(configProps);

        LOG.debug("Felix framework system capabilities: [{}]", configProps.getProperty(Constants.FRAMEWORK_SYSTEMCAPABILITIES));
        LOG.debug("Felix framework execution environment: [{}]", configProps.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));
        LOG.debug("Felix service caps: [{}]", configProps.getProperty(FELIX_SERVICE_CAPS_KEY));

        replaceFelixSystemPackages(configProps);
        replaceFelixFrameworkSystemCapabilities(configProps);
        replaceFelixExecutionEnvironment(configProps);

        // Retrieve Struts OSGi properties path from ServletContext (default to "struts-osgi.properties" if not present).
        String strutsOSGiPropertiesPath = getServletContextParam("struts.osgi.strutsOSGiPropertiesPath", "struts-osgi.properties");
        strutsOSGiPropertiesPath = strutsOSGiPropertiesPath.trim();
        if (strutsOSGiPropertiesPath.startsWith("/")) {
            strutsOSGiPropertiesPath = strutsOSGiPropertiesPath.substring(1);  // Must strip leading "/" if present.
        }

        LOG.trace("FelixOSGiHost: Before addExportedPackages");

        //struts, xwork and felix exported packages
        final Properties strutsConfigProps = getProperties(strutsOSGiPropertiesPath);

        try {
            addExportedPackages(strutsConfigProps, configProps);
        } catch (Throwable t) {
            throw new ConfigurationException("FelixOSGiHost couldn't start Apache Felix", t);
        }

        LOG.trace("FelixOSGiHost: After addExportedPackages, before addAutoStartBundles");

        //find bundles and adde em to autostart property
        try {
            addAutoStartBundles(configProps);
        } catch (Throwable t) {
            throw new ConfigurationException("FelixOSGiHost couldn't start Apache Felix", t);
        }

        LOG.trace("FelixOSGiHost: After addAutoStartBundles. before bundle cache processing");

        // Bundle cache
        String storageDir = configProps.getProperty(Constants.FRAMEWORK_STORAGE);
        if (storageDir == null || storageDir.isEmpty()) {
            String javaTmpDir = System.getProperty("java.io.tmpdir");
            if (javaTmpDir == null || javaTmpDir.isEmpty()) {
                LOG.warn("Felix environment 'java.io.tmpdir': [{}], and 'org.osgi.framework.storage': [{}].  Felix bundle cache will be created at the root directory (probable failure)", javaTmpDir, storageDir);
                javaTmpDir = File.separator;
            }
            if (javaTmpDir.endsWith(File.separator)) {
                 storageDir = javaTmpDir + ".felix-cache";
            } else {
                storageDir = javaTmpDir + File.separator + ".felix-cache";
            }
            configProps.setProperty(Constants.FRAMEWORK_STORAGE, storageDir);
        }

        LOG.debug("Storing bundles at [{}]", storageDir);

        String cleanBundleCache = getServletContextParam("struts.osgi.clearBundleCache", "true");
        if ("true".equalsIgnoreCase(cleanBundleCache)) {
            LOG.debug("Clearing bundle cache");
            configProps.put(FelixConstants.FRAMEWORK_STORAGE_CLEAN, FelixConstants.FRAMEWORK_STORAGE_CLEAN_ONFIRSTINIT);
        }

        String cacheLocking = getServletContextParam("struts.osgi.felixCacheLocking", "true");
        configProps.put("felix.cache.locking", cacheLocking);

        LOG.debug("Felix bundle cache locking: [{}]", cacheLocking);
        LOG.trace("FelixOSGiHost: After bundle cache processing, before configProps and init");

        //other properties
        configProps.put(FelixConstants.SERVICE_URLHANDLERS_PROP, "false");
        configProps.put(FelixConstants.LOG_LEVEL_PROP, getServletContextParam("struts.osgi.logLevel", "1"));
        configProps.put(FelixConstants.BUNDLE_CLASSPATH, ".");
        configProps.put(FelixConstants.FRAMEWORK_BEGINNING_STARTLEVEL, getServletContextParam("struts.osgi.runLevel", "3"));

        try {
            felix = new Felix(configProps);

            LOG.trace("FelixOSGiHost: After Felix construct. before init");

            felix.init();

            LOG.trace("FelixOSGiHost: After Felix init. before AutoProcessor process");

            AutoProcessor.process(configProps, felix.getBundleContext());

            LOG.trace("FelixOSGiHost: After Felix AutoProcessor process, before start");

            felix.start();

            LOG.trace("FelixOSGiHost: After Felix start");

            // Start detailed service/bundle state for debugging.
            if (LOG.isDebugEnabled()) {
                ServiceReference[] serviceReferences = felix.getRegisteredServices();
                LOG.debug("Felix registered service references: [{}]", Arrays.toString(serviceReferences));

                serviceReferences = felix.getServicesInUse();
                LOG.debug("Felix in-use service references: [{}]", Arrays.toString(serviceReferences));

                Map<String, Bundle> bundleMap = this.getBundles();
                LOG.debug("Felix bundle map has size: [{}]", bundleMap.size());

                Set<String> bundleKeys = bundleMap.keySet();
                Iterator<String> keyIterator = bundleKeys.iterator();
                while (keyIterator.hasNext()) {
                    String currentKey = keyIterator.next();
                    Bundle currentBundle = bundleMap.get(currentKey);
                    if (currentBundle != null) {
                        ServiceReference[] bundleRegisteredServices = currentBundle.getRegisteredServices();
                        ServiceReference[] bundleServicesInUse = currentBundle.getServicesInUse();
                        LOG.debug("    Key: [{}], SymbolicName: [{}], Location: [{}], BundleID: [{}], State: [{}]",
                                  currentKey, currentBundle.getSymbolicName(), currentBundle.getLocation(), currentBundle.getBundleId(), currentBundle.getState());
                        LOG.debug("        Bundle Registered Services: [{}]", Arrays.toString(bundleRegisteredServices));
                        LOG.debug("        Bundle Services In Use: [{}]", Arrays.toString(bundleServicesInUse));
                    } else {
                        LOG.debug("    Key: [{}] returned a null bundle", currentKey);
                    }
                }

                Bundle[] bundles = this.getBundleContext().getBundles();
                if (bundles != null) {
                    LOG.debug("BundleContext bundle array has size: [{}]", bundles.length);
                    for (int index = 0; index < bundles.length; index++) {
                        Bundle currentBundle = bundles[index];
                        if (currentBundle != null) {
                            LOG.debug("    Bundle [{}], SymbolicName: [{}], Location: [{}], BundleID: [{}], State: [{}]",
                                     index, currentBundle.getSymbolicName(), currentBundle.getLocation(), currentBundle.getBundleId(), currentBundle.getState());
                        } else {
                            LOG.debug("    Bundle [{}] is null", index);
                        }
                    }
                } else {
                    LOG.debug("Default Bundle Context bundle array is null");
                }
            }
            // End detailed service/bundle state for debugging.

            LOG.trace("Apache Felix is running");
        }
        catch (Throwable t) {
            throw new ConfigurationException("FelixOSGiHost couldn't start Apache Felix", t);
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
        if (felix == null) {
            throw new IllegalStateException("Felix reference is null (never started or initialized)");  // Clearer than a NPE.
        }
        Map<String, Bundle> bundles = new HashMap<>();
        for (Bundle bundle : felix.getBundleContext().getBundles()) {
            bundles.put(bundle.getSymbolicName(), bundle);
        }

        return Collections.unmodifiableMap(bundles);
    }

    @Override
    public Map<String, Bundle> getActiveBundles() {
        if (felix == null) {
            throw new IllegalStateException("Felix reference is null (never started or initialized)");  // Clearer than a NPE.
        }
        Map<String, Bundle> bundles = new HashMap<>();
        for (Bundle bundle : felix.getBundleContext().getBundles()) {
            if (bundle.getState() == Bundle.ACTIVE) {
                bundles.put(bundle.getSymbolicName(), bundle);
            }
        }
        return Collections.unmodifiableMap(bundles);
    }

    @Override
    public BundleContext getBundleContext() {
        if (felix == null) {
            throw new IllegalStateException("Felix reference is null (never started or initialized)");  // Clearer than a NPE.
        }
        return felix.getBundleContext();
    }

    @Override
    public void destroy() throws Exception {
        if (felix == null) {
            throw new IllegalStateException("Felix reference is null (never started or initialized)");  // Clearer than a NPE.
        }

        try {
            felix.stop();
            LOG.trace("Apache Felix has stopped");
        }
        catch (Throwable t) {

            LOG.error("FelixOSGiHost stop failure", t);

            throw t;  // Re-throw.
        }
    }

    @Override
    protected void addSpringOSGiSupport() {
        // see the javadoc for org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext for more details
        // OsgiBundleXmlWebApplicationContext expects the the BundleContext to be set in the ServletContext under the attribute
        // OsgiBundleXmlWebApplicationContext.BUNDLE_CONTEXT_ATTRIBUTE
        try {
            Class clazz = Class.forName("org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext");
            String key = (String) clazz.getDeclaredField("BUNDLE_CONTEXT_ATTRIBUTE").get(null);
            if (felix == null) {
                throw new IllegalStateException("Felix reference is null (never started or initialized)");  // Clearer than a NPE.
            }
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

    /**
     * Replace all instances of {@link FELIX_JRE_DETECT_JAVA_SPECIFICATION_VERSION} and {@link FELIX_JRE_DETECT_JPMS},
     * within the {@link Constants.FRAMEWORK_SYSTEMPACKAGES} property of the provided properties.  The replacement will be the value
     * for the key "jre-x.y" in the properties parameter (where x.y is the JRE version after transforming the System "java.version" property.
     * For example: "jre-x.y" is "jre-1.8" for Java 8, "jre-9.0" for Java 9, and "jre-11.0" for Java 11.
     * 
     * While performing the replacement, the elements within jre-x.y will also undergo a replacement of the substring {@link FELIX_DETECT_JAVA_VERSION}
     * into something like "0.0.0.JavaSE_001_008" for Java 8 and earlier, "0.0.0.JavaSE_009" for Java 9 and newer.  If you prefer
     * manual control, use literal strings rather than {@link FELIX_DETECT_JAVA_VERSION} in your properties file.
     * 
     * @param properties OSGi properties for which the {@link Constants.FRAMEWORK_SYSTEMPACKAGES} property's values
     *                   substrings {@link FELIX_JRE_DETECT_JAVA_SPECIFICATION_VERSION} and {@link FELIX_JRE_DETECT_JPMS} will be replaced by the value for the key
     *                   "jre-xxx" (where xxx is the JRE version).
     *                   If no {@link Constants.FRAMEWORK_SYSTEMPACKAGES} property exists, this is a no-op.
     */
    protected void replaceFelixSystemPackages(Properties properties) {
        //Felix has a way to load the config file and substitution expressions
        //but the method does not have a way to specify the file (other than in an env variable)

        if (properties == null) {
            throw new IllegalArgumentException("Cannot replace Felix system packages given a null properties reference");
        }

        //${jre-${java.specification.version}}
        String systemPackages = (String) properties.get(Constants.FRAMEWORK_SYSTEMPACKAGES);
        if (systemPackages != null && !systemPackages.isEmpty()) {
            LOG.debug("OSGi System Packages (before replacement): [{}]", systemPackages);

            final String systemJavaVersion = System.getProperty("java.version");
            if (systemJavaVersion != null && !systemJavaVersion.isEmpty()) {
                final String jreJavaSpecificationVersion = "jre-" + OsgiUtil.generateJavaVersionForSystemPackages(systemJavaVersion);
                LOG.debug("  System java.version: [{}], generated Java Specification Version: [{}]", systemJavaVersion, jreJavaSpecificationVersion);
                String jreJavaSpecificationVersionSubstitution = (String) properties.get(jreJavaSpecificationVersion);
                if (jreJavaSpecificationVersionSubstitution != null && !jreJavaSpecificationVersionSubstitution.isEmpty()) {
                    //${detect.java.version}
                    jreJavaSpecificationVersionSubstitution = expandAllFelixJREBaseElements(jreJavaSpecificationVersionSubstitution, properties);
                    final boolean constainsFelix_JRE_Java_Specification_Version = systemPackages.contains(FELIX_JRE_DETECT_JAVA_SPECIFICATION_VERSION);
                    final boolean constainsFelix_JRE_JPMS = systemPackages.contains(FELIX_JRE_DETECT_JPMS);
                    jreJavaSpecificationVersionSubstitution = jreJavaSpecificationVersionSubstitution.replace(FELIX_DETECT_JAVA_VERSION, OsgiUtil.generateJava_SE_SystemPackageVersionString(systemJavaVersion)).trim();
                    if (constainsFelix_JRE_Java_Specification_Version && constainsFelix_JRE_JPMS) {
                        systemPackages = systemPackages.replace(FELIX_JRE_DETECT_JAVA_SPECIFICATION_VERSION, jreJavaSpecificationVersionSubstitution);
                        systemPackages = systemPackages.replace(FELIX_JRE_DETECT_JPMS, "").trim();  // Only replace one instance when both set (avoid duplicates).
                    } else if (constainsFelix_JRE_Java_Specification_Version) {
                        systemPackages = systemPackages.replace(FELIX_JRE_DETECT_JAVA_SPECIFICATION_VERSION, jreJavaSpecificationVersionSubstitution);
                    } else if (constainsFelix_JRE_JPMS) {
                        systemPackages = systemPackages.replace(FELIX_JRE_DETECT_JPMS, jreJavaSpecificationVersionSubstitution);
                    } else {
                        LOG.warn("Properties property [{}] is present, but we are unable to replace Felix JRE system packages due to missing at least one of [{}] [{}] keys in the configuration.",
                                 jreJavaSpecificationVersion, FELIX_JRE_DETECT_JAVA_SPECIFICATION_VERSION, FELIX_JRE_DETECT_JPMS);
                    }
                    properties.put(Constants.FRAMEWORK_SYSTEMPACKAGES, systemPackages);
                } else {
                    LOG.warn("Properties property [{}] is null or empty.  Unable to replace Felix JRE system packages.  Felix JRE system packages will be cleared.", jreJavaSpecificationVersion);
                    systemPackages = systemPackages.replace(FELIX_JRE_DETECT_JAVA_SPECIFICATION_VERSION, "");
                    systemPackages = systemPackages.replace(FELIX_JRE_DETECT_JPMS, "").trim();
                    properties.put(Constants.FRAMEWORK_SYSTEMPACKAGES, systemPackages);
                }
            } else {
                LOG.warn("System property [{}] is null or empty.  Unable to replace Felix JRE system packages.", "java.version");
            }
            LOG.debug("OSGi System Packages (after replacement): [{}]", systemPackages);
        } else {
            LOG.warn("Unable to replace Felix JRE system packages.  Properties required key [{}] is missing or empty.",
                     Constants.FRAMEWORK_SYSTEMPACKAGES);
        }
    }

    /**
     * Replace all instances of the {@link FELIX_SERVICE_CAPS}, {@link FELIX_EE_CAP_JAVA_SPECIFICATION_VERSION} and {@link FELIX_EE_CAP_DETECT_JPMS}
     * within the {@link Constants.FRAMEWORK_SYSTEMCAPABILITIES} property of the provided properties.  The replacements will be based on the lookups within
     * the properties for the keys that match the substitution strings for each element (i.e. the element without "${}", transformed for JRE version if required).
     * 
     * @param properties OSGi properties for which the {@link Constants.FRAMEWORK_SYSTEMCAPABILITIES} property's values substrings
     *                   {@link FELIX_SERVICE_CAPS}, {@link FELIX_EE_CAP_JAVA_SPECIFICATION_VERSION} and {@link FELIX_EE_CAP_DETECT_JPMS} will be replaced by the
     *                   value for the keys that match the substitution strings for each element (i.e. the element without "${}", transformed for JRE version if required).
     *                   If no {@link Constants.FRAMEWORK_SYSTEMCAPABILITIES} property exists, this is a no-op.
     */
    protected void replaceFelixFrameworkSystemCapabilities(Properties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("Cannot replace Felix system capabilities given a null properties reference");
        }

        String systemCapabilities = (String) properties.get(Constants.FRAMEWORK_SYSTEMCAPABILITIES);
        if (systemCapabilities != null && !systemCapabilities.isEmpty()) {
            LOG.debug("OSGi System Capabilities (before replacement): [{}]", systemCapabilities);

            systemCapabilities = expandAllOsgiFrameworkSystemCapabilities(systemCapabilities, properties);
            properties.put(Constants.FRAMEWORK_SYSTEMCAPABILITIES, systemCapabilities);

            LOG.debug("OSGi System Capabilities (after replacement): [{}]", systemCapabilities);
        } else {
            LOG.warn("Unable to replace Felix system capabilities.  Properties required key [{}] is missing or empty",
                     Constants.FRAMEWORK_SYSTEMCAPABILITIES);
        }
    }

    /**
     * Replace all instances of the {@link FELIX_EE_JAVA_SPECIFICATION_VERSION} and {@link FELIX_EE_FELIX_DETECT_JPMS}
     * within the {@link Constants.FRAMEWORK_EXECUTIONENVIRONMENT} property of the provided properties.  The replacements will be based on the lookups within
     * the properties for the keys that match the substitution strings for each element (i.e. the element without "${}", transformed for JRE version if required).
     * 
     * @param properties OSGi properties for which the {@link Constants.FRAMEWORK_EXECUTIONENVIRONMENT} property's values substrings
     *                   {@link FELIX_EE_JAVA_SPECIFICATION_VERSION} and {@link FELIX_EE_FELIX_DETECT_JPMS} will be replaced by the value
     *                   for the keys that match the substitution strings for each element (i.e. the element without "${}", transformed for JRE version if required).
     *                   If no {@link Constants.FRAMEWORK_EXECUTIONENVIRONMENT} property exists, this is a no-op.
     */
    protected void replaceFelixExecutionEnvironment(Properties properties) {

        if (properties == null) {
            throw new IllegalArgumentException("Cannot replace Felix execution environment given a null properties reference");
        }

        String executionEnvironment = (String) properties.get(Constants.FRAMEWORK_EXECUTIONENVIRONMENT);
        if (executionEnvironment != null && !executionEnvironment.isEmpty()) {
            LOG.debug("OSGi Execution Environment (before replacement): [{}]", executionEnvironment);

            executionEnvironment = expandAllOsgiFrameworkExecutionEnvironments(executionEnvironment, properties);
            properties.put(Constants.FRAMEWORK_EXECUTIONENVIRONMENT, executionEnvironment);

            LOG.debug("OSGi Execution Environment (after replacement): [{}]", executionEnvironment);
        } else {
            LOG.warn("Unable to replace Felix execution environment.  Properties required key [{}] is missing or empty",
                     Constants.FRAMEWORK_EXECUTIONENVIRONMENT);
        }
    }

    /**
     * Recursive method to expand all Felix JRE base elements from the configuration.  Once complete the resulting string
     * should be the expansion of all ${jre-base} elements into a single string incorporating all the base elements.
     * 
     * @param jreProperty  A string retrieval of a JRE property entry (e.g. jre-1.7), that may or may not contain JRE base elements (e.g. ${jre-base-1.7}).
     * @param properties   The Felix properties.
     * @return  A fully-expanded version of jreProperty, after recursively expanding all jre-base elements in the Felix properties.
     */
    protected String expandAllFelixJREBaseElements(String jreProperty, Properties properties) {
        String result = jreProperty;

        if (properties == null) {
            throw new IllegalArgumentException("Cannot expand Felix JRE base elements given a null properties reference");
        }

        if (jreProperty != null && !jreProperty.isEmpty()) {
            final int nextBaseStart = jreProperty.indexOf(FELIX_JRE_BASE_PREFIX);
            final int nextBaseEnd = (nextBaseStart != -1 ? jreProperty.indexOf(FELIX_JRE_BASE_SUFFIX, nextBaseStart + FELIX_JRE_BASE_PREFIX.length() ) : -1);
            if (nextBaseStart != -1 && nextBaseEnd != -1) {
                final String jreBaseReplace = jreProperty.substring(nextBaseStart, nextBaseEnd + 1);    // Full token including "${}".
                final String jreBaseLookup = jreBaseReplace.substring(2, jreBaseReplace.length() - 1);  // Drop surrounding "${}" for lookup.
                String jreBaseSubstitution = (String) properties.get(jreBaseLookup);
                LOG.trace("    JRE Base elements - Result so far [{}], current replace[{}], current lookup [{}], current substitution [{}]", jreProperty, jreBaseReplace, jreBaseLookup, jreBaseSubstitution);
                if (jreBaseSubstitution == null || jreBaseSubstitution.isEmpty()) {
                    LOG.warn("Unable to expand Felix JRE base property [{}] as it is missing or empty.  Replacing with empty string", jreBaseLookup);
                    jreBaseSubstitution = "";
                } else {
                    jreBaseSubstitution = jreBaseSubstitution.trim();
                }
                result = expandAllFelixJREBaseElements(jreProperty.replace(jreBaseReplace, jreBaseSubstitution), properties);  // Recurse until all possible Felix JRE base elements have been replaced.
            }
        } else {
            LOG.warn("Unable to expand Felix JRE base elements.  Base property [{}] is missing or empty", jreProperty);
        }
        return result;
    }

    /**
     * Method to expand all OSGi framework system capabilities elements from the configuration.  Once complete the resulting string
     * should be the expansion of all entries found in the {@link Constants.FRAMEWORK_SYSTEMCAPABILITIES} content passed into the method.
     * 
     * @param capabilities  A string retrieval of an OSGi framework system capabilities property entry {@link Constants.FRAMEWORK_SYSTEMCAPABILITIES} containing one or more of
     *                     {@link FELIX_SERVICE_CAPS} {@link FELIX_EE_CAP_JAVA_SPECIFICATION_VERSION} {@link FELIX_EE_CAP_DETECT_JPMS}.
     * @param properties   The Felix properties.
     * @return  A fully-expanded version of the {@link Constants.FRAMEWORK_SYSTEMCAPABILITIES} content, after expanding all its contained elements in the Felix properties.
     */
    protected String expandAllOsgiFrameworkSystemCapabilities(String capabilities, Properties properties) {
        String result = capabilities;

        if (properties == null) {
            throw new IllegalArgumentException("Cannot expand OSGi framework system capabilities elements given a null properties reference");
        }

        if (capabilities != null && !capabilities.isEmpty()) {
            final String systemJavaVersion = System.getProperty("java.version");
            String eeCapJavaSpecificationVersion = null;
            String eeCapJavaSpecificationVersionSubstitution = null;
            final boolean constains_Felix_Service_Caps = capabilities.contains(FELIX_SERVICE_CAPS);
            final boolean constains_Felix_EE_Cap_Java_Specification_Version = capabilities.contains(FELIX_EE_CAP_JAVA_SPECIFICATION_VERSION);
            final boolean constains_Felix_EE_Cap_Detect_JPMS = capabilities.contains(FELIX_EE_CAP_DETECT_JPMS);

            if (systemJavaVersion != null && !systemJavaVersion.isEmpty()) {
                eeCapJavaSpecificationVersion = "eecap-" + OsgiUtil.generateJavaVersionForSystemPackages(systemJavaVersion);
                LOG.debug("  System java.version: [{}], generated EE Capabilities Java Specification Version: [{}]", systemJavaVersion, eeCapJavaSpecificationVersion);
                eeCapJavaSpecificationVersionSubstitution = properties.getProperty(eeCapJavaSpecificationVersion);
                if (eeCapJavaSpecificationVersionSubstitution == null || eeCapJavaSpecificationVersionSubstitution.isEmpty()) {
                    LOG.warn("System property [{}] is null or empty.  Unable to replace Felix EE capabilities.  Replacing with empty string", eeCapJavaSpecificationVersion);
                    eeCapJavaSpecificationVersionSubstitution = "";
                } else {
                    eeCapJavaSpecificationVersionSubstitution = eeCapJavaSpecificationVersionSubstitution.trim();
                }
            } else {
                LOG.warn("System property [{}] is null or empty.  Unable to replace Felix EE capabilities.", "java.version");
            }

            if (constains_Felix_Service_Caps) {
                String felixServiceCapsSubstitution = properties.getProperty(FELIX_SERVICE_CAPS_KEY);
                if (felixServiceCapsSubstitution == null || felixServiceCapsSubstitution.isEmpty()) {
                    LOG.warn("System property [{}] is null or empty.  Unable to replace Felix service capabilities.  Replacing with empty string", FELIX_SERVICE_CAPS_KEY);
                    felixServiceCapsSubstitution = "";
                } else {
                    felixServiceCapsSubstitution = felixServiceCapsSubstitution.trim();
                }
                result = result.replace(FELIX_SERVICE_CAPS, felixServiceCapsSubstitution);
            }

            if (constains_Felix_EE_Cap_Java_Specification_Version && constains_Felix_EE_Cap_Detect_JPMS) {
                result = result.replace(FELIX_EE_CAP_JAVA_SPECIFICATION_VERSION, eeCapJavaSpecificationVersionSubstitution);
                result = result.replace(FELIX_EE_CAP_DETECT_JPMS, "").trim();  // Only replace one instance when both set (avoid duplicates).
            } else if (constains_Felix_EE_Cap_Java_Specification_Version) {
                result = result.replace(FELIX_EE_CAP_JAVA_SPECIFICATION_VERSION, eeCapJavaSpecificationVersionSubstitution);
            } else if (constains_Felix_EE_Cap_Detect_JPMS) {
                result = result.replace(FELIX_EE_CAP_DETECT_JPMS, eeCapJavaSpecificationVersionSubstitution);
            } else {
                LOG.warn("Properties property [{}] is present, but we are unable to replace Felix EE capabilities due to missing at least one of [{}] [{}] keys in the configuration",
                         eeCapJavaSpecificationVersion, FELIX_EE_CAP_JAVA_SPECIFICATION_VERSION, FELIX_EE_CAP_DETECT_JPMS);
            }

        } else {
            LOG.warn("Unable to expand OSGi framework system capabilities elements.  Capability parameter [{}] is missing or empty", capabilities);
        }
        return result;
    }

    /**
     * Method to expand all OSGi framework execution environment elements from the configuration.  Once complete the resulting string
     * should be the expansion of all entries found in the {@link Constants.FRAMEWORK_EXECUTIONENVIRONMENT} content passed into the method.
     * 
     * @param executionEnvironment  A string retrieval of an OSGi framework execution environment property entry {@link Constants.FRAMEWORK_EXECUTIONENVIRONMENT} containing one or more of
     *                     {@link FELIX_EE_JAVA_SPECIFICATION_VERSION} {@link FELIX_EE_FELIX_DETECT_JPMS}.
     * @param properties   The Felix properties.
     * @return  A fully-expanded version of the {@link Constants.FRAMEWORK_EXECUTIONENVIRONMENT} content, after expanding all its contained elements in the Felix properties.
     */
    protected String expandAllOsgiFrameworkExecutionEnvironments(String executionEnvironment, Properties properties) {
        String result = executionEnvironment;

        if (properties == null) {
            throw new IllegalArgumentException("Cannot expand OSGi framework execution environment elements given a null properties reference");
        }

        if (executionEnvironment != null && !executionEnvironment.isEmpty()) {
            final String systemJavaVersion = System.getProperty("java.version");
            String eeJavaSpecificationVersion = null;
            String eeJavaSpecificationVersionSubstitution = null;
            final boolean constains_Felix_EE_Java_Specification_Version = executionEnvironment.contains(FELIX_EE_JAVA_SPECIFICATION_VERSION);
            final boolean constains_Felix_EE_Felix_Detect_JPMS = executionEnvironment.contains(FELIX_EE_FELIX_DETECT_JPMS);

            if (systemJavaVersion != null && !systemJavaVersion.isEmpty()) {
                eeJavaSpecificationVersion = "ee-" + OsgiUtil.generateJavaVersionForSystemPackages(systemJavaVersion);
                LOG.debug("  System java.version: [{}], generated EE Java Specification Version: [{}]", systemJavaVersion, eeJavaSpecificationVersion);
                eeJavaSpecificationVersionSubstitution = properties.getProperty(eeJavaSpecificationVersion);
                if (eeJavaSpecificationVersionSubstitution == null || eeJavaSpecificationVersionSubstitution.isEmpty()) {
                    LOG.warn("System property [{}] is null or empty.  Unable to replace Felix EE execution environment.  Replacing with empty string", eeJavaSpecificationVersion);
                    eeJavaSpecificationVersionSubstitution = "";
                } else {
                    eeJavaSpecificationVersionSubstitution = eeJavaSpecificationVersionSubstitution.trim();
                }
            } else {
                LOG.warn("System property [{}] is null or empty.  Unable to replace Felix EE execution environment", "java.version");
            }

            if (constains_Felix_EE_Java_Specification_Version && constains_Felix_EE_Felix_Detect_JPMS) {
                result = result.replace(FELIX_EE_JAVA_SPECIFICATION_VERSION, eeJavaSpecificationVersionSubstitution);
                result = result.replace(FELIX_EE_FELIX_DETECT_JPMS, "").trim();  // Only replace one instance when both set (avoid duplicates).
            } else if (constains_Felix_EE_Java_Specification_Version) {
                result = result.replace(FELIX_EE_JAVA_SPECIFICATION_VERSION, eeJavaSpecificationVersionSubstitution);
            } else if (constains_Felix_EE_Felix_Detect_JPMS) {
                result = result.replace(FELIX_EE_FELIX_DETECT_JPMS, eeJavaSpecificationVersionSubstitution);
            } else {
                LOG.warn("Properties property [{}] is present, but we are unable to replace Felix EE execution environment due to missing at least one of [{}] [{}] keys in the configuration",
                         eeJavaSpecificationVersion, FELIX_EE_JAVA_SPECIFICATION_VERSION, FELIX_EE_FELIX_DETECT_JPMS);
            }

        } else {
            LOG.warn("Unable to expand OSGi framework execution environment elements.  Execution environment parameter [{}] is missing or empty", executionEnvironment);
        }
        return result;
    }

}
