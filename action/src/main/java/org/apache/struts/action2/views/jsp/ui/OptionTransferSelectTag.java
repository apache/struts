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
package org.apache.struts.action2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action2.components.Component;
import org.apache.struts.action2.components.OptionTransferSelect;
import com.opensymphony.xwork.util.OgnlValueStack;

/**
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class OptionTransferSelectTag extends AbstractDoubleListTag {

	private static final long serialVersionUID = 250474334495763536L;
	
	protected String allowAddToLeft;
	protected String allowAddToRight;
	protected String allowAddAllToLeft;
	protected String allowAddAllToRight;
	protected String allowSelectAll;
	
	protected String leftTitle;
	protected String rightTitle;
	
	protected String buttonCssClass;
	protected String buttonCssStyle;

	protected String addToLeftLabel;
	protected String addToRightLabel;
	protected String addAllToLeftLabel;
	protected String addAllToRightLabel;
	protected String selectAllLabel;
	
	
	public Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new OptionTransferSelect(stack, req, res);
	}
	
	protected void populateParams() {
		super.populateParams();
		
		OptionTransferSelect optionTransferSelect = (OptionTransferSelect) component;
		optionTransferSelect.setAllowAddToLeft(allowAddToLeft);
		optionTransferSelect.setAllowAddToRight(allowAddToRight);
		optionTransferSelect.setAllowAddAllToLeft(allowAddAllToLeft);
		optionTransferSelect.setAllowAddAllToRight(allowAddAllToRight);
		optionTransferSelect.setAllowSelectAll(allowSelectAll);
		
		optionTransferSelect.setAddToLeftLabel(addToLeftLabel);
		optionTransferSelect.setAddToRightLabel(addToRightLabel);
		optionTransferSelect.setAddAllToLeftLabel(addAllToLeftLabel);
		optionTransferSelect.setAddAllToRightLabel(addAllToRightLabel);
		optionTransferSelect.setSelectAllLabel(selectAllLabel);
		
		optionTransferSelect.setButtonCssClass(buttonCssClass);
		optionTransferSelect.setButtonCssStyle(buttonCssStyle);
		
		optionTransferSelect.setLeftTitle(leftTitle);
		optionTransferSelect.setRightTitle(rightTitle);
	}


	public String getAddAllToLeftLabel() {
		return addAllToLeftLabel;
	}


	public void setAddAllToLeftLabel(String addAllToLeftLabel) {
		this.addAllToLeftLabel = addAllToLeftLabel;
	}


	public String getAddAllToRightLabel() {
		return addAllToRightLabel;
	}


	public void setAddAllToRightLabel(String addAllToRightLabel) {
		this.addAllToRightLabel = addAllToRightLabel;
	}


	public String getAddToLeftLabel() {
		return addToLeftLabel;
	}


	public void setAddToLeftLabel(String addToLeftLabel) {
		this.addToLeftLabel = addToLeftLabel;
	}


	public String getAddToRightLabel() {
		return addToRightLabel;
	}


	public void setAddToRightLabel(String addToRightLabel) {
		this.addToRightLabel = addToRightLabel;
	}


	public String getAllowAddAllToLeft() {
		return allowAddAllToLeft;
	}


	public void setAllowAddAllToLeft(String allowAddAllToLeft) {
		this.allowAddAllToLeft = allowAddAllToLeft;
	}


	public String getAllowAddAllToRight() {
		return allowAddAllToRight;
	}


	public void setAllowAddAllToRight(String allowAddAllToRight) {
		this.allowAddAllToRight = allowAddAllToRight;
	}


	public String getAllowAddToLeft() {
		return allowAddToLeft;
	}


	public void setAllowAddToLeft(String allowAddToLeft) {
		this.allowAddToLeft = allowAddToLeft;
	}


	public String getAllowAddToRight() {
		return allowAddToRight;
	}


	public void setAllowAddToRight(String allowAddToRight) {
		this.allowAddToRight = allowAddToRight;
	}


	public String getLeftTitle() {
		return leftTitle;
	}


	public void setLeftTitle(String leftTitle) {
		this.leftTitle = leftTitle;
	}


	public String getRightTitle() {
		return rightTitle;
	}


	public void setRightTitle(String rightTitle) {
		this.rightTitle = rightTitle;
	}
	
	
	public void setAllowSelectAll(String allowSelectAll) {
		this.allowSelectAll = allowSelectAll;
	}
	
	public String getAllowSelectAll() {
		return this.allowSelectAll;
	}
	
	public void setSelectAllLabel(String selectAllLabel) {
		this.selectAllLabel = selectAllLabel;
	}
	
	public String getSelectAllLabel() {
		return this.selectAllLabel;
	}
	
	public void setButtonCssClass(String buttonCssId) {
		this.buttonCssClass = buttonCssId;
	}
	
	public String getButtonCssClass() {
		return buttonCssClass;
	}
	
	public void setButtonCssStyle(String buttonCssStyle) {
		this.buttonCssStyle = buttonCssStyle;
	}
	
	public String getButtonCssStyle() {
		return this.buttonCssStyle;
	}
}
