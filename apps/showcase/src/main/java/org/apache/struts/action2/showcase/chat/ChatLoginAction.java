package org.apache.struts.action2.showcase.chat;

import java.util.Map;

import org.apache.struts.action2.interceptor.SessionAware;

import com.opensymphony.xwork.ActionSupport;

public class ChatLoginAction extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = 1L;
	
	private ChatService chatService;
	private Map session;
	
	private String name;
	
	public ChatLoginAction(ChatService chatService) {
		this.chatService = chatService;
	}
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public String execute() throws Exception {
		try {
			chatService.login(new User(name));
			session.put(ChatAuthenticationInterceptor.USER_SESSION_KEY, new User(name));
		}
		catch(ChatException e) {
			e.printStackTrace();
			addActionError(e.getMessage());
			return INPUT;
		}
		return SUCCESS;
	}

	
	// === SessionAware ===
	public void setSession(Map session) {
		this.session = session;
	}
}
