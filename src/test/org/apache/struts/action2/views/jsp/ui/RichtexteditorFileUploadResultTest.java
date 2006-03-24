/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp.ui;

import org.apache.struts.action2.components.AbstractRichtexteditorConnector;

/**
 * 
 * @author tm_jee
 * @version $Date: 2006/02/17 15:17:38 $ $Id: RichtexteditorFileUploadResultTest.java,v 1.1 2006/02/17 15:17:38 tmjee Exp $
 */
public class RichtexteditorFileUploadResultTest extends AbstractRichtexteditorTest {

	public void testExecuteResultWithFileChange() throws Exception {
		invocation.getStack().getContext().put("__richtexteditorFileUpload", 
				AbstractRichtexteditorConnector.FileUploadResult.uploadCompleteWithFilenamChanged("newFile.txt"));
		
		RichtexteditorFileUploadResult result = new RichtexteditorFileUploadResult();
		result.execute(invocation);
		
		verify(RichtexteditorFileUploadResultTest.class.getResourceAsStream("RichtexteditorFileUploadResultTest1.txt"));
	}
	
	public void testExecuteResultWithError() throws Exception {
		invocation.getStack().getContext().put("__richtexteditorFileUpload", 
				AbstractRichtexteditorConnector.FileUploadResult.invalidFile());
		
		RichtexteditorFileUploadResult result = new RichtexteditorFileUploadResult();
		result.execute(invocation);
		
		verify(RichtexteditorFileUploadResultTest.class.getResourceAsStream("RichtexteditorFileUploadResultTest2.txt"));
	}
	
	public void testExecuteResultWithSuccess() throws Exception {
		invocation.getStack().getContext().put("__richtexteditorFileUpload", 
				AbstractRichtexteditorConnector.FileUploadResult.uploadComplete());
		
		RichtexteditorFileUploadResult result = new RichtexteditorFileUploadResult();
		result.execute(invocation);
		
		verify(RichtexteditorFileUploadResultTest.class.getResourceAsStream("RichtexteditorFileUploadResultTest3.txt"));
	}
}
