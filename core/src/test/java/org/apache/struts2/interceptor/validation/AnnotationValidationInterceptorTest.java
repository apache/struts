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

package org.apache.struts2.interceptor.validation;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import org.apache.struts2.StrutsInternalTestCase;

public class AnnotationValidationInterceptorTest extends StrutsInternalTestCase {

    private AnnotationValidationInterceptor interceptor = new AnnotationValidationInterceptor();
    private Mock mockActionInvocation;
    private Mock mockActionProxy;
    private TestAction test;
    private ActionConfig config;

    public void setUp() throws Exception {
        super.setUp();
        test = new TestAction();
        interceptor = new AnnotationValidationInterceptor();
        container.inject(interceptor);
        config = new ActionConfig.Builder("", "foo", "").build();
        mockActionInvocation = new Mock(ActionInvocation.class);
        mockActionProxy = new Mock(ActionProxy.class);
        mockActionInvocation.matchAndReturn("getProxy", (ActionProxy) mockActionProxy.proxy());
        mockActionInvocation.matchAndReturn("getAction", test);
        mockActionInvocation.expect("invoke");

        ActionContext.getContext().setActionInvocation((ActionInvocation) mockActionInvocation.proxy());
    }

    public void testShouldNotSkip() throws Exception {
        mockActionProxy.expectAndReturn("getActionName", "foo");
        mockActionProxy.expectAndReturn("getMethod", "execute");
        mockActionProxy.expectAndReturn("getConfig", config);
        mockActionProxy.expectAndReturn("getMethod", "execute");
        interceptor.doIntercept((ActionInvocation)mockActionInvocation.proxy());
        mockActionProxy.verify();
    }

    public void testShouldSkip() throws Exception {
        mockActionProxy.expectAndReturn("getMethod", "skipMe");
        interceptor.doIntercept((ActionInvocation)mockActionInvocation.proxy());
        mockActionProxy.verify();
    }

    public void testShouldSkipBase() throws Exception {
        mockActionProxy.expectAndReturn("getMethod", "skipMeBase");
        interceptor.doIntercept((ActionInvocation)mockActionInvocation.proxy());
        mockActionProxy.verify();
    }

    public void testShouldSkipBase2() throws Exception {
        mockActionProxy.expectAndReturn("getMethod", "skipMeBase2");
        interceptor.doIntercept((ActionInvocation)mockActionInvocation.proxy());
        mockActionProxy.verify();
    }

    public void testShouldSkip2() throws Exception {
        mockActionProxy.expectAndReturn("getMethod", "skipMe2");
        interceptor.doIntercept((ActionInvocation)mockActionInvocation.proxy());
        mockActionProxy.verify();
    }

    public void testShouldNotSkipBase() throws Exception {
        mockActionProxy.expectAndReturn("getActionName", "foo");
        mockActionProxy.expectAndReturn("getMethod", "execute");
        mockActionProxy.expectAndReturn("getConfig", config);
        mockActionProxy.expectAndReturn("getMethod", "dontSkipMeBase");
        interceptor.doIntercept((ActionInvocation)mockActionInvocation.proxy());
        mockActionProxy.verify();
    }

    public static class TestAction extends TestActionBase {

        public String execute() {
            return "execute";
        }

        @SkipValidation
        public String skipMe() {
            return "skipme";
        }

        @SkipValidation
        public String skipMe2() {
            return "skipme2";
        }

        public String skipMeBase() {
            return "skipme";
        }
    }

    public static class TestActionBase  {

        @SkipValidation
        public String skipMeBase() {
            return "skipme";
        }

        @SkipValidation
        public String skipMeBase2() {
            return "skipme";
        }

        public String dontSkipMeBase() {
            return "dontskipme";
        }

        public String skipMe2() {
            return "skipme2";
        }
    }

}
