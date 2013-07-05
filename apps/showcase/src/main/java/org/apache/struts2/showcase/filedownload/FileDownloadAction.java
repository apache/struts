/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.showcase.filedownload;

import com.opensymphony.xwork2.Action;
import org.apache.struts2.ServletActionContext;

import java.io.InputStream;

/**
 * Demonstrates file resource download.
 * Set filePath to the local file resource to download,
 * relative to the application root ("/images/struts.gif").
 */
public class FileDownloadAction implements Action {

	private String inputPath;

	public String execute() throws Exception {
		return SUCCESS;
	}

	public void setInputPath(String value) {
		inputPath = sanitizeInputPath(value);
	}

	/**
	 * As the user modifiable parameter inputPath will be used to access server side resources, we want the path to be
	 * sanitized - in this case it is demonstrated to disallow inputPath parameter values containing "WEB-INF". Consider to
	 * use even stricter rules in production environments.
	 *
	 * @param value the raw parameter input value to sanitize
	 *
	 * @return the sanitized value; <tt>null</tt> if value contains an invalid path segment like WEB-INF
	 */
	String sanitizeInputPath( String value ) {
		if (value != null && value.toUpperCase().contains("WEB-INF")) {
			return null;
		}
		return value;
	}

	public InputStream getInputStream() throws Exception {
		return ServletActionContext.getServletContext().getResourceAsStream(inputPath);
	}
}
