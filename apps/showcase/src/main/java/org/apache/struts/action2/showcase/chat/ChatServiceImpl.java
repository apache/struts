package org.apache.struts.action2.showcase.chat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChatServiceImpl implements ChatService {
	
	private Map<String, User> availableUsers = new LinkedHashMap<String, User>();
	private Map<String, Room> availableRooms = new LinkedHashMap<String, Room>();
	
	
	public List<User> getAvailableUsers() {
		return new ArrayList<User>(availableUsers.values());
	}

	public List<Room> getAvailableRooms() {
		return new ArrayList<Room>(availableRooms.values());
	}

	public void addRoom(Room room) {
		if (availableRooms.containsKey(room.getName())) {
			throw new ChatException("room ["+room.getName()+"] is already available", ChatException.ErrorType.valueOf("ROOM_ALREADY_EXISTS"));
		}
		availableRooms.put(room.getName(), room);
	}
	
	public void login(User user) {
		assert(user != null);
		if (availableUsers.containsKey(user.getName())) {
			throw new ChatException("User ["+user.getName()+"] already exists", ChatException.ErrorType.valueOf("USER_ALREADY_EXISTS"));
		}
		availableUsers.put(user.getName(), user);
	}

	public void logout(String name) {
		assert(name != null);
		assert(name.trim().length() > 0);
		availableUsers.remove(name);
		for (Room room : availableRooms.values()) {
			if (room.hasMember(name)) {
				room.memberExit(name);
			}
		}
	}

	public void exitRoom(String userName, String roomName) {
		assert(roomName != null);
		assert(roomName.trim().length()> 0);
		
		if (availableRooms.containsKey(roomName)) {
			Room room = availableRooms.get(roomName);
			room.memberExit(userName);
		}
	}
	
	public void enterRoom(User user, String roomName) {
		assert(roomName != null);
		assert(roomName.trim().length() > 0);
		if (! availableRooms.containsKey(roomName)) {
			throw new ChatException("No such room exists ["+roomName+"]", ChatException.ErrorType.NO_SUCH_ROOM_EXISTS);
		}
		Room room = availableRooms.get(roomName);
		room.memberEnter(user);
	}

	public List<ChatMessage> getMessagesInRoom(String roomName) {
		assert(roomName != null);
		assert(roomName.trim().length() > 0);
		if (! availableRooms.containsKey(roomName)) {
			throw new ChatException("No such room exists ["+roomName+"]", ChatException.ErrorType.NO_SUCH_ROOM_EXISTS);
		}
		Room room = availableRooms.get(roomName);
		return room.getChatMessages();
	}

	public void sendMessageToRoom(String roomName, User user, String message) {
		assert(roomName != null);
		if (! availableRooms.containsKey(roomName)) {
			throw new ChatException("No such room exists ["+roomName+"]", ChatException.ErrorType.NO_SUCH_ROOM_EXISTS);
		}
		Room room = availableRooms.get(roomName);
		room.addMessage(new ChatMessage(message, user));
	}

	public List<User> getUsersAvailableInRoom(String roomName) {
		assert(roomName != null);
		if (! availableRooms.containsKey(roomName)) {
			throw new ChatException("No such room exists ["+roomName+"]", ChatException.ErrorType.NO_SUCH_ROOM_EXISTS);
		}
		Room room = availableRooms.get(roomName);
		return room.getMembers();
	}
}
