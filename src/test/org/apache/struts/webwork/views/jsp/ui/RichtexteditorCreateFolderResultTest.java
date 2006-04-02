/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.webwork.views.jsp.ui;

import org.apache.struts.webwork.components.AbstractRichtexteditorConnector;


/**
 * 
 * @author tm_jee
 * @version $Date: 2006/02/17 15:17:37 $ $Id: RichtexteditorCreateFolderResultTest.java,v 1.1 2006/02/17 15:17:37 tmjee Exp $
 */
public class RichtexteditorCreateFolderResultTest extends AbstractRichtexteditorTest {

	public void testExecuteResult() throws Exception {
		invocation.getStack().getContext().put("__richtexteditorCreateFolder", 
				AbstractRichtexteditorConnector.CreateFolderResult.noErrors());
		invocation.getStack().getContext().put("__richtexteditorCommand", "CreateFolder");
		invocation.getStack().getContext().put("__richtexteditorType", "Image");
		invocation.getStack().getContext().put("__richtexteditorFolderPath", "/folder/path/");
		invocation.getStack().getContext().put("__richtexteditorServerPath", "/server/path/");
		
		RichtexteditorCreateFolderResult result = new RichtexteditorCreateFolderResult();
		result.execute(invocation);
		
		verify(RichtexteditorCreateFolderResultTest.class.getResourceAsStream("RichtexteditorCreateFolderResultTest1.txt"));
	}
}
