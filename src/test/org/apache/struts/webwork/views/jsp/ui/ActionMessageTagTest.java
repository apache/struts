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
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

/**
 * ActionMessageTag test case.
 * 
 * @author tm_jee
 * @version $Date: 2005/12/22 16:24:22 $ $Id: ActionMessageTagTest.java,v 1.1 2005/12/22 16:24:22 tmjee Exp $
 */
public class ActionMessageTagTest extends AbstractUITagTest {

	public void testNoActionMessages() throws Exception {
		
		ActionMessageTag tag = new ActionMessageTag();
		((InternalActionSupport)action).setHasActionMessage(false);
		tag.setPageContext(pageContext);
		tag.doStartTag();
		tag.doEndTag();
		
		verify(ActionMessageTagTest.class.getResource("actionmessage-1.txt"));
	}
	
	public void testYesActionMessages() throws Exception {
		
		ActionMessageTag tag = new ActionMessageTag();
		((InternalActionSupport)action).setHasActionMessage(true);
		tag.setPageContext(pageContext);
		tag.doStartTag();
		tag.doEndTag();
		
		verify(ActionMessageTagTest.class.getResource("actionmessage-2.txt"));
	}
	
	public Action getAction() {
		return new InternalActionSupport();
	}

	/**
	 * Internal ActionSupport class for testing, can be in state with
	 * or without action messages.
	 * 
	 * @author tm_jee
	 * @version $Date: 2005/12/22 16:24:22 $ $Id: ActionMessageTagTest.java,v 1.1 2005/12/22 16:24:22 tmjee Exp $
	 */
	public class InternalActionSupport extends ActionSupport {
		
		private static final long serialVersionUID = -3230043189352453629L;
		
		private boolean canHaveActionMessage;
		
		public void setHasActionMessage(boolean canHaveActionMessage) {
			this.canHaveActionMessage = canHaveActionMessage;
		}
		
		public Collection getActionMessages() {
			if (canHaveActionMessage) {
				List messages = new ArrayList();
				messages.add("action message number 1");
				messages.add("action message number 2");
				messages.add("action message number 3");
				return messages;
			}
			else {
				return Collections.EMPTY_LIST;
			}
		}
		
		public boolean hasActionMessages() {
			return canHaveActionMessage;
		}
	}
}
