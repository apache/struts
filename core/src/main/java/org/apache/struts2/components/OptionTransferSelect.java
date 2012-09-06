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

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * Create a option transfer select component which is basically two &lt;select ...&gt;
 * tag with buttons in the middle of them allowing options in each of the
 * &lt;select ...&gt; to be moved between themselves. Will auto-select all its
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
 * that the optiontransferselect tag is being used in a form tag. The generated id
 * and doubleId will be &lt;form_id&gt;_&lt;optiontransferselect_nameame&gt; and
 * &lt;form_id&gt;_&lt;optiontransferselect_doubleName&gt; respectively.
 *
 * <!-- END SNIPPET: notice -->
 *
 * <p/>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;-- minimum configuration --&gt;
 * &lt;s:optiontransferselect
 *      label="Favourite Cartoons Characters"
 *      name="leftSideCartoonCharacters"
 *      list="{'Popeye', 'He-Man', 'Spiderman'}"
 *      doubleName="rightSideCartoonCharacters"
 *      doubleList="{'Superman', 'Mickey Mouse', 'Donald Duck'}"
 *  /&gt;
 *
 *  &lt;-- possible configuration --&gt;
 *  &lt;s:optiontransferselect
 *      label="Favourite Cartoons Characters"
 *      name="leftSideCartoonCharacters"
 *      leftTitle="Left Title"
 *      rightTitle="Right Title"
 *      list="{'Popeye', 'He-Man', 'Spiderman'}"
 *      multiple="true"
 *      headerKey="headerKey"
 *      headerValue="--- Please Select ---"
 *      emptyOption="true"
 *      doubleList="{'Superman', 'Mickey Mouse', 'Donald Duck'}"
 *      doubleName="rightSideCartoonCharacters"
 *      doubleHeaderKey="doubleHeaderKey"
 *      doubleHeaderValue="--- Please Select ---"
 *      doubleEmptyOption="true"
 *      doubleMultiple="true"
 *  /&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 */
@StrutsTag(name="optiontransferselect", tldTagClass="org.apache.struts2.views.jsp.ui.OptionTransferSelectTag", description="Renders an input form")
public class OptionTransferSelect extends DoubleListUIBean {

    private static final Logger LOG = LoggerFactory.getLogger(OptionTransferSelect.class);

    private static final String TEMPLATE = "optiontransferselect";

    protected String allowAddToLeft;
    protected String allowAddToRight;
    protected String allowAddAllToLeft;
    protected String allowAddAllToRight;
    protected String allowSelectAll;
    protected String allowUpDownOnLeft;
    protected String allowUpDownOnRight;

    protected String leftTitle;
    protected String rightTitle;

    protected String buttonCssClass;
    protected String buttonCssStyle;

    protected String addToLeftLabel;
    protected String addToRightLabel;
    protected String addAllToLeftLabel;
    protected String addAllToRightLabel;
    protected String selectAllLabel;
    protected String leftUpLabel;
    protected String leftDownlabel;
    protected String rightUpLabel;
    protected String rightDownLabel;

    protected String addToLeftOnclick;
    protected String addToRightOnclick;
    protected String addAllToLeftOnclick;
    protected String addAllToRightOnclick;
    protected String selectAllOnclick;
    protected String upDownOnLeftOnclick;
    protected String upDownOnRightOnclick;


    public OptionTransferSelect(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }


    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        Object doubleValue = null;

        // override DoubleListUIBean's
        if (doubleList != null) {
            doubleValue = findValue(doubleList);
            addParameter("doubleList", doubleValue);
        }
        if (size == null || size.trim().length() <= 0) {
            addParameter("size", "15");
        }
        if (doubleSize == null || doubleSize.trim().length() <= 0) {
            addParameter("doubleSize", "15");
        }
        if (multiple == null || multiple.trim().length() <= 0) {
            addParameter("multiple", Boolean.TRUE);
        }
        if (doubleMultiple == null || doubleMultiple.trim().length() <= 0) {
            addParameter("doubleMultiple", Boolean.TRUE);
        }





        // buttonCssClass
        if (buttonCssClass != null && buttonCssClass.trim().length() > 0) {
            addParameter("buttonCssClass", buttonCssClass);
        }

        // buttonCssStyle
        if (buttonCssStyle != null && buttonCssStyle.trim().length() > 0) {
            addParameter("buttonCssStyle", buttonCssStyle);
        }



        // allowSelectAll
        addParameter("allowSelectAll",
                allowSelectAll != null ? findValue(allowSelectAll, Boolean.class) : Boolean.TRUE);

