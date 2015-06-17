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
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import com.opensymphony.xwork2.util.fs.DefaultFileManager;
import com.opensymphony.xwork2.util.fs.DefaultFileManagerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class XmlConfigurationProviderInterceptorParamOverridingTest extends XWorkTestCase {

    public void testInterceptorParamOveriding() throws Exception {
        DefaultConfiguration conf = new DefaultConfiguration();
        final XmlConfigurationProvider p = new XmlConfigurationProvider("com/opensymphony/xwork2/config/providers/xwork-test-interceptor-param-overriding.xml");
        DefaultFileManagerFactory factory = new DefaultFileManagerFactory();
        factory.setContainer(container);
        factory.setFileManager(new DefaultFileManager());
        p.setFileManagerFactory(factory);
        conf.reload(new ArrayList<ConfigurationProvider>() {
            {
                add(new XWorkConfigurationProvider());
                add(p);
            }
        });

        RuntimeConfiguration rtConf = conf.getRuntimeConfiguration();

        ActionConfig actionOne = rtConf.getActionConfig("", "actionOne");
        ActionConfig actionTwo = rtConf.getActionConfig("", "actionTwo");

        List<InterceptorMapping> actionOneInterceptors = actionOne.getInterceptors();
        List<InterceptorMapping> actionTwoInterceptors = actionTwo.getInterceptors();

        assertNotNull(actionOne);
        assertNotNull(actionTwo);
        assertNotNull(actionOneInterceptors);
        assertNotNull(actionTwoInterceptors);
        assertEquals(actionOneInterceptors.size(), 3);
        assertEquals(actionTwoInterceptors.size(), 3);

        InterceptorMapping actionOneInterceptorMapping1 = actionOneInterceptors.get(0);
        InterceptorMapping actionOneInterceptorMapping2 = actionOneInterceptors.get(1);
        InterceptorMapping actionOneInterceptorMapping3 = actionOneInterceptors.get(2);
        InterceptorMapping actionTwoInterceptorMapping1 = actionTwoInterceptors.get(0);
        InterceptorMapping actionTwoInterceptorMapping2 = actionTwoInterceptors.get(1);
        InterceptorMapping actionTwoInterceptorMapping3 = actionTwoInterceptors.get(2);

        assertNotNull(actionOneInterceptorMapping1);
        assertNotNull(actionOneInterceptorMapping2);
        assertNotNull(actionOneInterceptorMapping3);
        assertNotNull(actionTwoInterceptorMapping1);
        assertNotNull(actionTwoInterceptorMapping2);
        assertNotNull(actionTwoInterceptorMapping3);

        assertEquals(((InterceptorForTestPurpose) actionOneInterceptorMapping1.getInterceptor()).getParamOne(), "i1p1");
        assertEquals(((InterceptorForTestPurpose) actionOneInterceptorMapping1.getInterceptor()).getParamTwo(), "i1p2");
        assertEquals(((InterceptorForTestPurpose) actionOneInterceptorMapping2.getInterceptor()).getParamOne(), "i2p1");
        assertEquals(((InterceptorForTestPurpose) actionOneInterceptorMapping2.getInterceptor()).getParamTwo(), null);
        assertEquals(((InterceptorForTestPurpose) actionOneInterceptorMapping3.getInterceptor()).getParamOne(), null);
        assertEquals(((InterceptorForTestPurpose) actionOneInterceptorMapping3.getInterceptor()).getParamTwo(), null);

        assertEquals(((InterceptorForTestPurpose) actionTwoInterceptorMapping1.getInterceptor()).getParamOne(), null);
        assertEquals(((InterceptorForTestPurpose) actionTwoInterceptorMapping1.getInterceptor()).getParamTwo(), null);
        assertEquals(((InterceptorForTestPurpose) actionTwoInterceptorMapping2.getInterceptor()).getParamOne(), null);
        assertEquals(((InterceptorForTestPurpose) actionTwoInterceptorMapping2.getInterceptor()).getParamTwo(), "i2p2");
        assertEquals(((InterceptorForTestPurpose) actionTwoInterceptorMapping3.getInterceptor()).getParamOne(), "i3p1");
        assertEquals(((InterceptorForTestPurpose) actionTwoInterceptorMapping3.getInterceptor()).getParamTwo(), "i3p2");

    }


    @Override
    protected void tearDown() throws Exception {

        configurationManager.clearContainerProviders();
    }
}
