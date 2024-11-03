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
package org.apache.struts2.interceptor.annotations;

import org.apache.struts2.action.Action;
import org.apache.struts2.ActionProxy;
import org.apache.struts2.ActionProxyFactory;
import org.apache.struts2.DefaultActionProxyFactory;
import org.apache.struts2.ObjectFactory;
import org.apache.struts2.XWorkTestCase;
import org.apache.struts2.config.Configuration;
import org.apache.struts2.config.ConfigurationException;
import org.apache.struts2.config.ConfigurationProvider;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.config.entities.InterceptorMapping;
import org.apache.struts2.config.entities.PackageConfig;
import org.apache.struts2.config.entities.ResultConfig;
import org.apache.struts2.config.providers.XmlConfigurationProvider;
import org.apache.struts2.inject.ContainerBuilder;
import org.apache.struts2.mock.MockResult;
import org.apache.struts2.util.location.LocatableProperties;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;

import java.util.Collections;

/**
 * @author Zsolt Szasz, zsolt at lorecraft dot com
 * @author Rainer Hermanns
 */
public class AnnotationWorkflowInterceptorTest extends XWorkTestCase {
    private static final String ANNOTATED_ACTION = "annotatedAction";
    private static final String SHORTCIRCUITED_ACTION = "shortCircuitedAction";
    private final AnnotationWorkflowInterceptor annotationWorkflow = new AnnotationWorkflowInterceptor();

    @Override
    public void setUp() throws Exception {
        super.setUp();
        XmlConfigurationProvider provider = new StrutsXmlConfigurationProvider("xwork-default.xml");
        container.inject(provider);
        loadConfigurationProviders(provider, new MockConfigurationProvider());
    }

    public void testInterceptsBeforeAndAfter() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy("", ANNOTATED_ACTION, null, null);
        assertEquals(Action.SUCCESS, proxy.execute());
        AnnotatedAction action = (AnnotatedAction) proxy.getInvocation().getAction();
        assertEquals("interfaceBefore-baseBefore-basePrivateBefore-before-execute-beforeResult-basePrivateBeforeResult-interfaceBeforeResult-after-basePrivateAfter-interfaceAfter", action.log);
    }

    public void testInterceptsShortcircuitedAction() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy("", SHORTCIRCUITED_ACTION, null, null);
        assertEquals("shortcircuit", proxy.execute());
        ShortcircuitedAction action = (ShortcircuitedAction) proxy.getInvocation().getAction();
        assertEquals("interfaceBefore-baseBefore-basePrivateBefore-before-basePrivateBeforeResult-interfaceBeforeResult", action.log);
    }

    private class MockConfigurationProvider implements ConfigurationProvider {
        private Configuration config;

        public void init(Configuration configuration) throws ConfigurationException {
            this.config = configuration;
        }

        public boolean needsReload() {
            return false;
        }

        public void destroy() {
        }


        public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
            if (!builder.contains(ObjectFactory.class)) {
                builder.factory(ObjectFactory.class);
            }
            if (!builder.contains(ActionProxyFactory.class)) {
                builder.factory(ActionProxyFactory.class, DefaultActionProxyFactory.class);
            }
        }

        public void loadPackages() throws ConfigurationException {
            PackageConfig packageConfig = new PackageConfig.Builder("default")
                .addActionConfig(ANNOTATED_ACTION, new ActionConfig.Builder("defaultPackage", ANNOTATED_ACTION, AnnotatedAction.class.getName())
                    .addInterceptors(Collections.singletonList(new InterceptorMapping("annotationWorkflow", annotationWorkflow)))
                    .addResultConfig(new ResultConfig.Builder("success", MockResult.class.getName()).build())
                    .build())
                .addActionConfig(SHORTCIRCUITED_ACTION, new ActionConfig.Builder("defaultPackage", SHORTCIRCUITED_ACTION, ShortcircuitedAction.class.getName())
                    .addInterceptors(Collections.singletonList(new InterceptorMapping("annotationWorkflow", annotationWorkflow)))
                    .addResultConfig(new ResultConfig.Builder("shortcircuit", MockResult.class.getName()).build())
                    .build())
                .build();
            config.addPackageConfig("defaultPackage", packageConfig);
            config.addPackageConfig("default", new PackageConfig.Builder(packageConfig).name("default").build());
        }
    }
}
