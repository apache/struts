/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package org.apache.struts.action2.views.jsp.ui;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.struts.action2.ServletActionContext;
import org.apache.struts.action2.components.AbstractRichtexteditorConnector.File;
import org.apache.struts.action2.components.AbstractRichtexteditorConnector.Folder;
import org.apache.struts.action2.components.AbstractRichtexteditorConnector.FoldersAndFiles;
import com.opensymphony.xwork.ActionInvocation;

/**
 * <!-- START SNIPPET: javadoc -->
 * 
 * WebWork's result, creating the appropriate result (in xml form) and write to 
 * the response stream corresponding to the Rich Text Editor's 'GetFoldersAndFiles' 
 * command
 * 
 * <p/>
 * 
 * An example of the response would be as follows:
 * 
 * <pre>
 * &lt;?xml version="1.0" encoding="utf-8" ?&gt;
 * &lt;Connector command="GetFoldersAndFiles" resourceType="File"&gt;
 *   &lt;CurrentFolder path="/Samples/Docs/" url="/UserFiles/File/Samples/Docs/" /&gt;
 *   &lt;Folders&gt;
 *     &lt;Folder name="Documents" /&gt;
 *     &lt;Folder name="Files" /&gt;
 *     &lt;Folder name="Other Files" /&gt;
 *     &lt;Folder name="Related" /&gt;
 *   &lt;/Folders&gt;
 *   &lt;Files&gt;
 *     &lt;File name="XML Definition.doc" size="14" /&gt;
 *     &lt;File name="Samples.txt" size="5" /&gt;
 *     &lt;File name="Definition.txt" size="125" /&gt;
 *     &lt;File name="External Resources.drw" size="840" /&gt;
 *     &lt;File name="Todo.txt" size="2" /&gt;
 *   &lt;/Files&gt;
 * &lt;/Connector&gt;
 * </pre>
 * 
 * <!-- END SNIPPET: javadoc -->
 * 
 * @author tm_jee
 * @version $Date: 2006/02/20 15:24:38 $ $Id: RichtexteditorGetFoldersAndFilesResult.java,v 1.3 2006/02/20 15:24:38 tmjee Exp $
 */
public class RichtexteditorGetFoldersAndFilesResult extends AbstractRichtexteditorResult {

	private static final Log _log = LogFactory.getLog(RichtexteditorGetFoldersAndFilesResult.class);
	
	private static final long serialVersionUID = -8405656868125936172L;

	/**
	 * <!-- START SNIPPET: execute -->
	 * 
	 * Write the result (in xml form) to the response stream corresponding to 
	 * the Rich Text Editor's 'GetFoldersAndFiles' command.
	 * 
	 * <!-- END SNIPPET: execute -->
	 * 
	 * @param invocation
	 */
	public void execute(ActionInvocation invocation) throws Exception {
		
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/xml; charset=UTF-8");
		response.setHeader("Cache-Control","no-cache");
		
		OutputStream os = response.getOutputStream();
		
		Document document = buildDocument();
		Element root = buildCommonResponseXml(document, 
				getCommand(invocation), getType(invocation), 
				getFolderPath(invocation), getServerPath(invocation));
		
		FoldersAndFiles foldersAndFiles = richtexteditorFoldersAndFiles(invocation);
		
		Folder[] folders = foldersAndFiles.getFolders();
		File[] files = foldersAndFiles.getFiles();
		
		Element foldersElement = document.createElement("Folders");
		if (folders != null) {
			for (int a=0; a< folders.length; a++) {
				Element folderElement = document.createElement("Folder");
				folderElement.setAttribute("name", folders[a].getFoldername());
				foldersElement.appendChild(folderElement);
			}
		}
		root.appendChild(foldersElement);
		
		Element filesElement = document.createElement("Files");
		if (files != null) {
			for (int a=0; a< files.length; a++) {
				Element fileElement = document.createElement("File");
				fileElement.setAttribute("name", files[a].getFilename());
				fileElement.setAttribute("size", String.valueOf(files[a].getSizeInKb()));
				filesElement.appendChild(fileElement);
			}
		}
		root.appendChild(filesElement);
		
		if (_log.isDebugEnabled()) {
			String result = stringFromDocument(document);
			_log.debug(result);
		}
		
		writeDocumentToStream(document, os);
		os.flush();
		os.close();
	}
}
