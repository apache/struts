/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
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

package com.opensymphony.xwork2.util.classloader;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Read resources from a jar file
 */
public class JarResourceStore implements ResourceStore {
    private static final Logger LOG = LoggerFactory.getLogger(JarResourceStore.class);

    private final File file;

    public JarResourceStore(File file) {
        this.file = file;
    }

    public void write(String pResourceName, byte[] pResourceData) {
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
            if (LOG.isDebugEnabled())
                LOG.debug("Unable to read file [#0] from [#1]", e, pResourceName, file.getName());
            return null;
        } finally {
            closeQuietly(in);
        }
    }

    public static long copy(InputStream input, OutputStream output)
            throws IOException {
        byte[] buffer = new byte[1024 * 4];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    private void closeQuietly(InputStream is) {
        try {
            if (is != null)
                is.close();
        } catch (IOException e) {
            if (LOG.isErrorEnabled())
                LOG.error("Unable to close input stream", e);
        }
    }
}
