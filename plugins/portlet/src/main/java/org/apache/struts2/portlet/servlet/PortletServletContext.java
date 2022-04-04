/*
 * $Id: PortletServletContext.java 590812 2007-10-31 20:32:54Z apetrelli $
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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequestDispatcher;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Wrapper object exposing a {@link PortletContext} as a {@link ServletContext} instance.
 * Clients accessing this context object will in fact operate on the
 * {@link PortletContext} object wrapped by this context object.
 */
public class PortletServletContext implements ServletContext {

	private PortletContext portletContext;
	
	public PortletServletContext(PortletContext portletContext) {
		this.portletContext = portletContext;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name) {
		return portletContext.getAttribute(name);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getAttributeNames()
	 */
	public Enumeration getAttributeNames() {
		return portletContext.getAttributeNames();
	}

	/**
	 * @see javax.servlet.ServletContext#getContext(java.lang.String)
	 * @throws IllegalStateException Not supported in a portlet.
	 */
	public ServletContext getContext(String uripath) {
		throw new IllegalStateException("Not supported in a portlet");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getInitParameter(java.lang.String)
	 */
	public String getInitParameter(String name) {
		return portletContext.getInitParameter(name);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getInitParameterNames()
	 */
	public Enumeration getInitParameterNames() {
		return portletContext.getInitParameterNames();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getMajorVersion()
	 */
	public int getMajorVersion() {
		return portletContext.getMajorVersion();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getMimeType(java.lang.String)
	 */
	public String getMimeType(String file) {
		return portletContext.getMimeType(file);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getMinorVersion()
	 */
	public int getMinorVersion() {
		return portletContext.getMinorVersion();
	}

	/**
	 * Returns a {@link PortletServletRequestDispatcher} wrapping the {@link PortletRequestDispatcher}
	 * as a {@link RequestDispatcher} instance.
	 * @see javax.servlet.ServletContext#getNamedDispatcher(java.lang.String)
	 * @return PortletServletRequestDispatcher
	 */
	public RequestDispatcher getNamedDispatcher(String name) {
		return new PortletServletRequestDispatcher(portletContext.getNamedDispatcher(name));
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getRealPath(java.lang.String)
	 */
	public String getRealPath(String path) {
		return portletContext.getRealPath(path);
	}

	/**
	 * Returns a {@link PortletServletRequestDispatcher} wrapping the {@link PortletRequestDispatcher}
	 * as a {@link RequestDispatcher} instance.
	 * @see javax.servlet.ServletContext#getNamedDispatcher(java.lang.String)
	 * @return PortletServletRequestDispatcher
	 */
	public RequestDispatcher getRequestDispatcher(String path) {
		return new PortletServletRequestDispatcher(portletContext.getRequestDispatcher(path));
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getResource(java.lang.String)
	 */
	public URL getResource(String path) throws MalformedURLException {
		return portletContext.getResource(path);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getResourceAsStream(java.lang.String)
	 */
	public InputStream getResourceAsStream(String path) {
		return portletContext.getResourceAsStream(path);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getResourcePaths(java.lang.String)
	 */
	public Set getResourcePaths(String path) {
		return portletContext.getResourcePaths(path);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getServerInfo()
	 */
	public String getServerInfo() {
		return portletContext.getServerInfo();
	}

	/**
	 * @see javax.servlet.ServletContext#getServlet(java.lang.String)
	 * @throws IllegalStateException Not supported in a portlet.
	 */
	public Servlet getServlet(String name) throws ServletException {
		throw new IllegalStateException("Not allowed in a portlet");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getServletContextName()
	 */
	public String getServletContextName() {
		return portletContext.getPortletContextName();
	}

	/**
	 * @see javax.servlet.ServletContext#getServletNames()
 	 * @throws IllegalStateException Not supported in a portlet.
	 */
	public Enumeration getServletNames() {
		throw new IllegalStateException("Not allowed in a portlet");
	}

	/**
	 * @see javax.servlet.ServletContext#getServlets()
	 * @throws IllegalStateException Not supported in a portlet.
	 */
	public Enumeration getServlets() {
		throw new IllegalStateException("Not allowed in a portlet");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#log(java.lang.String)
	 */
	public void log(String msg) {
		portletContext.log(msg);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#log(java.lang.Exception, java.lang.String)
	 */
	public void log(Exception exception, String msg) {
		log(msg, exception);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#log(java.lang.String, java.lang.Throwable)
	 */
	public void log(String message, Throwable throwable) {
		portletContext.log(message, throwable);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String name) {
		portletContext.removeAttribute(name);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String name, Object object) {
		portletContext.setAttribute(name, object);
	}
	
	/**
	 * Get the wrapped {@link PortletContext} instance.
	 * @return The wrapped {@link PortletContext} instance.
	 */
	public PortletContext getPortletContext() {
		return portletContext;
	}

}
