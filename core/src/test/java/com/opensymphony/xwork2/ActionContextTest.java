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
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.conversion.impl.ConversionData;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import org.apache.struts2.dispatcher.HttpParameters;

import java.util.HashMap;
import java.util.Map;


/**
 * Unit test for {@link ActionContext}.
 *
 * @author Jason Carreira
 */
public class ActionContextTest extends XWorkTestCase {

    private static final String APPLICATION_KEY = ActionContextTest.class.getName() + ".application";
    private static final String SESSION_KEY = ActionContextTest.class.getName() + ".session";
    private static final String PARAMETERS_KEY = ActionContextTest.class.getName() + ".params";
    private static final String ACTION_NAME = ActionContextTest.class.getName() + ".actionName";

    private ActionContext context;
    private Map<String, Object> application = new HashMap<>();
    private Map<String, Object> session = new HashMap<>();
    private Map<String, Object> params = new HashMap<>();

    @Override public void setUp() throws Exception {
        super.setUp();
        ValueStack valueStack = container.getInstance(ValueStackFactory.class).createValueStack();
        Map<String, Object> extraContext = valueStack.getContext();

        application.put(APPLICATION_KEY, APPLICATION_KEY);
        session.put(SESSION_KEY, SESSION_KEY);
        params.put(PARAMETERS_KEY, PARAMETERS_KEY);

        context = ActionContext.of(extraContext)
            .withApplication(application)
            .withSession(session)
            .withParameters(HttpParameters.create(params).build())
            .withActionName(ACTION_NAME)
            .bind();
    }

    public void testContextParams() {
        assertTrue(ActionContext.getContext().getApplication().containsKey(APPLICATION_KEY));
        assertTrue(ActionContext.getContext().getSession().containsKey(SESSION_KEY));
        assertTrue(ActionContext.getContext().getParameters().contains(PARAMETERS_KEY));
        assertEquals(ActionContext.getContext().getName(), ACTION_NAME);
    }

    public void testGetContext() {
        ActionContext threadContext = ActionContext.getContext();
        assertEquals(context, threadContext);
    }

    public void testNewActionContextCanFindDefaultTexts() {
        ValueStack valueStack = context.getValueStack();
        String actionErrorMessage = (String) valueStack.findValue("getText('xwork.error.action.execution')");
        assertNotNull(actionErrorMessage);
        assertEquals("Error during Action invocation", actionErrorMessage);
    }

    public void testApplication() {
        assertEquals(application, context.getApplication());
    }

    public void testContextMap() {
        Map<String, Object> map = new HashMap<>();
        ActionContext.of(map).bind();

        assertEquals(map, ActionContext.getContext().getContextMap());
    }

    public void testParameters() {
        assertEquals(1, context.getParameters().keySet().size());
        assertEquals(PARAMETERS_KEY, context.getParameters().get(PARAMETERS_KEY).getValue());
    }

    public void testConversionErrors() {
        Map<String, ConversionData> errors = context.getConversionErrors();
        assertNotNull(errors);
        assertEquals(0, errors.size());

        Map<String, ConversionData> errors2 = new HashMap<>();
        context.withConversionErrors(errors);
        assertEquals(errors2, context.getConversionErrors());
    }

    public void testStaticMethods() {
        assertEquals(context, ActionContext.getContext());

        ActionContext.clear();

        assertNull(ActionContext.getContext());
    }

}
