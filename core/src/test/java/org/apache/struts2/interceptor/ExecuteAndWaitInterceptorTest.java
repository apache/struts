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
package org.apache.struts2.interceptor;

import org.apache.struts2.action.Action;
import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionProxy;
import org.apache.struts2.ActionProxyFactory;
import org.apache.struts2.DefaultActionProxyFactory;
import org.apache.struts2.ObjectFactory;
import org.apache.struts2.config.Configuration;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.config.ConfigurationProvider;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.config.entities.InterceptorMapping;
import org.apache.struts2.config.entities.PackageConfig;
import org.apache.struts2.config.entities.ResultConfig;
import org.apache.struts2.inject.ContainerBuilder;
import org.apache.struts2.mock.MockResult;
import org.apache.struts2.ognl.OgnlUtil;
import org.apache.struts2.util.TokenHelper;
import org.apache.struts2.util.location.LocatableProperties;
import jakarta.servlet.http.HttpSession;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.exec.BackgroundProcess;
import org.apache.struts2.interceptor.exec.ExecutorProvider;
import org.apache.struts2.interceptor.exec.SandboxedSessionMap;
import org.apache.struts2.interceptor.parameter.ParametersInterceptor;
import org.apache.struts2.views.jsp.StrutsMockHttpServletRequest;
import org.apache.struts2.views.jsp.StrutsMockHttpSession;
import org.springframework.mock.web.MockHttpSession;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

/**
 * Test case for ExecuteAndWaitInterceptor.
 */
public class ExecuteAndWaitInterceptorTest extends StrutsInternalTestCase {

    private StrutsMockHttpServletRequest request;
    private HttpSession httpSession;
    private Map<String, Object> context;
    private Map<String, Object> params;
    private Map<String, Object> session;
    private ExecuteAndWaitInterceptor waitInterceptor;
    private ParametersInterceptor parametersInterceptor;
    private TokenInterceptor tokenInterceptor;
    private TokenSessionStoreInterceptor tokenSessionInterceptor;
    private ServletConfigInterceptor servletConfigInterceptor;

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

