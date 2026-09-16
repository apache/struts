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

import org.apache.struts2.ActionProxy;
import org.apache.struts2.junit.StrutsRestTestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrdersControllerRestTestCaseTest extends StrutsRestTestCase<OrdersController> {

    @Test
    public void showBindsTheIdFromTheRequestPath() throws Exception {
        ActionProxy proxy = getActionProxy("GET", "/orders/3");

        assertEquals("show", proxy.getMethod());
        proxy.execute();

        OrdersController controller = (OrdersController) proxy.getAction();
        assertEquals("Bob", ((Order) controller.getModel()).getClientName());
    }

    @Test
    public void executeActionRendersTheOrderAsJson() throws Exception {
        String json = executeAction("GET", "/orders/3.json");

        assertTrue(json, json.contains("\"clientName\":\"Bob\""));
    }
}
