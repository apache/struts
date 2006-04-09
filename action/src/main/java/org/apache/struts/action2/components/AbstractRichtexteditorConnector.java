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

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.struts.action2.interceptor.ServletRequestAware;
import org.apache.struts.action2.interceptor.ServletResponseAware;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionSupport;

/**
 * <!-- START SNIPPET: javadoc -->
 * 
 * An abstract class to be extended in order for the Rich text editor to perform server-side
 * browsing and uploading. 
 * 
 * <!-- END SNIPPET: javadoc -->
 * 
 * <pre>
 * <!-- START SNIPPET: example -->
 * 
 *   &lt;package name="richtexteditor-browse" extends="struts-default" namespace="/struts/richtexteditor/editor/filemanager/browser/default/connectors/jsp"&gt;
 *   	&lt;action name="connector" class="org.apache.struts.action2.components.DefaultRichtexteditorConnector" method="browse">
 *   		&lt;result name="getFolders" type="richtexteditorGetFolders" /&gt;
 *   		&lt;result name="getFoldersAndFiles" type="richtexteditorGetFoldersAndFiles" /&gt;
 *   		&lt;result name="createFolder" type="richtexteditorCreateFolder" /&gt;
 *   		&lt;result name="fileUpload" type="richtexteditorFileUpload" /&gt;
 *   	&lt;/action&gt;
 *   &lt;/package&gt;
 * 
 *   &lt;package name="richtexteditor-upload" extends="struts-default" namespace="/struts/richtexteditor/editor/filemanager/upload"&gt;
 *		&lt;action name="uploader" class="org.apache.struts.action2.components.DefaultRichtexteditorConnector" method="upload"&gt;
 *			&lt;result name="richtexteditorFileUpload" /&gt;
 *		&lt;/action&gt;    
 *   &lt;/package&gt;
 * 
 * <!-- END SNIPPET: example -->
 * </pre>
 * 
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 * @see org.apache.struts.action2.components.DefaultRichtexteditorConnector
 */
public abstract class AbstractRichtexteditorConnector extends ActionSupport implements ServletRequestAware, ServletResponseAware {
	
	private static final Log _log = LogFactory.getLog(AbstractRichtexteditorConnector.class);

	public static String GET_FOLDERS = "getFolders";
	public static String GET_FOLDERS_AND_FILES = "getFoldersAndFiles";
	public static String CREATE_FOLDER = "createFolder";
	public static String FILE_UPLOAD = "fileUpload";
	
	protected HttpServletRequest _request;
	protected HttpServletResponse _response;
	
	protected java.io.File _newFile;
	protected String _newFileFileName;
	protected String _newFileContentType;
	
