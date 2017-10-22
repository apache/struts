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
package org.apache.struts2.showcase.filedownload;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class FileDownloadActionTest {

	private FileDownloadAction fileDownloadAction;

	@Before
	public void setUp() {
	    this.fileDownloadAction = new FileDownloadAction();
	}

	@Test
	public void testSanitizeInputPathShouldAllowSimpleParameter() throws Exception {
		assertEquals("foo", fileDownloadAction.sanitizeInputPath("foo"));
	}

	@Test
	public void testSanitizeInputPathShouldReturnNullForNullInput() throws Exception {
		assertNull(fileDownloadAction.sanitizeInputPath(null));
	}

	@Test
	public void testSanitizeInputPathShouldReturnNullForLeadingWebInf() throws Exception {
		assertNull(fileDownloadAction.sanitizeInputPath("WEB-INF/foo"));
	}

	@Test
	public void testSanitizeInputPathShouldReturnNullForNonLeadingWebInf() throws Exception {
		assertNull(fileDownloadAction.sanitizeInputPath("./WEB-INF/foo"));
	}

	@Test
	public void testSanitizeInputPathShouldReturnNullForNonUppercaseWebInf() throws Exception {
		assertNull(fileDownloadAction.sanitizeInputPath("./wEB-Inf/foo"));
	}
}
