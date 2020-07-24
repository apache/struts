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
package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.views.jsp.AbstractUITagTest;

import javax.servlet.jsp.JspException;

public class LinkTagTest extends AbstractUITagTest {

    private static final String NONCE_VAL = "r4andom";

    public void testLinkTagAttributes() {
        LinkTag tag = new LinkTag();

        tag.setHref("mysrc.js");
        tag.setHreflang("test");
        tag.setRel("module");
        tag.setMedia("foo");
        tag.setReferrerpolicy("test");
        tag.setSizes("foo");
        tag.setCrossorigin("same-origin");
        tag.setType("anonymous");
        tag.setAs("test");
        tag.setDisabled("false");
        tag.setTitle("test");

        doLinkTest(tag);
        String s = writer.toString();

        assertTrue("Incorrect href attribute for link tag", s.contains("href=\"mysrc.js\""));
        assertTrue("Incorrect hreflang attribute for link tag", s.contains("hreflang=\"test\""));
        assertTrue("Incorrect rel attribute for link tag", s.contains("rel=\"module\""));
        assertTrue("Incorrect media attribute for link tag", s.contains("media=\"foo\""));
        assertTrue("Incorrect referrerpolicy attribute for link tag", s.contains("referrerpolicy=\"test\""));
        assertTrue("Incorrect sizes attribute for link tag", s.contains("sizes=\"foo\""));
        assertTrue("Incorrect crossorigin attribute for link tag", s.contains("crossorigin=\"same-origin\""));
        assertTrue("Incorrect type attribute for link tag", s.contains("type=\"anonymous\""));
        assertTrue("Incorrect as attribute for link tag", s.contains("as=\"test\""));
        assertFalse("Non-existent disabled attribute for link tag", s.contains("disabled"));
        assertTrue("Incorrect title attribute for link tag", s.contains("title=\"test\""));
        assertTrue("Incorrect nonce attribute for link tag", s.contains("nonce=\"" + NONCE_VAL+"\""));
    }

    private void doLinkTest(LinkTag tag) {
        //creating nonce value like the CspInterceptor does
        stack.getActionContext().getSession().put("nonce", NONCE_VAL);
        tag.setPageContext(pageContext);

        try {
            tag.doStartTag();
            tag.doEndTag();
        } catch (JspException e) {
            e.printStackTrace();
            fail();
        }
    }
}
