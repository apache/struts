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
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.XWorkTestCase;
import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

public class JarEntryRevisionTest extends XWorkTestCase {

    private FileManager fileManager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        fileManager = container.getInstance(FileManagerFactory.class).getFileManager();
    }

    private void createJarFile(long time) throws Exception {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        FileOutputStream fos = new FileOutputStream("target/JarEntryRevisionTest_testNeedsReloading.jar", false);
        JarOutputStream target = new JarOutputStream(fos, manifest);
        target.putNextEntry(new ZipEntry("com/opensymphony/xwork2/util/fs/"));
        ZipEntry entry = new ZipEntry("com/opensymphony/xwork2/util/fs/JarEntryRevisionTest.class");
        entry.setTime(time);
        target.putNextEntry(entry);
        InputStream source = getClass().getResourceAsStream("/com/opensymphony/xwork2/util/fs/JarEntryRevisionTest.class");
        IOUtils.copy(source, target);
        source.close();
        target.closeEntry();
        target.close();
        fos.close();
    }

    public void testNeedsReloading() throws Exception {
        long now = System.currentTimeMillis();

        createJarFile(now);
        URL url = new URL("jar:file:target/JarEntryRevisionTest_testNeedsReloading.jar!/com/opensymphony/xwork2/util/fs/JarEntryRevisionTest.class");
        Revision entry = JarEntryRevision.build(url, fileManager);
        assertFalse(entry.needsReloading());

        createJarFile(now + 60000);
        assertTrue(entry.needsReloading());
    }
}