	protected String _type;
	protected String _command;
	protected String _currentFolder;
	protected String _serverPath = "/struts/richtexteditor/data/";
	protected String _newFolderName;
	
	
	/**
	 * <!-- START SNIPPET: browse -->
	 * 
	 * The method that does the functionality when the richtexteditor 'browse' command is 
	 * issued. 
	 * 
	 * <p/>
	 * 
	 * Following are the result name that gets returned depending on the actual 'browse' 
	 * command.
	 * 
	 * <table border=1>
	 *    <tr>
	 *    	  <td>Browse Command</td>
	 *    	  <td>Result Name</td>
	 *    </tr>
	 *    <tr>
	 *        <td>GetFolders</td>
	 *        <td>getFolders</td>
	 *    </tr>
	 *    <tr>
	 *        <td>GetFoldersAndFiles</td>
	 *        <td>getFoldersAndFiles</td>
	 *    </tr>
	 *    <tr>
	 *        <td>CreateFolder</td>
	 *        <td>createFolder</td>
	 *    </tr>
	 *    <tr>
	 *        <td>FileUpload</td>
	 *        <td>fileUpload</td>
	 *    </tr>
	 * </table>
	 * 
	 * <!-- END SNIPPET: browse -->
	 * 
	 * @return result name
	 * @throws Exception
	 */
	public String browse() throws Exception {
		
		if ("GetFolders".equals(getCommand())) {
			_log.debug("Command "+getCommand()+" detected \n\t type="+getType()+"\n\t folderPath="+getCurrentFolder());
			
			ActionContext.getContext().put("__richtexteditorCommand", getCommand());
			ActionContext.getContext().put("__richtexteditorType", getType());
			ActionContext.getContext().put("__richtexteditorFolderPath", getCurrentFolder());
			ActionContext.getContext().put("__richtexteditorServerPath", calculateServerPath(getServerPath(), getCurrentFolder(), getType()));
			
			Folder[] folders = getFolders(getCurrentFolder(), getType());
			
			ActionContext.getContext().put("__richtexteditorGetFolders", folders);
			
			return GET_FOLDERS;
		}
		else if ("GetFoldersAndFiles".equals(getCommand())) {
			_log.debug("Command "+getCommand()+" detected \n\t type="+getType()+"\n\t folderPath="+getCurrentFolder());
			
			ActionContext.getContext().put("__richtexteditorCommand", getCommand());
			ActionContext.getContext().put("__richtexteditorType", getType());
			ActionContext.getContext().put("__richtexteditorFolderPath", getCurrentFolder());
			ActionContext.getContext().put("__richtexteditorServerPath", calculateServerPath(getServerPath(), getCurrentFolder(), getType()));
			
			FoldersAndFiles folderAndFiles = getFoldersAndFiles(getCurrentFolder(), getType());
			
			ActionContext.getContext().put("__richtexteditorGetFoldersAndFiles", folderAndFiles);
			
			return GET_FOLDERS_AND_FILES;
		}
		else if ("CreateFolder".equals(getCommand())) {
			_log.debug("Command "+getCommand()+" detected \n\t type="+getType()+"\n\t folderPath="+getCurrentFolder()+"\n\t newFolderName="+getNewFolderName());
			
			ActionContext.getContext().put("__richtexteditorCommand", getCommand());
			ActionContext.getContext().put("__richtexteditorType", getType());
			ActionContext.getContext().put("__richtexteditorFolderPath", getCurrentFolder());
			ActionContext.getContext().put("__richtexteditorServerPath", calculateServerPath(getServerPath(), getCurrentFolder(), getType()));
			
			CreateFolderResult createFolderResult = createFolder(getCurrentFolder(), getType(), getNewFolderName());
			
			ActionContext.getContext().put("__richtexteditorCreateFolder", createFolderResult);
			
			return CREATE_FOLDER;
		}
		else if ("FileUpload".equals(getCommand())) {
			_log.debug("Command "+getCommand()+" detected \n\t type="+getType()+"\n\t folderPath="+getCurrentFolder()+"\n\t newFileFileName="+getNewFileFileName()+"\n\t newFileContentType="+getNewFileContentType()+"\n\t newFile="+getNewFile());
			
			ActionContext.getContext().put("__richtexteditorCommand", getCommand());
			
			FileUploadResult fileUploadResult = fileUpload(getCurrentFolder(), getType(), getNewFileFileName(), getNewFileContentType(), getNewFile());
			
			ActionContext.getContext().put("__richtexteditorFileUpload", fileUploadResult);
			
			return FILE_UPLOAD;
		}
		else {
			_log.debug("Unknown Command "+getCommand()+" detected \n\t type="+getType()+"\n\t folderPath="+getCurrentFolder());
			
			unknownCommand(getCommand(), getCurrentFolder(), getType(), getNewFileFileName(), getNewFileContentType(), getNewFile());
			
			return ERROR;
		}
	}
	
	
	/**
	 * <!-- START SNIPPET: upload -->
	 * 
	 * The method that does the functionality when the richtexteditor 'upload' command is '/struts/richtexteditor/data/'
	 * issued. 
	 * 
	 * <p/>
	 * 
	 * It return a result name of 'fileUpload'.
	 * 
	 * <!-- END SNIPPET: upload -->
	 * 
	 * @return result name
	 * @throws Exception
	 */
	public String upload() throws Exception {
		_log.debug("Upload detected \n\t type="+getType()+"\n\t newFileFileName="+getNewFileFileName()+"\n\t newFileContentType="+getNewFileContentType()+"\n\t newFile="+getNewFile());
		
		FileUploadResult fileUploadResult = fileUpload("/", getType(), getNewFileFileName(), getNewFileContentType(), getNewFile());
		
		ActionContext.getContext().put("__richtexteditorFileUpload", fileUploadResult);
		
		return FILE_UPLOAD;
	}
	
