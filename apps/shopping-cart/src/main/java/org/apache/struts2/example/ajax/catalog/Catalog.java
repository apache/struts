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
package org.apache.struts2.example.ajax.catalog;

import java.util.Set;
import java.io.Serializable;

/**
 * Catalog
 *
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

