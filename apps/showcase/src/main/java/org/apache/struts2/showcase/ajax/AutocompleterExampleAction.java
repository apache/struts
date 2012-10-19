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

import com.opensymphony.xwork2.ActionSupport;

import java.util.ArrayList;
import java.util.List;

public class AutocompleterExampleAction extends ActionSupport {
	private String select;
	private List<String> options = new ArrayList<String>();

	private static final long serialVersionUID = -8481638176160014396L;

	public String execute() throws Exception {
		if ("fruits".equals(select)) {
			options.add("apple");
			options.add("banana");
			options.add("grape");
			options.add("pear");
		} else if ("colors".equals(select)) {
			options.add("red");
			options.add("green");
			options.add("blue");
		}
		return SUCCESS;
	}

	public String getSelect() {
		return select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public List<String> getOptions() {
		return options;
	}
}
