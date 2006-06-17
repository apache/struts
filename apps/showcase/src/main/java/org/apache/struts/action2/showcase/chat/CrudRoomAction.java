package org.apache.struts.action2.showcase.chat;

import com.opensymphony.xwork.ActionSupport;

public class CrudRoomAction extends ActionSupport {

	private static final long serialVersionUID = 1L;

	private ChatService chatService;
	
	private String name;
	private String description;
	
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CrudRoomAction(ChatService chatService) {
		this.chatService = chatService;
	}
	
	public String create() throws Exception {
		try {
			chatService.addRoom(new Room(name, description));
		}
		catch(ChatException e) {
			addActionError(e.getMessage());
		}
		return SUCCESS;
	}
}
