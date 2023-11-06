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
package org.apache.struts2.views.jsp;

import java.util.HashMap;
import java.util.Map;

import org.springframework.mock.web.MockJspWriter;
import org.springframework.mock.web.MockPageContext;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspWriter;


/**
 */
public class StrutsMockPageContext extends MockPageContext {

    private Map attributes = new HashMap();
    
    private JspWriter smpcOut = null;
    
    public StrutsMockPageContext() { }
    
    public StrutsMockPageContext(ServletContext context, HttpServletRequest request, HttpServletResponse response) {
    	super(context, request, response);
    }


    public void setAttribute(String s, Object o) {
        if ((s == null) || (o == null)) {
            throw new NullPointerException("PageContext does not accept null attributes");
        }

        this.attributes.put(s, o);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public Object getAttributes(String key) {
        return this.attributes.get(key);
    }

    public HttpSession getSession() {
        HttpSession session = super.getSession();

        if (session == null) {
            session = ((HttpServletRequest) getRequest()).getSession(true);
        }

        return session;
    }
    
    @Override
	public JspWriter getOut() {
		if (this.smpcOut == null) {
			this.smpcOut = new StrutsMockJspWriter();
		}
		return this.smpcOut;
	}
    
    public void setJspWriter(JspWriter w) {
    	this.smpcOut = w;
    }

    public Object findAttribute(String s) {
        return attributes.get(s);
    }

    public void removeAttribute(String key) {
        this.attributes.remove(key);
    }
}
