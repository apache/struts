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

import org.aopalliance.intercept.Joinpoint;
import org.aopalliance.intercept.MethodInterceptor;
import org.apache.commons.lang3.ClassUtils;
import org.apache.struts2.ActionContext;
import org.apache.struts2.ModelDriven;
import org.apache.struts2.StubValueStack;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;
import org.apache.struts2.ognl.DefaultOgnlBeanInfoCacheFactory;
import org.apache.struts2.ognl.DefaultOgnlExpressionCacheFactory;
import org.apache.struts2.ognl.OgnlUtil;
import org.apache.struts2.ognl.StrutsOgnlGuard;
import org.apache.struts2.ognl.ThreadAllowlist;
import org.apache.struts2.security.AcceptedPatternsChecker.IsAccepted;
import org.apache.struts2.security.ExcludedPatternsChecker.IsExcluded;
import org.apache.struts2.security.NotExcludedAcceptedPatternsChecker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.apache.struts2.ognl.OgnlCacheFactory.CacheType.LRU;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StrutsParameterAnnotationTest {

    private ParametersInterceptor parametersInterceptor;

    private ThreadAllowlist threadAllowlist;

    @Before
    public void setUp() throws Exception {
        parametersInterceptor = new ParametersInterceptor();
        parametersInterceptor.setRequireAnnotations(Boolean.TRUE.toString());

        threadAllowlist = new ThreadAllowlist();
        parametersInterceptor.setThreadAllowlist(threadAllowlist);

        var ognlUtil = new OgnlUtil(
                new DefaultOgnlExpressionCacheFactory<>(String.valueOf(1000), LRU.toString()),
                new DefaultOgnlBeanInfoCacheFactory<>(String.valueOf(1000), LRU.toString()),
                new StrutsOgnlGuard());
        parametersInterceptor.setOgnlUtil(ognlUtil);

        NotExcludedAcceptedPatternsChecker checker = mock(NotExcludedAcceptedPatternsChecker.class);
        when(checker.isAccepted(anyString())).thenReturn(IsAccepted.yes(""));
        when(checker.isExcluded(anyString())).thenReturn(IsExcluded.no(Set.of()));
        parametersInterceptor.setAcceptedPatterns(checker);
        parametersInterceptor.setExcludedPatterns(checker);
    }

    @After
    public void tearDown() throws Exception {
        threadAllowlist.clearAllowlist();
        ActionContext.clear();
    }

    private void testParameter(Object action, String paramName, boolean shouldContain) {
        Map<String, String[]> requestParamMap = new HashMap<>();
        requestParamMap.put(paramName, new String[]{"value"});
        HttpParameters httpParameters = HttpParameters.create(requestParamMap).build();

        Map<String, Parameter> acceptedParameters = parametersInterceptor.toAcceptableParameters(httpParameters, action);

        if (shouldContain) {
            assertThat(acceptedParameters).containsOnlyKeys(paramName);
        } else {
            assertThat(acceptedParameters).isEmpty();
            assertThat(threadAllowlist.getAllowlist()).isEmpty();
        }
    }

    private Set<Class<?>> getParentClasses(Class<?>... clazzes) {
        Set<Class<?>> set = new HashSet<>();
        for (Class<?> clazz : clazzes) {
            set.add(clazz);
            set.addAll(ClassUtils.getAllSuperclasses(clazz));
            set.addAll(ClassUtils.getAllInterfaces(clazz));
        }
        return set;
    }

    /**
     * Private String field cannot be injected even when annotated.
     */
    @Test
    public void privateStrAnnotated() {
        testParameter(new FieldAction(), "privateStr", false);
    }

    /**
     * Public String field can be injected when annotated.
     */
    @Test
    public void publicStrAnnotated() {
        testParameter(new FieldAction(), "publicStr", true);
        assertThat(threadAllowlist.getAllowlist()).isEmpty();
    }

    /**
     * Public String field cannot be injected when not annotated.
     */
    @Test
    public void publicStrNotAnnotated() {
        testParameter(new FieldAction(), "publicStrNotAnnotated", false);
    }

    /**
     * Private Pojo field cannot be injected even when annotated with the appropriate depth.
     */
    @Test
    public void privatePojoAnnotated() {
        testParameter(new FieldAction(), "privatePojo.key", false);
    }

    /**
     * Public Pojo field cannot be injected when annotated with depth zero.
     */
    @Test
    public void publicPojoDepthZero() {
        testParameter(new FieldAction(), "publicPojoDepthZero.key", false);
    }

    /**
     * Public Pojo field can be injected when annotated with depth one.
     */
    @Test
    public void publicPojoDepthOne() {
        testParameter(new FieldAction(), "publicPojoDepthOne.key", true);
        assertThat(threadAllowlist.getAllowlist()).containsExactlyInAnyOrderElementsOf(getParentClasses(Pojo.class));
    }

    /**
     * Public Pojo field can be injected when annotated with depth one, using the square bracket syntax.
     */
    @Test
    public void publicPojoDepthOne_sqrBracket() {
        testParameter(new FieldAction(), "publicPojoDepthOne['key']", true);
        assertThat(threadAllowlist.getAllowlist()).containsExactlyInAnyOrderElementsOf(getParentClasses(Pojo.class));
    }

    /**
     * Public Pojo field can be injected when annotated with depth one, using the bracket syntax.
     */
    @Test
    public void publicPojoDepthOne_bracket() {
        testParameter(new FieldAction(), "publicPojoDepthOne('key')", true);
        assertThat(threadAllowlist.getAllowlist()).containsExactlyInAnyOrderElementsOf(getParentClasses(Pojo.class));
    }

    /**
     * Public Pojo field can be injected when annotated with a depth greater than required.
     */
    @Test
    public void publicPojoDepthTwo() {
        testParameter(new FieldAction(), "publicPojoDepthTwo.key", true);
        assertThat(threadAllowlist.getAllowlist()).containsExactlyInAnyOrderElementsOf(getParentClasses(Pojo.class));
    }

    /**
     * Public Pojo field cannot be injected two levels when only annotated with depth one.
     */
    @Test
    public void publicNestedPojoDepthOne() {
        testParameter(new FieldAction(), "publicPojoDepthOne.key.key", false);
    }

    /**
     * Public Pojo field can be injected two levels when annotated with depth two.
     */
    @Test
    public void publicNestedPojoDepthTwo() {
        testParameter(new FieldAction(), "publicPojoDepthTwo.key.key", true);
        assertThat(threadAllowlist.getAllowlist()).containsExactlyInAnyOrderElementsOf(getParentClasses(Pojo.class));
    }

    /**
     * Public Pojo field can be injected two levels when annotated with depth two, using the square bracket syntax.
     */
    @Test
    public void publicNestedPojoDepthTwo_sqrBracket() {
        testParameter(new FieldAction(), "publicPojoDepthTwo['key']['key']", true);
        assertThat(threadAllowlist.getAllowlist()).containsExactlyInAnyOrderElementsOf(getParentClasses(Pojo.class));
    }

    /**
     * Public Pojo field can be injected two levels when annotated with depth two, using the bracket syntax.
     */
    @Test
    public void publicNestedPojoDepthTwo_bracket() {
        testParameter(new FieldAction(), "publicPojoDepthTwo('key')('key')", true);
        assertThat(threadAllowlist.getAllowlist()).containsExactlyInAnyOrderElementsOf(getParentClasses(Pojo.class));
    }

    /**
     * Private String setting method cannot be injected even when annotated.
     */
    @Test
    public void privateStrAnnotatedMethod() {
        testParameter(new MethodAction(), "privateStr", false);
    }

    /**
     * Public String setting method can be injected when annotated.
     */
    @Test
    public void publicStrAnnotatedMethod() {
        testParameter(new MethodAction(), "publicStr", true);
        assertThat(threadAllowlist.getAllowlist()).isEmpty();
    }

    /**
     * Public String setting method cannot be injected when not annotated.
     */
    @Test
    public void publicStrNotAnnotatedMethod() {
        testParameter(new MethodAction(), "publicStrNotAnnotated", false);
    }

    /**
     * Private Pojo returning method cannot be injected even when annotated with the appropriate depth.
     */
    @Test
    public void privatePojoAnnotatedMethod() {
        testParameter(new MethodAction(), "privatePojo.key", false);
    }

    /**
     * Public Pojo returning method cannot be injected when annotated with depth zero.
     */
    @Test
    public void publicPojoDepthZeroMethod() {
        testParameter(new MethodAction(), "publicPojoDepthZero.key", false);
    }

    /**
     * Public Pojo returning method can be injected when annotated with depth one.
     */
    @Test
    public void publicPojoDepthOneMethod() {
        testParameter(new MethodAction(), "publicPojoDepthOne.key", true);
        assertThat(threadAllowlist.getAllowlist()).containsExactlyInAnyOrderElementsOf(getParentClasses(Pojo.class));
    }

    /**
     * Public Pojo returning method cannot be injected two levels when only annotated with depth one.
     */
    @Test
    public void publicNestedPojoDepthOneMethod() {
        testParameter(new MethodAction(), "publicPojoDepthOne.key.key", false);
    }

    /**
     * Public Pojo returning method can be injected when annotated with a depth greater than required.
     */
    @Test
    public void publicPojoDepthTwoMethod() {
        testParameter(new MethodAction(), "publicPojoDepthTwo.key", true);
        assertThat(threadAllowlist.getAllowlist()).containsExactlyInAnyOrderElementsOf(getParentClasses(Pojo.class));
    }

    /**
     * Public Pojo returning method can be injected two levels when annotated with depth two.
     */
    @Test
    public void publicNestedPojoDepthTwoMethod() {
        testParameter(new MethodAction(), "publicPojoDepthTwo.key.key", true);
        assertThat(threadAllowlist.getAllowlist()).containsExactlyInAnyOrderElementsOf(getParentClasses(Pojo.class));
    }

    /**
     * Public list of Pojo field cannot be injected when annotated with depth one.
     */
    @Test
    public void publicPojoListDepthOne() {
        testParameter(new FieldAction(), "publicPojoListDepthOne[0].key", false);
    }

    /**
     * Public list of Pojo field can be injected when annotated with depth two.
     */
    @Test
    public void publicPojoListDepthTwo() {
        testParameter(new FieldAction(), "publicPojoListDepthTwo[0].key", true);
        assertThat(threadAllowlist.getAllowlist()).containsExactlyInAnyOrderElementsOf(getParentClasses(List.class, Pojo.class));
    }

    /**
     * Public list of Pojo returning method cannot be injected when annotated with depth one.
     */
    @Test
    public void publicPojoListDepthOneMethod() {
        testParameter(new MethodAction(), "publicPojoListDepthOne[0].key", false);
    }

    /**
     * Public list of Pojo returning method can be injected when annotated with depth two.
     */
    @Test
    public void publicPojoListDepthTwoMethod() {
        testParameter(new MethodAction(), "publicPojoListDepthTwo[0].key", true);
        assertThat(threadAllowlist.getAllowlist()).containsExactlyInAnyOrderElementsOf(getParentClasses(List.class, Pojo.class));
    }

    /**
     * Public map of Pojo field can be injected when annotated with depth two.
     */
    @Test
    public void publicPojoMapDepthTwo() {
        testParameter(new FieldAction(), "publicPojoMapDepthTwo['a'].key", true);
        assertThat(threadAllowlist.getAllowlist()).containsExactlyInAnyOrderElementsOf(getParentClasses(Map.class, String.class, Pojo.class));
    }

    /**
     * Public map of Pojo returning method can be injected when annotated with depth two.
     */
    @Test
    public void publicPojoMapDepthTwoMethod() {
        testParameter(new MethodAction(), "publicPojoMapDepthTwo['a'].key", true);
        assertThat(threadAllowlist.getAllowlist()).containsExactlyInAnyOrderElementsOf(getParentClasses(Map.class, String.class, Pojo.class));
    }

    /**
     * Public String field can be injected even when not annotated, if transition mode is enabled.
     */
    @Test
    public void publicStrNotAnnotated_transitionMode() {
        parametersInterceptor.setRequireAnnotationsTransitionMode(Boolean.TRUE.toString());
        testParameter(new FieldAction(), "publicStrNotAnnotated", true);
    }

    /**
     * Public String setting method can be injected even when not annotated, if transition mode is enabled.
     */
    @Test
    public void publicStrNotAnnotatedMethod_transitionMode() {
        parametersInterceptor.setRequireAnnotationsTransitionMode(Boolean.TRUE.toString());
        testParameter(new MethodAction(), "publicStrNotAnnotated", true);
    }

    /**
     * Models of ModelDriven actions can be injected without any annotations on the Action.
     */
    @Test
    public void publicModelPojo() {
        var action = new ModelAction();

        // Emulate ModelDrivenInterceptor running previously
        var valueStack = new StubValueStack();
        valueStack.push(action.getModel());
        ActionContext.of().withValueStack(valueStack).bind();

        testParameter(action, "name", true);
        testParameter(action, "name.nested", true);
    }

    /**
     * Models of ModelDriven actions can be injected without any annotations on the Action, even when the Action is
     * proxied.
     */
    @Test
    public void publicModelPojo_proxied() {
        var proxyFactory = new ProxyFactory(new ModelAction());
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.addAdvice((MethodInterceptor) Joinpoint::proceed);
        var proxiedAction = (ModelAction) proxyFactory.getProxy();

        // Emulate ModelDrivenInterceptor running previously
        var valueStack = new StubValueStack();
        valueStack.push(proxiedAction.getModel());
        ActionContext.of().withValueStack(valueStack).bind();

        testParameter(proxiedAction, "name", true);
        testParameter(proxiedAction, "name.nested", true);
    }

    public static class FieldAction {
        @StrutsParameter
        private String privateStr;

        @StrutsParameter
        public String publicStr;

        public String publicStrNotAnnotated;

        @StrutsParameter(depth = 1)
        private Pojo privatePojo;

        @StrutsParameter
        public Pojo publicPojoDepthZero;

        @StrutsParameter(depth = 1)
        public Pojo publicPojoDepthOne;

        @StrutsParameter(depth = 2)
        public Pojo publicPojoDepthTwo;

        @StrutsParameter(depth = 1)
        public List<Pojo> publicPojoListDepthOne;

        @StrutsParameter(depth = 2)
        public List<Pojo> publicPojoListDepthTwo;

        @StrutsParameter(depth = 2)
        public Map<String, Pojo> publicPojoMapDepthTwo;
    }

    public static class MethodAction {

        @StrutsParameter
        private void setPrivateStr(String str) {
        }

        @StrutsParameter
        public void setPublicStr(String str) {
        }

        public void setPublicStrNotAnnotated(String str) {
        }

        @StrutsParameter(depth = 1)
        private Pojo getPrivatePojo() {
            return null;
        }

        @StrutsParameter
        public Pojo getPublicPojoDepthZero() {
            return null;
        }

        @StrutsParameter
        public void setPublicPojoDepthZero() {
        }

        @StrutsParameter(depth = 1)
        public Pojo getPublicPojoDepthOne() {
            return null;
        }

        @StrutsParameter(depth = 2)
        public Pojo getPublicPojoDepthTwo() {
            return null;
        }

        @StrutsParameter(depth = 1)
        public List<Pojo> getPublicPojoListDepthOne() {
            return null;
        }

        @StrutsParameter(depth = 2)
        public List<Pojo> getPublicPojoListDepthTwo() {
            return null;
        }

        @StrutsParameter(depth = 2)
        public Map<String, Pojo> getPublicPojoMapDepthTwo() {
            return null;
        }
    }

    public static class ModelAction implements ModelDriven<Pojo> {

        @Override
        public Pojo getModel() {
            return new Pojo();
        }
    }

    public static class Pojo {
    }
}
