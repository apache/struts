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

import org.apache.struts2.components.Anchor;
import org.apache.struts2.components.UIBean;
import org.apache.struts2.components.ServletUrlRenderer;

public class AnchorTest extends AbstractTest {
    private Anchor tag;

    public void testRenderAnchor() {
        tag.setName("name_");
        tag.setDisabled("true");
        tag.setTabindex("1");
        tag.setId("id_");
        tag.setCssClass("class");
        tag.setCssStyle("style");
        tag.setTitle("title");
        tag.setHref("http://sometest.com?ab=10");

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        theme.renderTag(getTagName() + "-close", context);
        String output = writer.getBuffer().toString();
        String expected = s("<a name='name_' id='id_' class='class' style='style' href='http://sometest.com?ab=10' title='title' tabindex='1'></a>");
        assertEquals(expected, output);
    }

    public void testRenderScriptingEvents() {
        tag.setName("name_");
        tag.setOnclick("alert('click')");
        tag.setOnchange("alert('change)");
        tag.setOnfocus("alert('focus')");
        tag.setOnselect("alert('select')");
        tag.setOndblclick("alert('dbclick')");
        tag.setOnkeydown("alert('keydown')");
        tag.setOnkeypress("alert('keypress')");
        tag.setHref("http://sometest.com?ab=10");

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        theme.renderTag(getTagName() + "-close", context);
        String output = writer.getBuffer().toString();
        String expected = "<a name=\"name_\" id=\"name_\" href=\"http://sometest.com?ab=10\" onclick=\"alert('click')\" " +
                "ondblclick=\"alert('dbclick')\" onfocus=\"alert('focus')\" onkeypress=\"alert('keypress')\" " +
                "onkeydown=\"alert('keydown')\" onselect=\"alert('select')\" onchange=\"alert('change)\"></a>";
        assertEquals(expected, output);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.tag = new Anchor(stack, request, response);
        this.tag.setUrlRenderer(new ServletUrlRenderer());
    }

    @Override
    protected UIBean getUIBean() {
        return tag;
    }

    @Override
    protected String getTagName() {
        return "a";
    }
}
