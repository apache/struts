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

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.action.SessionAware;
import org.apache.struts2.interceptor.parameter.StrutsParameter;

import java.util.Map;

public class GuessCharacterAction extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = 9050915577007590674L;

	private Map<String, Object> session;
	private Character character;
	private Hangman hangman;

	@Override
	public String execute() throws Exception {
		hangman = (Hangman) session.get(HangmanConstants.HANGMAN_SESSION_KEY);
		hangman.guess(character);

		return SUCCESS;
	}

	public Hangman getHangman() {
		return hangman;
	}

	@StrutsParameter
	public void setCharacter(Character character) {
		this.character = character;
	}

	public Character getCharacter() {
		return this.character;
	}

    @Override
    public void withSession(Map<String, Object> session) {
        this.session = session;
    }
}
