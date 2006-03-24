/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.apache.struts.action2.example.ajax.actions;

import org.apache.struts.action2.example.ajax.cart.ShoppingCart;

/**
 * UpdateQuantityInCart
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public class UpdateQuantityInCart extends AbstractModifyCartAction {
    public String execute() throws Exception {
        ShoppingCart cart = getCart();
        if (cart != null) {
            cart.setQuantity(quantity, product);
        }
        return NONE;
    }
}