	/**
	 * <!-- START SNIPPET: calculateServerPath -->
	 * 
	 * This method should return the url that could be used to access the server-side 
	 * object. For example, if this methods return '/aaa/bbb/ccc', the say, server-side image
	 * selected is myImage.gif, then the image tag generated might be something like
	 * 
	 * <pre>
	 *   &lt;img src='/aaa/bbb/ccc/myImage.gif' .... /&lt;
	 * </pre>
	 * 
	 * For example, if the folderPath is '/folder1' and the type is 'Image', the 
	 * calculated server path might be '/aaa/bbb/ccc/Image/folder1/' such that if the 
	 * image is 'myImage.gif' the src attribute of the image tag might be 
	 * '/aaa/bbb/ccc/Image/folder1/myImage.gif'.
	 * 
	 * <!-- END SNIPPET: calculateServerPath -->
	 * 
	 * @param serverPath the server path provided through setServerPath (by default it is  
	 * @param folderPath the current folder path requested
	 * @param type the type (Image, Link or Flash)
	 * @return calculated server path
	 * @throws Exception
	 */
	protected abstract String calculateServerPath(String serverPath, String folderPath, String type) throws Exception;
	
	/**
	 * <!-- START SNIPPET: getFolders -->
	 * 
	 * Method that gets called when a 'GetFolders' command is issued by the rich text editor.
	 * This method should search the server-side and return an Folder[] that the server side has.
	 * 
	 * <p/>
	 * 
	 * The folder path queried by the rich text editor is <code>folderPath</code>. While the 
	 * type of could be one of 'Image', 'Link' or 'Flash'.
	 * 
	 * <!-- END SNIPPET: getFolders -->
	 * 
	 * @param virtualFolderPath 
	 * @param type
	 * @return An array of Folders
	 * @throws Exception
	 */
	protected abstract Folder[] getFolders(String virtualFolderPath, String type) throws Exception;
	
	/**
	 * <!-- START SNIPPET: getFoldersAndFiles -->
	 * 
	 * Method that gets called when a 'GetFoldersAndFiles' command is issued by the rich text
	 * editor. This method should typically search the server-side for files and folders under the 
	 * provided virtualFolderPath and return a FoldersAndFiles object.
	 * 
	 * <p/>
	 * 
	 * The folder path queried by the richtexted editor is <code>virtualFolderPath</code>.
	 * While the type could be one of 'Image', 'Link' or 'Flash'.
	 * 
	 * <!-- END SNIPPET: getFoldersAndFiles -->
	 * 
	 * @param virtualFolderPath
	 * @param type
	 * @return FoldersAndFiles
	 * @throws Exception
	 */
	protected abstract FoldersAndFiles getFoldersAndFiles(String virtualFolderPath, String type) throws Exception;
	
	/**
	 * <!-- START SNIPPET: createFolder -->
	 * 
	 * Method that gets called when a 'CreateFolder' command is issued by the rich text 
	 * editor. This method would typically create a folder in the server-side if it is 
	 * allowed to do so and return the result through CreateFolderResult object. CreateFolderResult
	 * contains static methods to return the available results.
	 * 
	 * <p/>
	 * 
	 * The folder path queried by the richtexted editor is <code>virtualFolderPath</code>.
	 * While the type could be one of 'Image', 'Link' or 'Flash'. The new folder name 
	 * to be created is <code>newFolderName</code>.
	 * 
	 * <!-- END SNIPPET: createFolder -->
	 * 
	 * 
	 * @param virtualFolderPath
	 * @param type
	 * @param newFolderName
	 * @return CreateFolderResult
	 * @throws Exception
	 */
	protected abstract CreateFolderResult createFolder(String virtualFolderPath, String type, String newFolderName) throws Exception;
	
	/**
	 * <!-- START SNIPPET: fileUpload -->
	 * 
	 * Method that gets called when a 'FileUpload' command is issued by the rich text
	 * editor. This method would typically handle the file upload and return a 
	 * FileUploadResult object. FileUploadResult contains only static methods that 
	 * could create the available results.
	 * 
	 * <p/>
	 * 
	 * The folder path queried by the richtexted editor is <code>virtualFolderPath</code>.
	 * While the type could be one of 'Image', 'Link' or 'Flash'. The upload file name
	 * is <code>filename</code> while its content type is <code>conetnType</code> and 
	 * its content could be read off the <code>newFile</code> object.
	 * 
	 * <!-- END SNIPPET: fileUpload -->
	 * 
	 * @param virtualFolderPath
	 * @param type
	 * @param filename
	 * @param contentType
	 * @param newFile
	 * @return FileUploadResult
	 * @throws Exception
	 */
	protected abstract FileUploadResult fileUpload(String virtualFolderPath, String type, String filename, String contentType, java.io.File newFile) throws Exception;
	
