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

package org.apache.struts2.spring;

import javax.servlet.ServletContext;

import junit.framework.TestCase;
import org.apache.struts2.StrutsConstants;
import org.easymock.EasyMock;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.opensymphony.xwork2.inject.Container;

/**
 * Unit test for {@link StrutsSpringObjectFactory}.
 *
 */
public class StrutsSpringObjectFactoryTest extends TestCase {

    public void testNoSpringContext() throws Exception {
        // to cover situations where there will be logged an error
        Container container = EasyMock.createNiceMock(Container.class);
        EasyMock.replay(container);
        
        StrutsSpringObjectFactory fac = new StrutsSpringObjectFactory(null, null, null, null, new MockServletContext(), null, container);

        assertEquals(AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, fac.getAutowireStrategy());
    }

    public void testWithSpringContext() throws Exception {
        Container container = EasyMock.createNiceMock(Container.class);
        EasyMock.replay(container);

        ConfigurableWebApplicationContext ac = new XmlWebApplicationContext();
        ServletContext msc = (ServletContext) new MockServletContext();
        msc.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ac);
        ac.setServletContext(msc);
        ac.setConfigLocations(new String[] {"org/apache/struts2/spring/StrutsSpringObjectFactoryTest-applicationContext.xml"});
        ac.refresh();
        StrutsSpringObjectFactory fac = new StrutsSpringObjectFactory("constructor", null, null, null, msc, null, container);

        assertEquals(AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, fac.getAutowireStrategy());
    }


}
