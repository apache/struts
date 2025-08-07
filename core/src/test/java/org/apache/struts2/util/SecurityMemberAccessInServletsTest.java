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
package org.apache.struts2.util;

import org.apache.struts2.ognl.SecurityMemberAccess;
import jakarta.servlet.jsp.tagext.TagSupport;
import org.apache.struts2.StrutsInternalTestCase;
import org.apache.struts2.ognl.StrutsContext;
import org.apache.struts2.views.jsp.ActionTag;

import java.lang.reflect.Member;

public class SecurityMemberAccessInServletsTest extends StrutsInternalTestCase {

    private StrutsContext context;

    @Override
    public void setUp() throws Exception {
        context = StrutsContext.empty();
    }

    public void testJavaxServletPackageAccess() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(null, null);

        sma.useExcludedPackageNamePatterns("^(?!jakarta\\.servlet\\..+)(jakarta\\..+)");

        String propertyName = "value";
        Member member = TagSupport.class.getMethod("doStartTag");

        // when
        boolean actual = sma.isAccessible(context, new ActionTag(), member, propertyName);

        // then
        assertTrue("jakarta.servlet package isn't accessible!", actual);
    }

    public void testJavaxServletPackageExclusion() throws Exception {
        // given
        SecurityMemberAccess sma = new SecurityMemberAccess(null, null);

        sma.useExcludedPackageNamePatterns("^jakarta\\..+");

        String propertyName = "value";
        Member member = TagSupport.class.getMethod("doStartTag");

        // when
        boolean actual = sma.isAccessible(context, new ActionTag(), member, propertyName);

        // then
        assertFalse("jakarta.servlet package is accessible!", actual);
    }

}
