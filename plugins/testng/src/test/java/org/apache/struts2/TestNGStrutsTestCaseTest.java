/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2;

import junit.framework.TestCase;

import org.apache.struts2.dispatcher.Dispatcher;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.annotations.Test;

import com.opensymphony.xwork2.config.ConfigurationManager;

public class TestNGStrutsTestCaseTest extends TestCase {

    public void testSimpleTest() throws Exception {
        TestListenerAdapter tla = new TestListenerAdapter();
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[] { RunTest.class });
        testng.addListener(tla);
        try {
            testng.run();
            assertEquals(1, tla.getPassedTests().size());
            assertEquals(0, tla.getFailedTests().size());
            assertTrue(RunTest.ran);
            assertNotNull(RunTest.mgr);
            assertNotNull(RunTest.du);
            assertNull(Dispatcher.getInstance());
        } finally {
            RunTest.mgr = null;
        }
    }
    
    public static class RunTest extends StrutsTestCase {
        public static boolean ran = false;
        public static ConfigurationManager mgr;
        public static Dispatcher du;
       
        @Test 
        public void testRun() {
            ran = true;
            mgr = this.configurationManager;
            du = Dispatcher.getInstance();
        }
    }
}

