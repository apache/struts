/*
 * Created on Mar 21, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.apache.struts.webwork.portlet.context;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.easymock.MockControl;

import junit.framework.TestCase;

/**
 * @author Nils-Helge Garli
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ServletContextHolderListenerTest extends TestCase {

    public void testContextInitialized() {
        MockControl mockContext = MockControl.createNiceControl(ServletContext.class);
        ServletContext context = (ServletContext)mockContext.getMock();
        ServletContextEvent event = new ServletContextEvent(context);
        ServletContextHolderListener listener = new ServletContextHolderListener();
        listener.contextInitialized(event);
        assertSame(ServletContextHolderListener.getServletContext(), context);
    }

}
