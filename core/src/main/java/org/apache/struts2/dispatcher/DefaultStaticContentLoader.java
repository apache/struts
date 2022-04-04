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
package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

/**
 * <p>
 * <b>Default implementation to server static content</b>
 * </p>
 *
 * <p>
 * This class is used to serve common static content needed when using various parts of Struts, such as JavaScript
 * files, CSS files, etc. It works by looking for requests to /struts/* (or /static/*), and then mapping the value after "/struts/"
 * to common packages in Struts and, optionally, in your class path. By default, the following packages are
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
 * This means that you can simply request /struts/xhtml/styles.css and the XHTML UI theme's default stylesheet
 * will be returned. Likewise, many of the AJAX UI components require various JavaScript files, which are found in the
 * org.apache.struts2.static package. If you wish to add additional packages to be searched, you can add a comma
 * separated (space, tab and new line will do as well) list in the filter init parameter named "packages". <b>Be
 * careful</b>, however, to expose any packages that may have sensitive information, such as properties file with
 * database access credentials.
 * </p>
 */
public class DefaultStaticContentLoader implements StaticContentLoader {

    /**
     * Provide a logging instance.
     */
    private Logger LOG = LogManager.getLogger(DefaultStaticContentLoader.class);

    /**
     * Store set of path prefixes to use with static resources.
     */
    protected List<String> pathPrefixes;

    /**
     * Store state of StrutsConstants.STRUTS_SERVE_STATIC_CONTENT setting.
     */
    protected boolean serveStatic;

    /**
     * Store state of StrutsConstants.STRUTS_SERVE_STATIC_BROWSER_CACHE setting.
     */
    protected boolean serveStaticBrowserCache;

    /**
     * Provide a formatted date for setting heading information when caching static content.
     */
    protected final Calendar lastModifiedCal = Calendar.getInstance();

    /**
     * Store state of StrutsConstants.STRUTS_I18N_ENCODING setting.
     */
    protected String encoding;


    /**
     * Modify state of StrutsConstants.STRUTS_SERVE_STATIC_CONTENT setting.
     *
     * @param serveStaticContent
     *            New setting
     */
    @Inject(StrutsConstants.STRUTS_SERVE_STATIC_CONTENT)
    public void setServeStaticContent(String serveStaticContent) {
        this.serveStatic = BooleanUtils.toBoolean(serveStaticContent);
    }

    /**
     * Modify state of StrutsConstants.STRUTS_SERVE_STATIC_BROWSER_CACHE
     * setting.
     *
     * @param serveStaticBrowserCache
     *            New setting
     */
    @Inject(StrutsConstants.STRUTS_SERVE_STATIC_BROWSER_CACHE)
    public void setServeStaticBrowserCache(String serveStaticBrowserCache) {
        this.serveStaticBrowserCache = BooleanUtils.toBoolean(serveStaticBrowserCache);
    }

    /**
     * Modify state of StrutsConstants.STRUTS_I18N_ENCODING setting.
     * @param encoding New setting
     */
    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.struts2.dispatcher.StaticResourceLoader#setHostConfig(javax.servlet.FilterConfig)
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
        return "org.apache.struts2.static template org.apache.struts2.interceptor.debugging static";
    }

    /**
     * Create a string array from a comma-delimited list of packages.
     *
     * @param packages
     *            A comma-delimited String listing packages
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
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public void findStaticResource(String path, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String name = cleanupPath(path);
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

        response.sendError(HttpServletResponse.SC_NOT_FOUND);
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

            try {
                copy(is, response.getOutputStream());
            } finally {
                is.close();
            }
        }
    }

    /**
     * Look for a static resource in the classpath.
     *
     * @param path The resource path
     * @return The inputstream of the resource
     * @throws IOException If there is a problem locating the resource
     */
    protected URL findResource(String path) throws IOException {
        return ClassLoaderUtil.getResource(path, getClass());
    }

    /**
     * @param name resource name
     * @param packagePrefix The package prefix to use to locate the resource
     * @return full path
     * @throws UnsupportedEncodingException If there is a encoding problem
     */
    protected String buildPath(String name, String packagePrefix) throws UnsupportedEncodingException {
        String resourcePath;
        if (packagePrefix.endsWith("/") && name.startsWith("/")) {
            resourcePath = packagePrefix + name.substring(1);
        } else {
            resourcePath = packagePrefix + name;
        }

        return URLDecoder.decode(resourcePath, encoding);
    }



    /**
     * Determine the content type for the resource name.
     *
     * @param name The resource name
     * @return The mime type
     */
    protected String getContentType(String name) {
        // NOT using the code provided activation.jar to avoid adding yet another dependency
        // this is generally OK, since these are the main files we server up
        if (name.endsWith(".js")) {
            return "text/javascript";
        } else if (name.endsWith(".css")) {
            return "text/css";
        } else if (name.endsWith(".html")) {
            return "text/html";
        } else if (name.endsWith(".txt")) {
            return "text/plain";
        } else if (name.endsWith(".gif")) {
            return "image/gif";
        } else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (name.endsWith(".png")) {
            return "image/png";
        } else {
            return null;
        }
    }

    /**
     * Copy bytes from the input stream to the output stream.
     *
     * @param input
     *            The input stream
     * @param output
     *            The output stream
     * @throws IOException
     *             If anything goes wrong
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
        return serveStatic && (resourcePath.startsWith("/struts/") || resourcePath.startsWith("/static/"));
    }

    /**
     * @param path requested path
     * @return path without leading "/struts" or "/static"
     */
    protected String cleanupPath(String path) {
        //path will start with "/struts" or "/static", remove them
        return path.substring(7);
    }
}
