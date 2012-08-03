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
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.XWorkTestCase;

import java.util.HashMap;

public class WildcardHelperTest extends XWorkTestCase {
	
	public void testMatch() {
		
		WildcardHelper wild = new WildcardHelper();
		HashMap<String,String> matchedPatterns = new HashMap<String,String>();
		int[] pattern = wild.compilePattern("wes-rules");
		assertEquals(wild.match(matchedPatterns,"wes-rules", pattern), true);
		assertEquals(wild.match(matchedPatterns, "rules-wes", pattern), false);
		
		pattern = wild.compilePattern("wes-*");
		assertEquals(wild.match(matchedPatterns,"wes-rules", pattern), true);
		assertEquals("rules".equals(matchedPatterns.get("1")), true);
		assertEquals(wild.match(matchedPatterns, "rules-wes", pattern), false);
		
		pattern = wild.compilePattern("path/**/file");
		assertEquals(wild.match(matchedPatterns, "path/to/file", pattern), true);
		assertEquals("to".equals(matchedPatterns.get("1")), true);
		assertEquals(wild.match(matchedPatterns, "path/to/another/location/of/file", pattern), true);
		assertEquals("to/another/location/of".equals(matchedPatterns.get("1")), true);
		
		pattern = wild.compilePattern("path/*/file");
		assertEquals(wild.match(matchedPatterns, "path/to/file", pattern), true);
		assertEquals("to".equals(matchedPatterns.get("1")), true);
		assertEquals(wild.match(matchedPatterns, "path/to/another/location/of/file", pattern), false);

		pattern = wild.compilePattern("path/*/another/**/file");
		assertEquals(wild.match(matchedPatterns, "path/to/another/location/of/file", pattern), true);
		assertEquals("to".equals(matchedPatterns.get("1")), true);
		assertEquals("location/of".equals(matchedPatterns.get("2")), true);
	}

}
