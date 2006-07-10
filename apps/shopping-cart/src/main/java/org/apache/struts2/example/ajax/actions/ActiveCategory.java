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

import org.apache.struts2.example.ajax.cart.ShoppingCart;
import org.apache.struts2.example.ajax.catalog.Catalog;
import org.apache.struts2.example.ajax.catalog.Category;
import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionSupport;

import java.util.Map;

/**
 * ActiveCategory
 *
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
