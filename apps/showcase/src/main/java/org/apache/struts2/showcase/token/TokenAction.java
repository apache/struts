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
package org.apache.struts2.showcase.token;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import java.util.Date;

/**
 * Example to illustrate the <code>token</code> and <code>tokenSession</code> interceptor.
 */
public class TokenAction extends ActionSupport {

	private static final long serialVersionUID = 616150375751184884L;

	private int amount;

	public String execute() throws Exception {
		// transfer from source to destination

		Integer balSource = (Integer) ActionContext.getContext().getSession().get("balanceSource");
		Integer balDest = (Integer) ActionContext.getContext().getSession().get("balanceDestination");

		Integer newSource = new Integer(balSource.intValue() - amount);
		Integer newDest = new Integer(balDest.intValue() + amount);

		ActionContext.getContext().getSession().put("balanceSource", newSource);
		ActionContext.getContext().getSession().put("balanceDestination", newDest);
		ActionContext.getContext().getSession().put("time", new Date());

		Thread.sleep(2000); // to simulate processing time

		return SUCCESS;
	}

	public String input() throws Exception {
		// prepare input form
		Integer balSource = (Integer) ActionContext.getContext().getSession().get("balanceSource");
		Integer balDest = (Integer) ActionContext.getContext().getSession().get("balanceDestination");

		if (balSource == null) {
			// first time set up an initial account balance
			balSource = new Integer(1200);
			ActionContext.getContext().getSession().put("balanceSource", balSource);
		}

		if (balDest == null) {
			// first time set up an initial account balance
			balDest = new Integer(2500);
			ActionContext.getContext().getSession().put("balanceDestination", balDest);
		}

		return INPUT;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

}
