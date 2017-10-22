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
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.HttpParameters;

import org.apache.struts2.result.ServletRedirectResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <!-- START SNIPPET: description -->
 * <p>
 * An interceptor to store a {@link ValidationAware} action's messages / errors and field errors into
 * HTTP Session, such that it will be retrievable at a later stage. This allows the action's message /
 * errors and field errors to be available longer that just the particular HTTP request.
 * </p>
 *
 * <p>
 * If no session exists, nothing will be stored and can be retrieved later. In other terms,
 * the application is responsible to open the session.
 * </p>
 *
 * <p>
 * In the 'STORE' mode, the interceptor will store the {@link ValidationAware} action's message / errors
 * and field errors into HTTP session.
 * </p>
 *
 * <p>
 * In the 'RETRIEVE' mode, the interceptor will retrieve the stored action's message / errors  and field
 * errors and put them back into the {@link ValidationAware} action.
 * </p>
 *
 * <p>
 * In the 'AUTOMATIC' mode, the interceptor will always retrieve the stored action's message / errors
 * and field errors and put them back into the {@link ValidationAware} action, and after Action execution, 
 * if the {@link com.opensymphony.xwork2.Result} is an instance of {@link ServletRedirectResult}, the action's message / errors
 * and field errors into automatically be stored in the HTTP session..
 * </p>
 *
 * <p>
 * The interceptor does nothing in the 'NONE' mode, which is the default.
 * </p>
 *
 * <p>
 * The operation mode could be switched using:<br>
 * 1] Setting the interceptor parameter eg.
 * </p>
 * <pre>
 *   &lt;action name="submitApplication" ...&gt;
 *      &lt;interceptor-ref name="store"&gt;
 *         &lt;param name="operationMode"&gt;STORE&lt;/param&gt;
 *      &lt;/interceptor-ref&gt;
 *      &lt;interceptor-ref name="defaultStack" /&gt;
 *      ....
 *   &lt;/action&gt;
 * </pre>
 *
 * <p>
 * 2] Through request parameter (allowRequestParameterSwitch must be 'true' which is the default)
 * </p>
 *
 * <pre>
 *   // the request will have the operation mode in 'STORE'
 *   http://localhost:8080/context/submitApplication.action?operationMode=STORE
 * </pre>
 *
 * <!-- END SNIPPET: description -->
 *
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *      <li>allowRequestParameterSwitch - To enable request parameter that could switch the operation mode
 *                                        of this interceptor. </li>
 *      <li>requestParameterSwitch - The request parameter that will indicate what mode this
 *                                   interceptor is in. </li>
 *      <li>operationMode - The operation mode this interceptor should be in
 *                          (either 'STORE', 'RETRIEVE', 'AUTOMATIC', or 'NONE'). 'NONE' being the default.</li>
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 *
 * <!-- START SNIPPET: extending -->
 * <p>
 * The following method could be overridden:
 * </p>
 *
 * <ul>
 *  <li>getRequestOperationMode - get the operation mode of this interceptor based on the request parameters</li>
 *  <li>mergeCollection - merge two collections</li>
 *  <li>mergeMap - merge two map</li>
 * </ul>
 *
 * <!-- END SNIPPET: extending -->
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;action name="submitApplication" ....&gt;
 *    &lt;interceptor-ref name="store"&gt;
 *      &lt;param name="operationMode"&gt;aSTORE&lt;/param&gt;
 *    &lt;/interceptor-ref&gt;
 *    &lt;interceptor-ref name="defaultStack" /&gt;
 *    &lt;result name="input" type="redirect"&gt;aapplicationFailed.action&lt;/result&gt;
 *    &lt;result type="dispatcher"&gt;applicationSuccess.jsp&lt;/result&gt;
 * &lt;/action&gt;
 *
 * &lt;action name="applicationFailed" ....&gt;
 *    &lt;interceptor-ref name="store"&gt;
 *       &lt;param name="operationMode"&gt;RETRIEVE&lt;/param&gt;
 *    &lt;/interceptor-ref&gt;
 *    &lt;result&gt;applicationFailed.jsp&lt;/result&gt;
 * &lt;/action&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * <!-- START SNIPPET: exampleDescription -->
 * <p>
 * With the example above, 'submitApplication.action' will have the action messages / errors / field errors stored
 * in the HTTP Session. Later when needed, (in this case, when 'applicationFailed.action' is fired, it
 * will get the action messages / errors / field errors stored in the HTTP Session and put them back into
 * the action.
 * </p>
 * <!-- END SNIPPET: exampleDescription -->
 */
public class MessageStoreInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = 9161650888603380164L;

    private static final Logger LOG = LogManager.getLogger(MessageStoreInterceptor.class);

    public static final String AUTOMATIC_MODE = "AUTOMATIC";
    public static final String STORE_MODE = "STORE";
    public static final String RETRIEVE_MODE = "RETRIEVE";
    public static final String NONE = "NONE";

    private boolean allowRequestParameterSwitch = true;
    private String requestParameterSwitch = "operationMode";
    private String operationMode = NONE;

    public static final String fieldErrorsSessionKey = "__MessageStoreInterceptor_FieldErrors_SessionKey";
    public static final String actionErrorsSessionKey = "__MessageStoreInterceptor_ActionErrors_SessionKey";
    public static final String actionMessagesSessionKey = "__MessageStoreInterceptor_ActionMessages_SessionKey";

    public void setAllowRequestParameterSwitch(boolean allowRequestParameterSwitch) {
        this.allowRequestParameterSwitch = allowRequestParameterSwitch;
    }

    public boolean getAllowRequestParameterSwitch() {
        return this.allowRequestParameterSwitch;
    }

    public void setRequestParameterSwitch(String requestParameterSwitch) {
        this.requestParameterSwitch = requestParameterSwitch;
    }

    public String getRequestParameterSwitch() {
        return this.requestParameterSwitch;
    }

    public void setOperationMode(String operationMode) {
        this.operationMode = operationMode;
    }

    public String getOperationModel() {
        return this.operationMode;
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        LOG.trace("entering MessageStoreInterceptor ...");

        before(invocation);

        LOG.trace("Registering listener to store messages before result will be executed");
        MessageStorePreResultListener preResultListener = createPreResultListener(invocation);
        preResultListener.init(this);

        invocation.addPreResultListener(preResultListener);

        String result = invocation.invoke();

        LOG.debug("exit executing MessageStoreInterceptor");

        return result;
    }

    protected MessageStorePreResultListener createPreResultListener(ActionInvocation invocation) {
        return new MessageStorePreResultListener();
    }

    /**
     * Handle the retrieving of field errors / action messages / field errors, which is
     * done before action invocation, and the <code>operationMode</code> is 'RETRIEVE'.
     *
     * @param invocation the action invocation
     * @throws Exception in case of any error
     */
    protected void before(ActionInvocation invocation) throws Exception {
        String reqOperationMode = getRequestOperationMode(invocation);

        if (RETRIEVE_MODE.equalsIgnoreCase(reqOperationMode) ||
                RETRIEVE_MODE.equalsIgnoreCase(operationMode) ||
                AUTOMATIC_MODE.equalsIgnoreCase(operationMode)) {

            Object action = invocation.getAction();
            if (action instanceof ValidationAware) {
                // retrieve error / message from session
                Map session = (Map) invocation.getInvocationContext().get(ActionContext.SESSION);

                if (session == null) {
                    LOG.debug("Session is not open, no errors / messages could be retrieve for action [{}]", action);
                    return;
                }

                ValidationAware validationAwareAction = (ValidationAware) action;

                LOG.debug("Retrieve error / message from session to populate into action [{}]", action);

                Collection actionErrors = (Collection) session.get(actionErrorsSessionKey);
                Collection actionMessages = (Collection) session.get(actionMessagesSessionKey);
                Map fieldErrors = (Map) session.get(fieldErrorsSessionKey);

                if (actionErrors != null && actionErrors.size() > 0) {
                    Collection mergedActionErrors = mergeCollection(validationAwareAction.getActionErrors(), actionErrors);
                    validationAwareAction.setActionErrors(mergedActionErrors);
                }

                if (actionMessages != null && actionMessages.size() > 0) {
                    Collection mergedActionMessages = mergeCollection(validationAwareAction.getActionMessages(), actionMessages);
                    validationAwareAction.setActionMessages(mergedActionMessages);
                }

                if (fieldErrors != null && fieldErrors.size() > 0) {
                    Map mergedFieldErrors = mergeMap(validationAwareAction.getFieldErrors(), fieldErrors);
                    validationAwareAction.setFieldErrors(mergedFieldErrors);
                }
                session.remove(actionErrorsSessionKey);
                session.remove(actionMessagesSessionKey);
                session.remove(fieldErrorsSessionKey);
            }
        }
    }

    /**
     * Get the operationMode through request parameter, if <code>allowRequestParameterSwitch</code>
     * is 'true', else it simply returns 'NONE', meaning its neither in the 'STORE_MODE' nor
     * 'RETRIEVE_MODE'.
     *
     * @param invocation the action invocation
     * @return the request operation mode
     */
    protected String getRequestOperationMode(ActionInvocation invocation) {
        String reqOperationMode = NONE;
        if (allowRequestParameterSwitch) {
            HttpParameters reqParams = invocation.getInvocationContext().getParameters();
            if (reqParams.contains(requestParameterSwitch)) {
                reqOperationMode = reqParams.get(requestParameterSwitch).getValue();
            }
        }
        return reqOperationMode;
    }

    /**
     * Merge <code>col1</code> and <code>col2</code> and return the composite
     * <code>Collection</code>.
     *
     * @param col1 first collection
     * @param col2 second collection
     * @return the merged collection
     */
    protected Collection mergeCollection(Collection col1, Collection col2) {
        Collection _col1 = (col1 == null ? new ArrayList() : col1);
        Collection _col2 = (col2 == null ? new ArrayList() : col2);
        _col1.addAll(_col2);
        return _col1;
    }

    /**
     * Merge <code>map1</code> and <code>map2</code> and return the composite
     * <code>Map</code>
     *
     * @param map1 first map
     * @param map2 second map
     * @return the merged map
     */
    protected Map mergeMap(Map map1, Map map2) {
        Map _map1 = (map1 == null ? new LinkedHashMap() : map1);
        Map _map2 = (map2 == null ? new LinkedHashMap() : map2);
        _map1.putAll(_map2);
        return _map1;
    }

}
