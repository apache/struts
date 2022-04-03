/*
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
package com.opensymphony.xwork2.spring;

public class TestAspect {
	protected String log = "";
	
	private String issueId;
	private int count;
	private String name;
	private int count2;
    private boolean exposeProxy;

	String getIssueId() {
		return issueId;
	}

	public void setIssueId(String issueId) {
		log = log + "setIssueId(" + issueId + ")-";
		this.issueId = issueId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		log = log + "setCount(" + count + ")-";
		this.count = count;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		log = log + "setName(" + name + ")-";
		this.name = name;
	}

	int getCount2() {
		return count2;
	}

	public void setCount2(int count2) {
		log = log + "setCount2(" + count2 + ")-";
		this.count2 = count2;
	}

    public void setExposeProxy(boolean exposeProxy) {
        this.exposeProxy = exposeProxy;
    }
}
