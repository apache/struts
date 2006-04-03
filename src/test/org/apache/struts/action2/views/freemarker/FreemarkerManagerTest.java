/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.freemarker;

import org.apache.struts.action2.StrutsConstants;
import org.apache.struts.action2.config.Configuration;
import org.apache.struts.action2.views.jsp.StrutsMockServletContext;

import junit.framework.TestCase;

/**
 * Test case for FreemarkerManager 
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class FreemarkerManagerTest extends TestCase {
	
	public void testIfStrutsEncodingIsSetProperty() throws Exception {
		Configuration.set(StrutsConstants.STRUTS_I18N_ENCODING, "UTF-8");
		StrutsMockServletContext servletContext = new StrutsMockServletContext();
		servletContext.setAttribute(FreemarkerManager.CONFIG_SERVLET_CONTEXT_KEY, null);
		freemarker.template.Configuration conf = FreemarkerManager.getInstance().getConfiguration(servletContext);
		assertEquals(conf.getDefaultEncoding(), "UTF-8");
	}
}
