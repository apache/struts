/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.request.locale;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Locale;

import static java.lang.System.setProperty;
import static java.lang.reflect.Modifier.FINAL;
import static org.apache.tiles.request.locale.URLApplicationResource.REMOTE_PROTOCOLS_PROPERTY;
import static org.apache.tiles.request.locale.URLApplicationResource.initRemoteProtocols;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests URLApplicationResource.
 */
public class URLApplicationResourceTest {

    private static final String EXPECTED_MESSAGE = "aMessage";

    private static class TestUrlStreamHandler extends URLStreamHandler {

        @Override
        protected URLConnection openConnection(final URL u) throws IOException {
            throw new IOException(EXPECTED_MESSAGE);
        }
    }

    private static class TestURLStreamHandlerFactory implements URLStreamHandlerFactory {

        @Override
        public URLStreamHandler createURLStreamHandler(final String protocol) {
            if ("test1".equals(protocol) || "test2".equals(protocol)) {
                return new TestUrlStreamHandler();
            }
            return null;
        }
    }

    @BeforeClass
    public static void setURLStreamHandlerFactory() {
        URL.setURLStreamHandlerFactory(new TestURLStreamHandlerFactory());
    }

    @After
    public void tearDown() {
        setProperty(REMOTE_PROTOCOLS_PROPERTY, "");
    }

    /**
     * Test getLocalePath(String path, Locale locale).
     */
    @Test
    public void testGetLocalePath() throws MalformedURLException {
        URLApplicationResource resource = new URLApplicationResource("/my test/path_fr.html", new URL("file:///"));
        assertEquals("/my test/path.html", resource.getLocalePath(null));
        assertEquals("/my test/path.html", resource.getLocalePath(Locale.ROOT));
        assertEquals("/my test/path_it.html", resource.getLocalePath(Locale.ITALIAN));
        assertEquals("/my test/path_it_IT.html", resource.getLocalePath(Locale.ITALY));
        assertEquals("/my test/path_en_GB_scotland.html", resource.getLocalePath(new Locale("en", "GB", "scotland")));
    }

    @Test
    public void testBuildFromString() throws MalformedURLException {
        URLApplicationResource resource = new URLApplicationResource("/my test/path_en_GB_scotland.html", new URL("file:///"));
        assertEquals("/my test/path_en_GB_scotland.html", resource.getLocalePath());
        assertEquals("/my test/path.html", resource.getPath());
        assertEquals(new Locale("en", "GB", "scotland"), resource.getLocale());
        resource = new URLApplicationResource("/my test/path_it_IT.html", new URL("file:///"));
        assertEquals("/my test/path_it_IT.html", resource.getLocalePath());
        assertEquals("/my test/path.html", resource.getPath());
        assertEquals(Locale.ITALY, resource.getLocale());
        resource = new URLApplicationResource("/my test/path_it.html", new URL("file:///"));
        assertEquals("/my test/path_it.html", resource.getLocalePath());
        assertEquals("/my test/path.html", resource.getPath());
        assertEquals(Locale.ITALIAN, resource.getLocale());
        resource = new URLApplicationResource("/my test/path.html", new URL("file:///"));
        assertEquals("/my test/path.html", resource.getLocalePath());
        assertEquals("/my test/path.html", resource.getPath());
        assertEquals(Locale.ROOT, resource.getLocale());
        resource = new URLApplicationResource("/my test/path_zz.html", new URL("file:///"));
        assertEquals("/my test/path_zz.html", resource.getLocalePath());
        assertEquals("/my test/path_zz.html", resource.getPath());
        assertEquals(Locale.ROOT, resource.getLocale());
        resource = new URLApplicationResource("/my test/path_en_ZZ.html", new URL("file:///"));
        assertEquals("/my test/path_en.html", resource.getLocalePath());
        assertEquals("/my test/path.html", resource.getPath());
        assertEquals(new Locale("en"), resource.getLocale());
        resource = new URLApplicationResource("/my test/path_tiles.html", new URL("file:///"));
        assertEquals("/my test/path_tiles.html", resource.getLocalePath());
        assertEquals("/my test/path_tiles.html", resource.getPath());
        assertEquals(Locale.ROOT, resource.getLocale());
        resource = new URLApplicationResource("/my test/path_longwordthatbreaksISO639.html", new URL("file:///"));
        assertEquals("/my test/path_longwordthatbreaksISO639.html", resource.getLocalePath());
        assertEquals("/my test/path_longwordthatbreaksISO639.html", resource.getPath());
        assertEquals(Locale.ROOT, resource.getLocale());
        resource = new URLApplicationResource("/my test/path_en_tiles.html", new URL("file:///"));
        assertEquals("/my test/path_en.html", resource.getLocalePath());
        assertEquals("/my test/path.html", resource.getPath());
        assertEquals(new Locale("en"), resource.getLocale());
        resource = new URLApplicationResource("/my test/path_en_longwordthatbreaksISO3166.html", new URL("file:///"));
        assertEquals("/my test/path_en.html", resource.getLocalePath());
        assertEquals("/my test/path.html", resource.getPath());
        assertEquals(new Locale("en"), resource.getLocale());
    }

