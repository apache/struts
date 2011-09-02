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

import org.apache.struts2.components.File;
import org.apache.struts2.components.UIBean;

public class FileTest extends AbstractCommonAttributesTest {
    private File tag;

    public void testRenderTextField() {
        tag.setName("name");
        tag.setValue("val1");
        tag.setSize("10");
        tag.setDisabled("true");
        tag.setAccept("accept_");
        tag.setTabindex("1");
        tag.setId("id1");
        tag.setCssClass("class1");
        tag.setCssStyle("style1");
        tag.setTitle("title");


        tag.evaluateParams();
        map.putAll(tag.getParameters());
        theme.renderTag(getTagName(), context);
        String output = writer.getBuffer().toString();
        String expected = s("<input name='name' type='file' size='10' value='val1' accept='accept_' tabindex='1' id='id1' class='class1' style='style1' title='title'></input>");
        assertEquals(expected, output);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.tag = new File(stack, request, response);
    }

    @Override
    protected UIBean getUIBean() {
        return tag;
    }

    @Override
    protected String getTagName() {
        return "file";
    }
}

