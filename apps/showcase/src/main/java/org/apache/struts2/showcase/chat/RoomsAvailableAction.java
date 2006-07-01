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
package org.apache.struts2.showcase.chat;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork.ActionSupport;

public class RoomsAvailableAction extends ActionSupport {

	private static final long serialVersionUID = 1L;

	private List<Room> availableRooms = new ArrayList<Room>();
	
	private ChatService chatService;
	
	public RoomsAvailableAction(ChatService chatService) {
		this.chatService = chatService;
	}
	
	public String execute() throws Exception {
		availableRooms = chatService.getAvailableRooms();
		return SUCCESS;
	}
	
	public List<Room> getAvailableRooms() {
		return availableRooms;
	}
}
