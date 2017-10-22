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
package com.opensymphony.xwork2.util.finder;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author David Blevins
 */
public class ResourceFinder {
    private static final Logger LOG = LogManager.getLogger(ResourceFinder.class);

    private final URL[] urls;
    private final String path;
    private final ClassLoaderInterface classLoaderInterface;
    private final List<String> resourcesNotLoaded = new ArrayList<>();

    public ResourceFinder(URL... urls) {
        this(null, new ClassLoaderInterfaceDelegate(Thread.currentThread().getContextClassLoader()), urls);
    }

    public ResourceFinder(String path) {
        this(path, new ClassLoaderInterfaceDelegate(Thread.currentThread().getContextClassLoader()), null);
    }

    public ResourceFinder(String path, URL... urls) {
        this(path, new ClassLoaderInterfaceDelegate(Thread.currentThread().getContextClassLoader()), urls);
    }

    public ResourceFinder(String path, ClassLoaderInterface classLoaderInterface) {
        this(path, classLoaderInterface, null);
    }

    public ResourceFinder(String path, ClassLoaderInterface classLoaderInterface, URL... urls) {
        path = StringUtils.trimToEmpty(path);
        if (!StringUtils.endsWith(path, "/")) {
            path += "/";
        }
        this.path = path;

        this.classLoaderInterface = classLoaderInterface == null ? new ClassLoaderInterfaceDelegate(Thread.currentThread().getContextClassLoader()) : classLoaderInterface ;

        for (int i = 0; urls != null && i < urls.length; i++) {
            URL url = urls[i];
            if (url == null || isDirectory(url) || "jar".equals(url.getProtocol())) {
                continue;
            }
            try {
                urls[i] = new URL("jar", "", -1, url.toString() + "!/");
            } catch (MalformedURLException e) {
            }
        }
        this.urls = (urls == null || urls.length == 0)? null : urls;
    }

    private static boolean isDirectory(URL url) {
        String file = url.getFile();
        return (file.length() > 0 && file.charAt(file.length() - 1) == '/');
    }

    /**
     * <p>
     * Returns a list of resources that could not be loaded in the last invoked findAvailable* or
     * mapAvailable* methods.
     * </p>
     *
     * <p>
     * The list will only contain entries of resources that match the requirements
     * of the last invoked findAvailable* or mapAvailable* methods, but were unable to be
     * loaded and included in their results.
     * </p>
     *
     * <p>
     * The list returned is unmodifiable and the results of this method will change
     * after each invocation of a findAvailable* or mapAvailable* methods.
     * </p>
     *
     * This method is not thread safe.
     *
     * @return not loaded resources
     */
    public List<String> getResourcesNotLoaded() {
        return Collections.unmodifiableList(resourcesNotLoaded);
    }

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //
    //   Find
    //
    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    public URL find(String uri) throws IOException {
        String fullUri = path + uri;

        return getResource(fullUri);
    }

