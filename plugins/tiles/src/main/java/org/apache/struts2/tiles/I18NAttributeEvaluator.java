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
package org.apache.struts2.tiles;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.tiles.evaluator.AbstractAttributeEvaluator;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.servlet.ServletUtil;

import javax.servlet.http.HttpServletRequest;

public class I18NAttributeEvaluator extends AbstractAttributeEvaluator {

    private static final Logger LOG = LogManager.getLogger(I18NAttributeEvaluator.class);

    @Override
    public Object evaluate(String expression, Request request) {
        Object result = expression;

        HttpServletRequest httpRequest = ServletUtil.getServletRequest(request).getRequest();
        ActionContext ctx = ServletActionContext.getActionContext(httpRequest);

        if (ctx == null) {
            LOG.error("Cannot obtain HttpServletRequest from [{}]", request.getClass().getName());
            throw new ConfigurationException("There is no ActionContext for current request!");
        }

        TextProviderFactory tpf = ctx.getContainer().getInstance(TextProviderFactory.class);
        TextProvider textProvider = tpf.createInstance(ctx.getActionInvocation().getAction().getClass());

        if (textProvider != null) {
            LOG.debug("Trying find text [{}] using TextProvider {}", expression, textProvider);
            result = textProvider.getText(expression);
        }
        return result;
    }

}
