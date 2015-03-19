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

package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.views.util.DefaultUrlHelper;
import org.easymock.IMocksControl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expect;


/**
 * @version $Date$ $Id$
 */
public class ServletActionRedirectResultTest extends StrutsInternalTestCase {

    public void testIncludeParameterInResultWithConditionParseOn() throws Exception {

        ResultConfig resultConfig = new ResultConfig.Builder("", "")
            .addParam("actionName", "someActionName")
            .addParam("namespace", "someNamespace")
            .addParam("encode", "true")
            .addParam("parse", "true")
            .addParam("location", "someLocation")
            .addParam("prependServletContext", "true")
            .addParam("method", "someMethod")
			.addParam("statusCode", "333")
			.addParam("param1", "${#value1}")
            .addParam("param2", "${#value2}")
            .addParam("param3", "${#value3}")
            .addParam("anchor", "${#fragment}")
            .build();



        ActionContext context = ActionContext.getContext();
        ValueStack stack = context.getValueStack();
        context.getContextMap().put("value1", "value 1");
        context.getContextMap().put("value2", "value 2");
        context.getContextMap().put("value3", "value 3");
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        context.put(ServletActionContext.HTTP_REQUEST, req);
        context.put(ServletActionContext.HTTP_RESPONSE, res);


        Map<String, ResultConfig> results=  new HashMap<String, ResultConfig>();
        results.put("myResult", resultConfig);

        ActionConfig actionConfig = new ActionConfig.Builder("", "", "")
                .addResultConfigs(results).build();

        ServletActionRedirectResult result = new ServletActionRedirectResult();
        result.setActionName("myAction");
        result.setNamespace("/myNamespace");
        result.setParse(true);
        result.setEncode(false);
        result.setPrependServletContext(false);
        result.setAnchor("fragment");
        result.setUrlHelper(new DefaultUrlHelper());

        IMocksControl control = createControl();
        ActionProxy mockActionProxy = control.createMock(ActionProxy.class);
        ActionInvocation mockInvocation = control.createMock(ActionInvocation.class);
        expect(mockInvocation.getProxy()).andReturn(mockActionProxy);
        expect(mockInvocation.getResultCode()).andReturn("myResult");
        expect(mockActionProxy.getConfig()).andReturn(actionConfig);
        expect(mockInvocation.getInvocationContext()).andReturn(context);
        expect(mockInvocation.getStack()).andReturn(stack).anyTimes();

        control.replay();
        result.setActionMapper(container.getInstance(ActionMapper.class));
        result.execute(mockInvocation);
        assertEquals("/myNamespace/myAction.action?param1=value+1&param2=value+2&param3=value+3#fragment", res.getRedirectedUrl());

        control.verify();
    }

    public void testIncludeParameterInResult() throws Exception {

        ResultConfig resultConfig = new ResultConfig.Builder("", "")
            .addParam("actionName", "someActionName")
            .addParam("namespace", "someNamespace")
            .addParam("encode", "true")
            .addParam("parse", "true")
            .addParam("location", "someLocation")
            .addParam("prependServletContext", "true")
            .addParam("method", "someMethod")
            .addParam("param1", "value 1")
            .addParam("param2", "value 2")
            .addParam("param3", "value 3")
            .addParam("anchor", "fragment")
            .build();

        ActionContext context = ActionContext.getContext();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        context.put(ServletActionContext.HTTP_REQUEST, req);
        context.put(ServletActionContext.HTTP_RESPONSE, res);


        Map<String, ResultConfig> results=  new HashMap<String, ResultConfig>();
        results.put("myResult", resultConfig);

        ActionConfig actionConfig = new ActionConfig.Builder("", "", "")
                .addResultConfigs(results).build();

        ServletActionRedirectResult result = new ServletActionRedirectResult();
        result.setActionName("myAction");
        result.setNamespace("/myNamespace");
        result.setParse(false);
        result.setEncode(false);
        result.setPrependServletContext(false);
        result.setAnchor("fragment");
        result.setUrlHelper(new DefaultUrlHelper());

        IMocksControl control = createControl();
        ActionProxy mockActionProxy = control.createMock(ActionProxy.class);
        ActionInvocation mockInvocation = control.createMock(ActionInvocation.class);
        expect(mockInvocation.getProxy()).andReturn(mockActionProxy);
        expect(mockInvocation.getResultCode()).andReturn("myResult");
        expect(mockActionProxy.getConfig()).andReturn(actionConfig);
        expect(mockInvocation.getInvocationContext()).andReturn(context);

        control.replay();
        result.setActionMapper(container.getInstance(ActionMapper.class));
        result.execute(mockInvocation);
        assertEquals("/myNamespace/myAction.action?param1=value+1&param2=value+2&param3=value+3#fragment", res.getRedirectedUrl());

        control.verify();
    }

    public void testBuildResultWithParameter() throws Exception {

        ResultConfig resultConfig = new ResultConfig.Builder("", ServletActionRedirectResult.class.getName())
            .addParam("actionName", "someActionName")
            .addParam("namespace", "someNamespace")
            .addParam("encode", "true")
            .addParam("parse", "true")
            .addParam("location", "someLocation")
            .addParam("prependServletContext", "true")
            .addParam("method", "someMethod")
            .addParam("param1", "value 1")
            .addParam("param2", "value 2")
            .addParam("param3", "value 3")
            .addParam("anchor", "fragment")
            .build();

        ObjectFactory factory = container.getInstance(ObjectFactory.class);
        ServletActionRedirectResult result = (ServletActionRedirectResult) factory.buildResult(resultConfig, new HashMap<String, Object>());
        assertNotNull(result);
    }
    
}
