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
package org.apache.struts2.views.java.simple;

import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.components.Token;
import org.apache.struts2.components.UIBean;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class TokenTest extends AbstractTest {
    private Token tag;

    public void testRenderTokenTag() {
        tag.setName("name");
        tag.setValue("val1");

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();


        //token id is random
        String pattern = s("<input type='hidden' name='struts.token.name' value='name'></input><input type='hidden' name='name' value='.*?'></input>");
        assertTrue(Pattern.matches(pattern, output));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.tag = new Token(stack, request, response);

        Map map = new HashMap();
        map.put(ActionContext.SESSION, new HashMap());
        ActionContext.setContext(new ActionContext(map));
    }

    @Override
    protected UIBean getUIBean() {
        return tag;
    }

    @Override
    protected String getTagName() {
        return "token";
    }
}
