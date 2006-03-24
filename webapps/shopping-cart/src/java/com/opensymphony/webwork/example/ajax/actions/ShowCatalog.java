/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.apache.struts.action2.example.ajax.actions;

import org.apache.struts.action2.example.ajax.catalog.Catalog;
import com.opensymphony.xwork.ActionSupport;

/**
 * ShowCatalog
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class ShowCatalog extends ActionSupport {
    protected Catalog catalog;

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    public Catalog getCatalog() {
        return catalog;
    }
}
