/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.freemarker.cache;

import freemarker.cache.TemplateLoader;
import freemarker.cache.URLTemplateLoader;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.utility.CollectionUtils;
import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.StringUtil;

import jakarta.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A {@link TemplateLoader} that uses streams reachable through {@link ServletContext#getResource(String)} as its source
 * of templates.  
 */
public class WebappTemplateLoader implements TemplateLoader {

    private static final Logger LOG = Logger.getLogger("freemarker.cache");

    private final ServletContext servletContext;
    private final String subdirPath;

    private Boolean urlConnectionUsesCaches;

    private boolean attemptFileAccess = true;

    /**
     * Creates a template loader that will use the specified servlet context to load the resources. It will use
     * the base path of <code>"/"</code> meaning templates will be resolved relative to the servlet context root
     * location.
     * 
     * @param servletContext
     *            the servlet context whose {@link ServletContext#getResource(String)} will be used to load the
     *            templates.
     */
    public WebappTemplateLoader(ServletContext servletContext) {
        this(servletContext, "/");
    }

    /**
     * Creates a template loader that will use the specified servlet context to load the resources. It will use the
     * specified base path, which is interpreted relatively to the context root (does not mater if you start it with "/"
     * or not). Path components should be separated by forward slashes independently of the separator character used by
     * the underlying operating system.
     * 
     * @param servletContext
     *            the servlet context whose {@link ServletContext#getResource(String)} will be used to load the
     *            templates.
     * @param subdirPath
     *            the base path to template resources.
     */
    public WebappTemplateLoader(ServletContext servletContext, String subdirPath) {
        NullArgumentException.check("servletContext", servletContext);
        NullArgumentException.check("subdirPath", subdirPath);

        subdirPath = subdirPath.replace('\\', '/');
        if (!subdirPath.endsWith("/")) {
            subdirPath += "/";
        }
        if (!subdirPath.startsWith("/")) {
            subdirPath = "/" + subdirPath;
        }
        this.subdirPath = subdirPath;
        this.servletContext = servletContext;
    }

    @Override
    public Object findTemplateSource(String name) throws IOException {
        String fullPath = subdirPath + name;

        if (attemptFileAccess) {
            // First try to open as plain file (to bypass servlet container resource caches).
            try {
                String realPath = servletContext.getRealPath(fullPath);
                if (realPath != null) {
                    File file = new File(realPath);
                    if (file.canRead() && file.isFile()) {
                        return file;
                    }
                }
            } catch (SecurityException e) {
                ;// ignore
            }
        }

        // If it fails, try to open it with servletContext.getResource.
        URL url = null;
        try {
            url = servletContext.getResource(fullPath);
        } catch (MalformedURLException e) {
            LOG.warn("Could not retrieve resource " + StringUtil.jQuoteNoXSS(fullPath),
                    e);
            return null;
        }
        return url == null ? null : new URLTemplateSource(url, getURLConnectionUsesCaches());
    }

    @Override
    public long getLastModified(Object templateSource) {
        if (templateSource instanceof File) {
            return ((File) templateSource).lastModified();
        } else {
            return ((URLTemplateSource) templateSource).lastModified();
        }
    }

    @Override
    public Reader getReader(Object templateSource, String encoding)
            throws IOException {
        if (templateSource instanceof File) {
            return new InputStreamReader(
                    new FileInputStream((File) templateSource),
                    encoding);
        } else {
            return new InputStreamReader(
                    ((URLTemplateSource) templateSource).getInputStream(),
                    encoding);
        }
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
        if (templateSource instanceof File) {
            // Do nothing.
        } else {
            ((URLTemplateSource) templateSource).close();
        }
    }

    /**
     * Getter pair of {@link #setURLConnectionUsesCaches(Boolean)}.
     * 
     * @since 2.3.21
     */
    public Boolean getURLConnectionUsesCaches() {
        return urlConnectionUsesCaches;
    }

    /**
     * It does the same as {@link URLTemplateLoader#setURLConnectionUsesCaches(Boolean)}; see there.
     * 
     * @since 2.3.21
     */
    public void setURLConnectionUsesCaches(Boolean urlConnectionUsesCaches) {
        this.urlConnectionUsesCaches = urlConnectionUsesCaches;
    }

    /**
     * Show class name and some details that are useful in template-not-found errors.
     * 
     * @since 2.3.21
     */
    @Override
    public String toString() {
        return TemplateLoaderUtils.getClassNameForToString(this)
                + "(subdirPath=" + StringUtil.jQuote(subdirPath)
                + ", servletContext={contextPath=" + StringUtil.jQuote(getContextPath())
                + ", displayName=" + StringUtil.jQuote(servletContext.getServletContextName()) + "})";
    }

    /** Gets the context path if we are on Servlet 2.5+, or else returns failure description string. */
    private String getContextPath() {
        try {
            Method m = servletContext.getClass().getMethod("getContextPath", CollectionUtils.EMPTY_CLASS_ARRAY);
            return (String) m.invoke(servletContext, CollectionUtils.EMPTY_OBJECT_ARRAY);
        } catch (Throwable e) {
            return "[can't query before Serlvet 2.5]";
        }
    }

    /**
     * Getter pair of {@link #setAttemptFileAccess(boolean)}.
     * 
     * @since 2.3.23
     */
    public boolean getAttemptFileAccess() {
        return attemptFileAccess;
    }

    /**
     * Specifies that before loading templates with {@link ServletContext#getResource(String)}, it should try to load
     * the template as {@link File}; default is {@code true}, though it's not always recommended anymore. This is a
     * workaround for the case when the servlet container doesn't show template modifications after the template was
     * already loaded earlier. But it's certainly better to counter this problem by disabling the URL connection cache
     * with {@link #setURLConnectionUsesCaches(Boolean)}, which is also the default behavior with
     * {@link Configuration#setIncompatibleImprovements(freemarker.template.Version) incompatible_improvements} 2.3.21
     * and later.
     * 
     * @since 2.3.23
     */
    public void setAttemptFileAccess(boolean attemptLoadingFromFile) {
        this.attemptFileAccess = attemptLoadingFromFile;
    }

}