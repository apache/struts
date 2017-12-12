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
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.jar.JarFile;

/**
 * WW-4901 Decouples from underlying implementation of {@link URL#openConnection()}
 * e.g. from IBM WebSphere com.ibm.ws.classloader.Handler$ClassLoaderURLConnection
 * @since 2.5.15
 */
class StrutsJarURLConnection extends JarURLConnection {
    private JarFile jarFile;

    private StrutsJarURLConnection(URL url) throws MalformedURLException {
        super(url);
    }

    @Override
    public JarFile getJarFile() throws IOException {
        connect();
        return jarFile;
    }

    @Override
    public void connect() throws IOException {
        if (connected) {
            return;
        }

        try (final InputStream in = getJarFileURL().openConnection().getInputStream()) {
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


    static JarURLConnection openConnection(URL url) throws IOException {
        URLConnection conn = url.openConnection();
        if (conn instanceof JarURLConnection) {
            return (JarURLConnection) conn;
        } else {
            try {
                conn.getInputStream().close();
            } catch (IOException ignored) {
            }
        }

        StrutsJarURLConnection result = new StrutsJarURLConnection(url);
        return result;
    }
}
