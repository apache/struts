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

import org.springframework.context.ApplicationContext;
import org.springframework.test.context.support.GenericXmlContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * Base class for Spring JUnit actions
 */
public abstract class StrutsSpringTestCase extends StrutsTestCase {

    private static final String DEFAULT_CONTEXT_LOCATION = "classpath*:applicationContext.xml";
    protected static ApplicationContext applicationContext;

    protected void setupBeforeInitDispatcher() throws Exception {
        // only load beans from spring once
        if (applicationContext == null) {
            GenericXmlContextLoader xmlContextLoader = new GenericXmlContextLoader();
            applicationContext = xmlContextLoader.loadContext(getContextLocations());
        }

        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);
    }

    protected String[] getContextLocations() {
        return new String[] {DEFAULT_CONTEXT_LOCATION};
    }

}
