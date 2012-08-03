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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Create a input transfer select component which is basically an text input
 * and  &lt;select ...&gt; tag with buttons in the middle of them allowing text
 * to be added to the transfer select. Will auto-select all its
 * elements upon its containing form submision.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * <p/>
 *
 *
 * <!-- START SNIPPET: notice -->
 *
 * NOTE: The id and doubleId need not be supplied as they will generated provided
 * that the inputtransferselect tag is being used in a form tag. The generated id
 * and doubleId will be &lt;form_id&gt;_&lt;inputtransferselect_doubleName&gt; and
 * &lt;form_id&gt;_&lt;inputtransferselect_doubleName&gt; respectively.
 *
 * <!-- END SNIPPET: notice -->
 *
 * <p/>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;-- minimum configuration --&gt;
 * &lt;s:inputtransferselect
 *      label="Favourite Cartoons Characters"
 *      name="cartoons"
 *      list="{'Popeye', 'He-Man', 'Spiderman'}"
 *  /&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 */
@StrutsTag(name="inputtransferselect", tldTagClass="org.apache.struts2.views.jsp.ui.InputTransferSelectTag", description="Renders an input form")
public class InputTransferSelect extends ListUIBean {

    private static final Logger LOG = LoggerFactory.getLogger(InputTransferSelect.class);

    private static final String TEMPLATE = "inputtransferselect";

    protected String size;
    protected String multiple;

    protected String allowRemoveAll;
    protected String allowUpDown;

    protected String leftTitle;
    protected String rightTitle;

    protected String buttonCssClass;
    protected String buttonCssStyle;

    protected String addLabel;
    protected String removeLabel;
    protected String removeAllLabel;
    protected String upLabel;
    protected String downLabel;

    protected String headerKey;
    protected String headerValue;


    public InputTransferSelect(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }


    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (size == null || size.trim().length() <= 0) {
            addParameter("size", "5");
        }

        if (multiple == null || multiple.trim().length() <= 0) {
            addParameter("multiple", Boolean.TRUE);
        }

        // allowUpDown
        addParameter("allowUpDown", allowUpDown != null ? findValue(allowUpDown, Boolean.class) : Boolean.TRUE);

        // allowRemoveAll
        addParameter("allowRemoveAll", allowRemoveAll != null ? findValue(allowRemoveAll, Boolean.class) : Boolean.TRUE);


        // leftTitle
        if (leftTitle != null) {
            addParameter("leftTitle", findValue(leftTitle, String.class));
        }

        // rightTitle
        if (rightTitle != null) {
            addParameter("rightTitle", findValue(rightTitle, String.class));
        }


        // buttonCssClass
        if (buttonCssClass != null && buttonCssClass.trim().length() > 0) {
            addParameter("buttonCssClass", buttonCssClass);
        }

        // buttonCssStyle
        if (buttonCssStyle != null && buttonCssStyle.trim().length() > 0) {
            addParameter("buttonCssStyle", buttonCssStyle);
        }

        // addLabel
        addParameter("addLabel", addLabel != null ? findValue(addLabel, String.class) : "->" );

        // removeLabel
        addParameter("removeLabel", removeLabel != null ? findValue(removeLabel, String.class) : "<-");

        // removeAllLabel
        addParameter("removeAllLabel", removeAllLabel != null ? findValue(removeAllLabel, String.class) : "<<--");


        // upLabel
        addParameter("upLabel", upLabel != null ? findValue(upLabel, String.class) : "^");


        // leftDownLabel
        addParameter("downLabel", downLabel != null ? findValue(downLabel, String.class) : "v");

        if ((headerKey != null) && (headerValue != null)) {
            addParameter("headerKey", findString(headerKey));
            addParameter("headerValue", findString(headerValue));
        }



