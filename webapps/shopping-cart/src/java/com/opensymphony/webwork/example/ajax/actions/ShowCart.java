/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.opensymphony.webwork.example.ajax.actions;

import com.opensymphony.webwork.example.ajax.cart.ShoppingCart;
import com.opensymphony.xwork.ActionSupport;

import java.util.Iterator;
import java.util.Set;

/**
 * ShowCart
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class ShowCart extends ActionSupport {
    private ShoppingCart cart;

    public void setShoppingCart(ShoppingCart cart) {
        this.cart = cart;
    }

    public ShoppingCart getCart() {
        return cart;
    }

    public int getNumCartItems() {
        ShoppingCart cart = getCart();
        if ((cart == null) || (cart.getContents() == null)) {
            return 0;
        }
        Set contents = cart.getContents();
        int number = 0;
        for( Iterator i=contents.iterator(); i.hasNext(); ) {
            ShoppingCart.CartEntry entry = (ShoppingCart.CartEntry)i.next();
            number += entry.getQuantity();
        }
        return number;
    }

    public double getCartTotal() {
        ShoppingCart cart = getCart();
        if ((cart == null) || (cart.getContents() == null)) {
            return 0;
        }
        double total = 0.0;
        for (Iterator iterator = cart.getContents().iterator(); iterator.hasNext();) {
            ShoppingCart.CartEntry cartEntry = (ShoppingCart.CartEntry) iterator.next();
            total += (cartEntry.getQuantity() * cartEntry.getProduct().getPrice());
        }
        return total;
    }
}
