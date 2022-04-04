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
package org.apache.struts2.convention.actions.result;

import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

/**
 * <p>
 * This is a test action with multiple results.
 * </p>
 */
@Results({
    @Result(name="error", location="error.jsp", params={"key", "ann-value", "key1", "ann-value1"}),
    @Result(name="input", location="foo.action", type="redirectAction"),
    @Result(name="success", location="/WEB-INF/location/namespace/action-success.jsp"),
    @Result(name="failure", location="/WEB-INF/location/namespace/action-failure.jsp")
})
public class ClassLevelResultsAction {
    public String execute() {
        return null;
    }
}