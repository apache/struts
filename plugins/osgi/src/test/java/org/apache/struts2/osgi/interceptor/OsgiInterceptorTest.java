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
