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

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;


public class StaticContentLoaderTest extends TestCase {

    private DefaultStaticContentLoader contentLoader;
    private MockHttpServletRequest req;
    private MockHttpServletResponse res;

    public void testCantHandleWithoutServingStatic() {
        DefaultStaticContentLoader contentLoader = new DefaultStaticContentLoader();

        assertFalse(contentLoader.canHandle("/static/test1.css"));
        assertFalse(contentLoader.canHandle("/struts/test1.css"));
        assertFalse(contentLoader.canHandle("test1.css"));
    }

    public void testCanHandle() {
        DefaultStaticContentLoader contentLoader = new DefaultStaticContentLoader();
        contentLoader.setServeStaticContent("true");
        contentLoader.setStaticContentPath("/static");

        assertTrue(contentLoader.canHandle("/static/test1.css"));
        assertFalse(contentLoader.canHandle("/struts/test1.css"));
        assertFalse(contentLoader.canHandle("test1.css"));
    }

    public void testValidResources() throws IOException {
        contentLoader.findStaticResource("/static/resource.css", req, res);
        assertTrue(res.getContentAsString().contains("color: red;"));
    }

    public void testInvalidRersources1() throws IOException {
        contentLoader.findStaticResource("/struts..", req, res);
        assertEquals(HttpServletResponse.SC_NOT_FOUND, res.getStatus());
        assertEquals(0, res.getContentLength());
    }

    public void testInvalidRersources2() throws IOException {
        contentLoader.findStaticResource("/static/..", req, res);
        assertEquals(HttpServletResponse.SC_NOT_FOUND, res.getStatus());
        assertEquals(0, res.getContentLength());
    }

    public void testInvalidRersources3() throws IOException {
        contentLoader.findStaticResource("/static/../othertest.properties", req, res);
        assertEquals(HttpServletResponse.SC_NOT_FOUND, res.getStatus());
        assertEquals(0, res.getContentLength());
    }

    public void testInvalidRersources4() throws IOException {
        contentLoader.findStaticResource("/static/..%252f", req, res);
        assertEquals(HttpServletResponse.SC_NOT_FOUND, res.getStatus());
        assertEquals(0, res.getContentLength());
    }

    public void testInvalidRersources5() throws IOException {
        contentLoader.findStaticResource("/static/..%252fothertest.properties", req, res);
        assertEquals(HttpServletResponse.SC_NOT_FOUND, res.getStatus());
        assertEquals(0, res.getContentLength());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.contentLoader = new DefaultStaticContentLoader();
        this.contentLoader.setStaticContentPath("/static");
        MockServletContext servletContext = new MockServletContext();
        req = new MockHttpServletRequest(servletContext);
        res = new MockHttpServletResponse();


        Mock hostConfigMock = new Mock(HostConfig.class);
        hostConfigMock.expectAndReturn("getInitParameter", C.args(C.eq("packages")), null);
        hostConfigMock.expectAndReturn("getInitParameter", C.args(C.eq("loggerFactory")), null);

        contentLoader.setEncoding("utf-8");

        contentLoader.setHostConfig((HostConfig) hostConfigMock.proxy());
    }


}
