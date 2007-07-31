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
package org.apache.struts2.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.portlet.context.PortletActionContext;
import org.apache.struts2.portlet.util.PortletUrlHelper;
import org.apache.struts2.views.util.UrlHelper;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptorUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.TextUtils;
import com.opensymphony.xwork2.validator.ActionValidatorManagerFactory;
import com.opensymphony.xwork2.validator.FieldValidator;
import com.opensymphony.xwork2.validator.ValidationInterceptor;
import com.opensymphony.xwork2.validator.Validator;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p/>
 * Renders HTML an input form.<p/>
 * <p/>
 * The remote form allows the form to be submitted without the page being refreshed. The results from the form
 * can be inserted into any HTML element on the page.<p/>
 * <p/>
 * NOTE:<p/>
 * The order / logic in determining the posting url of the generated HTML form is as follows:-
 * <ol>
 * <li>
 * If the action attribute is not specified, then the current request will be used to
 * determine the posting url
 * </li>
 * <li>
 * If the action is given, Struts will try to obtain an ActionConfig. This will be
 * successfull if the action attribute is a valid action alias defined struts.xml.
 * </li>
 * <li>
 * If the action is given and is not an action alias defined in struts.xml, Struts
 * will used the action attribute as if it is the posting url, separting the namespace
 * from it and using UrlHelper to generate the final url.
 * </li>
 * </ol>
 * <p/>
 * <!-- END SNIPPET: javadoc -->
 * <p/>
 * <p/> <b>Examples</b>
 * <p/>
 * <pre>
 * <!-- START SNIPPET: example -->
 * <p/>
 * &lt;s:form ... /&gt;
 * <p/>
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 */
@StrutsTag(name="form", tldTagClass="org.apache.struts2.views.jsp.ui.FormTag", description="Renders an input form")
public class Form extends ClosingUIBean {
    public static final String OPEN_TEMPLATE = "form";
    public static final String TEMPLATE = "form-close";

    private int sequence = 0;

    protected String onsubmit;
    protected String action;
    protected String target;
    protected String enctype;
    protected String method;
    protected String namespace;
    protected String validate;
    protected String portletMode;
    protected String windowState;
    protected String acceptcharset;

    protected boolean enableDynamicMethodInvocation = true;
    protected Configuration configuration;
    protected ObjectFactory objectFactory;

    public Form(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected boolean evaluateNameValue() {
        return false;
    }

    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Inject(StrutsConstants.STRUTS_ENABLE_DYNAMIC_METHOD_INVOCATION)
    public void setEnableDynamicMethodInvocation(String enable) {
        enableDynamicMethodInvocation = "true".equals(enable);
    }

    @Inject
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }


    /*
    * Revised for Portlet actionURL as form action, and add wwAction as hidden
    * field. Refer to template.simple/form.vm
    */
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();

        //boolean isAjax = "ajax".equalsIgnoreCase(this.theme);

        if (validate != null) {
            addParameter("validate", findValue(validate, Boolean.class));
        }

        // calculate the action and namespace
        /*String action = null;
        if (this.action != null) {
            // if it isn't specified, we'll make somethig up
            action = findString(this.action);
        }

        if (Dispatcher.getInstance().isPortletSupportActive() && PortletActionContext.isPortletRequest()) {
            evaluateExtraParamsPortletRequest(namespace, action);
        } else {
            String namespace = determineNamespace(this.namespace, getStack(),
                    request);
            evaluateExtraParamsServletRequest(action, namespace, isAjax);
        }*/

        if (onsubmit != null) {
            addParameter("onsubmit", findString(onsubmit));
        }

        if (target != null) {
            addParameter("target", findString(target));
        }

        if (enctype != null) {
            addParameter("enctype", findString(enctype));
        }

        if (method != null) {
            addParameter("method", findString(method));
        }

        if (acceptcharset != null) {
            addParameter("acceptcharset", findString(acceptcharset));
        }

