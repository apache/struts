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
package org.apache.struts2.dispatcher;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.config.Settings;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.opensymphony.xwork2.util.LocalizedTextUtil;

/**
 * Test case for Dispatcher.
 * 
 */
public class DispatcherTest extends StrutsTestCase {

	public void testDefaultResurceBundlePropertyLoaded() throws Exception {
        Locale.setDefault(Locale.US); // force to US locale as we also have _de and _da properties
		
		// some i18n messages from xwork-messages.properties
		assertEquals(
				LocalizedTextUtil.findDefaultText("xwork.error.action.execution", Locale.US), 
				"Error during Action invocation");
		
		// some i18n messages from struts-messages.properties
		assertEquals(
				LocalizedTextUtil.findDefaultText("struts.messages.error.uploading", Locale.US, 
						new Object[] { "some error messages" }), 
				"Error uploading: some error messages");
	}
	
	public void testPrepareSetEncodingProperly() throws Exception {
		HttpServletRequest req = new MockHttpServletRequest();
		HttpServletResponse res = new MockHttpServletResponse();
		
		Settings.set(StrutsConstants.STRUTS_I18N_ENCODING, "utf-8");
		
		
		Dispatcher du = Dispatcher.getInstance();
		du.prepare(req, res);
		
		assertEquals(req.getCharacterEncoding(), "utf-8");
	}
	
	public void testPrepareSetEncodingPropertyWithMultipartRequest() throws Exception {
		MockHttpServletRequest req = new MockHttpServletRequest();
		MockHttpServletResponse res = new MockHttpServletResponse();
		
		req.setContentType("multipart/form-data");
		Settings.set(StrutsConstants.STRUTS_I18N_ENCODING, "utf-8");
		
		
		Dispatcher du = Dispatcher.getInstance();
		du.prepare(req, res);
		
		assertEquals(req.getCharacterEncoding(), "utf-8");
	}
}