    @Test
    public void testBuildFromStringAndLocale() throws MalformedURLException {
        URLApplicationResource resource = new URLApplicationResource("/my test/path.html", new Locale("en", "GB", "scotland"), new URL("file:///"));
        assertEquals("/my test/path_en_GB_scotland.html", resource.getLocalePath());
        assertEquals("/my test/path.html", resource.getPath());
        assertEquals(new Locale("en", "GB", "scotland"), resource.getLocale());
        resource = new URLApplicationResource("/my test/path.html", Locale.ITALY, new URL("file:///"));
        assertEquals("/my test/path_it_IT.html", resource.getLocalePath());
        assertEquals("/my test/path.html", resource.getPath());
        assertEquals(Locale.ITALY, resource.getLocale());
        resource = new URLApplicationResource("/my test/path.html", Locale.ITALIAN, new URL("file:///"));
        assertEquals("/my test/path_it.html", resource.getLocalePath());
        assertEquals("/my test/path.html", resource.getPath());
        assertEquals(Locale.ITALIAN, resource.getLocale());
        resource = new URLApplicationResource("/my test/path.html", Locale.ROOT, new URL("file:///"));
        assertEquals("/my test/path.html", resource.getLocalePath());
        assertEquals("/my test/path.html", resource.getPath());
        assertEquals(Locale.ROOT, resource.getLocale());
    }

    @Test
    public void testGetLastModified() throws IOException {
        URL url = getClass().getClassLoader().getResource("org/apache/tiles/request/locale/resource.txt");
        assertNotNull(url);
        URLApplicationResource resource = new URLApplicationResource("org/apache/tiles/request/locale/resource.txt", url);
        assertTrue(resource.getLastModified() > 0);
    }

    @Test
    public void testGetLastModifiedWithSpace() throws IOException {
        URL url = getClass().getClassLoader().getResource("org/apache/tiles/request/locale/resource with space.txt");
        assertNotNull(url);
        URLApplicationResource resource = new URLApplicationResource("org/apache/tiles/request/locale/resource with space.txt", url);
        assertTrue(resource.getLastModified() > 0);
    }

    @Test
    public void testGetInputStream() throws IOException {
        URL url = getClass().getClassLoader().getResource("org/apache/tiles/request/locale/resource.txt");
        assertNotNull(url);
        URLApplicationResource resource = new URLApplicationResource("org/apache/tiles/request/locale/resource.txt", url);
        InputStream is = resource.getInputStream();
        assertNotNull(is);
        is.close();
    }

    @Test
    public void testGetInputStreamWithSpace() throws IOException {
        URL url = getClass().getClassLoader().getResource("org/apache/tiles/request/locale/resource with space.txt");
        assertNotNull(url);
        URLApplicationResource resource = new URLApplicationResource("org/apache/tiles/request/locale/resource with space.txt", url);
        InputStream is = resource.getInputStream();
        assertNotNull(is);
        is.close();
    }

    @Test(expected = FileNotFoundException.class)
    public void testLocalProtocol() throws IOException {
        URL url = new URL("file://foo/bar.txt");
        URLApplicationResource resource = new URLApplicationResource("org/apache/tiles/request/locale/resource.txt", url);
        resource.getInputStream();
    }

}
