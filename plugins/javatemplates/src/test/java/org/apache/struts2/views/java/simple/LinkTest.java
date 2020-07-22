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
import org.apache.struts2.components.Script;
import org.apache.struts2.components.UIBean;

import java.util.HashMap;
import java.util.Map;

public class LinkTest extends AbstractTest{

    private Link tag;

    public void testRenderScriptTag() {
        tag.setHref("testlink.com");
        tag.setHreflang("en");
        tag.setRel("preload");
        tag.setMedia("media_");
        tag.setSizes("sizes_");
        tag.setReferrerpolicy("foo");
        tag.setCrossorigin("same-origin");
        tag.setType("type_");
        tag.setAs("as_");
        tag.setDisabled("disabled");
        tag.setTitle("title_");
        tag.setIntegrity("test");
        tag.setImportance("auto");

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();

        assertTrue("Link doesn't have nonce attribute", output.contains("nonce="));
        assertTrue("Link doesn't have href attribute", output.contains("href="));
        assertTrue("Link doesn't have hreflang attribute", output.contains("hreflang="));
        assertTrue("Link doesn't have rel attribute", output.contains("rel="));
        assertTrue("Link doesn't have media attribute", output.contains("media"));
        assertTrue("Link doesn't have sizes attribute", output.contains("sizes"));
        assertTrue("Link doesn't have crossorigin attribute", output.contains("crossorigin="));
        assertTrue("Link doesn't have referrerpolicy attribute", output.contains("referrerpolicy="));
        assertTrue("Link doesn't have type attribute", output.contains("type="));
        assertTrue("Link doesn't have as attribute", output.contains("as="));
        assertTrue("Link doesn't have disabled attribute", output.contains("disabled="));
        assertTrue("Link doesn't have title attribute", output.contains("title="));
        assertTrue("Link doesn't have integrity attribute", output.contains("integrity="));
        assertTrue("Link doesn't have importance attribute", output.contains("importance="));
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
        session.put("nonce", "r4nd0m");
        actionContext.withSession(session);

        this.tag = new Link(stack, request, response);
    }
}
