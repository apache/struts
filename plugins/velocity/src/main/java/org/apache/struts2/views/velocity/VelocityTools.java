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
package org.apache.struts2.views.velocity;

import org.apache.struts2.ActionContext;
import jakarta.servlet.ServletContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.ToolManager;
import org.apache.velocity.tools.ToolboxFactory;

import static org.apache.commons.lang3.ObjectUtils.requireNonEmpty;

/**
 * @since 7.0
 */
public class VelocityTools {

    private final String toolLocation;
    private ToolManager toolManager;
    private VelocityEngine velocityEngine;

    public VelocityTools(String toolLocation) {
        this.toolLocation = requireNonEmpty(toolLocation);
    }

    public void init(ServletContext servletContext, VelocityEngine velocityEngine) {
        this.toolManager = new ToolManager();
        this.toolManager.configure(toolLocation);
        this.velocityEngine = velocityEngine;
    }

    public Context createContext() {
        if (toolManager == null || velocityEngine == null ||
                ActionContext.getContext() == null || ActionContext.getContext().getServletContext() == null) {
            return null;
        }
        var toolContext = new ToolContext(velocityEngine);
        toolContext.addToolbox(toolManager.getToolboxFactory().createToolbox(ToolboxFactory.DEFAULT_SCOPE));
        return toolContext;
    }
}
