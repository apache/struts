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

import com.opensymphony.xwork2.util.classloader.FileResourceStore;
import com.opensymphony.xwork2.util.classloader.JarResourceStore;
import com.opensymphony.xwork2.util.classloader.ReloadingClassLoader;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.commons.jci.monitor.FilesystemAlterationListener;
import org.apache.commons.jci.monitor.FilesystemAlterationMonitor;
import org.apache.commons.jci.monitor.FilesystemAlterationObserver;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Dispatcher;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.BeansException;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * This class can be used instead of XmlWebApplicationContext, and it will watch jar files and directories for changes
 * and reload then changed classes.
 * <br />
 * To use this class:
 * <ul>
 * <li>Set "struts.devMode" to "true" </li>
 * <li>Set "struts.class.reloading.watchList" to a comma separated list of directories, or jar files (absolute paths)</p>
 * <li>Add this to web.xml:
 * <pre>
 *  &lt;context-param&gt;
 *       &lt;param-name&gt;contextClass&lt;/param-name&gt;
 *       &lt;param-value&gt;org.apache.struts2.spring.ClassReloadingXMLWebApplicationContext&lt;/param-value&gt;
 *   &lt;/context-param&gt;
 *  </li>
 * <li>Add Apache Commons JCI FAM to the classpath. If you are using maven, add this to pom.xml:
 *  <pre>
 *  &lt;dependency&gt;
 *       &lt;groupId&gt;org.apache.commons&lt;/groupId&gt;
 *       &lt;artifactId&gt;commons-jci-fam&lt;/artifactId&gt;
 *       &lt;version&gt;1.0&lt;/version&gt;
 *       &lt;optional&gt;true&lt;/optional&gt;
 *  &lt;/dependency>
 *  </pre>
 * </li>
 * </ul>
 */
public class ClassReloadingXMLWebApplicationContext extends XmlWebApplicationContext implements FilesystemAlterationListener {
    private static final Logger LOG = LoggerFactory.getLogger(ClassReloadingXMLWebApplicationContext.class);

    protected ReloadingClassLoader classLoader;
    protected FilesystemAlterationMonitor fam;

    protected ClassReloadingBeanFactory beanFactory;
    //reload the runtime configuration when a change is detected
    private boolean reloadConfig;

    public void setupReloading(String[] watchList, String acceptClasses, ServletContext servletContext, boolean reloadConfig) {
        this.reloadConfig = reloadConfig;

        classLoader = new ReloadingClassLoader(ClassReloadingXMLWebApplicationContext.class.getClassLoader());

        //make a list of accepted classes
        if (StringUtils.isNotBlank(acceptClasses)) {
            String[] splitted = acceptClasses.split(",");
            Set<Pattern> patterns = new HashSet<Pattern>(splitted.length);
            for (String pattern : splitted)
                patterns.add(Pattern.compile(pattern));

            classLoader.setAccepClasses(patterns);
        }

        fam = new FilesystemAlterationMonitor();

        //setup stores
        for (String watch : watchList) {
            File file = new File(watch);

            //make it absolute, if it is a relative path
            if (!file.isAbsolute())
                file = new File(servletContext.getRealPath(watch));

            if (watch.endsWith(".jar")) {
                classLoader.addResourceStore(new JarResourceStore(file));
                //register with the fam
                fam.addListener(file, this);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Watching [#0] for changes", file.getAbsolutePath());
                }
            } else {
                //get all subdirs
                List<File> dirs = new ArrayList<File>();
                getAllPaths(file, dirs);

                classLoader.addResourceStore(new FileResourceStore(file));

                for (File dir : dirs) {
                    //register with the fam
                    fam.addListener(dir, this);
                    if (LOG.isDebugEnabled()) {
                	LOG.debug("Watching [#0] for changes", dir.getAbsolutePath());
                    }
                }
            }
        }
        //setup the bean factory
        beanFactory = new ClassReloadingBeanFactory();
        beanFactory.setInstantiationStrategy(new ClassReloadingInstantiationStrategy());
        beanFactory.setBeanClassLoader(classLoader);

        //start watch thread
        fam.start();
    }

    /**
     * If root is a dir, find al the subdir paths
     */
    private void getAllPaths(File root, List<File> dirs) {
        dirs.add(root);

        if (root.isDirectory()) {
            File[] files = root.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        getAllPaths(file, dirs);
                    }
                }
            }
        }
    }

    public void close() {
        super.close();

        if (fam != null) {
            fam.removeListener(this);
            fam.stop();
        }
    }

    public void refresh() throws BeansException, IllegalStateException {
        if (classLoader != null) {
            classLoader.reload();
        }

        super.refresh();
    }

    protected DefaultListableBeanFactory createBeanFactory() {
        return beanFactory != null ? beanFactory : super.createBeanFactory();
    }

    protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        super.prepareBeanFactory(beanFactory);

        //overwrite the class loader in the bean factory
        if (classLoader != null)
            beanFactory.setBeanClassLoader(classLoader);
    }

    public void onDirectoryChange(File file) {
        reload(file);
    }

    public void onDirectoryCreate(File file) {
        reload(file);
    }

    public void onDirectoryDelete(File file) {
    }

    public void onFileChange(File file) {
        reload(file);
    }

    public void onFileCreate(File file) {
        reload(file);
    }

    private void reload(File file) {
        if (classLoader != null) {
            final boolean debugEnabled = LOG.isDebugEnabled();
            if (debugEnabled)
                LOG.debug("Change detected in file [#0], reloading class loader", file.getAbsolutePath());
            classLoader.reload();
            if (reloadConfig && Dispatcher.getInstance() != null) {
                if (debugEnabled)
                    LOG.debug("Change detected in file [#0], reloading configuration", file.getAbsolutePath());
                Dispatcher.getInstance().getConfigurationManager().reload();
            }
        }
    }

    public void onFileDelete(File file) {
    }

    public void onStart(FilesystemAlterationObserver filesystemAlterationObserver) {
    }

    public void onStop(FilesystemAlterationObserver filesystemAlterationObserver) {
    }

    public ReloadingClassLoader getReloadingClassLoader() {
        return classLoader;
    }
}
