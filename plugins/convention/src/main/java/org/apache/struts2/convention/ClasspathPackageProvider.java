/*
 * $Id: ClasspathConfigurationProvider.java 655902 2008-05-13 15:15:12Z bpontarelli $
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
 */package org.apache.struts2.convention;

import com.opensymphony.xwork2.config.PackageProvider;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.inject.Container;

/**
 * <p>
 * This class is a configuration provider for the XWork configuration
 * system. This is really the only way to truly handle loading of the
 * packages, actions and results correctly. This doesn't contain any
 * logic and instead delegates to the configured instance of the
 * {@link ActionConfigBuilder} interface.
 * </p>
 */
public class ClasspathPackageProvider implements PackageProvider {
     private ActionConfigBuilder actionConfigBuilder;

    @Inject
    public ClasspathPackageProvider(Container container) {
        this.actionConfigBuilder = container.getInstance(ActionConfigBuilder.class, container.getInstance(String.class, ConventionConstants.CONVENTION_ACTION_CONFIG_BUILDER));
    }

    public void init(Configuration configuration) throws ConfigurationException {
    }

    public boolean needsReload() {
        return actionConfigBuilder.needsReload(); 
    }

    public void loadPackages() throws ConfigurationException {
        actionConfigBuilder.buildActionConfigs();
    }
}
