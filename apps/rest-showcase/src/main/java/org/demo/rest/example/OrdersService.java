/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.demo.rest.example;

import java.util.*;

public class OrdersService {

    private static Map<String,Order> orders = new HashMap<String,Order>();
    private static int nextId = 6;
    static {
        orders.put("3", new Order("3", "Bob", 33));
        orders.put("4", new Order("4", "Sarah", 44));
        orders.put("5", new Order("5", "Jim", 66));
    }

    public Order get(String id) {
        return orders.get(id);
    }

    public List<Order> getAll() {
        return new ArrayList<Order>(orders.values());
    }

    public void save(Order order) {
        if (order.getId() == null) {
            order.setId(String.valueOf(nextId++));
        }

        orders.put(order.getId(), order);
    }

    public void remove(String id) {
        orders.remove(id);
    }

}
