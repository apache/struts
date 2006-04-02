/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.apache.struts.webwork.example.ajax.catalog;

/**
 * Category
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public interface Category extends Identifiable {
    Category getParent();
}
