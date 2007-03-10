/*
 * $Id: Tree.java 497654 2007-01-19 00:21:57Z rgielen $
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

import java.io.Writer;

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
 * Renders a tree widget with AJAX support.<p/>
 *
 * The id attribute is normally specified, such that it could be looked up using
 * javascript if necessary.<p/>
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt-- statically --&gt;
 * &lt;s:tree id="..." label="..."&gt;
 *    &lt;s:treenode id="..." label="..." /&gt;
 *    &lt;s:treenode id="..." label="..."&gt;
 *        &lt;s:treenode id="..." label="..." /&gt;
 *        &lt;s:treenode id="..." label="..." /&gt;
 *    &;lt;/s:treenode&gt;
 *    &lt;s:treenode id="..." label="..." /&gt;
 * &lt;/s:tree&gt;
 *
 * &lt;-- dynamically --&gt;
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
@StrutsTag(name="tree", tldTagClass="org.apache.struts2.dojo.views.jsp.ui.TreeTag", description="Render a tree widget.")
public class Tree extends ClosingUIBean {

    private static final String TEMPLATE = "tree-close";
    private static final String OPEN_TEMPLATE = "tree";

    private String toggle = "fade";
    private String treeSelectedTopic;
    private String treeExpandedTopic;
    private String treeCollapsedTopic;
    protected String rootNodeAttr;
    protected String childCollectionProperty;
    protected String nodeTitleProperty;
    protected String nodeIdProperty;
    private String showRootGrid;

    private String showGrid;
    private String blankIconSrc;
    private String gridIconSrcL;
    private String gridIconSrcV;
    private String gridIconSrcP;
    private String gridIconSrcC;
    private String gridIconSrcX;
    private String gridIconSrcY;
    private String expandIconSrcPlus;
    private String expandIconSrcMinus;
    private String iconWidth;
    private String iconHeight;
    private String toggleDuration;
    private String templateCssPath;

    public Tree(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public boolean start(Writer writer) {
        boolean result = super.start(writer);

        if (this.label == null) {
            if ((rootNodeAttr == null)
                    || (childCollectionProperty == null)
                    || (nodeTitleProperty == null)
                    || (nodeIdProperty == null)) {
                fieldError("label","The TreeTag requires either a value for 'label' or ALL of 'rootNode', " +
                        "'childCollectionProperty', 'nodeTitleProperty', and 'nodeIdProperty'", null);
            }
        }
        return result;
    }

    protected void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (toggle != null) {
            addParameter("toggle", findString(toggle));
        }

        if (treeSelectedTopic != null) {
            addParameter("treeSelectedTopic", findString(treeSelectedTopic));
        }

        if (treeExpandedTopic != null) {
            addParameter("treeExpandedTopic", findString(treeExpandedTopic));
        }

        if (treeCollapsedTopic != null) {
            addParameter("treeCollapsedTopic", findString(treeCollapsedTopic));
        }

        if (rootNodeAttr != null) {
            addParameter("rootNode", findValue(rootNodeAttr));
        }

        if (childCollectionProperty != null) {
            addParameter("childCollectionProperty", findString(childCollectionProperty));
        }

        if (nodeTitleProperty != null) {
            addParameter("nodeTitleProperty", findString(nodeTitleProperty));
        }

        if (nodeIdProperty != null) {
            addParameter("nodeIdProperty", findString(nodeIdProperty));
        }

        if (showRootGrid != null) {
            addParameter("showRootGrid", findValue(showRootGrid, Boolean.class));
        }


        if (showGrid != null) {
            addParameter("showGrid", findValue(showGrid, Boolean.class));
        }

        if (blankIconSrc != null) {
            addParameter("blankIconSrc", findString(blankIconSrc));
        }

        if (gridIconSrcL != null) {
            addParameter("gridIconSrcL", findString(gridIconSrcL));
        }

        if (gridIconSrcV != null) {
            addParameter("gridIconSrcV", findString(gridIconSrcV));
        }

        if (gridIconSrcP != null)  {
            addParameter("gridIconSrcP", findString(gridIconSrcP));
        }

        if (gridIconSrcC != null) {
            addParameter("gridIconSrcC", findString(gridIconSrcC));
        }

        if (gridIconSrcX != null) {
            addParameter("gridIconSrcX", findString(gridIconSrcX));
        }

        if (gridIconSrcY != null) {
            addParameter("gridIconSrcY", findString(gridIconSrcY));
        }

        if (expandIconSrcPlus != null) {
            addParameter("expandIconSrcPlus", findString(expandIconSrcPlus));
        }

        if (expandIconSrcMinus != null) {
            addParameter("expandIconSrcMinus", findString(expandIconSrcMinus));
        }

        if (iconWidth != null) {
            addParameter("iconWidth", findValue(iconWidth, Integer.class));
        }
        if (iconHeight != null) {
            addParameter("iconHeight", findValue(iconHeight, Integer.class));
        }
        if (toggleDuration != null) {
            addParameter("toggleDuration", findValue(toggleDuration, Integer.class));
        }
        if (templateCssPath != null) {
            addParameter("templateCssPath", findString(templateCssPath));
        }
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

    public String getToggle() {
        return toggle;
    }

    @StrutsTagAttribute(description="The toggle property (either 'explode' or 'fade')", defaultValue="fade")
    public void setToggle(String toggle) {
        this.toggle = toggle;
    }

    public String getTreeSelectedTopic() {
        return treeSelectedTopic;
    }

    @StrutsTagAttribute(description="The treeSelectedTopic property")
    public void setTreeSelectedTopic(String treeSelectedTopic) {
        this.treeSelectedTopic = treeSelectedTopic;
    }

    public String getTreeExpandedTopic() {
        return treeExpandedTopic;
    }

    @StrutsTagAttribute(description="The treeExpandedTopic property.")
    public void setTreeExpandedTopic(String treeExpandedTopic) {
        this.treeExpandedTopic = treeExpandedTopic;
    }

    public String getTreeCollapsedTopic() {
        return treeCollapsedTopic;
    }

    @StrutsTagAttribute(description="The treeCollapsedTopic property.")
    public void setTreeCollapsedTopic(String treeCollapsedTopic) {
        this.treeCollapsedTopic = treeCollapsedTopic;
    }

    public String getRootNode() {
        return rootNodeAttr;
    }

    @StrutsTagAttribute(description="The rootNode property.")
    public void setRootNode(String rootNode) {
        this.rootNodeAttr = rootNode;
    }

    public String getChildCollectionProperty() {
        return childCollectionProperty;
    }

    @StrutsTagAttribute(description="The childCollectionProperty property.")
    public void setChildCollectionProperty(String childCollectionProperty) {
        this.childCollectionProperty = childCollectionProperty;
    }

    public String getNodeTitleProperty() {
        return nodeTitleProperty;
    }

    @StrutsTagAttribute(description="The nodeTitleProperty property.")
    public void setNodeTitleProperty(String nodeTitleProperty) {
        this.nodeTitleProperty = nodeTitleProperty;
    }

    public String getNodeIdProperty() {
        return nodeIdProperty;
    }

    @StrutsTagAttribute(description="The nodeIdProperty property.")
    public void setNodeIdProperty(String nodeIdProperty) {
        this.nodeIdProperty = nodeIdProperty;
    }

    @StrutsTagAttribute(description="The showRootGrid property (default true).")
    public void setShowRootGrid(String showRootGrid) {
        this.showRootGrid = showRootGrid;
    }

    public String getShowRootGrid() {
        return showRootGrid;
    }

    public String getBlankIconSrc() {
        return blankIconSrc;
    }

    @StrutsTagAttribute(description="Blank icon image source.")
    public void setBlankIconSrc(String blankIconSrc) {
        this.blankIconSrc = blankIconSrc;
    }

    public String getExpandIconSrcMinus() {
        return expandIconSrcMinus;
    }

    @StrutsTagAttribute(description="Expand icon (-) image source.")
    public void setExpandIconSrcMinus(String expandIconSrcMinus) {
        this.expandIconSrcMinus = expandIconSrcMinus;
    }

    public String getExpandIconSrcPlus() {
        return expandIconSrcPlus;
    }

    @StrutsTagAttribute(description="Expand Icon (+) image source.")
    public void setExpandIconSrcPlus(String expandIconSrcPlus) {
        this.expandIconSrcPlus = expandIconSrcPlus;
    }

    public String getGridIconSrcC() {
        return gridIconSrcC;
    }

    @StrutsTagAttribute(description="Image source for under child item child icons.")
    public void setGridIconSrcC(String gridIconSrcC) {
        this.gridIconSrcC = gridIconSrcC;
    }

    public String getGridIconSrcL() {
        return gridIconSrcL;
    }


    @StrutsTagAttribute(description=" Image source for last child grid.")
    public void setGridIconSrcL(String gridIconSrcL) {
        this.gridIconSrcL = gridIconSrcL;
    }

    public String getGridIconSrcP() {
        return gridIconSrcP;
    }

    @StrutsTagAttribute(description="Image source for under parent item child icons.")
    public void setGridIconSrcP(String gridIconSrcP) {
        this.gridIconSrcP = gridIconSrcP;
    }

    public String getGridIconSrcV() {
        return gridIconSrcV;
    }

    @StrutsTagAttribute(description="Image source for vertical line.")
    public void setGridIconSrcV(String gridIconSrcV) {
        this.gridIconSrcV = gridIconSrcV;
    }

    public String getGridIconSrcX() {
        return gridIconSrcX;
    }

    @StrutsTagAttribute(description="Image source for grid for sole root item.")
    public void setGridIconSrcX(String gridIconSrcX) {
        this.gridIconSrcX = gridIconSrcX;
    }

    public String getGridIconSrcY() {
        return gridIconSrcY;
    }

    @StrutsTagAttribute(description="Image source for grid for last root item.")
    public void setGridIconSrcY(String gridIconSrcY) {
        this.gridIconSrcY = gridIconSrcY;
    }

    public String getIconHeight() {
        return iconHeight;
    }


    @StrutsTagAttribute(description="Icon height", defaultValue="18px")
    public void setIconHeight(String iconHeight) {
        this.iconHeight = iconHeight;
    }

    public String getIconWidth() {
        return iconWidth;
    }

    @StrutsTagAttribute(description="Icon width", defaultValue="19px")
    public void setIconWidth(String iconWidth) {
        this.iconWidth = iconWidth;
    }



    public String getTemplateCssPath() {
        return templateCssPath;
    }

    @StrutsTagAttribute(description="Template css path", defaultValue="{contextPath}/struts/tree.css.")
    public void setTemplateCssPath(String templateCssPath) {
        this.templateCssPath = templateCssPath;
    }

    public String getToggleDuration() {
        return toggleDuration;
    }

    @StrutsTagAttribute(description="Toggle duration in milliseconds", defaultValue="150")
    public void setToggleDuration(String toggleDuration) {
        this.toggleDuration = toggleDuration;
    }

    public String getShowGrid() {
        return showGrid;
    }

    @StrutsTagAttribute(description="Show grid", type="Boolean", defaultValue="true")
    public void setShowGrid(String showGrid) {
        this.showGrid = showGrid;
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

