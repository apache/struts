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
import org.apache.struts.action2.components.AbstractRichtexteditorConnector.Folder;
import com.opensymphony.xwork.ActionInvocation;

/**
 * <!-- START SNIPPET: javadoc -->
 * 
 * Struts's result, creating the appropriate result (in xml form) and write to the 
 * response stream corresponding the the Rich Text Editor's 'GetFolders' command.
 * 
 * <p/>
 * 
 * An example of the response would be as follows:
 * 
 * <pre>
 * &lt;?xml version="1.0" encoding="utf-8" ?&gt;
 * &lt;Connector command="GetFolders" resourceType="File"&gt;
 *   &lt;CurrentFolder path="/Samples/Docs/" url="/UserFiles/File/Samples/Docs/" /&gt;
 *   &lt;Folders&gt;
 *     &lt;Folder name="Documents" /&gt;
 *     &lt;Folder name="Files" /&gt;
 *     &lt;Folder name="Other Files" /&gt;
 *     &lt;Folder name="Related" /&gt;
 *  &lt;/Folders&gt;
 * &lt;/Connector&gt;
 * </pre>
 * 
 * <!-- END SNIPPET: javadoc -->
 * 
 * @author tm_jee
 * @version $Date$ $Id$
 */
public class RichtexteditorGetFoldersResult extends AbstractRichtexteditorResult {

	private static final Log _log = LogFactory.getLog(RichtexteditorGetFoldersResult.class);
	
	private static final long serialVersionUID = -6414969434944547862L;

	
	/**
	 * <!-- START SNIPPET: execute -->
	 * 
	 * Write the response (in xml form) to the response stream corresponding to 
	 * the Rich Text Editor's 'GetFolders' command.
	 * 
	 * <!-- END SNIPPET: execute -->
	 */
	public void execute(ActionInvocation invocation) throws Exception {
		
		Folder[] folders = richtexteditorFolders(invocation);
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/xml; charset=UTF-8");
		response.setHeader("Cache-Control","no-cache");
		
		OutputStream os = response.getOutputStream();

		Document document = buildDocument();
		Element root = buildCommonResponseXml(document, 
				getCommand(invocation), getType(invocation), 
				getFolderPath(invocation), getServerPath(invocation));
		
		Element foldersElement = document.createElement("Folders");
		if (folders != null) {
			for (int a=0; a< folders.length; a++) {
				Element folderElement = document.createElement("Folder");
				folderElement.setAttribute("name", folders[a].getFoldername());
				foldersElement.appendChild(folderElement);
			}
		}
		root.appendChild(foldersElement);
		
		if (_log.isDebugEnabled()) {
			String result = stringFromDocument(document);
			_log.debug(result);
		}
		
		writeDocumentToStream(document, os);
		os.flush();
		os.close();
	}
}
