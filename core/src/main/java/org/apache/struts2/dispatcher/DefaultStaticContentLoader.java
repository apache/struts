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
package org.apache.struts2.dispatcher;

import org.apache.struts2.inject.Inject;
import org.apache.struts2.util.ClassLoaderUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.webjars.WebJarUrlProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;

/**
 * <p>
 * <b>Default implementation to server static content</b>
 * </p>
 *
 * <p>
 * This class is used to serve common static content needed when using various parts of Struts, such as JavaScript
 * files, CSS files, etc. It works by looking for requests to {@link #uiStaticContentPath}/*  and then mapping the value
 * after to common packages in Struts and, optionally, in your class path. By default, the following packages are
 * automatically searched:
 * </p>
 *
 * <ul>
 * <li>org.apache.struts2.static</li>
 * <li>template</li>
 * <li>static</li>
 * </ul>
 *
 * <p>
 * This means that you can simply request {@link #uiStaticContentPath}/xhtml/styles.css and the XHTML UI theme's default stylesheet
 * will be returned. Likewise, many of the AJAX UI components require various JavaScript files, which are found in the
 * org.apache.struts2.static package. If you wish to add additional packages to be searched, you can add a comma
 * separated (space, tab and new line will do as well) list in the filter init parameter named "packages". <b>Be
 * careful</b>, however, to expose any packages that may have sensitive information, such as properties file with
 * database access credentials.
 * </p>
 */
public class DefaultStaticContentLoader implements StaticContentLoader {

    protected static final String WEBJARS_REQUEST_PREFIX = "/webjars/";

    /**
     * Provide a logging instance.
     */
    private final Logger LOG = LogManager.getLogger(DefaultStaticContentLoader.class);

    /**
     * Store set of path prefixes to use with static resources.
     */
    protected List<String> pathPrefixes;

    /**
     * Store state of StrutsConstants.STRUTS_SERVE_STATIC_CONTENT setting.
     */
    protected boolean serveStatic;

    /**
     * Store state of {@link StrutsConstants#STRUTS_UI_STATIC_CONTENT_PATH} setting.
     */
    protected String uiStaticContentPath;

    /**
     * Store state of StrutsConstants.STRUTS_SERVE_STATIC_BROWSER_CACHE setting.
     */
    protected boolean serveStaticBrowserCache;

    /**
     * Provide a formatted date for setting heading information when caching static content.
     */
    protected final Calendar lastModifiedCal = Calendar.getInstance();

    protected boolean devMode;

    protected WebJarUrlProvider webJarUrlProvider;

    @Inject
    public void setWebJarUrlProvider(WebJarUrlProvider webJarUrlProvider) {
        this.webJarUrlProvider = webJarUrlProvider;
    }

    /**
     * Modify state of StrutsConstants.STRUTS_SERVE_STATIC_CONTENT setting.
     *
     * @param serveStaticContent New setting
     */
    @Inject(StrutsConstants.STRUTS_SERVE_STATIC_CONTENT)
    public void setServeStaticContent(String serveStaticContent) {
        this.serveStatic = BooleanUtils.toBoolean(serveStaticContent);
    }

    @Inject(StrutsConstants.STRUTS_UI_STATIC_CONTENT_PATH)
    public void setStaticContentPath(String uiStaticContentPath) {
        this.uiStaticContentPath = StaticContentLoader.Validator.validateStaticContentPath(uiStaticContentPath);
    }

    /**
     * Modify state of StrutsConstants.STRUTS_SERVE_STATIC_BROWSER_CACHE
     * setting.
     *
     * @param serveStaticBrowserCache New setting
     */
    @Inject(StrutsConstants.STRUTS_SERVE_STATIC_BROWSER_CACHE)
    public void setServeStaticBrowserCache(String serveStaticBrowserCache) {
        this.serveStaticBrowserCache = BooleanUtils.toBoolean(serveStaticBrowserCache);
    }

