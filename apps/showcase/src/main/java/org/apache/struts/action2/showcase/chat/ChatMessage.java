package org.apache.struts.action2.showcase.chat;

import java.util.Date;

public class ChatMessage {
	
	private Date creationDate;
	private String message;
	private User creator;
	
	public ChatMessage(String message, User creator) {
		assert(message != null);
		assert(creator != null);
		
		this.creationDate = new Date(System.currentTimeMillis());
		this.message = message;
		this.creator = creator;
	}
	
	public Date getCreationDate() {
		return creationDate;
	}
	public User getCreator() {
		return creator;
	}
	public String getMessage() {
		return message;
	}
}
