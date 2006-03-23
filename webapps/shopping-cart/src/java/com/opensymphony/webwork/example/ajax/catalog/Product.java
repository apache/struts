/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.opensymphony.webwork.example.ajax.catalog;

/**
 * Product
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public interface Product extends Identifiable {
    double getPrice();

    String getDescription();

    Category getCategory();
}
