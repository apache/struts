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
import com.opensymphony.xwork2.util.fs.DefaultFileManager;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.JarInputStream;

/**
 * Keep these test on a separate class, they can't be in UrlUtilTest because the
 * registered URLStreamHandlerFactory would make them fail
 */
public class UrlUtilTest2 extends TestCase {

    public void testOpenWithJarProtocol() throws IOException {
        FileManager fileManager = new DefaultFileManager();

        URL url = ClassLoaderUtil.getResource("xwork-jar.jar", ClassLoaderUtil.class);
        URL jarUrl = new URL("jar", "", url.toExternalForm() + "!/");
        URL outputURL = fileManager.normalizeToFileProtocol(jarUrl);

        assertNotNull(outputURL);
        assertUrlCanBeOpened(outputURL);
    }

    private void assertUrlCanBeOpened(URL url) throws IOException {
        InputStream is = url.openStream();
        try (JarInputStream jarStream = new JarInputStream(is)) {
            assertNotNull(jarStream);
        }
    }
}
