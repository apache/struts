package org.apache.struts.action2.showcase.chat;

import java.util.Map;

import org.apache.struts.action2.interceptor.SessionAware;

import com.opensymphony.xwork.ActionSupport;

public class SendMessageToRoomAction extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = 1L;
	
	private ChatService chatService;
	
	private String roomName;
	private String message;
	private Map session;
	
	
	public SendMessageToRoomAction(ChatService chatService) {
		this.chatService = chatService;
	}
	
	public String getRoomName() { return this.roomName; }
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	
	public String getMessage() { return this.message; }
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	public String execute() throws Exception {
		User user = (User) session.get(ChatAuthenticationInterceptor.USER_SESSION_KEY);
		try {
			chatService.sendMessageToRoom(roomName, user, message);
		}catch(ChatException e) {
			addActionError(e.getMessage());
		}
		return SUCCESS;
	}

	public void setSession(Map session) {
		this.session = session;
	}

	
}
