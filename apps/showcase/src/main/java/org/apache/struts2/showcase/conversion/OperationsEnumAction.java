/*
 * $Id: Person.java 440597 2006-09-06 03:34:39Z tmjee $
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
package org.apache.struts2.showcase.conversion;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 
 * @version $Date$ $Id$
 */
public class OperationsEnumAction extends ActionSupport {

	private static final long serialVersionUID = -2229489704988870318L;
	
	private List<OperationsEnum> selectedOperations = new LinkedList<OperationsEnum>();
	
	public List<OperationsEnum> getSelectedOperations() { return this.selectedOperations; }
	public void setSelectedOperations(List<OperationsEnum> selectedOperations) {
		this.selectedOperations = selectedOperations;
	}
	
	
	public List<OperationsEnum> getAvailableOperations() {
		return Arrays.asList(OperationsEnum.values());
	}
	
	public String input() throws Exception {
		return SUCCESS;
	}
	public String submit() throws Exception {
		return SUCCESS;
	}
}

