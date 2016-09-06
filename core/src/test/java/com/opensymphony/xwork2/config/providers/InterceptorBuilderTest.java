package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.InterceptorStackConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.interceptor.Interceptor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * <code>InterceptorBuilderTest</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id$
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
     *
     * @throws Exception
     */
    public void testBuildInterceptor_1() throws Exception {
        InterceptorStackConfig interceptorStackConfig1 = new InterceptorStackConfig.Builder("interceptorStack1").build();

        InterceptorConfig interceptorConfig1 = new InterceptorConfig.Builder("interceptor1", "com.opensymphony.xwork2.config.providers.InterceptorBuilderTest$MockInterceptor1").build();

        InterceptorConfig interceptorConfig2 = new InterceptorConfig.Builder("interceptor2", "com.opensymphony.xwork2.config.providers.InterceptorBuilderTest$MockInterceptor2").build();


        PackageConfig packageConfig = new PackageConfig.Builder("package1").namespace("/namespace").addInterceptorConfig(interceptorConfig1).addInterceptorConfig(interceptorConfig2).addInterceptorStackConfig(interceptorStackConfig1).build();

        List
                interceptorMappings =
                InterceptorBuilder.constructInterceptorReference(packageConfig, "interceptorStack1",
                        new LinkedHashMap<String, String>() {
                            private static final long serialVersionUID = -1358620486812957895L;

                            {
                                put("interceptor1.param1", "interceptor1_value1");
                                put("interceptor1.param2", "interceptor1_value2");
                                put("interceptor2.param1", "interceptor2_value1");
                                put("interceptor2.param2", "interceptor2_value2");
                            }
                        },null,  objectFactory);

        assertEquals(interceptorMappings.size(), 2);

        assertEquals(((InterceptorMapping) interceptorMappings.get(0)).getName(), "interceptor1");
        assertNotNull(((InterceptorMapping) interceptorMappings.get(0)).getInterceptor());
        assertEquals(((InterceptorMapping) interceptorMappings.get(0)).getInterceptor().getClass(), MockInterceptor1.class);
        assertEquals(((MockInterceptor1) ((InterceptorMapping) interceptorMappings.get(0)).getInterceptor()).getParam1(), "interceptor1_value1");
        assertEquals(((MockInterceptor1) ((InterceptorMapping) interceptorMappings.get(0)).getInterceptor()).getParam2(), "interceptor1_value2");

        assertEquals(((InterceptorMapping) interceptorMappings.get(1)).getName(), "interceptor2");
        assertNotNull(((InterceptorMapping) interceptorMappings.get(1)).getInterceptor());
        assertEquals(((InterceptorMapping) interceptorMappings.get(1)).getInterceptor().getClass(), MockInterceptor2.class);
        assertEquals(((MockInterceptor2) ((InterceptorMapping) interceptorMappings.get(1)).getInterceptor()).getParam1(), "interceptor2_value1");
        assertEquals(((MockInterceptor2) ((InterceptorMapping) interceptorMappings.get(1)).getInterceptor()).getParam2(), "interceptor2_value2");
    }

    public void testMultipleSameInterceptors() throws Exception {
        InterceptorConfig interceptorConfig1 = new InterceptorConfig.Builder("interceptor1", "com.opensymphony.xwork2.config.providers.InterceptorBuilderTest$MockInterceptor1").build();
        InterceptorConfig interceptorConfig2 = new InterceptorConfig.Builder("interceptor2", "com.opensymphony.xwork2.config.providers.InterceptorBuilderTest$MockInterceptor2").build();

        InterceptorStackConfig interceptorStackConfig1 = new InterceptorStackConfig.Builder("multiStack")
                .addInterceptor(new InterceptorMapping(interceptorConfig1.getName(), objectFactory.buildInterceptor(interceptorConfig1, Collections.<String, String>emptyMap())))
                .addInterceptor(new InterceptorMapping(interceptorConfig2.getName(), objectFactory.buildInterceptor(interceptorConfig2, Collections.<String, String>emptyMap())))
                .addInterceptor(new InterceptorMapping(interceptorConfig1.getName(), objectFactory.buildInterceptor(interceptorConfig1, Collections.<String, String>emptyMap())))
                .build();

        PackageConfig packageConfig = new PackageConfig.Builder("package1")
                .namespace("/namespace")
                .addInterceptorConfig(interceptorConfig1)
                .addInterceptorConfig(interceptorConfig2)
                .addInterceptorConfig(interceptorConfig1)
                .addInterceptorStackConfig(interceptorStackConfig1)
                .build();

        List interceptorMappings =  InterceptorBuilder.constructInterceptorReference(packageConfig, "multiStack",
                        new LinkedHashMap<String, String>() {
                            {
                                put("interceptor1.param1", "interceptor1_value1");
                                put("interceptor1.param2", "interceptor1_value2");
                            }
                        }, null,  objectFactory);

        assertEquals(interceptorMappings.size(), 3);

        assertEquals(((InterceptorMapping) interceptorMappings.get(0)).getName(), "interceptor1");
        assertNotNull(((InterceptorMapping) interceptorMappings.get(0)).getInterceptor());
        assertEquals(((InterceptorMapping) interceptorMappings.get(0)).getInterceptor().getClass(), MockInterceptor1.class);
        assertEquals(((MockInterceptor1) ((InterceptorMapping) interceptorMappings.get(0)).getInterceptor()).getParam1(), "interceptor1_value1");
        assertEquals(((MockInterceptor1) ((InterceptorMapping) interceptorMappings.get(0)).getInterceptor()).getParam2(), "interceptor1_value2");

        assertEquals(((InterceptorMapping) interceptorMappings.get(1)).getName(), "interceptor2");
        assertNotNull(((InterceptorMapping) interceptorMappings.get(1)).getInterceptor());
        assertEquals(((InterceptorMapping) interceptorMappings.get(1)).getInterceptor().getClass(), MockInterceptor2.class);

        assertEquals(((InterceptorMapping) interceptorMappings.get(2)).getName(), "interceptor1");
        assertNotNull(((InterceptorMapping) interceptorMappings.get(2)).getInterceptor());
        assertEquals(((InterceptorMapping) interceptorMappings.get(2)).getInterceptor().getClass(), MockInterceptor1.class);
        assertEquals(((MockInterceptor1) ((InterceptorMapping) interceptorMappings.get(2)).getInterceptor()).getParam1(), "interceptor1_value1");
        assertEquals(((MockInterceptor1) ((InterceptorMapping) interceptorMappings.get(2)).getInterceptor()).getParam2(), "interceptor1_value2");
    }

    /**
     * Try to test this
     * <interceptor-ref name="interceptorStack1">
     * <param name="interceptorStack2.interceptor1.param1">interceptor1_value1</param>
     * <param name="interceptorStack2.interceptor1.param2">interceptor1_value2</param>
     * <param name="interceptorStack3.interceptor2.param1">interceptor2_value1</param>
     * <param name="interceptorStack3.interceptor2.param2">interceptor2_value2</param>
     * </interceptor-ref>
     *
     * @throws Exception
     */
    public void testBuildInterceptor_2() throws Exception {
        InterceptorStackConfig interceptorStackConfig1 = new InterceptorStackConfig.Builder("interceptorStack1").build();

        InterceptorStackConfig interceptorStackConfig2 = new InterceptorStackConfig.Builder("interceptorStack2").build();

        InterceptorStackConfig interceptorStackConfig3 = new InterceptorStackConfig.Builder("interceptorStack3").build();

        InterceptorConfig interceptorConfig1 = new InterceptorConfig.Builder("interceptor1", "com.opensymphony.xwork2.config.providers.InterceptorBuilderTest$MockInterceptor1").build();

        InterceptorConfig interceptorConfig2 = new InterceptorConfig.Builder("interceptor2", "com.opensymphony.xwork2.config.providers.InterceptorBuilderTest$MockInterceptor2").build();


        PackageConfig packageConfig = new PackageConfig.Builder("package1").namespace("/namspace").
                addInterceptorConfig(interceptorConfig1).
                addInterceptorConfig(interceptorConfig2).
                addInterceptorStackConfig(interceptorStackConfig1).
                addInterceptorStackConfig(interceptorStackConfig2).
                addInterceptorStackConfig(interceptorStackConfig3).build();

        List interceptorMappings = InterceptorBuilder.constructInterceptorReference(packageConfig, "interceptorStack1",
                new LinkedHashMap<String, String>() {
                    private static final long serialVersionUID = -5819935102242042570L;

                    {
                        put("interceptorStack2.interceptor1.param1", "interceptor1_value1");
                        put("interceptorStack2.interceptor1.param2", "interceptor1_value2");
                        put("interceptorStack3.interceptor2.param1", "interceptor2_value1");
                        put("interceptorStack3.interceptor2.param2", "interceptor2_value2");
                    }
                }, null, objectFactory);

        assertEquals(interceptorMappings.size(), 2);

        assertEquals(((InterceptorMapping) interceptorMappings.get(0)).getName(), "interceptor1");
        assertNotNull(((InterceptorMapping) interceptorMappings.get(0)).getInterceptor());
        assertEquals(((InterceptorMapping) interceptorMappings.get(0)).getInterceptor().getClass(), MockInterceptor1.class);
        assertEquals(((MockInterceptor1) ((InterceptorMapping) interceptorMappings.get(0)).getInterceptor()).getParam1(), "interceptor1_value1");
        assertEquals(((MockInterceptor1) ((InterceptorMapping) interceptorMappings.get(0)).getInterceptor()).getParam2(), "interceptor1_value2");

        assertEquals(((InterceptorMapping) interceptorMappings.get(1)).getName(), "interceptor2");
        assertNotNull(((InterceptorMapping) interceptorMappings.get(1)).getInterceptor());
        assertEquals(((InterceptorMapping) interceptorMappings.get(1)).getInterceptor().getClass(), MockInterceptor2.class);
        assertEquals(((MockInterceptor2) ((InterceptorMapping) interceptorMappings.get(1)).getInterceptor()).getParam1(), "interceptor2_value1");
        assertEquals(((MockInterceptor2) ((InterceptorMapping) interceptorMappings.get(1)).getInterceptor()).getParam2(), "interceptor2_value2");
    }

    /**
     * Try to test this
     * <interceptor-ref name="interceptorStack1">
     * <param name="interceptorStack2.interceptorStack3.interceptorStack4.interceptor1.param1">interceptor1_value1</param>
     * <param name="interceptorStack2.interceptorStack3.interceptorStack4.interceptor1.param2">interceptor1_value2</param>
     * <param name="interceptorStack5.interceptor2.param1">interceptor2_value1</param>
     * <param name="interceptorStack5.interceptor2.param2">interceptor2_value2</param>
     * </interceptor-ref>
     *
     * @throws Exception
     */
    public void testBuildInterceptor_3() throws Exception {
        InterceptorConfig interceptorConfig1 = new InterceptorConfig.Builder("interceptor1", "com.opensymphony.xwork2.config.providers.InterceptorBuilderTest$MockInterceptor1").build();

        InterceptorConfig interceptorConfig2 = new InterceptorConfig.Builder("interceptor2", "com.opensymphony.xwork2.config.providers.InterceptorBuilderTest$MockInterceptor2").build();


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


        List interceptorMappings = InterceptorBuilder.constructInterceptorReference(
                packageConfig, "interceptorStack1",
                new LinkedHashMap<String, String>() {
                    private static final long serialVersionUID = 4675809753780875525L;

                    {
                        put("interceptorStack2.interceptorStack3.interceptorStack4.interceptor1.param1", "interceptor1_value1");
                        put("interceptorStack2.interceptorStack3.interceptorStack4.interceptor1.param2", "interceptor1_value2");
                        put("interceptorStack5.interceptor2.param1", "interceptor2_value1");
                        put("interceptorStack5.interceptor2.param2", "interceptor2_value2");
                    }
                }, null, objectFactory);

        assertEquals(interceptorMappings.size(), 2);

        assertEquals(((InterceptorMapping) interceptorMappings.get(0)).getName(), "interceptor1");
        assertNotNull(((InterceptorMapping) interceptorMappings.get(0)).getInterceptor());
        assertEquals(((InterceptorMapping) interceptorMappings.get(0)).getInterceptor().getClass(), MockInterceptor1.class);
        assertEquals(((MockInterceptor1) ((InterceptorMapping) interceptorMappings.get(0)).getInterceptor()).getParam1(), "interceptor1_value1");
        assertEquals(((MockInterceptor1) ((InterceptorMapping) interceptorMappings.get(0)).getInterceptor()).getParam2(), "interceptor1_value2");

        assertEquals(((InterceptorMapping) interceptorMappings.get(1)).getName(), "interceptor2");
        assertNotNull(((InterceptorMapping) interceptorMappings.get(1)).getInterceptor());
        assertEquals(((InterceptorMapping) interceptorMappings.get(1)).getInterceptor().getClass(), MockInterceptor2.class);
        assertEquals(((MockInterceptor2) ((InterceptorMapping) interceptorMappings.get(1)).getInterceptor()).getParam1(), "interceptor2_value1");
        assertEquals(((MockInterceptor2) ((InterceptorMapping) interceptorMappings.get(1)).getInterceptor()).getParam2(), "interceptor2_value2");
    }


    public static class MockInterceptor1 implements Interceptor {
        private static final long serialVersionUID = 2939902550126175874L;
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

        public void destroy() {
        }

        public void init() {
        }

        public String intercept(ActionInvocation invocation) throws Exception {
            return invocation.invoke();
        }
    }

    public static class MockInterceptor2 implements Interceptor {
        private static final long serialVersionUID = 267427973306989618L;
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

        public void destroy() {
        }

        public void init() {
        }

        public String intercept(ActionInvocation invocation) throws Exception {
            return invocation.invoke();
        }
    }

}
