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
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.SimpleAction;
import com.opensymphony.xwork2.SimpleFooAction;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.mock.MockActionProxy;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.apache.struts2.dispatcher.HttpParameters;

import java.util.HashMap;
import java.util.Map;

import static com.opensymphony.xwork2.security.DefaultAcceptedPatternsCheckerTest.ACCEPT_ALL_PATTERNS_CHECKER;
import static com.opensymphony.xwork2.security.DefaultExcludedPatternsCheckerTest.NO_EXCLUSION_PATTERNS_CHECKER;
import static org.junit.Assert.assertNotEquals;


/**
 * AliasInterceptorTest
 *
 * <p>
 * Test of aliasInterceptor specifically depends on actionTest test defined in /test/xwork.xml
 * stack.getContext().putAll(params);
 * </p>
 * e.g.
 * <pre>
 * <action name="aliasTest" class="com.opensymphony.xwork2.SimpleAction">
 *    <param name="aliases">#{ "aliasSource" : "aliasDest", "bar":"baz" }</param>
 *    <interceptor-ref name="defaultStack"/>
 *    <interceptor-ref name="alias"/>
 * </action>
 * </pre>
 *
 * @author Matthew Payne
 */
public class AliasInterceptorTest extends XWorkTestCase {

    public void testUsingDefaultInterceptorThatAliasPropertiesAreCopied() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("aliasSource", "source here");

