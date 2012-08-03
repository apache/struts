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

import javax.servlet.http.HttpServletResponse;


/**
 * All Actions that want to have access to the servlet response object must implement this interface.<p>
 * <p/>
 * This interface is only relevant if the Action is used in a servlet environment.<p>
 * <p/>
 * Note that using this interface makes the Action tied to a servlet environment, so it should be
 * avoided if possible since things like unit testing will become more difficult.
 *
 */
public interface ServletResponseAware {

    /**
     * Sets the HTTP response object in implementing classes.
     *
     * @param response the HTTP response.
     */
    public void setServletResponse(HttpServletResponse response);
}
