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

package org.apache.struts2.views.jsp.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.views.jsp.AbstractUITagTest;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Test case for UpDownSelectTag
 *
 */
public class UpDownSelectTagTest extends AbstractUITagTest {

    public void testWithAllSelected() throws Exception {

        UpDownSelectTag tag = new UpDownSelectTag();
        tag.setPageContext(pageContext);
        tag.setId("myId");
        tag.setName("myName");
        tag.setList("myMap");
        tag.setValue("myAllSelectedMapIds");
        tag.setEmptyOption("true");

        tag.doStartTag();
        tag.doEndTag();

        verify(UpDownSelectTagTest.class.getResource("updownselecttag-1.txt"));
    }

    public void testWithPartialSelected() throws Exception {

        UpDownSelectTag tag = new UpDownSelectTag();
        tag.setPageContext(pageContext);
        tag.setId("myId");
        tag.setName("myName");
        tag.setList("myMap");
        tag.setValue("mySelectedMapIds");
        tag.setEmptyOption("false");

        tag.doStartTag();
        tag.doEndTag();

        verify(UpDownSelectTagTest.class.getResource("updownselecttag-2.txt"));
    }

    public void testWithHeaderAndEmptyOption() throws Exception {

        UpDownSelectTag tag = new UpDownSelectTag();
        tag.setPageContext(pageContext);
        tag.setId("myId");
        tag.setName("myName");
        tag.setList("myMap");
        tag.setValue("mySelectedMapIds");
        tag.setEmptyOption("true");
        tag.setHeaderKey("-1");
        tag.setHeaderValue("--- Please Order ---");

        tag.doStartTag();
        tag.doEndTag();

        verify(UpDownSelectTagTest.class.getResource("updownselecttag-3.txt"));
    }

    public void testWithHeaderOnly() throws Exception {

        UpDownSelectTag tag = new UpDownSelectTag();
        tag.setPageContext(pageContext);
        tag.setId("myId");
        tag.setName("myName");
        tag.setList("myMap");
        tag.setValue("mySelectedMapIds");
        tag.setEmptyOption("false");
        tag.setHeaderKey("-1");
        tag.setHeaderValue("--- Please Order ---");

        tag.doStartTag();
        tag.doEndTag();

        verify(UpDownSelectTagTest.class.getResource("updownselecttag-4.txt"));
    }

    public void testWithEmptyOptionOnly() throws Exception {

        UpDownSelectTag tag = new UpDownSelectTag();
        tag.setPageContext(pageContext);
        tag.setId("myId");
        tag.setName("myName");
        tag.setList("myMap");
        tag.setValue("mySelectedMapIds");
        tag.setEmptyOption("true");

        tag.doStartTag();
        tag.doEndTag();

        verify(UpDownSelectTagTest.class.getResource("updownselecttag-5.txt"));
    }


    public void testDisableSomeSelectAllButton() throws Exception {

        UpDownSelectTag tag = new UpDownSelectTag();
        tag.setPageContext(pageContext);
        tag.setId("myId");
        tag.setName("myName");
        tag.setList("myMap");
        tag.setValue("mySelectedMapIds");
        tag.setEmptyOption("true");
        tag.setAllowSelectAll("false");

        tag.doStartTag();
        tag.doEndTag();

        verify(UpDownSelectTagTest.class.getResource("updownselecttag-6.txt"));
    }

    public void testDisableMoveUpButton() throws Exception {
        UpDownSelectTag tag = new UpDownSelectTag();
        tag.setPageContext(pageContext);
        tag.setId("myId");
        tag.setName("myName");
        tag.setList("myMap");
        tag.setValue("mySelectedMapIds");
        tag.setEmptyOption("true");
        tag.setAllowMoveUp("false");

        tag.doStartTag();
        tag.doEndTag();

        verify(UpDownSelectTagTest.class.getResource("updownselecttag-7.txt"));
    }

    public void testDisableMoveDownButton() throws Exception {
        UpDownSelectTag tag = new UpDownSelectTag();
        tag.setPageContext(pageContext);
        tag.setId("myId");
        tag.setName("myName");
        tag.setList("myMap");
        tag.setValue("mySelectedMapIds");
        tag.setEmptyOption("true");
        tag.setAllowMoveDown("false");

        tag.doStartTag();
        tag.doEndTag();

        verify(UpDownSelectTagTest.class.getResource("updownselecttag-8.txt"));
    }

    public void testChangeSelectAllButtonText() throws Exception {
        UpDownSelectTag tag = new UpDownSelectTag();
        tag.setPageContext(pageContext);
        tag.setId("myId");
        tag.setName("myName");
        tag.setList("myMap");
        tag.setValue("mySelectedMapIds");
        tag.setEmptyOption("true");
        tag.setSelectAllLabel("Select All");

        tag.doStartTag();
        tag.doEndTag();

        verify(UpDownSelectTagTest.class.getResource("updownselecttag-9.txt"));
    }

    public void testChangeMoveUpButtonText() throws Exception {
        UpDownSelectTag tag = new UpDownSelectTag();
        tag.setPageContext(pageContext);
        tag.setId("myId");
        tag.setName("myName");
        tag.setList("myMap");
        tag.setValue("mySelectedMapIds");
        tag.setEmptyOption("true");
        tag.setMoveUpLabel("Move Up");

        tag.doStartTag();
        tag.doEndTag();

        verify(UpDownSelectTagTest.class.getResource("updownselecttag-10.txt"));
    }

    public void testChangeMoveDownButtonText() throws Exception {
        UpDownSelectTag tag = new UpDownSelectTag();
        tag.setPageContext(pageContext);
        tag.setId("myId");
        tag.setName("myName");
        tag.setList("myMap");
        tag.setValue("mySelectedMapIds");
        tag.setEmptyOption("true");
        tag.setMoveDownLabel("Move Down");

        tag.doStartTag();
        tag.doEndTag();

        verify(UpDownSelectTagTest.class.getResource("updownselecttag-11.txt"));
    }

    public void testGenericSimple() throws Exception {
        UpDownSelectTag tag = new UpDownSelectTag();
        prepareTagGeneric(tag);
        verifyGenericProperties(tag, "simple", new String[]{"value"});
    }

    public void testGenericXhtml() throws Exception {
        UpDownSelectTag tag = new UpDownSelectTag();
        prepareTagGeneric(tag);
        verifyGenericProperties(tag, "xhtml", new String[]{"value"});
    }

    private void prepareTagGeneric(UpDownSelectTag tag) {
        tag.setList("myMap");
    }



    // ===============================
    public Action getAction() {
        return new ActionSupport() {

            public Map getMyMap() {
                Map _myMap = new LinkedHashMap();
                _myMap.put("england", "England");
                _myMap.put("america", "America");
                _myMap.put("australia", "Australia");
                _myMap.put("germany", "Germany");
                return _myMap;
            }

            public List getMySelectedMapIds() {
                List _mySelectedMapIds = new ArrayList();
                _mySelectedMapIds.add("america");
                _mySelectedMapIds.add("germany");
                return _mySelectedMapIds;
            }

            public List getMyAllSelectedMapIds() {
                List _mySelectedMapIds = new ArrayList();
                _mySelectedMapIds.add("england");
                _mySelectedMapIds.add("america");
                _mySelectedMapIds.add("australia");
                _mySelectedMapIds.add("germany");
                return _mySelectedMapIds;
            }
        };
    }
}
