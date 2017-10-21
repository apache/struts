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
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * <!-- START SNIPPET: description -->
 * <p>
 * This interceptor logs the start and end of the execution an action (in English-only, not internationalized).
 * <br>
 * <b>Note:</b>: This interceptor will log at <tt>INFO</tt> level.
 * </p>
 * <!-- END SNIPPET: description -->
 *
 * <!-- START SNIPPET: parameters -->
 * There are no parameters for this interceptor.
 * <!-- END SNIPPET: parameters -->
 *
 * <!-- START SNIPPET: extending -->
 * There are no obvious extensions to the existing interceptor.
 * <!-- END SNIPPET: extending -->
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;!-- prints out a message before and after the immediate action execution --&gt;
 * &lt;action name=&quot;someAction&quot; class=&quot;com.examples.SomeAction&quot;&gt;
 *     &lt;interceptor-ref name=&quot;completeStack&quot;/&gt;
 *     &lt;interceptor-ref name=&quot;logger&quot;/&gt;
 *     &lt;result name=&quot;success&quot;&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 *
 * &lt;!-- prints out a message before any more interceptors continue and after they have finished --&gt;
 * &lt;action name=&quot;someAction&quot; class=&quot;com.examples.SomeAction&quot;&gt;
 *     &lt;interceptor-ref name=&quot;logger&quot;/&gt;
 *     &lt;interceptor-ref name=&quot;completeStack&quot;/&gt;
 *     &lt;result name=&quot;success&quot;&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Jason Carreira
 */
public class LoggingInterceptor extends AbstractInterceptor {
    private static final Logger LOG = LogManager.getLogger(LoggingInterceptor.class);
    private static final String FINISH_MESSAGE = "Finishing execution stack for action ";
    private static final String START_MESSAGE = "Starting execution stack for action ";

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        logMessage(invocation, START_MESSAGE);
        String result = invocation.invoke();
        logMessage(invocation, FINISH_MESSAGE);
        return result;
    }

    private void logMessage(ActionInvocation invocation, String baseMessage) {
        if (LOG.isInfoEnabled()) {
            StringBuilder message = new StringBuilder(baseMessage);
            String namespace = invocation.getProxy().getNamespace();

            if ((namespace != null) && (namespace.trim().length() > 0)) {
                message.append(namespace).append("/");
            }

            message.append(invocation.getProxy().getActionName());
        	LOG.info(message.toString());
        }
    }

}
