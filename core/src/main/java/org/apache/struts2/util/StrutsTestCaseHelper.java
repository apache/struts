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
package org.apache.struts2.util;

import org.apache.struts2.ActionContext;
import org.apache.struts2.inject.Container;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.util.ValueStackFactory;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.DispatcherErrorHandler;

import java.util.HashMap;
import java.util.Map;

import static org.apache.struts2.StrutsConstants.STRUTS_ALLOWLIST_ENABLE;

/**
 * Generic test setup methods to be used with any unit testing framework.
 */
public class StrutsTestCaseHelper {

    public static Dispatcher initDispatcher(ServletContext ctx, Map<String, String> params) {
        Map<String, String> finalParams = params != null ? new HashMap<>(params) : new HashMap<>();
        finalParams.putIfAbsent(STRUTS_ALLOWLIST_ENABLE, "false");
        Dispatcher du = new DispatcherWrapper(ctx, finalParams);
        du.init();
        Dispatcher.setInstance(du);

        // Reset the value stack
        Container container = du.getContainer();
        ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();
        stack.getActionContext().withContainer(container).withValueStack(stack).bind();

        return du;
    }

    public static void tearDown(Dispatcher dispatcher) {
        if (dispatcher != null && dispatcher.getConfigurationManager() != null) {
            dispatcher.cleanup();
        }
        tearDown();
    }

    public static void tearDown() {
        (new Dispatcher(null, null)).cleanUpAfterInit(); // Clear ContainerHolder
        Dispatcher.clearInstance();
        ActionContext.clear();
    }

    private static class DispatcherWrapper extends Dispatcher {

        public DispatcherWrapper(ServletContext ctx, Map<String, String> params) {
            super(ctx, params);
            super.setDispatcherErrorHandler(new MockErrorHandler());
        }

        @Override
        public void setDispatcherErrorHandler(DispatcherErrorHandler errorHandler) {
            // ignore
        }
    }

    private static class MockErrorHandler implements DispatcherErrorHandler {
        public void init(ServletContext ctx) {
            // ignore
        }

        public void handleError(HttpServletRequest request, HttpServletResponse response, int code, Exception e) {
            System.out.println("Dispatcher#sendError: " + code);
            e.printStackTrace(System.out);
        }
    }

}
