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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.util.StrutsTestCaseHelper;
import org.springframework.mock.web.MockServletContext;

import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.logging.jdk.JdkLoggerFactory;

/**
 * Base test case for JUnit testing Struts.
 */
public abstract class StrutsTestCase extends XWorkTestCase {

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
    
    /**
     * Sets up the configuration settings, XWork configuration, and
     * message resources
     */
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

    protected void tearDown() throws Exception {
        super.tearDown();
        StrutsTestCaseHelper.tearDown();
    }

}
