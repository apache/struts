package org.apache.struts.action2.showcase.chat;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork.ActionSupport;

public class UsersAvailableAction extends ActionSupport {

	private static final long serialVersionUID = 1L;
	
	private List<User> availableUsers = new ArrayList<User>();
	private ChatService chatService;
	
	public UsersAvailableAction(ChatService chatService) {
		this.chatService = chatService;
	}
	
	public String execute() throws Exception {
		
		availableUsers = chatService.getAvailableUsers();
		
		return SUCCESS;
	}
	
	public List<User> getAvailableUsers() {
		return availableUsers;
	}
}
