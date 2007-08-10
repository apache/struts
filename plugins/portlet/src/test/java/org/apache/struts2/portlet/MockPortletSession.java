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

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.portlet.PortletContext;
import javax.portlet.PortletSession;

public class MockPortletSession implements PortletSession {

	private Map<String, Object> session;
	
	public MockPortletSession(Map<String, Object> session) {
		this.session = session;
	}
	
	public Object getAttribute(String arg0) {
		return session.get(arg0);
	}

	public Object getAttribute(String arg0, int arg1) {
		return session.get(arg0);
	}

	public Enumeration getAttributeNames() {
		return Collections.enumeration(session.keySet());
	}

	public Enumeration getAttributeNames(int arg0) {
		return Collections.enumeration(session.keySet());
	}

	public long getCreationTime() {
		return 0;
	}

	public String getId() {
		return null;
	}

	public long getLastAccessedTime() {
		return 0;
	}

	public int getMaxInactiveInterval() {
		return 0;
	}

	public PortletContext getPortletContext() {
		return null;
	}

	public void invalidate() {
		session.clear();
	}

	public boolean isNew() {
		return false;
	}

	public void removeAttribute(String arg0) {
		session.remove(arg0);
	}

	public void removeAttribute(String arg0, int arg1) {
		session.remove(arg0);
	}

	public void setAttribute(String arg0, Object arg1) {
		session.put(arg0, arg1);
	}

	public void setAttribute(String arg0, Object arg1, int arg2) {
		session.put(arg0, arg1);
	}

	public void setMaxInactiveInterval(int arg0) {
		
	}

}
