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
import org.apache.struts2.interceptor.parameter.StrutsParameter;

import java.util.ArrayList;
import java.util.List;

public class UsersAvailableInRoomAction extends ActionSupport {

	private static final long serialVersionUID = 1L;

	private final ChatService chatService;
	private List<User> usersAvailableInRoom = new ArrayList<>();

	private String roomName;

	public UsersAvailableInRoomAction(ChatService chatService) {
		this.chatService = chatService;
	}


	public String getRoomName() {
		return this.roomName;
	}

	@StrutsParameter
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public List<User> getUsersAvailableInRoom() {
		return usersAvailableInRoom;
	}

	@Override
	public String execute() throws Exception {
		try {
			usersAvailableInRoom = chatService.getUsersAvailableInRoom(roomName);
		} catch (ChatException e) {
			addActionError(e.getMessage());
		}
		return SUCCESS;
	}

}
