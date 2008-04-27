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

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
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
 * Renders a tree widget with AJAX support.<p/>
 *
 * The "id "attribute is normally specified(recommended), such that it could be looked up using
 * javascript if necessary. The "id" attribute is required if the "selectedNotifyTopic" or the 
 * "href" attributes are going to be used.<p/>
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <!-- START SNIPPET: example1 -->
 * &lt;s:tree id="..." label="..."&gt;
 *    &lt;s:treenode id="..." label="..." /&gt;
 *    &lt;s:treenode id="..." label="..."&gt;
 *        &lt;s:treenode id="..." label="..." /&gt;
 *        &lt;s:treenode id="..." label="..." /&gt;
 *    &lt;/s:treenode&gt;
 *    &lt;s:treenode id="..." label="..." /&gt;
 * &lt;/s:tree&gt;
 * <!-- END SNIPPET: example1 -->
 * 
 * <!-- START SNIPPET: example2 -->
 * &lt;s:tree
 *          id="..."
 *          rootNode="..."
 *          nodeIdProperty="..."
 *          nodeTitleProperty="..."
 *          childCollectionProperty="..." /&gt;
 * <!-- END SNIPPET: example2 -->
 * 
 * <!-- START SNIPPET: example3 -->
 * &lt;s:url id="nodesUrl" namespace="/nodecorate" action="getNodes" /&gt;
 * &lt;div style="float:left; margin-right: 50px;"&gt;
 *     &lt;sx:tree id="tree" href="%{#nodesUrl}" /&gt;
 * &lt;/div&gt;
 * 
 * On this example the url specified on the "href" attibute will be called to load
 * the elements on the root. The response is expected to be a JSON array of objects like:
 * [
 *      {
 *           label: "Node 1",
 *           hasChildren: false,
 *           id: "Node1"
 *      },
 *      {
 *           label: "Node 2",
 *           hasChildren: true,
 *           id: "Node2"
 *      },
 * ]
 * 
 * "label" is the text that will be displayed for the node. "hasChildren" marks the node has
 * having children or not (if true, a plus icon will be assigned to the node so it can be
 * expanded). The "id" attribute will be used to load the children of the node, when the node
 * is expanded. When a node is expanded a request will be made to the url in the "href" attribute
 * and the node's "id" will be passed in the parameter "nodeId".
 * 
 * The children collection for a node will be loaded only once, to reload the children of a 
 * node, use the "reload()" function of the treenode widget. To reload the children nodes of "Node1"
 * from the example above use the following javascript:
 * 
 * dojo.widget.byId("Node1").reload();
 * <!-- END SNIPPET: example3 -->
 */
@StrutsTag(name="tree", tldTagClass="org.apache.struts2.dojo.views.jsp.ui.TreeTag", description="Render a tree widget.")
public class Tree extends ClosingUIBean {

    private static final String TEMPLATE = "tree-close";
    private static final String OPEN_TEMPLATE = "tree";
    private final static transient Random RANDOM = new Random();    

    protected String toggle;
    protected String selectedNotifyTopics;
    protected String expandedNotifyTopics;
    protected String collapsedNotifyTopics;
    protected String rootNodeAttr;
    protected String childCollectionProperty;
    protected String nodeTitleProperty;
    protected String nodeIdProperty;
    protected String showRootGrid;

    protected String showGrid;
    protected String blankIconSrc;
    protected String gridIconSrcL;
    protected String gridIconSrcV;
    protected String gridIconSrcP;
    protected String gridIconSrcC;
    protected String gridIconSrcX;
    protected String gridIconSrcY;
    protected String expandIconSrcPlus;
    protected String expandIconSrcMinus;
    protected String iconWidth;
    protected String iconHeight;
    protected String toggleDuration;
    protected String templateCssPath;
    protected String href;
    protected String errorNotifyTopics;
    
    private List<String> childrenIds;
    
    public Tree(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public boolean start(Writer writer) {
        boolean result = super.start(writer);

        if (this.label == null && (href == null)) {
            if ((rootNodeAttr == null)
                    || (childCollectionProperty == null)
                    || (nodeTitleProperty == null)
                    || (nodeIdProperty == null)) {
                fieldError("label","The TreeTag requires either a value for 'label' or 'href' or ALL of 'rootNode', " +
                        "'childCollectionProperty', 'nodeTitleProperty', and 'nodeIdProperty'", null);
            }
        }
        return result;
    }

    protected void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (toggle != null) {
            addParameter("toggle", findString(toggle));
        } else {
            addParameter("toggle", "fade");
        }

        if (selectedNotifyTopics != null) {
            addParameter("selectedNotifyTopics", findString(selectedNotifyTopics));
        }

        if (expandedNotifyTopics != null) {
            addParameter("expandedNotifyTopics", findString(expandedNotifyTopics));
        }

        if (collapsedNotifyTopics != null) {
            addParameter("collapsedNotifyTopics", findString(collapsedNotifyTopics));
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
        if (href != null) 
            addParameter("href", findString(href));
        if (errorNotifyTopics != null)
            addParameter("errorNotifyTopics", findString(errorNotifyTopics));
                
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
        
        if (this.childrenIds != null)
            addParameter("childrenIds", this.childrenIds);
    }
    
    public void addChildrenId(String id) {
        if (this.childrenIds == null)
            this.childrenIds = new ArrayList<String>();
        this.childrenIds.add(id);
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

    @StrutsTagAttribute(description="Deprecated. Use 'selectedNotifyTopics' instead.")
    public void setTreeSelectedTopic(String selectedNotifyTopic) {
        this.selectedNotifyTopics = selectedNotifyTopic;
    }

    @StrutsTagAttribute(description="Deprecated. Use 'expandedNotifyTopics' instead.")
    public void setTreeExpandedTopics(String expandedNotifyTopic) {
        this.expandedNotifyTopics = expandedNotifyTopic;
    }

    @StrutsTagAttribute(description="Deprecated. Use 'collapsedNotifyTopics' instead.")
    public void setTreeCollapsedTopics(String collapsedNotifyTopic) {
        this.collapsedNotifyTopics = collapsedNotifyTopic;
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

    @StrutsTagAttribute(description="Comma separated lis of topics to be published when a node" +
                " is collapsed. An object with a 'node' property will be passed as parameter to the topics.")
    public void setCollapsedNotifyTopics(String collapsedNotifyTopics) {
        this.collapsedNotifyTopics = collapsedNotifyTopics;
    }

    @StrutsTagAttribute(description="Comma separated lis of topics to be published when a node" +
                " is expanded. An object with a 'node' property will be passed as parameter to the topics.")
    public void setExpandedNotifyTopics(String expandedNotifyTopics) {
        this.expandedNotifyTopics= expandedNotifyTopics;
    }

    @StrutsTagAttribute(description="Comma separated lis of topics to be published when a node" +
                " is selected. An object with a 'node' property will be passed as parameter to the topics.")
    public void setSelectedNotifyTopics(String selectedNotifyTopics) {
        this.selectedNotifyTopics = selectedNotifyTopics;
    }

    @StrutsTagAttribute(description="Url used to load the list of children nodes for an specific node, whose id will be " +
    		"passed as a parameter named 'nodeId' (empty for root)")
    public void setHref(String href) {
        this.href = href;
    }
    
    @StrutsTagAttribute(description="Comma delimmited list of topics that will published after the request(if the request fails)." +
    		"Only valid if 'href' is set")
    public void setErrorNotifyTopics(String errorNotifyTopics) {
        this.errorNotifyTopics = errorNotifyTopics;
    }
}


