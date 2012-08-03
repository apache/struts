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

package org.apache.struts2.dojo.components;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.ClosingUIBean;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.annotations.StrutsTagSkipInheritance;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Renders a tree node within a tree widget with AJAX support.<p/>
 *
 * Either of the following combinations should be used depending on if the tree
 * is to be constructed dynamically or statically. <p/>
 *
 * <b>Dynamically:</b>
 * <ul>
 *      <li>id - id of this tree node</li>
 *      <li>title - label to be displayed for this tree node</li>
 * </ul>
 *
 * <b>Statically:</b>
 * <ul>
 *      <li>rootNode - the parent node of which this tree is derived from</li>
 *      <li>nodeIdProperty - property to obtained this current tree node's id</li>
 *      <li>nodeTitleProperty - property to obtained this current tree node's title</li>
 *      <li>childCollectionProperty - property that returnds this current tree node's children</li>
 * </ul>
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;-- Creating tree statically using hard-coded data. --&gt;
 * &lt;s:tree id="..." label="..."&gt;
 *    &lt;s:treenode id="..." label="..." /&gt;
 *    &lt;s:treenode id="..." label="..."&gt;
 *        &lt;s:treenode id="..." label="..." /&gt;
 *        &lt;s:treenode id="..." label="..." /&gt;
 *    &lt;/s:treenode&gt;
 *    &lt;s:treenode id="..." label="..." /&gt;
 * &lt;/s:tree&gt;
 *
 * &lt;-- Creating tree dynamically using data from backing action. --&gt;
 * &lt;s:tree
 *          id="..."
 *          rootNode="..."
 *          nodeIdProperty="..."
 *          nodeTitleProperty="..."
 *          childCollectionProperty="..." /&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 */
@StrutsTag(name="treenode", tldTagClass="org.apache.struts2.dojo.views.jsp.ui.TreeNodeTag", description="Render a tree node within a tree widget.")
public class TreeNode extends ClosingUIBean {
    private static final String TEMPLATE = "treenode-close";
    private static final String OPEN_TEMPLATE = "treenode";
    private final static transient Random RANDOM = new Random();    

    public TreeNode(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    @StrutsTagSkipInheritance
    public void setTheme(String theme) {
        super.setTheme(theme);
    }
    
    @Override
    public String getTheme() {
        return "ajax";
    }

    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        
        // generate a random ID if not explicitly set and not parsing the content
        Boolean parseContent = (Boolean)stack.getContext().get(Head.PARSE_CONTENT);
        boolean generateId = (parseContent != null ? !parseContent : true);

        addParameter("pushId", generateId);
        if ((this.id == null || this.id.length() == 0) && generateId) {
            // resolves Math.abs(Integer.MIN_VALUE) issue reported by FindBugs 
            // http://findbugs.sourceforge.net/bugDescriptions.html#RV_ABSOLUTE_VALUE_OF_RANDOM_INT
            int nextInt = RANDOM.nextInt();
            nextInt = nextInt == Integer.MIN_VALUE ? Integer.MAX_VALUE : Math.abs(nextInt);  
            this.id = "widget_" + String.valueOf(nextInt);
            addParameter("id", this.id);
        }
        
        Tree parentTree = (Tree) findAncestor(Tree.class);
        parentTree.addChildrenId(this.id);
    }
    
    @StrutsTagAttribute(description="Label expression used for rendering tree node label.", required=true)
    public void setLabel(String label) {
        super.setLabel(label);
    }
    
    @StrutsTagAttribute(description="The css class to use for element")
    public void setCssClass(String cssClass) {
        super.setCssClass(cssClass);
    }

    @StrutsTagAttribute(description="The css style to use for element")
    public void setCssStyle(String cssStyle) {
        super.setCssStyle(cssStyle);
    }

    @StrutsTagAttribute(description="The id to use for the element")
    public void setId(String id) {
        super.setId(id);
    }

    @StrutsTagAttribute(description="The name to set for element")
    public void setName(String name) {
        super.setName(name);
    }
}
