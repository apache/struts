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
 * This marker interface should be implemented by actions that do not want any parameters set on
 * them automatically. This may be useful if one is using the action tag and want to supply
 * the parameters to the action manually using the param tag. It may also be useful if one for
 * security reasons wants to make sure that parameters cannot be set by malicious users.
 *
 */
public interface NoParameters extends com.opensymphony.xwork2.interceptor.NoParameters {
}
