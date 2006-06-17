package org.apache.struts.action2.showcase.chat;

public class ChatException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public enum ErrorType {
		ROOM_ALREADY_EXISTS, 
		USER_ALREADY_EXISTS,
		NO_SUCH_ROOM_EXISTS
	}
	
	public ChatException(String description, ErrorType type) {
		super(description);
	}
}
