package org.apache.struts.action2.showcase.chat;

import java.util.List;

public interface ChatService {
	List<User> getAvailableUsers();
	void login(User user);
	void logout(String name);
	
	List<Room> getAvailableRooms();
	void addRoom(Room room);
	void enterRoom(User user, String roomName);
	void exitRoom(String userName, String roomName);
	List<ChatMessage> getMessagesInRoom(String roomName);
	void sendMessageToRoom(String roomName, User user, String message);
	List<User> getUsersAvailableInRoom(String roomName);
}
