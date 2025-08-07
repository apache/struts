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
package org.apache.struts2.result;

import org.apache.struts2.ActionContext;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.ActionProxy;
import org.apache.struts2.ActionProxyFactory;
import org.apache.struts2.StrutsException;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.ognl.StrutsContext;
import org.apache.struts2.util.TextParseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
* <!-- START SNIPPET: description -->
*
* This result invokes an entire other action, complete with it's own interceptor stack and result.
*
* <!-- END SNIPPET: description -->
*
* <b>This result type takes the following parameters:</b>
*
* <!-- START SNIPPET: params -->
*
* <ul>
*
* <li><b>actionName (default)</b> - the name of the action that will be chained to</li>
*
* <li><b>namespace</b> - used to determine which namespace the Action is in that we're chaining. If namespace is null,
* this defaults to the current namespace</li>
*
* <li><b>method</b> - used to specify another method on target action to be invoked.
* If null, this defaults to execute method</li>
*
* <li><b>skipActions</b> - (optional) the list of comma separated action names for the
* actions that could be chained to</li>
*
* </ul>
*
* <!-- END SNIPPET: params -->
*
* <b>Example:</b>
*
* <pre><!-- START SNIPPET: example -->
* &lt;package name="public" extends="struts-default"&gt;
*     &lt;!-- Chain creatAccount to login, using the default parameter --&gt;
*     &lt;action name="createAccount" class="..."&gt;
*         &lt;result type="chain"&gt;login&lt;/result&gt;
*     &lt;/action&gt;
*
*     &lt;action name="login" class="..."&gt;
*         &lt;!-- Chain to another namespace --&gt;
*         &lt;result type="chain"&gt;
*             &lt;param name="actionName"&gt;dashboard&lt;/param&gt;
*             &lt;param name="namespace"&gt;/secure&lt;/param&gt;
*         &lt;/result&gt;
*     &lt;/action&gt;
* &lt;/package&gt;
*
* &lt;package name="secure" extends="struts-default" namespace="/secure"&gt;
*     &lt;action name="dashboard" class="..."&gt;
*         &lt;result&gt;dashboard.jsp&lt;/result&gt;
*     &lt;/action&gt;
* &lt;/package&gt;
* <!-- END SNIPPET: example --></pre>
*
* @author <a href='mailto:the_mindstorm[at]evolva[dot]ro'>Alexandru Popescu</a>
*/
public class ActionChainResult implements Result {

    private static final Logger LOG = LogManager.getLogger(ActionChainResult.class);

    /**
     * The result parameter name to set the name of the action to chain to.
     */
    public static final String DEFAULT_PARAM = "actionName";

    /**
     * The action context key to save the chain history.
     */
    private static final String CHAIN_HISTORY = "CHAIN_HISTORY";

    private ActionProxy proxy;
    private String actionName;

    private String namespace;

    private String methodName;

    /**
     * The list of actions to skip.
     */
    private String skipActions;

    private ActionProxyFactory actionProxyFactory;

    public ActionChainResult() {
        super();
    }

    public ActionChainResult(String namespace, String actionName, String methodName) {
        this.namespace = namespace;
        this.actionName = actionName;
        this.methodName = methodName;
    }

    public ActionChainResult(String namespace, String actionName, String methodName, String skipActions) {
        this.namespace = namespace;
        this.actionName = actionName;
        this.methodName = methodName;
        this.skipActions = skipActions;
    }

    /**
     * @param actionProxyFactory the actionProxyFactory to set
     */
    @Inject
    public void setActionProxyFactory(ActionProxyFactory actionProxyFactory) {
        this.actionProxyFactory = actionProxyFactory;
    }

