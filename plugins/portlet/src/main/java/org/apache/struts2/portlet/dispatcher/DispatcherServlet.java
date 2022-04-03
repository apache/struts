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
package org.apache.struts2.portlet.dispatcher;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.StrutsRequestWrapper;
import org.apache.struts2.portlet.PortletConstants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = -266147033645951967L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String dispatchTo = (String) request.getAttribute(PortletConstants.DISPATCH_TO);
		HttpServletRequest wrapper = wrapRequestIfNecessary(request);
		if(StringUtils.isNotEmpty(dispatchTo)) {
			request.getRequestDispatcher(dispatchTo).include(wrapper, response);
		}
	}

	private HttpServletRequest wrapRequestIfNecessary(HttpServletRequest request) {
		if(!(request instanceof StrutsRequestWrapper)) {
			return new StrutsRequestWrapper(request);
		}
		else {
			return request;
		}
	}

}
