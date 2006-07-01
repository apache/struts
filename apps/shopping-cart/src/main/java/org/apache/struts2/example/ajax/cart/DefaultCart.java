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
package org.apache.struts2.example.ajax.cart;

import org.apache.struts2.example.ajax.catalog.Product;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * DefaultCart
 *
 */
public class DefaultCart implements ShoppingCart {
    Map contents = new HashMap();

    public void addToCart(int quantity, Product product) {
        CartEntry entry = (CartEntry) contents.get(product);
        if (entry == null) {
            entry = new DefaultCartEntry(quantity, product);
            contents.put(product, entry);
        } else {
            entry.setQuantity(entry.getQuantity() + quantity);
        }
    }

    public void setQuantity(int quantity, Product product) {
        if (quantity <= 0) {
            contents.remove(product);
            return;
        }
        CartEntry entry = (CartEntry) contents.get(product);
        if (entry == null) {
            entry = new DefaultCartEntry(quantity, product);
            contents.put(product, entry);
        } else {
            entry.setQuantity(quantity+entry.getQuantity());
        }
    }

    public void removeFromCart(Product product) {
        contents.remove(product);
    }

    public Set getContents() {
        return new HashSet(contents.values());
    }

    public int getQuantityForProduct(Product product) {
        CartEntry entry = (CartEntry) contents.get(product);
        if (entry == null) {
            return 0;
        }
        return entry.getQuantity();
    }

    public String toString() {
        return "DefaultCart{" +
                "contents=" + contents +
                "}";
    }

    class DefaultCartEntry implements CartEntry {
        private int quantity;
        private Product product;

        public DefaultCartEntry(int quantity, Product product) {
            this.quantity = quantity;
            this.product = product;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public Product getProduct() {
            return product;
        }
    }
}
