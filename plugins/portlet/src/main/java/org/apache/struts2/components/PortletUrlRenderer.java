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

package org.apache.struts2.components;

import java.io.IOException;
import java.io.Writer;

import org.apache.struts2.StrutsException;
import org.apache.struts2.portlet.context.PortletActionContext;
import org.apache.struts2.portlet.util.PortletUrlHelper;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.TextUtils;

/**
 * Implementation of the {@link UrlRenderer} interface that renders URLs for portlet environments.
 * 
 * @see UrlRenderer
 *
 */
public class PortletUrlRenderer implements UrlRenderer {
	
	/**
	 * The servlet renderer used when not executing in a portlet context.
	 */
	private UrlRenderer servletRenderer = null;
	
	public PortletUrlRenderer() {
		this.servletRenderer = new ServletUrlRenderer();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void renderUrl(Writer writer, URL urlComponent) {
		if(PortletActionContext.getPortletContext() == null || "none".equalsIgnoreCase(urlComponent.portletUrlType)) {
			servletRenderer.renderUrl(writer, urlComponent);
		}
		else {
			String action = null;
			if(urlComponent.action != null) {
				action = urlComponent.findString(urlComponent.action);
			}
			String scheme = urlComponent.req.getScheme();

			if (urlComponent.scheme != null) {
				scheme = urlComponent.scheme;
			}

			String result;
			urlComponent.namespace = urlComponent.determineNamespace(urlComponent.namespace, urlComponent.stack, urlComponent.req);
			if (onlyActionSpecified(urlComponent)) {
				result = PortletUrlHelper.buildUrl(action, urlComponent.namespace, urlComponent.method, urlComponent.parameters, urlComponent.portletUrlType, urlComponent.portletMode, urlComponent.windowState);
			} else if(onlyValueSpecified(urlComponent)){
				result = PortletUrlHelper.buildResourceUrl(urlComponent.value, urlComponent.parameters);
			}
			else {
				result = createDefaultUrl(urlComponent);
			}
			if ( urlComponent.anchor != null && urlComponent.anchor.length() > 0 ) {
				result += '#' + urlComponent.findString(urlComponent.anchor);
			}

			String var = urlComponent.getVar();

			if (var != null) {
				urlComponent.putInContext(result);

				// add to the request and page scopes as well
				urlComponent.req.setAttribute(var, result);
			} else {
				try {
					writer.write(result);
				} catch (IOException e) {
					throw new StrutsException("IOError: " + e.getMessage(), e);
				}
			}
		}
	}

	private String createDefaultUrl(URL urlComponent) {
		String result;
		ActionInvocation ai = (ActionInvocation)urlComponent.getStack().getContext().get(
				ActionContext.ACTION_INVOCATION);
		String action = ai.getProxy().getActionName();
		result = PortletUrlHelper.buildUrl(action, urlComponent.namespace, urlComponent.method, urlComponent.parameters, urlComponent.portletUrlType, urlComponent.portletMode, urlComponent.windowState);
		return result;
	}

	private boolean onlyValueSpecified(URL urlComponent) {
		return urlComponent.value != null && urlComponent.action == null;
	}

	private boolean onlyActionSpecified(URL urlComponent) {
		return urlComponent.value == null && urlComponent.action != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void renderFormUrl(Form formComponent) {
		if(PortletActionContext.getPortletContext() == null) {
			servletRenderer.renderFormUrl(formComponent);
		}
		else {
			String namespace = formComponent.determineNamespace(formComponent.namespace, formComponent.getStack(),
					formComponent.request);
			String action = null;
			if (formComponent.action != null) {
				action = formComponent.findString(formComponent.action);
			}
			else {
				ActionInvocation ai = (ActionInvocation) formComponent.getStack().getContext().get(ActionContext.ACTION_INVOCATION);
				action = ai.getProxy().getActionName();
			}
			String type = "action";
			if (TextUtils.stringSet(formComponent.method)) {
				if ("GET".equalsIgnoreCase(formComponent.method.trim())) {
					type = "render";
				}
			}
			if (action != null) {
				String result = PortletUrlHelper.buildUrl(action, namespace, null,
						formComponent.getParameters(), type, formComponent.portletMode, formComponent.windowState);
				formComponent.addParameter("action", result);


				// name/id: cut out anything between / and . should be the id and
				// name
				String id = formComponent.getId();
				if (id == null) {
					int slash = action.lastIndexOf('/');
					int dot = action.indexOf('.', slash);
					if (dot != -1) {
						id = action.substring(slash + 1, dot);
					} else {
						id = action.substring(slash + 1);
					}
					formComponent.addParameter("id", formComponent.escape(id));
				}
			}
		}
		
	}

	public void beforeRenderUrl(URL urlComponent) {
		if(PortletActionContext.getPortletContext() == null) {
			servletRenderer.beforeRenderUrl(urlComponent);
		}
	}

	public void setServletRenderer(UrlRenderer nonPortletRenderer) {
		this.servletRenderer = nonPortletRenderer;
		
	}

}
