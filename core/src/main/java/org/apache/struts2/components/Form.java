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

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptorUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ActionValidatorManager;
import com.opensymphony.xwork2.validator.FieldValidator;
import com.opensymphony.xwork2.validator.ValidationInterceptor;
import com.opensymphony.xwork2.validator.Validator;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.Set;

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
@StrutsTag(
    name="form",
    tldTagClass="org.apache.struts2.views.jsp.ui.FormTag",
    description="Renders an input form",
    allowDynamicAttributes=true)
public class Form extends ClosingUIBean {
    public static final String OPEN_TEMPLATE = "form";
    public static final String TEMPLATE = "form-close";

    private int sequence = 0;

    protected String onsubmit;
    protected String onreset;
    protected String action;
    protected String target;
    protected String enctype;
    protected String method;
    protected String namespace;
    protected String validate;
    protected String portletMode;
    protected String windowState;
    protected String acceptcharset;
    protected boolean includeContext = true;

    protected String focusElement;
    protected Configuration configuration;
    protected ObjectFactory objectFactory;
    protected UrlRenderer urlRenderer;
    protected ActionValidatorManager actionValidatorManager;

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

    @Inject
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Inject
    public void setUrlRenderer(UrlRenderer urlRenderer) {
    	this.urlRenderer = urlRenderer;
    }

    @Inject
    public void setActionValidatorManager(ActionValidatorManager mgr) {
        this.actionValidatorManager = mgr;
    }


    /*
    * Revised for Portlet actionURL as form action, and add wwAction as hidden
    * field. Refer to template.simple/form.vm
    */
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        if (validate != null) {
            addParameter("validate", findValue(validate, Boolean.class));
        }

        if (name == null) {
            //make the name the same as the id
            String id = (String) getParameters().get("id");
             if (StringUtils.isNotEmpty(id)) {
                addParameter("name", id);
             }
        }

        if (onsubmit != null) {
            addParameter("onsubmit", findString(onsubmit));
        }

        if (onreset != null) {
            addParameter("onreset", findString(onreset));
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

        if (focusElement != null) {
            addParameter("focusElement", findString(focusElement));
        }
    }

    /**
     * Form component determine the its HTML element id as follows:-
     * <ol>
     *    <li>if an 'id' attribute is specified.</li>
     *    <li>if an 'action' attribute is specified, it will be used as the id.</li>
     * </ol>
     */
    protected void populateComponentHtmlId(Form form) {
        if (id != null) {
            addParameter("id", escape(id));
        }

        // if no id given, it will be tried to generate it from the action attribute
        // by the urlRenderer implementation
        urlRenderer.renderFormUrl(this);
    }

    /**
     * Evaluate client side JavaScript Enablement.
     * @param actionName the actioName to check for
     * @param namespace the namespace to check for
     * @param actionMethod the method to ckeck for
     */
    protected void evaluateClientSideJsEnablement(String actionName, String namespace, String actionMethod) {

        // Only evaluate if Client-Side js is to be enable when validate=true
        Boolean validate = (Boolean) getParameters().get("validate");
        if (validate != null && validate) {

            addParameter("performValidation", Boolean.FALSE);

            RuntimeConfiguration runtimeConfiguration = configuration.getRuntimeConfiguration();
            ActionConfig actionConfig = runtimeConfiguration.getActionConfig(namespace, actionName);

            if (actionConfig != null) {
                List<InterceptorMapping> interceptors = actionConfig.getInterceptors();
                for (InterceptorMapping interceptorMapping : interceptors) {
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

    public List getValidators(String name) {
        Class actionClass = (Class) getParameters().get("actionClass");
        if (actionClass == null) {
            return Collections.EMPTY_LIST;
        }

        List<Validator> all = actionValidatorManager.getValidators(actionClass, (String) getParameters().get("actionName"));
        List<Validator> validators = new ArrayList<Validator>();
        for (Validator validator : all) {
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

    @StrutsTagAttribute(description="HTML onreset attribute")
    public void setOnreset(String onreset) {
        this.onreset = onreset;
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

    @StrutsTagAttribute(description="Id of element that will receive the focus when page loads.")
    public void setFocusElement(String focusElement) {
        this.focusElement = focusElement;
    }

    @StrutsTagAttribute(description="Whether actual context should be included in URL", type="Boolean", defaultValue="true")
    public void setIncludeContext(boolean includeContext) {
        this.includeContext = includeContext;
    }
}
