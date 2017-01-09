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
package org.apache.struts2.showcase.chat;


import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.struts2.dispatcher.SessionMap;

public class ChatAuthenticationInterceptor extends AbstractInterceptor {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger(ChatAuthenticationInterceptor.class);
	public static final String USER_SESSION_KEY = "chatUserSessionKey";

	public String intercept(ActionInvocation invocation) throws Exception {

		LOG.debug("Authenticating chat user");

		SessionMap session = (SessionMap) ActionContext.getContext().get(ActionContext.SESSION);
		User user = (User) session.get(USER_SESSION_KEY);

		if (user == null) {
			return Action.LOGIN;
		}
		return invocation.invoke();
	}

}
