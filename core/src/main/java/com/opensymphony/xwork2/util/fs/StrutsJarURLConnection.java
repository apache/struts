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
package com.opensymphony.xwork2.util.fs;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * WW-4901 If was needed, decouples from underlying implementation of {@link URL#openConnection()}
 * e.g. from IBM WebSphere com.ibm.ws.classloader.Handler$ClassLoaderURLConnection
 * WW-4920 Also decouples from and fixes {@link JarURLConnection#parseSpecs(URL)} if was needed
 * e.g. from Oracle WebLogic which may report jar urls like "zip:C:/web-app-lib-path/some-jar.jar"
 * but {@link JarURLConnection#parseSpecs(URL)} breaks on such urls
 * While {@link JarURLConnection#parseSpecs(URL)} is private, then we had to extend {@link URLConnection} instead
 * @since 2.5.15
 */
class StrutsJarURLConnection extends URLConnection {
    private static final String FILE_URL_PREFIX = "file:";

    private JarURLConnection jarURLConnection;

    private JarFile jarFile;
    private String entryName;
    private URL jarFileURL;

    private StrutsJarURLConnection(URL url) throws IOException {
        super(url);

        URLConnection conn = this.url.openConnection();
        if (conn instanceof JarURLConnection) {//decoupling is not needed?
            jarURLConnection = (JarURLConnection) conn;
        } else {
            try {
                conn.getInputStream().close();
            } catch (IOException ignored) {
            }
            parseSpecs(url);
        }
    }

    /**
    * A fixed copy of {@link JarURLConnection#parseSpecs(URL)}
    */
    private void parseSpecs(URL url) throws MalformedURLException, UnsupportedEncodingException {
        String spec = url.getFile();

        int separator = spec.indexOf("!/");
        /*
         * REMIND: we don't handle nested JAR URLs
         */
        if (separator == -1) {
            throw new MalformedURLException("no !/ found in url spec:" + spec);
        }

        // start of fixing JarURLConnection#parseSpecs(URL) via handling MalformedURLException
        String jarFileSpec = spec.substring(0, separator++);
        try {
            jarFileURL = new URL(jarFileSpec);
        } catch (MalformedURLException e) {
            // Probably no protocol in original jar URL, like "jar:C:/mypath/myjar.jar".
            // This usually indicates that the jar file resides in the file system.
            if (!jarFileSpec.startsWith("/")) {
                jarFileSpec = "/" + jarFileSpec;
            }
            jarFileURL = new URL(FILE_URL_PREFIX + jarFileSpec);
        }
        // end of fix

        entryName = null;

        /* if ! is the last letter of the innerURL, entryName is null */
        if (++separator != spec.length()) {
            entryName = spec.substring(separator, spec.length());
            entryName = URLDecoder.decode (entryName, "UTF-8");
        }
    }

    @Override
    public void connect() throws IOException {
        if (connected) {
            return;
        }

        if (jarURLConnection != null) {
            connected = true;
            return;
        }

        try (final InputStream in = jarFileURL.openConnection().getInputStream()) {
            jarFile = AccessController.doPrivileged(
                    new PrivilegedExceptionAction<JarFile>() {
                        public JarFile run() throws IOException {
                            Path tmpFile = Files.createTempFile("jar_cache", null);
                            try {
                                Files.copy(in, tmpFile, StandardCopyOption.REPLACE_EXISTING);
                                JarFile jarFile = new JarFile(tmpFile.toFile(), true, JarFile.OPEN_READ);
                                tmpFile.toFile().deleteOnExit();
                                return jarFile;
                            } catch (Throwable thr) {
                                try {
                                    Files.delete(tmpFile);
                                } catch (IOException ioe) {
                                    thr.addSuppressed(ioe);
                                }
                                throw thr;
                            } finally {
                                in.close();
                            }
                        }
                    });
            connected = true;
        } catch (PrivilegedActionException pae) {
            throw (IOException) pae.getException();
        }
    }

    JarEntry getJarEntry() throws IOException {
        if (jarURLConnection != null) {
            return jarURLConnection.getJarEntry();
        } else {
            connect();
            return jarFile.getJarEntry(entryName);
        }
    }

    @Override
    public void setUseCaches(boolean usecaches) {
        super.setUseCaches(usecaches);

        if (jarURLConnection != null) {
            jarURLConnection.setUseCaches(usecaches);
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (jarURLConnection != null) {
            return jarURLConnection.getInputStream();
        } else {
            return jarFile.getInputStream(jarFile.getJarEntry(entryName));
        }
    }

    static StrutsJarURLConnection openConnection(URL url) throws IOException {
        return new StrutsJarURLConnection(url);
    }
}
