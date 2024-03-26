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

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class ChatLoginAction extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = 1L;

	private ChatService chatService;
	private Map session;

	private String name;

	public ChatLoginAction(ChatService chatService) {
		this.chatService = chatService;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String execute() throws Exception {
		try {
			chatService.login(new User(name));
			session.put(ChatAuthenticationInterceptor.USER_SESSION_KEY, new User(name));
		} catch (ChatException e) {
			e.printStackTrace();
			addActionError(e.getMessage());
			return INPUT;
		}
		return SUCCESS;
	}


	// === SessionAware ===
	public void setSession(Map session) {
		this.session = session;
	}
}
