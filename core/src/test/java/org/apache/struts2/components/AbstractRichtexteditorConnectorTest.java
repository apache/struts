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
package org.apache.struts2.components;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.OgnlValueStack;

import junit.framework.TestCase;

/**
 * test case for AbstractRichtexteditorConnector
 * 
 */
public class AbstractRichtexteditorConnectorTest extends TestCase {

	private MockRichtexteditorConnector action;
	
	public void testBrowseGetFolders() throws Exception {
		action.setCommand("GetFolders");
		action.setType("Image"); // Image, Link or Flash (according to FCK specs)
		action.setCurrentFolder("/");
		action.setServerPath("/myserver/path/");
		action._Folder = new AbstractRichtexteditorConnector.Folder[] {
			new AbstractRichtexteditorConnector.Folder("folder1"),
			new AbstractRichtexteditorConnector.Folder("folder2")
		};
		
		String resultName = action.browse();
		
		assertEquals(ActionContext.getContext().get("__richtexteditorCommand"), "GetFolders");
		assertEquals(ActionContext.getContext().get("__richtexteditorType"), "Image");
		assertEquals(ActionContext.getContext().get("__richtexteditorFolderPath"), "/");
		assertEquals(ActionContext.getContext().get("__richtexteditorServerPath"), "/calculated/myserver/path/");
		assertEquals(ActionContext.getContext().get("__richtexteditorGetFolders"), action._Folder);
		assertTrue(action.isCalculateServerPathCalled);
		assertTrue(action.isGetFoldersCalled);
		assertFalse(action.isCreateFolderCalled);
		assertFalse(action.isFileUploadCalled);
		assertFalse(action.isGetFoldersAndFilesCalled);
		assertFalse(action.isUnknownCommandCalled);
		assertEquals(resultName, AbstractRichtexteditorConnector.GET_FOLDERS);
	}
	
	public void testBrowseGetFoldersAndFiles() throws Exception {
		action.setCommand("GetFoldersAndFiles");
		action.setType("Link");
		action.setCurrentFolder("/currFolder");
		action.setServerPath("/myserver/path2/");
		action._FoldersAndFiles = new AbstractRichtexteditorConnector.FoldersAndFiles(
					new AbstractRichtexteditorConnector.Folder[] {
						new AbstractRichtexteditorConnector.Folder("folder1"), 
						new AbstractRichtexteditorConnector.Folder("folder2")
					}, 
					new AbstractRichtexteditorConnector.File[] {
						new AbstractRichtexteditorConnector.File("file1", 10l),
						new AbstractRichtexteditorConnector.File("file2", 20l)
					}
				);
		
		String resultName = action.browse();
		
		assertEquals(ActionContext.getContext().get("__richtexteditorCommand"), "GetFoldersAndFiles");
		assertEquals(ActionContext.getContext().get("__richtexteditorType"), "Link");
		assertEquals(ActionContext.getContext().get("__richtexteditorFolderPath"), "/currFolder");
		assertEquals(ActionContext.getContext().get("__richtexteditorServerPath"), "/calculated/myserver/path2/");
		assertEquals(ActionContext.getContext().get("__richtexteditorGetFoldersAndFiles"), action._FoldersAndFiles);
		assertTrue(action.isCalculateServerPathCalled);
		assertFalse(action.isGetFoldersCalled);
		assertFalse(action.isCreateFolderCalled);
		assertFalse(action.isFileUploadCalled);
		assertTrue(action.isGetFoldersAndFilesCalled);
		assertFalse(action.isUnknownCommandCalled);
		assertEquals(resultName, AbstractRichtexteditorConnector.GET_FOLDERS_AND_FILES);
	}
	
	public void testBrowseCreateFolder() throws Exception {
		action.setCommand("CreateFolder");
		action.setType("Flash");
		action.setCurrentFolder("/currFolder1");
		action.setServerPath("/myserver/path3/");
		action._CreateFolderResult = AbstractRichtexteditorConnector.CreateFolderResult.NO_ERRORS;
		
		String resultName = action.browse();
		
		assertEquals(ActionContext.getContext().get("__richtexteditorCommand"), "CreateFolder");
		assertEquals(ActionContext.getContext().get("__richtexteditorType"), "Flash");
		assertEquals(ActionContext.getContext().get("__richtexteditorFolderPath"), "/currFolder1");
		assertEquals(ActionContext.getContext().get("__richtexteditorServerPath"), "/calculated/myserver/path3/");
		assertEquals(ActionContext.getContext().get("__richtexteditorCreateFolder"), action._CreateFolderResult);
		assertTrue(action.isCalculateServerPathCalled);
		assertFalse(action.isGetFoldersCalled);
		assertTrue(action.isCreateFolderCalled);
		assertFalse(action.isFileUploadCalled);
		assertFalse(action.isGetFoldersAndFilesCalled);
		assertFalse(action.isUnknownCommandCalled);
		assertEquals(resultName, AbstractRichtexteditorConnector.CREATE_FOLDER);
	}
	
	public void testBrowseFileUpload() throws Exception {
		action.setCommand("FileUpload");
		action._FileUploadResult = AbstractRichtexteditorConnector.FileUploadResult.uploadComplete();
		
		String resultName = action.browse();
		
		assertEquals(ActionContext.getContext().get("__richtexteditorCommand"), "FileUpload");
		assertEquals(ActionContext.getContext().get("__richtexteditorFileUpload"), action._FileUploadResult);
		assertFalse(action.isCalculateServerPathCalled);
		assertFalse(action.isGetFoldersCalled);
		assertFalse(action.isCreateFolderCalled);
		assertTrue(action.isFileUploadCalled);
		assertFalse(action.isGetFoldersAndFilesCalled);
		assertFalse(action.isUnknownCommandCalled);
		assertEquals(resultName, AbstractRichtexteditorConnector.FILE_UPLOAD);
	}
	
	public void testFileUpload() throws Exception {
		action._FileUploadResult = AbstractRichtexteditorConnector.FileUploadResult.uploadCompleteWithFilenamChanged("newFile.txt");
		
		String resultName = action.upload();
		
		assertEquals(ActionContext.getContext().get("__richtexteditorFileUpload"), action._FileUploadResult);
		assertFalse(action.isCalculateServerPathCalled);
		assertFalse(action.isGetFoldersCalled);
		assertFalse(action.isCreateFolderCalled);
		assertTrue(action.isFileUploadCalled);
		assertFalse(action.isGetFoldersAndFilesCalled);
		assertFalse(action.isUnknownCommandCalled);
		assertEquals(resultName, AbstractRichtexteditorConnector.FILE_UPLOAD);
	}
	
	
	public void testUnknown() throws Exception {
		action.setCommand("noSuchCommand");
		
		String resultName = action.browse();
		
		assertFalse(action.isCalculateServerPathCalled);
		assertFalse(action.isGetFoldersCalled);
		assertFalse(action.isCreateFolderCalled);
		assertFalse(action.isFileUploadCalled);
		assertFalse(action.isGetFoldersAndFilesCalled);
		assertTrue(action.isUnknownCommandCalled);
		assertEquals(resultName, Action.ERROR);
	}
	
	
	
	protected void setUp() throws Exception {
		super.setUp();
		ActionContext.getContext().setValueStack(new OgnlValueStack());
		ActionContext.getContext().getContextMap().clear();
		action = new MockRichtexteditorConnector();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		action = null;
	}
}
