/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.action2.lifecycle;

import org.apache.struts.action2.config.ServletContextSingleton;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

/**
 * @author Scott N. Smith scottnelsonsmith@yahoo.com
 * @version $Id$
 * @deprecated XWork IoC has been deprecated in favor of Spring.
 *             Please refer to the Spring-Struts integration documentation for more info.
 */
public class SessionLifecycleListenerCompatWeblogic61
        extends SessionLifecycleListener {
    /**
     * This is needed by Weblogic Server 6.1 because it
     * uses a slightly obsolete Servlet 2.3-minus spec.
     * In this obsolete spec, the servlet context is not
     * available from the web session object, and so
     * it is retrieved from a special singleton whose sole
     * purpose is to hold the servlet context for this listenter.
     *
     * @param session the HTTP session.  Here is it not used.
     * @return the servlet context
     * @see ServletContextSingleton
     * @see SessionLifecycleListener#getServletContext(javax.servlet.http.HttpSession)
     */
    protected ServletContext getServletContext(HttpSession session) {
        return ServletContextSingleton.getInstance().getServletContext();
    }
}
