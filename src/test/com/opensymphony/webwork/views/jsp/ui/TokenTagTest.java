/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import com.opensymphony.webwork.util.TokenHelper;
import com.opensymphony.webwork.views.jsp.AbstractUITagTest;

import javax.servlet.jsp.JspException;


/**
 * TokenTagTest
 *
 * @author Jason Carreira
 *         Created Apr 10, 2003 7:12:12 PM
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
        assertTrue(s.indexOf("name=\"" + TokenHelper.DEFAULT_TOKEN_NAME) > -1);
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
            assertEquals(token, pageContext.getSession().getAttribute(tokenName));
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }

        return token;
    }
}
