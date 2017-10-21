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
package org.apache.struts2.interceptor;

import org.apache.struts2.dispatcher.HttpParameters;

/**
 * <p>
 * This interface gives actions an alternative way of receiving input parameters. The parameters will
 * contain all input parameters as implementation of {@link org.apache.struts2.dispatcher.Parameter}.
 * Actions that need this should simply implement it.
 * </p>
 *
 * <p>
 * One common use for this is to have the action propagate parameters to internally instantiated data
 * objects.
 * </p>
 */
public interface HttpParametersAware {

    /**
     * Sets the HTTP parameters in the implementing class.
     *
     * @param parameters an instance of {@link HttpParameters}.
     */
    void setParameters(HttpParameters parameters);
}
