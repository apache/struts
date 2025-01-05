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
package org.apache.struts2.interceptor.parameter;

import org.apache.struts2.action.Action;
import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionProxy;
import org.apache.struts2.ActionSupport;
import org.apache.struts2.ModelDrivenAction;
import org.apache.struts2.SimpleAction;
import org.apache.struts2.TestBean;
import org.apache.struts2.text.TextProvider;
import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.config.providers.MockConfigurationProvider;
import org.apache.struts2.config.providers.StrutsDefaultConfigurationProvider;
import org.apache.struts2.config.providers.XmlConfigurationProvider;
import org.apache.struts2.conversion.impl.XWorkConverter;
import org.apache.struts2.interceptor.ValidationAware;
import org.apache.struts2.mock.MockActionInvocation;
import org.apache.struts2.ognl.OgnlValueStack;
import org.apache.struts2.ognl.OgnlValueStackFactory;
import org.apache.struts2.ognl.SecurityMemberAccess;
import org.apache.struts2.ognl.accessor.CompoundRootAccessor;
import org.apache.struts2.ognl.accessor.RootAccessor;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.ValueStackFactory;
import org.apache.struts2.util.reflection.ReflectionContextState;
import ognl.OgnlContext;
import org.apache.struts2.action.NoParameters;
import org.apache.struts2.action.ParameterNameAware;
import org.apache.struts2.action.ParameterValueAware;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.apache.struts2.dispatcher.HttpParameters;
import org.junit.Assert;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
        final Map<String, Object> actual = injectValueStackFactory(pi);
        ValueStack stack = createStubValueStack(actual);
        final Map<String, Object> expected = new HashMap<String, Object>() {
            {
                put("fooKey", "fooValue");
                put("barKey", "barValue");
            }
        };
        ParameterNameAware a = expected::containsKey;
        final Map<String, Object> parameters = new HashMap<String, Object>() {
            {
                put("fooKey", "fooValue");
                put("barKey", "barValue");
                put("error-key", "error");
                put("error key", "error");
                put("error:key", "error");
                put("error+key", "error");
                put("test%test", "test%test");
            }
        };
        pi.applyParameters(a, stack, HttpParameters.create(parameters).build());
        assertEquals(expected, actual);
    }

    public void testInsecureParameters() throws Exception {
        // given
        loadConfigurationProviders(new StrutsDefaultConfigurationProvider(), new StrutsXmlConfigurationProvider("xwork-param-test.xml"));
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("name", "(#context[\"xwork.MethodAccessor.denyMethodExecution\"]= new " +
                    "java.lang.Boolean(false), #_memberAccess[\"allowStaticMethodAccess\"]= new java.lang.Boolean(true), " +
                    "@java.lang.Runtime@getRuntime().exec('mkdir /tmp/PWNAGE'))(meh)");
                put("top['name'](0)", "true");
                put("expression", "#f=#_memberAccess.getClass().getDeclaredField('allowStaticMethodAccess'),#f.setAccessible(true),#f.set(#_memberAccess,true),#req=@org.apache.struts2.ServletActionContext@getRequest(),#resp=@org.apache.struts2.ServletActionContext@getResponse().getWriter(),#resp.println(#req.getRealPath('/')),#resp.close()");
            }
        };

        ParametersInterceptor pi = new ParametersInterceptor();
        container.inject(pi);
        ValueStack vs = ActionContext.getContext().getValueStack();

        // when
        ValidateAction action = new ValidateAction();
        pi.applyParameters(action, vs, HttpParameters.create(params).build());

        // then
        assertEquals(3, action.getActionMessages().size());

        List<String> actionErrors = new ArrayList<>(action.getActionMessages());

        String msg1 = actionErrors.get(0);
        String msg2 = actionErrors.get(1);
        String msg3 = actionErrors.get(2);

        assertEquals("Unexpected Exception caught setting 'expression' on 'class org.apache.struts2.interceptor.parameter.ValidateAction: Error setting expression 'expression' with value '#f=#_memberAccess.getClass().getDeclaredField('allowStaticMethodAccess'),#f.setAccessible(true),#f.set(#_memberAccess,true),#req=@org.apache.struts2.ServletActionContext@getRequest(),#resp=@org.apache.struts2.ServletActionContext@getResponse().getWriter(),#resp.println(#req.getRealPath('/')),#resp.close()'", msg1);
        assertEquals("Unexpected Exception caught setting 'name' on 'class org.apache.struts2.interceptor.parameter.ValidateAction: Error setting expression 'name' with value '(#context[\"xwork.MethodAccessor.denyMethodExecution\"]= new java.lang.Boolean(false), #_memberAccess[\"allowStaticMethodAccess\"]= new java.lang.Boolean(true), @java.lang.Runtime@getRuntime().exec('mkdir /tmp/PWNAGE'))(meh)'", msg2);
        assertEquals("Unexpected Exception caught setting 'top['name'](0)' on 'class org.apache.struts2.interceptor.parameter.ValidateAction: Error setting expression 'top['name'](0)' with value 'true'", msg3);
        assertNull(action.getName());
    }

    public void testClassPollutionBlockedByPattern() throws Exception {
        // given
        final String pollution1 = "class.classLoader.jarPath";
        final String pollution2 = "model.class.classLoader.jarPath";

        loadConfigurationProviders(new StrutsDefaultConfigurationProvider(), new StrutsXmlConfigurationProvider("xwork-param-test.xml"));
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put(pollution1, "bad");
                put(pollution2, "very bad");
            }
        };

        final Map<String, Boolean> excluded = new HashMap<>();
        ParametersInterceptor pi = new ParametersInterceptor() {

            @Override
            protected boolean isExcluded(String paramName) {
                boolean result = super.isExcluded(paramName);
                excluded.put(paramName, result);
                return result;
            }

        };

        container.inject(pi);
        ValueStack vs = ActionContext.getContext().getValueStack();

        // when
        ValidateAction action = new ValidateAction();
        pi.applyParameters(action, vs, HttpParameters.create(params).build());

        // then
        assertEquals(0, action.getActionMessages().size());
        assertTrue(excluded.get(pollution1));
        assertTrue(excluded.get(pollution2));
    }

    public void testClassPollutionBlockedByOgnl() throws Exception {
        // given
        final String pollution1 = "class.classLoader.jarPath";
        final String pollution2 = "model.class.classLoader.jarPath";
        final String pollution3 = "class.classLoader.defaultAssertionStatus";

        loadConfigurationProviders(new StrutsDefaultConfigurationProvider(), new StrutsXmlConfigurationProvider("xwork-class-param-test.xml"));
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put(pollution1, "bad");
                put(pollution2, "very bad");
                put(pollution3, true);
            }
        };

        final Map<String, Boolean> excluded = new HashMap<>();
        ParametersInterceptor pi = new ParametersInterceptor() {

            @Override
            protected boolean isExcluded(String paramName) {
                boolean result = super.isExcluded(paramName);
                excluded.put(paramName, result);
                return result;
            }

        };

        container.inject(pi);
        ValueStack vs = ActionContext.getContext().getValueStack();

        // when
        ValidateAction action = new ValidateAction();
        pi.applyParameters(action, vs, HttpParameters.create(params).build());

        // then
        assertEquals(3, action.getActionMessages().size());

        List<String> actionErrors = new ArrayList<>(action.getActionMessages());
        String msg1 = actionErrors.get(0);
        String msg2 = actionErrors.get(1);
        String msg3 = actionErrors.get(2);

        assertEquals("Unexpected Exception caught setting 'class.classLoader.defaultAssertionStatus' on 'class org.apache.struts2.interceptor.parameter.ValidateAction: Error setting expression 'class.classLoader.defaultAssertionStatus' with value 'true'", msg1);
        assertEquals("Unexpected Exception caught setting 'class.classLoader.jarPath' on 'class org.apache.struts2.interceptor.parameter.ValidateAction: Error setting expression 'class.classLoader.jarPath' with value 'bad'", msg2);
        assertEquals("Unexpected Exception caught setting 'model.class.classLoader.jarPath' on 'class org.apache.struts2.interceptor.parameter.ValidateAction: Error setting expression 'model.class.classLoader.jarPath' with value 'very bad'", msg3);

        assertFalse(excluded.get(pollution1));
        assertFalse(excluded.get(pollution2));
        assertFalse(excluded.get(pollution3));
    }

    public void testDoesNotAllowMethodInvocations() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("@java.lang.System@exit(1).dummy", "dumb value");

        ActionContext extraContext = ActionContext.of().withParameters(HttpParameters.create(params).build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.MODEL_DRIVEN_PARAM_TEST, null, extraContext.getContextMap());
        assertEquals(Action.SUCCESS, proxy.execute());

        String property = System.getProperty("xwork.security.test");
        assertNull(property);
    }

    public void testModelDrivenParameters() throws Exception {
        Map<String, Object> params = new HashMap<>();
        final String fooVal = "org.apache.struts2.interceptor.parameter.ParametersInterceptorTest.foo";
        params.put("foo", fooVal);

        final String nameVal = "org.apache.struts2.interceptor.parameter.ParametersInterceptorTest.name";
        params.put("name", nameVal);
        params.put("count", "15");

        ActionContext extraContext = ActionContext.of().withParameters(HttpParameters.create(params).build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.MODEL_DRIVEN_PARAM_TEST, null, extraContext.getContextMap());
        assertEquals(Action.SUCCESS, proxy.execute());

        ModelDrivenAction action = (ModelDrivenAction) proxy.getAction();
        TestBean model = (TestBean) action.getModel();
        assertEquals(nameVal, model.getName());
        assertEquals(15, model.getCount());
        assertEquals(fooVal, action.getFoo());
    }

    public void testParametersDoesNotAffectSession() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("blah", "This is blah");
        params.put("#session.foo", "Foo");
        params.put("\u0023session['user']", "0wn3d");
        params.put("\\u0023session['user']", "0wn3d");
        params.put("\u0023session.user2", "0wn3d");
        params.put("\\u0023session.user2", "0wn3d");
        params.put("('\u0023'%20%2b%20'session['user3']')(unused)", "0wn3d");
        params.put("('\\u0023' + 'session[\\'user4\\']')(unused)", "0wn3d");
        params.put("('\u0023'%2b'session['user5']')(unused)", "0wn3d");
        params.put("('\\u0023'%2b'session['user5']')(unused)", "0wn3d");

        ActionContext extraContext = ActionContext.of().withParameters(HttpParameters.create(params).build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, null, extraContext.getContextMap());
        ValueStack stack = proxy.getInvocation().getStack();
        HashMap<String, Object> session = new HashMap<>();
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

    public void testArrayClassPollutionBlockedByPattern() throws Exception {
        // given
        final String pollution1 = "model.class.classLoader.jarPath";
        final String pollution2 = "model['class']['classLoader']['jarPath']";
        final String pollution3 = "model[\"class\"]['classLoader']['jarPath']";
        final String pollution4 = "class.classLoader.jarPath";
        final String pollution5 = "class['classLoader']['jarPath']";
        final String pollution6 = "class[\"classLoader\"]['jarPath']";

        loadConfigurationProviders(new StrutsDefaultConfigurationProvider(), new StrutsXmlConfigurationProvider("xwork-param-test.xml"));
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put(pollution1, "bad");
                put(pollution2, "bad");
                put(pollution3, "bad");
                put(pollution4, "bad");
                put(pollution5, "bad");
                put(pollution6, "bad");
            }
        };

        final Map<String, Boolean> excluded = new HashMap<>();
        ParametersInterceptor pi = new ParametersInterceptor() {

            @Override
            protected boolean isExcluded(String paramName) {
                boolean result = super.isExcluded(paramName);
                excluded.put(paramName, result);
                return result;
            }

        };

        container.inject(pi);
        ValueStack vs = ActionContext.getContext().getValueStack();

        // when
        ValidateAction action = new ValidateAction();
        pi.applyParameters(action, vs, HttpParameters.create(params).build());

        // then
        assertEquals(0, action.getActionMessages().size());
        assertTrue(excluded.get(pollution1));
        assertTrue(excluded.get(pollution2));
        assertTrue(excluded.get(pollution3));
        assertTrue(excluded.get(pollution4));
        assertTrue(excluded.get(pollution5));
        assertTrue(excluded.get(pollution6));
    }

    public void testAccessToOgnlInternals() throws Exception {
        // given
        Map<String, Object> params = new HashMap<>();
        params.put("blah", "This is blah");
        params.put("('\\u0023_memberAccess[\\'allowStaticFieldAccess\\']')(meh)", "true");
        params.put("('(aaa)(('\\u0023context[\\'xwork.MethodAccessor.denyMethodExecution\\']\\u003d\\u0023foo')(\\u0023foo\\u003dnew java.lang.Boolean(\"true\")))", "");
        params.put("(asdf)(('\\u0023rt.exit(1)')(\\u0023rt\\u003d@java.lang.Runtime@getRuntime()))", "1");

        ActionContext extraContext = ActionContext.of().withParameters(HttpParameters.create(params).build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, null, extraContext.getContextMap());
        ValueStack stack = proxy.getInvocation().getStack();

        // when
        proxy.execute();
        proxy.getAction();

        //then
        assertEquals("This is blah", ((SimpleAction) proxy.getAction()).getBlah());
        Field field = ReflectionContextState.class.getField("DENY_METHOD_EXECUTION");
        boolean allowStaticFieldAccess = ((OgnlContext) stack.getContext()).getMemberAccess().isAccessible(stack.getContext(), ReflectionContextState.class, field, "");
        assertFalse(allowStaticFieldAccess);
    }

    public void testParameters() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("blah", "This is blah");

        ActionContext extraContext = ActionContext.of().withParameters(HttpParameters.create(params).build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, null, extraContext.getContextMap());
        proxy.execute();
        assertEquals("This is blah", ((SimpleAction) proxy.getAction()).getBlah());
    }

    public void testActionSupportParametersBlocked() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("actionErrors", "fakeError");
        params.put("actionMessages", "fakeMessage");

        ActionContext extraContext = ActionContext.of().withParameters(HttpParameters.create(params).build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, null, extraContext.getContextMap());
        proxy.execute();
        assertEquals(0, ((ActionSupport) proxy.getAction()).getActionMessages().size());
        assertEquals(0, ((ActionSupport) proxy.getAction()).getActionErrors().size());
    }

    public void testParametersWithSpacesInTheName() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("theProtectedMap['p0 p1']", "test1");
        params.put("theProtectedMap['p0p1 ']", "test2");
        params.put("theProtectedMap[' p0p1 ']", "test3");
        params.put("theProtectedMap[' p0 p1 ']", "test4");

        ActionContext extraContext = ActionContext.of().withParameters(HttpParameters.create(params).build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, null, extraContext.getContextMap());
        proxy.execute();
        Map<String, String> existingMap = ((SimpleAction) proxy.getAction()).getTheProtectedMap();
        assertEquals(0, existingMap.size());
    }

    public void testParametersWithChineseInTheName() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("theProtectedMap['名字']", "test1");

        ActionContext extraContext = ActionContext.of().withParameters(HttpParameters.create(params).build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, null, extraContext.getContextMap());
        proxy.execute();
        Map<String, String> existingMap = ((SimpleAction) proxy.getAction()).getTheProtectedMap();
        assertEquals(1, existingMap.size());
    }

    public void testLargeParameterNameWithDefaultLimit() throws Exception {
        ParametersInterceptor parametersInterceptor = createParametersInterceptor();
        doTestParameterNameLengthRestriction(parametersInterceptor, ParametersInterceptor.PARAM_NAME_MAX_LENGTH);
    }

    public void testLargeParameterNameWithCustomLimit() throws Exception {
        ParametersInterceptor parametersInterceptor = createParametersInterceptor();
        int limit = 20;
        parametersInterceptor.setParamNameMaxLength(limit);
        doTestParameterNameLengthRestriction(parametersInterceptor, limit);
    }

    private void doTestParameterNameLengthRestriction(ParametersInterceptor parametersInterceptor,
                                                      int paramNameMaxLength) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paramNameMaxLength + 1; i++) {
            sb.append("x");
        }

        Map<String, Object> actual = new LinkedHashMap<>();
        parametersInterceptor.setValueStackFactory(createValueStackFactory(actual));
        ValueStack stack = createStubValueStack(actual);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put(sb.toString(), "");
        parameters.put("huuhaa", "");

        Action action = new SimpleAction();
        parametersInterceptor.applyParameters(action, stack, HttpParameters.create(parameters).build());
        assertEquals(1, actual.size());
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

        ActionContext extraContext = ActionContext.of().withParameters(HttpParameters.create(params).build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, null, extraContext.getContextMap());

        ActionConfig config = configuration.getRuntimeConfiguration().getActionConfig("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME);
        ParametersInterceptor pi = (ParametersInterceptor) config.getInterceptors().get(0).getInterceptor();
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

        ActionContext extraContext = ActionContext.of().withParameters(HttpParameters.create(params).build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, null, extraContext.getContextMap());

        ActionConfig config = configuration.getRuntimeConfiguration().getActionConfig("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME);
        ParametersInterceptor pi = (ParametersInterceptor) config.getInterceptors().get(0).getInterceptor();
        pi.setAcceptParamNames("blah, baz");

        proxy.execute();

        SimpleAction action = (SimpleAction) proxy.getAction();
        assertNull("try_1", action.getName());
        assertEquals("This is blah", (action).getBlah());
        assertEquals(123, action.getBaz());
    }


    public void testParametersNotAccessPrivateVariables() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("protectedMap.foo", "This is blah");
        params.put("theProtectedMap.boo", "This is blah");

        ActionContext extraContext = ActionContext.of().withParameters(HttpParameters.create(params).build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, null, extraContext.getContextMap());
        proxy.execute();
        SimpleAction action = (SimpleAction) proxy.getAction();
        assertEquals(1, action.getTheProtectedMap().size());
        assertNotNull(action.getTheProtectedMap().get("boo"));
        assertNull(action.getTheProtectedMap().get("foo"));
    }

    public void testParametersNotAccessProtectedMethods() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("theSemiProtectedMap.foo", "This is blah");
        params.put("theProtectedMap.boo", "This is blah");

        ActionContext extraContext = ActionContext.of().withParameters(HttpParameters.create(params).build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, null, extraContext.getContextMap());
        proxy.execute();
        SimpleAction action = (SimpleAction) proxy.getAction();
        assertEquals(1, action.getTheProtectedMap().size());
        assertNotNull(action.getTheProtectedMap().get("boo"));
        assertNull(action.getTheProtectedMap().get("foo"));
    }

    /**
     * This test demonstrates a vulnerability which allows to execute arbitrary code.
     * For further details and explanations see https://cwiki.apache.org/confluence/display/WW/S2-009
     *
     * @throws Exception
     */
    public void testEvalExpressionAsParameterName() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("blah", "(#context[\"xwork.MethodAccessor.denyMethodExecution\"]= new " +
            "java.lang.Boolean(false), #_memberAccess[\"allowStaticMethodAccess\"]= new java.lang.Boolean(true), " +
            "@java.lang.Runtime@getRuntime().exec('mkdir /tmp/PWNAGE'))(meh)");
        params.put("top['blah'](0)", "true");

        ActionContext extraContext = ActionContext.of().withParameters(HttpParameters.create(params).build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, null, extraContext.getContextMap());
        proxy.execute();
        @SuppressWarnings("unused")
        SimpleAction action = (SimpleAction) proxy.getAction();
        File pwn = new File("/tmp/PWNAGE");
        boolean dirExists = pwn.exists();
        @SuppressWarnings("unused")
        boolean deleted = pwn.delete();
        Assert.assertFalse("Remote exploit: The PWN folder has been created", dirExists);
    }

    public void testParametersOverwriteField() throws Exception {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("existingMap.boo", "This is blah");

        ActionContext extraContext = ActionContext.of().withParameters(HttpParameters.create(params).build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, null, extraContext.getContextMap());
        proxy.execute();
        SimpleAction action = (SimpleAction) proxy.getAction();
        assertEquals(1, action.getTheExistingMap().size());
        assertNotNull(action.getTheExistingMap().get("boo"));
        assertNull(action.getTheExistingMap().get("existingKey"));
    }

    public void testNonexistentParametersGetLoggedInDevMode() throws Exception {
        XmlConfigurationProvider provider = new StrutsXmlConfigurationProvider("xwork-test-beans.xml");
        container.inject(provider);
        loadConfigurationProviders(provider,
            new MockConfigurationProvider(Collections.singletonMap("struts.devMode", "true")));
        Map<String, Object> params = new HashMap<>();
        params.put("not_a_property", "There is no action property named like this");

        ActionContext extraContext = ActionContext.of().withParameters(HttpParameters.create(params).build());

        ActionConfig config = configuration.getRuntimeConfiguration().getActionConfig("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME);
        container.inject(config.getInterceptors().get(0).getInterceptor());
        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, null, extraContext.getContextMap());
        proxy.execute();
        final String actionError = "" + ((SimpleAction) proxy.getAction()).getActionMessages().toArray()[0];
        assertTrue(actionError.contains("Error setting expression 'not_a_property' with value 'There is no action property named like this'"));
    }

    public void testNonexistentParametersAreIgnoredInProductionMode() throws Exception {
        XmlConfigurationProvider provider = new StrutsXmlConfigurationProvider("xwork-test-beans.xml");
        container.inject(provider);
        loadConfigurationProviders(provider,
            new MockConfigurationProvider(Collections.singletonMap("struts.devMode", "false")));
        Map<String, Object> params = new HashMap<>();
        params.put("not_a_property", "There is no action property named like this");

        ActionContext extraContext = ActionContext.of().withParameters(HttpParameters.create(params).build());

        ActionConfig config = configuration.getRuntimeConfiguration().getActionConfig("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME);
        container.inject(config.getInterceptors().get(0).getInterceptor());
        ActionProxy proxy = actionProxyFactory.createActionProxy("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, null, extraContext.getContextMap());
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
        final Map<String, Object> actual = new LinkedHashMap<>();
        pi.setValueStackFactory(createValueStackFactory(actual));
        ValueStack stack = createStubValueStack(actual);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user.address.city", "London");
        parameters.put("user.name", "Superman");

        Action action = new SimpleAction();
        pi.applyParameters(action, stack, HttpParameters.create(parameters).build());

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
        final Map<String, Object> actual = new LinkedHashMap<>();
        pi.setValueStackFactory(createValueStackFactory(actual));
        ValueStack stack = createStubValueStack(actual);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("user.address.city", "London");
        parameters.put("user.address['postal']", "QJR387");
        parameters.put("user.name", "Superman");

        Action action = new SimpleAction();
        pi.applyParameters(action, stack, HttpParameters.create(parameters).build());

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
        final Map<String, Object> actual = injectValueStackFactory(pi);
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
        pi.applyParameters(new NoParametersAction(), stack, HttpParameters.create(parameters).build());
        assertEquals(expected, actual);
    }

    public void testInternalParametersAreIgnored() throws Exception {
        // given
        ParametersInterceptor interceptor = createParametersInterceptor();
        final Map<String, Object> actual = injectValueStackFactory(interceptor);
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
        interceptor.applyParameters(new NoParametersAction(), stack, HttpParameters.create(parameters).build());

        // then
        assertEquals(expected, actual);
    }

    public void testDMIMethodsAreIgnored() throws Exception {
        // given
        ParametersInterceptor interceptor = createParametersInterceptor();
        final Map<String, Object> actual = injectValueStackFactory(interceptor);
        ValueStack stack = injectValueStack(actual);

        final Map<String, Object> expected = new HashMap<String, Object>() {
            {
                put("ordinary.bean", "value");
            }
        };

        Map<String, Object> parameters = new HashMap<String, Object>() {
            {
                put("ordinary.bean", "value");
                put("action:", "myAction");
                put("method:", "doExecute");
            }
        };

        // when
        interceptor.applyParameters(new NoParametersAction(), stack, HttpParameters.create(parameters).build());

        // then
        assertEquals(expected, actual);
    }

    public void testBeanListSingleValue() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("beanList.name", new String[]{"Superman"});

        ActionContext extraContext = ActionContext.of().withParameters(HttpParameters.create(params).build());

        ActionProxy proxy = actionProxyFactory.createActionProxy("",
            MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME, null, extraContext.getContextMap());
        proxy.execute();
        SimpleAction action = (SimpleAction) proxy.getAction();
        assertNotNull(action);
        assertNotNull(action.getBeanList());
        assertFalse(action.getBeanList().isEmpty());
    }

    public void testExcludedParametersValuesAreIgnored() throws Exception {
        ParametersInterceptor pi = createParametersInterceptor();
        // Contains (based on pattern)
        pi.setExcludedValuePatterns(".*\\$\\{.*?\\}.*,.*%\\{.*?\\}.*");

        assertTrue("${2*2} was excluded by isParamValueExcluded", pi.isParamValueExcluded("${2*2}"));

        final Map<String, Object> actual = injectValueStackFactory(pi);
        ValueStack stack = injectValueStack(actual);

        final Map<String, Object> expected = new HashMap<String, Object>() {
            {
                put("fooKey", "fooValue");
                put("fooKey2", "");
            }
        };

        Map<String, Object> parameters = new HashMap<String, Object>() {
            {
                put("barKey$", "${2+2}");
                put("barKey2$", "foo${2+2}");
                put("barKey3$", "foo${2+2}foo");
                put("barKey%", "%{2+2}");
                put("barKey2%", "foo%{2+2}");
                put("barKey3%", "foo%{2+2}foo");
                put("allowedKey", "${foo}");
                put("allowedKey2", "%{bar}");
                put("fooKey", "fooValue");
                put("fooKey2", "");
            }
        };
        pi.applyParameters(new NoParametersAction(), stack, HttpParameters.create(parameters).build());
        assertEquals(expected, actual);
    }

    public void testAcceptedParametersValuesAreIgnored() throws Exception {
        ParametersInterceptor pi = createParametersInterceptor();
        // Starts with (based on pattern)
        pi.setAcceptedValuePatterns("^\\$\\{foo\\}.*,^%\\{bar\\}.*,^fooValue");

        assertTrue("${foo} was allowed by isParamValueAccepted", pi.isParamValueAccepted("${foo}"));

        final Map<String, Object> actual = injectValueStackFactory(pi);
        ValueStack stack = injectValueStack(actual);

        final Map<String, Object> expected = new HashMap<String, Object>() {
            {
                put("fooKey", "fooValue");
                put("fooKey2", "");
                put("allowedKey", "${foo}");
                put("allowedKey2", "%{bar}");
            }
        };

        Map<String, Object> parameters = new HashMap<String, Object>() {
            {
                put("barKey$", "${2+2}");
                put("barKey2$", "foo${2+2}");
                put("barKey3$", "foo${2+2}foo");
                put("barKey%", "%{2+2}");
                put("barKey2%", "foo%{2+2}");
                put("barKey3%", "foo%{2+2}foo");
                put("allowedKey", "${foo}");
                put("allowedKey2", "%{bar}");
                put("fooKey", "fooValue");
                put("fooKey2", "");
            }
        };
        pi.applyParameters(new NoParametersAction(), stack, HttpParameters.create(parameters).build());
        assertEquals(expected, actual);
    }

    public void testAcceptedAndExcludedParametersValuesAreIgnored() throws Exception {
        ParametersInterceptor pi = createParametersInterceptor();
        // Starts with (based on pattern)
        pi.setAcceptedValuePatterns("^\\$\\{foo\\}.*,^%\\{bar\\}.*,^fooValue");
        pi.setExcludedValuePatterns(".*\\$\\{2.*2\\}.*,.*\\%\\{2.*2\\}.*");

        assertTrue("${foo} was allowed by isParamValueAccepted", pi.isParamValueAccepted("${foo}"));
        assertTrue("${2*2} was excluded by isParamValueExcluded", pi.isParamValueExcluded("${2*2}"));

        final Map<String, Object> actual = injectValueStackFactory(pi);
        ValueStack stack = injectValueStack(actual);

        final Map<String, Object> expected = new HashMap<String, Object>() {
            {
                put("fooKey", "fooValue");
                put("allowedKey", "${foo}");
                put("allowedKey2", "%{bar}");
                put("fooKey2", "");
            }
        };

        Map<String, Object> parameters = new HashMap<String, Object>() {
            {
                put("barKey$", "${2+2}");
                put("barKey2$", "foo${2+2}");
                put("barKey%", "%{2+2}");
                put("barKey2%", "foo%{2+2}");
                put("barKey3", "nothing");

                put("allowedKey", "${foo}");
                put("allowedKey2", "%{bar}");
                put("fooKey", "fooValue");
                put("fooKey2", "");
            }
        };
        pi.applyParameters(new NoParametersAction(), stack, HttpParameters.create(parameters).build());
        assertEquals(expected, actual);
    }

    public void testExcludedParametersValuesAreIgnoredWithParameterValueAware() throws Exception {
        ParametersInterceptor pi = createParametersInterceptor();
        // Contains (based on pattern)
        pi.setExcludedValuePatterns(".*\\$\\{.*?\\}.*,.*%\\{.*?\\}.*");

        assertTrue("${2*2} was excluded by isParamValueExcluded", pi.isParamValueExcluded("${2*2}"));

        final Map<String, Object> actual = injectValueStackFactory(pi);
        ValueStack stack = injectValueStack(actual);

        final Map<String, Object> expected = new HashMap<String, Object>() {
            {
            	// acceptableParameterValue only allows fooValue even though fooKey2 and fooKey3 pass the excludeValuePatterns check
                put("fooKey", "fooValue");
            }
        };

        Object a = new ParameterValueAware() {
			@Override
			public boolean acceptableParameterValue(String parameterValue) {
				// Only fooValue will be allowed because the excludeValuePatterns will block ${2+2}
				return parameterValue.equals("fooValue") || parameterValue.equals("${2+2}");
			}
		};

        Map<String, Object> parameters = new HashMap<String, Object>() {
            {
                put("barKey$", "${2+2}");
                put("barKey2$", "foo${2+2}");
                put("barKey3$", "foo${2+2}foo");
                put("barKey%", "%{2+2}");
                put("barKey2%", "foo%{2+2}");
                put("barKey3%", "foo%{2+2}foo");
                put("allowedKey", "${foo}");
                put("allowedKey2", "%{bar}");
                put("fooKey", "fooValue");
                put("fooKey2", "fooValue2");
                put("fooKey3", "");
            }
        };
        pi.applyParameters(a, stack, HttpParameters.create(parameters).build());
        assertEquals(expected, actual);
    }


    private ValueStack injectValueStack(Map<String, Object> actual) {
        ValueStack stack = createStubValueStack(actual);
        container.inject(stack);
        return stack;
    }

    private Map<String, Object> injectValueStackFactory(ParametersInterceptor interceptor) {
        final Map<String, Object> actual = new HashMap<>();
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
            (CompoundRootAccessor) container.getInstance(RootAccessor.class),
            container.getInstance(TextProvider.class, "system"), new SecurityMemberAccess(null, null)) {
            @Override
            public void setValue(String expr, Object value) {
                actual.put(expr, value);
            }

            @Override
            public void setParameter(String expr, Object value) {
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
        XmlConfigurationProvider provider = new StrutsXmlConfigurationProvider("xwork-test-beans.xml");
        container.inject(provider);
        loadConfigurationProviders(provider, new MockConfigurationProvider());

        ActionConfig config = configuration.getRuntimeConfiguration().getActionConfig("", MockConfigurationProvider.PARAM_INTERCEPTOR_ACTION_NAME);
        container.inject(config.getInterceptors().get(0).getInterceptor());
    }

}

class ValidateAction implements ValidationAware {

    private final List<String> messages = new LinkedList<>();
    private final List<String> errors = new LinkedList<>();
    private String name;

    @Override
    public void setActionErrors(Collection<String> errorMessages) {
    }

    @Override
    public Collection<String> getActionErrors() {
        return errors;
    }

    @Override
    public void setActionMessages(Collection<String> messages) {
    }

    @Override
    public Collection<String> getActionMessages() {
        return messages;
    }

    @Override
    public void setFieldErrors(Map<String, List<String>> errorMap) {
    }

    @Override
    public Map<String, List<String>> getFieldErrors() {
        return null;
    }

    @Override
    public void addActionError(String anErrorMessage) {
        errors.add(anErrorMessage);
    }

    @Override
    public void addActionMessage(String aMessage) {
        messages.add(aMessage);
    }

    @Override
    public void addFieldError(String fieldName, String errorMessage) {
    }

    @Override
    public boolean hasActionErrors() {
        return !errors.isEmpty();
    }

    @Override
    public boolean hasActionMessages() {
        return !messages.isEmpty();
    }

    @Override
    public boolean hasFieldErrors() {
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
