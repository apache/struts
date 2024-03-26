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

public class SendMessageToRoomAction extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = 1L;

	private ChatService chatService;

	private String roomName;
	private String message;
	private Map session;


	public SendMessageToRoomAction(ChatService chatService) {
		this.chatService = chatService;
	}

	public String getRoomName() {
		return this.roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


	public String execute() throws Exception {
		User user = (User) session.get(ChatAuthenticationInterceptor.USER_SESSION_KEY);
		try {
			chatService.sendMessageToRoom(roomName, user, message);
		} catch (ChatException e) {
			addActionError(e.getMessage());
		}
		return SUCCESS;
	}

	public void setSession(Map session) {
		this.session = session;
	}


}