    public List<URL> findAll(String uri) throws IOException {
        String fullUri = path + uri;

        Enumeration<URL> resources = getResources(fullUri);
        List<URL> list = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            list.add(url);
        }
        return list;
    }


    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //
    //   Find String
    //
    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    /**
     * Reads the contents of the URL as a {@link String}'s and returns it.
     *
     * @param uri URL
     * @return a stringified content of a resource
     * @throws IOException if a resource pointed out by the uri param could not be find
     * @see ClassLoader#getResource(String)
     */
    public String findString(String uri) throws IOException {
        String fullUri = path + uri;

        URL resource = getResource(fullUri);
        if (resource == null) {
            throw new IOException("Could not find a resource in: " + fullUri);
        }

        return readContents(resource);
    }

    /**
     * Reads the contents of the found URLs as a list of {@link String}'s and returns them.
     *
     * @param uri URL
     * @return a list of the content of each resource URL found
     * @throws IOException if any of the found URLs are unable to be read.
     */
    public List<String> findAllStrings(String uri) throws IOException {
        String fulluri = path + uri;

        List<String> strings = new ArrayList<>();

        Enumeration<URL> resources = getResources(fulluri);
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            String string = readContents(url);
            strings.add(string);
        }
        return strings;
    }

    /**
     * Reads the contents of the found URLs as a Strings and returns them.
     * Individual URLs that cannot be read are skipped and added to the
     * list of 'resourcesNotLoaded'
     *
     * @param uri URL
     * @return a list of the content of each resource URL found
     * @throws IOException if classLoader.getResources throws an exception
     */
    public List<String> findAvailableStrings(String uri) throws IOException {
        resourcesNotLoaded.clear();
        String fulluri = path + uri;

        List<String> strings = new ArrayList<>();

        Enumeration<URL> resources = getResources(fulluri);
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            try {
                String string = readContents(url);
                strings.add(string);
            } catch (IOException notAvailable) {
                resourcesNotLoaded.add(url.toExternalForm());
            }
        }
        return strings;
    }

    /**
     * <p>
     * Reads the contents of all non-directory URLs immediately under the specified
     * location and returns them in a map keyed by the file name.
     * </p>
     *
     * <p>
     * Any URLs that cannot be read will cause an exception to be thrown.
     * </p>
     *
     * <p>
     * Example classpath:
     * </p>
     *
     * <pre>
     * META-INF/serializables/one
     * META-INF/serializables/two
     * META-INF/serializables/three
     * META-INF/serializables/four/foo.txt
     * </pre>
     *
     * <pre>
     * ResourceFinder finder = new ResourceFinder("META-INF/");
     * Map map = finder.mapAvailableStrings("serializables");
     * map.contains("one");  // true
     * map.contains("two");  // true
     * map.contains("three");  // true
     * map.contains("four");  // false
     * </pre>
     *
     * @param uri URL
     * @return a list of the content of each resource URL found
     * @throws IOException if any of the urls cannot be read
     */
    public Map<String, String> mapAllStrings(String uri) throws IOException {
        Map<String, String> strings = new HashMap<>();
        Map<String, URL> resourcesMap = getResourcesMap(uri);
        for (Map.Entry<String, URL> entry : resourcesMap.entrySet()) {
            String name = entry.getKey();
            URL url = entry.getValue();
            String value = readContents(url);
            strings.put(name, value);
        }
        return strings;
    }

    /**
     * <p>
     * Reads the contents of all non-directory URLs immediately under the specified
     * location and returns them in a map keyed by the file name.
     * </p>
     *
     * <p>
     * Individual URLs that cannot be read are skipped and added to the
     * list of 'resourcesNotLoaded'
     * </p>
     *
     * <p>
     * Example classpath:
     * </p>
     *
     * <pre>
     * META-INF/serializables/one
     * META-INF/serializables/two      # not readable
     * META-INF/serializables/three
     * META-INF/serializables/four/foo.txt
     * </pre>
     *
     * <pre>
     * ResourceFinder finder = new ResourceFinder("META-INF/");
     * Map map = finder.mapAvailableStrings("serializables");
     * map.contains("one");  // true
     * map.contains("two");  // false
     * map.contains("three");  // true
     * map.contains("four");  // false
     * </pre>
     *
     * @param uri URL
     * @return a list of the content of each resource URL found
     * @throws IOException if classLoader.getResources throws an exception
     */
    public Map<String, String> mapAvailableStrings(String uri) throws IOException {
        resourcesNotLoaded.clear();
        Map<String, String> strings = new HashMap<>();
        Map<String, URL> resourcesMap = getResourcesMap(uri);
        for (Map.Entry<String, URL> entry  : resourcesMap.entrySet()) {
            String name = entry.getKey();
            URL url = entry.getValue();
            try {
                String value = readContents(url);
                strings.put(name, value);
            } catch (IOException notAvailable) {
                resourcesNotLoaded.add(url.toExternalForm());
            }
        }
        return strings;
    }

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //
    //   Find Class
    //
    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    /**
     * Executes {@link #findString(String)} assuming the contents URL found is the name of
     * a class that should be loaded and returned.
     *
     * @param uri URL
     * @return class that should be loaded
     * @throws IOException in case of IO errors
     * @throws ClassNotFoundException when class is not found
     */
    public Class findClass(String uri) throws IOException, ClassNotFoundException {
        String className = findString(uri);
        return (Class) classLoaderInterface.loadClass(className);
    }

    /**
     * <p>
     * Executes findAllStrings assuming the strings are
     * the names of a classes that should be loaded and returned.
     * </p>
     *
     * <p>
     * Any URL or class that cannot be loaded will cause an exception to be thrown.
     * </p>
     *
     * @param uri URL
     * @return classes that should be loaded
     * @throws IOException in case of IO errors
     * @throws ClassNotFoundException when class is not found
     */
    public List<Class> findAllClasses(String uri) throws IOException, ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        List<String> strings = findAllStrings(uri);
        for (String className : strings) {
            Class clazz = classLoaderInterface.loadClass(className);
            classes.add(clazz);
        }
        return classes;
    }

    /**
     * <p>
     * Executes findAvailableStrings assuming the strings are
     * the names of a classes that should be loaded and returned.
     * </p>
     *
     * <p>
     * Any class that cannot be loaded will be skipped and placed in the
     * 'resourcesNotLoaded' collection.
     * </p>
     *
     * @param uri URL
     * @return list of available classes
     * @throws IOException if classLoader.getResources throws an exception
     */
    public List<Class> findAvailableClasses(String uri) throws IOException {
        resourcesNotLoaded.clear();
        List<Class> classes = new ArrayList<>();
        List<String> strings = findAvailableStrings(uri);
        for (String className : strings) {
            try {
                Class clazz = classLoaderInterface.loadClass(className);
                classes.add(clazz);
            } catch (Exception notAvailable) {
                resourcesNotLoaded.add(className);
            }
        }
        return classes;
    }

    /**
     * <p>
     * Executes mapAllStrings assuming the value of each entry in the
     * map is the name of a class that should be loaded.
     * </p>
     *
     * <p>
     * Any class that cannot be loaded will be cause an exception to be thrown.
     * </p>
     *
     * <p>
     * Example classpath:
     * </p>
     * <pre>
     * META-INF/xmlparsers/xerces
     * META-INF/xmlparsers/crimson
     * </pre>
     * <pre>
     * ResourceFinder finder = new ResourceFinder("META-INF/");
     * Map map = finder.mapAvailableStrings("xmlparsers");
     * map.contains("xerces");  // true
     * map.contains("crimson");  // true
     * Class xercesClass = map.get("xerces");
     * Class crimsonClass = map.get("crimson");
     * </pre>
     *
     * @param uri URL
     * @return map of all classes
     * @throws IOException in case of IO errors
     * @throws ClassNotFoundException when class is not found
     */
    public Map<String, Class> mapAllClasses(String uri) throws IOException, ClassNotFoundException {
        Map<String, Class> classes = new HashMap<>();
        Map<String, String> map = mapAllStrings(uri);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String string = entry.getKey();
            String className = entry.getValue();
            Class clazz = classLoaderInterface.loadClass(className);
            classes.put(string, clazz);
        }
        return classes;
    }

    /**
     * <p>
     * Executes mapAvailableStrings assuming the value of each entry in the
     * map is the name of a class that should be loaded.
     * </p>
     *
     * <p>
     * Any class that cannot be loaded will be skipped and placed in the
     * 'resourcesNotLoaded' collection.
     * </p>
     *
     * <p>
     * Example classpath:
     * </p>
     * <pre>
     * META-INF/xmlparsers/xerces
     * META-INF/xmlparsers/crimson
     * </pre>
     * <pre>
     * ResourceFinder finder = new ResourceFinder("META-INF/");
     * Map map = finder.mapAvailableStrings("xmlparsers");
     * map.contains("xerces");  // true
     * map.contains("crimson");  // true
     * Class xercesClass = map.get("xerces");
     * Class crimsonClass = map.get("crimson");
     * </pre>
     *
     * @param uri URL
     * @return map of available classes
     * @throws IOException if classLoader.getResources throws an exception
     */
    public Map<String, Class> mapAvailableClasses(String uri) throws IOException {
        resourcesNotLoaded.clear();
        Map<String, Class> classes = new HashMap<>();
        Map<String, String> map = mapAvailableStrings(uri);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String string = entry.getKey();
            String className = entry.getValue();
            try {
                Class clazz = classLoaderInterface.loadClass(className);
                classes.put(string, clazz);
            } catch (Exception notAvailable) {
                resourcesNotLoaded.add(className);
            }
        }
        return classes;
    }

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //
    //   Find Implementation
    //
    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    /**
     * <p>
     * Assumes the class specified points to a file in the classpath that contains
     * the name of a class that implements or is a subclass of the specfied class.
     * </p>
     *
     * <p>
     * Any class that cannot be loaded will be cause an exception to be thrown.
     * </p>
     *
     * <p>
     * Example classpath:
     * </p>
     *
     * <pre>
     * META-INF/java.io.InputStream    # contains the classname org.acme.AcmeInputStream
     * META-INF/java.io.OutputStream
     * </pre>
     * <pre>
     * ResourceFinder finder = new ResourceFinder("META-INF/");
     * Class clazz = finder.findImplementation(java.io.InputStream.class);
     * clazz.getName();  // returns "org.acme.AcmeInputStream"
     * </pre>
     *
     * @param interfase a superclass or interface
     * @return implementation class
     * @throws IOException            if the URL cannot be read
     * @throws ClassNotFoundException if the class found is not loadable
     * @throws ClassCastException     if the class found is not assignable to the specified superclass or interface
     */
    public Class findImplementation(Class interfase) throws IOException, ClassNotFoundException {
        String className = findString(interfase.getName());
        Class impl = classLoaderInterface.loadClass(className);
        if (!interfase.isAssignableFrom(impl)) {
            throw new ClassCastException("Class not of type: " + interfase.getName());
        }
        return impl;
    }

    /**
     * <p>
     * Assumes the class specified points to a file in the classpath that contains
     * the name of a class that implements or is a subclass of the specfied class.
     * </p>
     *
     * <p>
     * Any class that cannot be loaded or assigned to the specified interface will be cause
     * an exception to be thrown.
     * </p>
     *
     * <p>
     * Example classpath:
     * </p>
     *
     * <pre>
     * META-INF/java.io.InputStream    # contains the classname org.acme.AcmeInputStream
     * META-INF/java.io.InputStream    # contains the classname org.widget.NeatoInputStream
     * META-INF/java.io.InputStream    # contains the classname com.foo.BarInputStream
     * </pre>
     *
     * <pre>
     * ResourceFinder finder = new ResourceFinder("META-INF/");
     * List classes = finder.findAllImplementations(java.io.InputStream.class);
     * classes.contains("org.acme.AcmeInputStream");  // true
     * classes.contains("org.widget.NeatoInputStream");  // true
     * classes.contains("com.foo.BarInputStream");  // true
     * </pre>
     *
     * @param interfase a superclass or interface
     * @return list of implementation classes
     * @throws IOException            if the URL cannot be read
     * @throws ClassNotFoundException if the class found is not loadable
     * @throws ClassCastException     if the class found is not assignable to the specified superclass or interface
     */
    public List<Class> findAllImplementations(Class interfase) throws IOException, ClassNotFoundException {
        List<Class> implementations = new ArrayList<>();
        List<String> strings = findAllStrings(interfase.getName());
        for (String className : strings) {
            Class impl = classLoaderInterface.loadClass(className);
            if (!interfase.isAssignableFrom(impl)) {
                throw new ClassCastException("Class not of type: " + interfase.getName());
            }
            implementations.add(impl);
        }
        return implementations;
    }

    /**
     * <p>
     * Assumes the class specified points to a file in the classpath that contains
     * the name of a class that implements or is a subclass of the specfied class.
     * </p>
     *
     * <p>
     * Any class that cannot be loaded or are not assignable to the specified class will be
     * skipped and placed in the 'resourcesNotLoaded' collection.
     * </p>
     *
     * <p>
     * Example classpath:
     * </p>
     *
     * <pre>
     * META-INF/java.io.InputStream    # contains the classname org.acme.AcmeInputStream
     * META-INF/java.io.InputStream    # contains the classname org.widget.NeatoInputStream
     * META-INF/java.io.InputStream    # contains the classname com.foo.BarInputStream
     * </pre>
     *
     * <pre>
     * ResourceFinder finder = new ResourceFinder("META-INF/");
     * List classes = finder.findAllImplementations(java.io.InputStream.class);
     * classes.contains("org.acme.AcmeInputStream");  // true
     * classes.contains("org.widget.NeatoInputStream");  // true
     * classes.contains("com.foo.BarInputStream");  // true
     * </pre>
     * @param interfase a superclass or interface
     * @return list of implementation classes
     * @throws IOException if classLoader.getResources throws an exception
     */
    public List<Class> findAvailableImplementations(Class interfase) throws IOException {
        resourcesNotLoaded.clear();
        List<Class> implementations = new ArrayList<>();
        List<String> strings = findAvailableStrings(interfase.getName());
        for (String className : strings) {
            try {
                Class impl = classLoaderInterface.loadClass(className);
                if (interfase.isAssignableFrom(impl)) {
                    implementations.add(impl);
                } else {
                    resourcesNotLoaded.add(className);
                }
            } catch (Exception notAvailable) {
                resourcesNotLoaded.add(className);
            }
        }
        return implementations;
    }

    /**
     * <p>
     * Assumes the class specified points to a directory in the classpath that holds files
     * containing the name of a class that implements or is a subclass of the specified class.
     * </p>
     *
     * <p>
     * Any class that cannot be loaded or assigned to the specified interface will be cause
     * an exception to be thrown.
     * </p>
     *
     * <p>
     * Example classpath:
     * </p>
     *
     * <pre>
     * META-INF/java.net.URLStreamHandler/jar
     * META-INF/java.net.URLStreamHandler/file
     * META-INF/java.net.URLStreamHandler/http
     * </pre>
     *
     * <pre>
     * ResourceFinder finder = new ResourceFinder("META-INF/");
     * Map map = finder.mapAllImplementations(java.net.URLStreamHandler.class);
     * Class jarUrlHandler = map.get("jar");
     * Class fileUrlHandler = map.get("file");
     * Class httpUrlHandler = map.get("http");
     * </pre>
     *
     * @param interfase a superclass or interface
     * @return map of implementation classes
     * @throws IOException            if the URL cannot be read
     * @throws ClassNotFoundException if the class found is not loadable
     * @throws ClassCastException     if the class found is not assignable to the specified superclass or interface
     */
    public Map<String, Class> mapAllImplementations(Class interfase) throws IOException, ClassNotFoundException {
        Map<String, Class> implementations = new HashMap<>();
        Map<String, String> map = mapAllStrings(interfase.getName());
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String string = entry.getKey();
            String className = entry.getValue();
            Class impl = classLoaderInterface.loadClass(className);
            if (!interfase.isAssignableFrom(impl)) {
                throw new ClassCastException("Class not of type: " + interfase.getName());
            }
            implementations.put(string, impl);
        }
        return implementations;
    }

    /**
     * <p>
     * Assumes the class specified points to a directory in the classpath that holds files
     * containing the name of a class that implements or is a subclass of the specified class.
     * </p>
     *
     * <p>
     * Any class that cannot be loaded or are not assignable to the specified class will be
     * skipped and placed in the 'resourcesNotLoaded' collection.
     * </p>
     *
     * <p>
     * Example classpath:
     * </p>
     *
     * <pre>
     * META-INF/java.net.URLStreamHandler/jar
     * META-INF/java.net.URLStreamHandler/file
     * META-INF/java.net.URLStreamHandler/http
     * </pre>
     *
     * <pre>
     * ResourceFinder finder = new ResourceFinder("META-INF/");
     * Map map = finder.mapAllImplementations(java.net.URLStreamHandler.class);
     * Class jarUrlHandler = map.get("jar");
     * Class fileUrlHandler = map.get("file");
     * Class httpUrlHandler = map.get("http");
     * </pre>
     *
     * @param interfase a superclass or interface
     * @return list of available implementation classes
     * @throws IOException if classLoader.getResources throws an exception
     */
    public Map<String, Class> mapAvailableImplementations(Class interfase) throws IOException {
        resourcesNotLoaded.clear();
        Map<String, Class> implementations = new HashMap<>();
        Map<String, String> map = mapAvailableStrings(interfase.getName());
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String string = entry.getKey();
            String className = entry.getValue();
            try {
                Class impl = classLoaderInterface.loadClass(className);
                if (interfase.isAssignableFrom(impl)) {
                    implementations.put(string, impl);
                } else {
                    resourcesNotLoaded.add(className);
                }
            } catch (Exception notAvailable) {
                resourcesNotLoaded.add(className);
            }
        }
        return implementations;
    }

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //
    //   Find Properties
    //
    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    /**
     * <p>
     * Finds the corresponding resource and reads it in as a properties file
     * </p>
     *
     * <p>
     * Example classpath:
     * </p>
     *
     * <pre>
     * META-INF/widget.properties
     * </pre>
     *
     * <pre>
     * ResourceFinder finder = new ResourceFinder("META-INF/");
     * Properties widgetProps = finder.findProperties("widget.properties");
     * </pre>
     *
     * @param uri URL
     * @return corresponding resource as properties
     * @throws IOException if the URL cannot be read or is not in properties file format
     */
    public Properties findProperties(String uri) throws IOException {
        String fulluri = path + uri;

        URL resource = getResource(fulluri);
        if (resource == null) {
            throw new IOException("Could not find command in : " + fulluri);
        }

        return loadProperties(resource);
    }

    /**
     * <p>
     * Finds the corresponding resources and reads them in as a properties files
     * </p>
     *
     * <p>
     * Any URL that cannot be read in as a properties file will cause an exception to be thrown.
     * </p>
     *
     * <p>
     * Example classpath:
     * </p>
     *
     * <pre>
     * META-INF/app.properties
     * META-INF/app.properties
     * META-INF/app.properties
     * </pre>
     *
     * <pre>
     * ResourceFinder finder = new ResourceFinder("META-INF/");
     * List&lt;Properties&gt; appProps = finder.findAllProperties("app.properties");
     * </pre>
     *
     * @param uri URL
     * @return corresponding resource as list of properties
     * @throws IOException if the URL cannot be read or is not in properties file format
     */
    public List<Properties> findAllProperties(String uri) throws IOException {
        String fulluri = path + uri;

        List<Properties> properties = new ArrayList<>();

        Enumeration<URL> resources = getResources(fulluri);
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            Properties props = loadProperties(url);
            properties.add(props);
        }
        return properties;
    }

    /**
     * <p>
     * Finds the corresponding resources and reads them in as a properties files
     * </p>
     *
     * <p>
     * Any URL that cannot be read in as a properties file will be added to the
     * 'resourcesNotLoaded' collection.
     * </p>
     *
     * <p>
     * Example classpath:
     * </p>
     *
     * <pre>
     * META-INF/app.properties
     * META-INF/app.properties
     * META-INF/app.properties
     * </pre>
     *
     * <pre>
     * ResourceFinder finder = new ResourceFinder("META-INF/");
     * List&lt;Properties&gt; appProps = finder.findAvailableProperties("app.properties");
     * </pre>
     *
     * @param uri URL
     * @return corresponding resource as list of properties
     * @throws IOException if classLoader.getResources throws an exception
     */
    public List<Properties> findAvailableProperties(String uri) throws IOException {
        resourcesNotLoaded.clear();
        String fulluri = path + uri;

        List<Properties> properties = new ArrayList<>();

        Enumeration<URL> resources = getResources(fulluri);
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            try {
                Properties props = loadProperties(url);
                properties.add(props);
            } catch (Exception notAvailable) {
                resourcesNotLoaded.add(url.toExternalForm());
            }
        }
        return properties;
    }

    /**
     * <p>
     * Finds the corresponding resources and reads them in as a properties files
     * </p>
     *
     * <p>
     * Any URL that cannot be read in as a properties file will cause an exception to be thrown.
     * </p>
     *
     * <p>
     * Example classpath:
     * </p>
     *
     * <pre>
     * META-INF/jdbcDrivers/oracle.properties
     * META-INF/jdbcDrivers/mysql.props
     * META-INF/jdbcDrivers/derby
     * </pre>
     *
     * <pre>
     * ResourceFinder finder = new ResourceFinder("META-INF/");
     * List&lt;Properties&gt; driversList = finder.findAvailableProperties("jdbcDrivers");
     * Properties oracleProps = driversList.get("oracle.properties");
     * Properties mysqlProps = driversList.get("mysql.props");
     * Properties derbyProps = driversList.get("derby");
     * </pre>
     *
     * @param uri URL
     * @return corresponding resource as map of properties
     * @throws IOException if the URL cannot be read or is not in properties file format
     */
    public Map<String, Properties> mapAllProperties(String uri) throws IOException {
        Map<String, Properties> propertiesMap = new HashMap<>();
        Map<String, URL> map = getResourcesMap(uri);
        for (Map.Entry<String, URL> entry : map.entrySet()) {
            String string = entry.getKey();
            URL url = entry.getValue();
            Properties properties = loadProperties(url);
            propertiesMap.put(string, properties);
        }
        return propertiesMap;
    }

    /**
     * <p>
     * Finds the corresponding resources and reads them in as a properties files
     * </p>
     *
     * <p>
     * Any URL that cannot be read in as a properties file will be added to the
     * 'resourcesNotLoaded' collection.
     * </p>
     *
     * <p>
     * Example classpath:
     * </p>
     *
     * <pre>
     * META-INF/jdbcDrivers/oracle.properties
     * META-INF/jdbcDrivers/mysql.props
     * META-INF/jdbcDrivers/derby
     * </pre>
     *
     * <pre>
     * ResourceFinder finder = new ResourceFinder("META-INF/");
     * List&lt;Properties&gt; driversList = finder.findAvailableProperties("jdbcDrivers");
     * Properties oracleProps = driversList.get("oracle.properties");
     * Properties mysqlProps = driversList.get("mysql.props");
     * Properties derbyProps = driversList.get("derby");
     * </pre>
     *
     * @param uri URL
     * @return corresponding resource as map of available properties
     * @throws IOException if classLoader.getResources throws an exception
     */
    public Map<String, Properties> mapAvailableProperties(String uri) throws IOException {
        resourcesNotLoaded.clear();
        Map<String, Properties> propertiesMap = new HashMap<>();
        Map<String, URL> map = getResourcesMap(uri);
        for (Map.Entry<String, URL> entry : map.entrySet()) {
            String string = entry.getKey();
            URL url = entry.getValue();
            try {
                Properties properties = loadProperties(url);
                propertiesMap.put(string, properties);
            } catch (Exception notAvailable) {
                resourcesNotLoaded.add(url.toExternalForm());
            }
        }
        return propertiesMap;
    }

    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    //
    //   Map Resources
    //
    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

    public Map<String, URL> getResourcesMap(String uri) throws IOException {
        String basePath = path + uri;

        Map<String, URL> resources = new HashMap<>();
        if (!basePath.endsWith("/")) {
            basePath += "/";
        }
        Enumeration<URL> urls = getResources(basePath);

        while (urls.hasMoreElements()) {
            URL location = urls.nextElement();

            try {
                if ("jar".equals(location.getProtocol())) {
                    readJarEntries(location, basePath, resources);
                } else if ("file".equals(location.getProtocol())) {
                    readDirectoryEntries(location, resources);
                }
            } catch (Exception e) {
                LOG.debug("Got exception loading resources for {}", uri, e);
            }
        }

        return resources;
    }

    /**
     * @param uri URL
     * @return set of subpackages from jars or dirs
     *
     * @throws IOException in case of IO errors
     */
    public Set<String> findPackages(String uri) throws IOException {
        String basePath = path + uri;

        Set<String> resources = new HashSet<>();
        if (!basePath.endsWith("/")) {
            basePath += "/";
        }
        Enumeration<URL> urls = getResources(basePath);

        while (urls.hasMoreElements()) {
            URL location = urls.nextElement();

            try {
                if ("jar".equals(location.getProtocol())) {
                    readJarDirectoryEntries(location, basePath, resources);
                } else if ("file".equals(location.getProtocol())) {
                    readSubDirectories(new File(location.toURI()), uri, resources);
                }
            } catch (Exception e) {
                LOG.debug("Got exception search for subpackages for {}", uri, e);
            }
        }

        return convertPathsToPackages(resources);
    }

    /**
     * @param uri URL
     * @return a map of subpackages from jars or dirs
     *
     * @throws IOException in case of IO errors
     */
    public Map<URL, Set<String>> findPackagesMap(String uri) throws IOException {
        String basePath = path + uri;

        if (!basePath.endsWith("/")) {
            basePath += "/";
        }
        Enumeration<URL> urls = getResources(basePath);
        Map<URL, Set<String>> result = new HashMap<>();

        while (urls.hasMoreElements()) {
            URL location = urls.nextElement();

            try {
                if ("jar".equals(location.getProtocol())) {
                    Set<String> resources = new HashSet<>();
                    readJarDirectoryEntries(location, basePath, resources);
                    result.put(location, convertPathsToPackages(resources));
                } else if ("file".equals(location.getProtocol())) {
                    Set<String> resources = new HashSet<>();
                    readSubDirectories(new File(location.toURI()), uri, resources);
                    result.put(location, convertPathsToPackages(resources));
                }
            } catch (Exception e) {
                LOG.debug("Got exception finding subpackages for {}", uri, e);
            }
        }

        return result;
    }

    private Set<String> convertPathsToPackages(Set<String> resources) {
        Set<String> packageNames = new HashSet<>(resources.size());
        for(String resource : resources) {
            packageNames.add(StringUtils.removeEnd(StringUtils.replace(resource, "/", "."), "."));
        }

        return packageNames;
    }

    private static void readDirectoryEntries(URL location, Map<String, URL> resources) throws MalformedURLException {
        File dir = new File(URLDecoder.decode(location.getPath()));
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (!file.isDirectory()) {
                    String name = file.getName();
                    URL url = file.toURL();
                    resources.put(name, url);
                }
            }
        }
    }

    /**
     * Reads subdirectories of a file. The output is a list of subdirectories, relative to the basepath
     */
     private static void readSubDirectories(File dir, String basePath, Set<String> resources) throws MalformedURLException {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    String name = file.getName();
                    String subName = StringUtils.removeEnd(basePath, "/") + "/" + name;
                    resources.add(subName);
                    readSubDirectories(file, subName, resources);
                }
            }
        }
    }

    private static void readJarEntries(URL location, String basePath, Map<String, URL> resources) throws IOException {
        JarURLConnection conn = (JarURLConnection) location.openConnection();
        JarFile jarfile;
        jarfile = conn.getJarFile();

        Enumeration<JarEntry> entries = jarfile.entries();
        while (entries != null && entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();

            if (entry.isDirectory() || !name.startsWith(basePath) || name.length() == basePath.length()) {
                continue;
            }

            name = name.substring(basePath.length());

            if (name.contains("/")) {
                continue;
            }

            URL resource = new URL(location, name);
            resources.put(name, resource);
        }
    }

    //read directories in the jar that start with the basePath
    private static void readJarDirectoryEntries(URL location, String basePath, Set<String> resources) throws IOException {
        JarURLConnection conn = (JarURLConnection) location.openConnection();
        JarFile jarfile;
        jarfile = conn.getJarFile();

        Enumeration<JarEntry> entries = jarfile.entries();
        while (entries != null && entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();

            if (entry.isDirectory() && StringUtils.startsWith(name, basePath)) {
                resources.add(name);
            }
        }
    }

    private Properties loadProperties(URL resource) throws IOException {
        try (InputStream reader = new BufferedInputStream(resource.openStream())) {
            Properties properties = new Properties();
            properties.load(reader);

            return properties;
        }
    }

    private String readContents(URL resource) throws IOException {
        StringBuilder sb = new StringBuilder();

        try (InputStream reader = new BufferedInputStream(resource.openStream())) {
            int b = reader.read();
            while (b != -1) {
                sb.append((char) b);
                b = reader.read();
            }

            return sb.toString().trim();
        }
    }

    private URL getResource(String fullUri) {
        if (urls == null){
            return classLoaderInterface.getResource(fullUri);
        }
        return findResource(fullUri, urls);
    }

    private Enumeration<URL> getResources(String fulluri) throws IOException {
        if (urls == null) {
            return classLoaderInterface.getResources(fulluri);
        }
        Vector<URL> resources = new Vector<>();
        for (URL url : urls) {
            URL resource = findResource(fulluri, url);
            if (resource != null){
                resources.add(resource);
            }
        }
        return resources.elements();
    }

    private URL findResource(String resourceName, URL... search) {
        for (int i = 0; i < search.length; i++) {
            URL currentUrl = search[i];
            if (currentUrl == null) {
                continue;
            }
            JarFile jarFile;
            try {
                String protocol = currentUrl.getProtocol();
                if ("jar".equals(protocol)) {
                    /*
                    * If the connection for currentUrl or resURL is
                    * used, getJarFile() will throw an exception if the
                    * entry doesn't exist.
                    */
                    URL jarURL = ((JarURLConnection) currentUrl.openConnection()).getJarFileURL();
                    try {
                        JarURLConnection juc = (JarURLConnection) new URL("jar", "", jarURL.toExternalForm() + "!/").openConnection();
                        jarFile = juc.getJarFile();
                    } catch (IOException e) {
                        // Don't look for this jar file again
                        search[i] = null;
                        throw e;
                    }

                    String entryName;
                    if (currentUrl.getFile().endsWith("!/")) {
                        entryName = resourceName;
                    } else {
                        String file = currentUrl.getFile();
                        int sepIdx = file.lastIndexOf("!/");
                        if (sepIdx == -1) {
                            // Invalid URL, don't look here again
                            search[i] = null;
                            continue;
                        }
                        sepIdx += 2;
                        StringBuilder sb = new StringBuilder(file.length() - sepIdx + resourceName.length());
                        sb.append(file.substring(sepIdx));
                        sb.append(resourceName);
                        entryName = sb.toString();
                    }
                    if ("META-INF/".equals(entryName) && jarFile.getEntry("META-INF/MANIFEST.MF") != null){
                        return targetURL(currentUrl, "META-INF/MANIFEST.MF");
                    }
                    if (jarFile.getEntry(entryName) != null) {
                        return targetURL(currentUrl, resourceName);
                    }
                } else if ("file".equals(protocol)) {
                    String baseFile = currentUrl.getFile();
                    String host = currentUrl.getHost();
                    int hostLength = 0;
                    if (host != null) {
                        hostLength = host.length();
                    }
                    StringBuilder buf = new StringBuilder(2 + hostLength + baseFile.length() + resourceName.length());

                    if (hostLength > 0) {
                        buf.append("//").append(host);
                    }
                    // baseFile always ends with '/'
                    buf.append(baseFile);
                    String fixedResName = resourceName;
                    // Do not create a UNC path, i.e. \\host
                    while (fixedResName.startsWith("/") || fixedResName.startsWith("\\")) {
                        fixedResName = fixedResName.substring(1);
                    }
                    buf.append(fixedResName);
                    String filename = buf.toString();
                    File file = new File(filename);
                    File file2 = new File(URLDecoder.decode(filename));

                    if (file.exists() || file2.exists()) {
                        return targetURL(currentUrl, fixedResName);
                    }
                } else {
                    URL resourceURL = targetURL(currentUrl, resourceName);
                    URLConnection urlConnection = resourceURL.openConnection();

                    try {
                        urlConnection.getInputStream().close();
                    } catch (SecurityException e) {
                        return null;
                    }
                    // HTTP can return a stream on a non-existent file
                    // So check for the return code;
                    if (!"http".equals(resourceURL.getProtocol())) {
                        return resourceURL;
                    }

                    int code = ((HttpURLConnection) urlConnection).getResponseCode();
                    if (code >= 200 && code < 300) {
                        return resourceURL;
                    }
                }
            } catch (IOException | SecurityException e) {
                // Keep iterating through the URL list
            }
        }
        return null;
    }

    private URL targetURL(URL base, String name) throws MalformedURLException {
        StringBuilder sb = new StringBuilder(base.getFile().length() + name.length());
        sb.append(base.getFile());
        sb.append(name);
        String file = sb.toString();
        return new URL(base.getProtocol(), base.getHost(), base.getPort(), file, null);
    }
}
