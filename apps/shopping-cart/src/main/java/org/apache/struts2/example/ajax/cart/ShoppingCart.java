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

import java.util.Set;
import java.io.Serializable;

/**
 * ShoppingCart
 *
 */
public interface ShoppingCart extends Serializable {
    /**
     * Adds the specified quantity of the given {@link Product} to the shopping cart. This will add to any
     * existing quantity of this {@link Product} in the cart.
     *
     * @param quantity the quantity to add to the cart
     * @param product  the {@link Product} to add the quantity of
     */
    void addToCart(int quantity, Product product);

    /**
     * Sets the quantity of the specifed {@link Product} in the shopping cart. If the {@link Product} does not
     * exist in the Cart, it is added.
     *
     * @param quantity the quantity to set in the cart for the {@link Product}
     * @param product  the {@link Product} to set the quantity for
     */
    void setQuantity(int quantity, Product product);

    /**
     * Removes the given {@link Product} from the Cart
     *
     * @param product the {@link Product} to remove from the Cart
     */
    void removeFromCart(Product product);

    /**
     * Get a {@link java.util.List} of {@link CartEntry} objects in the cart
     *
     * @return a {@link java.util.List} of {@link CartEntry} objects in the Cart. Will not return null.
     */
    Set getContents();

    /**
     * Get the quantity of the product in the shopping cart
     *
     * @param product
     * @return the quantity, or 0 if the product is not in the cart
     */
    int getQuantityForProduct(Product product);

    interface CartEntry {
        int getQuantity();

        Product getProduct();

        void setQuantity(int quantity);
    }
}
