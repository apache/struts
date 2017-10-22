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
package it.org.apache.struts2.rest.example;

import net.sourceforge.jwebunit.junit.WebTestCase;

public class ListOrdersTest extends WebTestCase {

    public void setUp() throws Exception {
        getTestContext().setBaseUrl(ParameterUtils.getBaseUrl());
    }


    public void testListOrders() {
        beginAt("/orders");
        assertTextPresent("Bob");
        assertTextPresent("Sarah");
        assertTextPresent("Jim");
    }

    public void testListOrdersInHtml() {
        beginAt("/orders.xhtml");
        assertTextPresent("Bob");
        assertTextPresent("Sarah");
        assertTextPresent("Jim");
    }

    public void testListOrdersInXml() {
        beginAt("/orders.xml");
        assertTextPresent("<clientName>Bob");
        assertTextPresent("<clientName>Sarah");
        assertTextPresent("<clientName>Jim");
    }

    public void testListOrdersInJson() {
        beginAt("/orders.json");
        assertTextPresent("\"clientName\":\"Bob\"");
        assertTextPresent("\"clientName\":\"Sarah\"");
        assertTextPresent("\"clientName\":\"Jim\"");
    }
}
