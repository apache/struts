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
package org.apache.struts2.views.java.simple;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.security.DefaultNotExcludedAcceptedPatternsChecker;
import org.apache.struts2.components.Script;
import org.apache.struts2.components.UIBean;


import java.util.HashMap;
import java.util.Map;


public class ScriptTest extends AbstractTest {

    private Script tag;

    private static final String NONCE_VAL = "r4andom";

    public void testRenderScriptTag() {
        tag.setName("name_");
        tag.setType("text/javascript");
        tag.setSrc("mysrc");
        tag.setAsync("false");
        tag.setDefer("false");
        tag.setCharset("test");
        tag.setReferrerpolicy("foo");
        tag.setNomodule("bar");
        tag.setIntegrity("test");
        tag.setCrossorigin("test");

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();

        assertTrue("Script doesn't have nonce attribute", output.contains("nonce="));
        assertTrue("Script doesn't have type attribute", output.contains("type="));
        assertTrue("Script doesn't have src attribute", output.contains("src="));
        assertTrue("Script doesn't have async attribute", output.contains("async"));
        assertTrue("Script doesn't have defer attribute", output.contains("defer"));
        assertTrue("Script doesn't have charset attribute", output.contains("charset="));
        assertTrue("Script doesn't have referrerpolicy attribute", output.contains("referrerpolicy="));
        assertTrue("Script doesn't have nomodule attribute", output.contains("nomodule"));
        assertTrue("Script doesn't have integrity attribute", output.contains("integrity="));
        assertTrue("Script doesn't have crossorigin attribute", output.contains("crossorigin="));
    }

    @Override
    protected UIBean getUIBean() throws Exception {
        return tag;
    }

    @Override
    protected String getTagName() {
        return "script";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        ActionContext actionContext = stack.getActionContext();
        Map<String, Object> session = new HashMap<>();
        session.put("nonce", NONCE_VAL);
        actionContext.withSession(session);

        this.tag = new Script(stack, request, response);
        tag.setNotExcludedAcceptedPatterns(new DefaultNotExcludedAcceptedPatternsChecker());
    }
}
