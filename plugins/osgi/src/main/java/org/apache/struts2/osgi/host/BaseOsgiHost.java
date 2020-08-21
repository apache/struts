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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterface;
import com.opensymphony.xwork2.util.finder.ClassLoaderInterfaceDelegate;
import com.opensymphony.xwork2.util.finder.ResourceFinder;
import com.opensymphony.xwork2.util.fs.DefaultFileManager;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.main.AutoProcessor;
import org.apache.felix.shell.ShellService;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsException;
import org.apache.struts2.osgi.OsgiUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.util.tracker.ServiceTracker;

import javax.servlet.ServletContext;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A base OsgiHost implementation
 * <br>
 * Servlet config params:
 * <p>struts.osgi.searchForPropertiesFilesInRelativePath: Defaults to "false".  Set to "true" for fallback search for properties files in relative path (e.g. for unit testing).</p>
 */
public abstract class BaseOsgiHost implements OsgiHost {

    private static final Logger LOG = LogManager.getLogger(BaseOsgiHost.class);

    protected static final Pattern VERSION_PATTERN = Pattern.compile("([\\d]+)(?:[\\.-]|$)");
    protected static final String SCANNING_PACKAGE_INCLUDES = "scanning.package.includes";
    protected static final String JRE_JAVA_SPECIFICATION_VERSION = "${jre-${java.specification.version}}";
    protected static final String DETECT_JAVA_VERSION = "${detect.java.version}";
    /**
     * @deprecated use {@link #VERSION_PATTERN} instead
     */
    @Deprecated
    protected static final Pattern versionPattern = VERSION_PATTERN;

    protected ServletContext servletContext;

    @Override
    public abstract void init(ServletContext servletContext);

    @Override
    public abstract void destroy() throws Exception;

    /**
     * This bundle map will not change, but the status of the bundles can change over time.
     * Use getActiveBundles() for active bundles
     *
     * @return map with bundles
     */
    @Override
    public abstract Map<String, Bundle> getBundles();

    @Override
    public abstract Map<String, Bundle> getActiveBundles();

    @Override
    public abstract BundleContext getBundleContext();

    protected abstract void addSpringOSGiSupport();

    /**
     * Gets a param from the ServletContext, returning the default value if the param is not set
     *
     * @param paramName    the name of the param to get from the ServletContext
     * @param defaultValue value to return if the param is not set
     * @return param from the ServletContext, returning the default value if the param is not set
     */
    protected String getServletContextParam(String paramName, String defaultValue) {
        if (this.servletContext == null) {
            throw new IllegalStateException("OSGi Host servlet context is null!");
        }
        return StringUtils.defaultString(this.servletContext.getInitParameter(paramName), defaultValue);
    }

    protected void addAutoStartBundles(Properties configProps) {
        //starts system bundles in level 1
        List<String> bundleJarsLevel1 = new ArrayList<>();
        bundleJarsLevel1.add(getJarUrl(ShellService.class));
        bundleJarsLevel1.add(getJarUrl(ServiceTracker.class));

        configProps.put(AutoProcessor.AUTO_START_PROP + ".1", StringUtils.join(bundleJarsLevel1, " "));

        //get a list of directories under /bundles with numeric names (the runlevel)
        Map<String, String> runLevels = getRunLevelDirs("bundles");
        if (runLevels.isEmpty()) {
            //there are no run level dirs, search for bundles in that dir
            List<String> bundles = getBundlesInDir("bundles");
            if (!bundles.isEmpty()) {
                configProps.put(AutoProcessor.AUTO_START_PROP + ".2", StringUtils.join(bundles, " "));
            }
        } else {
            runLevels.entrySet().forEach(runLevel -> {
                String runLevelKey = runLevel.getKey();
                if ("1".endsWith(runLevelKey)) {
                    throw new StrutsException("Run level dirs must be greater than 1. Run level 1 is reserved for the Felix bundles");
                }
                List<String> bundles = getBundlesInDir(runLevel.getValue());
                configProps.put(AutoProcessor.AUTO_START_PROP + "." + runLevelKey, StringUtils.join(bundles, " "));
            });
        }
    }