	/**
	 * <!-- START SNIPPET: unknownCommand -->
	 * 
	 * Methods that get called when an unrecognized command is issued. Typical usage would
	 * be to log an error message.
	 * 
	 * <!-- END SNIPPET: unknownCommand -->
	 * 
	 * @param command
	 * @param virtualFolderPath
	 * @param type
	 * @param filename
	 * @param contentType
	 * @param newFile
	 * @throws Exception
	 */
	protected abstract void unknownCommand(String command, String virtualFolderPath, String type, String filename, String contentType, java.io.File newFile) throws Exception;
	
	
	// === FileUpload Details ======
	public java.io.File getNewFile() { return _newFile; }
	public void setNewFile(java.io.File newFile) { _newFile = newFile; }
	
	public String getNewFileFileName() { return _newFileFileName; }
	public void setNewFileFileName(String newFileFileName) { _newFileFileName = newFileFileName; }
	
	public String getNewFileContentType() { return _newFileContentType; }
	public void setNewFileContentType(String newFileContentType) { _newFileContentType = newFileContentType; }
	
	
	// === Browse / Upload details
	public String getCommand() { return _command; }
	public void setCommand(String command) { _command = command; }
	
	public String getType() { return _type; }
	public void setType(String type) { _type = type; }
	
	public String getCurrentFolder() { return _currentFolder; }
	public void setCurrentFolder(String currentFolder) { _currentFolder = currentFolder; }
	
	public String getNewFolderName() { return _newFolderName; }
	public void setNewFolderName(String newFolderName) { _newFolderName = newFolderName; }
	
	public String getServerPath() { return _serverPath; }
	public void setServerPath(String serverPath) { _serverPath = serverPath; }
	
	
	
	public void setServletRequest(HttpServletRequest request) {
		_request = request;
	}
	
	public void setServletResponse(HttpServletResponse response) {
		_response = response;
	}
	
	
	// ============================================================
	// === inner class ( Folder ) =================================
	// ============================================================
	
	/**
	 * <!-- START SNIPPET: folder -->
	 * 
	 * Class represents a Folder in the server side.
	 * 
	 * <!-- START SNIPPET: folder -->
	 * 
	 * @author tm_jee
	 * @version $Date$ $Id$
	 * @see AbstractRichtexteditorConnector#getFolders(String, String)
	 * @see AbstractRichtexteditorConnector#getFoldersAndFiles(String, String)
	 * @see AbstractRichtexteditorConnector.FoldersAndFiles
	 */
	public static class Folder implements Serializable {
		
		private static final long serialVersionUID = 5525329801043336814L;

		private String foldername;
		public Folder(String foldername) {
			assert(foldername != null);
			this.foldername = foldername;
		}
		public String getFoldername() { return this.foldername; }
	}
	
	
	// ============================================================
	// === inner class ( File ) ===================================
	// ============================================================
	
	/**
	 * <!-- START SNIPPET: File -->
	 * 
	 * Class represens a File in the server side.
	 * 
	 * <!-- END SNIPPET: File -->
	 * 
	 * @author tm_jee
	 * @version $Date$ $Id$
	 * @see AbstractRichtexteditorConnector#getFoldersAndFiles(String, String)
	 * @see AbstractRichtexteditorConnector.FoldersAndFiles
	 */
	public static class File implements Serializable {
		
		private static final long serialVersionUID = -3233042041063715213L;
		
		private String filename;
		private long sizeInKb;
		public File(String filename, long sizeInKb) {
			assert(filename != null);
			this.filename = filename;
			this.sizeInKb = sizeInKb;
		}
		public String getFilename() { return this.filename; }
		public long getSizeInKb() { return this.sizeInKb; }
	}
	
	// ============================================================
	// === inner class (FolderAndFiles) ===========================
	// ============================================================
	
