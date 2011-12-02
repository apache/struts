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

    protected String toggle;
    protected String selectedNotifyTopics;
    protected String expandedNotifyTopics;
    protected String collapsedNotifyTopics;
    protected String rootNode;
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
    
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Tree(stack,req,res);
    }

    protected void populateParams() {
        super.populateParams();

        Tree tree = (Tree) component;
        tree.setChildCollectionProperty(childCollectionProperty);
        tree.setNodeIdProperty(nodeIdProperty);
        tree.setNodeTitleProperty(nodeTitleProperty);
        tree.setRootNode(rootNode);
        tree.setToggle(toggle);
        tree.setSelectedNotifyTopics(selectedNotifyTopics);
        tree.setExpandedNotifyTopics(expandedNotifyTopics);
        tree.setCollapsedNotifyTopics(collapsedNotifyTopics);
        tree.setShowRootGrid(showRootGrid);

        tree.setShowGrid(showGrid);
        tree.setBlankIconSrc(blankIconSrc);
        tree.setGridIconSrcL(gridIconSrcC);
        tree.setGridIconSrcV(gridIconSrcV);
        tree.setGridIconSrcP(gridIconSrcP);
        tree.setGridIconSrcC(gridIconSrcC);
        tree.setGridIconSrcX(gridIconSrcX);
        tree.setGridIconSrcY(gridIconSrcY);
        tree.setExpandIconSrcPlus(expandIconSrcPlus);
        tree.setExpandIconSrcMinus(expandIconSrcMinus);
        tree.setIconWidth(iconWidth);
        tree.setIconHeight(iconHeight);
        tree.setToggleDuration(toggleDuration);
        tree.setTemplateCssPath(templateCssPath);
        tree.setHref(href);
        tree.setErrorNotifyTopics(errorNotifyTopics);
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

    public void setHref(String href) {
        this.href = href;
    }

    public void setErrorNotifyTopics(String errorNotifyTopics) {
        this.errorNotifyTopics = errorNotifyTopics;
    }
}

