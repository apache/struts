/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import com.opensymphony.webwork.components.AbstractRichtexteditorConnector;

/**
 * Test case for RichtexteditorGetFoldersResultTest.
 * 
 * @author tm_jee
 * @version $Date: 2006/02/17 15:17:38 $ $Id: RichtexteditorGetFoldersResultTest.java,v 1.1 2006/02/17 15:17:38 tmjee Exp $
 */
public class RichtexteditorGetFoldersResultTest extends AbstractRichtexteditorTest {

	public void testExecuteResult() throws Exception {
		invocation.getStack().getContext().put("__richtexteditorGetFolders", 
				new AbstractRichtexteditorConnector.Folder[] {
					new AbstractRichtexteditorConnector.Folder("folder1"),
					new AbstractRichtexteditorConnector.Folder("folder2"),
					new AbstractRichtexteditorConnector.Folder("folder3")
		});
		invocation.getStack().getContext().put("__richtexteditorCommand", "GetFolders");
		invocation.getStack().getContext().put("__richtexteditorType", "Image");
		invocation.getStack().getContext().put("__richtexteditorFolderPath", "/path/");
		invocation.getStack().getContext().put("__richtexteditorServerPath", "/server/path/");
		
		RichtexteditorGetFoldersResult result = new RichtexteditorGetFoldersResult();
		result.execute(invocation);
		
		verify(RichtexteditorGetFoldersResultTest.class.getResourceAsStream("RichtexteditorGetFoldersResultTest1.txt"));
	}
}
