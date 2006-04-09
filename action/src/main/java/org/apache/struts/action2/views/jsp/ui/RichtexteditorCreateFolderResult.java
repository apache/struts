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

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.apache.struts.action2.ServletActionContext;
import org.apache.struts.action2.components.AbstractRichtexteditorConnector.CreateFolderResult;
import com.opensymphony.xwork.ActionInvocation;


/**
 * <!-- START SNIPPET: javadoc -->
 * 
 * Struts's result, creating the apropriate result (in xml form) and write it out
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
 * @version $Date$ $Id$
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
