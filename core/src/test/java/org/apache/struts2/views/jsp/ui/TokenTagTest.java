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

package org.apache.struts2.views.jsp.ui;

import javax.servlet.jsp.JspException;

import org.apache.struts2.util.TokenHelper;
import org.apache.struts2.views.jsp.AbstractUITagTest;


/**
 * TokenTagTest
 *
 */
public class TokenTagTest extends AbstractUITagTest {

    public void testDefaultName() {
        String tokenName = TokenHelper.DEFAULT_TOKEN_NAME;
        TokenTag tag = new TokenTag();
        doTokenTest(tokenName, tag);
    }

    public void testMultipleTagsWithSameName() {
        String tokenName = "sameName";
        TokenTag tag = new TokenTag();
        tag.setName(tokenName);

        String token = doTokenTest(tokenName, tag);

        TokenTag anotherTag = new TokenTag();
        anotherTag.setName(tokenName);

        String anotherToken = doTokenTest(tokenName, anotherTag);
        assertEquals(token, anotherToken);
    }

    /**
     * WW-480
     */
    public void testNotFindableName() {
        String tokenName = "foo";
        TokenTag tag = new TokenTag();
        tag.setName(tokenName);
        doTokenTest(tokenName, tag);

        String s = writer.toString();
        assertTrue(s.indexOf("name=\"" + TokenHelper.TOKEN_NAME_FIELD) > -1);
        assertTrue(s.indexOf("value=\"" + tokenName + "\"") > -1);
        assertTrue(s.indexOf("name=\"" + tokenName + "\"") > -1);

        //System.out.println(s);
    }

    public void testSuppliedName() {
        String tokenName = "my.very.long.token.name";
        TokenTag tag = new TokenTag();
        tag.setName(tokenName);
        doTokenTest(tokenName, tag);
    }

    private String doTokenTest(String tokenName, TokenTag tag) {
        tag.setPageContext(pageContext);

        String token = null;

        try {
            tag.doStartTag();
            tag.doEndTag();

            token = (String) context.get(tokenName);
			assertNotNull(token);
			final String sessionTokenName = TokenHelper.buildTokenSessionAttributeName(tokenName);
			assertEquals(token, pageContext.getSession().getAttribute(sessionTokenName));
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        return token;
    }
}
