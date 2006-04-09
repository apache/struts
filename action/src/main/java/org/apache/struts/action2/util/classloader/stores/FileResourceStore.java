/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
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
package org.apache.struts.action2.util.classloader.stores;

import org.apache.commons.io.IOUtils;

import java.io.*;


/**
 * @author tcurdt
 */
public final class FileResourceStore implements ResourceStore {

    private final File root;

    public FileResourceStore(final File pFile) {
        root = pFile;
    }

    public byte[] read(final String resourceName) {
        InputStream is = null;
        try {
            is = new FileInputStream(getFile(resourceName));
            final byte[] data = IOUtils.toByteArray(is);
            return data;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }

        return null;
    }

    public void write(final String resourceName, final byte[] clazzData) {
        OutputStream os = null;
        try {
            final File file = getFile(resourceName);
            final File parent = file.getParentFile();
            if (!parent.exists()) {
                if (!parent.mkdirs()) {
                    throw new IOException("could not create" + parent);
                }
            }
            os = new FileOutputStream(file);
            os.write(clazzData);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void remove(final String pResourceName) {
        getFile(pResourceName).delete();
    }

    private File getFile(final String pResourceName) {
        final String fileName = pResourceName.replace('.', File.separatorChar) + ".class";
        return new File(root, fileName);
    }

}
