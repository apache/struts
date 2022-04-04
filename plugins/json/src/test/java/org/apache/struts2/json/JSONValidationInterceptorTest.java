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

package org.apache.struts2.json;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.mock.MockActionProxy;
import com.opensymphony.xwork2.validator.annotations.EmailValidator;
import com.opensymphony.xwork2.validator.annotations.IntRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.StringLengthFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.interceptor.validation.AnnotationValidationInterceptor;
import org.apache.struts2.interceptor.validation.SkipValidation;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

public class JSONValidationInterceptorTest extends StrutsTestCase {

    private MockActionInvocation invocation;
    private StringWriter stringWriter;
    private TestAction action;
    private StrutsMockHttpServletResponse response;
    private JSONValidationInterceptor interceptor;
    private StrutsMockHttpServletRequest request;
    private AnnotationValidationInterceptor validationInterceptor;

    public void testValidationFails() throws Exception {
        
        action.addActionError("General error");
        
        Map parameters = new HashMap();
        parameters.put("struts.enableJSONValidation", "true");
        request.setParameterMap(parameters);
        
        validationInterceptor.intercept(invocation);
        interceptor.setValidationFailedStatus(HttpServletResponse.SC_BAD_REQUEST);
        interceptor.intercept(invocation);

        String json = stringWriter.toString();

        String normalizedActual = TestUtils.normalize(json, true);

        //json
        assertThat(normalizedActual)
                .contains("\"errors\":[\"Generalerror\"]")
                .contains("\"fieldErrors\":{")
                .contains("\"value\":[\"Minvalueis-1\"]")
                .contains("\"text\":[\"Tooshort\",\"Thisisnoemail\"]")
                .contains("\"password\":[\"Passwordisn'tcorrect\"]");

        //execution
        assertFalse(action.isExecuted());
        //http status
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        assertEquals("application/json", response.getContentType());
        assertEquals("UTF-8", response.getCharacterEncoding());
    }

    public void testValidationSucceeds() throws Exception {
        JSONValidationInterceptor interceptor = new JSONValidationInterceptor();

        action.setText("abcd@ggg.com");
        action.setPassword("apassword");
        action.setValue(10);
        
        Map parameters = new HashMap();
        parameters.put("struts.enableJSONValidation", "true");
        request.setParameterMap(parameters);

        validationInterceptor.intercept(invocation);
        interceptor.intercept(invocation);

        String json = stringWriter.toString();

        String normalizedActual = TestUtils.normalize(json, true);
        assertEquals("", normalizedActual);
    }
    
    public void testValidationSucceedsValidateOnly() throws Exception {
        JSONValidationInterceptor interceptor = new JSONValidationInterceptor();

        action.setText("abcd@ggg.com");
        action.setPassword("apassword");
        action.setValue(10);

        //just validate
        Map parameters = new HashMap();
        parameters.put("struts.validateOnly", "true");
        parameters.put("struts.enableJSONValidation", "true");
        request.setParameterMap(parameters);
        
        validationInterceptor.intercept(invocation);
        interceptor.intercept(invocation);

        String json = stringWriter.toString();

        String normalizedActual = TestUtils.normalize(json, true);
        assertEquals("{}", normalizedActual);
        assertFalse(action.isExecuted());
        assertEquals("application/json", response.getContentType());
        assertEquals("UTF-8", response.getCharacterEncoding());
    }

    protected void setUp() throws Exception {
        super.setUp();
        ActionConfig config = new ActionConfig.Builder("", "name", "").build();
        this.action = new TestAction();
        this.interceptor = new JSONValidationInterceptor();
        this.validationInterceptor = new AnnotationValidationInterceptor();
        container.inject(validationInterceptor);
        this.request = new StrutsMockHttpServletRequest();
        stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        this.response = new StrutsMockHttpServletResponse();
        response.setWriter(writer);

        ActionContext context = ActionContext.getContext();

        context.put(StrutsStatics.HTTP_REQUEST, request);
        context.put(StrutsStatics.HTTP_RESPONSE, response);

        StrutsMockServletContext servletContext = new StrutsMockServletContext();

        context.put(StrutsStatics.SERVLET_CONTEXT, servletContext);
        invocation = new MockActionInvocation();
        ActionContext.getContext().setActionInvocation(invocation);
        invocation.setAction(action);
        invocation.setInvocationContext(context);
        MockActionProxy proxy = new MockActionProxy();
        proxy.setMethod("execute");
        proxy.setAction(action);
        proxy.setConfig(config);
        invocation.setProxy(proxy);
    }

    public static class TestAction extends ActionSupport {
        private String text = "x";
        private int value = -10;
        private boolean executed = false;
        private String password;

        public String execute() {
            executed = true;
            return Action.SUCCESS;
        }

        @SkipValidation
        public String skipMe() {
            return "skipme";
        }

        public String getText() {
            return text;
        }

        @StringLengthFieldValidator(minLength = "2", message = "Too short")
        @EmailValidator(message = "This is no email")
        public void setText(String text) {
            this.text = text;
        }

        @RequiredStringValidator(message = "Password isn't correct")
        public void setPassword(String password) {
            this.password = password;
        }

        public String getPassword() {
            return password;
        }

        public int getValue() {
            return value;
        }

        @IntRangeFieldValidator(min = "-1", message = "Min value is -1")
        public void setValue(int value) {
            this.value = value;
        }

        public boolean isExecuted() {
            return executed;
        }
    }
}
