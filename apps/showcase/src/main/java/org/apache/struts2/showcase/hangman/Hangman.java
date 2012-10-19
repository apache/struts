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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Hangman implements Serializable {

	private static final long serialVersionUID = 8566954355839652509L;

	private Vocab vocab;

	private Boolean win = false;

	private int guessLeft = 5;
	public List<Character> charactersAvailable;
	public List<Character> charactersGuessed;

	public Hangman(Vocab vocab) {
		// Arrays.asList(...) returns List that doesn't support remove(), hence
		// we wrap it with an ArrayList to avoid UnsupportedOperationException
		// when doing a remove()
		charactersAvailable = new ArrayList<Character>(Arrays.asList(
				new Character[]{
						Character.valueOf('A'), Character.valueOf('B'), Character.valueOf('C'),
						Character.valueOf('D'), Character.valueOf('E'), Character.valueOf('F'),
						Character.valueOf('G'), Character.valueOf('H'), Character.valueOf('I'),
						Character.valueOf('J'), Character.valueOf('K'), Character.valueOf('L'),
						Character.valueOf('M'), Character.valueOf('N'), Character.valueOf('O'),
						Character.valueOf('P'), Character.valueOf('Q'), Character.valueOf('R'),
						Character.valueOf('S'), Character.valueOf('T'), Character.valueOf('U'),
						Character.valueOf('V'), Character.valueOf('W'), Character.valueOf('X'),
						Character.valueOf('Y'), Character.valueOf('Z')
				}));
		charactersGuessed = new ArrayList<Character>();
		this.vocab = vocab;
	}

	public void guess(Character character) {
		assert (character != null);

		synchronized (charactersAvailable) {
			if (guessLeft < 0) {
				throw new HangmanException(
						HangmanException.Type.valueOf("GAME_ENDED"), "Game already eneded");
			}
			Character characterInUpperCase = Character.toUpperCase(character);
			boolean ok = charactersAvailable.remove(characterInUpperCase);
			if (ok) {
				charactersGuessed.add(characterInUpperCase);
				if (!vocab.containCharacter(characterInUpperCase)) {
					guessLeft = guessLeft - 1;
				}
			}
			if (vocab.containsAllCharacter(charactersGuessed)) {
				win = true;
			}
			System.out.println(" *********************************** " + win);
		}
	}

	public Boolean isWin() {
		return this.win;
	}

	public Vocab getVocab() {
		return vocab;
	}

	public Boolean gameEnded() {
		return ((guessLeft < 0) || win);
	}

	public Integer guessLeft() {
		return guessLeft;
	}

	public List<Character> getCharactersAvailable() {
		synchronized (charactersAvailable) {
			return new ArrayList<Character>(charactersAvailable);
			//return charactersAvailable;
		}
	}

	public boolean characterGuessedBefore(Character character) {
		return charactersGuessed.contains(character);
	}
}
