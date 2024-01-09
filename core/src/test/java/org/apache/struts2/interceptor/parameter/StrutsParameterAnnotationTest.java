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

import com.opensymphony.xwork2.security.AcceptedPatternsChecker;
import com.opensymphony.xwork2.security.NotExcludedAcceptedPatternsChecker;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;
import org.apache.struts2.ognl.ThreadAllowlist;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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

        NotExcludedAcceptedPatternsChecker checker = mock(NotExcludedAcceptedPatternsChecker.class);
        when(checker.isAccepted(anyString())).thenReturn(AcceptedPatternsChecker.IsAccepted.yes(""));
        when(checker.isExcluded(anyString())).thenReturn(NotExcludedAcceptedPatternsChecker.IsExcluded.no(new HashSet<>()));
        parametersInterceptor.setAcceptedPatterns(checker);
        parametersInterceptor.setExcludedPatterns(checker);
    }

    @After
    public void tearDown() throws Exception {
        threadAllowlist.clearAllowlist();
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

    @Test
    public void privateStrAnnotated() {
        testParameter(new FieldAction(), "privateStr", false);
    }

    @Test
    public void publicStrAnnotated() {
        testParameter(new FieldAction(), "publicStr", true);
        assertThat(threadAllowlist.getAllowlist()).isEmpty();
    }

    @Test
    public void publicStrNotAnnotated() {
        testParameter(new FieldAction(), "publicStrNotAnnotated", false);
    }

    @Test
    public void privatePojoAnnotated() {
        testParameter(new FieldAction(), "privatePojo.key", false);
    }

    @Test
    public void publicPojoDepthZero() {
        testParameter(new FieldAction(), "publicPojoDepthZero.key", false);
    }

    @Test
    public void publicPojoDepthOne() {
        testParameter(new FieldAction(), "publicPojoDepthOne.key", true);
        assertThat(threadAllowlist.getAllowlist()).containsExactly(Pojo.class);
    }

    @Test
    public void publicNestedPojoDepthOne() {
        testParameter(new FieldAction(), "publicPojoDepthOne.key.key", false);
    }

    @Test
    public void publicPojoDepthTwo() {
        testParameter(new FieldAction(), "publicPojoDepthTwo.key", true);
        assertThat(threadAllowlist.getAllowlist()).containsExactly(Pojo.class);
    }

    @Test
    public void publicNestedPojoDepthTwo() {
        testParameter(new FieldAction(), "publicPojoDepthTwo.key.key", true);
        assertThat(threadAllowlist.getAllowlist()).containsExactly(Pojo.class);
    }

    @Test
    public void privateStrAnnotatedMethod() {
        testParameter(new MethodAction(), "privateStr", false);
    }

    @Test
    public void publicStrAnnotatedMethod() {
        testParameter(new MethodAction(), "publicStr", true);
        assertThat(threadAllowlist.getAllowlist()).isEmpty();
    }

    @Test
    public void publicStrNotAnnotatedMethod() {
        testParameter(new MethodAction(), "publicStrNotAnnotated", false);
    }

    @Test
    public void privatePojoAnnotatedMethod() {
        testParameter(new MethodAction(), "privatePojo.key", false);
    }

    @Test
    public void publicPojoDepthZeroMethod() {
        testParameter(new MethodAction(), "publicPojoDepthZero.key", false);
    }

    @Test
    public void publicPojoDepthOneMethod() {
        testParameter(new MethodAction(), "publicPojoDepthOne.key", true);
        assertThat(threadAllowlist.getAllowlist()).containsExactly(Pojo.class);
    }

    @Test
    public void publicNestedPojoDepthOneMethod() {
        testParameter(new MethodAction(), "publicPojoDepthOne.key.key", false);
    }

    @Test
    public void publicPojoDepthTwoMethod() {
        testParameter(new MethodAction(), "publicPojoDepthTwo.key", true);
        assertThat(threadAllowlist.getAllowlist()).containsExactly(Pojo.class);
    }

    @Test
    public void publicNestedPojoDepthTwoMethod() {
        testParameter(new MethodAction(), "publicPojoDepthTwo.key.key", true);
        assertThat(threadAllowlist.getAllowlist()).containsExactly(Pojo.class);
    }

    class FieldAction {
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
        public Pojo publicPojoDepthOne ;

        @StrutsParameter(depth = 2)
        public Pojo publicPojoDepthTwo;
    }

    class MethodAction {

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
    }

    class Pojo {
    }
}