    @Inject(StrutsConstants.STRUTS_DEVMODE)
    public void setDevMode(String devMode) {
        this.devMode = Boolean.parseBoolean(devMode);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.struts2.dispatcher.StaticResourceLoader#setHostConfig(jakarta.servlet.FilterConfig)
     */
    public void setHostConfig(HostConfig filterConfig) {
        String param = filterConfig.getInitParameter("packages");
        String packages = getAdditionalPackages();
        if (param != null) {
            packages = param + " " + packages;
        }
        this.pathPrefixes = parse(packages);
    }

    protected String getAdditionalPackages() {
        List<String> packages = new LinkedList<>();
        packages.add("org.apache.struts2.static");
        packages.add("template");
        packages.add("static");

        if (devMode) {
            packages.add("org.apache.struts2.interceptor.debugging");
        }

        return StringUtils.join(packages.iterator(), ' ');
    }

    /**
     * Create a string array from a comma-delimited list of packages.
     *
     * @param packages A comma-delimited String listing packages
     * @return A string array of packages
     */
    protected List<String> parse(String packages) {
        if (packages == null) {
            return Collections.emptyList();
        }
        List<String> pathPrefixes = new ArrayList<>();

        StringTokenizer st = new StringTokenizer(packages, ", \n\t");
        while (st.hasMoreTokens()) {
            String pathPrefix = st.nextToken().replace('.', '/');
            if (!pathPrefix.endsWith("/")) {
                pathPrefix += "/";
            }
            pathPrefixes.add(pathPrefix);
        }

        return pathPrefixes;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.struts2.dispatcher.StaticResourceLoader#findStaticResource(java.lang.String,
     *      jakarta.servlet.http.HttpServletRequest,
     *      jakarta.servlet.http.HttpServletResponse)
     */
    public void findStaticResource(String path, HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        String name = cleanupPath(path);

        if (Validator.containsMalformedPathSegment(name)) {
            LOG.debug("Rejecting static resource request with malformed path segment");
            sendNotFound(response);
            return;
        }

        if (name.startsWith(WEBJARS_REQUEST_PREFIX)) {
            if (!findWebJarResource(name, path, request, response)) {
                sendNotFound(response);
            }
            return;
        }

        for (String pathPrefix : pathPrefixes) {
            URL resourceUrl = findResource(buildPath(name, pathPrefix));
            if (resourceUrl != null) {
                InputStream is = null;
                try {
                    //check that the resource path is under the pathPrefix path
                    String pathEnding = buildPath(name, pathPrefix);
                    if (resourceUrl.getFile().endsWith(pathEnding))
                        is = resourceUrl.openStream();
                } catch (IOException ex) {
                    // just ignore it
                    continue;
                }

                //not inside the try block, as this could throw IOExceptions also
                if (is != null) {
                    process(is, path, request, response);
                    return;
                }
            }
        }

        sendNotFound(response);
    }

    /**
     * Resolve and serve a WebJar asset requested under {@code <staticContentPath>/webjars/**}.
     *
     * @param name    the request path with the static-content prefix stripped, e.g. {@code /webjars/jquery/jquery.min.js}
     * @param path    the original request path (used for content-type detection)
     * @return true if the asset was resolved and streamed; false otherwise (caller sends 404)
     */
    protected boolean findWebJarResource(String name, String path, HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        String logicalPath = name.substring(WEBJARS_REQUEST_PREFIX.length());
        Optional<String> resource = webJarUrlProvider.resolveResourcePath(logicalPath);
        if (resource.isEmpty()) {
            return false;
        }
        URL resourceUrl = findResource(resource.get());
        if (resourceUrl == null) {
            return false;
        }
        process(resourceUrl.openStream(), path, request, response);
        return true;
    }

    protected void sendNotFound(HttpServletResponse response) {
        try {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (IOException e1) {
            // we're already sending an error, not much else we can do if more stuff breaks
            LOG.warn("Unable to send error response, code: {};", HttpServletResponse.SC_NOT_FOUND, e1);
        } catch (IllegalStateException ise) {
            // Log illegalstate instead of passing unrecoverable exception to calling thread
            LOG.warn("Unable to send error response, code: {}; isCommitted: {};", HttpServletResponse.SC_NOT_FOUND, response.isCommitted(), ise);
        }
    }

    protected void process(InputStream is, String path, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (is != null) {
            Calendar cal = Calendar.getInstance();

            // check for if-modified-since, prior to any other headers
            long ifModifiedSince = 0;
            try {
                ifModifiedSince = request.getDateHeader("If-Modified-Since");
            } catch (Exception e) {
                LOG.warn("Invalid If-Modified-Since header value: '{}', ignoring", request.getHeader("If-Modified-Since"));
            }
            long lastModifiedMillis = lastModifiedCal.getTimeInMillis();
            long now = cal.getTimeInMillis();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            long expires = cal.getTimeInMillis();

            if (ifModifiedSince > 0 && ifModifiedSince <= lastModifiedMillis) {
                // not modified, content is not sent - only basic
                // headers and status SC_NOT_MODIFIED
                response.setDateHeader("Expires", expires);
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                is.close();
                return;
            }

            // set the content-type header
            String contentType = getContentType(path);
            if (contentType != null) {
                response.setContentType(contentType);
            }

            if (serveStaticBrowserCache) {
                // set heading information for caching static content
                response.setDateHeader("Date", now);
                response.setDateHeader("Expires", expires);
                response.setDateHeader("Retry-After", expires);
                response.setHeader("Cache-Control", "public");
                response.setDateHeader("Last-Modified", lastModifiedMillis);
            } else {
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Expires", "-1");
            }

            try (is) {
                copy(is, response.getOutputStream());
            }
        }
    }

    /**
     * Look for a static resource in the classpath.
     *
     * @param path The resource path
     * @return The URL of the resource
     * @throws IOException If there is a problem locating the resource
     */
    protected URL findResource(String path) throws IOException {
        return ClassLoaderUtil.getResource(path, getClass());
    }

    /**
     * @param name          resource name
     * @param packagePrefix The package prefix to use to locate the resource
     * @return full path
     */
    protected String buildPath(String name, String packagePrefix) {
        if (packagePrefix.endsWith("/") && name.startsWith("/")) {
            return packagePrefix + name.substring(1);
        }
        return packagePrefix + name;
    }


    /**
     * Maps a lower-case file extension to its content type. Not using the code provided by
     * activation.jar to avoid adding yet another dependency; this covers the files we serve up
     * (Struts' own static assets plus WebJar assets).
     */
    private static final Map<String, String> CONTENT_TYPES = Map.ofEntries(
        Map.entry("js", "text/javascript"),
        Map.entry("mjs", "text/javascript"),
        Map.entry("css", "text/css"),
        Map.entry("html", "text/html"),
        Map.entry("txt", "text/plain"),
        Map.entry("gif", "image/gif"),
        Map.entry("jpg", "image/jpeg"),
        Map.entry("jpeg", "image/jpeg"),
        Map.entry("png", "image/png"),
        Map.entry("svg", "image/svg+xml"),
        Map.entry("ico", "image/x-icon"),
        Map.entry("woff2", "font/woff2"),
        Map.entry("woff", "font/woff"),
        Map.entry("ttf", "font/ttf"),
        Map.entry("otf", "font/otf"),
        Map.entry("eot", "application/vnd.ms-fontobject"),
        Map.entry("json", "application/json"),
        Map.entry("map", "application/json"));

    /**
     * Determine the content type for the resource name.
     *
     * @param name The resource name
     * @return The mime type, or {@code null} if the extension is unknown
     */
    protected String getContentType(String name) {
        int dot = name.lastIndexOf('.');
        if (dot < 0) {
            return null;
        }
        return CONTENT_TYPES.get(name.substring(dot + 1));
    }

    /**
     * Copy bytes from the input stream to the output stream.
     *
     * @param input  The input stream
     * @param output The output stream
     * @throws IOException If anything goes wrong
     */
    protected void copy(InputStream input, OutputStream output) throws IOException {
        final byte[] buffer = new byte[4096];
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        output.flush();
    }

    public boolean canHandle(String resourcePath) {
        return serveStatic && resourcePath.startsWith(uiStaticContentPath + "/");
    }

    /**
     * @param path requested path
     * @return path without leading {@link #uiStaticContentPath}
     */
    protected String cleanupPath(String path) {
        if (path.startsWith(uiStaticContentPath)) {
            return path.substring(uiStaticContentPath.length());
        } else {
            return path;
        }
    }
}
