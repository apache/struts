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

import java.util.Map;

import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.util.StrutsTestCaseHelper;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.springframework.mock.web.MockServletContext;

import com.opensymphony.xwork2.TestNGXWorkTestCase;

/**
 * Base test class for TestNG unit tests.  Provides common Struts variables
 * and performs Struts setup and teardown processes
 */
public class StrutsTestCase extends TestNGXWorkTestCase {

    @BeforeTest
    protected void setUp() throws Exception {
        super.setUp();
        initDispatcher(null);
    }
    
    protected Dispatcher initDispatcher(Map<String,String> params) {
        Dispatcher du = StrutsTestCaseHelper.initDispatcher(new MockServletContext(), params);
        configurationManager = du.getConfigurationManager();
        configuration = configurationManager.getConfiguration();
        container = configuration.getContainer();
        return du;
    }

    @AfterTest
    protected void tearDown() throws Exception {
        super.tearDown();
        StrutsTestCaseHelper.tearDown();
    }
}

