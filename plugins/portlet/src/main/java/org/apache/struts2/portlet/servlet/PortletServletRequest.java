/*
 * $Id: PortletServletRequest.java 590812 2007-10-31 20:32:54Z apetrelli $
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.apache.struts2.portlet.PortletConstants.*;

/**
 * Wrapper object exposing a {@link PortletRequest} as a
 * {@link HttpServletRequest} instance. Clients accessing this request object
 * will in fact operate on the {@link PortletRequest} object wrapped by this
 * request object.
 */
public class PortletServletRequest implements HttpServletRequest {

	private PortletRequest portletRequest;

	private PortletContext portletContext;

	private Map<String, String[]> extraParams;

	public PortletServletRequest(PortletRequest portletRequest,
			PortletContext portletContext) {
		this(portletRequest, portletContext, Collections.EMPTY_MAP);
	}

	public PortletServletRequest(PortletRequest portletRequest,
			PortletContext portletContext, Map<String, String[]> extraParams) {
		this.portletContext = portletContext;
		this.portletRequest = portletRequest;
		this.extraParams = extraParams;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getAuthType()
	 */
	public String getAuthType() {
		return portletRequest.getAuthType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getContextPath()
	 */
	public String getContextPath() {
		return portletRequest.getContextPath();
	}

	/**
	 * Not allowed in a portlet.
	 * 
	 * @throws IllegalStateException
	 *             Not allowed in a portlet.
	 */
	public Cookie[] getCookies() {
		if (portletRequest instanceof HttpServletRequest) {
			return ((HttpServletRequest) portletRequest).getCookies();
		}
		throw new IllegalStateException("Not allowed in a portlet");
	}

	/**
	 * Not allowed in a portlet.
	 * 
	 * @throws IllegalStateException
	 *             Not allowed in a portlet.
	 */
	public long getDateHeader(String name) {
		throw new IllegalStateException("Not allowed in a portlet");
	}

	/**
	 * Gets a property from the {@link PortletRequest}. Note that a
	 * {@link PortletRequest} is not guaranteed to map properties to headers.
	 * 
	 * @see PortletRequest#getProperty(String)
	 * @see javax.servlet.http.HttpServletRequest#getHeader(java.lang.String)
	 */
	public String getHeader(String name) {
		return portletRequest.getProperty(name);
	}

	/**
	 * Gets the property names from the {@link PortletRequest}. Note that a
	 * {@link PortletRequest} is not guaranteed to map properties to headers.
	 * 
	 * @see PortletRequest#getPropertyNames()
	 * @see javax.servlet.http.HttpServletRequest#getHeaderNames()
	 */
	public Enumeration getHeaderNames() {
		return portletRequest.getPropertyNames();
	}

	/**
	 * Gets the values for the specified property from the
	 * {@link PortletRequest}. Note that a {@link PortletRequest} is not
	 * guaranteed to map properties to headers.
	 * 
	 * @see PortletRequest#getProperties(String)
	 * @see HttpServletRequest#getHeaders(String)
	 */
	public Enumeration getHeaders(String name) {
		return portletRequest.getProperties(name);
	}

	/**
	 * Not allowed in a portlet.
	 * 
	 * @throws IllegalStateException
	 *             Not allowed in a portlet.
	 */
	public int getIntHeader(String name) {
		throw new IllegalStateException("Not allowed in a portlet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getMethod()
	 */
	public String getMethod() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getPathInfo()
	 */
	public String getPathInfo() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getPathTranslated()
	 */
	public String getPathTranslated() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getQueryString()
	 */
	public String getQueryString() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getRemoteUser()
	 */
	public String getRemoteUser() {
		return portletRequest.getRemoteUser();
	}

	/**
	 * Not allowed in a portlet.
	 * 
	 * @throws IllegalStateException
	 *             Not allowed in a portlet.
	 */
	public String getRequestURI() {
		throw new IllegalStateException("Not allowed in a portlet");
	}

	/**
	 * Not allowed in a portlet.
	 * 
	 * @throws IllegalStateException
	 *             Not allowed in a portlet.
	 */
	public StringBuffer getRequestURL() {
		throw new IllegalStateException("Not allowed in a portlet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getRequestedSessionId()
	 */
	public String getRequestedSessionId() {
		return portletRequest.getRequestedSessionId();
	}

	/**
	 * A {@link PortletRequest} has no servlet path. But for compatibility with
	 * Struts 2 components and interceptors, the action parameter on the request
	 * is mapped to the servlet path.
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getServletPath()
	 */
	public String getServletPath() {
		String actionPath = getParameter(ACTION_PARAM);
		if (actionPath != null && !actionPath.endsWith(".action")) {
			actionPath += ".action";
		}
		return actionPath;
	}

	/**
	 * Get the {@link PortletSession} as a {@link PortletHttpSession} instance.
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getSession()
	 */
	public HttpSession getSession() {
		return new PortletHttpSession(portletRequest.getPortletSession());
	}

	/**
	 * Get the {@link PortletSession} as a {@link PortletHttpSession} instance.
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getSession(boolean)
	 */
	public HttpSession getSession(boolean create) {
		return new PortletHttpSession(portletRequest.getPortletSession(create));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getUserPrincipal()
	 */
	public Principal getUserPrincipal() {
		return portletRequest.getUserPrincipal();
	}

	/**
	 * Not allowed in a portlet.
	 * 
	 * @throws IllegalStateException
	 *             Not allowed in a portlet.
	 */
	public boolean isRequestedSessionIdFromCookie() {
		throw new IllegalStateException("Not allowed in a portlet");
	}

	/**
	 * Not allowed in a portlet.
	 * 
	 * @throws IllegalStateException
	 *             Not allowed in a portlet.
	 */
	public boolean isRequestedSessionIdFromURL() {
		throw new IllegalStateException("Not allowed in a portlet");
	}

	/**
	 * Not allowed in a portlet.
	 * 
	 * @throws IllegalStateException
	 *             Not allowed in a portlet.
	 */
	public boolean isRequestedSessionIdFromUrl() {
		throw new IllegalStateException("Not allowed in a portlet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdValid()
	 */
	public boolean isRequestedSessionIdValid() {
		return portletRequest.isRequestedSessionIdValid();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#isUserInRole(java.lang.String)
	 */
	public boolean isUserInRole(String role) {
		return portletRequest.isUserInRole(role);
	}

	/**
	 * Gets an attribute value on the {@link PortletRequest}. If the attribute
	 * name is <tt>javax.servlet.include.servlet_path</tt>, it returns the
	 * same as {@link PortletServletRequest#getServletPath()}
	 * 
	 * @see javax.servlet.ServletRequest#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		if ("javax.servlet.include.servlet_path".equals(name)) {
			return getServletPath();
		} else {
			return portletRequest.getAttribute(name);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getAttributeNames()
	 */
	public Enumeration getAttributeNames() {
		return portletRequest.getAttributeNames();
	}

	/**
	 * Can only be invoked in the event phase.
	 * 
	 * @see ServletRequest#getCharacterEncoding()
	 * @throws IllegalStateException
	 *             If the portlet is not in the event phase.
	 */
	public String getCharacterEncoding() {
		if (portletRequest instanceof ActionRequest) {
			return ((ActionRequest) portletRequest).getCharacterEncoding();
		} else {
			throw new IllegalStateException("Not allowed in render phase");
		}
	}

	/**
	 * Can only be invoked in the event phase.
	 * 
	 * @see ServletRequest#getContentLength()
	 * @throws IllegalStateException
	 *             If the portlet is not in the event phase.
	 */
	public int getContentLength() {
		if (portletRequest instanceof ActionRequest) {
			return ((ActionRequest) portletRequest).getContentLength();
		} else {
			throw new IllegalStateException("Not allowed in render phase");
		}
	}

	/**
	 * Can only be invoked in the event phase.
	 * 
	 * @see ServletRequest#getContentType()
	 * @throws IllegalStateException
	 *             If the portlet is not in the event phase.
	 */
	public String getContentType() {
		if (portletRequest instanceof ActionRequest) {
			return ((ActionRequest) portletRequest).getContentType();
		} else {
			throw new IllegalStateException("Not allowed in render phase");
		}
	}

	/**
	 * Can only be invoked in the event phase. When invoked in the event phase,
	 * it will wrap the portlet's {@link InputStream} as a
	 * {@link PortletServletInputStream}.
	 * 
	 * @see ServletRequest#getInputStream()
	 * @throws IllegalStateException
	 *             If the portlet is not in the event phase.
	 */
	public ServletInputStream getInputStream() throws IOException {
		if (portletRequest instanceof ActionRequest) {
			return new PortletServletInputStream(
					((ActionRequest) portletRequest).getPortletInputStream());
		} else {
			throw new IllegalStateException("Not allowed in render phase");
		}
	}

	/**
	 * Not allowed in a portlet.
	 * 
	 * @throws IllegalStateException
	 *             Not allowed in a portlet.
	 */
	public String getLocalAddr() {
		if (portletRequest instanceof HttpServletRequest) {
			return ((HttpServletRequest) portletRequest).getLocalAddr();
		}
		throw new IllegalStateException("Not allowed in a portlet");
	}

	/**
	 * Not allowed in a portlet.
	 * 
	 * @throws IllegalStateException
	 *             Not allowed in a portlet.
	 */
	public String getLocalName() {
		if (portletRequest instanceof HttpServletRequest) {
			return ((HttpServletRequest) portletRequest).getLocalName();
		}
		throw new IllegalStateException("Not allowed in a portlet");
	}

	/**
	 * Not allowed in a portlet.
	 * 
	 * @throws IllegalStateException
	 *             Not allowed in a portlet.
	 */
	public int getLocalPort() {
		if (portletRequest instanceof HttpServletRequest) {
			return ((HttpServletRequest) portletRequest).getLocalPort();
		}
		throw new IllegalStateException("Not allowed in a portlet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getLocale()
	 */
	public Locale getLocale() {
		return portletRequest.getLocale();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getLocales()
	 */
	public Enumeration getLocales() {
		return portletRequest.getLocales();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getParameter(java.lang.String)
	 */
	public String getParameter(String name) {
		// Check if the parameter is overriden in the extra params
		if (extraParams.containsKey(name)) {
			String[] values = extraParams.get(name);
			if (values != null && values.length > 0) {
				return values[0];
			}
		}
		return portletRequest.getParameter(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getParameterMap()
	 */
	public Map getParameterMap() {
		return portletRequest.getParameterMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getParameterNames()
	 */
	public Enumeration getParameterNames() {
		return portletRequest.getParameterNames();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getParameterValues(java.lang.String)
	 */
	public String[] getParameterValues(String name) {
		return portletRequest.getParameterValues(name);
	}

	/**
	 * Not allowed in a portlet.
	 * 
	 * @throws IllegalStateException
	 *             Not allowed in a portlet.
	 */
	public String getProtocol() {
		if (portletRequest instanceof HttpServletRequest) {
			return ((HttpServletRequest) portletRequest).getProtocol();
		}
		throw new IllegalStateException("Not allowed in a portlet");
	}

	/**
	 * Can only be invoked in the event phase.
	 * 
	 * @see ServletRequest#getReader()
	 * @throws IllegalStateException
	 *             If the portlet is not in the event phase.
	 */
	public BufferedReader getReader() throws IOException {
		if (portletRequest instanceof ActionRequest) {
			return ((ActionRequest) portletRequest).getReader();
		} else {
			throw new IllegalStateException("Not allowed in render phase");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getRealPath(java.lang.String)
	 */
	public String getRealPath(String path) {
		return portletContext.getRealPath(path);
	}

	/**
	 * Not allowed in a portlet.
	 * 
	 * @throws IllegalStateException
	 *             Not allowed in a portlet.
	 */
	public String getRemoteAddr() {
		if (portletRequest instanceof HttpServletRequest) {
			return ((HttpServletRequest) portletRequest).getRemoteAddr();
		}
		throw new IllegalStateException("Not allowed in a portlet");
	}

	/**
	 * Not allowed in a portlet.
	 * 
	 * @throws IllegalStateException
	 *             Not allowed in a portlet.
	 */
	public String getRemoteHost() {
		if (portletRequest instanceof HttpServletRequest) {
			return ((HttpServletRequest) portletRequest).getRemoteHost();
		}
		throw new IllegalStateException("Not allowed in a portlet");
	}

	/**
	 * Not allowed in a portlet.
	 * 
	 * @throws IllegalStateException
	 *             Not allowed in a portlet.
	 */
	public int getRemotePort() {
		if (portletRequest instanceof HttpServletRequest) {
			return ((HttpServletRequest) portletRequest).getRemotePort();
		}
		throw new IllegalStateException("Not allowed in a portlet");
	}

	/**
	 * Get the {@link PortletRequestDispatcher} as a
	 * {@link PortletServletRequestDispatcher} instance.
	 * 
	 * @see javax.servlet.ServletRequest#getRequestDispatcher(java.lang.String)
	 */
	public RequestDispatcher getRequestDispatcher(String path) {
		return new PortletServletRequestDispatcher(portletContext
				.getRequestDispatcher(path));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getScheme()
	 */
	public String getScheme() {
		return portletRequest.getScheme();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getServerName()
	 */
	public String getServerName() {
		return portletRequest.getServerName();
	}

	/**
	 * Not allowed in a portlet.
	 * 
	 * @throws IllegalStateException
	 *             Not allowed in a portlet.
	 */
	public int getServerPort() {
		if (portletRequest instanceof HttpServletRequest) {
			return ((HttpServletRequest) portletRequest).getServerPort();
		}
		throw new IllegalStateException("Not allowed in a portlet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#isSecure()
	 */
	public boolean isSecure() {
		return portletRequest.isSecure();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String name) {
		portletRequest.removeAttribute(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#setAttribute(java.lang.String,
	 *      java.lang.Object)
	 */
	public void setAttribute(String name, Object o) {
		portletRequest.setAttribute(name, o);
	}

	/**
	 * Can only be invoked in the event phase.
	 * 
	 * @see ServletRequest#setCharacterEncoding(String)
	 * @throws IllegalStateException
	 *             If the portlet is not in the event phase.
	 */
	public void setCharacterEncoding(String env)
			throws UnsupportedEncodingException {
		if (portletRequest instanceof ActionRequest) {
			((ActionRequest) portletRequest).setCharacterEncoding(env);
		} else {
			throw new IllegalStateException("Not allowed in render phase");
		}
	}

	/**
	 * Get the wrapped {@link PortletRequest} instance.
	 * 
	 * @return The wrapped {@link PortletRequest} instance.
	 */
	public PortletRequest getPortletRequest() {
		return portletRequest;
	}
}
