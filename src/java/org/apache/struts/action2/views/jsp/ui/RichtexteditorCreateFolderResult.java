/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.webwork.views.jsp.ui;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.components.AbstractRichtexteditorConnector.CreateFolderResult;
import com.opensymphony.xwork.ActionInvocation;


/**
 * <!-- START SNIPPET: javadoc -->
 * 
 * WebWork's result, creating the apropriate result (in xml form) and write it out
 * to the response stream corresponding to a 'CreateFolder' command from the 
 * Rich Text Editor.
 * 
 * <p/>
 * 
 * An example of the response would be as follows:
 * 
 * <pre>
 * &lt;?xml version="1.0" encoding="utf-8" ?&gt;
 * &lt;Connector command="CreateFolder" resourceType="File"&gt;
 *   &lt;CurrentFolder path="/Samples/Docs/" url="/UserFiles/File/Samples/Docs/" /&gt;
 *   &lt;Error number="0" /&gt;
 * &lt;/Connector&gt;
 * </pre>
 * 
 * <!-- END SNIPPET: javadoc -->
 * 
 * @author tm_jee
 * @version $Date: 2006/02/20 15:24:37 $ $Id: RichtexteditorCreateFolderResult.java,v 1.3 2006/02/20 15:24:37 tmjee Exp $
 */
public class RichtexteditorCreateFolderResult extends AbstractRichtexteditorResult {
	
	private static final Log _log = LogFactory.getLog(RichtexteditorCreateFolderResult.class);

	private static final long serialVersionUID = 9024490340530057673L;

	/**
	 * <!-- START SNIPPET: execute -->
	 * 
	 * Write the result (in xml form) to the response stream corresponding to 
	 * the Rich Text Editor's 'CreateFolder' command
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
		
		CreateFolderResult createFolderResult = richtexteditorCreateFolderResult(invocation);
		
		Document document = buildDocument();
		Element root = buildCommonResponseXml(document, 
				getCommand(invocation), getType(invocation), 
				getFolderPath(invocation), getServerPath(invocation));
		
		Element errorElement = document.createElement("Error");
		errorElement.setAttribute("number", createFolderResult.getCode());
		root.appendChild(errorElement);
		
		if (_log.isDebugEnabled()) {
			String result = stringFromDocument(document);
			_log.debug(result);
		}
		
		writeDocumentToStream(document, os);
		os.flush();
		os.close();
	}
}
