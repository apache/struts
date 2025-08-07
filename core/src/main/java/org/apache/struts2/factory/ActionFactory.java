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
package org.apache.struts2.factory;

import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.ognl.StrutsContext;

import java.util.Map;

/**
 * Used by {@link org.apache.struts2.ObjectFactory} to build actions
 */
public interface ActionFactory {

    /**
     * Builds action instance
     *
     * @param actionName name of the action
     * @param namespace namespace for the action
     * @param config action config
     * @param extraContext extra context map
     *
     * @return action object
     *
     * @throws Exception in case of any errors
     */
    Object buildAction(String actionName, String namespace, ActionConfig config, StrutsContext extraContext) throws Exception;

}

