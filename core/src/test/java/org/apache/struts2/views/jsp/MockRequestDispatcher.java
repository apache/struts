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
package org.apache.struts2.views.jsp;

import com.mockobjects.ExpectationValue;
import com.mockobjects.MockObject;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

public class MockRequestDispatcher  extends MockObject implements RequestDispatcher {
    private final ExpectationValue myRequest = new ExpectationValue("request");
    private final ExpectationValue myResponse = new ExpectationValue("response");

    public MockRequestDispatcher() {
    }

    public void setExpectedRequest(ServletRequest aRequest) {
        this.myRequest.setExpected(aRequest);
    }

    public void setExpectedResponse(ServletResponse aResponse) {
        this.myResponse.setExpected(aResponse);
    }

    public void forward(ServletRequest aRequest, ServletResponse aResponse) throws ServletException, IOException {
        this.myRequest.setActual(aRequest);
        this.myResponse.setActual(aResponse);
    }

    public void include(ServletRequest aRequest, ServletResponse aResponse) throws ServletException, IOException {
        this.myRequest.setActual(aRequest);
        this.myResponse.setActual(aResponse);
    }
}

