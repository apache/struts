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

/**
 * <p>
 * This is a test action with multiple interceptors.
 * </p>
 */
public class InterceptorsAction {
    @Action(value = "action100", interceptorRefs = @InterceptorRef("interceptor-1"))
    public String run1() {
        return null;
    }

    @Action(value = "action200", interceptorRefs = @InterceptorRef("stack-1"))
    public String run2() {
        return null;
    }

    @Action(value = "action300", interceptorRefs = {@InterceptorRef("interceptor-1"), @InterceptorRef("interceptor-2")})
    public String run3() {
        return null;
    }
    
    @Action(value = "action400", interceptorRefs = {@InterceptorRef("interceptor-1"), @InterceptorRef("stack-1")})
    public String run4() {
        return null;
    }
}