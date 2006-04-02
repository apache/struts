/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.jsp.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.struts.webwork.views.jsp.AbstractUITagTest;
import org.apache.struts.webwork.views.jsp.ui.ActionErrorTag;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

/**
 * ActionErrorTag test case.
 * 
 * @author tm_jee
 * @version $Date: 2005/12/22 15:05:26 $ $Id: ActionErrorTagTest.java,v 1.1 2005/12/22 15:05:26 tmjee Exp $
 */
public class ActionErrorTagTest extends AbstractUITagTest {
	
	boolean shouldActionHaveError = false;
	
	public void testNoActionErrors() throws Exception {
		ActionErrorTag tag = new ActionErrorTag();
		((InternalActionSupport)action).setHasActionErrors(false);
		tag.setPageContext(pageContext);
		tag.doStartTag();
		tag.doEndTag();
		
		//assertEquals("", writer.toString());
		verify(ActionErrorTagTest.class.getResource("actionerror-1.txt"));
	}
	
	public void testHaveActionErrors() throws Exception {
		
		ActionErrorTag tag = new ActionErrorTag();
		((InternalActionSupport)action).setHasActionErrors(true);
		tag.setPageContext(pageContext);
		tag.doStartTag();
		tag.doEndTag();

		verify(ActionErrorTagTest.class.getResource("actionerror-2.txt"));
	}
	
	
	public Action getAction() {
		return new InternalActionSupport();
	}
	
	
	public class InternalActionSupport extends ActionSupport {
		
		private static final long serialVersionUID = -4777466640658557661L;
		
		private boolean yesActionErrors;
		
		public void setHasActionErrors(boolean aYesActionErrors) {
			yesActionErrors = aYesActionErrors;
		}
		
		public boolean hasActionErrors() {
			return yesActionErrors;
		}
		
		public Collection getActionErrors() {
			if (yesActionErrors) {
				List errors = new ArrayList();
				errors.add("action error number 1");
				errors.add("action error number 2");
				errors.add("action error number 3");
				return errors;
			}
			else {
				return Collections.EMPTY_LIST;
			}
		}
	}
}
