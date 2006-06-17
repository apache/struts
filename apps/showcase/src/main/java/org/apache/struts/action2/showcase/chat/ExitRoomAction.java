package org.apache.struts.action2.showcase.chat;

import java.util.Map;

import org.apache.struts.action2.interceptor.SessionAware;

import com.opensymphony.xwork.ActionSupport;

public class ExitRoomAction extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = 1L;

	private String roomName;
	
	private Map session;
	
	public String getRoomName() { return roomName; }
	public void setRoomName(String roomName) { this.roomName = roomName; }
	
	private ChatService chatService; 
	
	public ExitRoomAction(ChatService chatService) {
		this.chatService = chatService;
	}
	
	public String execute() throws Exception {
		User user = (User) session.get(ChatAuthenticationInterceptor.USER_SESSION_KEY);
		chatService.exitRoom(user.getName(), roomName);
		
		return SUCCESS;
	}
	
	// === SessionAware ===
	public void setSession(Map session) {
		this.session = session;
	}
	
}
