package org.apache.struts.action2.showcase.chat;

import java.util.Map;

import org.apache.struts.action2.interceptor.SessionAware;

import com.opensymphony.xwork.ActionSupport;

public class ChatLogoutAction extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = 1L;

	private ChatService chatService;
	
	private Map session;
	
	
	public ChatLogoutAction(ChatService chatService) {
		this.chatService = chatService;
	}
	
	public String execute() throws Exception {
		
		User user = (User) session.get(ChatAuthenticationInterceptor.USER_SESSION_KEY);
		if (user != null) {
			chatService.logout(user.getName());
			session.remove(ChatAuthenticationInterceptor.USER_SESSION_KEY);
		}
		
		return SUCCESS;
	}

	
	// === SessionAware ===
	public void setSession(Map session) {	
		this.session = session;
	}
}
