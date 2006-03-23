/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import com.opensymphony.webwork.util.TabbedPane;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.util.Map;
import java.util.Vector;


/**
 * TabbedPane tag.
 *
 * @author Onyeje Bose (digi9ten@yahoo.com)
 * @deprecated Please use the tabbed panel
 *
 * @ww.tag name="tabbedpane" tld-body-content="JSP"
 * description="This tag is deprecated. Use tabbed panel instead."
 */
public class TabbedPaneTag extends ComponentTag {

    private final static String TEMPLATE = "tabbedpane";


    protected String contentName;

    // Protected --------------------------------------------------------
    protected TabbedPane tabPane;


    public int getColSpanLength() {
        return ((this.getTabAlign().compareToIgnoreCase("CENTER") == 0) ? (this.getContent().size() + 2) : (this.getContent().size() + 1));
    }

    public void setContent(java.util.Vector content) {
        tabPane.setContent(content);
    }

    public java.util.Vector getContent() {
        return tabPane.getContent();
    }

    /**
     * @ww.tagattribute required="true"
     * @param contentName
     */
    public void setContentName(String contentName) {
        this.contentName = strVal(contentName);

        Object obj = findValue(this.contentName);

        if (obj instanceof Vector) {
            this.setContent((Vector) obj);
        }
    }

    public String getContentName() {
        return this.contentName;
    }

    public String getIndexLink() {
        return ("TABBEDPANE_" + getId() + "_INDEX");
    }

    // BodyTagSupport overrides --------------------------------------
    public void setPageContext(PageContext aPageContext) {
        tabPane = new TabbedPane(0);
        contentName = null;
        setSelectedIndex(0);
        setTabAlign("'CENTER'");

        super.setPageContext(aPageContext);
    }

    public void setSelectedIndex(int selectedIndex) {
        tabPane.setSelectedIndex(selectedIndex);
    }

    public int getSelectedIndex() {
        return tabPane.getSelectedIndex();
    }

    public String getSelectedUrl() {
        Map.Entry me = (Map.Entry) this.getContent().elementAt(this.getSelectedIndex());

        return me.getValue().toString();
    }

    public void setTabAlign(String tabAlign) {
        tabPane.setTabAlign(strVal(tabAlign));
    }

    public String getTabAlign() {
        return tabPane.getTabAlign();
    }

    public boolean compareNumbers(Number n1, Number n2) {
        return n1.longValue() == n2.longValue();
    }

    // BodyTag implementation ----------------------------------------
    public int doStartTag() throws JspException {
        String indexStr = pageContext.getRequest().getParameter(getIndexLink());

        if (indexStr != null) {
            try {
                int index = Integer.parseInt(indexStr);
                this.setSelectedIndex(((index < 0) ? 0 : index));
            } catch (Exception e) {
                throw new JspTagException("TabbedPane Error: " + e.toString());
            }
        }

        return super.doStartTag();
    }

    // IncludeTag overrides ------------------------------------------
    public void release() {
        this.setSelectedIndex(0);

        if (this.getTabAlign() == null) {
            this.setTabAlign("'CENTER'");
        }
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    protected String strVal(String objName) {
        try {
            return findString(objName);
        } catch (Exception e) {
            return objName;
        }
    }
}
