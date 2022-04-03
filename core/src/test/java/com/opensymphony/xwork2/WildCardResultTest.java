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
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.mock.MockResult;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;

/**
 * <code>WildCardResultTest</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id$
 */
public class WildCardResultTest extends XWorkTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // ensure we're using the default configuration, not simple config
        XmlConfigurationProvider configurationProvider = new StrutsXmlConfigurationProvider("xwork-sample.xml");
        container.inject(configurationProvider);
        loadConfigurationProviders(configurationProvider);
    }

    public void testWildCardEvaluation() throws Exception {
        ActionContext.clear();
        ActionProxy proxy = actionProxyFactory.createActionProxy(null, "WildCard", null, null);
        assertEquals("success", proxy.execute());
        assertEquals(VoidResult.class, proxy.getInvocation().getResult().getClass());

        ActionContext.clear();
        proxy = actionProxyFactory.createActionProxy(null, "WildCardInput", null, null);
        assertEquals("input", proxy.execute());
        assertEquals(MockResult.class, proxy.getInvocation().getResult().getClass());

        ActionContext.clear();
        proxy = actionProxyFactory.createActionProxy(null, "WildCardError", null, null);
        assertEquals("error", proxy.execute());
        assertEquals(MockResult.class, proxy.getInvocation().getResult().getClass());
    }

}
