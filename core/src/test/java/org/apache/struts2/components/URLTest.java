/*
 * $Id: pom.xml 560558 2007-07-28 15:47:10Z apetrelli $
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
package org.apache.struts2.components;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.views.jsp.AbstractTagTest;

/**
 * Verifies correct operation of parameter merging.
 * 
 * Contributed by: Daniel Uribe
 */
public class URLTest extends AbstractTagTest {
    public void testIncludeGetDuplicateRequestParams() throws Exception {
        String body = "";

        Map parameterMap = new HashMap();
        parameterMap.put("param", new String[] { "1", "2", "3" });

        request.setQueryString("param=1&param=2&param=3");
        request.setScheme("http");
        request.setParameterMap(parameterMap);
        URL url = new URL(stack, request, response);
        url.setIncludeParams(URL.GET);
        url.setIncludeContext(false);
        url.setValue("myAction.action");
        url.setNamespace("");

        url.start(writer);
        url.end(writer, body);

        assertEquals("myAction.action?param=1&amp;param=2&amp;param=3",
            writer.toString());
    }

    public void testIncludeAllDuplicateRequestParams() throws Exception {
        String body = "";

        Map parameterMap = new HashMap();
        parameterMap.put("param", new String[] { "1", "2", "3" });

        request.setQueryString("param=1&param=2&param=3");
        request.setScheme("http");
        request.setParameterMap(parameterMap);
        URL url = new URL(stack, request, response);
        url.setIncludeParams(URL.ALL);
        url.setIncludeContext(false);
        url.setValue("myAction.action");
        url.setNamespace("");

        url.start(writer);
        url.end(writer, body);

        assertEquals("myAction.action?param=1&amp;param=2&amp;param=3",
            writer.toString());
    }
}
