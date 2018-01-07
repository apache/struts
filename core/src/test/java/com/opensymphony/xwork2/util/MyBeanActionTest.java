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
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import org.apache.struts2.dispatcher.HttpParameters;

import java.util.HashMap;
import java.util.Map;

/**
 * <code>MyBeanActionTest</code>
 *
 * @author Rainer Hermanns
 */
public class MyBeanActionTest extends XWorkTestCase {

    public void testIndexedList() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("beanList(1234567890).name", "This is the bla bean");
        params.put("beanList(1234567891).name", "This is the 2nd bla bean");

        params.put("annotatedBeanList(1234567890).name", "This is the bla bean by annotation");
        params.put("annotatedBeanList(1234567891).name", "This is the 2nd bla bean by annotation");

        HashMap<String, Object> extraContext = new HashMap<>();
        extraContext.put(ActionContext.PARAMETERS, HttpParameters.create(params).build());

        try {
            ActionProxy proxy = actionProxyFactory.createActionProxy("", "MyBean", null, extraContext);
            proxy.execute();
            assertEquals(2, Integer.parseInt(proxy.getInvocation().getStack().findValue("beanList.size").toString()));
            assertEquals(MyBean.class.getName(), proxy.getInvocation().getStack().findValue("beanList.get(0)").getClass().getName());
            assertEquals(MyBean.class.getName(), proxy.getInvocation().getStack().findValue("beanList.get(1)").getClass().getName());

            assertEquals("This is the bla bean", proxy.getInvocation().getStack().findValue("beanList.get(0).name"));
            assertEquals(new Long(1234567890), Long.valueOf(proxy.getInvocation().getStack().findValue("beanList.get(0).id").toString()));
            assertEquals("This is the 2nd bla bean", proxy.getInvocation().getStack().findValue("beanList.get(1).name"));
            assertEquals(new Long(1234567891), Long.valueOf(proxy.getInvocation().getStack().findValue("beanList.get(1).id").toString()));

            assertEquals(2, Integer.parseInt(proxy.getInvocation().getStack().findValue("annotatedBeanList.size").toString()));
            assertEquals(MyBean.class.getName(), proxy.getInvocation().getStack().findValue("annotatedBeanList.get(0)").getClass().getName());
            assertEquals(MyBean.class.getName(), proxy.getInvocation().getStack().findValue("annotatedBeanList.get(1)").getClass().getName());

            assertEquals("This is the bla bean by annotation", proxy.getInvocation().getStack().findValue("annotatedBeanList.get(0).name"));
            assertEquals(new Long(1234567890), Long.valueOf(proxy.getInvocation().getStack().findValue("annotatedBeanList.get(0).id").toString()));
            assertEquals("This is the 2nd bla bean by annotation", proxy.getInvocation().getStack().findValue("annotatedBeanList.get(1).name"));
            assertEquals(new Long(1234567891), Long.valueOf(proxy.getInvocation().getStack().findValue("annotatedBeanList.get(1).id").toString()));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testIndexedMap() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("beanMap[1234567890].id", "1234567890");
        params.put("beanMap[1234567891].id", "1234567891");

        params.put("beanMap[1234567890].name", "This is the bla bean");
        params.put("beanMap[1234567891].name", "This is the 2nd bla bean");

        params.put("annotatedBeanMap[1234567890].id", "1234567890");
        params.put("annotatedBeanMap[1234567891].id", "1234567891");

        params.put("annotatedBeanMap[1234567890].name", "This is the bla bean by annotation");
        params.put("annotatedBeanMap[1234567891].name", "This is the 2nd bla bean by annotation");

        HashMap<String, Object> extraContext = new HashMap<>();
        extraContext.put(ActionContext.PARAMETERS, HttpParameters.create(params).build());

        try {
            ActionProxy proxy = actionProxyFactory.createActionProxy("", "MyBean", null, extraContext);
            proxy.execute();
            MyBeanAction action = (MyBeanAction) proxy.getInvocation().getAction();

            assertEquals(2, Integer.parseInt(proxy.getInvocation().getStack().findValue("beanMap.size").toString()));

            assertEquals(true, action.getBeanMap().containsKey(1234567890L));
            assertEquals(true, action.getBeanMap().containsKey(1234567891L));


            assertEquals(MyBean.class.getName(), proxy.getInvocation().getStack().findValue("beanMap.get(1234567890L)").getClass().getName());
            assertEquals(MyBean.class.getName(), proxy.getInvocation().getStack().findValue("beanMap.get(1234567891L)").getClass().getName());

            assertEquals("This is the bla bean", proxy.getInvocation().getStack().findValue("beanMap.get(1234567890L).name"));
            assertEquals("This is the 2nd bla bean", proxy.getInvocation().getStack().findValue("beanMap.get(1234567891L).name"));

            assertEquals("1234567890", proxy.getInvocation().getStack().findValue("beanMap.get(1234567890L).id").toString());
            assertEquals("1234567891", proxy.getInvocation().getStack().findValue("beanMap.get(1234567891L).id").toString());

            assertEquals(2, Integer.parseInt(proxy.getInvocation().getStack().findValue("annotatedBeanMap.size").toString()));

            assertEquals(true, action.getAnnotatedBeanMap().containsKey(1234567890L));
            assertEquals(true, action.getAnnotatedBeanMap().containsKey(1234567891L));


            assertEquals(MyBean.class.getName(), proxy.getInvocation().getStack().findValue("annotatedBeanMap.get(1234567890L)").getClass().getName());
            assertEquals(MyBean.class.getName(), proxy.getInvocation().getStack().findValue("annotatedBeanMap.get(1234567891L)").getClass().getName());

            assertEquals("This is the bla bean by annotation", proxy.getInvocation().getStack().findValue("annotatedBeanMap.get(1234567890L).name"));
            assertEquals("This is the 2nd bla bean by annotation", proxy.getInvocation().getStack().findValue("annotatedBeanMap.get(1234567891L).name"));

            assertEquals("1234567890", proxy.getInvocation().getStack().findValue("annotatedBeanMap.get(1234567890L).id").toString());
            assertEquals("1234567891", proxy.getInvocation().getStack().findValue("annotatedBeanMap.get(1234567891L).id").toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // ensure we're using the default configuration, not simple config
        XmlConfigurationProvider provider = new XmlConfigurationProvider("xwork-sample.xml");
        container.inject(provider);
        loadConfigurationProviders(provider);
    }
}
