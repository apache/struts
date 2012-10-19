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
package org.apache.struts2.showcase.freemarker;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

import java.text.DateFormatSymbols;

/**
 * Showcase action for freemarker templates.
 */
public class StandardTagsAction extends ActionSupport implements Preparable {

	private String name;
	private String[] gender;
	private String[] months;

	public void prepare() {
		months = new DateFormatSymbols().getMonths();
		name = StandardTagsAction.class.getName().substring(StandardTagsAction.class.getName().lastIndexOf(".") + 1);
		gender = new String[]{"Male", "Femal"};
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getMonths() {
		return months;
	}

	public void setMonths(String[] months) {
		this.months = months;
	}


	public String[] getGender() {
		return gender;
	}

	public void setGender(String[] gender) {
		this.gender = gender;
	}
}
