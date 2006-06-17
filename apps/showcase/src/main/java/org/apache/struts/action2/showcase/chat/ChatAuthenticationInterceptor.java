package org.apache.struts.action2.showcase.chat;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action2.dispatcher.SessionMap;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;

public class ChatAuthenticationInterceptor implements Interceptor {

	private static final long serialVersionUID = 1L;
	
	private static final Log _log = LogFactory.getLog(ChatAuthenticationInterceptor.class);
	
	public static final String USER_SESSION_KEY = "chatUserSessionKey";

	public void destroy() {
	}

	public void init() {
	}

	public String intercept(ActionInvocation invocation) throws Exception {
		
		_log.debug("Authenticating chat user");
		
		SessionMap session = (SessionMap) ActionContext.getContext().get(ActionContext.SESSION);
		User user = (User) session.get(USER_SESSION_KEY);
		
		if (user == null) {
			return Action.LOGIN;
		}
		return invocation.invoke();
	}

}