        XmlConfigurationProvider provider = new StrutsXmlConfigurationProvider("xwork-sample.xml");
        container.inject(provider);
        loadConfigurationProviders(provider);
        ActionProxy proxy = actionProxyFactory.createActionProxy("", "aliasTest", null, params);
        SimpleAction actionOne = (SimpleAction) proxy.getAction();
        actionOne.setAliasSource("name to be copied");
        actionOne.setFoo(17);
        actionOne.setBar(23);
        proxy.execute();
        assertEquals("name to be copied", actionOne.getAliasSource());
        assertEquals(actionOne.getAliasSource(), actionOne.getAliasDest());
        assertNull(actionOne.getBlah());    //  WW-5087
    }

    public void testNameNotAccepted() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("aliasSource", "source here");

        Map<String, String> httpParams = new HashMap<>();
        httpParams.put("name", "getAliasSource()");
        httpParams.put("value", "aliasDest");
        params.put("parameters", HttpParameters.create(httpParams).build());


        XmlConfigurationProvider provider = new StrutsXmlConfigurationProvider("xwork-sample.xml");
        container.inject(provider);
        loadConfigurationProviders(provider);
        ActionProxy proxy = actionProxyFactory.createActionProxy("", "dynamicAliasTest", null, params);
        SimpleAction actionOne = (SimpleAction) proxy.getAction();
        actionOne.setAliasSource("name to be copied");

        // prevent ERROR result
        actionOne.setFoo(-1);
        actionOne.setBar(1);

        proxy.execute();
        assertEquals("name to be copied", actionOne.getAliasSource());
        assertNotEquals(actionOne.getAliasSource(), actionOne.getAliasDest());

        proxy = actionProxyFactory.createActionProxy("", "dynamicAliasTest", null, params);
        ((AliasInterceptor)proxy.getConfig().getInterceptors().get(1).getInterceptor())
                .setExcludedPatterns(NO_EXCLUSION_PATTERNS_CHECKER);
        ((AliasInterceptor)proxy.getConfig().getInterceptors().get(1).getInterceptor())
                .setAcceptedPatterns(ACCEPT_ALL_PATTERNS_CHECKER);

        actionOne = (SimpleAction) proxy.getAction();
        actionOne.setAliasSource("name to be copied");

        // prevent ERROR result
        actionOne.setFoo(-1);
        actionOne.setBar(1);

        proxy.execute();
        assertEquals("name to be copied", actionOne.getAliasSource());
        assertEquals(actionOne.getAliasSource(), actionOne.getAliasDest());
    }

    public void testValueNotAccepted() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("aliasSource", "source here");

        Map<String, String> httpParams = new HashMap<>();
        httpParams.put("name", "aliasSource");
        httpParams.put("value", "[0].aliasDest");
        params.put("parameters", HttpParameters.create(httpParams).build());


        XmlConfigurationProvider provider = new StrutsXmlConfigurationProvider("xwork-sample.xml");
        container.inject(provider);
        loadConfigurationProviders(provider);
        ActionProxy proxy = actionProxyFactory.createActionProxy("", "dynamicAliasTest", null, params);
        SimpleAction actionOne = (SimpleAction) proxy.getAction();
        actionOne.setAliasSource("name to be copied");

        // prevent ERROR result
        actionOne.setFoo(-1);
        actionOne.setBar(1);

        proxy.execute();
        assertEquals("name to be copied", actionOne.getAliasSource());
        assertNotEquals(actionOne.getAliasSource(), actionOne.getAliasDest());

        proxy = actionProxyFactory.createActionProxy("", "dynamicAliasTest", null, params);
        ((AliasInterceptor) proxy.getConfig().getInterceptors().get(1).getInterceptor())
                .setExcludedPatterns(NO_EXCLUSION_PATTERNS_CHECKER);
        ((AliasInterceptor) proxy.getConfig().getInterceptors().get(1).getInterceptor())
                .setAcceptedPatterns(ACCEPT_ALL_PATTERNS_CHECKER);

        actionOne = (SimpleAction) proxy.getAction();
        actionOne.setAliasSource("name to be copied");

        // prevent ERROR result
        actionOne.setFoo(-1);
        actionOne.setBar(1);

        proxy.execute();
        assertEquals("name to be copied", actionOne.getAliasSource());
        assertEquals(actionOne.getAliasSource(), actionOne.getAliasDest());
    }

    public void testNotExisting() throws Exception {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> httpParams = new HashMap<>();
        httpParams.put("notExisting", "from http parameter");
        params.put(ActionContext.PARAMETERS, HttpParameters.create(httpParams).build());

        XmlConfigurationProvider provider = new StrutsXmlConfigurationProvider("xwork-sample.xml");
        container.inject(provider);
        loadConfigurationProviders(provider);
        ActionProxy proxy = actionProxyFactory.createActionProxy("", "aliasTest", null, params);
        SimpleAction actionOne = (SimpleAction) proxy.getAction();

        // prevent ERROR result
        actionOne.setFoo(-1);
        actionOne.setBar(1);

        proxy.execute();
        assertEquals("from http parameter", actionOne.getBlah());
        assertNull(actionOne.getAliasDest());    //  WW-5087
    }

    public void testInvalidAliasExpression() throws Exception {
        Action action = new SimpleFooAction();
        MockActionInvocation mai = new MockActionInvocation();

        MockActionProxy map = new MockActionProxy();

        ActionConfig cfg = new ActionConfig.Builder("", "", "")
            .addParam("aliases", "invalid alias expression")
            .build();
        map.setConfig(cfg);

        mai.setProxy(map);
        mai.setAction(action);
        mai.setInvocationContext(ActionContext.getContext());

        AliasInterceptor ai = new AliasInterceptor();
        ai.init();

        ai.intercept(mai);

        ai.destroy();
    }

    public void testSetAliasKeys() throws Exception {
        Action action = new SimpleFooAction();
        MockActionInvocation mai = new MockActionInvocation();

        MockActionProxy map = new MockActionProxy();

        ActionConfig cfg = new ActionConfig.Builder("", "", "")
            .addParam("hello", "invalid alias expression")
            .build();
        map.setConfig(cfg);

        mai.setProxy(map);
        mai.setAction(action);
        mai.setInvocationContext(ActionContext.getContext());

        AliasInterceptor ai = new AliasInterceptor();
        ai.init();
        ai.setAliasesKey("hello");

        ai.intercept(mai);

        ai.destroy();
    }

    public void testSetInvalidAliasKeys() throws Exception {
        Action action = new SimpleFooAction();
        MockActionInvocation mai = new MockActionInvocation();

        MockActionProxy map = new MockActionProxy();

        ActionConfig cfg = new ActionConfig.Builder("", "", "")
            .addParam("hello", "invalid alias expression")
            .build();
        map.setConfig(cfg);

        mai.setProxy(map);
        mai.setAction(action);
        mai.setInvocationContext(ActionContext.getContext());

        AliasInterceptor ai = new AliasInterceptor();
        ai.init();
        ai.setAliasesKey("iamnotinconfig");

        ai.intercept(mai);

        ai.destroy();
    }

}

