/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.opensymphony.webwork.example.ajax.actions;

import com.opensymphony.webwork.example.ajax.catalog.Catalog;
import com.opensymphony.webwork.example.ajax.catalog.Product;

/**
 * AbstractModifyCartAction
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public abstract class AbstractModifyCartAction extends ShowCart {
    protected int quantity;
    private Integer productId;
    protected Product product;
    protected Catalog catalog;

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getProductId() {
        return productId;
    }

    public Product getProduct() {
        return product;
    }

    public void validate() {
        if (productId != null) {
            product = catalog.findProductById(productId);
            if (product == null) {
                addFieldError("productId", "There is no product '" + productId + "' in the catalog " + catalog.getName());
            }
        }
    }
}
