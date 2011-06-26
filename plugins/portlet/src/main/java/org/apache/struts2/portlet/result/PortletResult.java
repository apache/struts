/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.portlet.result;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.StrutsResultSupport;
import org.apache.struts2.portlet.PortletActionConstants;
import org.apache.struts2.portlet.context.PortletActionContext;

import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Result type that includes a JSP to render.
 * 
 */
public class PortletResult extends StrutsResultSupport implements PortletActionConstants {

	private static final long serialVersionUID = 434251393926178567L;

	private boolean useDispatcherServlet;
	
	private String dispatcherServletName = DEFAULT_DISPATCHER_SERVLET_NAME;

	/**
	 * Logger instance.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(PortletResult.class);

	private String contentType = "text/html";

	private String title;
	
	protected PortletMode portletMode;

	public PortletResult() {
		super();
	}

	public PortletResult(String location) {
		super(location);
	}

	/**
	 * Execute the result. Obtains the
	 * {@link javax.portlet.PortletRequestDispatcher}from the
	 * {@link PortletActionContext}and includes the JSP.
	 * 
	 * @see com.opensymphony.xwork2.Result#execute(com.opensymphony.xwork2.ActionInvocation)
	 */
	public void doExecute(String finalLocation, ActionInvocation actionInvocation) throws Exception {

		if (PortletActionContext.isRender()) {
			executeRenderResult(finalLocation);
		} else if (PortletActionContext.isEvent()) {
			executeActionResult(finalLocation, actionInvocation);
		} else {
			executeRegularServletResult(finalLocation, actionInvocation);
		}
	}

	/**
	 * Executes the regular servlet result.
	 * 
	 * @param finalLocation
	 * @param actionInvocation
	 */
	private void executeRegularServletResult(String finalLocation, ActionInvocation actionInvocation)
			throws ServletException, IOException {
		ServletContext ctx = ServletActionContext.getServletContext();
		HttpServletRequest req = ServletActionContext.getRequest();
		HttpServletResponse res = ServletActionContext.getResponse();
		try {
			ctx.getRequestDispatcher(finalLocation).include(req, res);
		} catch (ServletException e) {
			LOG.error("ServletException including " + finalLocation, e);
			throw e;
		} catch (IOException e) {
			LOG.error("IOException while including result '" + finalLocation + "'", e);
			throw e;
		}
	}

	/**
	 * Executes the action result.
	 * 
	 * @param finalLocation
	 * @param invocation
	 */
	protected void executeActionResult(String finalLocation, ActionInvocation invocation) throws Exception {
		if (LOG.isDebugEnabled()) LOG.debug("Executing result in Event phase");
		ActionResponse res = PortletActionContext.getActionResponse();
		Map sessionMap = invocation.getInvocationContext().getSession();
		if (LOG.isDebugEnabled()) LOG.debug("Setting event render parameter: " + finalLocation);
		if (finalLocation.indexOf('?') != -1) {
			convertQueryParamsToRenderParams(res, finalLocation.substring(finalLocation.indexOf('?') + 1));
			finalLocation = finalLocation.substring(0, finalLocation.indexOf('?'));
		}
		if (finalLocation.endsWith(".action")) {
			// View is rendered with a view action...luckily...
			finalLocation = finalLocation.substring(0, finalLocation.lastIndexOf("."));
			res.setRenderParameter(ACTION_PARAM, finalLocation);
		} else {
			// View is rendered outside an action...uh oh...
            String namespace = invocation.getProxy().getNamespace();
            if ( namespace != null && namespace.length() > 0 && !namespace.endsWith("/")) {
                namespace += "/";
                
            }
            res.setRenderParameter(ACTION_PARAM, namespace + "renderDirect");
			sessionMap.put(RENDER_DIRECT_LOCATION, finalLocation);
		}
		if(portletMode != null) {
			res.setPortletMode(portletMode);
			res.setRenderParameter(PortletActionConstants.MODE_PARAM, portletMode.toString());
		}
		else {
			res.setRenderParameter(PortletActionConstants.MODE_PARAM, PortletActionContext.getRequest().getPortletMode()
					.toString());
		}
	}

	/**
	 * Converts the query params to render params.
	 * 
	 * @param response
	 * @param queryParams
	 */
	protected static void convertQueryParamsToRenderParams(ActionResponse response, String queryParams) {
		StringTokenizer tok = new StringTokenizer(queryParams, "&");
		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			String key = token.substring(0, token.indexOf('='));
			String value = token.substring(token.indexOf('=') + 1);
			response.setRenderParameter(key, value);
		}
	}

	/**
	 * Executes the render result.
	 * 
	 * @param finalLocation
	 * @throws PortletException
	 * @throws IOException
	 */
	protected void executeRenderResult(final String finalLocation) throws PortletException, IOException {
		if (LOG.isDebugEnabled()) LOG.debug("Executing result in Render phase");
		PortletContext ctx = PortletActionContext.getPortletContext();
		RenderRequest req = PortletActionContext.getRenderRequest();
		RenderResponse res = PortletActionContext.getRenderResponse();
		res.setContentType(contentType);
		if (StringUtils.isNotEmpty(title)) {
			res.setTitle(title);
		}
		if (LOG.isDebugEnabled()) LOG.debug("Location: " + finalLocation);
		if (useDispatcherServlet) {
			req.setAttribute(DISPATCH_TO, finalLocation);
			PortletRequestDispatcher dispatcher = ctx.getNamedDispatcher(dispatcherServletName);
			if(dispatcher == null) {
				throw new PortletException("Could not locate dispatcher servlet \"" + dispatcherServletName + "\". Please configure it in your web.xml file");
			}
			dispatcher.include(req, res);
		} else {
			PortletRequestDispatcher dispatcher = ctx.getRequestDispatcher(finalLocation);
			if (dispatcher == null) {
				throw new PortletException("Could not locate dispatcher for '" + finalLocation + "'");
			}
			dispatcher.include(req, res);
		}
	}

	/**
	 * Sets the content type.
	 * 
	 * @param contentType
	 *            The content type to set.
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Sets the title.
	 * 
	 * @param title
	 *            The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setPortletMode(String portletMode) {
		if(portletMode != null) {
			this.portletMode = new PortletMode(portletMode);
		}
	}

	@Inject("struts.portlet.useDispatcherServlet") 
	public void setUseDispatcherServlet(String useDispatcherServlet) {
		this.useDispatcherServlet = "true".equalsIgnoreCase(useDispatcherServlet);
	}
	
	@Inject("struts.portlet.dispatcherServletName")
	public void setDispatcherServletName(String dispatcherServletName) {
		this.dispatcherServletName = dispatcherServletName;
	}
}
