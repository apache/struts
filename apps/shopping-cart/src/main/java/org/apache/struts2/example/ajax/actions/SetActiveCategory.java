/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts2.example.ajax.actions;

import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * SetActiveCategory
 *
 */
public class SetActiveCategory extends ActionSupport implements SessionAware {
    private Map session;
    private Integer categoryId;
    private static Log LOG = LogFactory.getLog(SetActiveCategory.class);

    public void setSession(Map session) {
        this.session = session;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public String execute() throws Exception {
        LOG.debug("Setting the active category to " + categoryId);
        if (categoryId != null) {
            session.put(ActiveCategory.ACTIVE_CATEGORY_ID, categoryId);
        } else {
            session.remove(ActiveCategory.ACTIVE_CATEGORY_ID);
        }
        return SUCCESS;
    }
}
