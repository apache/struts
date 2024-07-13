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
package org.apache.struts2.showcase.ajax;

import com.opensymphony.xwork2.Action;
import org.apache.struts2.interceptor.parameter.StrutsParameter;

import java.io.Serializable;

public class AjaxTestAction implements Action, Serializable {

	private static int counter = 0;
	private String data;

	public String execute() throws Exception {
		return SUCCESS;
	}

	public long getServerTime() {
		return System.currentTimeMillis();
	}

	public int getCount() {
		return ++counter;
	}

	public String getData() {
		return data;
	}

	@StrutsParameter
	public void setData(String data) {
		this.data = data;
	}
}
