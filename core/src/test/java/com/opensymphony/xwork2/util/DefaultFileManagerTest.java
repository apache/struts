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
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.XWorkTestCase;

import java.io.InputStream;
import java.net.*;

/**
 * FileManager Tester.
 *
 * @author <Lukasz>
 * @since <pre>02/18/2009</pre>
 * @version 1.0
 */
public class DefaultFileManagerTest extends XWorkTestCase {

    private FileManager fileManager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        fileManager = container.getInstance(FileManagerFactory.class).getFileManager();
    }

    public void testGetFileInJar() throws Exception {
        testLoadFile("xwork-jar.xml", false);
        testLoadFile("xwork - jar.xml", false);
        testLoadFile("xwork-zip.xml", false);
        testLoadFile("xwork - zip.xml", false);
        testLoadFile("xwork-jar2.xml", false);
        testLoadFile("xwork - jar2.xml", false);
        testLoadFile("xwork-zip2.xml", false);
        testLoadFile("xwork - zip2.xml", false);

        testLoadFile("xwork-jar.xml", true);
        testLoadFile("xwork - jar.xml", true);
        testLoadFile("xwork-zip.xml", true);
        testLoadFile("xwork - zip.xml", true);
        testLoadFile("xwork-jar2.xml", true);
        testLoadFile("xwork - jar2.xml", true);
        testLoadFile("xwork-zip2.xml", true);
        testLoadFile("xwork - zip2.xml", true);
    }

    private void testLoadFile(String fileName, boolean reloadConfigs) throws Exception {
        fileManager.setReloadingConfigs(reloadConfigs);
        URL url = ClassLoaderUtil.getResource(fileName, DefaultFileManagerTest.class);
        InputStream file = fileManager.loadFile(url);
        assertNotNull(file);
        file.close();
        assertFalse(fileManager.fileNeedsReloading(url.toString()));

        long now = System.currentTimeMillis();
        JarURLConnection conn = (JarURLConnection) url.openConnection();
        conn.getJarEntry().setTime(now + 60000);
        conn.getInputStream().close();

        assertEquals(reloadConfigs, fileManager.fileNeedsReloading(url.toString()));
    }

    public void testReloadingConfigs() throws Exception {
        // given
        container.getInstance(FileManagerFactory.class).setReloadingConfigs("false");
        FileManager fm = container.getInstance(FileManagerFactory.class).getFileManager();
        String resourceName = "xwork-sample.xml";
        assertFalse(fm.fileNeedsReloading(resourceName));

        // when
        container.getInstance(FileManagerFactory.class).setReloadingConfigs("true");

        // then
        fm = container.getInstance(FileManagerFactory.class).getFileManager();
        assertTrue(fm.fileNeedsReloading(resourceName));
    }

    public void testSimpleFile() throws MalformedURLException {
        URL url = new URL("file:c:/somefile.txt");
        URL outputURL = fileManager.normalizeToFileProtocol(url);

        assertEquals(outputURL, url);
    }

    public void testJarFile() throws MalformedURLException {
        URL url = new URL("jar:file:/c:/somefile.jar!/");
        URL outputURL = fileManager.normalizeToFileProtocol(url);

        assertNotNull(outputURL);
        assertEquals("file:/c:/somefile.jar", outputURL.toExternalForm());

        url = new URL("jar:file:/c:/somefile.jar!/somestuf/bla/bla");
        outputURL = fileManager.normalizeToFileProtocol(url);
        assertEquals("file:/c:/somefile.jar", outputURL.toExternalForm());

        url = new URL("jar:file:c:/somefile.jar!/somestuf/bla/bla");
        outputURL = fileManager.normalizeToFileProtocol(url);
        assertEquals("file:c:/somefile.jar", outputURL.toExternalForm());
    }

    public void testJarFileWithJarWordInsidePath() throws MalformedURLException {
        URL url = new URL("jar:file:/c:/workspace/projar/somefile.jar!/");
        URL outputURL = fileManager.normalizeToFileProtocol(url);

        assertNotNull(outputURL);
        assertEquals("file:/c:/workspace/projar/somefile.jar", outputURL.toExternalForm());

        url = new URL("jar:file:/c:/workspace/projar/somefile.jar!/somestuf/bla/bla");
        outputURL = fileManager.normalizeToFileProtocol(url);
        assertEquals("file:/c:/workspace/projar/somefile.jar", outputURL.toExternalForm());

        url = new URL("jar:file:c:/workspace/projar/somefile.jar!/somestuf/bla/bla");
        outputURL = fileManager.normalizeToFileProtocol(url);
        assertEquals("file:c:/workspace/projar/somefile.jar", outputURL.toExternalForm());
    }

    public void testZipFile() throws MalformedURLException {
        URL url = new URL("zip:/c:/somefile.zip!/");
        URL outputURL = fileManager.normalizeToFileProtocol(url);

        assertNotNull(outputURL);
        assertEquals("file:/c:/somefile.zip", outputURL.toExternalForm());

        url = new URL("zip:/c:/somefile.zip!/somestuf/bla/bla");
        outputURL = fileManager.normalizeToFileProtocol(url);
        assertEquals("file:/c:/somefile.zip", outputURL.toExternalForm());

        url = new URL("zip:c:/somefile.zip!/somestuf/bla/bla");
        outputURL = fileManager.normalizeToFileProtocol(url);
        assertEquals("file:c:/somefile.zip", outputURL.toExternalForm());
    }

    public void testWSJarFile() throws MalformedURLException {
        URL url = new URL("wsjar:file:/c:/somefile.jar!/");
        URL outputURL = fileManager.normalizeToFileProtocol(url);

        assertNotNull(outputURL);
        assertEquals("file:/c:/somefile.jar", outputURL.toExternalForm());

        url = new URL("wsjar:file:/c:/somefile.jar!/somestuf/bla/bla");
        outputURL = fileManager.normalizeToFileProtocol(url);
        assertEquals("file:/c:/somefile.jar", outputURL.toExternalForm());

        url = new URL("wsjar:file:c:/somefile.jar!/somestuf/bla/bla");
        outputURL = fileManager.normalizeToFileProtocol(url);
        assertEquals("file:c:/somefile.jar", outputURL.toExternalForm());
    }

    public void disabled_testVsFile() throws MalformedURLException {
        URL url = new URL("vfsfile:/c:/somefile.jar!/");
        URL outputURL = fileManager.normalizeToFileProtocol(url);

        assertNotNull(outputURL);
        assertEquals("file:/c:/somefile.jar", outputURL.toExternalForm());

        url = new URL("vfsfile:/c:/somefile.jar!/somestuf/bla/bla");
        outputURL = fileManager.normalizeToFileProtocol(url);
        assertEquals("file:/c:/somefile.jar", outputURL.toExternalForm());

        url = new URL("vfsfile:c:/somefile.jar!/somestuf/bla/bla");
        outputURL = fileManager.normalizeToFileProtocol(url);
        assertEquals("file:c:/somefile.jar", outputURL.toExternalForm());

        url = new URL("vfszip:/c:/somefile.war/somelibrary.jar");
        outputURL = fileManager.normalizeToFileProtocol(url);
        assertEquals("file:/c:/somefile.war/somelibrary.jar", outputURL.toExternalForm());
    }

    public void disabled_testJBossFile() throws MalformedURLException {
        URL url = new URL("vfszip:/c:/somefile.jar!/");
        URL outputURL = fileManager.normalizeToFileProtocol(url);

        assertNotNull(outputURL);
        assertEquals("file:/c:/somefile.jar", outputURL.toExternalForm());

        url = new URL("vfszip:/c:/somefile.jar!/somestuf/bla/bla");
        outputURL = fileManager.normalizeToFileProtocol(url);
        assertEquals("file:/c:/somefile.jar", outputURL.toExternalForm());

        url = new URL("vfsmemory:c:/somefile.jar!/somestuf/bla/bla");
        outputURL = fileManager.normalizeToFileProtocol(url);
        assertEquals("file:c:/somefile.jar", outputURL.toExternalForm());

        url = new URL("vfsmemory:/c:/somefile.war/somelibrary.jar");
        outputURL = fileManager.normalizeToFileProtocol(url);
        assertEquals("file:/c:/somefile.war/somelibrary.jar", outputURL.toExternalForm());
    }

}
