/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.struts2.mock.web.portlet;

import javax.portlet.ResourceResponse;

/**
 * Mock implementation of the {@link javax.portlet.ResourceResponse} interface.
 *
 * @author Juergen Hoeller
 * @since 3.0
 */
public class MockResourceResponse extends MockMimeResponse implements ResourceResponse {

	private int contentLength = 0;


	@Override
	public void setContentLength(int len) {
		this.contentLength = len;
	}

	public int getContentLength() {
		return this.contentLength;
	}

}
