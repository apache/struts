/*
 * $Id: $
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
package org.apache.struts2.portlet;

import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortalContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.WindowState;

public class MockPortletRequest implements PortletRequest {

	private Map<String, String[]> params;
	private Map<String, Object> requestAttributes;
	private Map<String, Object>	session;
	
	public MockPortletRequest(Map<String, String[]> params, Map<String, Object> requestAttributes, Map<String, Object> session) {
		this.params = params;
		this.requestAttributes = requestAttributes;
		this.session = session;
	}
	
	public Object getAttribute(String arg0) {
		return requestAttributes.get(arg0);
	}

	public Enumeration getAttributeNames() {
		return Collections.enumeration(new HashMap<String, Object>(requestAttributes).keySet());
	}

	public String getAuthType() {
		return null;
	}

	public String getContextPath() {
		return null;
	}

	public Locale getLocale() {
		return null;
	}

	public Enumeration getLocales() {
		return null;
	}

	public String getParameter(String arg0) {
		return params.get(arg0)[0];
	}

	public Map getParameterMap() {
		return params;
	}

	public Enumeration getParameterNames() {
		return Collections.enumeration(new HashMap<String, String[]>(params).keySet());
	}

	public String[] getParameterValues(String arg0) {
		return params.get(arg0);
	}

	public PortalContext getPortalContext() {
		return null;
	}

	public PortletMode getPortletMode() {
		return null;
	}

	public PortletSession getPortletSession() {
		return getPortletSession(true);
	}

	public PortletSession getPortletSession(boolean arg0) {
		return new MockPortletSession(session);
	}

	public PortletPreferences getPreferences() {
		return null;
	}

	public Enumeration getProperties(String arg0) {
		return null;
	}

	public String getProperty(String arg0) {
		return null;
	}

	public Enumeration getPropertyNames() {
		return null;
	}

	public String getRemoteUser() {
		return null;
	}

	public String getRequestedSessionId() {
		return null;
	}

	public String getResponseContentType() {
		return null;
	}

	public Enumeration getResponseContentTypes() {		
		return null;
	}

	public String getScheme() {
		return null;
	}

	public String getServerName() {
		return null;
	}

	public int getServerPort() {
		return 0;
	}

	public Principal getUserPrincipal() {
		return null;
	}

	public WindowState getWindowState() {
		return null;
	}

	public boolean isPortletModeAllowed(PortletMode arg0) {
		return false;
	}

	public boolean isRequestedSessionIdValid() {
		return false;
	}

	public boolean isSecure() {
		return false;
	}

	public boolean isUserInRole(String arg0) {
		return false;
	}

	public boolean isWindowStateAllowed(WindowState arg0) {
		return false;
	}

	public void removeAttribute(String arg0) {	
		requestAttributes.remove(arg0);
	}

	public void setAttribute(String arg0, Object arg1) {
		requestAttributes.put(arg0, arg1);
	}

}