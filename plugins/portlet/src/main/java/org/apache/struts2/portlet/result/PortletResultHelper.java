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
package org.apache.struts2.portlet.result;

import javax.portlet.*;
import java.io.IOException;

/**
 * PortletResultHelper abstracts Portlet API result functions specific to the used API spec version.
 *
 * @author Rene Gielen
 */

public interface PortletResultHelper {

    /**
     * Set a render parameter, abstracted from the used Portlet API version
     *
     * @param response The response to set the parameter on.
     * @param key      The parameter key to set.
     * @param value    The parameter value to set.
     */
    void setRenderParameter( PortletResponse response, String key, String value );

    /**
     * Set a portlet mode, abstracted from the used Portlet API version
     *
     * @param response    The response to set the portlet mode on.
     * @param portletMode The portlet mode to set.
     *
     * @throws PortletModeException in case of errors during setting of portlet mode
     */
    void setPortletMode( PortletResponse response, PortletMode portletMode ) throws PortletModeException;

    /**
     * Call a dispatcher's include method, abstracted from the used Portlet API version.
     *
     * @param dispatcher  The dispatcher to call the include method on.
     * @param contentType The content type to set for the response.
     * @param request     The request to use for including
     * @param response    The response to use for including
     *
     * @throws IOException in case of any I/O errors
     * @throws PortletException in case of any portlet errors
     */
    void include( PortletRequestDispatcher dispatcher, String contentType, PortletRequest request,
                  PortletResponse response ) throws IOException, PortletException;
}
