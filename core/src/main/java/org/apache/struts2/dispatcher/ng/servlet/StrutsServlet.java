/*
 * $Id: DefaultActionSupport.java 651946 2008-04-27 13:41:38Z apetrelli $
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
package org.apache.struts2.dispatcher.ng.servlet;

import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.ng.ExecuteOperations;
import org.apache.struts2.dispatcher.ng.InitOperations;
import org.apache.struts2.dispatcher.ng.PrepareOperations;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet dispatcher for Struts.  The preferred way to use Struts is as a filter via the
 * {@link org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter} and its variants.  This servlet dispatcher
 * is only for those that really know what they are doing as it may not support every feature of Struts, particularly
 * static resource serving.
 */
public class StrutsServlet extends HttpServlet {

    private PrepareOperations prepare;
    private ExecuteOperations execute;

    @Override
    public void init(ServletConfig filterConfig) throws ServletException {
        InitOperations init = new InitOperations();
        Dispatcher dispatcher = null;
        try {
            ServletHostConfig config = new ServletHostConfig(filterConfig);
            init.initLogging(config);
            dispatcher = init.initDispatcher(config);
            init.initStaticContentLoader(config, dispatcher);

            prepare = new PrepareOperations(dispatcher);
            execute = new ExecuteOperations(dispatcher);
        } finally {
            if (dispatcher != null) {
                dispatcher.cleanUpAfterInit();
            }
            init.cleanup();
        }
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        try {
            prepare.createActionContext(request, response);
            prepare.assignDispatcherToThread();
            prepare.setEncodingAndLocale(request, response);
            request = prepare.wrapRequest(request);
            ActionMapping mapping = prepare.findActionMapping(request, response);
            if (mapping == null) {
                boolean handled = execute.executeStaticResourceRequest(request, response);
                if (!handled)
                    throw new ServletException("Resource loading not supported, use the StrutsPrepareAndExecuteFilter instead.");
            } else {
                execute.executeAction(request, response, mapping);
            }
        } finally {
            prepare.cleanupRequest(request);
        }
    }

    @Override
    public void destroy() {
        prepare.cleanupDispatcher();
    }

}
