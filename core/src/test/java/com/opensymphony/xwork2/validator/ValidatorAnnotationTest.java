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
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import org.apache.struts2.dispatcher.HttpParameters;

import java.util.Collection;
import java.util.HashMap;

/**
 * Unit test for annotated Validators.
 *
 * @author Rainer Hermanns
 */
public class ValidatorAnnotationTest extends XWorkTestCase {

    public void testNotAnnotatedMethodSuccess() throws Exception {
        HashMap<String, Object> params = new HashMap<>();
        params.put("date", "12/23/2002");
        params.put("foo", "5");
        params.put("bar", "7");

        HashMap<String, Object> extraContext = new HashMap<>();
        extraContext.put(ActionContext.PARAMETERS, HttpParameters.create(params).build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("", "notAnnotatedMethod", null, extraContext);
        proxy.execute();
        assertFalse(((ValidationAware) proxy.getAction()).hasActionErrors());

        Collection errors = ((ValidationAware) proxy.getAction()).getActionErrors();
        assertEquals(0, errors.size());
    }

    public void testNotAnnotatedMethodSuccess2() throws Exception {

        HashMap<String, Object> extraContext = new HashMap<>();
        extraContext.put(ActionContext.PARAMETERS, HttpParameters.create().build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("", "notAnnotatedMethod", null, extraContext);
        proxy.execute();
        assertFalse(((ValidationAware) proxy.getAction()).hasActionErrors());

        Collection errors = ((ValidationAware) proxy.getAction()).getActionErrors();
        assertEquals(0, errors.size());
    }

    public void testAnnotatedMethodFailure() throws Exception {

        HashMap<String, Object> extraContext = new HashMap<>();
        extraContext.put(ActionContext.PARAMETERS, HttpParameters.create().build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("", "annotatedMethod", null, extraContext);
        proxy.execute();
        assertTrue(((ValidationAware) proxy.getAction()).hasActionErrors());
        Collection errors = ((ValidationAware) proxy.getAction()).getActionErrors();
        assertEquals(1, errors.size());

        assertEquals("Need param1 or param2.", errors.iterator().next());

    }

    public void testAnnotatedMethodSuccess() throws Exception {
        HashMap<String, Object> params = new HashMap<>();

        //make it not fail
        params.put("param1", "key1");
        params.put("param2", "key2");

        HashMap<String, Object> extraContext = new HashMap<>();
        extraContext.put(ActionContext.PARAMETERS, HttpParameters.create(params).build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("", "annotatedMethod", null, extraContext);
        proxy.execute();
        assertFalse(((ValidationAware) proxy.getAction()).hasActionErrors());
    }

    public void testAnnotatedMethodSuccess2() throws Exception {
        HashMap<String, Object> params = new HashMap<>();

        //make it not fail
        params.put("param2", "key2");

        HashMap<String, Object> extraContext = new HashMap<>();
        extraContext.put(ActionContext.PARAMETERS, HttpParameters.create(params).build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("", "annotatedMethod", null, extraContext);
        proxy.execute();
        assertFalse(((ValidationAware) proxy.getAction()).hasActionErrors());
    }

    public void testAnnotatedMethodSuccess3() throws Exception {
        HashMap<String, Object> params = new HashMap<>();

        //make it not fail
        params.put("param1", "key1");

        HashMap<String, Object> extraContext = new HashMap<>();
        extraContext.put(ActionContext.PARAMETERS, HttpParameters.create(params).build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("", "annotatedMethod", null, extraContext);
        proxy.execute();
        assertFalse(((ValidationAware) proxy.getAction()).hasActionErrors());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        XmlConfigurationProvider provider1 = new XmlConfigurationProvider("xwork-default.xml");
        container.inject(provider1);
        XmlConfigurationProvider provider2 = new XmlConfigurationProvider("xwork-test-validation.xml");
        container.inject(provider2);
        loadConfigurationProviders(provider1, provider2);
    }

}
