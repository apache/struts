/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp.ui.table;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.table.WebTable;
import org.apache.struts.action2.views.jsp.ui.ComponentTag;
import com.opensymphony.xwork.util.OgnlValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @see WebTable
 */
public class WebTableTag extends ComponentTag {

	private static final long serialVersionUID = 2978932111492397942L;
	
	protected String sortOrder;
    protected String modelName;
    protected boolean sortable;
    protected int sortColumn;

    public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new WebTable(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        WebTable table = (WebTable) component;
        table.setSortOrder(sortOrder);
        table.setSortable(sortable);
        table.setModelName(modelName);
        table.setSortOrder(sortOrder);
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    public void setSortColumn(int sortColumn) {
        this.sortColumn = sortColumn;
    }
}
