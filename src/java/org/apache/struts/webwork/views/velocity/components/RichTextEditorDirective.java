/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.velocity.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.webwork.components.Component;
import org.apache.struts.webwork.components.RichTextEditor;
import com.opensymphony.xwork.util.OgnlValueStack;

/**
 * @author tm_jee
 * @version $Date: 2006/02/18 05:29:27 $ $Id: RichTextEditorDirective.java,v 1.1 2006/02/18 05:29:27 tmjee Exp $
 * @see RichTextEditor
 */
public class RichTextEditorDirective extends AbstractDirective {
	
	public String getBeanName() {
		return "richtexteditor";
	}

	protected Component getBean(OgnlValueStack stack, HttpServletRequest req, HttpServletResponse res) {
		return new RichTextEditor(stack, req, res);
	}
}
