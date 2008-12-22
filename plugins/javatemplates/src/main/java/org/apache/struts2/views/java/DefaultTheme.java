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
package org.apache.struts2.views.java;

import org.apache.struts2.StrutsException;
import org.apache.struts2.components.template.TemplateRenderingContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

/**
 * Default implementation of the theme
 */
public class DefaultTheme implements Theme {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultTheme.class);

    private String name;
    private Map<String, List<TagHandlerFactory>> handlerFactories;

    protected void setName(String name) {
        this.name = name;
    }

    protected void setHandlerFactories(Map<String, List<TagHandlerFactory>> handlers) {
        this.handlerFactories = handlers;
    }

    public String getName() {
        return name;
    }

    public void renderTag(String tagName, TemplateRenderingContext context) {
        if (tagName.endsWith(".java")) {
            tagName = tagName.substring(0, tagName.length() - ".java".length());
        }

        List<TagHandler> handlers = new ArrayList<TagHandler>();
        List<TagHandlerFactory> factories = handlerFactories.get(tagName);
        if (factories == null) {
            throw new StrutsException("Unable to find handlers for tag " + tagName);
        }

        TagHandler prev = null;
        for (int x = factories.size() - 1; x >= 0; x--) {
            prev = factories.get(x).create(prev);
            prev.setup(context);
            handlers.add(0, prev);
        }

        TagSerializer ser = (TagSerializer) handlers.get(handlers.size() - 1);

        TagGenerator gen = (TagGenerator) handlers.get(0);
        try {
            if (LOG.isTraceEnabled())
                LOG.trace("Rendering tag [#0]", tagName);
            gen.generate();
        } catch (IOException ex) {
            throw new StrutsException("Unable to write tag: " + tagName);
        }
    }

}
