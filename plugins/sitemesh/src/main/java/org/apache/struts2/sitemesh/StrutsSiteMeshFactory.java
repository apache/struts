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
package org.apache.struts2.sitemesh;

import com.opensymphony.module.sitemesh.Config;
import com.opensymphony.module.sitemesh.factory.DefaultFactory;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsStatics;

public class StrutsSiteMeshFactory extends DefaultFactory {

    private static final Logger LOG = LoggerFactory.getLogger(StrutsSiteMeshFactory.class);

    public StrutsSiteMeshFactory(Config config) {
        super(config);
    }

    /**
     * Determine whether a Page of given content-type should be parsed or not, avoiding inner action parsing.
     */
    @Override
    public boolean shouldParsePage(String contentType) {
        return !isInsideActionTag() && super.shouldParsePage(contentType);
    }

    private boolean isInsideActionTag() {
        if(ActionContext.getContext() == null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("ActionContext is null! Not a user request?");
            }
            return false;
        }
        Object attribute = ServletActionContext.getRequest().getAttribute(StrutsStatics.STRUTS_ACTION_TAG_INVOCATION);
        return (Boolean) ObjectUtils.defaultIfNull(attribute, Boolean.FALSE);
    }

}
