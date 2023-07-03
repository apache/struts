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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.util.AttributeMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.PageContext;
import java.util.Map;

public class TagUtils {

    private static final Logger LOG = LogManager.getLogger(TagUtils.class);

    public static ValueStack getStack(PageContext pageContext) {
        LOG.trace("Reading ValueStack out of page context: {}", pageContext);
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        ValueStack stack = ServletActionContext.getValueStack(req);
        stack = stack != null ? stack : ActionContext.getContext().getValueStack();

        if (stack == null) {
            LOG.warn("No ValueStack in ActionContext!");
            throw new ConfigurationException("Rendering tag out of Action scope, accessing directly JSPs is not recommended! " +
                "Please read https://struts.apache.org/security/#never-expose-jsp-files-directly");
        } else {
            LOG.trace("Adds the current PageContext to ActionContext");
            AttributeMap attrMap = new AttributeMap(stack.getContext());

            stack.getActionContext()
                .withPageContext(pageContext)
                .with("attr", attrMap);
        }

        return stack;
    }

}
