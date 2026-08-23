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

import jakarta.servlet.http.HttpSession;
import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionProxy;
import org.apache.struts2.ActionProxyFactory;
import org.apache.struts2.DefaultActionProxyFactory;
import org.apache.struts2.ObjectFactory;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.action.Action;
import org.apache.struts2.config.Configuration;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.config.ConfigurationProvider;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.config.entities.InterceptorMapping;
import org.apache.struts2.config.entities.PackageConfig;
import org.apache.struts2.config.entities.ResultConfig;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.inject.ContainerBuilder;
import org.apache.struts2.mock.MockResult;
import org.apache.struts2.ognl.OgnlUtil;
import org.apache.struts2.util.TokenHelper;
import org.apache.struts2.util.location.LocatableProperties;
import org.apache.struts2.views.jsp.StrutsMockHttpServletRequest;
import org.apache.struts2.views.jsp.StrutsMockHttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Covers how the background process is keyed within a single session: by action name only, and by
 * action name plus transaction token when {@link ExecuteAndWaitInterceptor#getBackgroundProcessName}
 * is overridden as described in that interceptor's javadoc.
 */
public class ExecuteAndWaitInterceptorTokenScopeTest extends StrutsInternalTestCase {

    private StrutsMockHttpServletRequest request;
    private Map<String, Object> session;
    private ExecuteAndWaitInterceptor waitInterceptor;

    /** Read by the provider during loadPackages() to pick which variant to install. */
    private boolean tokenScoped;

    /** The token-scoped extension documented in {@link ExecuteAndWaitInterceptor}'s javadoc. */
    public static class TokenizedExecuteAndWaitInterceptor extends ExecuteAndWaitInterceptor {
        @Override
        protected String getBackgroundProcessName(ActionProxy proxy) {
            String token = TokenHelper.getToken();
            return token == null
                ? super.getBackgroundProcessName(proxy)
                : super.getBackgroundProcessName(proxy) + "_" + token;
        }
    }

    public void testStockInterceptorSharesOneProcessAcrossTabs() throws Exception {
        setUpWith(false);

        assertEquals("wait", execute("tab-A"));
        assertEquals("wait", execute("tab-B"));

        assertEquals("both tabs share a single background process", 1, backgroundProcessKeys().size());
    }

    public void testTokenScopedInterceptorIsolatesTabs() throws Exception {
        setUpWith(true);

        assertEquals("wait", execute("tab-A"));
        assertEquals("wait", execute("tab-B"));

        List<String> keys = backgroundProcessKeys();
        assertEquals("each tab gets its own background process: " + keys, 2, keys.size());
        assertTrue(keys.toString(), keys.contains(ExecuteAndWaitInterceptor.KEY + "action1_tab-A"));
        assertTrue(keys.toString(), keys.contains(ExecuteAndWaitInterceptor.KEY + "action1_tab-B"));
    }

    private List<String> backgroundProcessKeys() {
        return session.keySet().stream()
            .filter(k -> k.startsWith(ExecuteAndWaitInterceptor.KEY))
            .sorted()
            .toList();
    }

    private String execute(String token) throws Exception {
        Map<String, Object> context = ActionContext.of(new HashMap<>())
            .withSession(session)
            .withParameters(HttpParameters.create(Map.of(
                TokenHelper.DEFAULT_TOKEN_NAME, token,
                TokenHelper.TOKEN_NAME_FIELD, TokenHelper.DEFAULT_TOKEN_NAME)).build())
            .withServletRequest(request)
            .getContextMap();
        return actionProxyFactory.createActionProxy("", "action1", null, context).execute();
    }

    private void setUpWith(boolean useTokenScoped) throws Exception {
        tokenScoped = useTokenScoped;
        loadConfigurationProviders(new WaitConfigurationProvider());

        session = new HashMap<>();
        request = new StrutsMockHttpServletRequest();
        HttpSession httpSession = new StrutsMockHttpSession();
        request.setSession(httpSession);
        request.setParameterMap(new HashMap<>());

        container.inject(waitInterceptor);
        waitInterceptor.init();
        waitInterceptor.setDelay(0);
        waitInterceptor.setDelaySleepInterval(0);
    }

    private class WaitConfigurationProvider implements ConfigurationProvider {

        private Configuration config;

        public void destroy() {
            waitInterceptor.destroy();
        }

        public boolean needsReload() {
            return false;
        }

        public void init(Configuration configuration) throws ConfigurationException {
            this.config = configuration;
        }

        public void loadPackages() throws ConfigurationException {
            waitInterceptor = tokenScoped ? new TokenizedExecuteAndWaitInterceptor() : new ExecuteAndWaitInterceptor();
            PackageConfig wait = new PackageConfig.Builder("")
                .addActionConfig("action1", new ActionConfig.Builder("", "action1", ExecuteAndWaitDelayAction.class.getName())
                    .addResultConfig(new ResultConfig.Builder(Action.SUCCESS, MockResult.class.getName()).build())
                    .addResultConfig(new ResultConfig.Builder(ExecuteAndWaitInterceptor.WAIT, MockResult.class.getName()).build())
                    .addInterceptor(new InterceptorMapping("execAndWait", waitInterceptor))
                    .build())
                .build();
            config.addPackageConfig("", wait);
        }

        public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
            builder.factory(ObjectFactory.class);
            builder.factory(ActionProxyFactory.class, DefaultActionProxyFactory.class);
            builder.factory(OgnlUtil.class, OgnlUtil.class);
        }
    }
}
