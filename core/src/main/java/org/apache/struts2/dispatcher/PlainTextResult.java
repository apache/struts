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
package org.apache.struts2.dispatcher;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork.ActionInvocation;

/**
 * <!-- START SNIPPET: description -->
 * 
 * A result that send the content out as plain text. Usefull typically when needed
 * to display the raw content of a JSP or Html file for example.
 * 
 * <!-- END SNIPPET: description -->
 * 
 * 
 * <!-- START SNIPPET: params -->
 * 
 * <ul>
 * 	<li>location (default) = location of the file (jsp/html) to be displayed as plain text.</li>
 *  <li>charSet (optional) = character set to be used. This character set will be used to set the
 *  response type (eg. Content-Type=text/plain; charset=UTF-8) and when reading 
 *  using a Reader. Some example of charSet would be UTF-8, ISO-8859-1 etc. 
 * </ul>
 * 
 * <!-- END SNIPPET: params -->
 * 
 * 
 * <pre>
 * <!-- START SNIPPET: example -->
 * 
 * &lt;action name="displayJspRawContent" &gt;
 *   &lt;result type="plaintext"&gt;/myJspFile.jsp&lt;/result&gt;
 * &lt;/action&gt;
 * 
 * 
 * &lt;action name="displayJspRawContent" &gt;
 *   &lt;result type="plaintext"&gt;
 *      &lt;param name="location"&gt;/myJspFile.jsp&lt;/param&gt;
 *      &lt;param name="charSet"&gt;UTF-8&lt;/param&gt;
 *   &lt;/result&gt;
 * &lt;/action&gt;
 * 
 * <!-- END SNIPPET: example -->
 * </pre>
 * 
 */
public class PlainTextResult extends StrutsResultSupport {
	
	private static final Log _log = LogFactory.getLog(PlainTextResult.class);

	private static final long serialVersionUID = 3633371605905583950L;
	
	public static final int BUFFER_SIZE = 1024;
	
	private String charSet;
	
	
	public String getCharSet() {
		return charSet;
	}
	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}
	
	
	
	protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
		
		// verify charset 
		Charset charset = null;
		if (charSet != null) {
			if (Charset.isSupported(charSet)) {
				charset = Charset.forName(charSet);
			}
			else {
				_log.warn("charset ["+charSet+"] is not recognized ");
				charset = null;
			}
		}
		
		HttpServletResponse response = (HttpServletResponse) invocation.getInvocationContext().get(HTTP_RESPONSE);
		ServletContext servletContext = (ServletContext) invocation.getInvocationContext().get(SERVLET_CONTEXT);

		
		if (charset != null) {
			response.setContentType("text/plain; charset="+charSet);
		}
		else {
			response.setContentType("text/plain");
		}
		response.setHeader("Content-Disposition", "inline");
		
		
		PrintWriter writer = response.getWriter();
		InputStreamReader reader = null;
		try {
			if (charset != null) {
				reader = new InputStreamReader(servletContext.getResourceAsStream(location), charset);
			}
			else {
				reader = new InputStreamReader(servletContext.getResourceAsStream(location));
			}
			if (reader == null) {
				_log.warn("resource at location ["+location+"] cannot be obtained (return null) from ServletContext !!! ");
			}
			else {
				char[] buffer = new char[BUFFER_SIZE];
				int charRead = 0;
				while((charRead = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, charRead);
				}
			}
		}
		finally {
			if (reader != null)
				reader.close();
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}
	}
}
