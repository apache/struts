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
package org.apache.struts2.dispatcher.listener;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.InitOperations;
import org.apache.struts2.dispatcher.PrepareOperations;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Servlet listener for Struts.  The preferred way to use Struts is as a filter via the
 * {@link org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter} and its variants.
 * This might be useful if Struts config information is needed from other servlet listeners, like
 * Sitemesh or OSGi
 */
public class StrutsListener implements ServletContextListener {
    private PrepareOperations prepare;

    public void contextInitialized(ServletContextEvent sce) {
        InitOperations init = new InitOperations();
        Dispatcher dispatcher = null;
        try {
            ListenerHostConfig config = new ListenerHostConfig(sce.getServletContext());
            init.initLogging(config);
            dispatcher = init.initDispatcher(config);
            init.initStaticContentLoader(config, dispatcher);

            prepare = new PrepareOperations(dispatcher);
            sce.getServletContext().setAttribute(StrutsStatics.SERVLET_DISPATCHER, dispatcher);
        } finally {
            if (dispatcher != null) {
                dispatcher.cleanUpAfterInit();
            }
            init.cleanup();
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        prepare.cleanupDispatcher();
    }
}
