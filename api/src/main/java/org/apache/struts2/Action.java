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
 * Default action interface. Provided purely for user convenience. Struts does not require actions to implement any
 * interfaces. Actions need only implement a public, no argument method which returns {@code String}. If a user does
 * not specify a method name, Struts defaults to {@code execute()}.
 *
 * <p>For example:
 *
 * <pre>
 *   static import ResultNames.*;
 *
 *   public class MyAction <b>implements Action</b> {
 *
 *     public String execute() {
 *       return SUCCESS;
 *     }
 *   }
 * </pre>
 *
 * <p>is equivalent to:
 *
 * <pre>
 *   static import ResultNames.*;
 *
 *   public class MyAction {
 *
 *     public String execute() {
 *       return SUCCESS;
 *     }
 *   }
 * </pre>
 *
 * @author crazybob@google.com (Bob Lee)
 */
public interface Action {

    /**
     * Executes this action.
     *
     * @return result name which matches a result name from the action mapping in the configuration file. See {@link
     *  ResultNames} for common suggestions.
     */
    String execute();
}
