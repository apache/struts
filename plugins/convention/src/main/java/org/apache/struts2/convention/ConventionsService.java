/*
 * $Id$
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
 */
package org.apache.struts2.convention;

import java.util.Map;

import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;

/**
 * <p>
 * This interface defines the conventions that are used by the convention plugin.
 * In most cases the methods on this class will provide the best default for any
 * values and also handle locating overrides of the default via the annotations
 * that are part of the plugin.
 * </p>
 */
public interface ConventionsService {
    /**
     * Locates the result location from annotations on the action class or the package or returns the
     * default if no annotations are present.
     *
     * @param   actionClass The action class.
     * @return  The result location if it is set in the annotations. Otherwise, the default result
     *          location is returned.
     */
    String determineResultPath(Class<?> actionClass);

    /**
     * Delegates to the other method but first looks up the Action's class using the given class name.
     *
     * @param   actionConfig (Optional) The configuration for the action that the result is being
     *          built for or null if the default result path is needed.
     * @return  The result location if it is set in the annotations for the class of the ActionConfig.
     *          Otherwise, the default result location is returned. If null is passed in, the default
     *          is returned,
     */
    String determineResultPath(ActionConfig actionConfig);

    /**
     * Returns a mapping between the result type strings and the {@link ResultTypeConfig} instances
     * based on the {@link PackageConfig} given.
     *
     * @param   packageConfig The PackageConfig to get the result types for.
     * @return  The result types or an empty Map of nothing is configured.
     */
    Map<String, ResultTypeConfig> getResultTypesByExtension(PackageConfig packageConfig);
}