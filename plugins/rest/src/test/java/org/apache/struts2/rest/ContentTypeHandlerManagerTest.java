/*
 * $Id: Restful2ActionMapper.java 540819 2007-05-23 02:48:36Z mrdon $
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
package org.apache.struts2.rest;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.inject.Container;
import junit.framework.TestCase;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.rest.handler.ContentTypeHandler;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static javax.servlet.http.HttpServletResponse.SC_NOT_MODIFIED;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ContentTypeHandlerManagerTest extends TestCase {

    private ContentTypeHandlerManager mgr;
    private MockHttpServletResponse mockResponse;
    private MockHttpServletRequest mockRequest;

    @Override
    public void setUp() {
        mgr = new ContentTypeHandlerManager();
        mockResponse = new MockHttpServletResponse();
        mockRequest = new MockHttpServletRequest();
        mockRequest.setMethod("GET");
        ActionContext.setContext(new ActionContext(new HashMap()));
        ServletActionContext.setRequest(mockRequest);
        ServletActionContext.setResponse(mockResponse);
    }

    @Override
    public void tearDown() {
        mockRequest = null;
        mockRequest = null;
        mgr = null;
    }

    public void testHandleResultOK() throws IOException {

        String obj = "mystring";
        ContentTypeHandler handler = new ContentTypeHandler() {
            public void toObject(Reader in, Object target) {}
            public String fromObject(Object obj, String resultCode, Writer stream) throws IOException {
                stream.write(obj.toString());
                return resultCode;
            }
            public String getContentType() { return "foo"; }
            public String getExtension() { return "foo"; }
        };
        mgr.handlers.put("xml", handler);
        mgr.defaultExtension = "xml";
        mgr.handleResult(new ActionConfig(), new DefaultHttpHeaders().withStatus(SC_OK), obj);

        assertEquals(obj.getBytes().length, mockResponse.getContentLength());
    }

    public void testHandleResultNotModified() throws IOException {

        Mock mockHandlerXml = new Mock(ContentTypeHandler.class);
        mockHandlerXml.matchAndReturn("getExtension", "xml");
        mgr.handlers.put("xml", (ContentTypeHandler) mockHandlerXml.proxy());
        mgr.handleResult(null, new DefaultHttpHeaders().withStatus(SC_NOT_MODIFIED), new Object());

        assertEquals(0, mockResponse.getContentLength());
    }

    public void testHandlerOverride() {
        Mock mockHandlerXml = new Mock(ContentTypeHandler.class);
        mockHandlerXml.matchAndReturn("getExtension", "xml");
        mockHandlerXml.matchAndReturn("toString", "xml");
        Mock mockHandlerJson = new Mock(ContentTypeHandler.class);
        mockHandlerJson.matchAndReturn("getExtension", "json");
        mockHandlerJson.matchAndReturn("toString", "json");
        Mock mockHandlerXmlOverride = new Mock(ContentTypeHandler.class);
        mockHandlerXmlOverride.matchAndReturn("getExtension", "xml");
        mockHandlerXmlOverride.matchAndReturn("toString", "xmlOverride");

        Mock mockContainer = new Mock(Container.class);
        mockContainer.matchAndReturn("getInstance", C.args(C.eq(ContentTypeHandler.class), C.eq("xmlOverride")), mockHandlerXmlOverride.proxy());
        mockContainer.matchAndReturn("getInstance", C.args(C.eq(ContentTypeHandler.class), C.eq("xml")), mockHandlerXml.proxy());
        mockContainer.matchAndReturn("getInstance", C.args(C.eq(ContentTypeHandler.class), C.eq("json")), mockHandlerJson.proxy());
        mockContainer.expectAndReturn("getInstanceNames", C.args(C.eq(ContentTypeHandler.class)), new HashSet(Arrays.asList("xml", "xmlOverride", "json")));

        mockContainer.matchAndReturn("getInstance", C.args(C.eq(String.class),
                C.eq(ContentTypeHandlerManager.STRUTS_REST_HANDLER_OVERRIDE_PREFIX+"xml")), "xmlOverride");
        mockContainer.expectAndReturn("getInstance", C.args(C.eq(String.class),
                C.eq(ContentTypeHandlerManager.STRUTS_REST_HANDLER_OVERRIDE_PREFIX+"json")), null);
        
        ContentTypeHandlerManager mgr = new ContentTypeHandlerManager();
        mgr.setContainer((Container) mockContainer.proxy());

        Map<String,ContentTypeHandler> handlers = mgr.handlers;
        assertNotNull(handlers);
        assertEquals(2, handlers.size());
        assertEquals(mockHandlerXmlOverride.proxy(), handlers.get("xml"));
        assertEquals(mockHandlerJson.proxy(), handlers.get("json"));
    }
}
