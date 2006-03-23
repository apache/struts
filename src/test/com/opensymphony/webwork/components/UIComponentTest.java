/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.components;

import java.util.Iterator;
import java.util.Stack;

import javax.servlet.jsp.tagext.TagSupport;

import com.opensymphony.webwork.TestConfigurationProvider;
import com.opensymphony.webwork.views.jsp.AbstractUITagTest;
import com.opensymphony.webwork.views.jsp.ui.ActionErrorTag;
import com.opensymphony.webwork.views.jsp.ui.ActionMessageTag;
import com.opensymphony.webwork.views.jsp.ui.AnchorTag;
import com.opensymphony.webwork.views.jsp.ui.CheckboxTag;
import com.opensymphony.webwork.views.jsp.ui.ComboBoxTag;
import com.opensymphony.webwork.views.jsp.ui.DivTag;
import com.opensymphony.webwork.views.jsp.ui.DoubleSelectTag;
import com.opensymphony.webwork.views.jsp.ui.FieldErrorTag;
import com.opensymphony.webwork.views.jsp.ui.FileTag;
import com.opensymphony.webwork.views.jsp.ui.FormTag;
import com.opensymphony.webwork.views.jsp.ui.HiddenTag;
import com.opensymphony.webwork.views.jsp.ui.LabelTag;
import com.opensymphony.webwork.views.jsp.ui.OptionTransferSelectTag;
import com.opensymphony.webwork.views.jsp.ui.PasswordTag;
import com.opensymphony.webwork.views.jsp.ui.RadioTag;
import com.opensymphony.webwork.views.jsp.ui.SelectTag;
import com.opensymphony.webwork.views.jsp.ui.SubmitTag;
import com.opensymphony.webwork.views.jsp.ui.TextFieldTag;
import com.opensymphony.webwork.views.jsp.ui.TextareaTag;
import com.opensymphony.webwork.views.jsp.ui.TokenTag;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.config.ConfigurationManager;


/**
 * Test case common for all UI component in general.
 * 
 * @author tm_jee
 * @version $Date: 2006/01/26 08:42:33 $ $Id: UIComponentTest.java,v 1.1 2006/01/26 08:42:33 tmjee Exp $
 */
public class UIComponentTest extends AbstractUITagTest {
	