    /**
     * @param dir directory
     * @return  a list of directories under a directory whose name is a number
     */
    protected Map<String, String> getRunLevelDirs(String dir) {
        Map<String, String> dirs = new HashMap<>();
        try {
            ResourceFinder finder = new ResourceFinder();
            URL url = finder.find("bundles");
            if (url != null) {
                if ("file".equals(url.getProtocol())) {
                    File bundlesDir = new File(url.toURI());
                    String[] runLevelDirs = bundlesDir.list((File file, String name) -> {
                        try {
                            return file.isDirectory() && Integer.valueOf(name) > 0;
                        } catch (NumberFormatException ex) {
                            //the name is not a number
                            return false;
                        }
                    });

                    if (runLevelDirs != null && runLevelDirs.length > 0) {
                        //add all the dirs to the list
                        for (String runLevel : runLevelDirs) {
                            dirs.put(runLevel, StringUtils.removeEnd(dir,  "/") + "/" + runLevel);
                        }
                    } else {
                        LOG.debug("No run level directories found under the [{}] directory", dir);
                    }
                } else {
                    LOG.warn("Unable to read [{}] directory.  Protocol [{}] is not supported (try exploded WAR files)", dir, url.getProtocol());
                }
            } else {
                LOG.warn("The [{}] directory was not found", dir);
            }
        } catch (Exception e) {
            LOG.warn("Unable load bundles from the [{}] directory", dir, e);
        }
        return dirs;
    }

    protected List<String> getBundlesInDir(String dir) {
        List<String> bundleJars = new ArrayList<>();
        try {
            ResourceFinder finder = new ResourceFinder();
            URL url = finder.find(dir);
            if (url != null) {
                if ("file".equals(url.getProtocol())) {
                    File bundlesDir = new File(url.toURI());
                    File[] bundles = bundlesDir.listFiles((File file, String name) -> StringUtils.endsWith(name, ".jar"));

                    if (bundles != null && bundles.length > 0) {
                        //add all the bundles to the list
                        for (File bundle : bundles) {
                            String externalForm = bundle.toURI().toURL().toExternalForm();
                            LOG.debug("Adding bundle [{}]", externalForm);
                            bundleJars.add(externalForm);
                        }

                    } else {
                        LOG.debug("No bundles found under the [{}] directory", dir);
                    }
                } else {
                    LOG.warn("Unable to read [{}] directory.  Protocol [{}] is not supported (try exploded WAR files)", dir, url.getProtocol());
                }
            } else {
                LOG.warn("The [{}] directory was not found", dir);
            }
        } catch (Exception e) {
            LOG.warn("Unable load bundles from the [{}] directory", dir, e);
        }
        return bundleJars;
    }

    protected String getJarUrl(Class clazz) {
        ProtectionDomain protectionDomain = clazz.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URL loc = codeSource.getLocation();
        return loc.toString();
    }

