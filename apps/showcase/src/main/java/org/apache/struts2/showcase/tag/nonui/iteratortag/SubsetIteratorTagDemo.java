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
package org.apache.struts2.showcase.tag.nonui.iteratortag;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Validateable;

/**
 *
 */
public class SubsetIteratorTagDemo extends ActionSupport implements Validateable {

	private static final long serialVersionUID = -8151855954644052650L;

	private String iteratorValue;
	private Integer count;
	private Integer start;


	public void validate() {
		if (iteratorValue == null || iteratorValue.trim().length() <= 0) {
			addFieldError("iteratorValue1", "iterator value 1 cannot be empty");
		} else if (iteratorValue.trim().indexOf(",") <= 0) {
			addFieldError("iteratorValue1", "iterator value 1 needs to be comma separated");
		}
	}


	public String getIteratorValue() {
		return this.iteratorValue;
	}

	public void setIteratorValue(String iteratorValue) {
		this.iteratorValue = iteratorValue;
	}


	public Integer getCount() {
		return this.count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}


	public Integer getStart() {
		return this.start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}


	public String input() throws Exception {
		return SUCCESS;
	}

	public String submit() throws Exception {
		return SUCCESS;
	}


}
