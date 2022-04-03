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
package org.apache.struts2;

import javax.servlet.ServletContext;

/**
 * <p>
 * Constants used internally by Struts. Do not use these constants directly,
 * instead use exposed helpers eg.: {@link org.apache.struts2.dispatcher.Dispatcher#getInstance(ServletContext)}
 * </p>
 */
public interface StrutsStatics {

    /**
     * Constant for the HTTP request object.
     */
    String HTTP_REQUEST = "com.opensymphony.xwork2.dispatcher.HttpServletRequest";

    /**
     * Constant for the HTTP response object.
     */
    String HTTP_RESPONSE = "com.opensymphony.xwork2.dispatcher.HttpServletResponse";

    /**
     * Constant for an HTTP {@link javax.servlet.RequestDispatcher request dispatcher}.
     */
    String SERVLET_DISPATCHER = "com.opensymphony.xwork2.dispatcher.ServletDispatcher";

    /**
     * Constant for the {@link javax.servlet.ServletContext servlet context} object.
     */
    String SERVLET_CONTEXT = "com.opensymphony.xwork2.dispatcher.ServletContext";

    /**
     * Constant for the JSP {@link javax.servlet.jsp.PageContext page context}.
     */
    String PAGE_CONTEXT = "com.opensymphony.xwork2.dispatcher.PageContext";

    /**
     * Constant for the PortletContext object
     */
    String STRUTS_PORTLET_CONTEXT = "struts.portlet.context";

    /**
     * Set as an attribute in the request to let other parts of the framework know that the invocation is happening inside an
     * action tag
     */
    String STRUTS_ACTION_TAG_INVOCATION = "struts.actiontag.invocation";

    String ACTION_MAPPING = "struts.actionMapping";
}
