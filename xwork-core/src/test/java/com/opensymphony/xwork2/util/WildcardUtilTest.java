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

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class WildcardUtilTest extends XWorkTestCase {
	
	public void testPattern() {
		
		Pattern p = WildcardUtil.compileWildcardPattern("a*b");
		assertTrue(p.matcher("ab").matches());
		assertTrue(p.matcher("axyb").matches());
		assertFalse(p.matcher("bxyb").matches());
		
		p = WildcardUtil.compileWildcardPattern("a\\*b");
		assertFalse(p.matcher("ab").matches());
		assertTrue(p.matcher("a*b").matches());
		
		p = WildcardUtil.compileWildcardPattern("a.*");
		assertFalse(p.matcher("ab").matches());
		assertFalse(p.matcher("ab.b").matches());
		assertTrue(p.matcher("a.b").matches());
		assertTrue(p.matcher("a.bc").matches());
		assertTrue(p.matcher("a.b.c").matches());
		
		p = WildcardUtil.compileWildcardPattern("a[*]");
		assertFalse(p.matcher("ab").matches());
		assertFalse(p.matcher("ab[b]").matches());
		assertTrue(p.matcher("a[b]").matches());
		assertTrue(p.matcher("a[bc]").matches());
		assertFalse(p.matcher("a[b].c").matches());

		p = WildcardUtil.compileWildcardPattern("a[*].*");
		assertTrue(p.matcher("a[b].c").matches());
		assertTrue(p.matcher("a[bc].cd").matches());
	}

}
