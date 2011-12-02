/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.mock.MockResult;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.location.LocatableProperties;

import java.util.HashMap;


/**
 * ActionNestingTest
 *
 * @author Jason Carreira
 *         Created Mar 5, 2003 2:02:01 PM
 */
public class ActionNestingTest extends XWorkTestCase {

    public static final String VALUE = "myValue";
    public static final String NESTED_VALUE = "myNestedValue";
    public static final String KEY = "myProperty";
    public static final String NESTED_KEY = "nestedProperty";
    public static final String NAMESPACE = "NestedActionTest";
    public static final String SIMPLE_ACTION_NAME = "SimpleAction";
    public static final String NO_STACK_ACTION_NAME = "NoStackNestedAction";
    public static final String STACK_ACTION_NAME = "StackNestedAction";


    private ActionContext context;


    public String getMyProperty() {
        return VALUE;
    }

    @Override public void setUp() throws Exception {
        super.setUp();
        loadConfigurationProviders(new NestedTestConfigurationProvider());

        context = ActionContext.getContext();
        context.getValueStack().push(this);
    }

    @Override protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testNestedContext() throws Exception {
        assertEquals(context, ActionContext.getContext());
        ActionProxy proxy = actionProxyFactory.createActionProxy(NAMESPACE, SIMPLE_ACTION_NAME, null);
        proxy.execute();
        assertEquals(context, ActionContext.getContext());
    }

    public void testNestedNoValueStack() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        assertEquals(VALUE, stack.findValue(KEY));

        ActionProxy proxy = actionProxyFactory.createActionProxy(NAMESPACE, NO_STACK_ACTION_NAME, null);
        proxy.execute();
        stack = ActionContext.getContext().getValueStack();
        assertEquals(stack.findValue(KEY), VALUE);
        assertNull(stack.findValue(NESTED_KEY));
    }

    public void testNestedValueStack() throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();
        assertEquals(VALUE, stack.findValue(KEY));

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.VALUE_STACK, stack);

        ActionProxy proxy = actionProxyFactory.createActionProxy(NAMESPACE, STACK_ACTION_NAME, extraContext);
        proxy.execute();
        assertEquals(context, ActionContext.getContext());
        assertEquals(stack, ActionContext.getContext().getValueStack());
        assertEquals(VALUE, stack.findValue(KEY));
        assertEquals(NESTED_VALUE, stack.findValue(NESTED_KEY));
        assertEquals(3, stack.size());
    }


    class NestedTestConfigurationProvider implements ConfigurationProvider {
        private Configuration configuration;
        public void destroy() {
        }
        public void init(Configuration configuration) {
            this.configuration = configuration;
        }

        public void register(ContainerBuilder builder, LocatableProperties props) {
        }
        
        public void loadPackages() {
            
            PackageConfig packageContext = new PackageConfig.Builder("nestedActionTest")
                .addActionConfig(SIMPLE_ACTION_NAME, new ActionConfig.Builder("nestedActionTest", SIMPLE_ACTION_NAME, SimpleAction.class.getName())
                        .addResultConfig(new ResultConfig.Builder(Action.SUCCESS, MockResult.class.getName()).build())
                        .addResultConfig(new ResultConfig.Builder(Action.ERROR, MockResult.class.getName()).build())
                        .build())
                .addActionConfig(NO_STACK_ACTION_NAME, new ActionConfig.Builder("nestedActionTest", NO_STACK_ACTION_NAME, NestedAction.class.getName())
                        .addResultConfig(new ResultConfig.Builder(Action.SUCCESS, MockResult.class.getName()).build())
                        .methodName("noStack")
                        .build())
                .addActionConfig(STACK_ACTION_NAME, new ActionConfig.Builder("nestedActionTest", STACK_ACTION_NAME, NestedAction.class.getName())
                        .addResultConfig(new ResultConfig.Builder(Action.SUCCESS, MockResult.class.getName()).build())
                        .methodName("stack")
                        .build())
                .namespace(NAMESPACE)
                .build();
            configuration.addPackageConfig("nestedActionTest", packageContext);
        }

        /**
         * Tells whether the ConfigurationProvider should reload its configuration
         *
         * @return
         */
        public boolean needsReload() {
            return false;
        }
    }
}
