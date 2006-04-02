/*
 * Copyright (c) 2002-2005 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.jsp.ui;

import org.apache.struts.webwork.components.ListUIBean;

/**
 * @author Matt Ho <a href="mailto:matt@enginegreen.com">&lt;matt@enginegreen.com&gt;</a>
 * @version $Id: AbstractListTag.java,v 1.25 2005/11/27 21:48:34 rgielen Exp $
 */
public abstract class AbstractListTag extends AbstractUITag {
    protected String list;
    protected String listKey;
    protected String listValue;

    protected void populateParams() {
        super.populateParams();

        ListUIBean listUIBean = ((ListUIBean) component);
        listUIBean.setList(list);
        listUIBean.setListKey(listKey);
        listUIBean.setListValue(listValue);
    }

    public void setList(String list) {
        this.list = list;
    }

    public void setListKey(String listKey) {
        this.listKey = listKey;
    }

    public void setListValue(String listValue) {
        this.listValue = listValue;
    }
}
