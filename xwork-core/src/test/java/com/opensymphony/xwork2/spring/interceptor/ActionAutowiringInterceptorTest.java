/*
 * Created on 6/11/2004
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
        prov.setThrowExceptionOnDuplicateBeans(false);
        XmlConfigurationProvider c = new XmlConfigurationProvider("com/opensymphony/xwork2/spring/xwork-autowire.xml");
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
        Map<Object, Object> application = new HashMap<Object, Object>();
        application.put(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, appContext);

        Map<String, Object> context = new HashMap<String, Object>();
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
        Map<String, Object> context = new HashMap<String, Object>();
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
