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
package org.apache.struts2.showcase.validation;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.parameter.StrutsParameter;

/**
 */

// START SNIPPET: quizAction

public class QuizAction extends ActionSupport {

	private static final long serialVersionUID = -7505437345373234225L;

	String name;
	int age;
	String answer;

	public String getName() {
		return name;
	}

	@StrutsParameter
	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	@StrutsParameter
	public void setAge(int age) {
		this.age = age;
	}

	public String getAnswer() {
		return answer;
	}

	@StrutsParameter
	public void setAnswer(String answer) {
		this.answer = answer;
	}
}

// END SNIPPET: quizAction

