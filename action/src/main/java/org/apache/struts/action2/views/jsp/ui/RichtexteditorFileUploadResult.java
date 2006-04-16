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

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.struts.action2.ServletActionContext;
import org.apache.struts.action2.components.AbstractRichtexteditorConnector.FileUploadResult;
import com.opensymphony.xwork.ActionInvocation;

/**
 * <!-- START SNIPPET: javadoc -->
 * 
 * Struts's result, creating the appropriate result (in javascript form) and write to 
 * the response stream corresponding to a 'FileUpload' command from the 
 * Rich Text Editor.
 * 
 * <p/>
 * 
 * An example of the response would be as follows:
 * 
 * <pre>
 * &lt;script type="text/javascript"&gt;
 *      window.parent.frames['frmUpload'].OnUploadCompleted(0) ;
 * &lt;/script&gt;
 * </pre>
 * 
 * <!-- END SNIPPET: javadoc -->
 * 
 */
public class RichtexteditorFileUploadResult extends AbstractRichtexteditorResult {
	
	private static final long serialVersionUID = 4094812005581706392L;
	
	private static final Log _log = LogFactory.getLog(RichtexteditorFileUploadResult.class);
	
	
	/**
	 * <!-- START SNIPPET: execute -->
	 * 
	 * Write the result (in javascript form) to the response stream corresponding to 
	 * the Rich Text Editor's 'FileUpload' command.
	 * 
	 * <!-- END SNIPPET: execute -->
	 */
	public void execute(ActionInvocation invocation) throws Exception {
		
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html; charset=UTF-8");
		response.setHeader("Cache-Control","no-cache");
		PrintWriter out = response.getWriter();
		
		/**
		 <script type="text/javascript">
		 	window.parent.frames['frmUpload'].OnUploadCompleted(0) ;
        </script>
		 */
		
		FileUploadResult fileUploadResult = richtexteditorFileUploadResult(invocation);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("<script type=\"text/javascript\">");
		if (fileUploadResult.getFilename() != null) {
			buffer.append("window.parent.frames['frmUpload'].OnUploadCompleted("+fileUploadResult.getCode()+", '"+fileUploadResult.getFilename()+"');");
		}
		else {
			buffer.append("window.parent.frames['frmUpload'].OnUploadCompleted("+fileUploadResult.getCode()+");");
		}
		buffer.append("</script>");
		
		String s = buffer.toString();
		if (_log.isDebugEnabled()) {
			_log.debug(s);
		}
		
		out.println(s);
		out.flush();
		out.close();
	}

}
