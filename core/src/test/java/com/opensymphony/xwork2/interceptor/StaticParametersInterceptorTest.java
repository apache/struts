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

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.SimpleFooAction;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.Parameterizable;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.mock.MockActionProxy;

import java.util.Map;
import java.util.HashMap;

/**
 * Unit test of {@link StaticParametersInterceptor}.
 *
 * @author Claus Ibsen
 */
public class StaticParametersInterceptorTest extends XWorkTestCase {

    private StaticParametersInterceptor interceptor;

    public void testParameterizable() throws Exception {
        Mock mock = new Mock(Parameterizable.class);

        MockActionInvocation mai = new MockActionInvocation();
        MockActionProxy map = new MockActionProxy();
        ActionConfig ac = new ActionConfig.Builder("", "", "").build();

        Map params = ac.getParams();

        map.setConfig(ac);
        mai.setProxy(map);
        mai.setAction(mock.proxy());
        mock.expect("setParams", params);

        interceptor.intercept(mai);
        mock.verify();
    }

    public void testWithOneParameters() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockActionProxy map = new MockActionProxy();
        ActionConfig ac = new ActionConfig.Builder("", "", "")
                .addParam("top.name", "Santa")
                .build();

        map.setConfig(ac);
        mai.setProxy(map);
        mai.setAction(new SimpleFooAction());

        User user = new User();
        ActionContext.getContext().getValueStack().push(user);
        int before = ActionContext.getContext().getValueStack().size();
        interceptor.intercept(mai);

        assertEquals(before, ActionContext.getContext().getValueStack().size());
        assertEquals("Santa", user.getName());
    }

    public void testWithOneParametersParse() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockActionProxy map = new MockActionProxy();
        ActionConfig ac = new ActionConfig.Builder("", "", "")
                .addParam("top.name", "${top.hero}")
                .build();
        map.setConfig(ac);
        mai.setProxy(map);
        mai.setAction(new SimpleFooAction());

        User user = new User();
        ActionContext.getContext().getValueStack().push(user);
        int before = ActionContext.getContext().getValueStack().size();
        interceptor.setParse("true");
        interceptor.intercept(mai);

        assertEquals(before, ActionContext.getContext().getValueStack().size());
        assertEquals("Superman", user.getName());
    }

    public void testWithOneParametersNoParse() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockActionProxy map = new MockActionProxy();
        ActionConfig ac = new ActionConfig.Builder("", "", "")
                .addParam("top.name", "${top.hero}")
                .build();
        map.setConfig(ac);
        mai.setProxy(map);
        mai.setAction(new SimpleFooAction());

        User user = new User();
        ActionContext.getContext().getValueStack().push(user);
        int before = ActionContext.getContext().getValueStack().size();
        interceptor.setParse("false");
        interceptor.intercept(mai);

        assertEquals(before, ActionContext.getContext().getValueStack().size());
        assertEquals("${top.hero}", user.getName());
    }

     public void testNoMerge() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockActionProxy map = new MockActionProxy();
        ActionConfig ac = new ActionConfig.Builder("", "", "")
                .addParam("top.name", "${top.hero}")
                .build();
        map.setConfig(ac);
        mai.setProxy(map);
        mai.setAction(new SimpleFooAction());

        User user = new User();
        ActionContext.getContext().getValueStack().push(user);
        ActionContext.getContext().setParameters(new HashMap<String, Object>()); 
        int before = ActionContext.getContext().getValueStack().size();
        interceptor.setMerge("false");
        interceptor.intercept(mai);

        assertEquals(before, ActionContext.getContext().getValueStack().size());
        assertEquals("${top.hero}", user.getName());
        assertEquals(0, ActionContext.getContext().getParameters().size()); 
    }

    public void testFewParametersParse() throws Exception {
        MockActionInvocation mai = new MockActionInvocation();
        MockActionProxy map = new MockActionProxy();
        ActionConfig ac = new ActionConfig.Builder("", "", "")
                .addParam("top.age", "${top.myAge}")
                .addParam("top.email", "${top.myEmail}")
                .build();
        map.setConfig(ac);
        mai.setProxy(map);
        mai.setAction(new SimpleFooAction());

        User user = new User();
        ActionContext.getContext().getValueStack().push(user);
        int before = ActionContext.getContext().getValueStack().size();
        interceptor.setParse("true");
        interceptor.intercept(mai);

        assertEquals(before, ActionContext.getContext().getValueStack().size());
        assertEquals(user.getMyAge(), user.age);
        assertEquals(user.getMyEmail(), user.email);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        interceptor = new StaticParametersInterceptor();
        interceptor.init();
        container.inject(interceptor);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        interceptor.destroy();
    }

    private class User {
        private String name;
        private int age;
        private String email;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getMyAge() {
            return 33;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getMyEmail() {
            return "lukasz dot lenart at gmail dot com";
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getHero() {
            return "Superman";
        }
    }

}
