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
package org.apache.struts2.osgi.interceptor;

import org.easymock.EasyMock;
import org.apache.struts2.osgi.host.OsgiHost;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import javax.servlet.ServletContext;

import com.opensymphony.xwork2.ActionInvocation;
import junit.framework.TestCase;

import java.util.List;

public class OsgiInterceptorTest extends TestCase {
    public void testBundleContextAware() throws Exception {
        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        BundleContext bundleContext = EasyMock.createStrictMock(BundleContext.class);
        ActionInvocation actionInvocation = EasyMock.createStrictMock(ActionInvocation.class);
        BundleContextAware bundleContextAware = EasyMock.createStrictMock(BundleContextAware.class);

        EasyMock.expect(servletContext.getAttribute(OsgiHost.OSGI_BUNDLE_CONTEXT)).andReturn(bundleContext);
        EasyMock.expect(actionInvocation.getAction()).andReturn(bundleContextAware);
        bundleContextAware.setBundleContext(bundleContext);
        EasyMock.expect(actionInvocation.invoke()).andReturn("");

        EasyMock.replay(bundleContextAware);
        EasyMock.replay(servletContext);
        EasyMock.replay(actionInvocation);

        OsgiInterceptor osgiInterceptor = new OsgiInterceptor();
        osgiInterceptor.setServletContext(servletContext);
        osgiInterceptor.intercept(actionInvocation);

        EasyMock.verify(bundleContextAware);
    }

     public void testBundleContextAwareNegative() throws Exception {
        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        ActionInvocation actionInvocation = EasyMock.createStrictMock(ActionInvocation.class);
        BundleContextAware bundleContextAware = EasyMock.createStrictMock(BundleContextAware.class);

        EasyMock.expect(servletContext.getAttribute(OsgiHost.OSGI_BUNDLE_CONTEXT)).andReturn(null);
        EasyMock.expect(actionInvocation.invoke()).andReturn("");

        EasyMock.replay(bundleContextAware);
        EasyMock.replay(servletContext);
        EasyMock.replay(actionInvocation);

        OsgiInterceptor osgiInterceptor = new OsgiInterceptor();
        osgiInterceptor.setServletContext(servletContext);
        osgiInterceptor.intercept(actionInvocation);

        EasyMock.verify(bundleContextAware);
    }

    public void testServiceAware() throws Exception {
        ServletContext servletContext = EasyMock.createStrictMock(ServletContext.class);
        BundleContext bundleContext = EasyMock.createStrictMock(BundleContext.class);
        ActionInvocation actionInvocation = EasyMock.createStrictMock(ActionInvocation.class);
        SomeAction someAction = new SomeAction();

        //service refs
        ServiceReference objectRef = EasyMock.createNiceMock(ServiceReference.class);
        Object someObject = new Object();

        EasyMock.expect(servletContext.getAttribute(OsgiHost.OSGI_BUNDLE_CONTEXT)).andReturn(bundleContext);
        EasyMock.expect(actionInvocation.getAction()).andReturn(someAction);
        EasyMock.expect(actionInvocation.invoke()).andReturn("");
        EasyMock.expect(bundleContext.getAllServiceReferences(Object.class.getName(), null)).andReturn(new ServiceReference[] {objectRef});
        EasyMock.expect(bundleContext.getService(objectRef)).andReturn(someObject);

        EasyMock.replay(bundleContext);
        EasyMock.replay(servletContext);
        EasyMock.replay(actionInvocation);

        OsgiInterceptor osgiInterceptor = new OsgiInterceptor();
        osgiInterceptor.setServletContext(servletContext);
        osgiInterceptor.intercept(actionInvocation);

        List<Object> objects = someAction.getServices();
        assertNotNull(objects);
        assertSame(someObject, objects.get(0));
    }
}
