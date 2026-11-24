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
package org.apache.struts2.components;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.webjars.StrutsWebJarUrlProvider;
import org.apache.struts2.webjars.WebJarUrlProvider;
import org.junit.Test;

import java.io.StringWriter;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebJarTest {

    private WebJar newComponent(ValueStack stack, HttpServletRequest request, WebJarUrlProvider provider) {
        Map<String, Object> context = new HashMap<>();
        when(stack.getContext()).thenReturn(context);
        WebJar webJar = new WebJar(stack, request);
        webJar.setWebJarUrlProvider(provider);
        return webJar;
    }

    @Test
    public void writesResolvedUrl() {
        ValueStack stack = mock(ValueStack.class);
        when(stack.findString("jquery/jquery.min.js")).thenReturn("jquery/jquery.min.js");
        HttpServletRequest request = mock(HttpServletRequest.class);
        WebJarUrlProvider provider = mock(WebJarUrlProvider.class);
        when(provider.resolveUrl("jquery/jquery.min.js", request))
            .thenReturn(Optional.of("/myapp/static/webjars/jquery/3.7.1/jquery.min.js"));

        WebJar webJar = newComponent(stack, request, provider);
        webJar.setPath("jquery/jquery.min.js");
        StringWriter writer = new StringWriter();

        webJar.start(writer);
        webJar.end(writer, "");

        assertThat(writer).hasToString("/myapp/static/webjars/jquery/3.7.1/jquery.min.js");
    }

    @Test
    public void unresolvedPathWritesNothing() {
        ValueStack stack = mock(ValueStack.class);
        when(stack.findString("nope/x.js")).thenReturn("nope/x.js");
        HttpServletRequest request = mock(HttpServletRequest.class);
        WebJarUrlProvider provider = mock(WebJarUrlProvider.class);
        when(provider.resolveUrl("nope/x.js", request)).thenReturn(Optional.empty());

        WebJar webJar = newComponent(stack, request, provider);
        webJar.setPath("nope/x.js");
        StringWriter writer = new StringWriter();

        webJar.start(writer);
        webJar.end(writer, "");

        assertThat(writer.toString()).isEmpty();
    }

    @Test
    public void writesEncodedUrlFromRealProvider() {
        ValueStack stack = mock(ValueStack.class);
        String logicalPath = "jquery/dir one/a\"?#%+\u65b0\u805e.js";
        when(stack.findString(logicalPath)).thenReturn(logicalPath);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getContextPath()).thenReturn("/myapp");
        WebJar webJar = newComponent(stack, request, new StrutsWebJarUrlProvider());
        webJar.setPath(logicalPath);
        StringWriter writer = new StringWriter();

        webJar.start(writer);
        webJar.end(writer, "");

        assertThat(writer.toString()).startsWith("/myapp/static/webjars/jquery/")
            .endsWith("/dir%20one/a%22%3F%23%25+%E6%96%B0%E8%81%9E.js");
    }

    @Test
    public void storesEncodedUrlInVarWithoutWritingOrEncodingItAgain() {
        ValueStack stack = mock(ValueStack.class);
        String logicalPath = "jquery/a b%20.js";
        when(stack.findString(logicalPath)).thenReturn(logicalPath);
        when(stack.findString("assetUrl")).thenReturn("assetUrl");
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getContextPath()).thenReturn("/myapp");
        WebJar webJar = newComponent(stack, request, new StrutsWebJarUrlProvider());
        webJar.setPath(logicalPath);
        webJar.setVar("assetUrl");
        StringWriter writer = new StringWriter();

        webJar.start(writer);
        webJar.end(writer, "");

        assertThat(writer.toString()).isEmpty();
        assertThat(stack.getContext().get("assetUrl")).asString()
            .startsWith("/myapp/static/webjars/jquery/")
            .endsWith("/a%20b%2520.js");
    }

    @Test
    public void realProviderOutputFitsInDoubleQuotedAttribute() throws Exception {
        ValueStack stack = mock(ValueStack.class);
        String logicalPath = "jquery/a\" b.js";
        when(stack.findString(logicalPath)).thenReturn(logicalPath);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getContextPath()).thenReturn("/myapp");
        WebJar webJar = newComponent(stack, request, new StrutsWebJarUrlProvider());
        webJar.setPath(logicalPath);
        StringWriter writer = new StringWriter();
        writer.write("<script src=\"");
        webJar.start(writer);
        webJar.end(writer, "");
        writer.write("\"></script>");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        var document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(writer.toString())));
        var element = document.getDocumentElement();
        assertThat(element.getTagName()).isEqualTo("script");
        assertThat(element.getAttributes().getLength()).isEqualTo(1);
        assertThat(element.getAttribute("src")).startsWith("/myapp/static/webjars/jquery/")
            .endsWith("/a%22%20b.js");
        assertThat(element.getChildNodes().getLength()).isZero();
    }
}
