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
import java.util.List;

import org.apache.struts2.TestAction;
import org.apache.struts2.views.jsp.AbstractUITagTest;

/**
 *
 */
public class OptionTransferSelectTagTest extends AbstractUITagTest {

    public void testWithAllSelected() throws Exception {
        List left = new ArrayList();
        left.add("Left1");
        left.add("Left2");

        List right = new ArrayList();
        right.add("Right1");
        right.add("Right2");

        TestAction testaction = (TestAction) action;
        testaction.setCollection(left);
        testaction.setList2(right);

        OptionTransferSelectTag tag = new OptionTransferSelectTag();
        tag.setPageContext(pageContext);

        tag.setName("collection");
        tag.setId("id");
        tag.setList("collection");
        tag.setSize("20");
        tag.setMultiple("true");
        tag.setEmptyOption("true");

        tag.setDoubleName("list2");
        tag.setDoubleList("list2");
        tag.setDoubleId("doubleId");
        tag.setDoubleSize("20");
        tag.setMultiple("true");
        tag.setDoubleEmptyOption("true");
        tag.setDoubleCssClass("c2");
        tag.setDoubleCssStyle("s2");

        tag.setAllowAddAllToLeft("true");
        tag.setAllowAddAllToRight("true");
        tag.setAllowAddToLeft("true");
        tag.setAllowAddToRight("true");
        tag.setAllowSelectAll("true");

        tag.setAddAllToLeftLabel("All Left");
        tag.setAddAllToRightLabel("All Right");
        tag.setAddToLeftLabel("Left");
        tag.setAddToRightLabel("Right");
        tag.setSelectAllLabel("Select All");

        tag.setLeftTitle("Title Left");
        tag.setRightTitle("Title Right");

        tag.setButtonCssClass("buttonCssClass");
        tag.setButtonCssStyle("buttonCssStyle");

        tag.setHeaderKey("Header Key");
        tag.setHeaderValue("Header Value");

        tag.setDoubleHeaderKey("Double Header Key");
        tag.setDoubleHeaderValue("Double Header Value");

        tag.doStartTag();
        tag.doEndTag();

        //System.out.println(writer.toString());
        verify(OptionTransferSelectTagTest.class.getResource("optiontransferselect-1.txt"));
    }

    public void testWithPartialSelectedOnBothSides() throws Exception {
        List left = new ArrayList();
        left.add("Left2");

        List right = new ArrayList();
        right.add("Right2");

        List leftVal = new ArrayList();
        leftVal.add("Left1");
        leftVal.add("Left2");
        leftVal.add("Left3");

        List rightVal = new ArrayList();
        rightVal.add("Right1");
        rightVal.add("Right2");
        rightVal.add("Right3");


        TestAction testaction = (TestAction) action;
        testaction.setCollection(left);
        testaction.setList2(right);
        testaction.setCollection2(leftVal);
        testaction.setList3(rightVal);


        OptionTransferSelectTag tag = new OptionTransferSelectTag();
        tag.setPageContext(pageContext);

        tag.setName("collection");
        tag.setId("id");
        tag.setList("collection2");
        tag.setSize("20");
        tag.setMultiple("true");
        tag.setEmptyOption("true");

        tag.setDoubleName("list2");
        tag.setDoubleList("list3");
        tag.setDoubleId("doubleId");
        tag.setDoubleSize("20");
        tag.setMultiple("true");
        tag.setDoubleEmptyOption("true");

        tag.setAllowAddAllToLeft("true");
        tag.setAllowAddAllToRight("true");
        tag.setAllowAddToLeft("true");
        tag.setAllowAddToRight("true");
        tag.setAllowSelectAll("true");

        tag.setAddAllToLeftLabel("All Left");
        tag.setAddAllToRightLabel("All Right");
        tag.setAddToLeftLabel("Left");
        tag.setAddToRightLabel("Right");
        tag.setSelectAllLabel("Select All");

        tag.setLeftTitle("Title Left");
        tag.setRightTitle("Title Right");

        tag.setButtonCssClass("buttonCssClass");
        tag.setButtonCssStyle("buttonCssStyle");

        tag.setHeaderKey("Header Key");
        tag.setHeaderValue("Header Value");

        tag.setDoubleHeaderKey("Double Header Key");
        tag.setDoubleHeaderValue("Double Header Value");

        tag.doStartTag();
        tag.doEndTag();

        //System.out.println(writer.toString());
        verify(OptionTransferSelectTagTest.class.getResource("optiontransferselect-2.txt"));
    }

