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

import org.apache.struts.action2.components.AbstractRichtexteditorConnector;

/**
 * 
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
