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
package org.apache.struts2.url;

import org.apache.struts2.views.util.UrlHelper;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class StrutsQueryStringBuilderTest {

    private QueryStringBuilder builder;

    @Test
    public void testBuildParametersStringWithUrlHavingSomeExistingParameters() {
        String expectedUrl = "http://localhost:8080/myContext/myPage.jsp?initParam=initValue&amp;param1=value1&amp;param2=value2&amp;param3%22%3CsCrIpT%3Ealert%281%29%3B%3C%2FsCrIpT%3E=value3";

        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("param1", "value1");
        parameters.put("param2", "value2");
        parameters.put("param3\"<sCrIpT>alert(1);</sCrIpT>", "value3");

        StringBuilder url = new StringBuilder("http://localhost:8080/myContext/myPage.jsp?initParam=initValue");

        builder.build(parameters, url, UrlHelper.AMP);

        assertEquals(expectedUrl, url.toString());
    }

    @Test
    public void testBuildParametersStringWithJavaScriptInjected() {
        String expectedUrl = "http://localhost:8080/myContext/myPage.jsp?initParam=initValue&amp;param1=value1&amp;param2=value2&amp;param3%22%3Cscript+type%3D%22text%2Fjavascript%22%3Ealert%281%29%3B%3C%2Fscript%3E=value3";

        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("param1", "value1");
        parameters.put("param2", "value2");
        parameters.put("param3\"<script type=\"text/javascript\">alert(1);</script>", "value3");

        StringBuilder url = new StringBuilder("http://localhost:8080/myContext/myPage.jsp?initParam=initValue");

        builder.build(parameters, url, UrlHelper.AMP);

        assertEquals(expectedUrl, url.toString());
    }

    @Test
    public void testBuildParametersStringWithEmptyListParameters() {
        String expectedUrl = "https://www.nowhere.com/myworld.html";
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("param1", new String[]{});
        parameters.put("param2", new ArrayList<>());
        StringBuilder url = new StringBuilder("https://www.nowhere.com/myworld.html");
        builder.build(parameters, url, UrlHelper.AMP);
        assertEquals(expectedUrl, url.toString());
    }

    @Test
    public void testBuildParametersStringWithListParameters() {
        String expectedUrl = "https://www.nowhere.com/myworld.html?param1=x&param2=y&param2=z";
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("param1", new String[]{"x"});
        parameters.put("param2", new ArrayList<String>() {
            {
                add("y");
                add("z");
            }
        });
        StringBuilder url = new StringBuilder("https://www.nowhere.com/myworld.html");
        builder.build(parameters, url, "&");
        assertEquals(expectedUrl, url.toString());
    }

    @Before
    public void setUp() throws Exception {
        builder = new StrutsQueryStringBuilder(new StrutsUrlEncoder());
    }

}
