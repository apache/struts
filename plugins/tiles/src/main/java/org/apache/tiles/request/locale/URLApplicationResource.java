/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.request.locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * A {@link PostfixedApplicationResource} that can be accessed through a URL.
 */

public class URLApplicationResource extends PostfixedApplicationResource {
    /**
     * System parameter to specify additional remote protocols. If an url has a remote protocol, then any
     * {@link IOException} will be thrown directly. If an url has a local protocol, then any {@link IOException}
     * will be caught and transformed into a {@link FileNotFoundException}.
     */
    static final String REMOTE_PROTOCOLS_PROPERTY = "tiles.remoteProtocols";

    private static final Logger LOG = LogManager.getLogger(URLApplicationResource.class);

    private static final Set<String> REMOTE_PROTOCOLS;

    static {
        REMOTE_PROTOCOLS = initRemoteProtocols();
    }

    /**
     * Creates an unmodifiable set of <em>remote</em> protocols which are used in {@link URL} objects, see {@link URL#getProtocol()}.
     * AN url with a remote protocol establishes a network connection when its {@link URL#openConnection()} is being called.
     * The set will always contain the built-in remote protocols below:
     * <ul>
     *  <li><a href="http://hg.openjdk.java.net/jdk8/jdk8/jdk/file/687fd7c7986d/src/share/classes/sun/net/www/protocol/ftp">ftp</a></li>
     *  <li><a href="http://hg.openjdk.java.net/jdk8/jdk8/jdk/file/687fd7c7986d/src/share/classes/sun/net/www/protocol/http">http</a></li>
     *  <li><a href="http://hg.openjdk.java.net/jdk8/jdk8/jdk/file/687fd7c7986d/src/share/classes/sun/net/www/protocol/https">https</a></li>
     * </ul>
     * It's possible, that your environment provides additional remote protocols because of following reasons:
     * <ul>
     *     <li>your application server adds more remote protocols, see its documentation for further details.</li>
     *     <li>your application supplies custom remote protocols trough its own {@link java.net.URLStreamHandlerFactory}
     *     (see following excellent <a href="https://stackoverflow.com/questions/26363573/registering-and-using-a-custom-java-net-url-protocol">explanation</a>
     *     for getting an idea how to do this)</li>
     * </ul>
     * If you need to use such extra remote protocols in Tiles, you may enhance the set via system property {@code tiles.remoteProtocols}. Suppose
     * you need to add your custom remote protocols "foo" and "bar". To do so, add following parameter to the command line (use ";" as separator):
     * <pre>
     *     -Dtiles.remoteProtocols=foo;bar
     * </pre>
     * The resulting set will then contain the built-in protocols plus "foo" and "bar".
     *
     * @return Unmodifiable set of remote protocols, never {@code null}
     */
    static Set<String> initRemoteProtocols() {
        Set<String> remoteProtocols = new HashSet<>();
        remoteProtocols.add("ftp");
        remoteProtocols.add("http");
        remoteProtocols.add("https");

        String protocolsProp = System.getProperty(REMOTE_PROTOCOLS_PROPERTY);
        if (protocolsProp != null) {
            for (String protocol : protocolsProp.split(";")) {
                remoteProtocols.add(protocol.trim());
            }
        }
        return Collections.unmodifiableSet(remoteProtocols);
    }

    private boolean isLocal(URL url) {
        return !REMOTE_PROTOCOLS.contains(url.getProtocol());
    }

    /**
     * the URL where the contents can be found.
     */
    private final URL url;
    /**
     * if the URL matches a file, this is the file.
     */
    private File file;
    /**
     * if the URL points to a local resource
     */
    private final boolean local;

    /**
     * Creates a URLApplicationResource for the specified path that can be accessed through the specified URL.
     *
     * @param localePath the path including localization.
     * @param url        the URL where the contents can be found.
     */
    public URLApplicationResource(String localePath, URL url) {
        super(localePath);
        this.url = url;
        if ("file".equals(url.getProtocol())) {
            file = getFile(url);
        }
        local = isLocal(url);
    }

    /**
     * Creates a URLApplicationResource for the specified path that can be accessed through the specified URL.
     *
     * @param path   the path excluding localization.
     * @param locale the Locale.
     * @param url    the URL where the contents can be found.
     */
    public URLApplicationResource(String path, Locale locale, URL url) {
        super(path, locale);
        this.url = url;
        if ("file".equals(url.getProtocol())) {
            file = getFile(url);
        }
        local = isLocal(url);
    }

    private URLConnection openConnection() throws IOException {
        try {
            return url.openConnection();
        } catch (IOException e) {
            // If the url points to a local resource, but it cannot be
            // opened, then the resource actually does not exist. In this
            // case throw a FileNotFoundException
            if (local) {
                FileNotFoundException fne = new FileNotFoundException(url.toString());
                fne.initCause(e);
                throw fne;
            }
            throw e;
        }
    }

    private File getFile(URL url) {
        try {
            return new File(new URI(url.toExternalForm()).getSchemeSpecificPart());
        } catch (URISyntaxException e) {
            LOG.debug("Cannot translate URL to file name, expect a performance impact", e);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getInputStream() throws IOException {
        if (file != null) {
            if (file.exists()) {
                return Files.newInputStream(file.toPath());
            } else {
                throw new FileNotFoundException("File does not exist: " + file);
            }
        } else {
            return openConnection().getInputStream();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getLastModified() throws IOException {
        if (file != null) {
            return file.lastModified();
        } else {
            URLConnection connection = openConnection();
            if (connection instanceof JarURLConnection) {
                return ((JarURLConnection) connection).getJarEntry().getTime();
            } else {
                return connection.getLastModified();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Resource " + getLocalePath() + " at " + url.toString();
    }

}
