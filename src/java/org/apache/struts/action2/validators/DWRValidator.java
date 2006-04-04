/*
 * $Id$
 *
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.struts.action2.validators;

import org.apache.struts.action2.dispatcher.ApplicationMap;
import org.apache.struts.action2.dispatcher.DispatcherUtils;
import org.apache.struts.action2.dispatcher.RequestMap;
import org.apache.struts.action2.dispatcher.SessionMap;
import com.opensymphony.xwork.*;
import com.opensymphony.xwork.config.entities.ActionConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ltd.getahead.dwr.ExecutionContext;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * User: plightbo
 * Date: Dec 11, 2004
 * Time: 6:17:58 PM
 * <p/>
 * <dwr>
 * <allow>
 * <create creator="new" javascript="validator" class="org.apache.struts.action2.validators.DWRValidator"/>
 * <convert converter="bean" match="com.opensymphony.xwork.ValidationAwareSupport"/>
 * </allow>
 * </dwr>
 */
public class DWRValidator {
    private static final Log LOG = LogFactory.getLog(DWRValidator.class);

    public ValidationAwareSupport doPost(String namespace, String action, Map params) throws Exception {
        HttpServletRequest req = ExecutionContext.get().getHttpServletRequest();
        ServletContext servletContext = ExecutionContext.get().getServletContext();
        HttpServletResponse res = ExecutionContext.get().getHttpServletResponse();

        Map requestParams = new HashMap(req.getParameterMap());
        if (params != null) {
            requestParams.putAll(params);
        } else {
            params = requestParams;
        }
        Map requestMap = new RequestMap(req);
        Map session = new SessionMap(req);
        Map application = new ApplicationMap(servletContext);
        DispatcherUtils du = DispatcherUtils.getInstance();
        HashMap ctx = du.createContextMap(requestMap,
                params,
                session,
                application,
                req,
                res,
                servletContext);

        try {
            ValidatorActionProxy proxy = new ValidatorActionProxy(namespace, action, ctx);
            proxy.execute();
            Object a = proxy.getAction();

            if (a instanceof ValidationAware) {
                ValidationAware aware = (ValidationAware) a;
                ValidationAwareSupport vas = new ValidationAwareSupport();
                vas.setActionErrors(aware.getActionErrors());
                vas.setActionMessages(aware.getActionMessages());
                vas.setFieldErrors(aware.getFieldErrors());

                return vas;
            } else {
                return null;
            }
        } catch (Exception e) {
            LOG.error("Error while trying to validate", e);
            return null;
        }
    }

    public static class ValidatorActionInvocation extends DefaultActionInvocation {
        protected ValidatorActionInvocation(ActionProxy proxy, Map extraContext) throws Exception {
            super(proxy, extraContext, true);
        }

        protected String invokeAction(Object action, ActionConfig actionConfig) throws Exception {
            return Action.NONE; // don't actually execute the action
        }
    }

    public static class ValidatorActionProxy extends DefaultActionProxy {
        protected ValidatorActionProxy(String namespace, String actionName, Map extraContext) throws Exception {
            super(namespace, actionName, extraContext, false, true);
        }

        protected void prepare() throws Exception {
            invocation = new ValidatorActionInvocation(this, extraContext);
        }
    }
}
