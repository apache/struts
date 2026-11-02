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
package org.apache.struts2.interceptor.parameter;

import java.lang.reflect.Member;

/**
 * Decides, while one parameter is being bound, whether OGNL may use a member it has actually resolved.
 * <p>
 * {@link ParameterAuthorizer#isAuthorized} runs before OGNL and has to predict which accessor or field OGNL will go
 * through. This guard runs where OGNL resolves the member, so a difference between that prediction and OGNL's name
 * resolution fails closed. It adds to the authorizer and does not replace it: Map-backed models and custom property
 * accessors never reach it.
 *
 * @see ParameterAuthorizer#memberGuard
 * @since 7.6.0
 */
@FunctionalInterface
public interface ParameterMemberGuard {

    /**
     * Permits every member, which is what an instance of {@code SecurityMemberAccess} does outside parameter binding.
     */
    ParameterMemberGuard PERMIT_ALL = (owner, member, propertyName) -> true;

    /**
     * @param owner        the object OGNL is about to use the member on, or {@code null} for a static member
     * @param member       the member OGNL resolved
     * @param propertyName the property name OGNL reached the member by, which may be {@code null}
     * @return {@code true} if OGNL may use the member for the parameter being bound
     */
    boolean permits(Object owner, Member member, String propertyName);
}
