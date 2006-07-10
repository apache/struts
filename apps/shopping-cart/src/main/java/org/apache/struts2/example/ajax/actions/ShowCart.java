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
import com.opensymphony.xwork2.ActionSupport;

import java.util.Iterator;
import java.util.Set;

/**
 * ShowCart
 *
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
