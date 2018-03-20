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
package org.apache.struts2.factory;

import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.DefaultActionProxyFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Initializable;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <!-- START SNIPPET: description -->
 * <p>
 * Prefix based factory should be used with {@link org.apache.struts2.dispatcher.mapper.PrefixBasedActionMapper}
 * to use appropriate {@link com.opensymphony.xwork2.ActionProxyFactory} connected with given
 * {@link org.apache.struts2.dispatcher.mapper.ActionMapper}
 * </p>
 *
 * <p>
 * Add below entry to struts.xml to enable the factory:
 * </p>
 *
 * <pre>
 * &lt;constant name="struts.actionProxyFactory" value="prefix"/&gt;
 * </pre>
 *
 * <p>
 * The factory will use the same set of patterns as defined with:
 * </p>
 *
 * <pre>
 * &lt;constant name="struts.mapper.prefixMapping" value="..."/&gt;
 * </pre>
 * <!-- END SNIPPET: description -->
 */
public class PrefixBasedActionProxyFactory extends StrutsActionProxyFactory implements Initializable {

    private static final Logger LOG = LogManager.getLogger(PrefixBasedActionProxyFactory.class);

    private Map<String, ActionProxyFactory> actionProxyFactories = new HashMap<>();
    private Set<String> prefixes = new HashSet<>();

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    @Inject(StrutsConstants.PREFIX_BASED_MAPPER_CONFIGURATION)
    public void setPrefixBasedActionProxyFactories(String list) {
        if (list != null) {
            prefixes = new HashSet<>(Arrays.asList(list.split(",")));
        }
    }

    @Override
    public void init() {
        for (String factory : prefixes) {
            String[] thisFactory = factory.split(":");
            if (thisFactory.length == 2) {
                String factoryPrefix = thisFactory[0].trim();
                String factoryName = thisFactory[1].trim();
                ActionProxyFactory obj = container.getInstance(ActionProxyFactory.class, factoryName);
                if (obj != null) {
                    actionProxyFactories.put(factoryPrefix, obj);
                } else {
                    LOG.warn("Invalid PrefixBasedActionProxyFactory config entry: [{}]", factory);
                }
            }
        }
    }

    public ActionProxy createActionProxy(String namespace, String actionName, String methodName,
                                         Map<String, Object> extraContext, boolean executeResult, boolean cleanupContext) {

        String uri = namespace + (namespace.endsWith("/") ? actionName : "/" + actionName);
        for (int lastIndex = uri.lastIndexOf('/'); lastIndex > (-1); lastIndex = uri.lastIndexOf('/', lastIndex - 1)) {
            String key = uri.substring(0, lastIndex);
            ActionProxyFactory actionProxyFactory = actionProxyFactories.get(key);
            if (actionProxyFactory != null) {
                LOG.debug("Using ActionProxyFactory [{}] for prefix [{}]", actionProxyFactory, key);
                return actionProxyFactory.createActionProxy(namespace, actionName, methodName, extraContext, executeResult, cleanupContext);
            } else {
                LOG.debug("No ActionProxyFactory defined for [{}]", key);
            }
        }
        LOG.debug("Cannot find any matching ActionProxyFactory, falling back to [{}]", super.getClass().getName());
        return super.createActionProxy(namespace, actionName, methodName, extraContext, executeResult, cleanupContext);
    }
}
