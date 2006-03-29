/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.freemarker;

import com.mockobjects.servlet.MockServletContext;
import org.apache.struts.action2.StrutsConstants;
import org.apache.struts.action2.config.Configuration;
import org.apache.struts.action2.views.jsp.StrutsMockServletContext;

import junit.framework.TestCase;

/**
 * Test case for FreemarkerManager 
 * 
 * @author tm_jee
 * @version $Date: 2006/02/16 14:13:05 $ $Id: FreemarkerManagerTest.java,v 1.1 2006/02/16 14:13:05 tmjee Exp $
 */
public class FreemarkerManagerTest extends TestCase {
	
	public void testIfWebworkEncodingIsSetProperty() throws Exception {
		Configuration.set(StrutsConstants.STRUTS_I18N_ENCODING, "UTF-8");
		StrutsMockServletContext servletContext = new StrutsMockServletContext();
		servletContext.setAttribute(FreemarkerManager.CONFIG_SERVLET_CONTEXT_KEY, null);
		freemarker.template.Configuration conf = FreemarkerManager.getInstance().getConfiguration(servletContext);
		assertEquals(conf.getDefaultEncoding(), "UTF-8");
	}
}
