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
package org.apache.struts2.jasper.compiler;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.struts2.jasper.Constants;
import org.apache.struts2.jasper.JspCompilationContext;
import org.apache.struts2.jasper.Options;
import org.apache.struts2.jasper.runtime.JspFactoryImpl;
import org.apache.struts2.jasper.security.SecurityClassLoad;
import org.apache.struts2.jasper.servlet.JspCServletContext;
import org.apache.struts2.jasper.servlet.JspServletWrapper;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * Class for tracking JSP compile time file dependencies when the
 * &#060;%{@literal @}include file="..."%&#062; directive is used.
 * </p>
 *
 * <p>
 * A background thread periodically checks the files a JSP page
 * is dependent upon.  If a dependent file changes the JSP page
 * which included it is recompiled.
 * </p>
 *
 * <p>
 * Only used if a web application context is a directory.
 * </p>
 *
 * @author Glenn L. Nielsen
 * @version $Revision: 505593 $
 */
public final class JspRuntimeContext {

    // Logger
    private Log log = LogFactory.getLog(JspRuntimeContext.class);

    /*
     * Counts how many times the webapp's JSPs have been reloaded.
     */
    private int jspReloadCount;

    /**
     * Preload classes required at runtime by a JSP servlet so that
     * we don't get a defineClassInPackage security exception.
     */
    static {
        JspFactoryImpl factory = new JspFactoryImpl();
        SecurityClassLoad.securityClassLoad(factory.getClass().getClassLoader());
        if (System.getSecurityManager() != null) {
            String basePackage = "org.apache.struts2.jasper.";
            try {
                factory.getClass().getClassLoader().loadClass(basePackage +
                        "runtime.JspFactoryImpl$PrivilegedGetPageContext");
                factory.getClass().getClassLoader().loadClass(basePackage +
                        "runtime.JspFactoryImpl$PrivilegedReleasePageContext");
                factory.getClass().getClassLoader().loadClass(basePackage +
                        "runtime.JspRuntimeLibrary");
                factory.getClass().getClassLoader().loadClass(basePackage +
                        "runtime.JspRuntimeLibrary$PrivilegedIntrospectHelper");
                factory.getClass().getClassLoader().loadClass(basePackage +
                        "runtime.ServletResponseWrapperInclude");
                factory.getClass().getClassLoader().loadClass(basePackage +
                        "servlet.JspServletWrapper");
            } catch (ClassNotFoundException ex) {
                throw new IllegalStateException(ex);
            }
        }

        JspFactory.setDefaultFactory(factory);
    }

    // ----------------------------------------------------------- Constructors

    /**
     * <p>
     * Create a JspRuntimeContext for a web application context.
     * </p>
     *
     * <p>
     * Loads in any previously generated dependencies from file.
     * </p>
     *
     * @param context ServletContext for web application
     * @param options options
     */
    public JspRuntimeContext(ServletContext context, Options options) {

        this.context = context;
        this.options = options;

        // Get the parent class loader
        parentClassLoader = Thread.currentThread().getContextClassLoader();
        if (parentClassLoader == null) {
            parentClassLoader = this.getClass().getClassLoader();
        }

        if (log.isDebugEnabled()) {
            if (parentClassLoader != null) {
                log.debug(Localizer.getMessage("jsp.message.parent_class_loader_is",
                        parentClassLoader.toString()));
            } else {
                log.debug(Localizer.getMessage("jsp.message.parent_class_loader_is",
                        "<none>"));
            }
        }

        try {
            initClassPath();
        } catch (IOException e) {
            context.log("ClassPath Init for context failed", e);
        }

        if (context instanceof JspCServletContext) {
            return;
        }

        if (Constants.IS_SECURITY_ENABLED) {
            initSecurity();
        }

        // If this web application context is running from a
        // directory, start the background compilation thread
        String appBase = context.getRealPath("/");
        if (!options.getDevelopment()
                && appBase != null
                && options.getCheckInterval() > 0) {
            lastCheck = System.currentTimeMillis();
        }
    }

    // ----------------------------------------------------- Instance Variables

