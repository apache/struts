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
// START SNIPPET: entire-file
package org.apache.struts2.showcase.fileupload;

import com.opensymphony.xwork2.ActionSupport;

import java.io.File;

/**
 * Showcase action - mutiple file upload using array.
 *
 * @version $Date$ $Id$
 */
public class MultipleFileUploadUsingArrayAction extends ActionSupport {

	private File[] uploads;
	private String[] uploadFileNames;
	private String[] uploadContentTypes;


	public String upload() throws Exception {
		System.out.println("\n\n upload2");
		System.out.println("files:");
		for (File u : uploads) {
			System.out.println("*** " + u + "\t" + u.length());
		}
		System.out.println("filenames:");
		for (String n : uploadFileNames) {
			System.out.println("*** " + n);
		}
		System.out.println("content types:");
		for (String c : uploadContentTypes) {
			System.out.println("*** " + c);
		}
		System.out.println("\n\n");
		return SUCCESS;
	}

	public File[] getUpload() {
		return this.uploads;
	}

	public void setUpload(File[] upload) {
		this.uploads = upload;
	}

	public String[] getUploadFileName() {
		return this.uploadFileNames;
	}

	public void setUploadFileName(String[] uploadFileName) {
		this.uploadFileNames = uploadFileName;
	}

	public String[] getUploadContentType() {
		return this.uploadContentTypes;
	}

	public void setUploadContentType(String[] uploadContentType) {
		this.uploadContentTypes = uploadContentType;
	}
}
// END SNIPPET: entire-file