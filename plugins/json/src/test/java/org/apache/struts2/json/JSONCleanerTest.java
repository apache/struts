/*
 * $Id$
 *
 * Copyright 2003-2004 The Apache Software Foundation.
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
package org.apache.struts2.json;

import junit.framework.TestCase;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class JSONCleanerTest extends TestCase {

	public void testDefaultBlock1() throws JSONException {

		JSONCleaner cleaner = getCleaner();
		cleaner.setDefaultBlock(true);
		cleaner.setAllowed("a,c");

		Map data = getData();
		cleaner.clean("", data);
		assertEquals(2, data.size());
		assertEquals("x", data.get("a"));
		assertNull(data.get("b"));
		assertNotNull(data.get("c"));
		assertNull(data.get("d"));

	}

	public void testDefaultBlock2() throws JSONException {

		JSONCleaner cleaner = getCleaner();
		cleaner.setDefaultBlock(true);
		cleaner.setAllowed("a,c,d.x");

		Map data = getData();
		cleaner.clean("", data);
		assertEquals(3, data.size());
		assertEquals("x", data.get("a"));
		assertNull(data.get("b"));
		assertNotNull(data.get("c"));
		assertNotNull(data.get("d"));
		assertEquals(1, ((Map) data.get("d")).size());
		assertEquals("a", ((Map) data.get("d")).get("x"));
		assertNull(((Map) data.get("d")).get("y"));

	}

	public void testDefaultAllow1() throws JSONException {

		JSONCleaner cleaner = getCleaner();
		cleaner.setDefaultBlock(false);
		cleaner.setBlocked("b,d");

		Map data = getData();
		cleaner.clean("", data);
		assertEquals(2, data.size());
		assertEquals("x", data.get("a"));
		assertNull(data.get("b"));
		assertNotNull(data.get("c"));
		assertNull(data.get("d"));

	}

	public void testDefaultAllow2() throws JSONException {

		JSONCleaner cleaner = getCleaner();
		cleaner.setDefaultBlock(false);
		cleaner.setBlocked("b,d.y");

		Map data = getData();
		cleaner.clean("", data);
		assertEquals(3, data.size());
		assertEquals("x", data.get("a"));
		assertNull(data.get("b"));
		assertNotNull(data.get("c"));
		assertNotNull(data.get("d"));
		assertEquals(1, ((Map) data.get("d")).size());
		assertEquals("a", ((Map) data.get("d")).get("x"));
		assertNull(((Map) data.get("d")).get("y"));

	}

	private JSONCleaner getCleaner() {

		return new JSONCleaner() {
			protected Object cleanValue(String ognlName, Object data) throws JSONException {
				return data;
			}
		};

	}

	private Map getData() {

		Map data = new HashMap();
		data.put("a", "x");
		data.put("b", "y");

		List list = new ArrayList();
		list.add("p");
		list.add("q");
		list.add("r");
		data.put("c", list);

		Map map = new HashMap();
		map.put("x", "a");
		map.put("y", "b");
		data.put("d", map);

		return data;

	}

}
