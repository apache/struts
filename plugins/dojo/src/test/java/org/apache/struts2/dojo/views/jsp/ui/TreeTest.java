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

package org.apache.struts2.dojo.views.jsp.ui;


import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Test case for Tree component.
 */
public class TreeTest extends AbstractUITagTest{

    public void testStaticTree() throws Exception {
        // Root
        TreeTag tag = new TreeTag();
        tag.setShowRootGrid("false");
        tag.setShowGrid("false");
        tag.setTemplateCssPath("/struts/tree.css");
        tag.setPageContext(pageContext);
        tag.setId("rootId");
        tag.setLabel("Root");
        tag.doStartTag();

            // Child 1
            TreeNodeTag nodeTag1 = new TreeNodeTag();
            nodeTag1.setTheme("ajax");
            nodeTag1.setPageContext(pageContext);
            nodeTag1.setId("child1");
            nodeTag1.setLabel("Child 1");
            nodeTag1.doStartTag();
            nodeTag1.doEndTag();

            // Child 2
            TreeNodeTag nodeTag2 = new TreeNodeTag();
            nodeTag2.setTheme("ajax");
            nodeTag2.setPageContext(pageContext);
            nodeTag2.setId("child2");
            nodeTag2.setLabel("Child 2");
            nodeTag2.doStartTag();

                // Grand Child 1
                TreeNodeTag gNodeTag1 = new TreeNodeTag();
                gNodeTag1.setTheme("ajax");
                gNodeTag1.setPageContext(pageContext);
                gNodeTag1.setId("gChild1");
                gNodeTag1.setLabel("Grand Child 1");
                gNodeTag1.doStartTag();
                gNodeTag1.doEndTag();

                // Grand Child 2
                TreeNodeTag gNodeTag2 = new TreeNodeTag();
                gNodeTag2.setTheme("ajax");
                gNodeTag2.setPageContext(pageContext);
                gNodeTag2.setId("gChild2");
                gNodeTag2.setLabel("Grand Child 2");
                gNodeTag2.doStartTag();
                gNodeTag2.doEndTag();

                // Grand Child 3
                TreeNodeTag gNodeTag3= new TreeNodeTag();
                gNodeTag3.setTheme("ajax");
                gNodeTag3.setPageContext(pageContext);
                gNodeTag3.setId("gChild3");
                gNodeTag3.setLabel("Grand Child 3");
                gNodeTag3.doStartTag();
                gNodeTag3.doEndTag();

            nodeTag2.doEndTag();


            // Child 3
            TreeNodeTag nodeTag3 = new TreeNodeTag();
            nodeTag3.setTheme("ajax");
            nodeTag3.setPageContext(pageContext);
            nodeTag3.setId("child3");
            nodeTag3.setLabel("Child 4");
            nodeTag3.doStartTag();
            nodeTag3.doEndTag();

        tag.doEndTag();

        //System.out.println(writer.toString());
        verify(TreeTest.class.getResource("tree-1.txt"));
    }



    public void testDynamicTree() throws Exception {

        TreeTag tag = new TreeTag();
        tag.setPageContext(pageContext);
        tag.setTheme("ajax");
        tag.setId("myTree");
        tag.setRootNode("%{myTreeRoot}");
        tag.setNodeIdProperty("id");
        tag.setNodeTitleProperty("name");
        tag.setChildCollectionProperty("children");
        tag.doStartTag();
        tag.doEndTag();

        //System.out.println(writer.toString());
        verify(TreeTest.class.getResource("tree-2.txt"));
    }


    public Action getAction() {
        return new InternalActionSupport();
    }

    public static class InternalActionSupport extends ActionSupport {
        public Category getMyTreeRoot() {
            return Category.getById(1);
        }
    }
}
