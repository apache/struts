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

import jakarta.servlet.ServletException;
import org.apache.struts2.ActionContext;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.action.Action;
import org.apache.struts2.components.URL;
import org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.ValueStackFactory;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ActionExtensionEndToEndTest extends StrutsInternalTestCase {

    private static final String EXECUTED = "actionExtensionEndToEndTest.executed";

    private StrutsPrepareAndExecuteFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    public static class RecordingAction implements Action {
        @Override
        public String execute() {
            ServletActionContext.getRequest().setAttribute(EXECUTED, Boolean.TRUE);
            return NONE;
        }
    }

    @Override
    protected void setUp() throws Exception {
        PrepareOperations.clearDevModeOverride();
        Map<String, String> params = new HashMap<>();
        params.put("config", "struts-default.xml,org/apache/struts2/dispatcher/struts-extension-one-package.xml");
        params.put(StrutsConstants.STRUTS_ACTION_EXTENSION, "action,,");
        initDispatcher(params);

        filter = new StrutsPrepareAndExecuteFilter() {
            @Override
            protected InitOperations createInitOperations() {
                return new InitOperations() {
                    @Override
                    public Dispatcher initDispatcher(HostConfig filterConfig) {
                        return dispatcher;
                    }
                };
            }
        };
        filter.init(new MockFilterConfig(servletContext));
    }

    public void testStaticHtmlFallsThrough() throws Exception {
        doFilter("/index.html");

        assertThat(filterChain.getRequest()).isNotNull();
        assertThat(request.getAttribute(EXECUTED)).isNull();
    }

    public void testDeclaredActionServedOnItsExtension() throws Exception {
        doFilter("/shop/cart.html");

        assertThat(filterChain.getRequest()).isNull();
        assertThat(request.getAttribute(EXECUTED)).isEqualTo(Boolean.TRUE);
        assertThat(response.getStatus()).isEqualTo(200);
    }

    public void testDeclaredActionRejectedOnGlobalExtension() throws Exception {
        doFilter("/shop/cart.action");

        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(request.getAttribute(EXECUTED)).isNull();
        assertThat(filterChain.getRequest()).isNull();
    }

    public void testUndeclaredActionUnchanged() throws Exception {
        doFilter("/home.action");

        assertThat(filterChain.getRequest()).isNull();
        assertThat(request.getAttribute(EXECUTED)).isEqualTo(Boolean.TRUE);
        assertThat(response.getStatus()).isEqualTo(200);

        doFilter("/home");

        assertThat(filterChain.getRequest()).isNull();
        assertThat(request.getAttribute(EXECUTED)).isEqualTo(Boolean.TRUE);
        assertThat(response.getStatus()).isEqualTo(200);
    }

    public void testUndeclaredActionNotServedOnDeclaredOnlyExtension() throws Exception {
        doFilter("/home.html");

        assertThat(filterChain.getRequest()).isNotNull();
        assertThat(request.getAttribute(EXECUTED)).isNull();
    }

    public void testLinkFromHtmlPageToUndeclaredAction() {
        MockHttpServletRequest pageRequest = new MockHttpServletRequest();
        MockHttpServletResponse pageResponse = new MockHttpServletResponse();
        ActionMapping current = new ActionMapping("cart", "/shop", null, null);
        current.setExtension("html");
        ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();
        ActionContext context = stack.getActionContext()
                .withContainer(container)
                .withValueStack(stack)
                .withActionMapping(current)
                .withServletRequest(pageRequest)
                .withServletResponse(pageResponse)
                .bind();

        URL url = new URL(context.getValueStack(), pageRequest, pageResponse);
        container.inject(url);
        url.setAction("home");
        url.setNamespace("/");
        StringWriter writer = new StringWriter();
        url.start(writer);
        url.end(writer, "");

        assertThat(writer.toString()).endsWith("/home.action");
    }

    private void doFilter(String uri) throws ServletException, IOException {
        request = new MockHttpServletRequest(servletContext);
        request.setRequestURI(uri);
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        filter.doFilter(request, response, filterChain);
    }
}
