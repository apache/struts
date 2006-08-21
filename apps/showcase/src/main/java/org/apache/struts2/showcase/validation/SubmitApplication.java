/*
 * $Id: QuizAction.java 420385 2006-07-10 00:57:05Z mrdon $
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.showcase.validation;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 
 * @version $Date$ $Id$
 */
public class SubmitApplication extends ActionSupport {
	
	private String name;
	private Integer age;
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	
	public void setAge(Integer age) {
		this.age = age;
	}
	public Integer getAge() {
		return age;
	}
	
	public String submitApplication() throws Exception {
		return SUCCESS;
	}
	
	public String applicationOk() throws Exception {
		addActionMessage("Your application looks ok.");
		return SUCCESS;
	}
	public String cancelApplication() throws Exception {
		addActionMessage("So you have decided to cancel the application");
		return SUCCESS;
	}
}
