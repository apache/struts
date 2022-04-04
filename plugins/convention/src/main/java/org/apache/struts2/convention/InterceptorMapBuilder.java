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

import java.util.List;

import org.apache.struts2.convention.annotation.Action;

import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.PackageConfig;

/**
 * <p>
 * This interface defines how interceptors are built from
 * annotations.
 * </p>
 */
public interface InterceptorMapBuilder {
	/**
     * Builds the interceptor configurations given the action information.
     *
     * @param   actionClass The class of the action.
     * @param   annotation The action annotation.
     * @param   actionName The action name.
     * @param   builder The package configuration builder.
     * @return  The mapping of the interceptors. If there were none found
     *          then this should return an empty List.
     */
	List<InterceptorMapping> build(Class<?> actionClass, PackageConfig.Builder builder, String actionName, Action annotation);
}
