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
package org.apache.struts2;

/**
 * Commonly used result names returned by action methods.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public final class ResultNames {

    private ResultNames() {}

    /**
     * The action executed successfully.
     */
    public static final String SUCCESS = "success";

    /**
     * The action requires more input, i.e.&nbsp;a validation error occurred.
     */
    public static final String INPUT = "input";

    /**
     * The action requires the user to log in before executing.
     */
    public static final String LOGIN = "login";

    /**
     * The action execution failed irrecoverably.
     */
    public static final String ERROR = "error";

    /**
     * The action executed successfully, but do not execute a result.
     */
    public static final String NONE = "none";
}
