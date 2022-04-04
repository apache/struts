/*
 * $Id: PortletServletOutputStream.java 590812 2007-10-31 20:32:54Z apetrelli $
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
package org.apache.struts2.portlet.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;

/**
 * Wrapper object exposing a {@link OutputStream} from a portlet as a {@link ServletOutputStream} instance.
 * Clients accessing this stream object will in fact operate on the
 * {@link OutputStream} object wrapped by this stream object.
 */
public class PortletServletOutputStream extends ServletOutputStream {

	private OutputStream portletOutputStream;
	
	public PortletServletOutputStream(OutputStream portletOutputStream) {
		this.portletOutputStream = portletOutputStream;
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(int)
	 */
	@Override
	public void write(int ch) throws IOException {
		portletOutputStream.write(ch);
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#close()
	 */
	@Override
	public void close() throws IOException {
		portletOutputStream.close();
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#flush()
	 */
	@Override
	public void flush() throws IOException {
		portletOutputStream.flush();
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[])
	 */
	@Override
	public void write(byte[] b) throws IOException {
		portletOutputStream.write(b);
	}

	/* (non-Javadoc)
	 * @see java.io.OutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		portletOutputStream.write(b, off, len);
	}
	
	/**
	 * Get the wrapped {@link OutputStream} instance.
	 * @return The wrapped {@link OutputStream} instance.
	 */
	public OutputStream getOutputStream() {
		return portletOutputStream;
	}
}