        // allowAddToLeft
        addParameter("allowAddToLeft",
                allowAddToLeft != null ? findValue(allowAddToLeft, Boolean.class) : Boolean.TRUE);

        // allowAddToRight
        addParameter("allowAddToRight",
                allowAddToRight != null ? findValue(allowAddToRight, Boolean.class) : Boolean.TRUE);

        // allowAddAllToLeft
        addParameter("allowAddAllToLeft",
                allowAddAllToLeft != null ? findValue(allowAddAllToLeft, Boolean.class) : Boolean.TRUE);

        // allowAddAllToRight
        addParameter("allowAddAllToRight",
                allowAddAllToRight != null ? findValue(allowAddAllToRight, Boolean.class) : Boolean.TRUE);

        // allowUpDownOnLeft
        addParameter("allowUpDownOnLeft",
                allowUpDownOnLeft != null ? findValue(allowUpDownOnLeft, Boolean.class) : Boolean.TRUE);

        // allowUpDownOnRight
        addParameter("allowUpDownOnRight",
                allowUpDownOnRight != null ? findValue(allowUpDownOnRight, Boolean.class) : Boolean.TRUE);


        // leftTitle
        if (leftTitle != null) {
            addParameter("leftTitle", findValue(leftTitle, String.class));
        }

        // rightTitle
        if (rightTitle != null) {
            addParameter("rightTitle", findValue(rightTitle, String.class));
        }


        // addToLeftLabel
        addParameter("addToLeftLabel",
                addToLeftLabel != null ? findValue(addToLeftLabel, String.class) : "<-" );

        // addToRightLabel
        addParameter("addToRightLabel",
                addToRightLabel != null ? findValue(addToRightLabel, String.class) : "->");

        // addAllToLeftLabel
        addParameter("addAllToLeftLabel",
                addAllToLeftLabel != null ? findValue(addAllToLeftLabel, String.class) : "<<--");

        // addAllToRightLabel
        addParameter("addAllToRightLabel",
                addAllToRightLabel != null ? findValue(addAllToRightLabel, String.class) : "-->>");

        // selectAllLabel
        addParameter("selectAllLabel",
                selectAllLabel != null ? findValue(selectAllLabel, String.class) : "<*>");

        // leftUpLabel
        addParameter("leftUpLabel",
                leftUpLabel != null ? findValue(leftUpLabel, String.class) : "^");


        // leftDownLabel
        addParameter("leftDownLabel",
                leftDownlabel != null ? findValue(leftDownlabel, String.class) : "v");


        // rightUpLabel
        addParameter("rightUpLabel",
                rightUpLabel != null ? findValue(rightUpLabel, String.class) : "^");


        // rightDownlabel
        addParameter("rightDownLabel",
                rightDownLabel != null ? findValue(rightDownLabel, String.class) : "v");


        // selectAllOnclick
        addParameter("selectAllOnclick",
                selectAllOnclick != null ? findValue(selectAllOnclick, String.class) : "");

        // addToLeftOnclick
        addParameter("addToLeftOnclick",
                addToLeftOnclick != null ? findValue(addToLeftOnclick, String.class) : "");

        // addToRightOnclick
        addParameter("addToRightOnclick",
                addToRightOnclick != null ? findValue(addToRightOnclick, String.class) : "");

        // addAllToLeftOnclick
        addParameter("addAllToLeftOnclick",
                addAllToLeftOnclick != null ? findValue(addAllToLeftOnclick, String.class) : "");

        // addAllToRightOnclick
        addParameter("addAllToRightOnclick",
                addAllToRightOnclick != null ? findValue(addAllToRightOnclick, String.class) : "");

        // upDownOnLeftOnclick
        addParameter("upDownOnLeftOnclick",
                upDownOnLeftOnclick != null ? findValue(upDownOnLeftOnclick, String.class) : "");

        // upDownOnRightOnclick
        addParameter("upDownOnRightOnclick",
                upDownOnRightOnclick != null ? findValue(upDownOnRightOnclick, String.class) : "");



