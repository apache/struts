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
package com.opensymphony.xwork2.spring.interceptor;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Simon Stewart
 */
public class ActionAutowiringInterceptorTest extends XWorkTestCase {

    public void testShouldAutowireAction() throws Exception {
        StaticWebApplicationContext context = new StaticWebApplicationContext();
        context.getBeanFactory().registerSingleton("bean", new TestBean());
        TestBean bean = (TestBean) context.getBean("bean");

        loadSpringApplicationContextIntoApplication(context);

        SimpleAction action = new SimpleAction();
        ActionInvocation invocation = new TestActionInvocation(action);

        ActionAutowiringInterceptor interceptor = new ActionAutowiringInterceptor();
        interceptor.setApplicationContext(context);
        interceptor.init();

        interceptor.intercept(invocation);

        assertEquals(bean, action.getBean());
    }

    public void testSetAutowireType() throws Exception {
        XmlConfigurationProvider prov = new XmlConfigurationProvider("xwork-default.xml");
        container.inject(prov);
        prov.setThrowExceptionOnDuplicateBeans(false);
        XmlConfigurationProvider c = new XmlConfigurationProvider("com/opensymphony/xwork2/spring/xwork-autowire.xml");
        container.inject(c);
        loadConfigurationProviders(c, prov);

        StaticWebApplicationContext appContext = new StaticWebApplicationContext();

        loadSpringApplicationContextIntoApplication(appContext);

        ActionAutowiringInterceptor interceptor = new ActionAutowiringInterceptor();
        interceptor.init();

        SimpleAction action = new SimpleAction();
        ActionInvocation invocation = new TestActionInvocation(action);

        interceptor.intercept(invocation);

        ApplicationContext loadedContext = interceptor.getApplicationContext();

        assertEquals(appContext, loadedContext);
    }

    protected void loadSpringApplicationContextIntoApplication(ApplicationContext appContext) {
        Map<Object, Object> application = new HashMap<>();
        application.put(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, appContext);

        Map<String, Object> context = new HashMap<>();
        context.put(ActionContext.APPLICATION, application);
        ActionContext actionContext = new ActionContext(context);
        ActionContext.setContext(actionContext);
    }

    public void testLoadsApplicationContextUsingWebApplicationContextUtils() throws Exception {
        StaticWebApplicationContext appContext = new StaticWebApplicationContext();

        loadSpringApplicationContextIntoApplication(appContext);

        ActionAutowiringInterceptor interceptor = new ActionAutowiringInterceptor();
        interceptor.init();

        SimpleAction action = new SimpleAction();
        ActionInvocation invocation = new TestActionInvocation(action);

        interceptor.intercept(invocation);

        ApplicationContext loadedContext = interceptor.getApplicationContext();

        assertEquals(appContext, loadedContext);
    }

    public void testIfApplicationContextIsNullThenBeanWillNotBeWiredUp() throws Exception {
        Map<String, Object> context = new HashMap<>();
        context.put(ActionContext.APPLICATION, new HashMap());
        ActionContext actionContext = new ActionContext(context);
        ActionContext.setContext(actionContext);

        ActionAutowiringInterceptor interceptor = new ActionAutowiringInterceptor();
        interceptor.init();

        SimpleAction action = new SimpleAction();
        ActionInvocation invocation = new TestActionInvocation(action);
        TestBean bean = action.getBean();

        // If an exception is thrown here, things are going to go wrong in
        // production
        interceptor.intercept(invocation);

        assertEquals(bean, action.getBean());
    }

}
