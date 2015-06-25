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
package com.opensymphony.xwork2.util.profiling;

import junit.framework.TestCase;

/**
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class ProfilingTimerBeanTest extends TestCase {
	
	public void testAddChild() throws Exception {
		ProfilingTimerBean bean0 = new ProfilingTimerBean("bean0");
		ProfilingTimerBean bean1 = new ProfilingTimerBean("bean1");
		ProfilingTimerBean bean2 = new ProfilingTimerBean("bean2");
		ProfilingTimerBean bean3 = new ProfilingTimerBean("bean3");
		ProfilingTimerBean bean4 = new ProfilingTimerBean("bean4");
		ProfilingTimerBean bean5 = new ProfilingTimerBean("bean5");
		ProfilingTimerBean bean6 = new ProfilingTimerBean("bean6");
		ProfilingTimerBean bean7 = new ProfilingTimerBean("bean7");
		ProfilingTimerBean bean8 = new ProfilingTimerBean("bean8");
		
		/*  bean0
		 *    + bean1
		 *       + bean2
		 *    + bean3
		 *       + bean4
		 *         + bean5
		 *           + bean6
		 *       +bean7
		 *    + bean8
		 */
		
		bean0.addChild(bean1);
		bean0.addChild(bean3);
		bean0.addChild(bean8);
		
		bean1.addChild(bean2);
		
		bean3.addChild(bean4);
		bean3.addChild(bean7);
		
		bean4.addChild(bean5);
		
		bean5.addChild(bean6);
		
		
		// bean0
		assertNull(bean0.getParent());
		assertEquals(bean0.children.size(), 3);
		assertTrue(bean0.children.contains(bean1));
		assertTrue(bean0.children.contains(bean3));
		assertTrue(bean0.children.contains(bean8));
		
		// bean1
		assertEquals(bean1.getParent(), bean0);
		assertEquals(bean1.children.size(), 1);
		assertTrue(bean1.children.contains(bean2));
		
		// bean2
		assertEquals(bean2.getParent(), bean1);
		assertEquals(bean2.children.size(), 0);
		
		// bean3
		assertEquals(bean3.getParent(), bean0);
		assertEquals(bean3.children.size(), 2);
		assertTrue(bean3.children.contains(bean4));
		assertTrue(bean3.children.contains(bean7));
		
		// bean4
		assertEquals(bean4.getParent(), bean3);
		assertEquals(bean4.children.size(), 1);
		assertTrue(bean4.children.contains(bean5));
		
		// bean5
		assertEquals(bean5.getParent(), bean4);
		assertEquals(bean5.children.size(), 1);
		assertTrue(bean5.children.contains(bean6));
		
		// bean6
		assertEquals(bean6.getParent(), bean5);
		assertEquals(bean6.children.size(), 0);
		
		// bean7
		assertEquals(bean7.getParent(), bean3);
		assertEquals(bean7.children.size(), 0);
		
		// bean8
		assertEquals(bean8.getParent(), bean0);
		assertEquals(bean8.children.size(), 0);
	}
	
	public void testTime() throws Exception {
		ProfilingTimerBean bean0 = new ProfilingTimerBean("bean0");
		bean0.setStartTime();
		Thread.sleep(1050);
		bean0.setEndTime();
		assertTrue(bean0.totalTime >= 1000);
	}
	
	public void testPrint() throws Exception {
		ProfilingTimerBean bean0 = new ProfilingTimerBean("bean0");
		bean0.setStartTime();
		Thread.sleep(1050);
		bean0.setEndTime();
		assertEquals(bean0.getPrintable(2000), "");
		assertTrue(bean0.getPrintable(500).length() > 0);
	}
}
