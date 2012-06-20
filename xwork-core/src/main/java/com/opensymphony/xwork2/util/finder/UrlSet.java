/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.util.finder;

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Use with ClassFinder to filter the Urls to be scanned, example:
 * <pre>
 * UrlSet urlSet = new UrlSet(classLoader);
 * urlSet = urlSet.exclude(ClassLoader.getSystemClassLoader().getParent());
 * urlSet = urlSet.excludeJavaExtDirs();
 * urlSet = urlSet.excludeJavaEndorsedDirs();
 * urlSet = urlSet.excludeJavaHome();
 * urlSet = urlSet.excludePaths(System.getProperty("sun.boot.class.path", ""));
 * urlSet = urlSet.exclude(".*?/JavaVM.framework/.*");
 * urlSet = urlSet.exclude(".*?/activemq-(core|ra)-[\\d.]+.jar(!/)?");
 * </pre>
 * @author David Blevins
 * @version $Rev$ $Date$
 */
public class UrlSet {
    private static final Logger LOG = LoggerFactory.getLogger(UrlSet.class);
    private final Map<String,URL> urls;
    private Set<String> protocols;
    private FileManager fileManager;

    public UrlSet(FileManager fileManager, ClassLoaderInterface classLoader) throws IOException {
        this(fileManager);
        load(getUrls(classLoader));
    }

    public UrlSet(FileManager fileManager, ClassLoaderInterface classLoader, Set<String> protocols) throws IOException {
        this(fileManager);
        this.protocols = protocols;
        this.fileManager = fileManager;
        load(getUrls(classLoader, protocols));
    }

    /**
     * Ignores all URLs that are not "jar" or "file"
     * @param urls
     */
    public UrlSet(FileManager fileManager, Collection<URL> urls){
        this(fileManager);
        load(urls);
    }

    private UrlSet(FileManager fileManager) {
        this.urls = new HashMap<String,URL>();
        this.fileManager = fileManager;
    }

    private UrlSet(FileManager fileManager, Map<String, URL> urls) {
        this.urls = urls;
        this.fileManager = fileManager;
    }

