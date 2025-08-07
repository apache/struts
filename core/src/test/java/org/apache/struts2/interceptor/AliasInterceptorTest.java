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

import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionProxy;
import org.apache.struts2.SimpleAction;
import org.apache.struts2.SimpleFooAction;
import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.action.Action;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.config.providers.XmlConfigurationProvider;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.mock.MockActionInvocation;
import org.apache.struts2.mock.MockActionProxy;
import org.apache.struts2.ognl.StrutsContext;

import java.util.HashMap;
import java.util.Map;

import static org.apache.struts2.security.DefaultAcceptedPatternsCheckerTest.ACCEPT_ALL_PATTERNS_CHECKER;
import static org.apache.struts2.security.DefaultExcludedPatternsCheckerTest.NO_EXCLUSION_PATTERNS_CHECKER;
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
 * <action name="aliasTest" class="org.apache.struts2.SimpleAction">
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
        StrutsContext context = StrutsContext.empty();
        context.put("aliasSource", "source here");

        XmlConfigurationProvider provider = new StrutsXmlConfigurationProvider("xwork-sample.xml");
        container.inject(provider);
        loadConfigurationProviders(provider);
        ActionProxy proxy = actionProxyFactory.createActionProxy("", "aliasTest", null, context);
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
        StrutsContext context = StrutsContext.empty();
        context.put("aliasSource", "source here");

        Map<String, String> httpParams = new HashMap<>();
        httpParams.put("name", "getAliasSource()");
        httpParams.put("value", "aliasDest");
        context.put("parameters", HttpParameters.create(httpParams).build());


        XmlConfigurationProvider provider = new StrutsXmlConfigurationProvider("xwork-sample.xml");
        container.inject(provider);
        loadConfigurationProviders(provider);
        ActionProxy proxy = actionProxyFactory.createActionProxy("", "dynamicAliasTest", null, context);
        SimpleAction actionOne = (SimpleAction) proxy.getAction();
        actionOne.setAliasSource("name to be copied");

        // prevent ERROR result
        actionOne.setFoo(-1);
        actionOne.setBar(1);

        proxy.execute();
        assertEquals("name to be copied", actionOne.getAliasSource());
        assertNotEquals(actionOne.getAliasSource(), actionOne.getAliasDest());

        proxy = actionProxyFactory.createActionProxy("", "dynamicAliasTest", null, context);
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

    public void testValueNotAccepted() throws Exception {
        StrutsContext context = StrutsContext.empty();
        context.put("aliasSource", "source here");

        Map<String, String> httpParams = new HashMap<>();
        httpParams.put("name", "aliasSource");
        httpParams.put("value", "[0].aliasDest");
        context.put("parameters", HttpParameters.create(httpParams).build());


        XmlConfigurationProvider provider = new StrutsXmlConfigurationProvider("xwork-sample.xml");
        container.inject(provider);
        loadConfigurationProviders(provider);
        ActionProxy proxy = actionProxyFactory.createActionProxy("", "dynamicAliasTest", null, context);
        SimpleAction actionOne = (SimpleAction) proxy.getAction();
        actionOne.setAliasSource("name to be copied");

        // prevent ERROR result
        actionOne.setFoo(-1);
        actionOne.setBar(1);

        proxy.execute();
        assertEquals("name to be copied", actionOne.getAliasSource());
        assertNotEquals(actionOne.getAliasSource(), actionOne.getAliasDest());

        proxy = actionProxyFactory.createActionProxy("", "dynamicAliasTest", null, context);
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
        Map<String, Object> httpParams = new HashMap<>();
        httpParams.put("notExisting", "from http parameter");
        ActionContext context = ActionContext.of()
                .withParameters(HttpParameters.create(httpParams).build());

        XmlConfigurationProvider provider = new StrutsXmlConfigurationProvider("xwork-sample.xml");
        container.inject(provider);
        loadConfigurationProviders(provider);
        ActionProxy proxy = actionProxyFactory.createActionProxy("", "aliasTest", null, context.getStrutsContext());
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

