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
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.util.Collections;
import java.util.Map;

/**
 * Simple facade to make using XWork standalone easier
 */
public class XWork {
    
    ConfigurationManager configurationManager;
    
    public XWork() {
        this(new ConfigurationManager());
    }
    
    public XWork(ConfigurationManager mgr) {
        this.configurationManager = mgr;
    }
    
    public void setLoggerFactory(LoggerFactory factory) {
        LoggerFactory.setLoggerFactory(factory);
    }
    
    /**
     * Executes an action
     * 
     * @param namespace The namespace
     * @param name The action name
     * @param method The method name
     * @throws Exception If anything goes wrong
     */
    public void executeAction(String namespace, String name, String method) throws XWorkException {
        Map<String, Object> extraContext = Collections.emptyMap();
        executeAction(namespace, name, method, extraContext);
    }
    
    /**
     * Executes an action with extra context information
     * 
     * @param namespace The namespace
     * @param name The action name
     * @param method The method name
     * @param extraContext A map of extra context information
     * @throws Exception If anything goes wrong
     */
    public void executeAction(String namespace, String name, String method, Map<String, Object> extraContext) throws XWorkException {
        Configuration config = configurationManager.getConfiguration();
        try {
            ActionProxy proxy = config.getContainer().getInstance(ActionProxyFactory.class).createActionProxy(
                    namespace, name, method, extraContext, true, false);
        
            proxy.execute();
        } catch (Exception e) {
            throw new XWorkException(e);
        } finally {
            ActionContext.setContext(null);
        }
    }
}
