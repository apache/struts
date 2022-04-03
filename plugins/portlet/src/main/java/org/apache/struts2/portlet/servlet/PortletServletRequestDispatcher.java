/*
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
package org.apache.struts2.portlet.servlet;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class PortletServletRequestDispatcher implements RequestDispatcher {

	private PortletRequestDispatcher portletRequestDispatcher;
	
	public PortletServletRequestDispatcher(PortletRequestDispatcher portletRequestDispatcher) {
		this.portletRequestDispatcher = portletRequestDispatcher;
	}

	public void forward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		throw new IllegalStateException("Not allowed in a portlet");
		
	}

	public void include(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		if(request instanceof PortletServletRequest && response instanceof PortletServletResponse) {
			PortletRequest req = ((PortletServletRequest)request).getPortletRequest();
			PortletResponse resp = ((PortletServletResponse)response).getPortletResponse();
			if(req instanceof RenderRequest && resp instanceof RenderResponse) {
				try {
					portletRequestDispatcher.include((RenderRequest)req, (RenderResponse)resp);
				}
				catch(PortletException e) {
					throw new ServletException(e);
				}
			}
			else {
				throw new IllegalStateException("Can only be invoked in the render phase");
			}
		}
		else {
			throw new IllegalStateException("Can only be invoked in a portlet");
		}
	}

}
