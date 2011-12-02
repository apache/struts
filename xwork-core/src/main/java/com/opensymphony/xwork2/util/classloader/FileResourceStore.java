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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Reads a class from disk
 *  class taken from Apache JCI
 */
public final class FileResourceStore implements ResourceStore {
    private static final Logger LOG = LoggerFactory.getLogger(FileResourceStore.class);
    private final File root;

    public FileResourceStore(final File pFile) {
        root = pFile;
    }

    public byte[] read(final String pResourceName) {
        FileInputStream fis = null;
        try {
            File file = getFile(pResourceName);
            byte[] data = new byte[(int) file.length()];
            fis = new FileInputStream(file);
            fis.read(data);

            return data;
        } catch (Exception e) {
            if (LOG.isDebugEnabled())
                LOG.debug("Unable to read file [#0]", e, pResourceName);
            return null;
        } finally {
            closeQuietly(fis);
        }
    }

    public void write(final String pResourceName, final byte[] pData) {

    }

    private void closeQuietly(InputStream is) {
        try {
            if (is != null)
                is.close();
        } catch (IOException e) {
            if (LOG.isErrorEnabled())
                LOG.error("Unable to close file input stream", e);
        }
    }

    private File getFile(final String pResourceName) {
        final String fileName = pResourceName.replace('/', File.separatorChar);
        return new File(root, fileName);
    }

    public String toString() {
        return this.getClass().getName() + root.toString();
    }
}
