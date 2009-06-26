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

package org.apache.struts2.components;

import org.apache.struts2.dispatcher.mapper.ActionMapper;
import java.io.Writer;

/**
 * Implementations of this interface are responsible for rendering/creating URLs for a specific
 * environment (e.g. Servlet, Portlet). 
 *
 */
public interface UrlRenderer {
	
	/**
	 * Preprocessing step
	 * @param urlComponent
	 */
	void beforeRenderUrl(UrlProvider provider);
	
	/**
	 * Render a URL.
	 * @param writer A writer that the implementation can use to write the result to.
	 * @param urlComponent The {@link UrlProvider} component that "owns" this renderer.
	 */
	void renderUrl(Writer writer, UrlProvider provider);
	
	/**
	 * Render a Form URL.
	 * @param formComponent The {@link Form} component that "owns" this renderer.
	 */
	void renderFormUrl(Form formComponent);

      void setActionMapper(ActionMapper actionMapper);

}
