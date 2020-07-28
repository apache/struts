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

public class ScriptTagTest extends AbstractUITagTest {

    private static final String NONCE_VAL = "r4andom";

    public void testScriptTagAttributes() {
        ScriptTag tag = new ScriptTag();


        tag.setSrc("mysrc.js");
        tag.setAsync("false");
        tag.setType("module");
        tag.setCharset("foo");
        tag.setNomodule("true");
        tag.setDefer("true");
        tag.setReferrerpolicy("same-origin");
        tag.setCrossorigin("anonymous");
        tag.setIntegrity("test");

        doScriptTest(tag);
        String s = writer.toString();
        
        assertTrue("Incorrect src attribute for script tag", s.contains("src=\"mysrc.js\""));
        assertFalse("Non-existent async attribute for script tag", s.contains("async"));
        assertTrue("Incorrect type attribute for script tag", s.contains("type=\"module\""));
        assertTrue("Incorrect charset attribute for script tag", s.contains("charset=\"foo\""));
        assertTrue("Non-existent nomodule attribute for script tag", s.contains("nomodule"));
        assertTrue("Non-existent defer attribute for script tag", s.contains("defer"));
        assertTrue("Incorrect referrerpolicy attribute for script tag", s.contains("referrerpolicy=\"same-origin\""));
        assertTrue("Incorrect crossorigin attribute for script tag", s.contains("crossorigin=\"anonymous\""));
        assertTrue("Incorrect integrity attribute for script tag", s.contains("integrity=\"test\""));
        assertTrue("Incorrect nonce attribute for script tag", s.contains("nonce=\"" + NONCE_VAL+"\""));
    }

    private void doScriptTest(ScriptTag tag) {
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
