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
package org.apache.struts2.dispatcher;

import java.io.IOException;
import org.apache.struts2.StrutsInternalTestCase;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;

public class DefaultStaticContentLoaderTest extends StrutsInternalTestCase {

    private HttpServletRequest requestMock;
    private HttpServletResponse responseMock;
    private DefaultStaticContentLoader defaultStaticContentLoader;

    public void testParsePackages() {
        DefaultStaticContentLoader filterDispatcher = new DefaultStaticContentLoader();
        List<String> result1 = filterDispatcher.parse("foo.bar.package1 foo.bar.package2 foo.bar.package3");
        List<String> result2 = filterDispatcher.parse("foo.bar.package1\tfoo.bar.package2\tfoo.bar.package3");
        List<String> result3 = filterDispatcher.parse("foo.bar.package1,foo.bar.package2,foo.bar.package3");
        List<String> result4 = filterDispatcher.parse("foo.bar.package1    foo.bar.package2  \t foo.bar.package3   , foo.bar.package4");

        assertEquals(result1.get(0), "foo/bar/package1/");
        assertEquals(result1.get(1), "foo/bar/package2/");
        assertEquals(result1.get(2), "foo/bar/package3/");

        assertEquals(result2.get(0), "foo/bar/package1/");
        assertEquals(result2.get(1), "foo/bar/package2/");
        assertEquals(result2.get(2), "foo/bar/package3/");

        assertEquals(result3.get(0), "foo/bar/package1/");
        assertEquals(result3.get(1), "foo/bar/package2/");
        assertEquals(result3.get(2), "foo/bar/package3/");

        assertEquals(result4.get(0), "foo/bar/package1/");
        assertEquals(result4.get(1), "foo/bar/package2/");
        assertEquals(result4.get(2), "foo/bar/package3/");
        assertEquals(result4.get(3), "foo/bar/package4/");
    }

    /**
     * Test to exercise the code path and prove findStaticResource() will output 
     * the desired log warning when an IOException is thrown.
     */
    public void testFindStaticResourceIOException() {
        expect(requestMock.getDateHeader("If-Modified-Since")).andStubReturn(0L);
        try {
            responseMock.sendError(HttpServletResponse.SC_NOT_FOUND);
            expectLastCall().andStubThrow(new IOException("Fake IO Exception (SC_NOT_FOUND)"));
            replay(responseMock);
        } catch (IOException ioe) {
            fail("Mock sendError call setup failed.  Ex: " + ioe);
        }
        try {
            defaultStaticContentLoader.findStaticResource("/static/fake.html", requestMock, responseMock);
        } catch (IOException ioe) {
            fail("DefaultStaticContentLoader.findStaticResource() call failed.  Ex: " + ioe);
        }
    }

    /**
     * Test to exercise the code path and prove findStaticResource() will output 
     * the desired log warning when an IllegalStateException is thrown.
     */
    public void testFindStaticResourceIllegalStateException() {
        expect(requestMock.getDateHeader("If-Modified-Since")).andStubReturn(0L);
        try {
            expect(responseMock.isCommitted()).andStubReturn(Boolean.TRUE);
            responseMock.sendError(HttpServletResponse.SC_NOT_FOUND);
            expectLastCall().andStubThrow(new IllegalStateException("Fake IllegalState Exception (SC_NOT_FOUND)"));
            replay(responseMock);
        } catch (IOException ioe) {
            fail("Mock sendError call setup failed.  Ex: " + ioe);
        }
        try {
            defaultStaticContentLoader.findStaticResource("/static/fake.html", requestMock, responseMock);
        } catch (IOException ioe) {
            fail("DefaultStaticContentLoader.findStaticResource() call failed.  Ex: " + ioe);
        }
    }

    public void testStaticContentPath() {
        // given
        DefaultStaticContentLoader loader = new DefaultStaticContentLoader();

        // when
        loader.setStaticContentPath(null);
        // then
        assertEquals(StaticContentLoader.DEFAULT_STATIC_CONTENT_PATH, loader.uiStaticContentPath);

        // when
        loader.setStaticContentPath(" ");
        // then
        assertEquals(StaticContentLoader.DEFAULT_STATIC_CONTENT_PATH, loader.uiStaticContentPath);

        // when
        loader.setStaticContentPath("content");
        // then
        assertEquals("/content", loader.uiStaticContentPath);

        // when
        loader.setStaticContentPath("/content");
        // then
        assertEquals("/content", loader.uiStaticContentPath);

        // when
        loader.setStaticContentPath("/content/");
        // then
        assertEquals("/content", loader.uiStaticContentPath);
    }

    protected void setUp() throws Exception {
        super.setUp();
        requestMock = createMock(HttpServletRequest.class);
        responseMock = createMock(HttpServletResponse.class);
        HostConfig hostConfigMock = createMock(HostConfig.class);
        expect(hostConfigMock.getInitParameter("packages")).andStubReturn(null);
        expect(hostConfigMock.getInitParameter("loggerFactory")).andStubReturn(null);
        defaultStaticContentLoader = new DefaultStaticContentLoader();
        defaultStaticContentLoader.setHostConfig(hostConfigMock);
        defaultStaticContentLoader.setEncoding("UTF-8");
        defaultStaticContentLoader.setStaticContentPath("/static");
    }
}
