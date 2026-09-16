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
package org.apache.struts2.rest;

import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.InterceptorStackConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import org.apache.struts2.config.DefaultPropertiesProvider;
import org.apache.struts2.config.StrutsBeanSelectionProvider;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * WW-5718: {@code restDefaultStack} was forked from core's {@code defaultStack} and has to carry
 * the same resource-isolation interceptors, in the same slot - after {@code conversionError} and
 * ahead of validation - or a package extending {@code rest-default} silently loses them.
 */
public class RestDefaultStackTest extends XWorkTestCase {

    private List<String> stack;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        loadConfigurationProviders(
                new StrutsXmlConfigurationProvider("struts-default.xml"),
                new StrutsXmlConfigurationProvider("struts-plugin.xml"),
                new DefaultPropertiesProvider(),
                new StrutsBeanSelectionProvider());
        PackageConfig restDefault = configuration.getPackageConfig("rest-default");
        assertEquals("restDefaultStack", restDefault.getDefaultInterceptorRef());
        InterceptorStackConfig stackConfig =
                (InterceptorStackConfig) restDefault.getInterceptorConfig("restDefaultStack");
        stack = stackConfig.getInterceptors().stream().map(InterceptorMapping::getName).collect(Collectors.toList());
    }

    public void testResourceIsolationInterceptorsSitBetweenConversionErrorAndValidation() {
        assertThat(stack).containsSubsequence("conversionError", "coep", "coop", "fetchMetadata", "validation");
    }
}
