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

import org.apache.struts2.components.Div;
import org.apache.struts2.components.UIBean;

public class DivTest extends AbstractCommonAttributesTest {
    private Div tag;

    public void testRenderAnchor() {
        tag.setName("name_");
        tag.setDisabled("true");
        tag.setId("id_");
        tag.setCssClass("class");
        tag.setCssStyle("style");
        tag.setTitle("title");

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        theme.renderTag(getTagName() + "-close", context);
        String output = writer.getBuffer().toString();
        String expected = s("<div name='name_' id='id_' class='class' style='style' title='title'></div>");
        assertEquals(expected, output);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.tag = new Div(stack, request, response);
    }

    @Override
    protected UIBean getUIBean() {
        return tag;
    }

    @Override
    protected String getTagName() {
        return "div";
    }
}
