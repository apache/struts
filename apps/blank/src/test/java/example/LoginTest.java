/*
 * $Id$
 *
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

package example;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.config.entities.ActionConfig;

import java.util.Map;

public class LoginTest extends ConfigTest {

    public void FIXME_testLoginConfig() throws Exception {
        ActionConfig config = assertClass("example", "Login_input", "example.Login");
        assertResult(config, ActionSupport.SUCCESS, "Menu");
        assertResult(config, ActionSupport.INPUT, "/example/Login.jsp");
    }

    public void testLoginSubmit() throws Exception {
        Login login = new Login();
        login.setUsername("username");
        login.setPassword("password");
        String result = login.execute();
        assertSuccess(result);
    }

    // Needs access to an envinronment that includes validators
    public void FIXME_testLoginSubmitInput() throws Exception {
        Login login = new Login();
        String result = login.execute();
        assertInput(result);
        Map errors = assertFieldErrors(login);
        assertFieldError(errors,"username","Username is required.");
        assertFieldError(errors,"password","Password is required.");
    }

}
