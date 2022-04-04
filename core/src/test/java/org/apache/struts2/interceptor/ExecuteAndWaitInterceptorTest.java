/*
 * $Id$
 *
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

package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.interceptor.ParametersInterceptor;
import com.opensymphony.xwork2.mock.MockResult;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.views.jsp.StrutsMockHttpServletRequest;
import org.apache.struts2.views.jsp.StrutsMockHttpSession;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Test case for ExecuteAndWaitInterceptor.
 */
public class ExecuteAndWaitInterceptorTest extends StrutsInternalTestCase {

    private StrutsMockHttpServletRequest request;
    private HttpSession httpSession;
    private Map context;
    private Map params;
    private Map session;
    private ExecuteAndWaitInterceptor waitInterceptor;
    private ParametersInterceptor parametersInterceptor;

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
        assertTrue("Job done already after 500 so there should not be such long delay", diff <= 1000);
    }

    public void testWaitDelayAndJobAlreadyDone2() throws Exception {
        waitInterceptor.setDelay(1500);
        waitInterceptor.setDelaySleepInterval(200); // just takes a little longer to find out job is done

        ActionProxy proxy = buildProxy("action1");
        long before = System.currentTimeMillis();
        String result = proxy.execute();
        long diff = System.currentTimeMillis() - before;
        assertEquals("success", result);
        assertTrue("Job done already after 500 so there should not be such long delay", diff <= 1000);
    }

    protected ActionProxy buildProxy(String actionName) throws Exception {
        return actionProxyFactory.createActionProxy("", actionName, null, context);
    }

    protected void setUp() throws Exception {
        loadConfigurationProviders(new WaitConfigurationProvider());

        session = new HashMap();
        params = new HashMap();
        context = new HashMap();
        context.put(ActionContext.SESSION, session);
        context.put(ActionContext.PARAMETERS, params);

        request = new StrutsMockHttpServletRequest();
        httpSession = new StrutsMockHttpSession();
        request.setSession(httpSession);
        request.setParameterMap(params);
        context.put(ServletActionContext.HTTP_REQUEST, request);
        container.inject(parametersInterceptor);
    }

    protected void tearDown() throws Exception {
        configurationManager.clearContainerProviders();
        configurationManager.destroyConfiguration();
        ActionContext.setContext(null);
    }

    private class WaitConfigurationProvider implements ConfigurationProvider {

        Configuration configuration;
        public void destroy() {
            waitInterceptor.destroy();
        }

        public boolean needsReload() {
            return false;
        }

        public void init(Configuration configuration) throws ConfigurationException {
            this.configuration = configuration;
        }

        public void loadPackages() throws ConfigurationException {


            // interceptors
            waitInterceptor = new ExecuteAndWaitInterceptor();
            parametersInterceptor = new ParametersInterceptor();
            PackageConfig wait = new PackageConfig.Builder("")
                .addActionConfig("action1", new ActionConfig.Builder("", "action1", ExecuteAndWaitDelayAction.class.getName())
                    .addResultConfig(new ResultConfig.Builder(Action.SUCCESS, MockResult.class.getName()).build())
                    .addResultConfig(new ResultConfig.Builder(ExecuteAndWaitInterceptor.WAIT, MockResult.class.getName()).build())
                    .addInterceptor(new InterceptorMapping("params", parametersInterceptor))
                    .addInterceptor(new InterceptorMapping("execAndWait", waitInterceptor))
                .build())
            .build();
            configuration.addPackageConfig("", wait);
        }

        public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
            builder.factory(ObjectFactory.class);
            builder.factory(ActionProxyFactory.class, DefaultActionProxyFactory.class);
            builder.factory(OgnlUtil.class, OgnlUtil.class);
        }

    }
}