    /**
     * Replace all instances of {@link JRE_JAVA_SPECIFICATION_VERSION}, within the {@link Constants.FRAMEWORK_SYSTEMPACKAGES}
     * property of the provided properties.  The replacement will be the value for the key "jre-x.y"
     * in the properties parameter (where x.y is the JRE version after transforming the System "java.version" property.
     * For example: "jre-x.y" is "jre-1.8" for Java 8, "jre-9.0" for Java 9, and "jre-11.0" for Java 11.
     * 
     * While performing the replacement, the elements within jre-x.y will also undergo a replacement of the substring {@link DETECT_JAVA_VERSION}
     * into something like "0.0.0.JavaSE_001_008" for Java 8 and earlier, "0.0.0.JavaSE_009" for Java 9 and newer.  If you prefer
     * manual control, use literal strings rather than {@link DETECT_JAVA_VERSION} in your properties file.
     * 
     * @param properties OSGi properties for which the {@link Constants.FRAMEWORK_SYSTEMPACKAGES} property's values
     *                   substrings {@link JRE_JAVA_SPECIFICATION_VERSION} will be replaced by the value for the key
     *                   "jre-xxx" (where xxx is the JRE version).
     *                   If no {@link Constants.FRAMEWORK_SYSTEMPACKAGES} property exists, this is a no-op.
     */
    protected void replaceSystemPackages(Properties properties) {
        //Felix has a way to load the config file and substitution expressions
        //but the method does not have a way to specify the file (other than in an env variable)

        if (properties == null) {
            throw new IllegalArgumentException("Cannot replace system packages using a null properties reference");
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
                    jreJavaSpecificationVersionSubstitution = jreJavaSpecificationVersionSubstitution.replace(DETECT_JAVA_VERSION, OsgiUtil.generateJava_SE_SystemPackageVersionString(systemJavaVersion)).trim();
                    systemPackages = systemPackages.replace(JRE_JAVA_SPECIFICATION_VERSION, jreJavaSpecificationVersionSubstitution);
                    properties.put(Constants.FRAMEWORK_SYSTEMPACKAGES, systemPackages);
                } else {
                    LOG.warn("Properties property [{}] is null or empty.  Unable to replace JRE system packages.  JRE system packages will be cleared", jreJavaSpecificationVersion);
                    systemPackages = systemPackages.replace(JRE_JAVA_SPECIFICATION_VERSION, "");
                    properties.put(Constants.FRAMEWORK_SYSTEMPACKAGES, systemPackages);
                }
            } else {
                LOG.warn("System property [{}] is null or empty.  Unable to replace JRE system packages", "java.version");
            }
            LOG.debug("OSGi System Packages (after replacement): [{}]", systemPackages);
        } else {
            LOG.warn("Unable to replace JRE system packages.  Properties required key [{}] is missing or empty",
                     Constants.FRAMEWORK_SYSTEMPACKAGES);
        }
    }

    /**
     * Find sub-packages of the packages defined in the property file and export them
     * 
     * @param strutsConfigProps Struts-OSGi configuration properties containing the {@link SCANNING_PACKAGE_INCLUDES} property
     *                          containing comma-separated top-level package values.
     * @param configProps OSGi configuration properties for which the {@link Constants.FRAMEWORK_SYSTEMPACKAGES} property's
     *                    value will have the sub-packages of strutsConfigProps appended to it.
     *                    If no {@link Constants.FRAMEWORK_SYSTEMPACKAGES} property exists, and the exported packages is non-empty,
     *                    then one is created that will contain the sub-packages of strutsConfigProps.
     */
    protected void addExportedPackages(Properties strutsConfigProps, Properties configProps) {

        if (strutsConfigProps == null) {
            throw new IllegalArgumentException("Cannot add exported packages using a null struts config properties reference");
        }

        LOG.debug("  scanning.package.includes lookup returns: [{}]", (String) strutsConfigProps.get(SCANNING_PACKAGE_INCLUDES));

        String[] rootPackages = StringUtils.split((String) strutsConfigProps.get(SCANNING_PACKAGE_INCLUDES), ",");
        ResourceFinder finder = new ResourceFinder(StringUtils.EMPTY);
        List<String> exportedPackages = new ArrayList<>();
        if (rootPackages == null || rootPackages.length == 0) {
            LOG.warn("Struts config roperties required key [{}] is missing or empty.  No exported packages are available to add", SCANNING_PACKAGE_INCLUDES);
            return;
        }
        //build a list of subpackages
        for (String rootPackage : rootPackages) {
            LOG.debug("  Looking for root package: [{}] ", rootPackage);

            try {
                String version = null;
                if (rootPackage.indexOf(";") > 0) {
                    String[] splitted = rootPackage.split(";");
                    rootPackage = splitted[0];
                    version = splitted[1];
                }
                Map<URL, Set<String>> subpackagesMap = finder.findPackagesMap(StringUtils.replace(rootPackage.trim(), ".", "/"));

                LOG.debug("  Trimmed package map for: [{}] has size: [{}]", rootPackage.trim(), subpackagesMap.size());

                for (Map.Entry<URL, Set<String>> entry : subpackagesMap.entrySet()) {
                    URL url = entry.getKey();
                    Set<String> packages = entry.getValue();

                    //get version if not set
                    if (StringUtils.isBlank(version)) {
                        version = getVersion(url);

                        LOG.debug("  Version was null.  Retrieved version: [{}] for [{}]", version, (url != null ? url.toString() : null) );
                    }

                    if (packages != null) {
                        LOG.debug("  Subpackages size: [{}]", packages.size());

                        for (String subpackage : packages) {
                            LOG.trace("  Adding subppackage: [{}; version=\"{}\"]", subpackage, version);

                            exportedPackages.add(subpackage + "; version=\"" + version + "\"");
                        }
                    } else {
                        LOG.debug("  Subpackages is null");
                    }
                }
            } catch (IOException e) {
                LOG.error("Unable to find subpackages of [{}]", rootPackage, e);
            }
        }

        //make a string with the exported packages and add it to the system properties
        if (!exportedPackages.isEmpty() && configProps != null) {
            String systemPackages = (String) configProps.get(Constants.FRAMEWORK_SYSTEMPACKAGES);
            if (systemPackages == null || systemPackages.isEmpty()) {
                LOG.warn("Config properties required key [{}] is missing or empty.  Only the exported packages will be present",
                         Constants.FRAMEWORK_SYSTEMPACKAGES);
                systemPackages = StringUtils.join(exportedPackages, ",");
            } else {
                systemPackages = StringUtils.removeEnd(systemPackages, ",") + "," + StringUtils.join(exportedPackages, ",");
            }
            configProps.put(Constants.FRAMEWORK_SYSTEMPACKAGES, systemPackages);

            LOG.debug("Adding exported framework packages: [{}]", systemPackages);
        } else {
            LOG.warn("NOT adding any exported framework packages.  No exported packages or config props is null");
        }
    }

    /**
     * @param url URL for package
     * @return  the version used to export the packages. it tries to get it from MANIFEST.MF, or the file name
     */
    protected String getVersion(URL url) {
        if ("jar".equals(url.getProtocol())) {
            JarFile jarFile = null;
            try {
                ActionContext actionContext = null;
                FileManagerFactory fileManagerFactory = null;
                FileManager fileManager = null;
                actionContext = ServletActionContext.getContext();
                if (actionContext == null) {
                    LOG.warn("ActionContext is null.  Cannot load FileManagerFactory from it");
                }
                if (actionContext != null) {
                    fileManagerFactory = actionContext.getInstance(FileManagerFactory.class);
                }
                if (fileManagerFactory == null) {
                    LOG.warn("FileManagerFactory is null in ActionContext.  Cannot load FileManager from it");
                } else {
                    fileManager = fileManagerFactory.getFileManager();
                }
                if (fileManager == null) {
                    if (fileManagerFactory != null) {
                       LOG.debug("FileManager is null in FileManagerFactory.  Using a DefaultFileManager");
                    } else {
                       LOG.debug("Using a DefaultFileManager");
                    }
                    fileManager = new DefaultFileManager();
                }

                jarFile = new JarFile(new File(fileManager.normalizeToFileProtocol(url).toURI()));
                Manifest manifest = jarFile.getManifest();
                String jarFileName = jarFile.getName();
                if (jarFileName != null) {
                    // Strip extraneous file path elements to limit the string to the JAR name itself, if possible.
                    int lastExclamationIndex = jarFileName.lastIndexOf("!");
                    if (lastExclamationIndex != -1) {
                        jarFileName = jarFileName.substring(0, lastExclamationIndex);
                    }
                    if (jarFileName.toLowerCase().endsWith(".jar")) {
                        int lastPathSeparatorIndex = jarFileName.lastIndexOf(File.separator);
                        if (lastPathSeparatorIndex != -1) {
                            jarFileName = jarFileName.substring(lastPathSeparatorIndex + 1);
                        }
                    }
                }
                if (manifest != null) {
                    String version = manifest.getMainAttributes().getValue("Bundle-Version");
                    if (StringUtils.isNotBlank(version)) {
                        LOG.debug("Attempting to get bundle version for [{}] via its JAR manifest", url.toExternalForm());
                        return getVersionFromString(version);
                    } else {
                        //try to get the version from the file name
                        LOG.debug("Attempting to get bundle version for [{}] via its filename [{}]", url.toExternalForm(), jarFileName);
                        return getVersionFromString(jarFileName);
                    }
                } else {
                    //try to get the version from the file name
                    LOG.debug("Attempting to get bundle version for [{}] via its filename [{}]", url.toExternalForm(), jarFileName);
                    return getVersionFromString(jarFileName);
                }
            } catch (Exception e) {
                LOG.error("Unable to extract version from [{}], defaulting to '1.0.0'", url.toExternalForm());
            } finally {
                if (jarFile != null) {
                    try {
                        jarFile.close();
                    } catch (Exception ex) {}
                }
            }
        }
        return "1.0.0";
    }

    /**
     * @param str string for extract version
     * @return Extracts numbers followed by "." or "-" from the string and joins them with "."
     */
    protected String getVersionFromString(String str) {
        final String trimmedString = (str != null ? str.trim() : str);
        Matcher matcher = VERSION_PATTERN.matcher(trimmedString);
        List<String> parts = new ArrayList<>();
        while (matcher.find()) {
            parts.add(matcher.group(1));
        }
        //default
        if (parts.isEmpty()) {
            return "1.0.0";
        }
        while (parts.size() < 3) {
            parts.add("0");
        }
        while (parts.size() > 3) {
            parts.remove(0);  // Assume a bad match with extra digits picked up from earlier in the path.  Only keep last three parts (x.y.z).
        }
        return StringUtils.join(parts, ".");
    }

    protected Properties getProperties(String fileName) {
        ResourceFinder finder = new ResourceFinder(StringUtils.EMPTY);
        try {
            return finder.findProperties(fileName);
        } catch (IOException e) {
            LOG.error("Unable to read the property file [{}]", fileName, e);
            if (searchForPropertiesFilesInRelativePath()) {
                try {
                    LOG.warn("Relative path search is enabled.  Retry attempt to read the property file [{}] from the relative path", fileName);
                    return findPropertiesFileInRelativePath(fileName);
                } catch (IOException ioex) {
                    LOG.error("Unable to read the property file [{}] using the relative path", fileName, ioex);
                }
            }
            return new Properties();
        }
    }

    /**
     * Check ServletContext initialization parameter "struts.osgi.searchForPropertiesFilesInRelativePath".
     * 
     * @return true if "struts.osgi.searchForPropertiesFilesInRelativePath" is "true" in ServletContext initialization parameters, false otherwise.
     */
    protected boolean searchForPropertiesFilesInRelativePath() {
        if (this.servletContext != null) {
            final String searchForPropertiesFilesInRelativePath = getServletContextParam("struts.osgi.searchForPropertiesFilesInRelativePath", "false");
            return Boolean.parseBoolean(searchForPropertiesFilesInRelativePath);
}
        return false;
    }

    /**
     * Attempt to read a properties file from the relative path of the current classloader.
     * Intended as an alternate configuration fallback for special scenarios where the default lookup
     * is not functional (such as for unit tests).
     * 
     * @param fileName the filename (relative path) of the properties file.
     * @return a Properties bundle loaded from the provided fileName.
     * @throws IOException if the properties file does not exist or cannot be loaded.
     */
    protected Properties findPropertiesFileInRelativePath(String fileName) throws IOException {
        if (fileName == null || fileName.toLowerCase().endsWith(".class") || fileName.toLowerCase().endsWith(".jar")) {
            throw new IllegalArgumentException("Provided file name cannot be null, nor should it be a class or jar file");
        }

        final ClassLoaderInterface classLoaderInterface = new ClassLoaderInterfaceDelegate(Thread.currentThread().getContextClassLoader());
        final URL fileUrl = classLoaderInterface.getResource(fileName);
        try (InputStream reader = new BufferedInputStream(fileUrl.openStream())) {
            Properties properties = new Properties();
            properties.load(reader);

            return properties;
        }
    }

}
