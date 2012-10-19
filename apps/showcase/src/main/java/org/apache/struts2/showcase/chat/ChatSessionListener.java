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

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class ChatSessionListener implements HttpSessionListener {

	private static final Logger LOG = LoggerFactory.getLogger(ChatSessionListener.class);

	public void sessionCreated(HttpSessionEvent event) {
	}

	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
		if (context != null) {
			User user = (User) session.getAttribute(ChatInterceptor.CHAT_USER_SESSION_KEY);
			if (user != null) {
				ChatService service = (ChatService) context.getBean("chatService");
				service.logout(user.getName());

				LOG.info("session expired, logged user [" + user.getName() + "] out");
			}
		}
	}

}
