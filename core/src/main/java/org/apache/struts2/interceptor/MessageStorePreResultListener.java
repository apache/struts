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

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.ServletActionRedirectResult;
import org.apache.struts2.dispatcher.ServletRedirectResult;

import java.util.Map;

/**
 * This listener is used by {@link MessageStoreInterceptor} to store messages in HttpSession
 * just before result will be executed. It must be done that way as after result will be executed
 * HttpSession cannot be modified (response was already sent to browser).
 */
class MessageStorePreResultListener implements PreResultListener {

    private static final Logger LOG = LoggerFactory.getLogger(MessageStorePreResultListener.class);

    private MessageStoreInterceptor interceptor;

    public MessageStorePreResultListener(MessageStoreInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    public void beforeResult(ActionInvocation invocation, String resultCode) {

        boolean isCommitted = ServletActionContext.getResponse().isCommitted();
        if (isCommitted) {
            LOG.trace("Response was already committed, cannot store messages!");
            return;
        }

        boolean isInvalidated = ServletActionContext.getRequest().getSession(false) == null;
        if (isInvalidated) {
            LOG.trace("Session was invalidated or never created, cannot store messages!");
            return;
        }

        Map<String, Object> session = invocation.getInvocationContext().getSession();
        if (session == null) {
            LOG.trace("Could not store action [#0] error/messages into session, because session hasn't been opened yet.", invocation.getAction());
            return;
        }

        String reqOperationMode = interceptor.getRequestOperationMode(invocation);

        boolean isRedirect = false;
        try {
            ResultConfig resultConfig = invocation.getProxy().getConfig().getResults().get(resultCode);
            if (resultConfig != null) {
                isRedirect = ServletRedirectResult.class.isAssignableFrom(Class.forName(resultConfig.getClassName()));
            }
        } catch (Exception e) {
            LOG.warn("Cannot read result!", e);
        }

        if (MessageStoreInterceptor.STORE_MODE.equalsIgnoreCase(reqOperationMode) ||
                MessageStoreInterceptor.STORE_MODE.equalsIgnoreCase(interceptor.getOperationModel()) ||
                (MessageStoreInterceptor.AUTOMATIC_MODE.equalsIgnoreCase(interceptor.getOperationModel()) && isRedirect)) {

            Object action = invocation.getAction();
            if (action instanceof ValidationAware) {
                LOG.debug("Storing action [#0] error/messages into session ", action);

                ValidationAware validationAwareAction = (ValidationAware) action;
                session.put(MessageStoreInterceptor.actionErrorsSessionKey, validationAwareAction.getActionErrors());
                session.put(MessageStoreInterceptor.actionMessagesSessionKey, validationAwareAction.getActionMessages());
                session.put(MessageStoreInterceptor.fieldErrorsSessionKey, validationAwareAction.getFieldErrors());

            } else {
                LOG.debug("Action [#0] is not ValidationAware, no message / error that are storeable", action);
            }
        }
    }
}
