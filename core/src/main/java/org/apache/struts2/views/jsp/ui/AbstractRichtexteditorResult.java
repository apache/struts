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
package org.apache.struts2.views.jsp.ui;

import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.struts2.components.AbstractRichtexteditorConnector.CreateFolderResult;
import org.apache.struts2.components.AbstractRichtexteditorConnector.FileUploadResult;
import org.apache.struts2.components.AbstractRichtexteditorConnector.Folder;
import org.apache.struts2.components.AbstractRichtexteditorConnector.FoldersAndFiles;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;

/**
 * <!-- START SNIPPET: javadoc -->
 * 
 * Abstract result for all Rich Text Editor results. It contains common methods
 * that might come in handy to its subclass. 
 * 
 * Configuration of result necessary in xwork.xml (is already there by default) are
 * as follows:
 * 
 * <pre>
 *  &lt;!-- Results necessary when using 'browse server' and 'upload' feature of Richtexteditor --&gt;
 *  &lt;result-type name="richtexteditorGetFolders" 
 *                  class="org.apache.struts2.views.jsp.ui.RichtexteditorGetFoldersResult" /&gt;
 *  &lt;result-type name="richtexteditorGetFoldersAndFiles" 
 *                  class="org.apache.struts2.views.jsp.ui.RichtexteditorGetFoldersAndFilesResult" /&gt;
 *  &lt;result-type name="richtexteditorCreateFolder" 
 *                  class="org.apache.struts2.views.jsp.ui.RichtexteditorCreateFolderResult" /&gt;
 *  &lt;result-type name="richtexteditorFileUpload" 
 *                  class="org.apache.struts2.views.jsp.ui.RichtexteditorFileUploadResult" /&gt;
 * </pre>
 * 
 * <!-- END SNIPPET: javadoc -->
 * 
 */
public abstract class AbstractRichtexteditorResult implements Result {

	/**
	 * Build an xml <code>Document</code>
	 * 
	 * @return An XML Document
	 * @throws ParserConfigurationException
	 */
	protected Document buildDocument() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();
		
