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
package org.apache.struts.action2.components;

import org.apache.struts.action2.components.AbstractRichtexteditorConnector.CreateFolderResult;
import org.apache.struts.action2.components.AbstractRichtexteditorConnector.FileUploadResult;
import org.apache.struts.action2.components.AbstractRichtexteditorConnector.Folder;
import org.apache.struts.action2.components.AbstractRichtexteditorConnector.FoldersAndFiles;

/**
 * 
 */
public class MockRichtexteditorConnector extends AbstractRichtexteditorConnector {

	private static final long serialVersionUID = -1711041371619240994L;
	
	public boolean isCalculateServerPathCalled = false;
	public boolean isGetFoldersCalled = false;
	public boolean isGetFoldersAndFilesCalled = false;
	public boolean isCreateFolderCalled = false;
	public boolean isFileUploadCalled = false;
	public boolean isUnknownCommandCalled = false;
	
	public Folder[] _Folder = null;
	public FoldersAndFiles _FoldersAndFiles = null;
	public CreateFolderResult _CreateFolderResult = null;
	public FileUploadResult _FileUploadResult = null;
	
	
	protected String calculateServerPath(String serverPath, String folderPath, String type) throws Exception {
		isCalculateServerPathCalled = true;
		return "/calculated"+serverPath;
	}

	protected Folder[] getFolders(String virtualFolderPath, String type) throws Exception {
		isGetFoldersCalled = true;
		return _Folder;
	}

	protected FoldersAndFiles getFoldersAndFiles(String virtualFolderPath, String type) throws Exception {
		isGetFoldersAndFilesCalled = true;
		return _FoldersAndFiles;
	}

	protected CreateFolderResult createFolder(String virtualFolderPath, String type, String newFolderName) throws Exception {
		isCreateFolderCalled = true;
		return _CreateFolderResult;
	}

	protected FileUploadResult fileUpload(String virtualFolderPath, String type, String filename, String contentType, java.io.File newFile) throws Exception {
		isFileUploadCalled = true;
		return _FileUploadResult;
	}

	protected void unknownCommand(String command, String virtualFolderPath, String type, String filename, String contentType, java.io.File newFile) throws Exception {
		isUnknownCommandCalled = true;
	}
	
	public void reset() { 
		isCalculateServerPathCalled = false;
		isCreateFolderCalled = false;
		isFileUploadCalled = false;
		isGetFoldersAndFilesCalled = false;
		isGetFoldersCalled = false;
		isUnknownCommandCalled = false;
	}

}
