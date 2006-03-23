/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.opensymphony.webwork.example.ajax.actions;

import com.opensymphony.webwork.example.ajax.catalog.Catalog;
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
