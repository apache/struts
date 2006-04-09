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

import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.struts.action2.util.ServletContextAware;
import org.apache.struts.action2.views.util.UrlHelper;

/**
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class DefaultRichtexteditorConnector extends AbstractRichtexteditorConnector implements ServletContextAware {

	private static final Log _log = LogFactory.getLog(DefaultRichtexteditorConnector.class);
	
	private static final long serialVersionUID = -3792445192115623052L;
	
	protected String _actualServerPath = "/org/apache/struts/static/richtexteditor/data/";
	
	
	public String getActualServerPath() { return _actualServerPath; }
	public void setActualServerPath(String actualServerPath) { _actualServerPath = actualServerPath; }
	
	
	protected String calculateServerPath(String serverPath, String folderPath, String type) throws Exception {
		//return UrlHelper.buildUrl(serverPath, _request, _response, null, _request.getScheme(), true, true, true);
		return UrlHelper.buildUrl(serverPath+type+folderPath, _request, _response, new HashMap(), _request.getScheme(), true, true, true);
	}
	
	protected String calculateActualServerPath(String actualServerPath, String type, String folderPath) throws Exception {
		String path = "file://"+servletContext.getRealPath("/WEB-INF/classes"+actualServerPath);
		makeDirIfNotExists(path);
		path = path.endsWith("/") ? path : path+"/";
		return path+type+folderPath;
	}
	
	private ServletContext servletContext;
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	protected Folder[] getFolders(String virtualFolderPath, String type) throws Exception {
		String path = calculateActualServerPath(getActualServerPath(), type, virtualFolderPath);
		makeDirIfNotExists(path);
		java.io.File f = new java.io.File(new URI(path));
		java.io.File[] children = f.listFiles(new FileFilter() {
			public boolean accept(java.io.File pathname) {
				if (! pathname.isFile()) {
					return true;
				}
				return false;
			}
		});
		
		List tmpFolders = new ArrayList();
		for (int a=0; a< children.length; a++) {
			tmpFolders.add(new Folder(children[a].getName()));
		}
		
		return (Folder[]) tmpFolders.toArray(new Folder[0]);
	}

	protected FoldersAndFiles getFoldersAndFiles(String virtualFolderPath, String type) throws Exception {
		String path = calculateActualServerPath(getActualServerPath(), type, virtualFolderPath);
		makeDirIfNotExists(path);
		java.io.File f = new java.io.File(new URI(path));
		java.io.File[] children = f.listFiles();
		
		List directories = new ArrayList();
		List files = new ArrayList();
		for (int a=0; a< children.length; a++) {
			if (children[a].isDirectory()) {
				directories.add(new Folder(children[a].getName()));
			}
			else {
				try {
					files.add(new File(children[a].getName(), fileSizeInKBytes(children[a])));
				}
				catch(Exception e) {
					_log.error("cannot deal with file "+children[a], e);
				}
			}
		}
		
		return new FoldersAndFiles(
				(Folder[]) directories.toArray(new Folder[0]), 
				(File[]) files.toArray(new File[0])
		);
	}

	protected CreateFolderResult createFolder(String virtualFolderPath, String type, String newFolderName) {
		try {
			String tmpPath = calculateActualServerPath(getActualServerPath(), type, virtualFolderPath);
			tmpPath = tmpPath+newFolderName;
			boolean alreadyExists = makeDirIfNotExists(tmpPath);
			if (alreadyExists) {
				return CreateFolderResult.folderAlreadyExists();
			}
		}
		catch(Exception e) {
			_log.error(e.toString(), e);
			return CreateFolderResult.unknownError();
		}
		return CreateFolderResult.noErrors();
	}

	protected FileUploadResult fileUpload(String virtualFolderPath, String type, String filename, String contentType, java.io.File newFile) {
		try {
			String tmpDir = calculateActualServerPath(getActualServerPath(), type, virtualFolderPath);
			makeDirIfNotExists(tmpDir);
			String tmpFile = tmpDir+filename;
			if(makeFileIfNotExists(tmpFile)) {
				// already exists
				int a=0;
				String ext = String.valueOf(a);
				tmpFile = calculateActualServerPath(getActualServerPath(), type, virtualFolderPath)+filename+ext;
				while(makeFileIfNotExists(tmpFile)) {
					a = a + 1;
					ext = String.valueOf(a);
					if (a > 100) {
						return FileUploadResult.invalidFile();
					}
				}
				copyFile(newFile, new java.io.File(new URI(tmpFile)));
				return FileUploadResult.uploadCompleteWithFilenamChanged(filename+ext);
			}
			else {
				copyFile(newFile, new java.io.File(new URI(tmpFile)));
				return FileUploadResult.uploadComplete();
			}
		}
		catch(Exception e) {
			_log.error(e.toString(), e);
			return FileUploadResult.invalidFile();
		}
	}

	protected void unknownCommand(String command, String virtualFolderPath, String type, String filename, String contentType, java.io.File newFile) {
		throw new RuntimeException("unknown command "+command);
	}

	
	

	
	/**
	 *
	 * @param path
	 * @return true if file already exists, false otherwise.
	 */
	protected boolean makeDirIfNotExists(String path) throws URISyntaxException {
		java.io.File dir = new java.io.File(new URI(path));
		if (! dir.exists()) {
			_log.debug("make directory "+dir);
			boolean ok = dir.mkdirs();
			if (! ok) {
				throw new RuntimeException("cannot make directory "+dir);
			}
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param filePath
	 * @return true if file already exists, false otherwise
	 */
	protected boolean makeFileIfNotExists(String filePath) throws IOException, URISyntaxException {
		java.io.File f = new java.io.File(new URI(filePath));
		if (! f.exists()) {
			_log.debug("creating file "+filePath);
			boolean ok = f.createNewFile();
			if (! ok) {
				throw new RuntimeException("cannot create file "+filePath);
			}
			return false;
		}
		return true;
	}
	
	protected void copyFile(java.io.File from, java.io.File to) throws FileNotFoundException, IOException {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			_log.debug("copy file from "+from+" to "+to);
			fis = new FileInputStream(from);
			fos = new FileOutputStream(to);
			int tmpByte = fis.read();
			while(tmpByte != -1) {
				fos.write(tmpByte);
				tmpByte = fis.read();
			}
			fos.flush();
		}
		finally {
			if (fis != null)
				fis.close();
			if (fos != null)
				fos.close();
		}
	}
	
	protected long fileSizeInKBytes(java.io.File file) throws FileNotFoundException, IOException {
		FileInputStream fis = null;
		long size = 0;
		try {
			fis = new FileInputStream(file);
			size = fis.getChannel().size();
		}
		finally {
			if (fis != null)
				fis.close();
		}
		if (size > 0) {
			size = (size / 100);
		}
		_log.debug("size of file "+file+" is "+size+" kb");
		return size;
	}
}
