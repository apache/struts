/*
 * $Id: PortletServletConfig.java 590812 2007-10-31 20:32:54Z apetrelli $
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
package org.apache.struts2.portlet.servlet;

import java.util.Enumeration;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * Wrapper object exposing a {@link PortletConfig} as a {@link ServletConfig} instance.
 * Clients accessing this config object will in fact operate on the
 * {@link PortletConfig} object wrapped by this config object.
 */
public class PortletServletConfig implements ServletConfig {

	private PortletConfig portletConfig;
	
	public PortletServletConfig(PortletConfig portletConfig) {
		this.portletConfig = portletConfig;
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getInitParameter(java.lang.String)
	 */
	public String getInitParameter(String name) {
		return portletConfig.getInitParameter(name);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getInitParameterNames()
	 */
	public Enumeration getInitParameterNames() {
		return portletConfig.getInitParameterNames();
	}

	/**
	 * Get the {@link PortletContext} as a {@link PortletServletContext} instance.
	 * @see javax.servlet.ServletConfig#getServletContext()
	 */
	public ServletContext getServletContext() {
		return new PortletServletContext(portletConfig.getPortletContext());
	}

	/**
	 * Will return the portlet name.
	 * @see javax.servlet.ServletConfig#getServletName()
	 */
	public String getServletName() {
		return portletConfig.getPortletName();
	}
	
	/**
	 * Get the wrapped {@link PortletConfig} instance.
	 * @return The wrapped {@link PortletConfig} instance.
	 */
	public PortletConfig getPortletConfig() {
		return portletConfig;
	}

}
