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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.UpDownSelect;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * @see UpDownSelect
 */
public class UpDownSelectTag extends SelectTag {

    private static final long serialVersionUID = -8136573053799541353L;

    protected String allowMoveUp;
    protected String allowMoveDown;
    protected String allowSelectAll;

    protected String moveUpLabel;
    protected String moveDownLabel;
    protected String selectAllLabel;


    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new UpDownSelect(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        UpDownSelect c = (UpDownSelect) component;

        c.setAllowMoveUp(allowMoveUp);
        c.setAllowMoveDown(allowMoveDown);
        c.setAllowSelectAll(allowSelectAll);

        c.setMoveUpLabel(moveUpLabel);
        c.setMoveDownLabel(moveDownLabel);
        c.setSelectAllLabel(selectAllLabel);

    }


    public String getAllowMoveUp() {
        return allowMoveUp;
    }

    public void setAllowMoveUp(String allowMoveUp) {
        this.allowMoveUp = allowMoveUp;
    }



    public String getAllowMoveDown() {
        return allowMoveDown;
    }

    public void setAllowMoveDown(String allowMoveDown) {
        this.allowMoveDown = allowMoveDown;
    }



    public String getAllowSelectAll() {
        return allowSelectAll;
    }

    public void setAllowSelectAll(String allowSelectAll) {
        this.allowSelectAll = allowSelectAll;
    }


    public String getMoveUpLabel() {
        return moveUpLabel;
    }

    public void setMoveUpLabel(String moveUpLabel) {
        this.moveUpLabel = moveUpLabel;
    }



    public String getMoveDownLabel() {
        return moveDownLabel;
    }

    public void setMoveDownLabel(String moveDownLabel) {
        this.moveDownLabel = moveDownLabel;
    }



    public String getSelectAllLabel() {
        return selectAllLabel;
    }

    public void setSelectAllLabel(String selectAllLabel) {
        this.selectAllLabel = selectAllLabel;
    }
}
