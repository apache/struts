/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.components;

import org.apache.struts.action2.components.AbstractRichtexteditorConnector.CreateFolderResult;
import org.apache.struts.action2.components.AbstractRichtexteditorConnector.FileUploadResult;
import org.apache.struts.action2.components.AbstractRichtexteditorConnector.Folder;
import org.apache.struts.action2.components.AbstractRichtexteditorConnector.FoldersAndFiles;

/**
 * 
 * @author tm_jee
 * @version $Date: 2006/02/17 15:17:36 $ $Id: MockRichtexteditorConnector.java,v 1.1 2006/02/17 15:17:36 tmjee Exp $
 */
public class MockRichtexteditorConnector extends AbstractRichtexteditorConnector {

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
