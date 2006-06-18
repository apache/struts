/*
 * $Id: MemoryStorage.java 394498 2006-04-16 15:28:06Z tmjee $
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.action2.showcase.chat;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action2.dispatcher.SessionMap;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;

public class ChatAuthenticationInterceptor implements Interceptor {

	private static final long serialVersionUID = 1L;
	
	private static final Log _log = LogFactory.getLog(ChatAuthenticationInterceptor.class);
	
	public static final String USER_SESSION_KEY = "chatUserSessionKey";

	public void destroy() {
	}

	public void init() {
	}

	public String intercept(ActionInvocation invocation) throws Exception {
		
		_log.debug("Authenticating chat user");
		
		SessionMap session = (SessionMap) ActionContext.getContext().get(ActionContext.SESSION);
		User user = (User) session.get(USER_SESSION_KEY);
		
		if (user == null) {
			return Action.LOGIN;
		}
		return invocation.invoke();
	}

}
