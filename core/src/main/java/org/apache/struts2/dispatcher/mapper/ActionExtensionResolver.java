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
package org.apache.struts2.dispatcher.mapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.config.Configuration;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.inject.Inject;

import java.util.List;

/**
 * Chooses the extension of a generated action URL from the extensions the target action accepts, so links,
 * forms and redirects point at an extension the target can be reached by.
 *
 * @since 7.5.0
 */
public class ActionExtensionResolver {

    private Configuration configuration;
    private List<String> extensions;

    /**
     * @param configuration the configuration used to look up the target action
     * @since 7.5.0
     */
    @Inject
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * @param extensions the global {@code struts.action.extension} list
     * @since 7.5.0
     */
    @Inject(StrutsConstants.STRUTS_ACTION_EXTENSION)
    public void setExtensions(String extensions) {
        this.extensions = StringUtils.isNotEmpty(extensions) ? ActionExtensions.parse(extensions) : null;
    }

    /**
     * Sets the extension of {@code target} to the current request's extension when the target accepts it, otherwise
     * to the target's first accepted extension, which is also used when the current request has no extension, as in
     * {@link DefaultActionMapper#lookupExtension(String)}. Leaves {@code target} unchanged when it already has an extension,
     * when the target action is unknown, or when neither the target nor the global constant declares extensions.
     *
     * @param target the mapping of the action the URL points at
     * @since 7.5.0
     */
    public void applyTo(ActionMapping target) {
        if (target.getExtension() != null) {
            return;
        }
        ActionConfig actionConfig = configuration.getRuntimeConfiguration()
            .getActionConfig(target.getNamespace(), StringUtils.substringBefore(target.getName(), "!"));
        if (actionConfig == null) {
            return;
        }
        List<String> effective = actionConfig.getExtensions() != null ? actionConfig.getExtensions() : extensions;
        if (effective == null) {
            return;
        }
        String current = currentExtension();
        target.setExtension(current == null ? effective.get(0) : ActionExtensions.select(effective, current));
    }

    private String currentExtension() {
        ActionContext context = ActionContext.getContext();
        if (context == null || context.getActionMapping() == null) {
            return null;
        }
        return context.getActionMapping().getExtension();
    }
}
