/*
 * Copyright 2002-2009 The Apache Software Foundation.
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

package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;

/**
 * <code>WildCardResultTest</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id$
 */
public class DefaultClasstTest extends XWorkTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // ensure we're using the default configuration, not simple config
        XmlConfigurationProvider configurationProvider = new XmlConfigurationProvider("xwork-sample.xml");
        container.inject(configurationProvider);
        loadConfigurationProviders(configurationProvider);
    }

    public void testWildCardEvaluation() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy("Abstract-crud", "edit", null);
        assertEquals("com.opensymphony.xwork2.SimpleAction", proxy.getConfig().getClassName());
        
        proxy = actionProxyFactory.createActionProxy("/example", "edit", null);
        assertEquals("com.opensymphony.xwork2.ModelDrivenAction", proxy.getConfig().getClassName());
         

        proxy = actionProxyFactory.createActionProxy("/example2", "override", null);
        assertEquals("com.opensymphony.xwork2.ModelDrivenAction", proxy.getConfig().getClassName());
        
        proxy = actionProxyFactory.createActionProxy("/example2/subItem", "save", null);
        assertEquals("com.opensymphony.xwork2.ModelDrivenAction", proxy.getConfig().getClassName());
        
        proxy = actionProxyFactory.createActionProxy("/example2", "list", null);
        assertEquals("com.opensymphony.xwork2.ModelDrivenAction", proxy.getConfig().getClassName());
        
        proxy = actionProxyFactory.createActionProxy("/example3", "list", null);
        assertEquals("com.opensymphony.xwork2.SimpleAction", proxy.getConfig().getClassName());
    }

}
