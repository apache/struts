/*
 * $Id: TreeTag.java 471756 2006-11-06 15:01:43Z husted $
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.dojo.components.Tree;
import org.apache.struts2.views.jsp.ui.AbstractClosingTag;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * @see Tree
 */
public class TreeTag extends AbstractClosingTag {

    private static final long serialVersionUID = 2735218501058548013L;

    private String toggle;
    private String selectedNotifyTopics;
    private String expandedNotifyTopics;
    private String collapsedNotifyTopics;
    private String rootNode;
    private String childCollectionProperty;
    private String nodeTitleProperty;
    private String nodeIdProperty;
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


    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Tree(stack,req,res);
    }

    protected void populateParams() {
        super.populateParams();

        Tree tree = (Tree) component;
        if (childCollectionProperty != null)
            tree.setChildCollectionProperty(childCollectionProperty);
        if (nodeIdProperty != null)
            tree.setNodeIdProperty(nodeIdProperty);
        if (nodeTitleProperty != null)
            tree.setNodeTitleProperty(nodeTitleProperty);
        if (rootNode != null)
            tree.setRootNode(rootNode);
        if (toggle != null)
            tree.setToggle(toggle);
        if (selectedNotifyTopics != null)
            tree.setSelectedNotifyTopics(selectedNotifyTopics);
        if (expandedNotifyTopics != null)
            tree.setExpandedNotifyTopics(expandedNotifyTopics);
        if (collapsedNotifyTopics != null)
            tree.setCollapsedNotifyTopics(collapsedNotifyTopics);
        if (showRootGrid != null)
            tree.setShowRootGrid(showRootGrid);

        if (showGrid != null)
            tree.setShowGrid(showGrid);
        if (blankIconSrc != null)
            tree.setBlankIconSrc(blankIconSrc);
        if (gridIconSrcL != null)
            tree.setGridIconSrcL(gridIconSrcC);
        if (gridIconSrcV != null)
            tree.setGridIconSrcV(gridIconSrcV);
        if (gridIconSrcP != null)
            tree.setGridIconSrcP(gridIconSrcP);
        if (gridIconSrcC != null)
            tree.setGridIconSrcC(gridIconSrcC);
        if (gridIconSrcX != null)
            tree.setGridIconSrcX(gridIconSrcX);
        if (gridIconSrcY != null)
            tree.setGridIconSrcY(gridIconSrcY);
        if (expandIconSrcPlus != null)
            tree.setExpandIconSrcPlus(expandIconSrcPlus);
        if (expandIconSrcMinus != null)
            tree.setExpandIconSrcMinus(expandIconSrcMinus);
        if (iconWidth != null)
            tree.setIconWidth(iconWidth);
        if (iconHeight != null)
            tree.setIconHeight(iconHeight);
        if (toggleDuration != null)
            tree.setToggleDuration(toggleDuration);
        if (templateCssPath != null)
            tree.setTemplateCssPath(templateCssPath);
    }

    public String getToggle() {
        return toggle;
    }

    public void setToggle(String toggle) {
        this.toggle = toggle;
    }

    @Deprecated
    public void setTreeSelectedTopic(String treeSelectedTopic) {
        this.selectedNotifyTopics = treeSelectedTopic;
    }

    @Deprecated
    public void setTreeExpandedTopic(String treeExpandedTopic) {
        this.expandedNotifyTopics = treeExpandedTopic;
    }

    @Deprecated
    public void setTreeCollapsedTopic(String treeCollapsedTopic) {
        this.collapsedNotifyTopics = treeCollapsedTopic;
    }

    public String getRootNode() {
        return rootNode;
    }

    public void setRootNode(String rootNode) {
        this.rootNode = rootNode;
    }

    public String getChildCollectionProperty() {
        return childCollectionProperty;
    }

    public void setChildCollectionProperty(String childCollectionProperty) {
        this.childCollectionProperty = childCollectionProperty;
    }

    public String getNodeTitleProperty() {
        return nodeTitleProperty;
    }

    public void setNodeTitleProperty(String nodeTitleProperty) {
        this.nodeTitleProperty = nodeTitleProperty;
    }

    public String getNodeIdProperty() {
        return nodeIdProperty;
    }

    public void setNodeIdProperty(String nodeIdProperty) {
        this.nodeIdProperty = nodeIdProperty;
    }

    public String getShowRootGrid() {
        return showRootGrid;
    }

    public void setShowRootGrid(String showRootGrid) {
        this.showRootGrid = showRootGrid;
    }

    public String getBlankIconSrc() {
        return blankIconSrc;
    }

    public void setBlankIconSrc(String blankIconSrc) {
        this.blankIconSrc = blankIconSrc;
    }

    public String getExpandIconSrcMinus() {
        return expandIconSrcMinus;
    }

    public void setExpandIconSrcMinus(String expandIconSrcMinus) {
        this.expandIconSrcMinus = expandIconSrcMinus;
    }

    public String getExpandIconSrcPlus() {
        return expandIconSrcPlus;
    }

    public void setExpandIconSrcPlus(String expandIconSrcPlus) {
        this.expandIconSrcPlus = expandIconSrcPlus;
    }

    public String getGridIconSrcC() {
        return gridIconSrcC;
    }

    public void setGridIconSrcC(String gridIconSrcC) {
        this.gridIconSrcC = gridIconSrcC;
    }

    public String getGridIconSrcL() {
        return gridIconSrcL;
    }

    public void setGridIconSrcL(String gridIconSrcL) {
        this.gridIconSrcL = gridIconSrcL;
    }

    public String getGridIconSrcP() {
        return gridIconSrcP;
    }

    public void setGridIconSrcP(String gridIconSrcP) {
        this.gridIconSrcP = gridIconSrcP;
    }

    public String getGridIconSrcV() {
        return gridIconSrcV;
    }

    public void setGridIconSrcV(String gridIconSrcV) {
        this.gridIconSrcV = gridIconSrcV;
    }

    public String getGridIconSrcX() {
        return gridIconSrcX;
    }

    public void setGridIconSrcX(String gridIconSrcX) {
        this.gridIconSrcX = gridIconSrcX;
    }

    public String getGridIconSrcY() {
        return gridIconSrcY;
    }

    public void setGridIconSrcY(String gridIconSrcY) {
        this.gridIconSrcY = gridIconSrcY;
    }

    public String getIconHeight() {
        return iconHeight;
    }

    public void setIconHeight(String iconHeight) {
        this.iconHeight = iconHeight;
    }

    public String getIconWidth() {
        return iconWidth;
    }

    public void setIconWidth(String iconWidth) {
        this.iconWidth = iconWidth;
    }

    public String getTemplateCssPath() {
        return templateCssPath;
    }

    public void setTemplateCssPath(String templateCssPath) {
        this.templateCssPath = templateCssPath;
    }

    public String getToggleDuration() {
        return toggleDuration;
    }

    public void setToggleDuration(String toggleDuration) {
        this.toggleDuration = toggleDuration;
    }

    public String getShowGrid() {
        return showGrid;
    }

    public void setShowGrid(String showGrid) {
        this.showGrid = showGrid;
    }

    public void setCollapsedNotifyTopics(String collapsedNotifyTopics) {
        this.collapsedNotifyTopics = collapsedNotifyTopics;
    }

    public void setExpandedNotifyTopics(String expandedNotifyTopics) {
        this.expandedNotifyTopics = expandedNotifyTopics;
    }

    public void setSelectedNotifyTopics(String selectedNotifyTopics) {
        this.selectedNotifyTopics = selectedNotifyTopics;
    }
}

