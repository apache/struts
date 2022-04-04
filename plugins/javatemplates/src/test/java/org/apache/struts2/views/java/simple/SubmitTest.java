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

import org.apache.struts2.components.Submit;
import org.apache.struts2.components.UIBean;

public class SubmitTest extends AbstractCommonAttributesTest {
    private Submit tag;

    public void testRenderButtonWithBody() {
        tag.setName("name");
        tag.setValue("val1");
        tag.setDisabled("true");
        tag.setTabindex("1");
        tag.setId("id1");
        tag.setCssClass("class1");
        tag.setCssStyle("style1");
        tag.setTitle("title");
        tag.setLabel("some label");
        tag.setType("button");

        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);

        tag.addParameter("body", "<span>hey hey hey, here I go now</span>");
        map.clear();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName() + "-close", context);
        String output = writer.getBuffer().toString();
        String expected = s("<button name='name' type='submit' value='val1' tabindex='1' id='id1' class='class1' style='style1'><span>hey hey hey, here I go now</span></button>");
        assertEquals(expected, output);
    }

    public void testRenderButtonWithLabel() {
        tag.setName("name");
        tag.setValue("val1");
        tag.setDisabled("true");
        tag.setTabindex("1");
        tag.setId("id1");
        tag.setCssClass("class1");
        tag.setCssStyle("style1");
        tag.setTitle("title");
        tag.setLabel("Just as soon as I belong, than its time I disappear");
        tag.setType("button");


        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        theme.renderTag(getTagName() + "-close", context);
        String output = writer.getBuffer().toString();
        String expected = s("<button name='name' type='submit' value='val1' tabindex='1' id='id1' class='class1' style='style1'>Just as soon as I belong, than its time I disappear</button>");
        assertEquals(expected, output);
    }

    public void testRenderButtonSubmit() {
        tag.setName("name");
        tag.setValue("val1");
        tag.setDisabled("true");
        tag.setTabindex("1");
        tag.setId("id1");
        tag.setCssClass("class1");
        tag.setCssStyle("style1");
        tag.setTitle("title");
        tag.setLabel("label");
        tag.setType("input");


        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        theme.renderTag(getTagName() + "-close", context);
        String output = writer.getBuffer().toString();
        String expected = s("<input name='name' type='submit' value='val1' tabindex='1' id='id1' class='class1' style='style1'></input>");
        assertEquals(expected, output);
    }

    public void testRenderButtonWithoutType() {
        tag.setName("name");
        tag.setValue("val1");
        tag.setDisabled("true");
        tag.setTabindex("1");
        tag.setId("id1");
        tag.setCssClass("class1");
        tag.setCssStyle("style1");
        tag.setTitle("title");
        tag.setLabel("label");


        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        theme.renderTag(getTagName() + "-close", context);
        String output = writer.getBuffer().toString();
        String expected = s("<input name='name' type='submit' value='val1' tabindex='1' id='id1' class='class1' style='style1'></input>");
        assertEquals(expected, output);
    }

    public void testRenderButtonImage() {
        tag.setSrc("http://somesource/image.gif");
        tag.setLabel("alt text");
        tag.setType("image");


        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        theme.renderTag(getTagName() + "-close", context);
        String output = writer.getBuffer().toString();
        String expected = s("<input src='http://somesource/image.gif' type='image' alt='alt text'></input>");
        assertEquals(expected, output);
    }

    public void testRenderButtonImageWithBody() {
        tag.setSrc("http://somesource/image.gif");
        tag.setLabel("alt text");
        tag.setType("image");


        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        map.clear();
        tag.setType("image");
        tag.addParameter("body", "<span>hey hey hey, here I go now</span>");
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName() + "-close", context);
        String output = writer.getBuffer().toString();
        String expected = s("<input src='http://somesource/image.gif' type='image' alt='alt text'><span>hey hey hey, here I go now</span></input>");
        assertEquals(expected, output);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.tag = new Submit(stack, request, response);
    }

    @Override
    protected UIBean getUIBean() {
        return tag;
    }

    @Override
    protected String getTagName() {
        return "submit";
    }
}
