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

import org.apache.struts2.convention.annotation.Action;

import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;

/**
 * <p>
 * This interface defines how results are constructed for an Action.
 * The action information is supplied and the result is a mapping of
 * ResultConfig instances to the result name.
 * </p>
 */
public interface ResultMapBuilder {
    /**
     * Builds the result configurations given the action information.
     *
     * @param   actionClass The class of the action.
     * @param   annotation The action annotation.
     * @param   actionName The action name.
     * @param   packageConfig The package configuration that the action will be added to.
     * @return  The mapping of the result names to the result configurations. If there were none found
     *          than this should return an empty Map.
     */
    Map<String, ResultConfig> build(Class<?> actionClass, Action annotation, String actionName,
        PackageConfig packageConfig);
}