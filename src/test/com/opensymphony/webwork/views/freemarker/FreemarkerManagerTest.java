/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.freemarker;

import com.mockobjects.servlet.MockServletContext;
import com.opensymphony.webwork.WebWorkConstants;
import com.opensymphony.webwork.config.Configuration;
import com.opensymphony.webwork.views.jsp.WebWorkMockServletContext;

import junit.framework.TestCase;

/**
 * Test case for FreemarkerManager 
 * 
 * @author tm_jee
 * @version $Date: 2006/02/16 14:13:05 $ $Id: FreemarkerManagerTest.java,v 1.1 2006/02/16 14:13:05 tmjee Exp $
 */
public class FreemarkerManagerTest extends TestCase {
	
	public void testIfWebworkEncodingIsSetProperty() throws Exception {
		Configuration.set(WebWorkConstants.WEBWORK_I18N_ENCODING, "UTF-8");
		WebWorkMockServletContext servletContext = new WebWorkMockServletContext();
		servletContext.setAttribute(FreemarkerManager.CONFIG_SERVLET_CONTEXT_KEY, null);
		freemarker.template.Configuration conf = FreemarkerManager.getInstance().getConfiguration(servletContext);
		assertEquals(conf.getDefaultEncoding(), "UTF-8");
	}
}
