/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.mock.MockActionProxy;

import java.util.HashMap;
import java.util.Map;


/**
 * AliasInterceptorTest
 *
 * Test of aliasInterceptor specifically depends on actionTest test defined in /test/xwork.xml
 * stack.getContext().putAll(params);
 * <p/>
 * e.g.
 * <action name="aliasTest" class="com.opensymphony.xwork2.SimpleAction">
 *    <param name="aliases">#{ "aliasSource" : "aliasDest", "bar":"baz" }</param>
 *    <interceptor-ref name="defaultStack"/>
 *    <interceptor-ref name="alias"/>
 * </action>
 *
 * @author Matthew Payne
 */
public class AliasInterceptorTest extends XWorkTestCase {

    public void testUsingDefaultInterceptorThatAliasPropertiesAreCopied() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("aliasSource", "source here");

        XmlConfigurationProvider provider = new XmlConfigurationProvider("xwork-sample.xml");
        container.inject(provider);
        loadConfigurationProviders(provider);
        ActionProxy proxy = actionProxyFactory.createActionProxy("", "aliasTest", params);
        SimpleAction actionOne = (SimpleAction) proxy.getAction();
        actionOne.setAliasSource("name to be copied");
        actionOne.setFoo(17);
        actionOne.setBar(23);
        proxy.execute();
        assertEquals(actionOne.getAliasSource(), actionOne.getAliasDest());
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

