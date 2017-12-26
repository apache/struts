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

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.util.finder.ResourceFinder;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.main.AutoProcessor;
import org.apache.felix.shell.ShellService;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.util.tracker.ServiceTracker;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
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
 */
public abstract class BaseOsgiHost implements OsgiHost {

    private static final Logger LOG = LogManager.getLogger(BaseOsgiHost.class);

    protected static final Pattern versionPattern = Pattern.compile("([\\d])+[\\.-]");

    protected ServletContext servletContext;

    public abstract void init(ServletContext servletContext);

    public abstract void destroy() throws Exception;

    /**
     * This bundle map will not change, but the status of the bundles can change over time.
     * Use getActiveBundles() for active bundles
     *
     * @return map with bundles
     */
    public abstract Map<String, Bundle> getBundles();

    public abstract Map<String, Bundle> getActiveBundles();

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
        return StringUtils.defaultString(this.servletContext.getInitParameter(paramName), defaultValue);
    }

    protected void addAutoStartBundles(Properties configProps) {
        //starts system bundles in level 1
        List<String> bundleJarsLevel1 = new ArrayList<String>();
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
            for (Map.Entry<String, String> runLevel : runLevels.entrySet()) {
                String runLevelKey = runLevel.getKey();
                if ("1".endsWith(runLevelKey)) {
                    throw new StrutsException("Run level dirs must be greater than 1. Run level 1 is reserved for the Felix bundles");
                }
                List<String> bundles = getBundlesInDir(runLevel.getValue());
                configProps.put(AutoProcessor.AUTO_START_PROP + "." + runLevelKey, StringUtils.join(bundles, " "));
            }
        }
    }

    /**
     * @param dir directory
     * @return  a list of directories under a directory whose name is a number
     */
    protected Map<String, String> getRunLevelDirs(String dir) {
        Map<String, String> dirs = new HashMap<String, String>();
        try {
            ResourceFinder finder = new ResourceFinder();
            URL url = finder.find("bundles");
            if (url != null) {
                if ("file".equals(url.getProtocol())) {
                    File bundlesDir = new File(url.toURI());
                    String[] runLevelDirs = bundlesDir.list(new FilenameFilter() {
                        public boolean accept(File file, String name) {
                            try {
                                return file.isDirectory() && Integer.valueOf(name) > 0;
                            } catch (NumberFormatException ex) {
                                //the name is not a number
                                return false;
                            }
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
                    LOG.warn("Unable to read [{}] directory", dir);
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
        List<String> bundleJars = new ArrayList<String>();
        try {
            ResourceFinder finder = new ResourceFinder();
            URL url = finder.find(dir);
            if (url != null) {
                if ("file".equals(url.getProtocol())) {
                    File bundlesDir = new File(url.toURI());
                    File[] bundles = bundlesDir.listFiles(new FilenameFilter() {
                        public boolean accept(File file, String name) {
                            return StringUtils.endsWith(name, ".jar");
                        }
                    });

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
                    LOG.warn("Unable to read [{}] directory", dir);
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

    protected void replaceSystemPackages(Properties properties) {
        //Felix has a way to load the config file and substitution expressions
        //but the method does not have a way to specify the file (other than in an env variable)

        //${jre-${java.specification.version}}
        String systemPackages = (String) properties.get(Constants.FRAMEWORK_SYSTEMPACKAGES);
        String jreVersion = "jre-" + System.getProperty("java.version").substring(0, 3);
        systemPackages = systemPackages.replace("${jre-${java.specification.version}}", (String) properties.get(jreVersion));
        properties.put(Constants.FRAMEWORK_SYSTEMPACKAGES, systemPackages);
    }

    /*
        Find subpackages of the packages defined in the property file and export them
     */
    protected void addExportedPackages(Properties strutsConfigProps, Properties configProps) {
        String[] rootPackages = StringUtils.split((String) strutsConfigProps.get("scanning.package.includes"), ",");
        ResourceFinder finder = new ResourceFinder(StringUtils.EMPTY);
        List<String> exportedPackages = new ArrayList<String>();
        //build a list of subpackages
        for (String rootPackage : rootPackages) {
            try {
                String version = null;
                if (rootPackage.indexOf(";") > 0) {
                    String[] splitted = rootPackage.split(";");
                    rootPackage = splitted[0];
                    version = splitted[1];
                }
                Map<URL, Set<String>> subpackagesMap = finder.findPackagesMap(StringUtils.replace(rootPackage.trim(), ".", "/"));
                for (Map.Entry<URL, Set<String>> entry : subpackagesMap.entrySet()) {
                    URL url = entry.getKey();
                    Set<String> packages = entry.getValue();

                    //get version if not set
                    if (StringUtils.isBlank(version)) {
                        version = getVersion(url);
                    }

                    if (packages != null) {
                        for (String subpackage : packages) {
                            exportedPackages.add(subpackage + "; version=" + version);
                        }
                    }
                }
            } catch (IOException e) {
                LOG.error("Unable to find subpackages of [{}]", rootPackage, e);
            }
        }

        //make a string with the exported packages and add it to the system properties
        if (!exportedPackages.isEmpty()) {
            String systemPackages = (String) configProps.get(Constants.FRAMEWORK_SYSTEMPACKAGES);
            systemPackages = StringUtils.removeEnd(systemPackages, ",") + "," + StringUtils.join(exportedPackages, ",");
            configProps.put(Constants.FRAMEWORK_SYSTEMPACKAGES, systemPackages);
        }
    }

    /**
     * @param url URL for package
     * @return  the version used to export the packages. it tries to get it from MANIFEST.MF, or the file name
     */
    protected String getVersion(URL url) {
        if ("jar".equals(url.getProtocol())) {
            try {
                FileManager fileManager = ServletActionContext.getContext().getInstance(FileManagerFactory.class).getFileManager();
                JarFile jarFile = new JarFile(new File(fileManager.normalizeToFileProtocol(url).toURI()));
                Manifest manifest = jarFile.getManifest();
                if (manifest != null) {
                    String version = manifest.getMainAttributes().getValue("Bundle-Version");
                    if (StringUtils.isNotBlank(version)) {
                        return getVersionFromString(version);
                    }
                } else {
                    //try to get the version from the file name
                    return getVersionFromString(jarFile.getName());
                }
            } catch (Exception e) {
                LOG.error("Unable to extract version from [{}], defaulting to '1.0.0'", url.toExternalForm());
            }
        }
        return "1.0.0";
    }

    /**
     * @param str string for extract version
     * @return Extracts numbers followed by "." or "-" from the string and joins them with "."
     */
    protected String getVersionFromString(String str) {
        Matcher matcher = versionPattern.matcher(str);
        List<String> parts = new ArrayList<String>();
        while (matcher.find()) {
            parts.add(matcher.group(1));
        }
        //default
        if (parts.size() == 0) {
            return "1.0.0";
        }
        while (parts.size() < 3) {
            parts.add("0");
        }
        return StringUtils.join(parts, ".");
    }

    protected Properties getProperties(String fileName) {
        ResourceFinder finder = new ResourceFinder("");
        try {
            return finder.findProperties(fileName);
        } catch (IOException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Unable to read property file [#]", fileName);
            }
            return new Properties();
        }
    }

}
