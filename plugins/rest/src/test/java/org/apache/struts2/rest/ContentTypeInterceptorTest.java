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
package org.apache.struts2.rest;

import com.mockobjects.dynamic.AnyConstraintMatcher;
import com.mockobjects.dynamic.Mock;
import org.apache.struts2.action.Action;
import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.ActionSupport;
import junit.framework.TestCase;

import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.rest.handler.ContentTypeHandler;
import org.springframework.mock.web.MockHttpServletRequest;

public class ContentTypeInterceptorTest extends TestCase {

    public void testRequestWithoutEncoding() throws Exception {
        ContentTypeInterceptor interceptor = new ContentTypeInterceptor();

        ActionSupport action = new ActionSupport();

        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        Mock mockContentTypeHandler = new Mock(ContentTypeHandler.class);
        mockContentTypeHandler.expect("toObject", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                return true;
            }
        });
        mockActionInvocation.expectAndReturn("invoke", Action.SUCCESS);
        mockActionInvocation.expectAndReturn("getAction", action);
        Mock mockContentTypeHandlerManager = new Mock(ContentTypeHandlerManager.class);
        mockContentTypeHandlerManager.expectAndReturn("getHandlerForRequest", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                return true;
            }
        }, mockContentTypeHandler.proxy());
        interceptor.setContentTypeHandlerSelector((ContentTypeHandlerManager) mockContentTypeHandlerManager.proxy());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent(new byte[] {1});

        ActionContext.of()
            .withActionMapping(new ActionMapping())
            .withServletRequest(request)
            .bind();

        interceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
        mockContentTypeHandlerManager.verify();
        mockActionInvocation.verify();
        mockContentTypeHandler.verify();
    }

    public void testRequestWithEncodingAscii() throws Exception {
        final Charset charset = StandardCharsets.US_ASCII;

        ContentTypeInterceptor interceptor = new ContentTypeInterceptor();

        ActionSupport action = new ActionSupport();

        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        Mock mockContentTypeHandler = new Mock(ContentTypeHandler.class);
        mockContentTypeHandler.expect("toObject", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                InputStreamReader in = (InputStreamReader) args[1];
                return charset.equals(Charset.forName(in.getEncoding()));
            }
        });
        mockActionInvocation.expectAndReturn("invoke", Action.SUCCESS);
        mockActionInvocation.expectAndReturn("getAction", action);
        Mock mockContentTypeHandlerManager = new Mock(ContentTypeHandlerManager.class);
        mockContentTypeHandlerManager.expectAndReturn("getHandlerForRequest", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                return true;
            }
        }, mockContentTypeHandler.proxy());
        interceptor.setContentTypeHandlerSelector((ContentTypeHandlerManager) mockContentTypeHandlerManager.proxy());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent(new byte[] {1});
        request.setCharacterEncoding(charset.name());

        ActionContext.of()
            .withActionMapping(new ActionMapping())
            .withServletRequest(request)
            .bind();

        interceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
        mockContentTypeHandlerManager.verify();
        mockActionInvocation.verify();
        mockContentTypeHandler.verify();
    }

    public void testRequestWithEncodingUtf() throws Exception {
        final Charset charset = StandardCharsets.UTF_8;

        ContentTypeInterceptor interceptor = new ContentTypeInterceptor();

        ActionSupport action = new ActionSupport();

        Mock mockActionInvocation = new Mock(ActionInvocation.class);
        Mock mockContentTypeHandler = new Mock(ContentTypeHandler.class);
        mockContentTypeHandler.expect("toObject", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                InputStreamReader in = (InputStreamReader) args[1];
                return charset.equals(Charset.forName(in.getEncoding()));
            }
        });
        mockActionInvocation.expectAndReturn("invoke", Action.SUCCESS);
        mockActionInvocation.expectAndReturn("getAction", action);
        Mock mockContentTypeHandlerManager = new Mock(ContentTypeHandlerManager.class);
        mockContentTypeHandlerManager.expectAndReturn("getHandlerForRequest", new AnyConstraintMatcher() {
            public boolean matches(Object[] args) {
                return true;
            }
        }, mockContentTypeHandler.proxy());
        interceptor.setContentTypeHandlerSelector((ContentTypeHandlerManager) mockContentTypeHandlerManager.proxy());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContent(new byte[] {1});
        request.setCharacterEncoding(charset.name());

        ActionContext.of()
            .withActionMapping(new ActionMapping())
            .withServletRequest(request)
            .bind();

        interceptor.intercept((ActionInvocation) mockActionInvocation.proxy());
        mockContentTypeHandlerManager.verify();
        mockActionInvocation.verify();
        mockContentTypeHandler.verify();
    }
}
