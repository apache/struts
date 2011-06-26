/*
 * $Id: PortletResult.java 582626 2007-10-07 13:26:12Z mrdon $
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

import java.io.IOException;
import java.util.Map;
import java.util.StringTokenizer;

import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.portlet.RenderResponse;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.StrutsResultSupport;
import org.apache.struts2.portlet.PortletActionConstants;
import org.apache.struts2.portlet.context.PortletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

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

    PortletResultHelper resultHelper;

	public PortletResult() {
		super();
        determineResultHelper();
	}

	public PortletResult(String location) {
		super(location);
        determineResultHelper();
	}

    private void determineResultHelper() {
        if (PortletActionContext.isJSR268Supported()) {
            this.resultHelper = new PortletResultHelperJSR286();
        } else {
            this.resultHelper = new PortletResultHelperJSR168();
        }
    }

	/**
	 * Execute the result. Obtains the
	 * {@link javax.portlet.PortletRequestDispatcher}from the
	 * {@link PortletActionContext}and includes the JSP.
	 *
	 * @see com.opensymphony.xwork2.Result#execute(com.opensymphony.xwork2.ActionInvocation)
	 */
	public void doExecute(String finalLocation, ActionInvocation actionInvocation) throws Exception {

		if (PortletActionContext.isRender() || PortletActionContext.isResource()) {
			executeMimeResult(finalLocation);
		} else if (PortletActionContext.isAction() || PortletActionContext.isEvent()) {
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
        String phase = (PortletActionContext.isEvent()) ? "Event" : "Action";
		if (LOG.isDebugEnabled()) LOG.debug("Executing result in "+phase+" phase");
		Map sessionMap = invocation.getInvocationContext().getSession();
		if (LOG.isDebugEnabled()) LOG.debug("Setting event render parameter: " + finalLocation);
		if (finalLocation.indexOf('?') != -1) {
			convertQueryParamsToRenderParams(finalLocation.substring(finalLocation.indexOf('?') + 1));
			finalLocation = finalLocation.substring(0, finalLocation.indexOf('?'));
		}
        PortletResponse response = PortletActionContext.getResponse();
		if (finalLocation.endsWith(".action")) {
			// View is rendered with a view action...luckily...
			finalLocation = finalLocation.substring(0, finalLocation.lastIndexOf("."));
			resultHelper.setRenderParameter(response, ACTION_PARAM, finalLocation);
		} else {
			// View is rendered outside an action...uh oh...
			resultHelper.setRenderParameter(response, ACTION_PARAM, "renderDirect");
			sessionMap.put(RENDER_DIRECT_LOCATION, finalLocation);
		}
		if(portletMode != null) {
			resultHelper.setPortletMode(response, portletMode);
			resultHelper.setRenderParameter(response, PortletActionConstants.MODE_PARAM, portletMode.toString());
		}
		else {
			resultHelper.setRenderParameter(response, PortletActionConstants.MODE_PARAM, PortletActionContext.getRequest().getPortletMode()
					.toString());
		}
	}

	/**
	 * Converts the query params to render params.
	 *
	 * @param response
	 * @param queryParams
	 */
	protected void convertQueryParamsToRenderParams(String queryParams) {
		StringTokenizer tok = new StringTokenizer(queryParams, "&");
		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			String key = token.substring(0, token.indexOf('='));
			String value = token.substring(token.indexOf('=') + 1);
			resultHelper.setRenderParameter(PortletActionContext.getResponse(), key, value);
		}
	}

    /**
     * Executes the render result.
     *
     * @param finalLocation
     * @throws PortletException
     * @throws IOException
     */
    protected void executeMimeResult(final String finalLocation) throws PortletException, IOException {
        if (LOG.isDebugEnabled()) LOG.debug("Executing mime result");
        PortletContext ctx = PortletActionContext.getPortletContext();
        PortletRequest req = PortletActionContext.getRequest();
        PortletResponse res = PortletActionContext.getResponse();

		if (StringUtils.isNotEmpty(title) && res instanceof RenderResponse) {
		    ((RenderResponse)res).setTitle(title);
		}
        if (LOG.isDebugEnabled()) LOG.debug("Location: " + finalLocation);
        PortletRequestDispatcher dispatcher;
        if (useDispatcherServlet) {
            req.setAttribute(DISPATCH_TO, finalLocation);
            dispatcher = ctx.getNamedDispatcher(dispatcherServletName);
            if(dispatcher == null) {
                throw new PortletException("Could not locate dispatcher servlet \"" + dispatcherServletName + "\". Please configure it in your web.xml file");
            }
        } else {
            dispatcher = ctx.getRequestDispatcher(finalLocation);
            if (dispatcher == null) {
                throw new PortletException("Could not locate dispatcher for '" + finalLocation + "'");
            }
        }
        resultHelper.include( dispatcher, contentType, req, res );
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