	/**
	 * <!-- START SNIPPET: foldersandfiles -->
	 * 
	 * Represents the files and folders to be returned from the server-side.
	 * 
	 * <!-- END SNIPPET: foldersandfiles -->
	 * 
	 * @author tm_jee
	 * @version $Date$ $Id$
	 * @see AbstractRichtexteditorConnector#getFoldersAndFiles(String, String)
	 * @see AbstractRichtexteditorConnector.File
	 * @see AbstractRichtexteditorConnector.Folder
	 */
	public static class FoldersAndFiles implements Serializable {
		
		private static final long serialVersionUID = 752900354674277779L;
		
		private Folder[] folders;
		private File[] files;
		public FoldersAndFiles(Folder[] folders, File[] files) {
			this.folders = folders;
			this.files = files;
		}
		public Folder[] getFolders() { return this.folders; }
		public File[] getFiles() { return this.files; }
	}
	
	// ===========================================================
	// ==== inner class (CreateFolderResult) =====================
	// ===========================================================
	
	/**
	 * <!-- START SNIPPET: createfolderresult -->
	 * 
	 * Represensts the result of a server-side 'CreateFolder' command call. It 
	 * acts like a static factory containing only static methods to create the 
	 * possible results which are:
	 * 
	 * <ul>
	 * 	<li>no errors</li>
	 *  <li>folder already exists</li>
	 *  <li>invalidFolderName</li>
	 *  <li>no permission</li>
	 *  <li>unknown error</li>
	 * </ul>
	 * 
	 * <!-- END SNIPPET: createfolderresult -->
	 * 
	 * @author tm_jee
	 * @version $Date$ $Id$
	 * @see AbstractRichtexteditorConnector#createFolder(String, String, String)
	 */
	public static class CreateFolderResult implements Serializable {
		
		private static final long serialVersionUID = 114722657875221279L;
		
		public static final CreateFolderResult NO_ERRORS = new CreateFolderResult("0");
		public static final CreateFolderResult FOLDER_ALREADY_EXISTS = new CreateFolderResult("101");
		public static final CreateFolderResult INVALID_FOLDER_NAME = new CreateFolderResult("102");
		public static final CreateFolderResult NO_PERMISSION = new CreateFolderResult("103");
		public static final CreateFolderResult UNKNOWN_ERROR = new CreateFolderResult("110");
		
		private String code;
		private CreateFolderResult(String code) {
			this.code = code;
		}
		public String getCode() { return this.code; }
		
		public static CreateFolderResult noErrors() { return NO_ERRORS; }
		public static CreateFolderResult folderAlreadyExists() { return FOLDER_ALREADY_EXISTS; }
		public static CreateFolderResult invalidFolderName() { return INVALID_FOLDER_NAME; }
		public static CreateFolderResult noPermission() { return NO_PERMISSION; }
		public static CreateFolderResult unknownError() { return UNKNOWN_ERROR; }
	}
	
	// =============================================================
	// === inner class (FileUploadResult) ==========================
	// =============================================================
	
	/**
	 * <!-- START SNIPPET: fileuploadresult -->
	 * 
	 * Represents the result of a server-side 'FileUpload' command call. It
	 * acts like a static factory containing only static methods to create the possible
	 * results which are:
	 * 
	 * <ul>
	 * 	<li>upload complete</li>
	 *  <li>invalid file </li>
	 *  <li>upload complete with file name changed</li>
	 * </ul>
	 * 
	 * <!-- END SNIPPET: fileuploadresult -->
	 * 
	 * @author tm_jee
	 * @version $Date$ $Id$
	 * @see AbstractRichtexteditorConnector#fileUpload(String, String, String, String, java.io.File)
	 */
	public static class FileUploadResult implements Serializable {
		
		private static final long serialVersionUID = 7864740137253316440L;
		
		private static final FileUploadResult UPLOAD_COMPLETED = new FileUploadResult("0");
		private static final FileUploadResult INVALID_FILE = new FileUploadResult("202");
		
		private String code;
		private String filename;
		private FileUploadResult(String code) {
			this(code, null);
		}
		private FileUploadResult(String code, String newFilename) {
			this.code = code;
			filename = newFilename;
		}
		public String getCode() { return code; }
		public String getFilename() { return filename; }
		
		public static FileUploadResult uploadComplete() { return UPLOAD_COMPLETED; }
		public static FileUploadResult invalidFile() { return INVALID_FILE; }
		public static FileUploadResult uploadCompleteWithFilenamChanged(String newFilename) {
			assert(newFilename != null);
			return new FileUploadResult("201", newFilename);
		}
	}
	
}
