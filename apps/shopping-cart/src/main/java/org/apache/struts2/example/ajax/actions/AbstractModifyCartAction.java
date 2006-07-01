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

import org.apache.struts2.example.ajax.catalog.Catalog;
import org.apache.struts2.example.ajax.catalog.Product;

/**
 * AbstractModifyCartAction
 *
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
