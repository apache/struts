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
import com.opensymphony.xwork2.ObjectFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.tiles.preparer.ViewPreparer;
import org.apache.tiles.preparer.factory.BasicPreparerFactory;

/**
 * This is a basic ViewPreparer factory that uses {@link ObjectFactory} to create the ViewPreparer
 */
public class StrutsPreparerFactory extends BasicPreparerFactory {

    private static final Logger LOG = LogManager.getLogger(StrutsPreparerFactory.class);

    @Override
    protected ViewPreparer createPreparer(String name) {
        ActionContext actionContext = ActionContext.getContext();
        if (actionContext == null) {
            LOG.warn("Action context not initialised, request has omitted an action? Fallback to super.createPreparer!");
            return super.createPreparer(name);
        }

        try {
            ObjectFactory factory = actionContext.getContainer().getInstance(ObjectFactory.class);
            return (ViewPreparer) factory.buildBean(name, ActionContext.getContext().getContextMap());
        } catch (Exception e) {
            LOG.error(new ParameterizedMessage("Cannot create a ViewPreparer [{}], fallback to super.createPreparer!", name), e);
            return super.createPreparer(name);
        }
    }
}
