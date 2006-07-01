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

import java.util.*;

/**
 * TestCatalog
 *
 */
public class TestCatalog implements Catalog {
    private static Map categories = new HashMap();
    private static Map products = new HashMap();

    public static Category CATEGORY_ELECTRONICS = new TestCategory(5001, "Electronics", null);
    public static Category CATEGORY_CLOTHING = new TestCategory(5002, "Clothing", null);
    public static Category CATEGORY_TELEVISIONS = new TestCategory(5003, "Televisions", CATEGORY_ELECTRONICS);
    public static Category CATEGORY_STEREOS = new TestCategory(5004, "Stereos", CATEGORY_ELECTRONICS);
    public static Category CATEGORY_SHIRTS = new TestCategory(5005, "Shirts", CATEGORY_CLOTHING);
    public static Category CATEGORY_SLACKS = new TestCategory(5006, "Slacks", CATEGORY_CLOTHING);

    public static Product PRODUCT_HD_TV = new TestProduct(7001, "High Definition Television", "54\" High Definition Plasma Television", 5445.00, CATEGORY_TELEVISIONS);
    public static Product PRODUCT_STEREO = new TestProduct(7002, "All-in-one stereo", "All-in-one stereo with AM/FM tuner, 6 CD changer, 2x300 Watt speakers", 99.99, CATEGORY_STEREOS);

    public static Product PRODUCT_BLUE_SHIRT = new TestProduct(8001, "Blue Shirt", "Blue Shirt", 29.99, CATEGORY_SHIRTS);
    public static Product PRODUCT_RED_SHIRT = new TestProduct(8002, "Red Shirt", "Red Shirt", 29.99, CATEGORY_SHIRTS);
    public static Product PRODUCT_GREEN_SHIRT = new TestProduct(8003, "Green Shirt", "Green Shirt", 29.99, CATEGORY_SHIRTS);

    public TestCatalog() {
    }

    public int getId() {
        return 1001;
    }

    public String getName() {
        return "Test Catalog";
    }

    public Set findAllCategories() {
        return new HashSet(categories.values());
    }

    public Set findAllProducts() {
        return new HashSet(products.values());
    }

    public Category findCategoryForId(Integer id) {
        return (Category) categories.get(id);
    }

    public Product findProductById(Integer productId) {
        return (Product) products.get(productId);
    }

    public Set findProductsByCategory(Category category) {
        Set categoryProducts = new HashSet();
        for (Iterator iterator = products.values().iterator(); iterator.hasNext();) {
            Product product = (Product) iterator.next();
            if (product.getCategory().equals(category)) {
                categoryProducts.add(product);
            }
        }
        return categoryProducts;
    }

    public String toString() {
        return "Test Catalog";
    }

    private static class TestCategory implements Category {
        private int id;
        private String name;
        private Category parent;

        private TestCategory(int id, String name, Category parent) {
            this.id = id;
            this.name = name;
            this.parent = parent;

            categories.put(new Integer(id), this);
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Category getParent() {
            return parent;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final TestCategory testCategory = (TestCategory) o;

            return id == testCategory.id;

        }

        public int hashCode() {
            return id;
        }


        public String toString() {
            return "TestCategory{" +
                    "id='" + id + "'" +
                    ", name='" + name + "'" +
                    ", parent=" + parent +
                    "}";
        }
    }

    private static class TestProduct implements Product {
        private int id;
        private String name;
        private String description;
        private double price;
        private Category category;

        public TestProduct(int id, String name, String description, double price, Category category) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.category = category;

            products.put(new Integer(id), this);
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public double getPrice() {
            return price;
        }

        public Category getCategory() {
            return category;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final TestProduct testProduct = (TestProduct) o;

            return id == testProduct.id;

        }

        public int hashCode() {
            return id;
        }


        public String toString() {
            return "TestProduct{" +
                    "id='" + id + "'" +
                    ", name='" + name + "'" +
                    ", description='" + description + "'" +
                    ", price=" + price +
                    ", category=" + category +
                    "}";
        }
    }
}
