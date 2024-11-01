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
package org.apache.struts2.showcase.hangman;

import org.apache.struts2.ActionSupport;

import java.util.Map;

import org.apache.struts2.action.SessionAware;

public class GetUpdatedHangmanAction extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = 5506025785406043027L;

	private Map<String, Object> session;
	private Hangman hangman;


	public String execute() throws Exception {
		hangman = (Hangman) session.get(HangmanConstants.HANGMAN_SESSION_KEY);

		System.out.println("\n\n\n");
		System.out.println("hangman=" + hangman);
		System.out.println("available = " + hangman.getCharactersAvailable().size());
		System.out.println("guess left=" + hangman.guessLeft());
		System.out.println("\n\n\n");

		return SUCCESS;
	}

	public Hangman getHangman() {
		return hangman;
	}

	public void setHangman(Hangman hangman) {
		this.hangman = hangman;
	}

    @Override
    public void withSession(Map<String, Object> session) {
        this.session = session;
    }
}
