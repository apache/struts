/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.action2.views.jsp.ui;

import org.apache.struts.action2.TestAction;
import org.apache.struts.action2.views.jsp.AbstractUITagTest;

import java.util.HashMap;


/**
 */
public class RadioTest extends AbstractUITagTest {

    public void testMapChecked() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");

        HashMap map = new HashMap();
        map.put("1", "One");
        map.put("2", "Two");
        testAction.setMap(map);

        RadioTag tag = new RadioTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setValue("\"1\"");
        tag.setList("map");
        tag.setListKey("key");
        tag.setListValue("value");

        tag.doStartTag();
        tag.doEndTag();

        verify(RadioTag.class.getResource("Radio-2.txt"));
    }

    public void testSimple() throws Exception {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");
        testAction.setList(new String[][]{
                {"hello", "world"},
                {"foo", "bar"}
        });

        RadioTag tag = new RadioTag();
        tag.setPageContext(pageContext);
        tag.setLabel("mylabel");
        tag.setName("myname");
        tag.setValue("");
        tag.setList("list");
        tag.setListKey("top[0]");
        tag.setListValue("top[1]");

        tag.doStartTag();
        tag.doEndTag();

        verify(RadioTag.class.getResource("Radio-1.txt"));
    }

    public void testGenericSimple() throws Exception {
        RadioTag tag = new RadioTag();
        prepareTagGeneric(tag);
        verifyGenericProperties(tag, "simple", new String[]{"id","value"});
    }

    public void testGenericXhtml() throws Exception {
        RadioTag tag = new RadioTag();
        prepareTagGeneric(tag);
        verifyGenericProperties(tag, "xhtml", new String[]{"id","value"});
    }

    public void testGenericAjax() throws Exception {
        RadioTag tag = new RadioTag();
        prepareTagGeneric(tag);
        verifyGenericProperties(tag, "ajax", new String[]{"id","value"});
    }

    private void prepareTagGeneric(RadioTag tag) {
        TestAction testAction = (TestAction) action;
        testAction.setFoo("bar");
        testAction.setList(new String[][]{
                {"hello", "world"},
                {"foo", "bar"}
        });
        tag.setList("list");
        tag.setListKey("top[0]");
        tag.setListValue("top[1]");
    }

}
