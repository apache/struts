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
package com.opensymphony.xwork2.interceptor;

/**
 * This interface is implemented by actions that want to declare acceptable parameters. Works in conjunction with {@link
 * ParametersInterceptor}. For example, actions may want to create a white list of parameters they will accept or a
 * blacklist of parameters they will reject to prevent clients from setting other unexpected (and possibly dangerous)
 * parameters.
 */
public interface ParameterNameAware {

    /**
     * Tests if the the action will accept the parameter with the given name.
     *
     * @param parameterName  the parameter name
     * @return <tt>true</tt> if accepted, <tt>false</tt> otherwise
     */
    boolean acceptableParameterName(String parameterName);
    
}
