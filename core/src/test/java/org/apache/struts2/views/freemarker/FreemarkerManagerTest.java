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
package org.apache.struts2.views.freemarker;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.config.Configuration;
import org.apache.struts2.views.jsp.StrutsMockServletContext;

/**
 * Test case for FreemarkerManager 
 * 
 */
public class FreemarkerManagerTest extends StrutsTestCase {
	
	public void testIfStrutsEncodingIsSetProperty() throws Exception {
		Configuration.set(StrutsConstants.STRUTS_I18N_ENCODING, "UTF-8");
		StrutsMockServletContext servletContext = new StrutsMockServletContext();
		servletContext.setAttribute(FreemarkerManager.CONFIG_SERVLET_CONTEXT_KEY, null);
		freemarker.template.Configuration conf = FreemarkerManager.getInstance().getConfiguration(servletContext);
		assertEquals(conf.getDefaultEncoding(), "UTF-8");
	}
}