		return document;
	}
	
	
	/**
	 * Build a common xml structure for all xml based result. For example:
	 * 
	 * <pre>
	 * &lt;?xml version="1.0" encoding="utf-8" ?&gt;
     * &lt;Connector command="RequestedCommandName" resourceType=" RequestedResourceType"&gt;
     *       &lt;CurrentFolder path="CurrentFolderPath" url="CurrentFolderUrl" /&gt;
     *       &lt;!-- Here goes all specific command data --&gt;
     * &lt;/Connector&gt;
	 * </pre>
	 * 
	 * @param document
	 * @param command
	 * @param type
	 * @param folderPath
	 * @param serverPath
	 * @return A common xml structure for all xml based result.
	 */
	protected Element buildCommonResponseXml(Document document, String command, String type, String folderPath, String serverPath)  {
		Element connectorElement = document.createElement("Connector");
		connectorElement.setAttribute("command",command);
		connectorElement.setAttribute("resourceType",type);
		document.appendChild(connectorElement);
		
		Element myEl=document.createElement("CurrentFolder");
		myEl.setAttribute("path", folderPath);
		myEl.setAttribute("url", serverPath);
		connectorElement.appendChild(myEl);
		
		return connectorElement;
	}
	
	
	/**
	 * Convert a <code>Document<code> to its string representation.
	 * 
	 * @param document
	 * @return The documents String representation.
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 */
	protected String stringFromDocument(Document document) throws TransformerConfigurationException, TransformerException {
		//document.normalizeDocument();
		StringWriter writer = new StringWriter();
		String result = null;
		try {
			writer = new StringWriter();
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(new DOMSource(document), new StreamResult(writer));
			result = writer.getBuffer().toString();

		}
		finally {
			if (writer != null) 
				try {
					writer.close();
				}
				catch(Exception e) {
				}
				writer = null;
		}
		return result;
	}
	
	/**
	 * Write a <code>Document</code> to an OutputStream <code>out</code>
	 * 
	 * @param document
	 * @param out
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 */
	protected void writeDocumentToStream(Document document, OutputStream out) throws TransformerConfigurationException, TransformerException {
		//document.normalizeDocument();
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.transform(new DOMSource(document), new StreamResult(out));
	}
	
	/**
	 * Get the command send by the Rich Text Editor. It would be one of the followings. 
	 * Only valid when rich text editor issue a server-side 'Browse' not 'Upload'.
	 * 
	 * <ul>
	 *   <li>GetFolders</li>
	 *   <li>GetFoldersAndFiles</li>
	 *   <li>CreateFolder</li>
	 *   <li>FileUpload</li>
	 * </ul>
	 * 
	 * @param invocation
	 * @return The command send by the Rich Text Editor.
	 */
	protected String getCommand(ActionInvocation invocation) {
		return (String) invocation.getStack().getContext().get("__richtexteditorCommand");
	}
	
	/**
	 * Get the type send by the Rich Text Editor. It could be one of the followings:
	 * 
	 * <ul>
	 *    <li>Image</li>
	 *    <li>File</li>
	 *    <li>Flash</li>
	 * </ul>
	 * 
	 * @param invocation
	 * @return The type send by the Rich Text Editor.
	 */
	protected String getType(ActionInvocation invocation) {
		return (String) invocation.getStack().getContext().get("__richtexteditorType");
	}
	
	/**
	 * Get the folder path send by the Rich Text Editor.
	 * 
	 * @param invocation
	 * @return The folder path send by the Rich Text Editor.
	 */
	protected String getFolderPath(ActionInvocation invocation) {
		return (String) invocation.getStack().getContext().get("__richtexteditorFolderPath");
	}
	
	/**
	 * Get the server path calculated from AbstractRichtexteditoConnector or its 
	 * decendant through AbstractRichtexteditorConnector#calculate#calculateServerPath(String, String String)
	 * 
	 * @param invocation
	 * @return The server path calculated from AbstractRichtexteditoConnector
	 * @see org.apache.struts2.components.AbstractRichtexteditorConnector#calculateServerPath(String, String, String)
	 */
	protected String getServerPath(ActionInvocation invocation) {
		return (String) invocation.getStack().getContext().get("__richtexteditorServerPath");
	}
	
	/**
	 * Get the <code>Folder[]</code> computed from AbstractRichtexteditorConnector or its
	 * decendant through AbstractRichtexteditorConnector#getFolders(String, String). Only
	 * valid if it is a 'GetFolder' command.
	 * 
	 * @param invocation
	 * @return The <code>Folder[]</code> computed from AbstractRichtexteditorConnector
	 * @see org.apache.struts2.components.AbstractRichtexteditorConnector#getFolders(String, String)
	 */
	protected Folder[] richtexteditorFolders(ActionInvocation invocation) {
		return (Folder[]) invocation.getStack().getContext().get("__richtexteditorGetFolders");
	}
	
	/**
	 * Get the <code>FoldersAndFiles</code> computed from AbstractRichtexteditorConnector or its
	 * decendant through AbstractRichtexteditorConnector#getFoldersAndFiles(String, String). Only
	 * valid if it is a 'GetFoldersAndFiles' command.
	 * 
	 * @param invocation
	 * @return The <code>FoldersAndFiles</code> computed from AbstractRichtexteditorConnector
	 * @see org.apache.struts2.components.AbstractRichtexteditorConnector#getFoldersAndFiles(String, String)
	 */
	protected FoldersAndFiles richtexteditorFoldersAndFiles(ActionInvocation invocation) {
		return (FoldersAndFiles) invocation.getStack().getContext().get("__richtexteditorGetFoldersAndFiles");
	}
	
	
	/**
	 * Get the <code>CreateFolderResult</code> computed from AbstractRichtexteditorConnector or its
	 * decendant through AbstractRichtexteditorConnector#createFolder(String, String, String). Only 
	 * valid if it is a 'CreateFolder' command.
	 * 
	 * @param invocation
	 * @return The <code>CreateFolderResult</code> computed from AbstractRichtexteditorConnector
	 * @see org.apache.struts2.components.AbstractRichtexteditorConnector#createFolder(String, String, String)
	 */
	protected CreateFolderResult richtexteditorCreateFolderResult(ActionInvocation invocation) {
		return (CreateFolderResult) invocation.getStack().getContext().get("__richtexteditorCreateFolder");
	}
	
	/**
	 * Get the <code>FileUploadResult</code> computed from AbstractRichtexteditorConnector or its
	 * decendant through AbstractRichtexteditorConnector#fileUpload(String, String, String, String, File).
	 * Only valid if it is a 'FileUpload' command
	 * 
	 * @param invocation
	 * @return The <code>FileUploadResult</code> computed from AbstractRichtexteditorConnector
	 * @see org.apache.struts2.components.AbstractRichtexteditorConnector#fileUpload(String, String, String, String, java.io.File)
	 */
	protected FileUploadResult richtexteditorFileUploadResult(ActionInvocation invocation) {
		return (FileUploadResult) invocation.getStack().getContext().get("__richtexteditorFileUpload");
	}
}
