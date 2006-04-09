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
package org.apache.struts.action2.pico;

import org.apache.struts.action2.dispatcher.FilterDispatcher;
import com.opensymphony.xwork.ObjectFactory;
import org.nanocontainer.nanowar.ServletRequestContainerLauncher;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.gems.adapters.ThreadLocalReference;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 * User: patrick Date: Dec 22, 2005 Time: 4:14:10 PM
 */
public class PicoFilterDispatcher extends FilterDispatcher {
    private ObjectReference objectReference;

    public void init(FilterConfig config) throws ServletException {
        objectReference = new ThreadLocalReference();
        ObjectFactory.setObjectFactory(new PicoObjectFactory(objectReference));
        super.init(filterConfig);
    }

    protected Object beforeActionInvocation(HttpServletRequest request, ServletContext servletContext) {
        objectReference.set(request);

        ServletRequestContainerLauncher containerLauncher = new ServletRequestContainerLauncher(servletContext, request);
        try {
            containerLauncher.startContainer();
        } catch (ServletException e) {
            throw new RuntimeException("Could not start pico container", e);
        }

        return containerLauncher;
    }

    protected void afterActionInvocation(HttpServletRequest request, Object o, Object o1) {
        ServletRequestContainerLauncher containerLauncher = (ServletRequestContainerLauncher) o;
        containerLauncher.killContainer();

        objectReference.set(null);
    }
}
