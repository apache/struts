/*
 * Copyright (c) 2005 Opensymphony. All Rights Reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import com.opensymphony.webwork.components.Component;
import com.opensymphony.webwork.components.Tree;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Tree
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class TreeTag extends AbstractClosingTag {
    private String toggle;
    private String treeSelectedTopic;
    private String treeExpandedTopic;
    private String treeCollapsedTopic;
    private String openAll;
    private String rootNode;
    private String childCollectionProperty;
    private String nodeTitleProperty;
    private String nodeIdProperty;


    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Tree(stack,req,res);
    }

    protected void populateParams() {
        super.populateParams();

        Tree tree = (Tree) component;
        tree.setChildCollectionProperty(childCollectionProperty);
        tree.setNodeIdProperty(nodeIdProperty);
        tree.setNodeTitleProperty(nodeTitleProperty);
        tree.setOpenAll(openAll);
        tree.setRootNode(rootNode);
        tree.setToggle(toggle);
        tree.setTreeCollapsedTopic(treeCollapsedTopic);
        tree.setTreeExpandedTopic(treeExpandedTopic);
        tree.setTreeSelectedTopic(treeSelectedTopic);
    }

    public String getToggle() {
        return toggle;
    }

    public void setToggle(String toggle) {
        this.toggle = toggle;
    }

    public String getTreeSelectedTopic() {
        return treeSelectedTopic;
    }

    public void setTreeSelectedTopic(String treeSelectedTopic) {
        this.treeSelectedTopic = treeSelectedTopic;
    }

    public String getTreeExpandedTopic() {
        return treeExpandedTopic;
    }

    public void setTreeExpandedTopic(String treeExpandedTopic) {
        this.treeExpandedTopic = treeExpandedTopic;
    }

    public String getTreeCollapsedTopic() {
        return treeCollapsedTopic;
    }

    public void setTreeCollapsedTopic(String treeCollapsedTopic) {
        this.treeCollapsedTopic = treeCollapsedTopic;
    }

    public String getOpenAll() {
        return openAll;
    }

    public void setOpenAll(String openAll) {
        this.openAll = openAll;
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
}