    /**
     * This web applications ServletContext
     */
    private ServletContext context;
    private Options options;
    private ClassLoader parentClassLoader;
    private PermissionCollection permissionCollection;
    private CodeSource codeSource;
    private String classpath;
    private long lastCheck = -1L;

    /**
     * Maps JSP pages to their JspServletWrapper's
     */
    private Map<String, JspServletWrapper> jsps = new ConcurrentHashMap<>();


    // ------------------------------------------------------ Public Methods

    /**
     * Add a new JspServletWrapper.
     *
     * @param jspUri JSP URI
     * @param jsw    Servlet wrapper for JSP
     */
    public void addWrapper(String jspUri, JspServletWrapper jsw) {
        jsps.put(jspUri, jsw);
    }

    /**
     * Get an already existing JspServletWrapper.
     *
     * @param jspUri JSP URI
     * @return JspServletWrapper for JSP
     */
    public JspServletWrapper getWrapper(String jspUri) {
        return jsps.get(jspUri);
    }

    /**
     * Remove a  JspServletWrapper.
     *
     * @param jspUri JSP URI of JspServletWrapper to remove
     */
    public void removeWrapper(String jspUri) {
        jsps.remove(jspUri);
    }

    /**
     * Returns the number of JSPs for which JspServletWrappers exist, i.e.,
     * the number of JSPs that have been loaded into the webapp.
     *
     * @return The number of JSPs that have been loaded into the webapp
     */
    public int getJspCount() {
        return jsps.size();
    }

    /**
     * Get the SecurityManager Policy CodeSource for this web
     * applicaiton context.
     *
     * @return CodeSource for JSP
     */
    public CodeSource getCodeSource() {
        return codeSource;
    }

    /**
     * Get the parent URLClassLoader.
     *
     * @return URLClassLoader parent
     */
    public ClassLoader getParentClassLoader() {
        return parentClassLoader;
    }

    /**
     * Get the SecurityManager PermissionCollection for this
     * web application context.
     *
     * @return PermissionCollection permissions
     */
    public PermissionCollection getPermissionCollection() {
        return permissionCollection;
    }

    /**
     * Process a "destroy" event for this web application context.
     */
    public void destroy() {
        for (Object o : jsps.values()) {
            ((JspServletWrapper) o).destroy();
        }
    }

    /**
     * Increments the JSP reload counter.
     */
    public synchronized void incrementJspReloadCount() {
        jspReloadCount++;
    }

    /**
     * Resets the JSP reload counter.
     *
     * @param count Value to which to reset the JSP reload counter
     */
    public synchronized void setJspReloadCount(int count) {
        this.jspReloadCount = count;
    }

    /**
     * Gets the current value of the JSP reload counter.
     *
     * @return The current value of the JSP reload counter
     */
    public int getJspReloadCount() {
        return jspReloadCount;
    }


    /**
     * Method used by background thread to check the JSP dependencies
     * registered with this class for JSP's.
     */
    public void checkCompile() {

        if (lastCheck < 0) {
            // Checking was disabled
            return;
        }
        long now = System.currentTimeMillis();
        if (now > (lastCheck + (options.getCheckInterval() * 1000L))) {
            lastCheck = now;
        } else {
            return;
        }

        Object[] wrappers = jsps.values().toArray();
        for (Object wrapper : wrappers) {
            JspServletWrapper jsw = (JspServletWrapper) wrapper;
            JspCompilationContext ctxt = jsw.getJspEngineContext();
            // JspServletWrapper also synchronizes on this when
            // it detects it has to do a reload
            synchronized (jsw) {
                try {
                    ctxt.compile();
                } catch (FileNotFoundException ex) {
                    ctxt.incrementRemoved();
                } catch (Throwable t) {
                    jsw.getServletContext().log("Background compile failed",
                            t);
                }
            }
        }

    }

    /**
     * @return The classpath that is passed off to the Java compiler.
     */
    public String getClassPath() {
        return classpath;
    }


    // -------------------------------------------------------- Private Methods


