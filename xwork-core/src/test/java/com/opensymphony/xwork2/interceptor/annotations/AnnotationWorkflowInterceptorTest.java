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
package com.opensymphony.xwork2.interceptor.annotations;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.mock.MockResult;
import com.opensymphony.xwork2.util.location.LocatableProperties;

import java.util.Arrays;

/**
 * @author Zsolt Szasz, zsolt at lorecraft dot com
 * @author Rainer Hermanns
 */
public class AnnotationWorkflowInterceptorTest extends XWorkTestCase {
    private static final String ANNOTATED_ACTION = "annotatedAction";
    private static final String SHORTCIRCUITED_ACTION = "shortCircuitedAction";
    private final AnnotationWorkflowInterceptor annotationWorkflow = new AnnotationWorkflowInterceptor();

    @Override
    public void setUp() throws Exception{
        super.setUp();
        XmlConfigurationProvider provider = new XmlConfigurationProvider("xwork-default.xml");
        container.inject(provider);
        loadConfigurationProviders(provider, new MockConfigurationProvider());
    }

    public void testInterceptsBeforeAndAfter() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy("", ANNOTATED_ACTION, null);
        assertEquals(Action.SUCCESS, proxy.execute());
        AnnotatedAction action = (AnnotatedAction)proxy.getInvocation().getAction();
        assertEquals("baseBefore-before-execute-beforeResult-after", action.log);
    }

    public void testInterceptsShortcircuitedAction() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy("", SHORTCIRCUITED_ACTION, null);
        assertEquals("shortcircuit", proxy.execute());
        ShortcircuitedAction action = (ShortcircuitedAction)proxy.getInvocation().getAction();
        assertEquals("baseBefore-before", action.log);
    }

    private class MockConfigurationProvider implements ConfigurationProvider {
        private Configuration config;

        public void init(Configuration configuration) throws ConfigurationException {
            this.config = configuration;
        }

        public boolean needsReload() {
            return false;
        }

        public void destroy() { }


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
                            .addInterceptors(Arrays.asList(new InterceptorMapping[]{ new InterceptorMapping("annotationWorkflow", annotationWorkflow) }))
                            .addResultConfig(new ResultConfig.Builder("success", MockResult.class.getName()).build())
                            .build())
                    .addActionConfig(SHORTCIRCUITED_ACTION, new ActionConfig.Builder("defaultPackage", SHORTCIRCUITED_ACTION, ShortcircuitedAction.class.getName())
                            .addInterceptors(Arrays.asList(new InterceptorMapping[]{ new InterceptorMapping("annotationWorkflow", annotationWorkflow) }))
                            .addResultConfig(new ResultConfig.Builder("shortcircuit", MockResult.class.getName()).build())
                            .build())
                    .build();
            config.addPackageConfig("defaultPackage", packageConfig);
            config.addPackageConfig("default", new PackageConfig.Builder(packageConfig).name("default").build());
        }
    }
}
