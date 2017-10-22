/*
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
package org.apache.struts2.showcase.action;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.interceptor.annotations.After;
import org.apache.struts2.ServletActionContext;

import java.io.*;
import java.net.URL;

/**
 * Will only work on containers that unzip war files
 */
public class JSPEvalAction extends ExampleAction {
	private String jsp;
	private final static String FILE = "/interactive/demo.jsp";

	public String execute() throws IOException {
		if (jsp != null) {
			//write it to file
			URL url = ServletActionContext.getServletContext().getResource(FILE);
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(url
					.getFile())));
			try {
				//directive
				writer.write("<%@ taglib prefix=\"s\" uri=\"/struts-tags\" %>");
				writer.write(jsp);
			} finally {
				if (writer != null)
					writer.close();
			}
		}
		return Action.SUCCESS;
	}

	@After
	public void cleanUp() throws IOException {
		URL url = ServletActionContext.getServletContext().getResource(FILE);
		FileOutputStream out = new FileOutputStream(new File(url.getFile()));
		try {
			out.getChannel().truncate(0);
		} finally {
			if (out != null)
				out.close();
		}
	}

	public void setJsp(String jsp) {
		this.jsp = jsp;
	}

}
