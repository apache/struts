/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.opensymphony.webwork.example.ajax.actions;

import com.opensymphony.webwork.example.ajax.cart.ShoppingCart;
import com.opensymphony.webwork.example.ajax.catalog.Catalog;
import com.opensymphony.webwork.example.ajax.catalog.Category;
import com.opensymphony.webwork.interceptor.SessionAware;
import com.opensymphony.xwork.ActionSupport;

import java.util.Map;

/**
 * ActiveCategory
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class ActiveCategory extends ActionSupport implements SessionAware {
    public static final String ACTIVE_CATEGORY_ID = "catalog_activeCategoryId";
    private Map session;
    private Category category;
    private Catalog catalog;
    private ShoppingCart cart;

    public void setSession(Map session) {
        this.session = session;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public Integer getCategoryId() {
        return (Integer) session.get(ACTIVE_CATEGORY_ID);
    }

    public Category getCategory() {
        return category;
    }

    public String execute() throws Exception {
        Integer categoryId = getCategoryId();
        if (categoryId != null) {
            category = catalog.findCategoryForId(categoryId);
        }
        return SUCCESS;
    }

    public void setShoppingCart(ShoppingCart cart) {
        this.cart = cart;
    }

    public ShoppingCart getCart() {
        return cart;
    }
}
