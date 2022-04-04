/*
 * $Id: ActionLevelResultAction.java 655902 2008-05-13 15:15:12Z bpontarelli $
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
package org.apache.struts2.convention.actions.interceptor;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;

/**
 * <p>
 * This is a test action with 1 interceptor and 1 stack at the action level.
 * </p>
 */
@InterceptorRefs({
    @InterceptorRef("interceptor-1"),
    @InterceptorRef("stack-1")
})
public class ActionLevelInterceptor3Action {

    @Action(value = "action900")
    public String run1() throws Exception {
        return null;
    }
}