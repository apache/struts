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
package org.apache.struts2.showcase.tag.nonui.actionPrefix;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.parameter.StrutsParameter;

public class SubmitAction extends ActionSupport {

	private static final long serialVersionUID = -7832803019378213087L;

	private String text;

	public String getText() {
		return text;
	}

	@StrutsParameter
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}

	public String alternateMethod() {
		return "methodPrefixResult";
	}

}
