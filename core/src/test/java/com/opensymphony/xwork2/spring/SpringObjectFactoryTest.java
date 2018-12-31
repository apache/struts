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
package com.opensymphony.xwork2.spring;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.interceptor.ModelDrivenInterceptor;
import com.opensymphony.xwork2.mock.DummyTextProvider;
import com.opensymphony.xwork2.test.StubConfigurationProvider;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.validator.Validator;
import com.opensymphony.xwork2.validator.validators.ExpressionValidator;
import com.opensymphony.xwork2.validator.validators.RequiredStringValidator;
import org.apache.struts2.interceptor.NoOpInterceptor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.aop.interceptor.DebugInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.StaticApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class SpringObjectFactoryTest extends XWorkTestCase {

    private StaticApplicationContext sac;
    private SpringObjectFactory objectFactory;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        sac = new StaticApplicationContext();
        loadConfigurationProviders(new StubConfigurationProvider() {

            @Override
            public void register(ContainerBuilder builder,
                    LocatableProperties props) throws ConfigurationException {

                // No registered beans during initialization; They will be registered in each test, runtime.
                props.setProperty("applicationContextPath", "com/opensymphony/xwork2/spring/emptyContext-spring.xml");

                builder.factory(TextProvider.class, DummyTextProvider.class);
                builder.factory(ObjectFactory.class, SpringObjectFactory.class);
            }
            
        });

        objectFactory = (SpringObjectFactory) container.getInstance(ObjectFactory.class);
        objectFactory.setApplicationContext(sac);
        objectFactory.setAlwaysRespectAutowireStrategy(false);
    }

    @Override
    public void tearDown() throws Exception {
        sac = null;
        objectFactory = null;
    }

    public void testFallsBackToDefaultObjectFactoryActionSearching() throws Exception {
        ActionConfig actionConfig = new ActionConfig.Builder("foo", "bar", ModelDrivenAction.class.getName()).build();

        Object action = objectFactory.buildBean(actionConfig.getClassName(), null);

        assertEquals(ModelDrivenAction.class, action.getClass());
    }

    public void testFallsBackToDefaultObjectFactoryInterceptorBuilding() throws Exception {
        InterceptorConfig iConfig = new InterceptorConfig.Builder("timer", ModelDrivenInterceptor.class.getName()).build();

        Interceptor interceptor = objectFactory.buildInterceptor(iConfig, new HashMap<String, String>());

        assertEquals(ModelDrivenInterceptor.class, interceptor.getClass());
    }

    public void testFallsBackToDefaultObjectFactoryResultBuilding() throws Exception {
        ResultConfig rConfig = new ResultConfig.Builder(Action.SUCCESS, ActionChainResult.class.getName()).build();
        Result result = objectFactory.buildResult(rConfig, ActionContext.getContext().getContextMap());

        assertEquals(ActionChainResult.class, result.getClass());
    }

    public void testFallsBackToDefaultObjectFactoryValidatorBuilding() throws Exception {
        Map<String, Object> extraContext = new HashMap<>();
        Validator validator = objectFactory.buildValidator(RequiredStringValidator.class.getName(), new HashMap<String, Object>(), extraContext);

        assertEquals(RequiredStringValidator.class, validator.getClass());
    }

    public void testObtainActionBySpringName() throws Exception {
        sac.registerPrototype("simple-action", SimpleAction.class, new MutablePropertyValues());

        ActionConfig actionConfig = new ActionConfig.Builder("fs", "jim", "simple-action").build();
        Object action = objectFactory.buildBean(actionConfig.getClassName(), null);

        assertEquals(SimpleAction.class, action.getClass());
    }

    public void testObtainInterceptorBySpringName() throws Exception {
        sac.registerSingleton("noop-interceptor", NoOpInterceptor.class, new MutablePropertyValues());

        InterceptorConfig iConfig = new InterceptorConfig.Builder("noop", "noop-interceptor").build();
        Interceptor interceptor = objectFactory.buildInterceptor(iConfig, new HashMap<String, String>());

        assertEquals(NoOpInterceptor.class, interceptor.getClass());
    }

    public void testObtainResultBySpringName() throws Exception {
        // TODO: Does this need to be a prototype?
        sac.registerPrototype("chaining-result", ActionChainResult.class, new MutablePropertyValues());

        ResultConfig rConfig = new ResultConfig.Builder(Action.SUCCESS, "chaining-result").build();
        Result result = objectFactory.buildResult(rConfig, ActionContext.getContext().getContextMap());

        assertEquals(ActionChainResult.class, result.getClass());
    }

    public void testObtainValidatorBySpringName() throws Exception {
        sac.registerPrototype("expression-validator", ExpressionValidator.class, new MutablePropertyValues());

        Map<String, Object> extraContext = new HashMap<>();
        Validator validator = objectFactory.buildValidator("expression-validator", new HashMap<String, Object>(), extraContext);

        assertEquals(ExpressionValidator.class, validator.getClass());
    }

    public void testShouldAutowireObjectsObtainedFromTheObjectFactoryByFullClassName() throws Exception {
        sac.getBeanFactory().registerSingleton("bean", new TestBean());
        TestBean bean = (TestBean) sac.getBean("bean");

        SimpleAction action = (SimpleAction) objectFactory.buildBean(SimpleAction.class.getName(), null);

        assertEquals(bean, action.getBean());
    }

    public void testShouldGiveReferenceToAppContextIfBeanIsApplicationContextAwareAndNotInstantiatedViaSpring() throws Exception {
        Foo foo = (Foo) objectFactory.buildBean(Foo.class.getName(), null);

        assertTrue("Expected app context to have been set", foo.isApplicationContextSet());
    }

    public static class Foo implements ApplicationContextAware {
        boolean applicationContextSet = false;

        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            applicationContextSet = true;
        }

        public boolean isApplicationContextSet() {
            return applicationContextSet;
        }
    }

    public void testShouldAutowireObjectsObtainedFromTheObjectFactoryByClass() throws Exception {
        sac.getBeanFactory().registerSingleton("bean", new TestBean());
        TestBean bean = (TestBean) sac.getBean("bean");

        SimpleAction action = (SimpleAction) objectFactory.buildBean(SimpleAction.class, null);

        assertEquals(bean, action.getBean());
    }

    public void testShouldGiveReferenceToAppContextIfBeanIsLoadedByClassApplicationContextAwareAndNotInstantiatedViaSpring() throws Exception {
        Foo foo = (Foo) objectFactory.buildBean(Foo.class, null);

        assertTrue("Expected app context to have been set", foo.isApplicationContextSet());
    }

    public void testLookingUpAClassInstanceDelegatesToSpring() throws Exception {
        sac.registerPrototype("simple-action", SimpleAction.class, new MutablePropertyValues());

        Class clazz = objectFactory.getClassInstance("simple-action");

        assertNotNull("Nothing returned", clazz);
        assertEquals("Expected to have instance of SimpleAction returned", SimpleAction.class, clazz);
    }

    public void testLookingUpAClassInstanceFallsBackToTheDefaultObjectFactoryIfSpringBeanNotFound() throws Exception {
        Class clazz = objectFactory.getClassInstance(SimpleAction.class.getName());

        assertNotNull("Nothing returned", clazz);
        assertEquals("Expected to have instance of SimpleAction returned", SimpleAction.class, clazz);
    }

    public void testSetAutowireStrategy() throws Exception {
        assertEquals(objectFactory.getAutowireStrategy(), AutowireCapableBeanFactory.AUTOWIRE_BY_NAME);

        objectFactory.setAutowireStrategy(AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE);

        sac.getBeanFactory().registerSingleton("bean", new TestBean());
        TestBean bean = (TestBean) sac.getBean("bean");

        sac.registerPrototype("simple-action", SimpleAction.class, new MutablePropertyValues());

        ActionConfig actionConfig = new ActionConfig.Builder("jim", "bob", "simple-action").build();
        SimpleAction simpleAction = (SimpleAction) objectFactory.buildBean(actionConfig.getClassName(), null);
        objectFactory.autoWireBean(simpleAction);
        assertEquals(simpleAction.getBean(), bean);
    }

    public void testShouldUseConstructorBasedInjectionWhenCreatingABeanFromAClassName() throws Exception {
        SpringObjectFactory factory = objectFactory;
        objectFactory.setAlwaysRespectAutowireStrategy(false);
        sac.registerSingleton("actionBean", SimpleAction.class, new MutablePropertyValues());

        ConstructorBean bean = (ConstructorBean) factory.buildBean(ConstructorBean.class, null);

        assertNotNull("Bean should not be null", bean);
        assertNotNull("Action should have been added via DI", bean.getAction());
    }

    public void testShouldUseAutowireStrategyWhenCreatingABeanFromAClassName_constructor() throws Exception {
        objectFactory.setAlwaysRespectAutowireStrategy(true);
        objectFactory.setAutowireStrategy(AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR);
        sac.registerSingleton("actionBean", SimpleAction.class, new MutablePropertyValues());

        ConstructorBean bean = (ConstructorBean) objectFactory.buildBean(ConstructorBean.class, null);

        assertNotNull("Bean should not be null", bean);
        assertNotNull("Action should have been added via DI", bean.getAction());
    }

    public void testShouldUseAutowireStrategyWhenCreatingABeanFromAClassName_setterByType() throws Exception {
        objectFactory.setAlwaysRespectAutowireStrategy(true);

        objectFactory.setAutowireStrategy(AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE);
        sac.registerSingleton("actionBean", SimpleAction.class, new MutablePropertyValues());

        SetterByTypeBean bean = (SetterByTypeBean) objectFactory.buildBean(SetterByTypeBean.class, null);

        assertNotNull("Bean should not be null", bean);
        assertNotNull("Action should have been added via DI", bean.getAction());
    }

    public void testShouldUseAutowireStrategyWhenCreatingABeanFromAClassName_setterByName() throws Exception {
        objectFactory.setAlwaysRespectAutowireStrategy(true);

        objectFactory.setAutowireStrategy(AutowireCapableBeanFactory.AUTOWIRE_BY_NAME);
        sac.registerSingleton("actionBean", SimpleAction.class, new MutablePropertyValues());

        SetterByNameBean bean = (SetterByNameBean) objectFactory.buildBean(SetterByNameBean.class, null);

        assertNotNull("Bean should not be null", bean);
        assertNotNull("Action should have been added via DI", bean.getActionBean());
    }

    public void testFallBackToDefaultObjectFactoryWhenTheCConstructorDIIsAmbiguous() throws Exception {
        objectFactory.setAlwaysRespectAutowireStrategy(true);
        sac.registerSingleton("firstActionBean", SimpleAction.class, new MutablePropertyValues());
        sac.registerSingleton("secondActionBean", SimpleAction.class, new MutablePropertyValues());

        ConstructorBean bean = (ConstructorBean) objectFactory.buildBean(ConstructorBean.class, null);

        assertNotNull("Bean should have been created using default constructor", bean);
        assertNull("Not expecting this to have been set", bean.getAction());
    }

    public void testObjectFactoryUsesSpringObjectFactoryToCreateActions() throws Exception {
        sac.registerSingleton("actionBean", SimpleAction.class, new MutablePropertyValues());
        ActionConfig actionConfig = new ActionConfig.Builder("as", "as", ConstructorAction.class.getName()).build();

        ConstructorAction action = (ConstructorAction) objectFactory.buildBean(actionConfig.getClassName(), null);

        assertNotNull("Bean should not be null", action);
        assertNotNull("Action should have been added via DI", action.getAction());
    }

    public void testShouldUseApplicationContextToApplyAspectsToGeneratedBeans() throws Exception {
        sac.registerSingleton("debugInterceptor", DebugInterceptor.class, new MutablePropertyValues());

        MutablePropertyValues values = new MutablePropertyValues();
        values.addPropertyValue("beanNames", new String[]{"*Action"});
        values.addPropertyValue("interceptorNames", new String[]{"debugInterceptor"});
        sac.registerSingleton("proxyFactory", BeanNameAutoProxyCreator.class, values);

        sac.refresh();

        ActionConfig actionConfig = new ActionConfig.Builder("", "", SimpleAction.class.getName()).build();
        Action action = (Action) objectFactory.buildBean(actionConfig.getClassName(), null);

        assertNotNull("Bean should not be null", action);
        System.out.println("Action class is: " + action.getClass().getName());
        assertTrue("Action should have been advised", action instanceof Advised);
    }

    public void testInjectingInternalStrutsBeans() throws Exception {
        sac.registerPrototype("injectable", InternalBeansInjectable.class, new MutablePropertyValues());

        InternalBeansInjectable action = (InternalBeansInjectable) objectFactory.buildBean("injectable", null, true);

        assertNotNull("TextProvider should be injected by internal DI", action.getTextProvider());
    }

    public static class InternalBeansInjectable {

        private TextProvider textProvider;

        public InternalBeansInjectable() {
        }

        public TextProvider getTextProvider() {
            return textProvider;
        }

        @Inject
        public void setTextProvider(TextProvider textProvider) {
            this.textProvider = textProvider;
        }
    }

    public static class ConstructorBean {
        private SimpleAction action;

        public ConstructorBean() {
            // Empty constructor
        }

        public ConstructorBean(SimpleAction action) {
            this.action = action;
        }

        public SimpleAction getAction() {
            return action;
        }
    }

    public static class SetterByNameBean {
        private SimpleAction action;

        public SetterByNameBean() {
            // Empty constructor
        }

        public SimpleAction getActionBean() {
            return action;
        }

        public void setActionBean(SimpleAction action) {
            this.action = action;
        }
    }

    public static class SetterByTypeBean {
        private SimpleAction action;

        public SetterByTypeBean() {
            // Empty constructor
        }

        public SimpleAction getAction() {
            return action;
        }

        public void setAction(SimpleAction action) {
            this.action = action;
        }
    }

    public static class ConstructorAction implements Action {
        private SimpleAction action;

        public ConstructorAction(SimpleAction action) {
            this.action = action;
        }

        public String execute() throws Exception {
            return SUCCESS;
        }

        public SimpleAction getAction() {
            return action;
        }
    }
}
