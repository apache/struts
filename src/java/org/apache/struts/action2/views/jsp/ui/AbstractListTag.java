/*
 * Copyright (c) 2002-2005 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp.ui;

import org.apache.struts.action2.components.ListUIBean;

/**
 * @author Matt Ho <a href="mailto:matt@enginegreen.com">&lt;matt@enginegreen.com&gt;</a>
 * @version $Id$
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
