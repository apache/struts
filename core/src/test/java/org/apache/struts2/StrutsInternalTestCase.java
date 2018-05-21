/*
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

import com.opensymphony.xwork2.XWorkTestCase;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.util.StrutsTestCaseHelper;
import org.apache.struts2.views.jsp.StrutsMockServletContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Base test case for JUnit testing Struts.
 */
public abstract class StrutsInternalTestCase extends XWorkTestCase {

    protected StrutsMockServletContext servletContext;
    protected Dispatcher dispatcher;

    /**
     * Sets up the configuration settings, XWork configuration, and
     * message resources
     */
    protected void setUp() throws Exception {
        super.setUp();
        initDispatcher(null);
    }
    
    protected Dispatcher initDispatcher(Map<String,String> params) {
        servletContext = new StrutsMockServletContext();
        dispatcher = StrutsTestCaseHelper.initDispatcher(servletContext, params);
        configurationManager = dispatcher.getConfigurationManager();
        configuration = configurationManager.getConfiguration();
        container = configuration.getContainer();
        container.inject(dispatcher);
        return dispatcher;
    }

    /**
     * Init Dispatcher with provided comma delimited list of xml configs to use, ie:
     * initDispatcherWithConfigs("struts-default.xml,test-struts-config.xml")
     *
     * @param configs comma delimited list of config files
     * @return instance of {@see Dispatcher}
     */
    protected Dispatcher initDispatcherWithConfigs(String configs) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("config", configs);
        return initDispatcher(params);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        // maybe someone else already destroyed Dispatcher
        if (dispatcher != null && dispatcher.getConfigurationManager() != null) {
            dispatcher.cleanup();
            dispatcher = null;
        }
        StrutsTestCaseHelper.tearDown();
    }

}
