package org.apache.struts.action2.showcase.chat;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ChatSessionListener implements HttpSessionListener {

	private static final Log _log = LogFactory.getLog(ChatSessionListener.class);
	
	public void sessionCreated(HttpSessionEvent event) {
	}

	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
		if (context != null) {
			User user = (User) session.getAttribute(ChatInterceptor.CHAT_USER_SESSION_KEY);
			ChatService service = (ChatService) context.getBean("chatService");
			service.logout(user.getName());
			
			_log.info("session expired, logged user ["+user.getName()+"] out");
		}
	}

}