        // keep a collection of the tag names for anything special the templates might want to do (such as pure client
        // side validation)
        if (!parameters.containsKey("tagNames")) {
            // we have this if check so we don't do this twice (on open and close of the template)
            addParameter("tagNames", new ArrayList());
        }
    }

    /**
     * The Form component determines its HTML element id as follows:-
     * <ol>
     *    <li>if an 'id' attribute is specified.</li>
     *    <li>if an 'action' attribute is specified, it will be used as the id.</li>
     * </ol>
     */
    protected void populateComponentHtmlId(Form form) {
        boolean isAjax = "ajax".equalsIgnoreCase(this.theme);

        String action = null;
        if (this.action != null) {
            // if it isn't specified, we'll make somethig up
            action = findString(this.action);
        }

        if (id != null) {
            addParameter("id", escape(id));
        }
        if (Dispatcher.getInstance().isPortletSupportActive() && PortletActionContext.isPortletRequest()) {
            evaluateExtraParamsPortletRequest(namespace, action);
        } else {
            String namespace = determineNamespace(this.namespace, getStack(),
                    request);
            evaluateExtraParamsServletRequest(action, namespace, isAjax);
        }
    }

    /**
     * @param isAjax
     * @param namespace
     * @param action
     */
    private void evaluateExtraParamsServletRequest(String action, String namespace, boolean isAjax) {
        if (action == null) {
            // no action supplied? ok, then default to the current request (action or general URL)
            ActionInvocation ai = (ActionInvocation) getStack().getContext().get(ActionContext.ACTION_INVOCATION);
            if (ai != null) {
                action = ai.getProxy().getActionName();
                namespace = ai.getProxy().getNamespace();
            } else {
                // hmm, ok, we need to just assume the current URL cut down
                String uri = request.getRequestURI();
                action = uri.substring(uri.lastIndexOf('/'));
            }
        }

        String actionMethod = "";
        // FIXME: our implementation is flawed - the only concept of ! should be in DefaultActionMapper
        // handle "name!method" convention.
        if (enableDynamicMethodInvocation) {
            if (action.indexOf("!") != -1) {
                int endIdx = action.lastIndexOf("!");
                actionMethod = action.substring(endIdx + 1, action.length());
                action = action.substring(0, endIdx);
            }
        }

        final ActionConfig actionConfig = configuration.getRuntimeConfiguration().getActionConfig(namespace, action);
        String actionName = action;
        if (actionConfig != null) {

            ActionMapping mapping = new ActionMapping(action, namespace, actionMethod, parameters);
            String result = UrlHelper.buildUrl(actionMapper.getUriFromActionMapping(mapping), request, response, null);
            addParameter("action", result);

            // let's try to get the actual action class and name
            // this can be used for getting the list of validators
            addParameter("actionName", actionName);
            try {
                Class clazz = objectFactory.getClassInstance(actionConfig.getClassName());
                addParameter("actionClass", clazz);
            } catch (ClassNotFoundException e) {
                // this is OK, we'll just move on
            }

            addParameter("namespace", namespace);

            // if the name isn't specified, use the action name
            if (name == null) {
                addParameter("name", action);
            }

            // if the id isn't specified, use the action name
            if (id == null) {
                addParameter("id", action);
            }
        } else if (action != null) {
            // Since we can't find an action alias in the configuration, we just assume
            // the action attribute supplied is the path to be used as the uri this
            // form is submitting to.

            String result = UrlHelper.buildUrl(action, request, response, null);
            addParameter("action", result);

            // namespace: cut out anything between the start and the last /
            int slash = result.lastIndexOf('/');
            if (slash != -1) {
                addParameter("namespace", result.substring(0, slash));
            } else {
                addParameter("namespace", "");
            }

            // name/id: cut out anything between / and . should be the id and name
            if (id == null) {
                slash = result.lastIndexOf('/');
                int dot = result.indexOf('.', slash);
                if (dot != -1) {
                    id = result.substring(slash + 1, dot);
                } else {
                    id = result.substring(slash + 1);
                }
                addParameter("id", escape(id));
            }
        }

        // WW-1284
        // evaluate if client-side js is to be enabled. (if validation interceptor
        // does allow validation eg. method is not filtered out)
        evaluateClientSideJsEnablement(actionName, namespace, actionMethod);
    }

    private void evaluateClientSideJsEnablement(String actionName, String namespace, String actionMethod) {

        // Only evaluate if Client-Side js is to be enable when validate=true
        Boolean validate = (Boolean) getParameters().get("validate");
        if (validate != null && validate.booleanValue()) {

            addParameter("performValidation", Boolean.FALSE);

            RuntimeConfiguration runtimeConfiguration = configuration.getRuntimeConfiguration();
            ActionConfig actionConfig = runtimeConfiguration.getActionConfig(namespace, actionName);

            if (actionConfig != null) {
                List interceptors = actionConfig.getInterceptors();
                for (Iterator i = interceptors.iterator(); i.hasNext();) {
                    InterceptorMapping interceptorMapping = (InterceptorMapping) i.next();
                    if (ValidationInterceptor.class.isInstance(interceptorMapping.getInterceptor())) {
                        ValidationInterceptor validationInterceptor = (ValidationInterceptor) interceptorMapping.getInterceptor();

                        Set excludeMethods = validationInterceptor.getExcludeMethodsSet();
                        Set includeMethods = validationInterceptor.getIncludeMethodsSet();

                        if (MethodFilterInterceptorUtil.applyMethod(excludeMethods, includeMethods, actionMethod)) {
                            addParameter("performValidation", Boolean.TRUE);
                        }
                        return;
                    }
                }
            }
        }
    }

    /**
     * Constructs the action url adapted to a portal environment.
     *
     * @param action The action to create the URL for.
     */
    private void evaluateExtraParamsPortletRequest(String namespace, String action) {

        if (this.action != null) {
            // if it isn't specified, we'll make somethig up
            action = findString(this.action);
        }

        String type = "action";
        if (TextUtils.stringSet(method)) {
            if ("GET".equalsIgnoreCase(method.trim())) {
                type = "render";
            }
        }
        if (action != null) {
            String result = PortletUrlHelper.buildUrl(action, namespace,
                    getParameters(), type, portletMode, windowState);
            addParameter("action", result);

            // namespace: cut out anything between the start and the last /
            int slash = result.lastIndexOf('/');
            if (slash != -1) {
                addParameter("namespace", result.substring(0, slash));
            } else {
                addParameter("namespace", "");
            }

            // name/id: cut out anything between / and . should be the id and
            // name
            if (id == null) {
                slash = action.lastIndexOf('/');
                int dot = action.indexOf('.', slash);
                if (dot != -1) {
                    id = action.substring(slash + 1, dot);
                } else {
                    id = action.substring(slash + 1);
                }
                addParameter("id", escape(id));
            }
        }

    }

    public List getValidators(String name) {
        Class actionClass = (Class) getParameters().get("actionClass");
        if (actionClass == null) {
            return Collections.EMPTY_LIST;
        }

        List all = ActionValidatorManagerFactory.getInstance().getValidators(actionClass, (String) getParameters().get("actionName"));
        List validators = new ArrayList();
        for (Iterator iterator = all.iterator(); iterator.hasNext();) {
            Validator validator = (Validator) iterator.next();
            if (validator instanceof FieldValidator) {
                FieldValidator fieldValidator = (FieldValidator) validator;
                if (fieldValidator.getFieldName().equals(name)) {
                    validators.add(fieldValidator);
                }
            }
        }

        return validators;
    }

    /**
     * Get a incrementing sequence unique to this <code>Form</code> component.
     * It is used by <code>Form</code> component's child that might need a
     * sequence to make them unique.
     *
     * @return int
     */
    protected int getSequence() {
        return sequence++;
    }

    @StrutsTagAttribute(description="HTML onsubmit attribute")
    public void setOnsubmit(String onsubmit) {
        this.onsubmit = onsubmit;
    }

    @StrutsTagAttribute(description="Set action name to submit to, without .action suffix", defaultValue="current action")
    public void setAction(String action) {
        this.action = action;
    }

    @StrutsTagAttribute(description="HTML form target attribute")
    public void setTarget(String target) {
        this.target = target;
    }

    @StrutsTagAttribute(description="HTML form enctype attribute")
    public void setEnctype(String enctype) {
        this.enctype = enctype;
    }

    @StrutsTagAttribute(description="HTML form method attribute")
    public void setMethod(String method) {
        this.method = method;
    }

    @StrutsTagAttribute(description="Namespace for action to submit to", defaultValue="current namespace")
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @StrutsTagAttribute(description="Whether client side/remote validation should be performed. Only" +
                " useful with theme xhtml/ajax", type="Boolean", defaultValue="false")
    public void setValidate(String validate) {
        this.validate = validate;
    }

    @StrutsTagAttribute(description="The portlet mode to display after the form submit")
    public void setPortletMode(String portletMode) {
        this.portletMode = portletMode;
    }

    @StrutsTagAttribute(description="The window state to display after the form submit")
    public void setWindowState(String windowState) {
        this.windowState = windowState;
    }

    @StrutsTagAttribute(description="The accepted charsets for this form. The values may be comma or blank delimited.")
    public void setAcceptcharset(String acceptcharset) {
        this.acceptcharset = acceptcharset;
    }
}