        // inform the form component our select tag infos, so they know how to select
        // its elements upon onsubmit
        Form formAncestor = (Form) findAncestor(Form.class);
        if (formAncestor != null) {

            // inform ancestor form that we are having a customOnsubmit (see form-close.ftl [simple theme])
            enableAncestorFormCustomOnsubmit();


            // key -> select tag id, value -> headerKey (if exists)
            Map formOptiontransferselectIds = (Map) formAncestor.getParameters().get("optiontransferselectIds");
            Map formOptiontransferselectDoubleIds = (Map) formAncestor.getParameters().get("optiontransferselectDoubleIds");

            // init lists
            if (formOptiontransferselectIds == null) {
                formOptiontransferselectIds = new LinkedHashMap();
            }
            if (formOptiontransferselectDoubleIds == null) {
                formOptiontransferselectDoubleIds = new LinkedHashMap();
            }


            // id
            String tmpId = (String) getParameters().get("id");
            String tmpHeaderKey = (String) getParameters().get("headerKey");
            if (tmpId != null && (! formOptiontransferselectIds.containsKey(tmpId))) {
                formOptiontransferselectIds.put(tmpId, tmpHeaderKey);
            }

            // doubleId
            String tmpDoubleId = (String) getParameters().get("doubleId");
            String tmpDoubleHeaderKey = (String) getParameters().get("doubleHeaderKey");
            if (tmpDoubleId != null && (! formOptiontransferselectDoubleIds.containsKey(tmpDoubleId))) {
                formOptiontransferselectDoubleIds.put(tmpDoubleId, tmpDoubleHeaderKey);
            }

            formAncestor.getParameters().put("optiontransferselectIds", formOptiontransferselectIds);
            formAncestor.getParameters().put("optiontransferselectDoubleIds", formOptiontransferselectDoubleIds);

        }
        else {
            if (LOG.isWarnEnabled()) {
        	LOG.warn("form enclosing optiontransferselect "+this+" not found, auto select upon form submit of optiontransferselect will not work");
            }
        }
    }



    public String getAddAllToLeftLabel() {
        return addAllToLeftLabel;
    }

    @StrutsTagAttribute(description="Set Add To Left button label")
    public void setAddAllToLeftLabel(String addAllToLeftLabel) {
        this.addAllToLeftLabel = addAllToLeftLabel;
    }

    public String getAddAllToRightLabel() {
        return addAllToRightLabel;
    }

    @StrutsTagAttribute(description="Set Add All To Right button label")
    public void setAddAllToRightLabel(String addAllToRightLabel) {
        this.addAllToRightLabel = addAllToRightLabel;
    }

    public String getAddToLeftLabel() {
        return addToLeftLabel;
    }

    @StrutsTagAttribute(description="Set Add To Left button label")
    public void setAddToLeftLabel(String addToLeftLabel) {
        this.addToLeftLabel = addToLeftLabel;
    }

    public String getAddToRightLabel() {
        return addToRightLabel;
    }

    @StrutsTagAttribute(description="Set Add To Right button label")
    public void setAddToRightLabel(String addToRightLabel) {
        this.addToRightLabel = addToRightLabel;
    }

    public String getAllowAddAllToLeft() {
        return allowAddAllToLeft;
    }

    @StrutsTagAttribute(description="Enable Add All To Left button")
    public void setAllowAddAllToLeft(String allowAddAllToLeft) {
        this.allowAddAllToLeft = allowAddAllToLeft;
    }

    public String getAllowAddAllToRight() {
        return allowAddAllToRight;
    }

    @StrutsTagAttribute(description="Enable Add All To Right button")
    public void setAllowAddAllToRight(String allowAddAllToRight) {
        this.allowAddAllToRight = allowAddAllToRight;
    }

    public String getAllowAddToLeft() {
        return allowAddToLeft;
    }

    @StrutsTagAttribute(description="Enable Add To Left button")
    public void setAllowAddToLeft(String allowAddToLeft) {
        this.allowAddToLeft = allowAddToLeft;
    }

    public String getAllowAddToRight() {
        return allowAddToRight;
    }

    @StrutsTagAttribute(description="Enable Add To Right button")
    public void setAllowAddToRight(String allowAddToRight) {
        this.allowAddToRight = allowAddToRight;
    }

    public String getLeftTitle() {
        return leftTitle;
    }

    @StrutsTagAttribute(description="Enable up / down on the left side")
    public void setAllowUpDownOnLeft(String allowUpDownOnLeft) {
        this.allowUpDownOnLeft = allowUpDownOnLeft;
    }

    public String getAllowUpDownOnLeft() {
        return this.allowUpDownOnLeft;
    }

    @StrutsTagAttribute(description="Enable up / down on the right side")
    public void setAllowUpDownOnRight(String allowUpDownOnRight) {
        this.allowUpDownOnRight = allowUpDownOnRight;
    }

    public String getAllowUpDownOnRight() {
        return this.allowUpDownOnRight;
    }

    @StrutsTagAttribute(description="Set Left title")
    public void setLeftTitle(String leftTitle) {
        this.leftTitle = leftTitle;
    }

    public String getRightTitle() {
        return rightTitle;
    }

    @StrutsTagAttribute(description="Set Right title")
    public void setRightTitle(String rightTitle) {
        this.rightTitle = rightTitle;
    }

    @StrutsTagAttribute(description="Enable Select All button")
    public void setAllowSelectAll(String allowSelectAll) {
        this.allowSelectAll = allowSelectAll;
    }

    public String getAllowSelectAll() {
        return this.allowSelectAll;
    }

    @StrutsTagAttribute(description="Set Select All button label")
    public void setSelectAllLabel(String selectAllLabel) {
        this.selectAllLabel = selectAllLabel;
    }

    public String getSelectAllLabel() {
        return this.selectAllLabel;
    }

    @StrutsTagAttribute(description="Set buttons css class")
    public void setButtonCssClass(String buttonCssClass) {
        this.buttonCssClass = buttonCssClass;
    }

    public String getButtonCssClass() {
        return buttonCssClass;
    }

    @StrutsTagAttribute(description="Set button css style")
    public void setButtonCssStyle(String buttonCssStyle) {
        this.buttonCssStyle = buttonCssStyle;
    }

    public String getButtonCssStyle() {
        return this.buttonCssStyle;
    }

    @StrutsTagAttribute(description="Up label for the left side")
    public void setLeftUpLabel(String leftUpLabel) {
        this.leftUpLabel = leftUpLabel;
    }
    public String getLeftUpLabel() {
        return this.leftUpLabel;
    }

    @StrutsTagAttribute(description="Down label for the left side.")
    public void setLeftDownLabel(String leftDownLabel) {
        this.leftDownlabel = leftDownLabel;
    }
    public String getLeftDownLabel() {
        return this.leftDownlabel;
    }

    @StrutsTagAttribute(description="Up label for the right side.")
    public void setRightUpLabel(String rightUpLabel) {
        this.rightUpLabel = rightUpLabel;
    }
    public String getRightUpLabel() {
        return this.rightUpLabel;
    }

    @StrutsTagAttribute(description="Down label for the left side.")
    public void setRightDownLabel(String rightDownlabel) {
        this.rightDownLabel = rightDownlabel;
    }
    public String getRightDownLabel() {
        return rightDownLabel;
    }

    public String getAddAllToLeftOnclick() {
        return addAllToLeftOnclick;
    }

    @StrutsTagAttribute(description="Javascript to run after Add All To Left button pressed")
    public void setAddAllToLeftOnclick(String addAllToLeftOnclick) {
        this.addAllToLeftOnclick = addAllToLeftOnclick;
    }

    public String getAddAllToRightOnclick() {
        return addAllToRightOnclick;
    }

    @StrutsTagAttribute(description="Javascript to run after Add All To Right button pressed")
    public void setAddAllToRightOnclick(String addAllToRightOnclick) {
        this.addAllToRightOnclick = addAllToRightOnclick;
    }

    public String getAddToLeftOnclick() {
        return addToLeftOnclick;
    }

    @StrutsTagAttribute(description="Javascript to run after Add To Left button pressed")
    public void setAddToLeftOnclick(String addToLeftOnclick) {
        this.addToLeftOnclick = addToLeftOnclick;
    }

    public String getAddToRightOnclick() {
        return addToRightOnclick;
    }

    @StrutsTagAttribute(description="Javascript to run after Add To Right button pressed")
    public void setAddToRightOnclick(String addToRightOnclick) {
        this.addToRightOnclick = addToRightOnclick;
    }

    @StrutsTagAttribute(description="Javascript to run after up / down on the left side buttons pressed")
    public void setUpDownOnLeftOnclick(String upDownOnLeftOnclick) {
        this.upDownOnLeftOnclick = upDownOnLeftOnclick;
    }

    public String getUpDownOnLeftOnclick() {
        return this.upDownOnLeftOnclick;
    }

    @StrutsTagAttribute(description="Javascript to run after up / down on the right side buttons pressed")
    public void setUpDownOnRightOnclick(String upDownOnRightOnclick) {
        this.upDownOnRightOnclick = upDownOnRightOnclick;
    }

    public String getUpDownOnRightOnclick() {
        return this.upDownOnRightOnclick;
    }

    @StrutsTagAttribute(description="Javascript to run after Select All button pressed")
    public void setSelectAllOnclick(String selectAllOnclick) {
        this.selectAllOnclick = selectAllOnclick;
    }

    public String getSelectAllOnclick() {
        return this.selectAllOnclick;
    }

}
