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
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.ng.HostConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

public class DefaultStaticContentLoader implements StaticContentLoader {
    /**
     * Provide a logging instance.
     */
    private Logger log;

    /**
     * Store set of path prefixes to use with static resources.
     */
    protected String[] pathPrefixes;

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
     * @param val
     *            New setting
     */
    @Inject(StrutsConstants.STRUTS_SERVE_STATIC_CONTENT)
    public void setServeStaticContent(String val) {
        serveStatic = "true".equals(val);
    }

    /**
     * Modify state of StrutsConstants.STRUTS_SERVE_STATIC_BROWSER_CACHE
     * setting.
     *
     * @param val
     *            New setting
     */
    @Inject(StrutsConstants.STRUTS_SERVE_STATIC_BROWSER_CACHE)
    public void setServeStaticBrowserCache(String val) {
        serveStaticBrowserCache = "true".equals(val);
    }

    /**
     * Modify state of StrutsConstants.STRUTS_I18N_ENCODING setting.
     * @param val New setting
     */
    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public void setEncoding(String val) {
        encoding = val;
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
        initLogging(filterConfig);
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
    protected String[] parse(String packages) {
        if (packages == null) {
            return null;
        }
        List<String> pathPrefixes = new ArrayList<String>();

        StringTokenizer st = new StringTokenizer(packages, ", \n\t");
        while (st.hasMoreTokens()) {
            String pathPrefix = st.nextToken().replace('.', '/');
            if (!pathPrefix.endsWith("/")) {
                pathPrefix += "/";
            }
            pathPrefixes.add(pathPrefix);
        }

        return pathPrefixes.toArray(new String[pathPrefixes.size()]);
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
                log.warn("Invalid If-Modified-Since header value: '"
                        + request.getHeader("If-Modified-Since") + "', ignoring");
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
            return;
        }
    }

    private void initLogging(HostConfig filterConfig) {
        String factoryName = filterConfig.getInitParameter("loggerFactory");
        if (factoryName != null) {
            try {
                Class cls = ClassLoaderUtil.loadClass(factoryName, this.getClass());
                LoggerFactory fac = (LoggerFactory)cls.newInstance();
                LoggerFactory.setLoggerFactory(fac);
            } catch (InstantiationException e) {
                System.err.println("Unable to instantiate logger factory: "+factoryName+", using default");
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                System.err.println("Unable to access logger factory: "+factoryName+", using default");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.err.println("Unable to locate logger factory class: "+factoryName+", using default");
                e.printStackTrace();
            }
        }

        log = LoggerFactory.getLogger(FilterDispatcher.class);

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
     * @throws UnsupportedEncodingException
     * @throws IOException
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
