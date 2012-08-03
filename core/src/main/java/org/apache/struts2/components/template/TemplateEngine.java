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

package org.apache.struts2.components.template;

import java.util.Map;

/**
 * Any template language which wants to support UI tag templating needs to provide an implementation of this interface
 * to handle rendering the template
 */
public interface TemplateEngine {

    /**
     * Renders the template
     * @param templateContext  context for the given template.
     * @throws Exception is thrown if there is a failure when rendering.
     */
    void renderTemplate(TemplateRenderingContext templateContext) throws Exception;

    /**
     * Get's the properties for the given template.
     *
     * @param template   the template.
     * @return  the properties as key value pairs.
     */
    Map getThemeProps(Template template);

}
