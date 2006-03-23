/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package com.opensymphony.webwork.example.ajax.catalog;

import java.util.Set;
import java.io.Serializable;

/**
 * Catalog
 *
 * @author Jason Carreira <jcarreira@eplus.com>
 */
public interface Catalog extends Identifiable, Serializable {
    /**
     * Get a Set of all {@link Category}s in this Catalog.
     */
    Set findAllCategories();

    /**
     * Get a Set of all {@link Product}s in this Catalog.
     *
     * @return all {@link Product}s in this Catalog. Will not be null.
     */
    Set findAllProducts();

    /**
     * Get the {@link Category} for the given Id.
     *
     * @param id the Category Id
     * @return the {@link Category} for this Id, or null if none exists.
     */
    Category findCategoryForId(Integer id);

    /**
     * Find the {@link Product} for the given Id
     *
     * @param productId the Product id
     * @return the {@link Product} for this Id, or null if none exists.
     */
    Product findProductById(Integer productId);


    /**
     * Find all {@link Product}s for the given {@link Category}
     *
     * @param category the category of products to return
     * @return a Set of {@link Product}s in this {@link Category}. Will not be null.
     */
    Set findProductsByCategory(Category category);
}

