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

package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.components.ListUIBean;

/**
 */
public abstract class AbstractListTag extends AbstractUITag {
    protected String list;
    protected String listKey;
    protected String listValue;
    protected String listValueKey;
    protected String listLabelKey;
    protected String listCssClass;
    protected String listCssStyle;
    protected String listTitle;

    protected void populateParams() {
        super.populateParams();

        ListUIBean listUIBean = ((ListUIBean) component);
        listUIBean.setList(list);
        listUIBean.setListKey(listKey);
        listUIBean.setListValue(listValue);
        listUIBean.setListValueKey(listValueKey);
        listUIBean.setListLabelKey(listLabelKey);
        listUIBean.setListCssClass(listCssClass);
        listUIBean.setListCssStyle(listCssStyle);
        listUIBean.setListTitle(listTitle);
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

    public void setListValueKey(String listValueKey) {
        this.listValueKey = listValueKey;
    }

    public void setListLabelKey(String listLabelKey) {
        this.listLabelKey = listLabelKey;
    }

    public void setListCssClass(String listCssClass) {
        this.listCssClass = listCssClass;
    }

    public void setListCssStyle(String listCssStyle) {
        this.listCssStyle = listCssStyle;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }
}