	// actionError
	public void testActionErrorComponentDisposeItselfFromComponentStack() throws Exception {
		ActionMessageTag t = new ActionMessageTag();
		t.setPageContext(pageContext);
	
		try {
			t.doStartTag();
			ActionErrorTag tag = new ActionErrorTag();
			tag.setPageContext(pageContext);
			tag.doStartTag();
			assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
			tag.doEndTag();
			assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
			
			t.doEndTag();
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
		
	}
	
	// actionMessage
	public void testActionMessageDisposeItselfFromComponentStack() throws Exception {
		ActionErrorTag t = new ActionErrorTag();
		t.setPageContext(pageContext);
		
		try {
			t.doStartTag();
			ActionMessageTag tag = new ActionMessageTag();
			tag.setPageContext(pageContext);
			tag.doStartTag();
			assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
			tag.doEndTag();
			assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
			
			t.doEndTag();
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	//	 Anchor 
	public void testAnchorComponentDisposeItselfFromComponentStack() throws Exception {
		
		TextFieldTag t = new TextFieldTag();
		t.setPageContext(pageContext);
		t.setName("textFieldName");
		
		AnchorTag tag = new AnchorTag();
		tag.setPageContext(pageContext);
		
		try {
			t.doStartTag();
			tag.doStartTag();
			assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
			tag.doEndTag();
			assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
		
			t.doEndTag();
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	// checkbox
	public void testCheckboxDisposeItselfFromComponentStack() throws Exception {
		ActionErrorTag t = new ActionErrorTag();
		t.setPageContext(pageContext);
		
		try {
			t.doStartTag();
			CheckboxTag tag = new CheckboxTag();
			tag.setName("name");
			tag.setLabel("label");
			tag.setPageContext(pageContext);
			tag.doStartTag();
			assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
			tag.doEndTag();
			assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
			
			t.doEndTag();
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	// combobox
	public void testComboboxDisposeItselfFromComponentStack() throws Exception {
		ActionErrorTag t = new ActionErrorTag();
		t.setPageContext(pageContext);
		
		try {
			t.doStartTag();
			ComboBoxTag tag = new ComboBoxTag();
			tag.setName("name");
			tag.setLabel("label");
			tag.setPageContext(pageContext);
			tag.doStartTag();
			assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
			tag.doEndTag();
			assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
			
			t.doEndTag();
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	// div
	public void testDivComponentDisposeItselfFromComponentStack() throws Exception {
		ActionErrorTag t = new ActionErrorTag();
		t.setPageContext(pageContext);
		
		try {
			t.doStartTag();
			DivTag tag = new DivTag();
			tag.setPageContext(pageContext);
			tag.doStartTag();
			assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
			tag.doEndTag();
			assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
			
			t.doEndTag();
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	// doubleselect 
	public void testDoubleselectComponentDisposeItselfFromComponentStack() throws Exception {
		ActionErrorTag t = new ActionErrorTag();
		t.setPageContext(pageContext);
		
		try {
			t.doStartTag();
			DoubleSelectTag tag = new DoubleSelectTag();
			tag.setName("name");
			tag.setLabel("label");
			tag.setList("#{1:'one',2:'two'}");
			tag.setDoubleName("doubleName");
			tag.setDoubleList("1?({'aa','bb'}:{'cc','dd'}");
			tag.setFormName("formName");
			tag.setPageContext(pageContext);
			tag.doStartTag();
			assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
			tag.doEndTag();
			assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
			
			t.doEndTag();
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	// fieldError
	public void testFielderrorComponentDisposeItselfFromComponentStack() throws Exception {
		ActionErrorTag t = new ActionErrorTag();
		t.setPageContext(pageContext);
		
		try {
			t.doStartTag();
			FieldErrorTag tag = new FieldErrorTag();
			tag.setPageContext(pageContext);
			tag.doStartTag();
			assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
			tag.doEndTag();
			assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
			
			t.doEndTag();
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	// file
	public void testFileDisposeItselfFromComponentStack() throws Exception {
		ActionErrorTag t = new ActionErrorTag();
		t.setPageContext(pageContext);
		
		try {
			t.doStartTag();
			FileTag tag = new FileTag();
			tag.setName("name");
			tag.setLabel("label");
			tag.setPageContext(pageContext);
			tag.doStartTag();
			assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
			tag.doEndTag();
			assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
			
			t.doEndTag();
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	// form
	public void testFormComponentDisposeItselfFromComponentStack() throws Exception {
		ConfigurationManager.clearConfigurationProviders();
        ConfigurationManager.addConfigurationProvider(new TestConfigurationProvider());
        ActionContext.getContext().setValueStack(stack);
        
        request.setupGetServletPath("/testAction");
        
		ActionErrorTag t = new ActionErrorTag();
		t.setPageContext(pageContext);
		
		try {
			t.doStartTag();
			FormTag tag = new FormTag();
			tag.setName("myForm");
	        tag.setMethod("POST");
	        tag.setAction("myAction");
	        tag.setEnctype("myEncType");
	        tag.setTitle("mytitle");
			tag.setPageContext(pageContext);
			tag.doStartTag();
			assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
			tag.doEndTag();
			assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
			
			t.doEndTag();
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	// hidden
	public void testHiddenComponentDisposeItselfFromComponentStack() throws Exception {
		ActionErrorTag t = new ActionErrorTag();
		t.setPageContext(pageContext);
		
		try {
			t.doStartTag();
			HiddenTag tag = new HiddenTag();
			tag.setPageContext(pageContext);
			tag.doStartTag();
			assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
			tag.doEndTag();
			assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
			
			t.doEndTag();
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	// label
	public void testLabelComponentDisposeItselfFromComponentStack() throws Exception {
		ActionErrorTag t = new ActionErrorTag();
		t.setPageContext(pageContext);
		
		try {
			t.doStartTag();
			LabelTag tag = new LabelTag();
			tag.setName("name");
			tag.setLabel("label");
			tag.setPageContext(pageContext);
			tag.doStartTag();
			assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
			tag.doEndTag();
			assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
			
			t.doEndTag();
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	// optiontransferselect
	public void testOptiontransferselectComponentDisposeItselfFromComponentStack() throws Exception {
		ActionErrorTag t = new ActionErrorTag();
		t.setPageContext(pageContext);
		
		try {
			t.doStartTag();
			OptionTransferSelectTag tag = new OptionTransferSelectTag();
			tag.setId("myId");
			tag.setDoubleId("myDoubleId");
			tag.setName("name");
			tag.setLabel("label");
			tag.setList("{}");
			tag.setDoubleList("{}");
			tag.setPageContext(pageContext);
			tag.doStartTag();
			assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
			tag.doEndTag();
			assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
			
			t.doEndTag();
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	// password
	public void testPasswordComponentDisposeItselfFromComponentStack() throws Exception {
		ActionErrorTag t = new ActionErrorTag();
		t.setPageContext(pageContext);
		
		try {
			t.doStartTag();
			PasswordTag tag = new PasswordTag();
			tag.setName("name");
			tag.setLabel("label");
			tag.setPageContext(pageContext);
			tag.doStartTag();
			assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
			tag.doEndTag();
			assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
			
			t.doEndTag();
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	// radio
	public void testRadioComponentDisposeItselfFromComponentStack() throws Exception {
		ActionErrorTag t = new ActionErrorTag();
		t.setPageContext(pageContext);
		
		try {
			t.doStartTag();
			RadioTag tag = new RadioTag();
			tag.setList("{}");
			tag.setName("name");
			tag.setLabel("label");
			tag.setPageContext(pageContext);
			tag.doStartTag();
			assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
			tag.doEndTag();
			assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
			
			t.doEndTag();
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	// select
	public void testSelectComponentDisposeItselfFromComponentStack() throws Exception {
		ActionErrorTag t = new ActionErrorTag();
		t.setPageContext(pageContext);
		
		try {
			t.doStartTag();
			SelectTag tag = new SelectTag();
			tag.setList("{}");
			tag.setName("name");
			tag.setLabel("label");
			tag.setPageContext(pageContext);
			tag.doStartTag();
			assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
			tag.doEndTag();
			
			assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
			
			t.doEndTag();
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	// submit
	public void testSubmitDisposeItselfFromComponentStack() throws Exception {
		ActionErrorTag t = new ActionErrorTag();
		t.setPageContext(pageContext);
		
		try {
			t.doStartTag();
			SubmitTag tag = new SubmitTag();
			tag.setName("name");
			tag.setPageContext(pageContext);
			tag.doStartTag();
			assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
			tag.doEndTag();
			assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
			
			t.doEndTag();
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	// textarea
	public void testTextareaComponentDisposeItselfFromComponentStack() throws Exception {
		ActionErrorTag t = new ActionErrorTag();
		t.setPageContext(pageContext);
		
		try {
			t.doStartTag();
			TextareaTag tag = new TextareaTag();
			tag.setName("name");
			tag.setLabel("label");
			tag.setPageContext(pageContext);
			tag.doStartTag();
			assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
			tag.doEndTag();
			assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
			
			t.doEndTag();
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	// textfield
	public void testTextfieldComponentDisposeItselfFromComponentStack() throws Exception {
		ActionErrorTag t = new ActionErrorTag();
		t.setPageContext(pageContext);
		
		try {
			t.doStartTag();
			TextFieldTag tag = new TextFieldTag();
			tag.setName("name");
			tag.setLabel("label");
			tag.setPageContext(pageContext);
			tag.doStartTag();
			assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
			tag.doEndTag();
			assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
			
			t.doEndTag();
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
	
	// token
	public void testTokenComponentDisposeItselfFromComponentStack() throws Exception {
		ActionErrorTag t = new ActionErrorTag();
		t.setPageContext(pageContext);
		
		try {
			t.doStartTag();
			TokenTag tag = new TokenTag();
			tag.setPageContext(pageContext);
			tag.doStartTag();
			assertEquals(tag.getComponent().getComponentStack().peek(), tag.getComponent());
			tag.doEndTag();
			assertEquals(t.getComponent().getComponentStack().peek(), t.getComponent());
			
			t.doEndTag();
		}
		catch(Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
}
