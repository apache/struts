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

package org.apache.struts2.dojo.views.freemarker.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opensymphony.xwork2.util.ValueStack;

public class DojoModels {
    protected DateTimePickerModel dateTimePicker;
    protected TabbedPanelModel tabbedPanel;
    protected TreeModel treeModel;
    protected TreeNodeModel treenodeModel;
    protected AutocompleterModel autocompleter;
    protected DivModel div;
    protected AnchorModel a;
    protected SubmitModel submit;
    protected BindModel bind;
    protected HeadModel head;
    protected TextAreaModel textarea;
    
    private ValueStack stack;
    private HttpServletRequest req;
    private HttpServletResponse res;
    
    public DojoModels(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        this.stack = stack;
        this.req = req;
        this.res = res;
    }
    
    public BindModel getBind() {
        if (bind == null) {
            bind = new BindModel(stack, req, res);
        }

        return bind;
    }
    
    public TextAreaModel getTextarea() {
        if (textarea == null) {
            textarea = new TextAreaModel(stack, req, res);
        }

        return textarea;
    }
    
    public HeadModel getHead() {
        if (head == null) {
            head = new HeadModel(stack, req, res);
        }

        return head;
    }
    
    public DateTimePickerModel getDatetimepicker() {
        if (dateTimePicker == null) {
            dateTimePicker = new DateTimePickerModel(stack, req, res);
        }

        return dateTimePicker;
    }
    
    public AutocompleterModel getAutocompleter() {
        if (autocompleter == null) {
            autocompleter = new AutocompleterModel(stack, req, res);
        }

        return autocompleter;
    }
    
    public TabbedPanelModel getTabbedpanel() {
        if (tabbedPanel == null) {
            tabbedPanel = new TabbedPanelModel(stack, req, res);
        }

        return tabbedPanel;
    }
    
    public TreeModel getTree() {
        if (treeModel == null) {
            treeModel = new TreeModel(stack,req, res);
        }
        return treeModel;
    }

    public TreeNodeModel getTreenode() {
        if (treenodeModel == null) {
            treenodeModel = new TreeNodeModel(stack, req, res);
        }
        return treenodeModel;
    }
    
    public DivModel getDiv() {
        if (div == null) {
            div = new DivModel(stack, req, res);
        }

        return div;
    }
    
    public AnchorModel getA() {
        if (a == null) {
            a = new AnchorModel(stack, req, res);
        }

        return a;
    }
    
    public SubmitModel getSubmit() {
        if (submit == null) {
            submit = new SubmitModel(stack, req, res);
        }

        return submit;
    }
}
