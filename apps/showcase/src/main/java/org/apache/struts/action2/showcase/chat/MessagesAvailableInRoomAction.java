package org.apache.struts.action2.showcase.chat;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork.ActionSupport;

public class MessagesAvailableInRoomAction extends ActionSupport {

	private static final long serialVersionUID = 1L;

	private String roomName;
	private ChatService chatService;
	private List<ChatMessage> messagesAvailableInRoom = new ArrayList<ChatMessage>();
	
	public String getRoomName() { return this.roomName; }
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	
	public List<ChatMessage> getMessagesAvailableInRoom() {
		return messagesAvailableInRoom;
	}
	
	public MessagesAvailableInRoomAction(ChatService chatService) {
		this.chatService = chatService;
	}
	
	public String execute() throws Exception {
		try {
			messagesAvailableInRoom = chatService.getMessagesInRoom(roomName);
		}
		catch(ChatException e) {
			addActionError(e.getMessage());
		}
		return SUCCESS;
	}
	
}
