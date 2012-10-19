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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PropertiesVocabSource implements VocabSource {

	private Properties prop;
	private List<Vocab> vocabs;

	public PropertiesVocabSource() {
	}

	public PropertiesVocabSource(Properties prop) {
		assert (prop != null);
		this.prop = prop;
		vocabs = readVocab(prop);
	}

	public void setVocabProperties(Properties prop) {
		assert (prop != null);
		this.prop = prop;
		vocabs = readVocab(prop);
	}

	public Vocab getRandomVocab() {
		if (vocabs == null) {
			throw new HangmanException(HangmanException.Type.valueOf("NO_VOCAB_SOURCE"), "No vocab source");
		}
		if (vocabs.size() <= 0) {
			throw new HangmanException(HangmanException.Type.valueOf("NO_VOCAB"), "No vocab");
		}
		long vocabIndex = Math.round((Math.random() * (double) prop.size()));
		vocabIndex = vocabIndex == vocabs.size() ? vocabs.size() - 1 : vocabIndex;
		return vocabs.get((int) vocabIndex);
	}

	protected List<Vocab> readVocab(Properties prop) {
		List<Vocab> vocabList = new ArrayList<Vocab>();

		for (Map.Entry e : prop.entrySet()) {
			String vocab = (String) e.getKey();
			String hint = (String) e.getValue();

			vocabList.add(new Vocab(vocab, hint));
		}
		return vocabList;
	}
}