    public void testWithoutHeaderOnBothSides() throws Exception {
        List left = new ArrayList();
        left.add("Left2");

        List right = new ArrayList();
        right.add("Right2");

        List leftVal = new ArrayList();
        leftVal.add("Left1");
        leftVal.add("Left2");
        leftVal.add("Left3");

        List rightVal = new ArrayList();
        rightVal.add("Right1");
        rightVal.add("Right2");
        rightVal.add("Right3");


        TestAction testaction = (TestAction) action;
        testaction.setCollection(left);
        testaction.setList2(right);
        testaction.setCollection2(leftVal);
        testaction.setList3(rightVal);


        OptionTransferSelectTag tag = new OptionTransferSelectTag();
        tag.setPageContext(pageContext);

        tag.setName("collection");
        tag.setId("id");
        tag.setList("collection2");
        tag.setSize("20");
        tag.setMultiple("true");
        tag.setEmptyOption("true");

        tag.setDoubleName("list2");
        tag.setDoubleList("list3");
        tag.setDoubleId("doubleId");
        tag.setDoubleSize("20");
        tag.setMultiple("true");
        tag.setDoubleEmptyOption("true");

        tag.setAllowAddAllToLeft("true");
        tag.setAllowAddAllToRight("true");
        tag.setAllowAddToLeft("true");
        tag.setAllowAddToRight("true");
        tag.setAllowSelectAll("true");

        tag.setAddAllToLeftLabel("All Left");
        tag.setAddAllToRightLabel("All Right");
        tag.setAddToLeftLabel("Left");
        tag.setAddToRightLabel("Right");
        tag.setSelectAllLabel("Select All");

        tag.setLeftTitle("Title Left");
        tag.setRightTitle("Title Right");

        tag.setButtonCssClass("buttonCssClass");
        tag.setButtonCssStyle("buttonCssStyle");

        tag.doStartTag();
        tag.doEndTag();

        //System.out.println(writer.toString());
        verify(OptionTransferSelectTagTest.class.getResource("optiontransferselect-3.txt"));
    }

    public void testWithoutHeaderOnOneSide() throws Exception {
        List left = new ArrayList();
        left.add("Left2");

        List right = new ArrayList();
        right.add("Right2");

        List leftVal = new ArrayList();
        leftVal.add("Left1");
        leftVal.add("Left2");
        leftVal.add("Left3");

        List rightVal = new ArrayList();
        rightVal.add("Right1");
        rightVal.add("Right2");
        rightVal.add("Right3");


        TestAction testaction = (TestAction) action;
        testaction.setCollection(left);
        testaction.setList2(right);
        testaction.setCollection2(leftVal);
        testaction.setList3(rightVal);


        OptionTransferSelectTag tag = new OptionTransferSelectTag();
        tag.setPageContext(pageContext);

        tag.setName("collection");
        tag.setId("id");
        tag.setList("collection2");
        tag.setSize("20");
        tag.setMultiple("true");
        tag.setEmptyOption("true");

        tag.setDoubleName("list2");
        tag.setDoubleList("list3");
        tag.setDoubleId("doubleId");
        tag.setDoubleSize("20");
        tag.setMultiple("true");
        tag.setDoubleEmptyOption("true");

        tag.setAllowAddAllToLeft("true");
        tag.setAllowAddAllToRight("true");
        tag.setAllowAddToLeft("true");
        tag.setAllowAddToRight("true");
        tag.setAllowSelectAll("true");

        tag.setAddAllToLeftLabel("All Left");
        tag.setAddAllToRightLabel("All Right");
        tag.setAddToLeftLabel("Left");
        tag.setAddToRightLabel("Right");
        tag.setSelectAllLabel("Select All");

        tag.setLeftTitle("Title Left");
        tag.setRightTitle("Title Right");

        tag.setButtonCssClass("buttonCssClass");
        tag.setButtonCssStyle("buttonCssStyle");

        tag.setHeaderKey("Header Key");
        tag.setHeaderValue("Header Value");

        tag.doStartTag();
        tag.doEndTag();

        //System.out.println(writer.toString());
        verify(OptionTransferSelectTagTest.class.getResource("optiontransferselect-4.txt"));
    }

