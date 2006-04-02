/*
 * Copyright (c) 2005 ePlus Corporation. All Rights Reserved.
 */
package org.apache.struts.action2.components;

import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Renders a tree widget with AJAX support.<p/>
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/> <b>Examples</b>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;tree .../&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * Created : Oct 27, 2005 3:56:23 PM
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 *
 * @a2.tag name="tree" tld-body-content="JSP" tld-tag-class="org.apache.struts.action2.views.jsp.ui.TreeTag"
 * description="Render a tree widget."
 */
public class Tree extends ClosingUIBean {
    private static final String TEMPLATE = "tree-close";
    private static final String OPEN_TEMPLATE = "tree";
    private String toggle = "fade";
    private String treeSelectedTopic;
    private String treeExpandedTopic;
    private String treeCollapsedTopic;
    private String openAll;
    protected String rootNodeAttr;
    protected String childCollectionProperty;
    protected String nodeTitleProperty;
    protected String nodeIdProperty;

    public Tree(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
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

        addParameter("openAll", Boolean.valueOf(openAll));

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

    /**
     * The toggle property.
     * @a2.tagattribute required="false"
     */
    public void setToggle(String toggle) {
        this.toggle = toggle;
    }

    public String getTreeSelectedTopic() {
        return treeSelectedTopic;
    }

    /**
     * The treeSelectedTopic property.
     * @a2.tagattribute required="false"
     */
    public void setTreeSelectedTopic(String treeSelectedTopic) {
        this.treeSelectedTopic = treeSelectedTopic;
    }

    public String getTreeExpandedTopic() {
        return treeExpandedTopic;
    }

    /**
     * The treeExpandedTopic property.
     * @a2.tagattribute required="false"
     */
    public void setTreeExpandedTopic(String treeExpandedTopic) {
        this.treeExpandedTopic = treeExpandedTopic;
    }

    public String getTreeCollapsedTopic() {
        return treeCollapsedTopic;
    }

    /**
     * The treeCollapsedTopic property.
     * @a2.tagattribute required="false"
     */
    public void setTreeCollapsedTopic(String treeCollapsedTopic) {
        this.treeCollapsedTopic = treeCollapsedTopic;
    }

    public String getOpenAll() {
        return openAll;
    }

    /**
     * The openAll property.
     * @a2.tagattribute required="false" type="boolean" default="false"
     */
    public void setOpenAll(String openAll) {
        this.openAll = openAll;
    }

    public String getRootNode() {
        return rootNodeAttr;
    }

    /**
     * The rootNode property.
     * @a2.tagattribute required="false"
     */
    public void setRootNode(String rootNode) {
        this.rootNodeAttr = rootNode;
    }

    public String getChildCollectionProperty() {
        return childCollectionProperty;
    }

    /**
     * The childCollectionProperty property.
     * @a2.tagattribute required="false"
     */
    public void setChildCollectionProperty(String childCollectionProperty) {
        this.childCollectionProperty = childCollectionProperty;
    }

    public String getNodeTitleProperty() {
        return nodeTitleProperty;
    }

    /**
     * The nodeTitleProperty property.
     * @a2.tagattribute required="false"
     */
    public void setNodeTitleProperty(String nodeTitleProperty) {
        this.nodeTitleProperty = nodeTitleProperty;
    }

    public String getNodeIdProperty() {
        return nodeIdProperty;
    }

    /**
     * The nodeIdProperty property.
     * @a2.tagattribute required="false"
     */
    public void setNodeIdProperty(String nodeIdProperty) {
        this.nodeIdProperty = nodeIdProperty;
    }


}