    /**
     * Method used to initialize classpath for compiles.
     */
    private void initClassPath() throws IOException {

        URL[] urls;
        if (parentClassLoader instanceof URLClassLoader) {
            urls = ((URLClassLoader) parentClassLoader).getURLs();
        } else {    //jdk9 or later
            urls = Collections.list(parentClassLoader.getResources("")).toArray(new URL[0]);
        }
        StringBuffer cpath = new StringBuffer();
        String sep = System.getProperty("path.separator");

        for (int i = 0; i < urls.length; i++) {
            // Tomcat 4 can use URL's other than file URL's,
            // a protocol other than file: will generate a
            // bad file system path, so only add file:
            // protocol URL's to the classpath.

            if (urls[i].getProtocol().equals("file")) {
                cpath.append((String) urls[i].getFile() + sep);
            }
        }

        cpath.append(options.getScratchDir() + sep);

        String cp = (String) context.getAttribute(Constants.SERVLET_CLASSPATH);
        if (cp == null || cp.equals("")) {
            cp = options.getClassPath();
        }

        classpath = cpath.toString() + cp;

        if (log.isDebugEnabled()) {
            log.debug("Compilation classpath initialized: " + getClassPath());
        }
    }

    /**
     * Method used to initialize SecurityManager data.
     */
    private void initSecurity() {

        // Setup the PermissionCollection for this web app context
        // based on the permissions configured for the root of the
        // web app context directory, then add a file read permission
        // for that directory.
        Policy policy = Policy.getPolicy();
        if (policy != null) {
            try {
                // Get the permissions for the web app context
                String docBase = context.getRealPath("/");
                if (docBase == null) {
                    docBase = options.getScratchDir().toString();
                }
                String codeBase = docBase;
                if (!codeBase.endsWith(File.separator)) {
                    codeBase = codeBase + File.separator;
                }
                File contextDir = new File(codeBase);
                URL url = contextDir.getCanonicalFile().toURL();
                codeSource = new CodeSource(url, (Certificate[]) null);
                permissionCollection = policy.getPermissions(codeSource);

                // Create a file read permission for web app context directory
                if (!docBase.endsWith(File.separator)) {
                    permissionCollection.add
                            (new FilePermission(docBase, "read"));
                    docBase = docBase + File.separator;
                } else {
                    permissionCollection.add
                            (new FilePermission
                                    (docBase.substring(0, docBase.length() - 1), "read"));
                }
                docBase = docBase + "-";
                permissionCollection.add(new FilePermission(docBase, "read"));

                // Create a file read permission for web app tempdir (work)
                // directory
                String workDir = options.getScratchDir().toString();
                if (!workDir.endsWith(File.separator)) {
                    permissionCollection.add
                            (new FilePermission(workDir, "read"));
                    workDir = workDir + File.separator;
                }
                workDir = workDir + "-";
                permissionCollection.add(new FilePermission(workDir, "read"));

                // Allow the JSP to access org.apache.struts2.jasper.runtime.HttpJspBase
                permissionCollection.add(new RuntimePermission(
                        "accessClassInPackage.org.apache.struts2.jasper.runtime"));

                URL[] urls;
                if (parentClassLoader instanceof URLClassLoader) {
                    urls = ((URLClassLoader) parentClassLoader).getURLs();
                } else {    //jdk9 or later
                    urls = Collections.list(parentClassLoader.getResources("")).toArray(new URL[0]);
                }
                String jarUrl = null;
                String jndiUrl = null;
                for (URL url1 : urls) {
                    if (jndiUrl == null
                            && url1.toString().startsWith("jndi:")) {
                        jndiUrl = url1.toString() + "-";
                    }
                    if (jarUrl == null
                            && url1.toString().startsWith("jar:jndi:")
                            ) {
                        jarUrl = url1.toString();
                        jarUrl = jarUrl.substring(0, jarUrl.length() - 2);
                        jarUrl = jarUrl.substring(0,
                                jarUrl.lastIndexOf('/')) + "/-";
                    }
                }
                if (jarUrl != null) {
                    permissionCollection.add(
                            new FilePermission(jarUrl, "read"));
                    permissionCollection.add(
                            new FilePermission(jarUrl.substring(4), "read"));
                }
                if (jndiUrl != null)
                    permissionCollection.add(
                            new FilePermission(jndiUrl, "read"));
            } catch (Exception e) {
                context.log("Security Init for context failed", e);
            }
        }
    }


}
