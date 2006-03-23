/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.opensymphony.webwork.example.ajax.actions;

import com.opensymphony.webwork.interceptor.SessionAware;
import com.opensymphony.xwork.ActionSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * SetActiveCategory
 *
 * @author Jason Carreira <jcarreira@eplus.com>
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
