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

import org.apache.struts2.interceptor.parameter.StrutsParameter;

/**
 */

// START SNIPPET: nonFieldValidatorsExample

public class NonFieldValidatorsExampleAction extends AbstractValidationActionSupport {

	private static final long serialVersionUID = -524460368233581186L;

	private String someText;
	private String someTextRetype;
	private String someTextRetypeAgain;

	public String getSomeText() {
		return someText;
	}

	@StrutsParameter
	public void setSomeText(String someText) {
		this.someText = someText;
	}

	public String getSomeTextRetype() {
		return someTextRetype;
	}

	@StrutsParameter
	public void setSomeTextRetype(String someTextRetype) {
		this.someTextRetype = someTextRetype;
	}

	public String getSomeTextRetypeAgain() {
		return someTextRetypeAgain;
	}

	@StrutsParameter
	public void setSomeTextRetypeAgain(String someTextRetypeAgain) {
		this.someTextRetypeAgain = someTextRetypeAgain;
	}
}


// END SNIPPET: nonFieldValidatorsExample



