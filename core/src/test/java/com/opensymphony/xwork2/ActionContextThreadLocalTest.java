/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2;

import junit.framework.TestCase;

import java.util.HashMap;


/**
 * Simple Test ActionContext's ThreadLocal
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class ActionContextThreadLocalTest extends TestCase {

	
	public void testGetContext() throws Exception {
	    ActionContext.setContext(null);
		assertNull(ActionContext.getContext());
	}
	
	public void testSetContext() throws Exception {
		ActionContext context = new ActionContext(new HashMap<String, Object>());
		ActionContext.setContext(context);
		assertEquals(context, ActionContext.getContext());
	}
}
