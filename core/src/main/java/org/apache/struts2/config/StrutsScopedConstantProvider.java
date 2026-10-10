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
package org.apache.struts2.config;

import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.ActionProxy;
import org.apache.struts2.config.entities.PackageConfig;
import org.apache.struts2.inject.Container;
import org.apache.struts2.inject.Inject;

import java.util.Optional;
import java.util.Set;

/**
 * @since 7.5.0
 */
public class StrutsScopedConstantProvider implements ScopedConstantProvider {

    private Container container;
    private Configuration configuration;
    private volatile Set<String> scopableNames;

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    @Inject
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getValue(String name) {
        if (!getScopableNames().contains(name)) {
            throw new IllegalArgumentException(String.format(
                    "Constant [%s] is not scopable; register it through a %s bean", name, ScopableConstants.class.getName()));
        }
        return currentPackage()
                .map(packageConfig -> packageConfig.getAllScopedConstants().get(name))
                .orElseGet(() -> container.getInstance(String.class, name));
    }

    private Optional<PackageConfig> currentPackage() {
        ActionContext context = ActionContext.getContext();
        ActionInvocation invocation = context != null ? context.getActionInvocation() : null;
        ActionProxy proxy = invocation != null ? invocation.getProxy() : null;
        if (proxy == null || proxy.getConfig() == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(configuration.getPackageConfig(proxy.getConfig().getPackageName()));
    }

    private Set<String> getScopableNames() {
        Set<String> names = scopableNames;
        if (names == null) {
            names = ScopableConstants.collectNames(container);
            scopableNames = names;
        }
        return names;
    }
}
