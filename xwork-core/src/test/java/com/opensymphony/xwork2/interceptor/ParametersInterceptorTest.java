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

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ModelDrivenAction;
import com.opensymphony.xwork2.SimpleAction;
import com.opensymphony.xwork2.TestBean;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.providers.MockConfigurationProvider;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.ognl.OgnlValueStack;
import com.opensymphony.xwork2.ognl.OgnlValueStackFactory;
import com.opensymphony.xwork2.ognl.accessor.CompoundRootAccessor;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import ognl.PropertyAccessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Unit test for {@link ParametersInterceptor}.
 *
 * @author Jason Carreira
 */
public class ParametersInterceptorTest extends XWorkTestCase {

    public void testParameterNameAware() {
        ParametersInterceptor pi = createParametersInterceptor();
        final Map actual = injectValueStackFactory(pi);
        ValueStack stack = createStubValueStack(actual);
        final Map expected = new HashMap() {
            {
                put("fooKey", "fooValue");
                put("barKey", "barValue");
            }
        };
        Object a = new ParameterNameAware() {
            public boolean acceptableParameterName(String parameterName) {
                return expected.containsKey(parameterName);
            }
        };
        Map parameters = new HashMap() {
            {
                put("fooKey", "fooValue");
                put("barKey", "barValue");
                put("error", "error");
            }
        };
        pi.setParameters(a, stack, parameters);
        assertEquals(expected, actual);
    }