        // inform the form component our select tag infos, so they know how to select
        // its elements upon onsubmit
        Form formAncestor = (Form) findAncestor(Form.class);
        if (formAncestor != null) {

            // inform ancestor form that we are having a customOnsubmit (see form-close.ftl [simple theme])
            enableAncestorFormCustomOnsubmit();


            // key -> select tag id, value -> headerKey (if exists)
            Map formInputtransferselectIds = (Map) formAncestor.getParameters().get("inputtransferselectIds");

            // init lists
            if (formInputtransferselectIds == null) {
                formInputtransferselectIds = new LinkedHashMap();
            }

            // id
            String tmpId = (String) getParameters().get("id");
            String tmpHeaderKey = (String) getParameters().get("headerKey");
            if (tmpId != null && (! formInputtransferselectIds.containsKey(tmpId))) {
                formInputtransferselectIds.put(tmpId, tmpHeaderKey);
            }

            formAncestor.getParameters().put("inputtransferselectIds", formInputtransferselectIds);

        }
        else {
            if (LOG.isWarnEnabled()) {
        	LOG.warn("form enclosing inputtransferselect "+this+" not found, auto select upon form submit of inputtransferselect will not work");
            }
        }
    }

    public String getSize() {
        return size;
    }

    @StrutsTagAttribute(description="the size of the select box")
    public void setSize(String size) {
        this.size = size;
    }

    public String getMultiple() {
        return multiple;
    }

    @StrutsTagAttribute(description="Determine whether or not multiple entries are shown")
    public void setMultiple(String multiple) {
        this.multiple = multiple;
    }

    public String getAllowRemoveAll() {
        return allowRemoveAll;
    }

    @StrutsTagAttribute(description="Determine whether the remove all button will display")
    public void setAllowRemoveAll(String allowRemoveAll) {
        this.allowRemoveAll = allowRemoveAll;
    }

    public String getAllowUpDown() {
        return allowUpDown;
    }

    @StrutsTagAttribute(description="Determine whether items in the list can be reordered")
    public void setAllowUpDown(String allowUpDown) {
        this.allowUpDown = allowUpDown;
    }

    public String getLeftTitle() {
        return leftTitle;
    }

    @StrutsTagAttribute(description="the left hand title")
    public void setLeftTitle(String leftTitle) {
        this.leftTitle = leftTitle;
    }

    public String getRightTitle() {
        return rightTitle;
    }

    @StrutsTagAttribute(description="the right hand title")
    public void setRightTitle(String rightTitle) {
        this.rightTitle = rightTitle;
    }

    public String getButtonCssClass() {
        return buttonCssClass;
    }

    @StrutsTagAttribute(description="the css class used for rendering buttons")
    public void setButtonCssClass(String buttonCssClass) {
        this.buttonCssClass = buttonCssClass;
    }

    public String getButtonCssStyle() {
        return buttonCssStyle;
    }

    @StrutsTagAttribute(description="the css style used for rendering buttons")
    public void setButtonCssStyle(String buttonCssStyle) {
        this.buttonCssStyle = buttonCssStyle;
    }

    public String getAddLabel() {
        return addLabel;
    }

    @StrutsTagAttribute(description="the label used for the add button")
    public void setAddLabel(String addLabel) {
        this.addLabel = addLabel;
    }

    public String getRemoveLabel() {
        return removeLabel;
    }

    @StrutsTagAttribute(description="the label used for the remove button")
    public void setRemoveLabel(String removeLabel) {
        this.removeLabel = removeLabel;
    }

    public String getRemoveAllLabel() {
        return removeAllLabel;
    }

    @StrutsTagAttribute(description="the label used for the remove all button")
    public void setRemoveAllLabel(String removeAllLabel) {
        this.removeAllLabel = removeAllLabel;
    }

    public String getUpLabel() {
        return upLabel;
    }

    @StrutsTagAttribute(description="the label used for the up button")
    public void setUpLabel(String upLabel) {
        this.upLabel = upLabel;
    }

    public String getDownLabel() {
        return downLabel;
    }

    @StrutsTagAttribute(description="the label used for the down button")
    public void setDownLabel(String downLabel) {
        this.downLabel = downLabel;
    }

    public String getHeaderKey() {
        return headerKey;
    }

    @StrutsTagAttribute(description="the header key of the select box")
    public void setHeaderKey(String headerKey) {
        this.headerKey = headerKey;
    }

    public String getHeaderValue() {
        return headerValue;
    }

    @StrutsTagAttribute(description="the header value of the select box")
    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }
}
