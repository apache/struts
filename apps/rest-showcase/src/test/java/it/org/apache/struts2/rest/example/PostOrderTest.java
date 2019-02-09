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

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import net.sourceforge.jwebunit.junit.WebTestCase;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class PostOrderTest extends WebTestCase {

    public void setUp() throws Exception {
        getTestContext().setBaseUrl(ParameterUtils.getBaseUrl());
    }


    public void testPostOrder() {
        beginAt("/orders/new");
        setWorkingForm(0);
        setTextField("clientName", "Test1");
        setTextField("amount", "321");
        submit();
        assertTextPresent("Test1");
        assertLinkNotPresentWithText("Back to Orders");
    }

    public void testPostOrderWithErrors() {
        beginAt("/orders/new");
        setWorkingForm(0);
        setTextField("amount", "321");
        try {
            submit();
        } catch (FailingHttpStatusCodeException ex) {
            // ignore;
        }
        assertTextPresent("client name is empty");
        assertTextFieldEquals("amount", "321");
    }

    public void testPostOrderInHtml() {
        beginAt("/orders/new.xhtml");
        setWorkingForm(0);
        setTextField("clientName", "Test2");
        setTextField("amount", "321");
        try {
            submit();
        } catch (FailingHttpStatusCodeException ex) {
            // ignore;
        }
        assertTextPresent("Test2");
        assertLinkNotPresentWithText("Back to Orders");
    }

    public void testPostOrderInXml() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(ParameterUtils.getBaseUrl() + "/orders.xml");
        httpPost.setEntity(new StringEntity("<org.apache.struts2.rest.example.Order>\n" +
                "<clientName>Test3</clientName>\n" +
                "<amount>3342</amount>\n" +
                "</org.apache.struts2.rest.example.Order>"));
        CloseableHttpResponse response = client.execute(httpPost);
        assertEquals(201, response.getStatusLine().getStatusCode());
        assertTrue(response.getHeaders("Location")[0].getValue().startsWith(ParameterUtils.getBaseUrl() + "/orders/"));
        client.close();
    }

    public void testPostOrderInXmlWithBadData() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(ParameterUtils.getBaseUrl() + "/orders.xml");
        httpPost.setEntity(new StringEntity("<org.apache.struts2.rest.example.Order>\n" +
                "<amount>3342</amount>\n" +
                "</org.apache.struts2.rest.example.Order>"));
        CloseableHttpResponse response = client.execute(httpPost);
        assertEquals(400, response.getStatusLine().getStatusCode());
        assertTrue(response.toString().contains("<string>The client name is empty"));
        assertNull(response.getHeaders("Location"));
        client.close();
    }

    public void testPostOrderInJson() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(ParameterUtils.getBaseUrl() + "/orders.json");
        httpPost.setEntity(new StringEntity("{\"amount\":33,\"clientName\":\"Test4\"}"));
        CloseableHttpResponse response = client.execute(httpPost);
        assertEquals(201, response.getStatusLine().getStatusCode());
        assertTrue(response.getHeaders("Location")[0].getValue().startsWith(ParameterUtils.getBaseUrl() + "/orders/"));
        client.close();
    }

    public void testPostOrderInJsonWithBadData() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(ParameterUtils.getBaseUrl() + "/orders.json");
        httpPost.setEntity(new StringEntity("{\"amount\":33}"));
        CloseableHttpResponse response = client.execute(httpPost);
        assertEquals(400, response.getStatusLine().getStatusCode());
        assertTrue(response.toString()
                .contains("{\"actionErrors\":[],\"fieldErrors\":{\"clientName\":[\"The client name is empty\"]}}"));
        assertNull(response.getHeaders("Location"));
        client.close();
    }
}