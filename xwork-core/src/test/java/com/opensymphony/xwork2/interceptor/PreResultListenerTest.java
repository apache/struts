/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.interceptor;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;

import java.util.HashMap;


/**
 * PreResultListenerTest
 *
 * @author Jason Carreira
 *         Date: Nov 13, 2003 11:16:43 PM
 */
public class PreResultListenerTest extends XWorkTestCase {

    private int count = 1;


    public void testPreResultListenersAreCalled() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy("package", "action", new HashMap(), false, true);
        ActionInvocation invocation = proxy.getInvocation();
        Mock preResultListenerMock1 = new Mock(PreResultListener.class);
        preResultListenerMock1.expect("beforeResult", C.args(C.eq(invocation), C.eq(Action.SUCCESS)));
        invocation.addPreResultListener((PreResultListener) preResultListenerMock1.proxy());
        proxy.execute();
        preResultListenerMock1.verify();
    }

    public void testPreResultListenersAreCalledInOrder() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy("package", "action", new HashMap(), false, true);
        ActionInvocation invocation = proxy.getInvocation();
        CountPreResultListener listener1 = new CountPreResultListener();
        CountPreResultListener listener2 = new CountPreResultListener();
        invocation.addPreResultListener(listener1);
        invocation.addPreResultListener(listener2);
        proxy.execute();
        assertNotNull(listener1.getMyOrder());
        assertNotNull(listener2.getMyOrder());
        assertEquals(listener1.getMyOrder().intValue() + 1, listener2.getMyOrder().intValue());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loadConfigurationProviders(new ConfigurationProvider() {
            Configuration configuration;
            public void destroy() {
            }
            
            public void init(Configuration config) {
                this.configuration = config;
            }

            public void loadPackages() {
                PackageConfig packageConfig = new PackageConfig.Builder("package")
                        .addActionConfig("action", new ActionConfig.Builder("package", "action", SimpleFooAction.class.getName()).build())
                        .build();
                configuration.addPackageConfig("package", packageConfig);
            }

            /**
             * Tells whether the ConfigurationProvider should reload its configuration
             *
             * @return
             */
            public boolean needsReload() {
                return false;
            }

            public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
                builder.factory(ActionProxyFactory.class, DefaultActionProxyFactory.class);
                builder.factory(ObjectFactory.class);
                
            }
        });
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    private class CountPreResultListener implements PreResultListener {
        private Integer myOrder = null;

        public Integer getMyOrder() {
            return myOrder;
        }

        public void beforeResult(ActionInvocation invocation, String resultCode) {
            myOrder = new Integer(count++);
        }
    }
}
