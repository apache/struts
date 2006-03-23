/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
*/

package com.opensymphony.webwork.interceptor;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.WebWorkTestCase;
import com.opensymphony.webwork.views.jsp.WebWorkMockHttpServletRequest;
import com.opensymphony.webwork.views.jsp.WebWorkMockHttpSession;
import com.opensymphony.xwork.*;
import com.opensymphony.xwork.mock.MockResult;
import com.opensymphony.xwork.config.Configuration;
import com.opensymphony.xwork.config.ConfigurationException;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.ConfigurationProvider;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;
import com.opensymphony.xwork.config.entities.ResultConfig;
import com.opensymphony.xwork.config.entities.InterceptorMapping;
import com.opensymphony.xwork.interceptor.ParametersInterceptor;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test case for ExecuteAndWaitInterceptor.
 *
 * @author Claus Ibsen
 */
public class ExecuteAndWaitInterceptorTest extends WebWorkTestCase {

    private WebWorkMockHttpServletRequest request;
    private HttpSession httpSession;
    private Map context;
    private Map params;
    private Map session;
    private ExecuteAndWaitInterceptor waitInterceptor;

    public void testOneWait() throws Exception {
        waitInterceptor.setDelay(0);
        waitInterceptor.setDelaySleepInterval(0);

        ActionProxy proxy = buildProxy("action1");
        String result = proxy.execute();
        assertEquals("wait", result);

        Thread.sleep(1000);

        ActionProxy proxy2 = buildProxy("action1");
        String result2 = proxy2.execute();
        assertEquals("success", result2);
    }

    public void testTwoWait() throws Exception {
        waitInterceptor.setDelay(0);
        waitInterceptor.setDelaySleepInterval(0);

        ActionProxy proxy = buildProxy("action1");
        String result = proxy.execute();
        assertEquals("wait", result);

        Thread.sleep(300);

        ActionProxy proxy2 = buildProxy("action1");
        String result2 = proxy2.execute();
        assertEquals("wait", result2);

        Thread.sleep(300);

        ActionProxy proxy3 = buildProxy("action1");
        String result3 = proxy3.execute();
        assertEquals("success", result3);
    }

    public void testOneWaitWithDelay() throws Exception {
        waitInterceptor.setDelay(200);
        waitInterceptor.setDelaySleepInterval(100);

        ActionProxy proxy = buildProxy("action1");
        long before = System.currentTimeMillis();
        String result = proxy.execute();
        long after = System.currentTimeMillis();
        assertEquals("wait", result);
        assertTrue("delay should be ca. 200 millis", (after - before) >= 190);

        Thread.sleep(400);

        ActionProxy proxy2 = buildProxy("action1");
        String result2 = proxy2.execute();
        assertEquals("success", result2);
    }

    public void testTwoWaitWithDelay() throws Exception {
        waitInterceptor.setDelay(100);
        waitInterceptor.setDelaySleepInterval(100);

        ActionProxy proxy = buildProxy("action1");
        long before = System.currentTimeMillis();
        String result = proxy.execute();
        long after = System.currentTimeMillis();
        assertEquals("wait", result);
        assertTrue("delay should be ca. 100 millis", (after - before) >= 90);

        Thread.sleep(100);

        ActionProxy proxy2 = buildProxy("action1");
        long before2 = System.currentTimeMillis();
        String result2 = proxy2.execute();
        long after2 = System.currentTimeMillis();
        assertEquals("wait", result2);
        assertTrue("there should be no delay", (after2 - before2) < 110);

        Thread.sleep(400);

        ActionProxy proxy3 = buildProxy("action1");
        String result3 = proxy3.execute();
        assertEquals("success", result3);
    }

    public void testWaitDelayAndJobAlreadyDone() throws Exception {
        waitInterceptor.setDelay(1500);
        waitInterceptor.setDelaySleepInterval(100);

        ActionProxy proxy = buildProxy("action1");
        long before = System.currentTimeMillis();
        String result = proxy.execute();
        long diff = System.currentTimeMillis() - before;
        assertEquals("success", result);
        assertTrue("Job done already after 500 so there should not be such long delay", diff <= 750);
    }

    public void testWaitDelayAndJobAlreadyDone2() throws Exception {
        waitInterceptor.setDelay(1500);
        waitInterceptor.setDelaySleepInterval(200); // just takes a little longer to find out job is done

        ActionProxy proxy = buildProxy("action1");
        long before = System.currentTimeMillis();
        String result = proxy.execute();
        long diff = System.currentTimeMillis() - before;
        assertEquals("success", result);
        assertTrue("Job done already after 500 so there should not be such long delay", diff <= 750);
    }

    protected ActionProxy buildProxy(String actionName) throws Exception {
        return ActionProxyFactory.getFactory().createActionProxy("", actionName, context);
    }

    protected void setUp() throws Exception {
        ConfigurationManager.clearConfigurationProviders();
        ConfigurationManager.addConfigurationProvider(new WaitConfigurationProvider());
        ConfigurationManager.getConfiguration().reload();

        session = new HashMap();
        params = new HashMap();
        context = new HashMap();
        context.put(ActionContext.SESSION, session);
        context.put(ActionContext.PARAMETERS, params);

        request = new WebWorkMockHttpServletRequest();
        httpSession = new WebWorkMockHttpSession();
        request.setSession(httpSession);
        request.setParameterMap(params);
        context.put(ServletActionContext.HTTP_REQUEST, request);
    }

    protected void tearDown() throws Exception {
        ConfigurationManager.clearConfigurationProviders();
        ConfigurationManager.destroyConfiguration();
        ActionContext.setContext(null);
    }

    private class WaitConfigurationProvider implements ConfigurationProvider {

        public void destroy() {
            waitInterceptor.destroy();
        }

        public boolean needsReload() {
            return false;
        }

        public void init(Configuration configuration) throws ConfigurationException {
            PackageConfig wait = new PackageConfig();

            Map results = new HashMap();
            results.put(Action.SUCCESS, new ResultConfig(Action.SUCCESS, MockResult.class, null));
            results.put(ExecuteAndWaitInterceptor.WAIT, new ResultConfig(ExecuteAndWaitInterceptor.WAIT, MockResult.class, null));

            // interceptors
            waitInterceptor = new ExecuteAndWaitInterceptor();
            List interceptors = new ArrayList();
            interceptors.add(new InterceptorMapping("params", new ParametersInterceptor()));
            interceptors.add(new InterceptorMapping("execAndWait", waitInterceptor));

            ActionConfig ac = new ActionConfig(null, ExecuteAndWaitDelayAction.class, null, results, interceptors);
            wait.addActionConfig("action1", ac);

            configuration.addPackageConfig("wait", wait);
        }

    }
}

