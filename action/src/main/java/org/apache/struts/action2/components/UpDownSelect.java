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
package org.apache.struts.action2.components;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork.util.OgnlValueStack;

/**
 * <!-- START SNIPPET: javadoc -->
 * 
 * Create a Select component with buttons to move the elements in the select component
 * up and down. When the containing form is submited, its elements will be submitted in 
 * the order they are arranged (top to bottom).
 * 
 * <!-- END SNIPPET: javadoc -->
 * 
 * <p/>
 * 
 * <pre>
 * <!-- START SNIPPET: example -->
 * 
 * &lt;!-- Example 1: simple example --&gt;
 * &lt;a:updownselect 
 * list="#{'england':'England', 'america':'America', 'germany':'Germany'}" 
 * name="prioritisedFavouriteCountries" 
 * headerKey="-1" 
 * headerValue="--- Please Order Them Accordingly ---" 
 * emptyOption="true" /&gt;
 *
 * &lt;!-- Example 2: more complex example --&gt;
 * &lt;a:updownselect 
 * list="defaultFavouriteCartoonCharacters" 
 * name="prioritisedFavouriteCartoonCharacters" 
 * headerKey="-1" 
 * headerValue="--- Please Order ---" 
 * emptyOption="true" 
 * allowMoveUp="true" 
 * allowMoveDown="true" 
 * allowSelectAll="true" 
 * moveUpLabel="Move Up"
 * moveDownLabel="Move Down" 
 * selectAllLabel="Select All" /&gt;
 * 
 * <!-- END SNIPPET: example -->
 * </pre>
 * 
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 * 
 * @a2.tag name="updownselect" tld-body-content="JSP" tld-tag-class="org.apache.struts.action2.views.jsp.ui.UpDownSelectTag"
 * description="Render a up down select element"
 */
public class UpDownSelect extends Select {
	
	private static final Log _log = LogFactory.getLog(UpDownSelect.class);
	

	final public static String TEMPLATE = "updownselect";
	
	protected String allowMoveUp;
	protected String allowMoveDown;
	protected String allowSelectAll;
	
	protected String moveUpLabel;
	protected String moveDownLabel;
	protected String selectAllLabel;
	
	
	public String getDefaultTemplate() {
		return TEMPLATE;
	}
	
	public UpDownSelect(OgnlValueStack stack, HttpServletRequest request, HttpServletResponse response) {
		super(stack, request, response);
	}
	
	public void evaluateParams() {
		super.evaluateParams();
		
		
		// override Select's default
		if (size == null || size.trim().length() <= 0) {
			addParameter("size", "5");
		}
		if (multiple == null || multiple.trim().length() <= 0) {
			addParameter("multiple", Boolean.TRUE);
		}
		
		
		
		if (allowMoveUp != null) {
			addParameter("allowMoveUp", findValue(allowMoveUp, Boolean.class));
		}
		if (allowMoveDown != null) {
			addParameter("allowMoveDown", findValue(allowMoveDown, Boolean.class));
		}
		if (allowSelectAll != null) {
			addParameter("allowSelectAll", findValue(allowSelectAll, Boolean.class));
		}
		
		if (moveUpLabel != null) {
			addParameter("moveUpLabel", findString(moveUpLabel));
		}
		if (moveDownLabel != null) {
			addParameter("moveDownLabel", findString(moveDownLabel));
		}
		if (selectAllLabel != null) {
			addParameter("selectAllLabel", findString(selectAllLabel));
		}
		
		
		// inform our form ancestor about this UpDownSelect so the form knows how to 
		// auto select all options upon it submission
		Form ancestorForm = (Form) findAncestor(Form.class);
		if (ancestorForm != null) {
			
			// inform form ancestor that we are using a custom onsubmit
			enableAncestorFormCustomOnsubmit();
			
			Map m = (Map) ancestorForm.getParameters().get("updownselectIds");
			if (m == null) {
				// map with key -> id ,  value -> headerKey
				m = new LinkedHashMap();
			}
			m.put(getParameters().get("id"), getParameters().get("headerKey"));
			ancestorForm.getParameters().put("updownselectIds", m);
		}
		else {
			_log.warn("no ancestor form found for updownselect "+this+", therefore autoselect of all elements unpon form submission will not work ");
		}
	}

	
	public String getAllowMoveUp() { 
		return allowMoveUp;
	}
	/**
     * Whether move up button should be displayed
	 * @a2.tagattribute required="false" type="Boolean" default="true"
	 */
	public void setAllowMoveUp(String allowMoveUp) {
		this.allowMoveUp = allowMoveUp;
	}
	
	
	
	public String getAllowMoveDown() {
		return allowMoveDown;
	}
	/**
     * Whether move down button should be displayed
	 * @a2.tagattribute required="false" type="Boolean" default="true"
	 */
	public void setAllowMoveDown(String allowMoveDown) {
		this.allowMoveDown = allowMoveDown;
	}
	
	
	
	public String getAllowSelectAll() {
		return allowSelectAll;
	}
	/**
     * Whether or not select all button should be displayed
	 * @a2.tagattribute required="false" type="Boolean" default="true"
	 */
	public void setAllowSelectAll(String allowSelectAll) {
		this.allowSelectAll = allowSelectAll;
	}
	
	
	public String getMoveUpLabel() {
		return moveUpLabel;
	}
	/**
     * Text to display on the move up button
	 * @a2.tagattribute required="false" type="String" default="^"
	 */
	public void setMoveUpLabel(String moveUpLabel) {
		this.moveUpLabel = moveUpLabel;
	}
	
	
	
	public String getMoveDownLabel() {
		return moveDownLabel;
	}
	/**
     * Text to display on the move down button
	 * @a2.tagattribute required="false" type="String" default="v"
	 */
	public void setMoveDownLabel(String moveDownLabel) {
		this.moveDownLabel = moveDownLabel;
	}
	

	
	public String getSelectAllLabel() {
		return selectAllLabel;
	}
	/**
     * Text to display on the select all button
	 * @a2.tagattribute required="false" type="String" default="*"
	 */
	public void setSelectAllLabel(String selectAllLabel) {
		this.selectAllLabel = selectAllLabel;
	}
}