    /**
     * Set the action name.
     *
     * @param actionName The action name.
     */
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    /**
     * sets the namespace of the Action that we're chaining to.  if namespace
     * is null, this defaults to the current namespace.
     *
     * @param namespace the name of the namespace we're chaining to
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Set the list of actions to skip.
     * To test if an action should not throe an infinite recursion,
     * only the action name is used, not the namespace.
     *
     * @param actions The list of action name separated by a white space.
     */
    public void setSkipActions(String actions) {
        this.skipActions = actions;
    }

    public void setMethod(String method) {
        this.methodName = method;
    }

    public ActionProxy getProxy() {
        return proxy;
    }

    /**
     * Get the XWork chain history.
     * The stack is a list of <code>namespace/action!method</code> keys.
     *
     * @return the chain history as string list
     */
    public static LinkedList<String> getChainHistory() {
        LinkedList<String> chainHistory = (LinkedList<String>) ActionContext.getContext().get(CHAIN_HISTORY);
        //  Add if not exists
        if (chainHistory == null) {
            chainHistory = new LinkedList<>();
            ActionContext.getContext().put(CHAIN_HISTORY, chainHistory);
        }

        return chainHistory;
    }

    /**
     * @param invocation the DefaultActionInvocation calling the action call stack
     */
    public void execute(ActionInvocation invocation) throws Exception {
        if (invocation == null) {
            throw new IllegalArgumentException("Invocation cannot be null!");
        }

        String finalNamespace = namespace != null ? translateVariables(namespace) : invocation.getProxy()
                .getNamespace();
        String finalActionName = translateVariables(actionName);
        String finalMethodName = methodName != null ? translateVariables(methodName) : null;

        if (isInChainHistory(finalNamespace, finalActionName, finalMethodName)) {
            addToHistory(finalNamespace, finalActionName, finalMethodName);
            throw new StrutsException("Infinite recursion detected: " + ActionChainResult.getChainHistory());
        }

        if (ActionChainResult.getChainHistory().isEmpty() && invocation.getProxy() != null) {
            addToHistory(finalNamespace, invocation.getProxy().getActionName(), invocation.getProxy().getMethod());
        }
        addToHistory(finalNamespace, finalActionName, finalMethodName);

        StrutsContext extraContext = ActionContext.of()
            .withValueStack(invocation.getInvocationContext().getValueStack())
            .withParameters(invocation.getInvocationContext().getParameters())
            .with(CHAIN_HISTORY, ActionChainResult.getChainHistory())
            .getStrutsContext();

        LOG.debug("Chaining to action {}", finalActionName);

        proxy = actionProxyFactory.createActionProxy(finalNamespace, finalActionName, finalMethodName, extraContext);
        proxy.execute();
    }

    protected String translateVariables(String text) {
        return TextParseUtil.translateVariables(text, ActionContext.getContext().getValueStack());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ActionChainResult that = (ActionChainResult) o;
        return Objects.equals(actionName, that.actionName) && Objects.equals(methodName,
                that.methodName) && Objects.equals(namespace, that.namespace);
    }

    @Override
    public int hashCode() {
        int result;
        result = (actionName != null ? actionName.hashCode() : 0);
        result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
        return result;
    }

    private boolean isInChainHistory(String namespace, String actionName, String methodName) {
        LinkedList<? extends String> chainHistory = ActionChainResult.getChainHistory();
        Set<String> skipActionsList = new HashSet<>();
        if (skipActions != null && !skipActions.isEmpty()) {
            String finalSkipActions = translateVariables(skipActions);
            skipActionsList.addAll(TextParseUtil.commaDelimitedStringToSet(finalSkipActions));
        }
        if (!skipActionsList.contains(actionName)) {
            return chainHistory.contains(makeKey(namespace, actionName, methodName));
        }
        return false;
    }

    private void addToHistory(String namespace, String actionName, String methodName) {
        List<String> chainHistory = ActionChainResult.getChainHistory();
        chainHistory.add(makeKey(namespace, actionName, methodName));
    }

    private String makeKey(String namespace, String actionName, String methodName) {
        return namespace + "/" + actionName + (methodName != null ? "!" + methodName : "");
    }
}
