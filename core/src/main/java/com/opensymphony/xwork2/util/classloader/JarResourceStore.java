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
package com.opensymphony.xwork2.util.classloader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Read resources from a jar file
 */
public class JarResourceStore extends AbstractResourceStore {
    private static final Logger LOG = LogManager.getLogger(JarResourceStore.class);

    public JarResourceStore(File file) {
        super(file);
    }

    public byte[] read(String pResourceName) {
        InputStream in = null;
        try {
            ZipFile jarFile = new ZipFile(file);
            ZipEntry entry = jarFile.getEntry(pResourceName);

            //read into byte array
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            in = jarFile.getInputStream(entry);
            copy(in, out);

            return out.toByteArray();
        } catch (Exception e) {
            LOG.debug("Unable to read file [{}] from [{}]", pResourceName, file.getName(), e);
            return null;
        } finally {
            closeQuietly(in);
        }
    }

    public static long copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024 * 4];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
