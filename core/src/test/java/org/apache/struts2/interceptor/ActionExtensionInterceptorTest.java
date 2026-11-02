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
package org.apache.struts2.interceptor;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.struts2.ActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.TestAction;
import org.apache.struts2.action.Action;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.config.entities.ResultConfig;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.mock.MockActionInvocation;
import org.apache.struts2.mock.MockActionProxy;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ActionExtensionInterceptorTest extends StrutsInternalTestCase {

    private ActionExtensionInterceptor interceptor;
    private MockActionInvocation invocation;
    private MockHttpServletResponse response;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        interceptor = new ActionExtensionInterceptor();
    }

    public void testUndeclaredPasses() throws Exception {
        prepare(config(null), "second", "second", "html");

        assertThat(interceptor.intercept(invocation)).isEqualTo("success");
    }

    public void testAllowedPasses() throws Exception {
        prepare(config(List.of("html")), "second", "second", "html");

        assertThat(interceptor.intercept(invocation)).isEqualTo("success");
    }

    public void testNullMappingExtensionCountsAsEmpty() throws Exception {
        prepare(config(List.of("action", "")), "second", "second", null);

        assertThat(interceptor.intercept(invocation)).isEqualTo("success");
    }

    public void testRejectedWithoutResultSends404() throws Exception {
        prepare(config(List.of("html")), "second", "second", "action");

        assertThat(interceptor.intercept(invocation)).isEqualTo(Action.NONE);
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_NOT_FOUND);
    }

    public void testRejectedUsesBadExtensionResult() throws Exception {
        prepare(config(List.of("html"), "bad-extension"), "second", "second", "action");

        assertThat(interceptor.intercept(invocation)).isEqualTo("bad-extension");
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
    }

    public void testCustomResultName() throws Exception {
        interceptor.setBadExtensionResultName("wrong-ext");
        prepare(config(List.of("html"), "wrong-ext"), "second", "second", "action");

        assertThat(interceptor.intercept(invocation)).isEqualTo("wrong-ext");
    }

    public void testChainedInvocationNotEnforced() throws Exception {
        prepare(config(List.of("html")), "first", "second", "action");

        assertThat(interceptor.intercept(invocation)).isEqualTo("success");
    }

    public void testNoMappingPasses() throws Exception {
        prepare(config(List.of("html")), null, "second", null);

        assertThat(interceptor.intercept(invocation)).isEqualTo("success");
    }

    private ActionConfig config(List<String> extensions, String... resultNames) {
        ActionConfig.Builder builder = new ActionConfig.Builder("pkg", "second", TestAction.class.getName())
            .extensions(extensions);
        for (String resultName : resultNames) {
            builder.addResultConfig(new ResultConfig.Builder(resultName, "result").build());
        }
        return builder.build();
    }

    private void prepare(ActionConfig config, String mappingName, String proxyName, String extension) {
        MockActionProxy proxy = new MockActionProxy();
        proxy.setConfig(config);
        proxy.setActionName(proxyName);
        proxy.setNamespace("/");
        invocation = new MockActionInvocation();
        invocation.setProxy(proxy);
        invocation.setAction(new TestAction());
        invocation.setResultCode("success");
        response = new MockHttpServletResponse();
        ActionContext context = ActionContext.getContext()
            .withServletRequest(new MockHttpServletRequest("GET", "/x"))
            .withServletResponse(response);
        if (mappingName != null) {
            ActionMapping mapping = new ActionMapping(mappingName, "/", "", null);
            mapping.setExtension(extension);
            context.withActionMapping(mapping);
        }
        invocation.setInvocationContext(context);
    }
}