    public void testDoesNotAllowMethodInvocations() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("@java.lang.System@exit(1).dummy", "dumb value");

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.MODEL_DRIVEN_PARAM_TEST, extraContext);
        assertEquals(Action.SUCCESS, proxy.execute());

        ModelDrivenAction action = (ModelDrivenAction) proxy.getAction();
        TestBean model = (TestBean) action.getModel();

        String property = System.getProperty("xwork.security.test");
        assertNull(property);
    }

    public void testModelDrivenParameters() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        final String fooVal = "com.opensymphony.xwork2.interceptor.ParametersInterceptorTest.foo";
        params.put("foo", fooVal);

        final String nameVal = "com.opensymphony.xwork2.interceptor.ParametersInterceptorTest.name";
        params.put("name", nameVal);
        params.put("count", "15");

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.MODEL_DRIVEN_PARAM_TEST, extraContext);
        assertEquals(Action.SUCCESS, proxy.execute());

        ModelDrivenAction action = (ModelDrivenAction) proxy.getAction();
        TestBean model = (TestBean) action.getModel();
        assertEquals(nameVal, model.getName());
        assertEquals(15, model.getCount());
        assertEquals(fooVal, action.getFoo());
    }

    public void testParametersDoesNotAffectSession() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("blah", "This is blah");
        params.put("#session.foo", "Foo");
        params.put("\u0023session[\'user\']", "0wn3d");
        params.put("\\u0023session[\'user\']", "0wn3d");
        params.put("\u0023session.user2", "0wn3d");
        params.put("\\u0023session.user2", "0wn3d");
        params.put("('\u0023'%20%2b%20'session[\'user3\']')(unused)", "0wn3d");
        params.put("('\\u0023' + 'session[\\'user4\\']')(unused)", "0wn3d");
        params.put("('\u0023'%2b'session[\'user5\']')(unused)", "0wn3d");
        params.put("('\\u0023'%2b'session[\'user5\']')(unused)", "0wn3d");

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);
        ValueStack stack = proxy.getInvocation().getStack();
        HashMap<String, Object> session = new HashMap<String, Object>();
        stack.getContext().put("session", session);
        proxy.execute();
        assertEquals("This is blah", ((SimpleAction) proxy.getAction()).getBlah());
        assertNull(session.get("foo"));
        assertNull(session.get("user"));
        assertNull(session.get("user2"));
        assertNull(session.get("user3"));
        assertNull(session.get("user4"));
        assertNull(session.get("user5"));
    }

    public void testAccessToOgnlInternals() throws Exception {
        // given
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("blah", "This is blah");
        params.put("('\\u0023_memberAccess[\\'allowStaticMethodAccess\\']')(meh)", "true");
        params.put("('(aaa)(('\\u0023context[\\'xwork.MethodAccessor.denyMethodExecution\\']\\u003d\\u0023foo')(\\u0023foo\\u003dnew java.lang.Boolean(\"false\")))", "");
        params.put("(asdf)(('\\u0023rt.exit(1)')(\\u0023rt\\u003d@java.lang.Runtime@getRuntime()))", "1");

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, "", extraContext);
        ValueStack stack = proxy.getInvocation().getStack();

        // when
        proxy.execute();
        proxy.getAction();

        //then
        assertEquals("This is blah", ((SimpleAction) proxy.getAction()).getBlah());
        Object allowMethodAccess = stack.findValue("\u0023_memberAccess['allowStaticMethodAccess']");
        assertNotNull(allowMethodAccess);
        assertEquals(Boolean.FALSE, allowMethodAccess);
    }

    public void testParameters() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("blah", "This is blah");

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);
        proxy.execute();
        assertEquals("This is blah", ((SimpleAction) proxy.getAction()).getBlah());
    }

     public void testParametersWithSpacesInTheName() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("theProtectedMap['p0 p1']", "test1");
        params.put("theProtectedMap['p0p1 ']", "test2");
        params.put("theProtectedMap[' p0p1 ']", "test3");
        params.put("theProtectedMap[' p0 p1 ']", "test4");

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);
        proxy.execute();
        Map<String, String> existingMap =  ((SimpleAction) proxy.getAction()).getTheProtectedMap();
        assertEquals(4, existingMap.size());
        assertEquals("test1", existingMap.get("p0 p1"));
        assertEquals("test2", existingMap.get("p0p1 "));
        assertEquals("test3", existingMap.get(" p0p1 "));
        assertEquals("test4", existingMap.get(" p0 p1 "));
    }

    public void testExcludedTrickyParameters() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("blah", "This is blah");
                put("name", "try_1");
                put("(name)", "try_2");
                put("['name']", "try_3");
                put("['na' + 'me']", "try_4");
                put("{name}[0]", "try_5");
                put("(new string{'name'})[0]", "try_6");
                put("#{key: 'name'}.key", "try_7");

            }
        };

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);

        ActionConfig config = configuration.getRuntimeConfiguration().getActionConfig("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME);
        ParametersInterceptor pi =(ParametersInterceptor) config.getInterceptors().get(0).getInterceptor();
        pi.setExcludeParams("name");

        proxy.execute();

        SimpleAction action = (SimpleAction) proxy.getAction();
        assertNull(action.getName());
        assertEquals("This is blah", (action).getBlah());
    }

    public void testAcceptedTrickyParameters() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("blah", "This is blah");
                put("baz", "123");
                put("name", "try_1");
                put("(name)", "try_2");
                put("['name']", "try_3");
                put("['na' + 'me']", "try_4");
                put("{name}[0]", "try_5");
                put("(new string{'name'})[0]", "try_6");
                put("#{key: 'name'}.key", "try_7");
            }
        };

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);

        ActionConfig config = configuration.getRuntimeConfiguration().getActionConfig("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME);
        ParametersInterceptor pi =(ParametersInterceptor) config.getInterceptors().get(0).getInterceptor();
        pi.setAcceptParamNames("blah, baz");

        proxy.execute();

        SimpleAction action = (SimpleAction) proxy.getAction();
        assertNull(action.getName());
        assertEquals("This is blah", (action).getBlah());
        assertEquals(123, action.getBaz());
    }


    public void testParametersNotAccessPrivateVariables() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("protectedMap.foo", "This is blah");
        params.put("theProtectedMap.boo", "This is blah");

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);
        proxy.execute();
        SimpleAction action = (SimpleAction) proxy.getAction();
        assertEquals(1, action.getTheProtectedMap().size());
        assertNotNull(action.getTheProtectedMap().get("boo"));
        assertNull(action.getTheProtectedMap().get("foo"));
    }

    public void testParametersNotAccessProtectedMethods() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("theSemiProtectedMap.foo", "This is blah");
        params.put("theProtectedMap.boo", "This is blah");

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);
        proxy.execute();
        SimpleAction action = (SimpleAction) proxy.getAction();
        assertEquals(1, action.getTheProtectedMap().size());
        assertNotNull(action.getTheProtectedMap().get("boo"));
        assertNull(action.getTheProtectedMap().get("foo"));
    }

    public void testParametersOverwriteField() throws Exception {
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("existingMap.boo", "This is blah");

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);
        proxy.execute();
        SimpleAction action = (SimpleAction) proxy.getAction();
        assertEquals(1, action.getTheExistingMap().size());
        assertNotNull(action.getTheExistingMap().get("boo"));
        assertNull(action.getTheExistingMap().get("existingKey"));
    }

    public void testNonexistentParametersGetLoggedInDevMode() throws Exception {
        loadConfigurationProviders(new XmlConfigurationProvider("xwork-test-beans.xml"),
                new MockConfigurationProvider(Collections.singletonMap("devMode", "true")));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("not_a_property", "There is no action property named like this");

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);
        ParametersInterceptor.setDevMode("true");

        ActionConfig config = configuration.getRuntimeConfiguration().getActionConfig("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME);
        container.inject(config.getInterceptors().get(0).getInterceptor());
        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);
        proxy.execute();
        final String actionMessage = "" + ((SimpleAction) proxy.getAction()).getActionMessages().toArray()[0];
        assertTrue(actionMessage.contains("Error setting expression 'not_a_property' with value 'There is no action property named like this'"));
    }

    public void testNonexistentParametersAreIgnoredInProductionMode() throws Exception {
        loadConfigurationProviders(new XmlConfigurationProvider("xwork-test-beans.xml"),
                new MockConfigurationProvider(Collections.singletonMap("devMode", "false")));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("not_a_property", "There is no action property named like this");

        HashMap<String, Object> extraContext = new HashMap<String, Object>();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionConfig config = configuration.getRuntimeConfiguration().getActionConfig("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME);
        container.inject(config.getInterceptors().get(0).getInterceptor());
        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);
        proxy.execute();
        assertTrue(((SimpleAction) proxy.getAction()).getActionMessages().isEmpty());
    }

    public void testNoParametersAction() throws Exception {
        ParametersInterceptor interceptor = new ParametersInterceptor();
        interceptor.init();

        MockActionInvocation mai = new MockActionInvocation();
        Action action = new NoParametersAction();
        mai.setAction(action);

        interceptor.doIntercept(mai);
        interceptor.destroy();
    }

    public void testNoOrdered() throws Exception {
        ParametersInterceptor pi = createParametersInterceptor();
        final Map<String, Object> actual = new LinkedHashMap<String, Object>();
        pi.setValueStackFactory(createValueStackFactory(actual));
        ValueStack stack = createStubValueStack(actual);

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("user.address.city", "London");
        parameters.put("user.name", "Superman");

        Action action = new SimpleAction();
        pi.setParameters(action, stack, parameters);

        assertEquals("ordered should be false by default", false, pi.isOrdered());
        assertEquals(2, actual.size());
        assertEquals("London", actual.get("user.address.city"));
        assertEquals("Superman", actual.get("user.name"));

        // is not ordered
        List<Object> values = new ArrayList<Object>(actual.values());
        assertEquals("London", values.get(0));
        assertEquals("Superman", values.get(1));
    }

    public void testOrdered() throws Exception {
        ParametersInterceptor pi = new ParametersInterceptor();
        pi.setOrdered(true);
        container.inject(pi);
        final Map<String, Object> actual = new LinkedHashMap<String, Object>();
        pi.setValueStackFactory(createValueStackFactory(actual));
        ValueStack stack = createStubValueStack(actual);

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("user.address.city", "London");
        parameters.put("user.address['postal']", "QJR387");
        parameters.put("user.name", "Superman");

        Action action = new SimpleAction();
        pi.setParameters(action, stack, parameters);

        assertEquals(true, pi.isOrdered());
        assertEquals(3, actual.size());
        assertEquals("London", actual.get("user.address.city"));
        assertEquals("QJR387", actual.get("user.address['postal']"));
        assertEquals("Superman", actual.get("user.name"));

        // should be ordered so user.name should be first
        List<Object> values = new ArrayList<Object>(actual.values());
        assertEquals("Superman", values.get(0));
        assertEquals("London", values.get(1));
        assertEquals("QJR387", values.get(2));
    }

    public void testSetOrdered() throws Exception {
        ParametersInterceptor pi = createParametersInterceptor();
        assertEquals("ordered should be false by default", false, pi.isOrdered());
        pi.setOrdered(true);
        assertEquals(true, pi.isOrdered());
    }

    public void testExcludedParametersAreIgnored() throws Exception {
        ParametersInterceptor pi = createParametersInterceptor();
        pi.setExcludeParams("dojo\\..*");
        final Map actual = injectValueStackFactory(pi);
        ValueStack stack = injectValueStack(actual);

        final Map<String, Object> expected = new HashMap<String, Object>() {
            {
                put("fooKey", "fooValue");
            }
        };

        Map<String, Object> parameters = new HashMap<String, Object>() {
            {
                put("dojo.test", "dojoValue");
                put("fooKey", "fooValue");
            }
        };
        pi.setParameters(new NoParametersAction(), stack, parameters);
        assertEquals(expected, actual);
    }

    public void testInternalParametersAreIgnored() throws Exception {
        // given
        ParametersInterceptor interceptor = createParametersInterceptor();
        final Map actual = injectValueStackFactory(interceptor);
        ValueStack stack = injectValueStack(actual);


        final Map<String, Object> expected = new HashMap<String, Object>() {
            {
                put("ordinary.bean", "value");
            }
        };

        Map<String, Object> parameters = new HashMap<String, Object>() {
            {
                put("ordinary.bean", "value");
                put("#some.internal.object", "true");
                put("(bla)#some.internal.object", "true");
                put("#some.internal.object(bla)#some.internal.object", "true");
                put("#_some.internal.object", "true");
                put("\u0023_some.internal.object", "true");
                put("\u0023_some.internal.object,[dfd],bla(\u0023_some.internal.object)", "true");
                put("\\u0023_some.internal.object", "true");
            }
        };

        // when
        interceptor.setParameters(new NoParametersAction(), stack, parameters);

        // then
        assertEquals(expected, actual);
    }

    private ValueStack injectValueStack(Map actual) {
        ValueStack stack = createStubValueStack(actual);
        container.inject(stack);
        return stack;
    }

    private Map injectValueStackFactory(ParametersInterceptor interceptor) {
        final Map actual = new HashMap();
        interceptor.setValueStackFactory(createValueStackFactory(actual));
        return actual;
    }

    private ParametersInterceptor createParametersInterceptor() {
        ParametersInterceptor pi = new ParametersInterceptor();
        container.inject(pi);
        return pi;
    }

    private ValueStackFactory createValueStackFactory(final Map<String, Object> context) {
        OgnlValueStackFactory factory = new OgnlValueStackFactory() {
            @Override
            public ValueStack createValueStack(ValueStack stack) {
                return createStubValueStack(context);
            }
        };
        container.inject(factory);
        return factory;
    }

    private ValueStack createStubValueStack(final Map<String, Object> actual) {
        ValueStack stack = new OgnlValueStack(
                container.getInstance(XWorkConverter.class),
                (CompoundRootAccessor)container.getInstance(PropertyAccessor.class, CompoundRoot.class.getName()),
                container.getInstance(TextProvider.class, "system"), true) {
            @Override
            public void setValue(String expr, Object value) {
                actual.put(expr, value);
            }
        };
        container.inject(stack);
        return stack;
    }

    /*
    public void testIndexedParameters() throws Exception {
        Map params = new HashMap();
        params.put("indexedProp[33]", "This is blah");

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.PARAMETERS, params);

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, extraContext);
        proxy.execute();
        assertEquals("This is blah", ((SimpleAction) proxy.getAction()).getIndexedProp(33));
    }
    */


    private class NoParametersAction implements Action, NoParameters {

        public String execute() throws Exception {
            return SUCCESS;
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loadConfigurationProviders(new XmlConfigurationProvider("xwork-test-beans.xml"), new MockConfigurationProvider());

        ActionConfig config = configuration.getRuntimeConfiguration().getActionConfig("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME);
        container.inject(config.getInterceptors().get(0).getInterceptor());
    }
}
