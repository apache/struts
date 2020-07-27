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
import org.apache.struts2.components.Link;
import org.apache.struts2.components.UIBean;

import java.util.HashMap;
import java.util.Map;

public class LinkTest extends AbstractTest{

    private Link tag;

    private static final String NONCE_VAL = "r4andom";

    public void testRenderScriptTag() {
        tag.setHref("testhref");
        tag.setHreflang("test");
        tag.setRel("module");
        tag.setMedia("foo");
        tag.setReferrerpolicy("test");
        tag.setSizes("foo");
        tag.setCrossorigin("same-origin");
        tag.setType("anonymous");
        tag.setAs("test");
        tag.setDisabled("disabled_");
        tag.setTitle("test");

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String s = writer.getBuffer().toString();

        assertTrue("Incorrect href attribute for link tag", s.contains("href=\"testhref\""));
        assertTrue("Incorrect hreflang attribute for link tag", s.contains("hreflang=\"test\""));
        assertTrue("Incorrect rel attribute for link tag", s.contains("rel=\"module\""));
        assertTrue("Incorrect media attribute for link tag", s.contains("media=\"foo\""));
        assertTrue("Incorrect referrerpolicy attribute for link tag", s.contains("referrerpolicy=\"test\""));
        assertTrue("Incorrect sizes attribute for link tag", s.contains("sizes=\"foo\""));
        assertTrue("Incorrect crossorigin attribute for link tag", s.contains("crossorigin=\"same-origin\""));
        assertTrue("Incorrect type attribute for link tag", s.contains("type=\"anonymous\""));
        assertTrue("Incorrect as attribute for link tag", s.contains("as=\"test\""));
        assertTrue("Non-existent disabled attribute for link tag", s.contains("disabled=\"disabled_\""));
        assertTrue("Incorrect title attribute for link tag", s.contains("title=\"test\""));
        assertTrue("Incorrect nonce attribute for link tag", s.contains("nonce=\"" + NONCE_VAL+"\""));
    }
    @Override
    protected UIBean getUIBean() throws Exception {
        return tag;
    }

    @Override
    protected String getTagName() {
        return "link";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        ActionContext actionContext = stack.getActionContext();
        Map<String, Object> session = new HashMap<>();
        session.put("nonce", NONCE_VAL);
        actionContext.withSession(session);

        this.tag = new Link(stack, request, response);
    }
}