    public void testExecutorProvider() throws Exception {
        waitInterceptor.setExecutorProvider(new TestExecutorProvider());

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

        Thread.sleep(500);

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

    public void testFromDeserializedSession() throws Exception {
        waitInterceptor.setDelay(0);
        waitInterceptor.setDelaySleepInterval(0);

        ActionProxy proxy = buildProxy("action1");
        String result = proxy.execute();
        assertEquals("wait", result);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(session);//WW-4900 action1 and invocation are not serializable but we should not fail at this line
        oos.close();
        byte[] b = baos.toByteArray();
        baos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        ObjectInputStream ois = new ObjectInputStream(bais);
        session = (Map<String, Object>) ois.readObject();
        context = ActionContext.of(context).withSession(session).getContextMap();
        ois.close();
        bais.close();

        Thread.sleep(1000);

        ActionProxy proxy2 = buildProxy("action1");
        String result2 = proxy2.execute();
        assertEquals("wait", result2);//WW-4900 A new thread should be started when background thread missed

        Thread.sleep(1000);

        ActionProxy proxy3 = buildProxy("action1");
        String result3 = proxy3.execute();
        assertEquals("success", result3);
    }

    public void testWaitWithoutTokenCheckLeavesSessionTokensUnchanged() throws Exception {
        waitInterceptor.setDelay(0);
        waitInterceptor.setDelaySleepInterval(0);
        String customTokenSessionName = TokenHelper.buildTokenSessionAttributeName("custom");
        session.put(customTokenSessionName, "server-generated");

        for (String tokenName : new String[]{TokenHelper.DEFAULT_TOKEN_NAME, "custom"}) {
            Map<String, String[]> requestParams = new HashMap<>();
            requestParams.put(TokenHelper.TOKEN_NAME_FIELD, new String[]{tokenName});
            requestParams.put(TokenHelper.DEFAULT_TOKEN_NAME, new String[]{"request-supplied"});
            requestParams.put("custom", new String[]{"request-supplied"});
            withRequestParams(requestParams);

            assertEquals("wait", buildProxy("plainWait").execute());

            assertNull(session.get(TokenHelper.buildTokenSessionAttributeName(TokenHelper.DEFAULT_TOKEN_NAME)));
            assertEquals("server-generated", session.get(customTokenSessionName));
        }
    }

    public void testTokenAndWaitAcceptsWaitPageRefreshWithValidatedToken() throws Exception {
        assertWaitPageRefreshAccepted("tokenWait");
    }

    public void testTokenSessionAndWaitAcceptsWaitPageRefreshWithValidatedToken() throws Exception {
        assertWaitPageRefreshAccepted("tokenSessionWait");
    }

    private void assertWaitPageRefreshAccepted(String actionName) throws Exception {
        waitInterceptor.setDelay(0);
        waitInterceptor.setDelaySleepInterval(0);
        String tokenSessionName = TokenHelper.buildTokenSessionAttributeName(TokenHelper.DEFAULT_TOKEN_NAME);
        session.put(tokenSessionName, "server-generated");

        Map<String, String[]> requestParams = new HashMap<>();
        requestParams.put(TokenHelper.TOKEN_NAME_FIELD, new String[]{TokenHelper.DEFAULT_TOKEN_NAME});
        requestParams.put(TokenHelper.DEFAULT_TOKEN_NAME, new String[]{"server-generated"});
        withRequestParams(requestParams);

        assertEquals("wait", buildProxy(actionName).execute());
        assertEquals("server-generated", session.get(tokenSessionName));

        Thread.sleep(300);

        assertEquals("wait", buildProxy(actionName).execute());
        assertEquals("server-generated", session.get(tokenSessionName));

        Thread.sleep(500);

        assertEquals("success", buildProxy(actionName).execute());
        assertNull(session.get(tokenSessionName));
    }

    public void testBackgroundWritesReachTheRealSession() throws Exception {
        useLiveSession();

        assertEquals("wait", buildProxy("sessionAction").execute());
        awaitDone("sessionAction");

        assertEquals("success", buildProxy("sessionAction").execute());
        assertEquals("done", httpSession.getAttribute("result"));
        assertEquals("yes", httpSession.getAttribute("aware"));
    }

    public void testBackgroundWritesAreInvisibleUntilPolled() throws Exception {
        useLiveSession();

        assertEquals("wait", buildProxy("sessionAction").execute());
        BackgroundProcess process = awaitDone("sessionAction");

        assertNull(process.getException());
        assertEquals("done", process.getSessionSandbox().get("result"));
        assertNull(httpSession.getAttribute("result"));
        assertNull(httpSession.getAttribute("aware"));
    }

    public void testWritesOfAFailedActionAreApplied() throws Exception {
        useLiveSession();
        Map<String, String[]> requestParams = new HashMap<>();
        requestParams.put("failWith", new String[]{"boom"});
        withRequestParams(requestParams);

        assertEquals("wait", buildProxy("sessionAction").execute());
        awaitDone("sessionAction");

        ActionProxy poll = buildProxy("sessionAction");
        try {
            poll.execute();
            fail("the background failure should be rethrown by the polling request");
        } catch (IllegalStateException failure) {
            assertEquals("boom", failure.getMessage());
        }
        assertEquals("done", httpSession.getAttribute("result"));
    }

    public void testSessionAwareGetsTheLiveMapBackAfterWriteBack() throws Exception {
        useLiveSession();

        assertEquals("wait", buildProxy("sessionAction").execute());
        SessionWritingWaitAction action = (SessionWritingWaitAction) awaitDone("sessionAction").getAction();

        assertEquals("success", buildProxy("sessionAction").execute());
        assertFalse(action.getSession() instanceof SandboxedSessionMap);
        assertSame(session, action.getSession());
    }

    private void useLiveSession() {
        httpSession = new MockHttpSession();
        request.setSession(httpSession);
        session = new SessionMap(request);
        context = ActionContext.of(context).withSession(session).getContextMap();
    }

    private BackgroundProcess awaitDone(String actionName) throws InterruptedException {
        BackgroundProcess process = (BackgroundProcess) httpSession.getAttribute(ExecuteAndWaitInterceptor.KEY + actionName);
        await().atMost(5, TimeUnit.SECONDS).until(process::isDone);
        return process;
    }

    private void withRequestParams(Map<String, String[]> requestParams) {
        context = ActionContext.of(context).withParameters(HttpParameters.create(requestParams).build()).getContextMap();
    }

    protected ActionProxy buildProxy(String actionName) throws Exception {
        return actionProxyFactory.createActionProxy("", actionName, null, context);
    }

    protected void setUp() throws Exception {
        super.setUp();
        loadConfigurationProviders(new WaitConfigurationProvider());

        session = new HashMap<>();
        params = new HashMap<>();
        context = new HashMap<>();

        request = new StrutsMockHttpServletRequest();
        httpSession = new StrutsMockHttpSession();
        request.setSession(httpSession);
        request.setParameterMap(params);

        context = ActionContext.of(context)
            .withSession(session)
            .withParameters(HttpParameters.create().build())
            .withServletRequest(request)
            .getContextMap();

        container.inject(waitInterceptor);
        container.inject(parametersInterceptor);
        container.inject(tokenInterceptor);
        container.inject(tokenSessionInterceptor);
        container.inject(servletConfigInterceptor);

        waitInterceptor.init();
        parametersInterceptor.init();
        tokenInterceptor.init();
        tokenSessionInterceptor.init();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
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
            tokenInterceptor = new TokenInterceptor();
            tokenSessionInterceptor = new TokenSessionStoreInterceptor();
            servletConfigInterceptor = new ServletConfigInterceptor();
            PackageConfig wait = new PackageConfig.Builder("")
                .addActionConfig("action1", new ActionConfig.Builder("", "action1", ExecuteAndWaitDelayAction.class.getName())
                    .addResultConfig(new ResultConfig.Builder(Action.SUCCESS, MockResult.class.getName()).build())
                    .addResultConfig(new ResultConfig.Builder(ExecuteAndWaitInterceptor.WAIT, MockResult.class.getName()).build())
                    .addInterceptor(new InterceptorMapping("params", parametersInterceptor))
                    .addInterceptor(new InterceptorMapping("execAndWait", waitInterceptor))
                    .build())
                .addActionConfig("plainWait", waitActionConfig("plainWait"))
                .addActionConfig("sessionAction", new ActionConfig.Builder("", "sessionAction", SessionWritingWaitAction.class.getName())
                    .addResultConfig(new ResultConfig.Builder(Action.SUCCESS, MockResult.class.getName()).build())
                    .addResultConfig(new ResultConfig.Builder(ExecuteAndWaitInterceptor.WAIT, MockResult.class.getName()).build())
                    .addInterceptor(new InterceptorMapping("servletConfig", servletConfigInterceptor))
                    .addInterceptor(new InterceptorMapping("params", parametersInterceptor))
                    .addInterceptor(new InterceptorMapping("execAndWait", waitInterceptor))
                    .build())
                .addActionConfig("tokenWait", waitActionConfig("tokenWait", new InterceptorMapping("token", tokenInterceptor)))
                .addActionConfig("tokenSessionWait", waitActionConfig("tokenSessionWait", new InterceptorMapping("tokenSession", tokenSessionInterceptor)))
                .build();
            configuration.addPackageConfig("", wait);
        }

        private ActionConfig waitActionConfig(String name, InterceptorMapping... tokenInterceptors) {
            ActionConfig.Builder builder = new ActionConfig.Builder("", name, ExecuteAndWaitDelayAction.class.getName())
                .addResultConfig(new ResultConfig.Builder(Action.SUCCESS, MockResult.class.getName()).build())
                .addResultConfig(new ResultConfig.Builder(ExecuteAndWaitInterceptor.WAIT, MockResult.class.getName()).build());
            for (InterceptorMapping tokenInterceptor : tokenInterceptors) {
                builder.addInterceptor(tokenInterceptor);
            }
            return builder
                .addInterceptor(new InterceptorMapping("params", parametersInterceptor))
                .addInterceptor(new InterceptorMapping("execAndWait", waitInterceptor))
                .build();
        }

        public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
            builder.factory(ObjectFactory.class);
            builder.factory(ActionProxyFactory.class, DefaultActionProxyFactory.class);
            builder.factory(OgnlUtil.class, OgnlUtil.class);
        }

    }

}

class TestExecutorProvider implements ExecutorProvider {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void execute(Runnable task) {
        executor.execute(task);
    }

    @Override
    public boolean isShutdown() {
        return executor.isShutdown();
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }
}


