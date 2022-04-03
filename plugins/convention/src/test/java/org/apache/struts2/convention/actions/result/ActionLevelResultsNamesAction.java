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
package org.apache.struts2.convention.actions.result;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;

/**
 * <p>
 * This is a test action with multiple results names.
 * </p>
 */
public class ActionLevelResultsNamesAction {
    @Action(results = {
        @Result(name={"error", "input"}, location="error.jsp"),
        @Result(name="success", location="/WEB-INF/location/namespace/action-success.jsp"),
        @Result(name="failure", location="/WEB-INF/location/namespace/action-failure.jsp")
    })
    public String execute() {
        return null;
    }

    @Action(results = {
        @Result(location="/WEB-INF/location/namespace/action-success.jsp")
    })
    public String noname() {
        return null;
    }
}