    public void testWithoutEmptyOptionOnBothSides() throws Exception {
        List left = new ArrayList();
        left.add("Left2");

        List right = new ArrayList();
        right.add("Right2");

        List leftVal = new ArrayList();
        leftVal.add("Left1");
        leftVal.add("Left2");
        leftVal.add("Left3");

        List rightVal = new ArrayList();
        rightVal.add("Right1");
        rightVal.add("Right2");
        rightVal.add("Right3");


        TestAction testaction = (TestAction) action;
        testaction.setCollection(left);
        testaction.setList2(right);
        testaction.setCollection2(leftVal);
        testaction.setList3(rightVal);


        OptionTransferSelectTag tag = new OptionTransferSelectTag();
        tag.setPageContext(pageContext);

        tag.setName("collection");
        tag.setId("id");
        tag.setList("collection2");
        tag.setSize("20");
        tag.setMultiple("true");
        tag.setEmptyOption("false");

        tag.setDoubleName("list2");
        tag.setDoubleList("list3");
        tag.setDoubleId("doubleId");
        tag.setDoubleSize("20");
        tag.setMultiple("true");
        tag.setDoubleEmptyOption("false");

        tag.setAllowAddAllToLeft("true");
        tag.setAllowAddAllToRight("true");
        tag.setAllowAddToLeft("true");
        tag.setAllowAddToRight("true");
        tag.setAllowSelectAll("true");

        tag.setAddAllToLeftLabel("All Left");
        tag.setAddAllToRightLabel("All Right");
        tag.setAddToLeftLabel("Left");
        tag.setAddToRightLabel("Right");
        tag.setSelectAllLabel("Select All");

        tag.setLeftTitle("Title Left");
        tag.setRightTitle("Title Right");

        tag.setButtonCssClass("buttonCssClass");
        tag.setButtonCssStyle("buttonCssStyle");

        tag.setHeaderKey("Header Key");
        tag.setHeaderValue("Header Value");

        tag.setDoubleHeaderKey("Double Header Key");
        tag.setDoubleHeaderValue("Double Header Value");

        tag.doStartTag();
        tag.doEndTag();

        //System.out.println(writer.toString());
        verify(OptionTransferSelectTagTest.class.getResource("optiontransferselect-5.txt"));
    }

