/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
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
package org.apache.struts2.spring;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.config.Settings;
import junit.framework.TestCase;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import javax.servlet.ServletContext;

/**
 * Unit test for {@link StrutsSpringObjectFactory}.
 *
 */
public class StrutsSpringObjectFactoryTest extends StrutsTestCase {

    public void testNoSpringContext() throws Exception {
        // to cover situations where there will be logged an error
        StrutsSpringObjectFactory fac = new StrutsSpringObjectFactory();
        ServletContext msc = (ServletContext) new MockServletContext();
        fac.init(msc);
        
        assertEquals(AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, fac.getAutowireStrategy());
    }

    public void testWithSpringContext() throws Exception {
        StrutsSpringObjectFactory fac = new StrutsSpringObjectFactory();

        // autowire by constructure, we try a non default setting in this unit test
        Settings.set(StrutsConstants.STRUTS_OBJECTFACTORY_SPRING_AUTOWIRE, "constructor");

        ConfigurableWebApplicationContext ac = new XmlWebApplicationContext();
        ServletContext msc = (ServletContext) new MockServletContext();
        msc.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ac);
        ac.setServletContext(msc);
        ac.setConfigLocations(new String[] {"org/apache/struts2/spring/StrutsSpringObjectFactoryTest-applicationContext.xml"});
        ac.refresh();

        fac.init(msc);

        assertEquals(AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, fac.getAutowireStrategy());
    }


}
