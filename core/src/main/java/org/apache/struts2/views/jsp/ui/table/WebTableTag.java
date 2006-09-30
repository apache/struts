/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.views.jsp.ui.table;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.table.WebTable;
import org.apache.struts2.views.jsp.ui.ComponentTag;

import com.opensymphony.xwork2.util.ValueStack;


/**
 * @see WebTable
 */
public class WebTableTag extends ComponentTag {

	private static final long serialVersionUID = 2978932111492397942L;
	
	protected String sortOrder;
    protected String modelName;
    protected boolean sortable;
    protected int sortColumn;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
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
