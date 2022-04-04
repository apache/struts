/*
 * $Id$
 *
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
package org.apache.struts2.convention;

import junit.framework.TestCase;

/**
 * <p>
 * This class tests the SEO name builder.
 * </p>
 */
public class SEOActionNameBuilderTest extends TestCase {
    public void testEmptyActionName() {
        SEOActionNameBuilder builder = new SEOActionNameBuilder("true", "_");
        try {
            builder.build("Action");
            fail("Should have failed");
        } catch (IllegalStateException e) {
            //ok
        }
    }
    
    public void testBuild() throws Exception {
        SEOActionNameBuilder builder = new SEOActionNameBuilder("true", "_");
        assertEquals("foo", builder.build("Foo"));
        assertEquals("foo", builder.build("FooAction"));
        assertEquals("foo_bar", builder.build("FooBarAction"));
        assertEquals("foo_bar_baz", builder.build("FooBarBazAction"));
    }

    public void testDash() throws Exception {
        SEOActionNameBuilder builder = new SEOActionNameBuilder("true", "-");
        assertEquals("foo", builder.build("Foo"));
        assertEquals("foo", builder.build("FooAction"));
        assertEquals("foo-bar", builder.build("FooBarAction"));
        assertEquals("foo-bar-baz", builder.build("FooBarBazAction"));
    }
}