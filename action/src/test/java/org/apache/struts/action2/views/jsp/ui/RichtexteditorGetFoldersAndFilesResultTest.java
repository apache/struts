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
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class RichtexteditorGetFoldersAndFilesResultTest extends AbstractRichtexteditorTest {

	public void testExecuteResult() throws Exception {
		invocation.getStack().getContext().put("__richtexteditorGetFoldersAndFiles", 
				new AbstractRichtexteditorConnector.FoldersAndFiles(
						new AbstractRichtexteditorConnector.Folder[] {
								new AbstractRichtexteditorConnector.Folder("folder1"),
								new AbstractRichtexteditorConnector.Folder("folder2"), 
								new AbstractRichtexteditorConnector.Folder("folder3")
						}, 
						new AbstractRichtexteditorConnector.File[] {
								new AbstractRichtexteditorConnector.File("file1.txt", 10),
								new AbstractRichtexteditorConnector.File("file2.txt", 11),
								new AbstractRichtexteditorConnector.File("file3.txt", 12)
						}));
		
		invocation.getStack().getContext().put("__richtexteditorCommand", "GetFoldersAndFiles");
		invocation.getStack().getContext().put("__richtexteditorType", "Image");
		invocation.getStack().getContext().put("__richtexteditorFolderPath", "/path/");
		invocation.getStack().getContext().put("__richtexteditorServerPath", "/server/path/");
		
		RichtexteditorGetFoldersAndFilesResult result = new RichtexteditorGetFoldersAndFilesResult();
		
		result.execute(invocation);
		
		verify(RichtexteditorGetFoldersAndFilesResultTest.class.getResourceAsStream("RichtexteditorGetFoldersAndFilesResultTest1.txt"));
	}
}
