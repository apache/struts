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
package org.apache.struts2.portlet.util;

import org.apache.struts2.portlet.context.PortletActionContext;
import javax.portlet.PortletRequest;
import javax.portlet.MimeResponse;
import javax.portlet.BaseURL;
import javax.portlet.PortletSecurityException;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * PortletUrlJSR286Helper.
 *
 * @author Rene Gielen
 */
public class PortletUrlHelperJSR286 extends PortletUrlHelper {

    private static final Logger LOG = LogManager.getLogger(PortletUrlHelperJSR286.class);

    protected String encodeUrl( StringBuffer sb, PortletRequest req ) {
        MimeResponse resp = (MimeResponse) PortletActionContext.getResponse();
        return resp.encodeURL(req.getContextPath() + sb.toString());
    }

    protected Object createUrl( String scheme, String type, Map<String, String[]> portletParams ) {
        MimeResponse response = (MimeResponse) PortletActionContext.getResponse();
        BaseURL url;
        if (URLTYPE_NAME_ACTION.equalsIgnoreCase(type)) {
            if (LOG.isDebugEnabled()) LOG.debug("Creating action url");
            url = response.createActionURL();
        }
        else if(URLTYPE_NAME_RESOURCE.equalsIgnoreCase(type)) {
        	if (LOG.isDebugEnabled()) LOG.debug("Creating resource url");
        	url = response.createResourceURL();
        }
        else {
            if (LOG.isDebugEnabled()) LOG.debug("Creating render url");
            url = response.createRenderURL();
        }

        url.setParameters(portletParams);

        if ("HTTPS".equalsIgnoreCase(scheme)) {
            try {
                url.setSecure(true);
            } catch ( PortletSecurityException e) {
                LOG.error("Cannot set scheme to https", e);
            }
        }
        return url;
    }

}
