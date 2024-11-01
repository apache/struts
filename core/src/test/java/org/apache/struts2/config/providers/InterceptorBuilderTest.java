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
package org.apache.struts2.config.providers;

import org.apache.struts2.ActionInvocation;
import org.apache.struts2.ObjectFactory;
import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.config.entities.InterceptorConfig;
import org.apache.struts2.config.entities.InterceptorMapping;
import org.apache.struts2.config.entities.InterceptorStackConfig;
import org.apache.struts2.config.entities.PackageConfig;
import org.apache.struts2.interceptor.AbstractInterceptor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * <code>InterceptorBuilderTest</code>
 */
public class InterceptorBuilderTest extends XWorkTestCase {

    ObjectFactory objectFactory;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        objectFactory = container.getInstance(ObjectFactory.class);
    }

    /**
     * Try to test this
     * <interceptor-ref name="interceptorStack1">
     * <param name="interceptor1.param1">interceptor1_value1</param>
     * <param name="interceptor1.param2">interceptor1_value2</param>
     * <param name="interceptor2.param1">interceptor2_value1</param>
     * <param name="interceptor2.param2">interceptor2_value2</param>
     * </interceptor-ref>
     */
    public void testBuildInterceptor_1() {
        InterceptorStackConfig interceptorStackConfig1 = new InterceptorStackConfig.Builder("interceptorStack1").build();

        InterceptorConfig interceptorConfig1 = new InterceptorConfig.Builder("interceptor1", "org.apache.struts2.config.providers.InterceptorBuilderTest$MockInterceptor1").build();
        InterceptorConfig interceptorConfig2 = new InterceptorConfig.Builder("interceptor2", "org.apache.struts2.config.providers.InterceptorBuilderTest$MockInterceptor2").build();

        PackageConfig packageConfig = new PackageConfig.Builder("package1").namespace("/namespace").addInterceptorConfig(interceptorConfig1).addInterceptorConfig(interceptorConfig2).addInterceptorStackConfig(interceptorStackConfig1).build();

        List<InterceptorMapping> interceptorMappings =
            InterceptorBuilder.constructInterceptorReference(packageConfig, "interceptorStack1",
                new LinkedHashMap<String, String>() {
                    {
                        put("interceptor1.param1", "interceptor1_value1");
                        put("interceptor1.param2", "interceptor1_value2");
                        put("interceptor2.param1", "interceptor2_value1");
                        put("interceptor2.param2", "interceptor2_value2");
                    }
                }, null, objectFactory);

        assertEquals(interceptorMappings.size(), 2);

        assertEquals(interceptorMappings.get(0).getName(), "interceptor1");
        assertNotNull(interceptorMappings.get(0).getInterceptor());
        assertEquals(interceptorMappings.get(0).getInterceptor().getClass(), MockInterceptor1.class);
        assertEquals(((MockInterceptor1) interceptorMappings.get(0).getInterceptor()).getParam1(), "interceptor1_value1");
        assertEquals(((MockInterceptor1) interceptorMappings.get(0).getInterceptor()).getParam2(), "interceptor1_value2");

        assertEquals(interceptorMappings.get(1).getName(), "interceptor2");
        assertNotNull(interceptorMappings.get(1).getInterceptor());
        assertEquals(interceptorMappings.get(1).getInterceptor().getClass(), MockInterceptor2.class);
        assertEquals(((MockInterceptor2) interceptorMappings.get(1).getInterceptor()).getParam1(), "interceptor2_value1");
        assertEquals(((MockInterceptor2) interceptorMappings.get(1).getInterceptor()).getParam2(), "interceptor2_value2");
    }

    public void testMultipleSameInterceptors() {
        InterceptorConfig interceptorConfig1 = new InterceptorConfig.Builder("interceptor1", "org.apache.struts2.config.providers.InterceptorBuilderTest$MockInterceptor1").build();
        InterceptorConfig interceptorConfig2 = new InterceptorConfig.Builder("interceptor2", "org.apache.struts2.config.providers.InterceptorBuilderTest$MockInterceptor2").build();

        InterceptorStackConfig interceptorStackConfig1 = new InterceptorStackConfig.Builder("multiStack")
            .addInterceptor(new InterceptorMapping(interceptorConfig1.getName(), objectFactory.buildInterceptor(interceptorConfig1, Collections.emptyMap())))
            .addInterceptor(new InterceptorMapping(interceptorConfig2.getName(), objectFactory.buildInterceptor(interceptorConfig2, Collections.emptyMap())))
            .addInterceptor(new InterceptorMapping(interceptorConfig1.getName(), objectFactory.buildInterceptor(interceptorConfig1, Collections.emptyMap())))
            .build();

        PackageConfig packageConfig = new PackageConfig.Builder("package1")
            .namespace("/namespace")
            .addInterceptorConfig(interceptorConfig1)
            .addInterceptorConfig(interceptorConfig2)
            .addInterceptorConfig(interceptorConfig1)
            .addInterceptorStackConfig(interceptorStackConfig1)
            .build();

        List<InterceptorMapping> interceptorMappings = InterceptorBuilder.constructInterceptorReference(packageConfig, "multiStack",
            new LinkedHashMap<String, String>() {
                {
                    put("interceptor1.param1", "interceptor1_value1");
                    put("interceptor1.param2", "interceptor1_value2");
                }
            }, null, objectFactory);

        assertEquals(interceptorMappings.size(), 3);

        assertEquals(interceptorMappings.get(0).getName(), "interceptor1");
        assertNotNull(interceptorMappings.get(0).getInterceptor());
        assertEquals(interceptorMappings.get(0).getInterceptor().getClass(), MockInterceptor1.class);
        assertEquals(((MockInterceptor1) interceptorMappings.get(0).getInterceptor()).getParam1(), "interceptor1_value1");
        assertEquals(((MockInterceptor1) interceptorMappings.get(0).getInterceptor()).getParam2(), "interceptor1_value2");

        assertEquals(interceptorMappings.get(1).getName(), "interceptor2");
        assertNotNull(interceptorMappings.get(1).getInterceptor());
        assertEquals(interceptorMappings.get(1).getInterceptor().getClass(), MockInterceptor2.class);

        assertEquals(interceptorMappings.get(2).getName(), "interceptor1");
        assertNotNull(interceptorMappings.get(2).getInterceptor());
        assertEquals(interceptorMappings.get(2).getInterceptor().getClass(), MockInterceptor1.class);
        assertEquals(((MockInterceptor1) interceptorMappings.get(2).getInterceptor()).getParam1(), "interceptor1_value1");
        assertEquals(((MockInterceptor1) interceptorMappings.get(2).getInterceptor()).getParam2(), "interceptor1_value2");
    }

    /**
     * Try to test this
     * <interceptor-ref name="interceptorStack1">
     * <param name="interceptorStack2.interceptor1.param1">interceptor1_value1</param>
     * <param name="interceptorStack2.interceptor1.param2">interceptor1_value2</param>
     * <param name="interceptorStack3.interceptor2.param1">interceptor2_value1</param>
     * <param name="interceptorStack3.interceptor2.param2">interceptor2_value2</param>
     * </interceptor-ref>
     */
    public void testBuildInterceptor_2() {
        InterceptorStackConfig interceptorStackConfig1 = new InterceptorStackConfig.Builder("interceptorStack1").build();
        InterceptorStackConfig interceptorStackConfig2 = new InterceptorStackConfig.Builder("interceptorStack2").build();
        InterceptorStackConfig interceptorStackConfig3 = new InterceptorStackConfig.Builder("interceptorStack3").build();

        InterceptorConfig interceptorConfig1 = new InterceptorConfig.Builder("interceptor1", "org.apache.struts2.config.providers.InterceptorBuilderTest$MockInterceptor1").build();
        InterceptorConfig interceptorConfig2 = new InterceptorConfig.Builder("interceptor2", "org.apache.struts2.config.providers.InterceptorBuilderTest$MockInterceptor2").build();

        PackageConfig packageConfig = new PackageConfig.Builder("package1").namespace("/namespace").
            addInterceptorConfig(interceptorConfig1).
            addInterceptorConfig(interceptorConfig2).
            addInterceptorStackConfig(interceptorStackConfig1).
            addInterceptorStackConfig(interceptorStackConfig2).
            addInterceptorStackConfig(interceptorStackConfig3).build();

        List<InterceptorMapping> interceptorMappings = InterceptorBuilder.constructInterceptorReference(packageConfig, "interceptorStack1",
            new LinkedHashMap<String, String>() {
                {
                    put("interceptorStack2.interceptor1.param1", "interceptor1_value1");
                    put("interceptorStack2.interceptor1.param2", "interceptor1_value2");
                    put("interceptorStack3.interceptor2.param1", "interceptor2_value1");
                    put("interceptorStack3.interceptor2.param2", "interceptor2_value2");
                }
            }, null, objectFactory);

        assertEquals(interceptorMappings.size(), 2);

        assertEquals(interceptorMappings.get(0).getName(), "interceptor1");
        assertNotNull(interceptorMappings.get(0).getInterceptor());
        assertEquals(interceptorMappings.get(0).getInterceptor().getClass(), MockInterceptor1.class);
        assertEquals(((MockInterceptor1) interceptorMappings.get(0).getInterceptor()).getParam1(), "interceptor1_value1");
        assertEquals(((MockInterceptor1) interceptorMappings.get(0).getInterceptor()).getParam2(), "interceptor1_value2");

        assertEquals(interceptorMappings.get(1).getName(), "interceptor2");
        assertNotNull(interceptorMappings.get(1).getInterceptor());
        assertEquals(interceptorMappings.get(1).getInterceptor().getClass(), MockInterceptor2.class);
        assertEquals(((MockInterceptor2) interceptorMappings.get(1).getInterceptor()).getParam1(), "interceptor2_value1");
        assertEquals(((MockInterceptor2) interceptorMappings.get(1).getInterceptor()).getParam2(), "interceptor2_value2");
    }

    /**
     * Try to test this
     * <interceptor-ref name="interceptorStack1">
     * <param name="interceptorStack2.interceptorStack3.interceptorStack4.interceptor1.param1">interceptor1_value1</param>
     * <param name="interceptorStack2.interceptorStack3.interceptorStack4.interceptor1.param2">interceptor1_value2</param>
     * <param name="interceptorStack5.interceptor2.param1">interceptor2_value1</param>
     * <param name="interceptorStack5.interceptor2.param2">interceptor2_value2</param>
     * </interceptor-ref>
     */
    public void testBuildInterceptor_3() {
        InterceptorConfig interceptorConfig1 = new InterceptorConfig.Builder("interceptor1", "org.apache.struts2.config.providers.InterceptorBuilderTest$MockInterceptor1").build();
        InterceptorConfig interceptorConfig2 = new InterceptorConfig.Builder("interceptor2", "org.apache.struts2.config.providers.InterceptorBuilderTest$MockInterceptor2").build();

        InterceptorStackConfig interceptorStackConfig1 = new InterceptorStackConfig.Builder("interceptorStack1").build();
        InterceptorStackConfig interceptorStackConfig2 = new InterceptorStackConfig.Builder("interceptorStack2").build();
        InterceptorStackConfig interceptorStackConfig3 = new InterceptorStackConfig.Builder("interceptorStack3").build();
        InterceptorStackConfig interceptorStackConfig4 = new InterceptorStackConfig.Builder("interceptorStack4").build();
        InterceptorStackConfig interceptorStackConfig5 = new InterceptorStackConfig.Builder("interceptorStack5").build();

        PackageConfig packageConfig = new PackageConfig.Builder("package1").
            addInterceptorConfig(interceptorConfig1).
            addInterceptorConfig(interceptorConfig2).
            addInterceptorStackConfig(interceptorStackConfig1).
            addInterceptorStackConfig(interceptorStackConfig2).
            addInterceptorStackConfig(interceptorStackConfig3).
            addInterceptorStackConfig(interceptorStackConfig4).
            addInterceptorStackConfig(interceptorStackConfig5).build();

        List<InterceptorMapping> interceptorMappings = InterceptorBuilder.constructInterceptorReference(
            packageConfig, "interceptorStack1",
            new LinkedHashMap<String, String>() {
                {
                    put("interceptorStack2.interceptorStack3.interceptorStack4.interceptor1.param1", "interceptor1_value1");
                    put("interceptorStack2.interceptorStack3.interceptorStack4.interceptor1.param2", "interceptor1_value2");
                    put("interceptorStack5.interceptor2.param1", "interceptor2_value1");
                    put("interceptorStack5.interceptor2.param2", "interceptor2_value2");
                }
            }, null, objectFactory);

        assertEquals(interceptorMappings.size(), 2);

        assertEquals(interceptorMappings.get(0).getName(), "interceptor1");
        assertNotNull(interceptorMappings.get(0).getInterceptor());
        assertEquals(interceptorMappings.get(0).getInterceptor().getClass(), MockInterceptor1.class);
        assertEquals(((MockInterceptor1) interceptorMappings.get(0).getInterceptor()).getParam1(), "interceptor1_value1");
        assertEquals(((MockInterceptor1) interceptorMappings.get(0).getInterceptor()).getParam2(), "interceptor1_value2");

        assertEquals(interceptorMappings.get(1).getName(), "interceptor2");
        assertNotNull(interceptorMappings.get(1).getInterceptor());
        assertEquals(interceptorMappings.get(1).getInterceptor().getClass(), MockInterceptor2.class);
        assertEquals(((MockInterceptor2) interceptorMappings.get(1).getInterceptor()).getParam1(), "interceptor2_value1");
        assertEquals(((MockInterceptor2) interceptorMappings.get(1).getInterceptor()).getParam2(), "interceptor2_value2");
    }

    public static class MockInterceptor1 extends AbstractInterceptor {
        private String param1;
        private String param2;

        public void setParam1(String param1) {
            this.param1 = param1;
        }

        public String getParam1() {
            return this.param1;
        }

        public void setParam2(String param2) {
            this.param2 = param2;
        }

        public String getParam2() {
            return this.param2;
        }

        public String intercept(ActionInvocation invocation) throws Exception {
            return invocation.invoke();
        }
    }

    public static class MockInterceptor2 extends AbstractInterceptor {
        private String param1;
        private String param2;

        public void setParam1(String param1) {
            this.param1 = param1;
        }

        public String getParam1() {
            return this.param1;
        }

        public void setParam2(String param2) {
            this.param2 = param2;
        }

        public String getParam2() {
            return this.param2;
        }

        public String intercept(ActionInvocation invocation) throws Exception {
            return invocation.invoke();
        }
    }

}
