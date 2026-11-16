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
package org.apache.struts2.boot.fixture;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.struts2.action.ServletResponseAware;

/**
 * Adds two cookies of the application's own, one plain and one with its own {@code SameSite}
 * attribute, so a test can tell which cookie attributes the container adds to them.
 */
public class CookieProbeAction implements ServletResponseAware {

    public static final String PLAIN_COOKIE = "plainProbe";
    public static final String OWN_SAME_SITE_COOKIE = "ownSameSiteProbe";

    private HttpServletResponse response;

    public String execute() {
        response.addCookie(new Cookie(PLAIN_COOKIE, "1"));
        Cookie ownSameSite = new Cookie(OWN_SAME_SITE_COOKIE, "1");
        ownSameSite.setAttribute("SameSite", "Strict");
        response.addCookie(ownSameSite);
        return "success";
    }

    @Override
    public void withServletResponse(HttpServletResponse response) {
        this.response = response;
    }
}
