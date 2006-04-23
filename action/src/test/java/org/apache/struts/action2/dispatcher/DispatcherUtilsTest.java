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
package org.apache.struts.action2.dispatcher;

import java.util.Locale;

import org.springframework.mock.web.MockServletContext;
import org.apache.struts.action2.StrutsTestCase;

import com.opensymphony.xwork.util.LocalizedTextUtil;

import junit.framework.TestCase;

/**
 * Test case for DispatcherUtils.
 * 
 */
public class DispatcherUtilsTest extends StrutsTestCase {

	public void testDefaultResurceBundlePropertyLoaded() throws Exception {
        Locale.setDefault(Locale.US); // force to US locale as we also have _de and _da properties
        DispatcherUtils.initialize(new MockServletContext());
		
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
	
}
