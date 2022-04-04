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

package org.apache.struts2.util;

import com.opensymphony.xwork2.ognl.SecurityMemberAccess;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.TestAction;

import javax.servlet.jsp.tagext.TagSupport;
import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class SecurityMemberAccessInServletsTest extends StrutsInternalTestCase {

    private Map context;

    @Override
    public void setUp() throws Exception {
        context = new HashMap();
    }

    public void testJavaxServletPackageAccess() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false);

        Set<Pattern> excluded = new HashSet<Pattern>();
        excluded.add(Pattern.compile("^(?!javax\\.servlet\\..+)(javax\\..+)"));
        sma.setExcludedPackageNamePatterns(excluded);

        String propertyName = "value";
        Member member = TagSupport.class.getMethod("doStartTag");

        // when
        boolean actual = sma.isAccessible(context, new TestAction(), member, propertyName);

        // then
        assertTrue("javax.servlet package isn't accessible!", actual);
    }

    public void testJavaxServletPackageExclusion() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(false);

        Set<Pattern> excluded = new HashSet<Pattern>();
        excluded.add(Pattern.compile("^javax\\..+"));
        sma.setExcludedPackageNamePatterns(excluded);

        String propertyName = "value";
        Member member = TagSupport.class.getMethod("doStartTag");

        // when
        boolean actual = sma.isAccessible(context, new TestAction(), member, propertyName);

        // then
        assertFalse("javax.servlet package is accessible!", actual);
    }

}
