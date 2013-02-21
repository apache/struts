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
package org.apache.struts2.showcase.model;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.Serializable;

/**
 * Skill.
 */

public class Skill implements IdEntity {

	private static final long serialVersionUID = -4150317722693212439L;

	private String name;
	private String description;

	public Skill() {
	}

	public Skill(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return StringEscapeUtils.escapeEcmaScript(StringEscapeUtils.escapeHtml4(name));
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return StringEscapeUtils.escapeEcmaScript(StringEscapeUtils.escapeHtml4(description));
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Serializable getId() {
		return getName();
	}

	public void setId(Serializable id) {
		setName((String) id);
	}

	public String toString() {
		return getName();
	}
}
