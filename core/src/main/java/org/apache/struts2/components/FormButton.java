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

package org.apache.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.inject.Inject;

/**
 * FormButton.
 */
public abstract class FormButton extends ClosingUIBean {

    static final String BUTTONTYPE_INPUT = "input";
    static final String BUTTONTYPE_BUTTON = "button";
    static final String BUTTONTYPE_IMAGE = "image";

    protected String action;
    protected String method;
    protected String align;
    protected String type;

    public FormButton(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    //public void evaluateParams() {
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        if (align == null) {
            align = "right";
        }

        String submitType = BUTTONTYPE_INPUT;
        if (type != null && (BUTTONTYPE_BUTTON.equalsIgnoreCase(type) || (supportsImageType() && BUTTONTYPE_IMAGE.equalsIgnoreCase(type))))
        {
            submitType = type;
        }

        //super.evaluateParams();

        addParameter("type", submitType);

        if (!BUTTONTYPE_INPUT.equals(submitType) && (label == null)) {
            addParameter("label", getParameters().get("nameValue"));
        }

        if (action != null || method != null) {
            String name;

            if (action != null) {
                ActionMapping mapping = new ActionMapping();
                mapping.setName(findString(action));
                if (method != null) {
                    mapping.setMethod(findString(method));
                }
                mapping.setExtension("");
                name = "action:" + actionMapper.getUriFromActionMapping(mapping);
            } else {
                name = "method:" + findString(method);
            }

            addParameter("name", name);
        }

        addParameter("align", findString(align));

    }

    /**
     * Override UIBean's implementation, such that component Html id is determined
     * in the following order :-
     * <ol>
     *   <li>This component id attribute</li>
     *   <li>[containing_form_id]_[this_component_name]</li>
     *   <li>[containing_form_id]_[this_component_action]_[this_component_method]</li>
     *   <li>[containing_form_id]_[this_component_method]</li>
     *   <li>[this_component_name]</li>
     *   <li>[this_component_action]_[this_component_method]</li>
     *   <li>[this_component_method]</li>
     *   <li>[an increasing sequential number unique to the form starting with 0]</li>
     * </ol>
     */
    protected void populateComponentHtmlId(Form form) {
        String _tmp_id = "";
        if (id != null) {
            // this check is needed for backwards compatibility with 2.1.x
        	_tmp_id = findStringIfAltSyntax(id);
        }
        else {
            if (form != null && form.getParameters().get("id") != null) {
                _tmp_id = _tmp_id + form.getParameters().get("id").toString() + "_";
            }
            if (name != null) {
                _tmp_id = _tmp_id + escape(name);
            } else if (action != null || method != null){
                if (action != null) {
                    _tmp_id = _tmp_id + escape(action);
                }
                if (method != null) {
                    _tmp_id = _tmp_id + "_" + escape(method);
                }
            } else {
                // if form is null, this component is used, without a form, i guess
                // there's not much we could do then.
                if (form != null) {
                    _tmp_id = _tmp_id + form.getSequence();
                }
            }
        }
        addParameter("id", _tmp_id);
    }

    /**
     * Indicate whether the concrete button supports the type "image".
     *
     * @return <tt>true</tt> if type image is supported.
     */
    protected abstract boolean supportsImageType();

    @Inject
    public void setActionMapper(ActionMapper mapper) {
        this.actionMapper = mapper;
    }

    @StrutsTagAttribute(description="Set action attribute.")
    public void setAction(String action) {
        this.action = action;
    }

    @StrutsTagAttribute(description="Set method attribute.")
    public void setMethod(String method) {
        this.method = method;
    }

    @StrutsTagAttribute(description="HTML align attribute.")
    public void setAlign(String align) {
        this.align = align;
    }

    @StrutsTagAttribute(description="The type of submit to use. Valid values are <i>input</i>, " +
                "<i>button</i> and <i>image</i>.", defaultValue="input")
    public void setType(String type) {
        this.type = type;
    }
}
