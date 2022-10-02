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

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * Tests PostfixedApplicationResource.
 */
public class PostfixedApplicationResourceTest {

    private static class TestApplicationResource extends PostfixedApplicationResource {
        public TestApplicationResource(String path, Locale locale) {
            super(path, locale);
        }

        public TestApplicationResource(String localePath) {
            super(localePath);
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        public long getLastModified() throws IOException {
            return 0;
        }

    }

    /**
     * Test getLocalePath(String path, Locale locale).
     */
    @Test
    public void testGetLocalePath() {
        TestApplicationResource resource = new TestApplicationResource("/my test/path_fr.html");
        assertEquals("/my test/path.html", resource.getLocalePath(null));
        assertEquals("/my test/path.html", resource.getLocalePath(Locale.ROOT));
        assertEquals("/my test/path_it.html", resource.getLocalePath(Locale.ITALIAN));
        assertEquals("/my test/path_it_IT.html", resource.getLocalePath(Locale.ITALY));
        assertEquals("/my test/path_en_GB_scotland.html", resource.getLocalePath(new Locale("en", "GB", "scotland")));
    }

    @Test
    public void testBuildFromString() {
        TestApplicationResource resource = new TestApplicationResource("/my test/path_en_GB_scotland.html");
        assertEquals("/my test/path_en_GB_scotland.html", resource.getLocalePath());
        assertEquals("/my test/path.html", resource.getPath());
        assertEquals(new Locale("en", "GB", "scotland"), resource.getLocale());
        resource = new TestApplicationResource("/my test/path_it_IT.html");
        assertEquals("/my test/path_it_IT.html", resource.getLocalePath());
        assertEquals("/my test/path.html", resource.getPath());
        assertEquals(Locale.ITALY, resource.getLocale());
        resource = new TestApplicationResource("/my test/path_it.html");
        assertEquals("/my test/path_it.html", resource.getLocalePath());
        assertEquals("/my test/path.html", resource.getPath());
        assertEquals(Locale.ITALIAN, resource.getLocale());
        resource = new TestApplicationResource("/my test/path.html");
        assertEquals("/my test/path.html", resource.getLocalePath());
        assertEquals("/my test/path.html", resource.getPath());
        assertEquals(Locale.ROOT, resource.getLocale());
        resource = new TestApplicationResource("/my test/path_zz.html");
        assertEquals("/my test/path_zz.html", resource.getLocalePath());
        assertEquals("/my test/path_zz.html", resource.getPath());
        assertEquals(Locale.ROOT, resource.getLocale());
        resource = new TestApplicationResource("/my test/path_en_ZZ.html");
        assertEquals("/my test/path_en.html", resource.getLocalePath());
        assertEquals("/my test/path.html", resource.getPath());
        assertEquals(new Locale("en"), resource.getLocale());
        resource = new TestApplicationResource("/my test/path_tiles.html");
        assertEquals("/my test/path_tiles.html", resource.getLocalePath());
        assertEquals("/my test/path_tiles.html", resource.getPath());
        assertEquals(Locale.ROOT, resource.getLocale());
        resource = new TestApplicationResource("/my test/path_longwordthatbreaksISO639.html");
        assertEquals("/my test/path_longwordthatbreaksISO639.html", resource.getLocalePath());
        assertEquals("/my test/path_longwordthatbreaksISO639.html", resource.getPath());
        assertEquals(Locale.ROOT, resource.getLocale());
        resource = new TestApplicationResource("/my test/path_en_tiles.html");
        assertEquals("/my test/path_en.html", resource.getLocalePath());
        assertEquals("/my test/path.html", resource.getPath());
        assertEquals(new Locale("en"), resource.getLocale());
        resource = new TestApplicationResource("/my test/path_en_longwordthatbreaksISO3166.html");
        assertEquals("/my test/path_en.html", resource.getLocalePath());
        assertEquals("/my test/path.html", resource.getPath());
        assertEquals(new Locale("en"), resource.getLocale());
    }

    @Test
    public void testBuildFromStringAndLocale() {
        TestApplicationResource resource = new TestApplicationResource("/my test/path.html", new Locale("en", "GB", "scotland"));
        assertEquals("/my test/path_en_GB_scotland.html", resource.getLocalePath());
        assertEquals("/my test/path.html", resource.getPath());
        assertEquals(new Locale("en", "GB", "scotland"), resource.getLocale());
        resource = new TestApplicationResource("/my test/path.html", Locale.ITALY);
        assertEquals("/my test/path_it_IT.html", resource.getLocalePath());
        assertEquals("/my test/path.html", resource.getPath());
        assertEquals(Locale.ITALY, resource.getLocale());
        resource = new TestApplicationResource("/my test/path.html", Locale.ITALIAN);
        assertEquals("/my test/path_it.html", resource.getLocalePath());
        assertEquals("/my test/path.html", resource.getPath());
        assertEquals(Locale.ITALIAN, resource.getLocale());
        resource = new TestApplicationResource("/my test/path.html", Locale.ROOT);
        assertEquals("/my test/path.html", resource.getLocalePath());
        assertEquals("/my test/path.html", resource.getPath());
        assertEquals(Locale.ROOT, resource.getLocale());
    }
}