    private void load(Collection<URL> urls){
        for (URL location : urls) {
            try {
                this.urls.put(location.toExternalForm(), location);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public UrlSet include(UrlSet urlSet){
        Map<String, URL> urls = new HashMap<String, URL>(this.urls);
        urls.putAll(urlSet.urls);
        return new UrlSet(fileManager, urls);
    }

    public UrlSet exclude(UrlSet urlSet) {
        Map<String, URL> urls = new HashMap<String, URL>(this.urls);
        Map<String, URL> parentUrls = urlSet.urls;
        for (String url : parentUrls.keySet()) {
            urls.remove(url);
        }
        return new UrlSet(fileManager, urls);
    }

    public UrlSet exclude(ClassLoaderInterface parent) throws IOException {
        return exclude(new UrlSet(fileManager, parent, this.protocols));
    }

    public UrlSet exclude(File file) throws MalformedURLException {
        return exclude(relative(file));
    }

    public UrlSet exclude(String pattern) throws MalformedURLException {
        return exclude(matching(pattern));
    }

    /**
     * Calls excludePaths(System.getProperty("java.ext.dirs"))
     * @return
     * @throws MalformedURLException
     */
    public UrlSet excludeJavaExtDirs() throws MalformedURLException {
        return excludePaths(System.getProperty("java.ext.dirs", ""));
    }

    /**
     * Calls excludePaths(System.getProperty("java.endorsed.dirs"))
     *
     * @return
     * @throws MalformedURLException
     */
    public UrlSet excludeJavaEndorsedDirs() throws MalformedURLException {
        return excludePaths(System.getProperty("java.endorsed.dirs", ""));
    }

    public UrlSet excludeJavaHome() throws MalformedURLException {
        String path = System.getProperty("java.home");
        if (path != null) {

            File java = new File(path);

            if (path.matches("/System/Library/Frameworks/JavaVM.framework/Versions/[^/]+/Home")){
                java = java.getParentFile();
            }
            return exclude(java);
        } else {
            return this;
        }
    }

    public UrlSet excludePaths(String pathString) throws MalformedURLException {
        String[] paths = pathString.split(File.pathSeparator);
        UrlSet urlSet = this;
        for (String path : paths) {
            if (StringUtils.isNotEmpty(path)) {
                File file = new File(path);
                urlSet = urlSet.exclude(file);
            }
        }
        return urlSet;
    }

    public UrlSet matching(String pattern) {
        Map<String, URL> urls = new HashMap<String, URL>();
        for (Map.Entry<String, URL> entry : this.urls.entrySet()) {
            String url = entry.getKey();
            if (url.matches(pattern)){
                urls.put(url, entry.getValue());
            }
        }
        return new UrlSet(fileManager, urls);
    }

    /**
     * Try to find a classes directory inside a war file add its normalized url to this set
     */
    public UrlSet includeClassesUrl(ClassLoaderInterface classLoaderInterface) throws IOException {
        Enumeration<URL> rootUrlEnumeration = classLoaderInterface.getResources("");
        while (rootUrlEnumeration.hasMoreElements()) {
            URL url = rootUrlEnumeration.nextElement();
            String externalForm = StringUtils.removeEnd(url.toExternalForm(), "/");
            if (externalForm.endsWith(".war/WEB-INF/classes")) {
                //if it is inside a war file, get the url to the file
                externalForm = StringUtils.substringBefore(externalForm, "/WEB-INF/classes");
                URL warUrl = new URL(externalForm);
                URL normalizedUrl = fileManager.normalizeToFileProtocol(warUrl);
                URL finalUrl = ObjectUtils.defaultIfNull(normalizedUrl, warUrl);

                Map<String, URL> newUrls = new HashMap<String, URL>(this.urls);
                if ("jar".equals(finalUrl.getProtocol()) || "file".equals(finalUrl.getProtocol())) {
                    newUrls.put(finalUrl.toExternalForm(), finalUrl);
                }
                return new UrlSet(fileManager, newUrls);
            }
        }

        return this;
    }

    public UrlSet relative(File file) throws MalformedURLException {
        String urlPath = file.toURL().toExternalForm();
        Map<String, URL> urls = new HashMap<String, URL>();
        for (Map.Entry<String, URL> entry : this.urls.entrySet()) {
            String url = entry.getKey();
            if (url.startsWith(urlPath) || url.startsWith("jar:"+urlPath)){
                urls.put(url, entry.getValue());
            }
        }
        return new UrlSet(fileManager, urls);
    }

    public List<URL> getUrls() {
        return new ArrayList<URL>(urls.values());
    }

    private List<URL> getUrls(ClassLoaderInterface classLoader) throws IOException {
        List<URL> list = new ArrayList<URL>();

        //find jars
        ArrayList<URL> urls = Collections.list(classLoader.getResources("META-INF"));

        for (URL url : urls) {
            if ("jar".equalsIgnoreCase(url.getProtocol())) {
                String externalForm = url.toExternalForm();
                //build a URL pointing to the jar, instead of the META-INF dir
                url = new URL(StringUtils.substringBefore(externalForm, "META-INF"));
                list.add(url);
            } else if (LOG.isDebugEnabled())
                LOG.debug("Ignoring URL [#0] because it is not a jar", url.toExternalForm());

        }

        //usually the "classes" dir
        list.addAll(Collections.list(classLoader.getResources("")));
        return list;
    }

    private List<URL> getUrls(ClassLoaderInterface classLoader, Set<String> protocols) throws IOException {

        if (protocols == null) {
            return getUrls(classLoader);
        }

        List<URL> list = new ArrayList<URL>();

        //find jars
        ArrayList<URL> urls = Collections.list(classLoader.getResources("META-INF"));

        for (URL url : urls) {
            if (protocols.contains(url.getProtocol())) {
                String externalForm = url.toExternalForm();
                //build a URL pointing to the jar, instead of the META-INF dir
                url = new URL(StringUtils.substringBefore(externalForm, "META-INF"));
                list.add(url);
            } else if (LOG.isDebugEnabled())
                LOG.debug("Ignoring URL [#0] because it is not a valid protocol", url.toExternalForm());

        }

        // Usually the "classes" dir.
        ArrayList<URL> classesList = Collections.list(classLoader.getResources(""));
        for (URL url : classesList) {
            list.addAll(fileManager.getAllPhysicalUrls(url));
        }

        return list;
    }

}
