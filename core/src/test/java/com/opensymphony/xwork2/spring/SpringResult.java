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
package com.opensymphony.xwork2.spring;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;

public class SpringResult implements Result {

	private static final long serialVersionUID = -2877126768401198951L;

	private boolean initialize = false;

	//  this String should be populated by spring
	private String stringParameter;

	public void initialize() {
		// this method should be called by spring
		this.initialize = true;
	}

	public void execute(ActionInvocation invocation) throws Exception {
		// intetionally empty
	}

	public void setStringParameter(String stringParameter) {
		this.stringParameter = stringParameter;
	}

	public String getStringParameter() {
		return this.stringParameter;
	}

	public boolean isInitialize() {
		return this.initialize;
	}
}

