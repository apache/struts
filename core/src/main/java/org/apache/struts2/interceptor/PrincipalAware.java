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

package org.apache.struts2.interceptor;

/**
 * Actions that want access to the Principal information from HttpServletRequest object
 * should implement this interface.
 *
 * <p>This interface is only relevant if the Action is used in a servlet environment.
 * By using this interface you will not become tied to servlet environment.</p>
 *
 */
public interface PrincipalAware {
    void setPrincipalProxy(PrincipalProxy principalProxy);
}
