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

import com.opensymphony.xwork2.FileManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;

/**
 * Represents jar resource revision, used for jar://* resource
 */
public class JarEntryRevision extends Revision {

    private static Logger LOG = LogManager.getLogger(JarEntryRevision.class);

    private URL jarFileURL;
    private long lastModified;

    public static Revision build(URL fileUrl, FileManager fileManager) {
        StrutsJarURLConnection conn = null;
        try {
            conn = StrutsJarURLConnection.openConnection(fileUrl);
            conn.setUseCaches(false);
            URL url = fileManager.normalizeToFileProtocol(fileUrl);
            if (url != null) {
                return new JarEntryRevision(fileUrl, conn.getJarEntry().getTime());
            } else {
                return null;
            }
        } catch (Throwable e) {
            LOG.warn("Could not create JarEntryRevision for [{}]!", fileUrl, e);
            return null;
        }
        finally {
            if(null != conn) {
                try {
                    conn.getInputStream().close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private JarEntryRevision(URL jarFileURL, long lastModified) {
        if (jarFileURL == null) {
            throw new IllegalArgumentException("jarFileURL cannot be null");
        }
        this.jarFileURL = jarFileURL;
        this.lastModified = lastModified;
    }

    public boolean needsReloading() {
        StrutsJarURLConnection conn = null;
        long lastLastModified = lastModified;
        try {
            conn = StrutsJarURLConnection.openConnection(jarFileURL);
            conn.setUseCaches(false);
            lastLastModified = conn.getJarEntry().getTime();
        } catch (IOException ignored) {
        }
        finally {
            if(null != conn) {
                try {
                    conn.getInputStream().close();
                } catch (IOException ignored) {
                }
            }
        }

        return lastModified < lastLastModified;
    }

}
