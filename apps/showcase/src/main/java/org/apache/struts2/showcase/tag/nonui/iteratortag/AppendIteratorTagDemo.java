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
public class AppendIteratorTagDemo extends ActionSupport implements Validateable {

	private static final long serialVersionUID = -6525059998526094664L;

	private String iteratorValue1;
	private String iteratorValue2;


	public void validate() {
		if (iteratorValue1 == null || iteratorValue1.trim().length() <= 0) {
			addFieldError("iteratorValue1", "iterator value 1 cannot be empty");
		} else if (iteratorValue1.trim().indexOf(",") <= 0) {
			addFieldError("iteratorValue1", "iterator value 1 needs to be comma separated");
		}
		if (iteratorValue2 == null || iteratorValue2.trim().length() <= 0) {
			addFieldError("iteratorValue2", "iterator value 2 cannot be empty");
		} else if (iteratorValue2.trim().indexOf(",") <= 0) {
			addFieldError("iteratorValue2", "iterator value 2 needs to be comma separated");
		}
	}


	public String getIteratorValue1() {
		return iteratorValue1;
	}

	public void setIteratorValue1(String iteratorValue1) {
		this.iteratorValue1 = iteratorValue1;
	}


	public String getIteratorValue2() {
		return iteratorValue2;
	}

	public void setIteratorValue2(String iteratorValue2) {
		this.iteratorValue2 = iteratorValue2;
	}


	public String input() throws Exception {
		return SUCCESS;
	}

	public String submit() throws Exception {
		return SUCCESS;
	}
}
