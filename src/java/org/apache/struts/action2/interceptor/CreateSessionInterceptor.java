/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.interceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.struts.action2.ServletActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.AroundInterceptor;

/**
 * <!-- START SNIPPET: description -->
 *
 * This interceptor creates the HttpSession.
 * <p/>
 * This is particular usefull when using the &lt;@ww.tokten&gt; tag in freemarker templates.
 * The tag <b>do</b> require that a HttpSession is already created since freemarker commits
 * the response to the client immediately.
 * 
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
 *
 *
 * <!-- START SNIPPET: extending -->
 * 
 * <ul>
 * 	<li>none</li>
 * </ul>
 * 
 * <!-- END SNIPPET: extending -->
 *
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>None</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <b>Example:</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * 
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="create-session"/&gt;
 *     &lt;interceptor-ref name="defaultStack"/&gt;
 *     &lt;result name="input"&gt;input_with_token_tag.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * 
 * <!-- END SNIPPET: example -->
 * </pre>
 * 
 * @author Claus Ibsen
 * @version $Date: 2006/03/19 07:40:59 $ $Id: CreateSessionInterceptor.java,v 1.4 2006/03/19 07:40:59 davsclaus Exp $
 */
public class CreateSessionInterceptor extends AroundInterceptor {
	
	private static final long serialVersionUID = -4590322556118858869L;
	
	private static final Log _log = LogFactory.getLog(CreateSessionInterceptor.class);
	
	
	protected void before(ActionInvocation invocation) throws Exception {
		_log.debug("Creating HttpSession");
		ServletActionContext.getRequest().getSession(true);
	}

	protected void after(ActionInvocation dispatcher, String result) throws Exception {
	}
}
