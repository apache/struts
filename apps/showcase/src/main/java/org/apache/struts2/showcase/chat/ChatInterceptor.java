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
import com.opensymphony.xwork2.interceptor.Interceptor;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.http.HttpSession;

/**
 * Authenticate showcase chat example, make sure everyone have a username.
 */
public class ChatInterceptor implements Interceptor {

	private static final Logger LOG = LogManager.getLogger(ChatInterceptor.class);

	private static final long serialVersionUID = 1L;

	public static final String CHAT_USER_SESSION_KEY = "ChatUserSessionKey";

	public void destroy() {
	}

	public void init() {
	}

	public String intercept(ActionInvocation invocation) throws Exception {
		HttpSession session = (HttpSession) ActionContext.getContext().get(ActionContext.SESSION);
		User chatUser = (User) session.getAttribute(CHAT_USER_SESSION_KEY);
		if (chatUser == null) {
			LOG.debug("Chat user not logged in");
			return Action.LOGIN;
		}
		return invocation.invoke();
	}
}