    public void testWithoutEmptyOptionOnOneSide() throws Exception {
        List left = new ArrayList();
        left.add("Left2");

        List right = new ArrayList();
        right.add("Right2");

        List leftVal = new ArrayList();
        leftVal.add("Left1");
        leftVal.add("Left2");
        leftVal.add("Left3");

        List rightVal = new ArrayList();
        rightVal.add("Right1");
        rightVal.add("Right2");
        rightVal.add("Right3");


        TestAction testaction = (TestAction) action;
        testaction.setCollection(left);
        testaction.setList2(right);
        testaction.setCollection2(leftVal);
        testaction.setList3(rightVal);


        OptionTransferSelectTag tag = new OptionTransferSelectTag();
        tag.setPageContext(pageContext);

        tag.setName("collection");
        tag.setId("id");
        tag.setList("collection2");
        tag.setSize("20");
        tag.setMultiple("true");
        tag.setEmptyOption("true");

        tag.setDoubleName("list2");
        tag.setDoubleList("list3");
        tag.setDoubleId("doubleId");
        tag.setDoubleSize("20");
        tag.setMultiple("true");
        tag.setDoubleEmptyOption("false");

        tag.setAllowAddAllToLeft("true");
        tag.setAllowAddAllToRight("true");
        tag.setAllowAddToLeft("true");
        tag.setAllowAddToRight("true");
        tag.setAllowSelectAll("true");

        tag.setAddAllToLeftLabel("All Left");
        tag.setAddAllToRightLabel("All Right");
        tag.setAddToLeftLabel("Left");
        tag.setAddToRightLabel("Right");
        tag.setSelectAllLabel("Select All");

        tag.setLeftTitle("Title Left");
        tag.setRightTitle("Title Right");

        tag.setButtonCssClass("buttonCssClass");
        tag.setButtonCssStyle("buttonCssStyle");

        tag.setHeaderKey("Header Key");
        tag.setHeaderValue("Header Value");

        tag.setDoubleHeaderKey("Double Header Key");
        tag.setDoubleHeaderValue("Double Header Value");

        tag.doStartTag();
        tag.doEndTag();

        //System.out.println(writer.toString());
        verify(OptionTransferSelectTagTest.class.getResource("optiontransferselect-6.txt"));
    }

    public void testDisableSomeButtons() throws Exception {
        List left = new ArrayList();
        left.add("Left2");

        List right = new ArrayList();
        right.add("Right2");

        List leftVal = new ArrayList();
        leftVal.add("Left1");
        leftVal.add("Left2");
        leftVal.add("Left3");

        List rightVal = new ArrayList();
        rightVal.add("Right1");
        rightVal.add("Right2");
        rightVal.add("Right3");


        TestAction testaction = (TestAction) action;
        testaction.setCollection(left);
        testaction.setList2(right);
        testaction.setCollection2(leftVal);
        testaction.setList3(rightVal);


        OptionTransferSelectTag tag = new OptionTransferSelectTag();
        tag.setPageContext(pageContext);

        tag.setName("collection");
        tag.setId("id");
        tag.setList("collection2");
        tag.setSize("20");
        tag.setMultiple("true");
        tag.setEmptyOption("true");

        tag.setDoubleName("list2");
        tag.setDoubleList("list3");
        tag.setDoubleId("doubleId");
        tag.setDoubleSize("20");
        tag.setMultiple("true");
        tag.setDoubleEmptyOption("true");

        tag.setAllowAddAllToLeft("false");
        tag.setAllowAddAllToRight("false");
        tag.setAllowAddToLeft("true");
        tag.setAllowAddToRight("true");
        tag.setAllowSelectAll("false");

        tag.setAddAllToLeftLabel("All Left");
        tag.setAddAllToRightLabel("All Right");
        tag.setAddToLeftLabel("Left");
        tag.setAddToRightLabel("Right");
        tag.setSelectAllLabel("Select All");

        tag.setLeftTitle("Title Left");
        tag.setRightTitle("Title Right");

        tag.setButtonCssClass("buttonCssClass");
        tag.setButtonCssStyle("buttonCssStyle");

        tag.setHeaderKey("Header Key");
        tag.setHeaderValue("Header Value");

        tag.setDoubleHeaderKey("Double Header Key");
        tag.setDoubleHeaderValue("Double Header Value");

        tag.setAddToLeftOnclick("alert('Moving Left')");
        tag.setAddToRightOnclick("alert('Moving Right')");

        tag.doStartTag();
        tag.doEndTag();

        //System.out.println(writer.toString());
        verify(OptionTransferSelectTagTest.class.getResource("optiontransferselect-7.txt"));
    }
}
