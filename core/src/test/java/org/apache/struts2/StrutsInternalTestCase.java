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

import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.logging.jdk.JdkLoggerFactory;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.util.StrutsTestCaseHelper;
import org.apache.struts2.views.jsp.StrutsMockServletContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Base test case for JUnit testing Struts.
 */
public abstract class StrutsInternalTestCase extends XWorkTestCase {

    static {
        ConsoleHandler handler = new ConsoleHandler();
        final SimpleDateFormat df = new SimpleDateFormat("mm:ss.SSS");
        Formatter formatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                StringBuilder sb = new StringBuilder();
                sb.append(record.getLevel());
                sb.append(':');
                for (int x=9-record.getLevel().toString().length(); x>0; x--) {
                    sb.append(' ');
                }
                sb.append('[');
                sb.append(df.format(new Date(record.getMillis())));
                sb.append("] ");
                sb.append(formatMessage(record));
                sb.append('\n');
                return sb.toString();
            }
        };
        handler.setFormatter(formatter);
        Logger logger = Logger.getLogger("");
        if (logger.getHandlers().length > 0)
            logger.removeHandler(logger.getHandlers ()[0]);
        logger.addHandler(handler);
        logger.setLevel(Level.WARNING);
        LoggerFactory.setLoggerFactory(new JdkLoggerFactory());
    }

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
