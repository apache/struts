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
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.webjars.WebJarUrlProvider;

import java.io.IOException;
import java.io.Writer;
import java.util.Optional;

/**
 * <p>Resolves a version-less WebJar resource path to a servable URL and writes it to the output
 * (or stores it in a variable when {@code var} is set). Compose it with {@code <s:script>}/{@code <s:link>}
 * or a raw {@code <link>}/{@code <script>} element.</p>
 *
 * <b>Examples</b>
 * <pre>
 *   &lt;link rel="stylesheet" href="&lt;s:webjar path="bootstrap/css/bootstrap.min.css" /&gt;" /&gt;
 *   &lt;@s.webjar path="jquery/jquery.min.js"/&gt;
 * </pre>
 */
@StrutsTag(
    name = "webjar",
    tldTagClass = "org.apache.struts2.views.jsp.WebJarTag",
    description = "Resolve a version-less WebJar resource path to a servable URL")
public class WebJar extends ContextBean {

    private static final Logger LOG = LogManager.getLogger(WebJar.class);

    protected String path;

    private final HttpServletRequest request;
    private WebJarUrlProvider webJarUrlProvider;

    public WebJar(ValueStack stack, HttpServletRequest request) {
        super(stack);
        this.request = request;
    }

    @Inject
    public void setWebJarUrlProvider(WebJarUrlProvider webJarUrlProvider) {
        this.webJarUrlProvider = webJarUrlProvider;
    }

    @Override
    public boolean end(Writer writer, String body) {
        String logicalPath = findString(path);
        Optional<String> url = (logicalPath == null)
            ? Optional.empty()
            : webJarUrlProvider.resolveUrl(logicalPath, request);

        if (url.isPresent()) {
            if (StringUtils.isNotBlank(getVar())) {
                putInContext(url.get());
            } else {
                try {
                    writer.write(url.get());
                } catch (IOException e) {
                    LOG.error("Could not write WebJar URL for path '{}'", path, e);
                }
            }
        }
        return super.end(writer, body);
    }

    @StrutsTagAttribute(required = true,
        description = "The version-less WebJar resource path, e.g. bootstrap/css/bootstrap.min.css")
    public void setPath(String path) {
        this.path = path;
    }
}
