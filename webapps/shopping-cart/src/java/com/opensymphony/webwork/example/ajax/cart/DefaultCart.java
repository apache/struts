/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.opensymphony.webwork.example.ajax.cart;

import com.opensymphony.webwork.example.ajax.catalog.Product;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * DefaultCart
 *
 * @author Jason Carreira <jcarreira@eplus.com>
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
