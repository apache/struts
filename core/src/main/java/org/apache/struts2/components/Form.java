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
package org.apache.struts2.components;

import org.apache.struts2.ActionInvocation;
import org.apache.struts2.ModelDriven;
import org.apache.struts2.ObjectFactory;
import org.apache.struts2.interceptor.ModelDrivenInterceptor;
import org.apache.struts2.util.CompoundRoot;
import org.apache.struts2.config.Configuration;
import org.apache.struts2.config.RuntimeConfiguration;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.struts2.config.entities.InterceptorMapping;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.interceptor.MethodFilterInterceptorUtil;
import org.apache.struts2.util.ValueStack;
import org.apache.struts2.text.CompositeTextProvider;
import org.apache.struts2.text.TextProvider;
import org.apache.struts2.text.TextProviderFactory;
import org.apache.struts2.validator.ActionValidatorManager;
import org.apache.struts2.validator.DelegatingValidatorContext;
import org.apache.struts2.validator.FieldValidator;
import org.apache.struts2.validator.ValidationException;
import org.apache.struts2.validator.ValidationInterceptor;
import org.apache.struts2.validator.Validator;
import org.apache.struts2.validator.ValidatorContext;
import org.apache.struts2.validator.validators.VisitorFieldValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>
 * Renders HTML an input form.
 * </p>
 *
 * <p>
 * The remote form allows the form to be submitted without the page being refreshed. The results from the form
 * can be inserted into any HTML element on the page.
 * </p>
 * <p>
 * NOTE:<br>
 * The order / logic in determining the posting url of the generated HTML form is as follows:
 * </p>
 *
 * <ol>
 * <li>
 * If the action attribute is not specified, then the current request will be used to
 * determine the posting url
 * </li>
 * <li>
 * If the action is given, Struts will try to obtain an ActionConfig. This will be
 * successful if the action attribute is a valid action alias defined struts.xml.
 * </li>
 * <li>
 * If the action is given and is not an action alias defined in struts.xml, Struts
 * will used the action attribute as if it is the posting url, separting the namespace
 * from it and using UrlHelper to generate the final url.
 * </li>
 * </ol>
 * <p>
 * <strong>The client-side JS <code>validate</code> attribute is deprecated since 7.4.0 — use the html5 theme's
 * constraint attributes instead. Removed in 8.0.0.</strong>
 * </p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <p><b>Examples</b></p>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *
 * &lt;s:form ... /&gt;
 *
 * <!-- END SNIPPET: example -->
 * </pre>
 */
@StrutsTag(
    name = "form",
    tldTagClass = "org.apache.struts2.views.jsp.ui.FormTag",
    description = "Renders an input form",
    allowDynamicAttributes = true)
public class Form extends ClosingUIBean {
    public static final String OPEN_TEMPLATE = "form";
    public static final String TEMPLATE = "form-close";

    private static final String ATTR_ACTION_CLASS = "actionClass";

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

    private List<Validator> cachedActionValidators;
    private String cachedActionName;
    private boolean actionValidatorsResolved;
    private final Map<String, List<Validator>> cachedVisitorValidators = new HashMap<>();
    private final Map<String, String> visitedPaths = new HashMap<>();
    private final Map<String, List<Object>> visitedObjects = new HashMap<>();
    private Boolean modelDrivenConfigured;
    protected TextProviderFactory textProviderFactory;

    public Form(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected boolean evaluateNameValue() {
        return false;
    }

    @Override
    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    @Override
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

    @Inject
    public void setTextProviderFactory(TextProviderFactory textProviderFactory) {
        this.textProviderFactory = textProviderFactory;
    }


    /*
     * Revised for Portlet actionURL as form action, and add wwAction as hidden
     * field. Refer to template.simple/form.vm
     */
    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        if (validate != null) {
            addParameter("validate", findValue(validate, Boolean.class));
        }

        if (name == null) {
            //make the name the same as the id
            String id = (String) getAttributes().get("id");
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
        if (!attributes.containsKey("tagNames")) {
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
    @Override
    protected void populateComponentHtmlId(Form form) {
        if (id != null) {
            super.populateComponentHtmlId(null);
        }

        // if no id given, it will be tried to generate it from the action attribute
        // by the urlRenderer implementation
        urlRenderer.renderFormUrl(this);
    }

    /**
     * Evaluate client side JavaScript Enablement.
     *
     * @param actionName   the actioName to check for
     * @param namespace    the namespace to check for
     * @param actionMethod the method to ckeck for
     * @deprecated since 7.4.0, for removal in 8.0.0. The generated client-side validator only ever
     * covered fields rendered by a nested Struts tag (WW-2975). Use the {@code html5} theme with
     * {@code struts.ui.html5.constraints=true}, which derives native HTML5 constraint attributes
     * per field instead.
     */
    @Deprecated(since = "7.4.0", forRemoval = true)
    protected void evaluateClientSideJsEnablement(String actionName, String namespace, String actionMethod) {

        // Only evaluate if Client-Side js is to be enable when validate=true
        Boolean validate = (Boolean) getAttributes().get("validate");
        if (validate != null && validate) {

            addParameter("performValidation", Boolean.FALSE);

            RuntimeConfiguration runtimeConfiguration = configuration.getRuntimeConfiguration();
            ActionConfig actionConfig = runtimeConfiguration.getActionConfig(namespace, actionName);

            if (actionConfig != null) {
                List<InterceptorMapping> interceptors = actionConfig.getInterceptors();
                for (InterceptorMapping interceptorMapping : interceptors) {
                    if (interceptorMapping.getInterceptor() instanceof ValidationInterceptor validationInterceptor) {

                        Set<String> excludeMethods = validationInterceptor.getExcludeMethodsSet();
                        Set<String> includeMethods = validationInterceptor.getIncludeMethodsSet();

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
     * Looks up the validators for a field, for the deprecated client-side JavaScript validator.
     *
     * @param name the field name to look up
     * @return the validators applying to the field, never null
     * @deprecated since 7.4.0, for removal in 8.0.0. Use {@link #getFieldValidators(String)}, which
     * is generically typed and resolves the action's validators once per form rather than per field.
     */
    @Deprecated(since = "7.4.0", forRemoval = true)
    public List getValidators(String name) {
        Class actionClass = (Class) getAttributes().get(ATTR_ACTION_CLASS);
        if (actionClass == null) {
            return Collections.EMPTY_LIST;
        }

        String formActionValue = findString(action);
        ActionMapping mapping = actionMapper.getMappingFromActionName(formActionValue);

        if (mapping == null) {
            mapping = actionMapper.getMappingFromActionName((String) getAttributes().get("actionName"));
        }

        if (mapping == null) {
            return Collections.EMPTY_LIST;
        }

        String actionName = mapping.getName();

        String methodName = null;
        if (isValidateAnnotatedMethodOnly(actionName)) {
            methodName = mapping.getMethod();
        }

        List<Validator> actionValidators = actionValidatorManager.getValidators(actionClass, actionName, methodName);
        List<Validator> validators = new ArrayList<>();

        findFieldValidators(name, actionClass, actionName, actionValidators, validators, "");

        return validators;
    }

    /**
     * Returns the validators declared for a single field, resolving the action's validator list at
     * most once per form render.
     *
     * @since 7.4.0
     */
    public List<Validator> getFieldValidators(String name) {
        resolveActionValidators();
        if (cachedActionValidators.isEmpty()) {
            return Collections.emptyList();
        }
        Class actionClass = (Class) getAttributes().get(ATTR_ACTION_CLASS);
        List<Validator> validators = new ArrayList<>();
        findFieldValidators(name, actionClass, cachedActionName, cachedActionValidators, validators, "");
        recordVisitedPath(name, validators);
        // the wrapper only exists to prefix the field name for the deprecated JS validator; callers of
        // this method dispatch on the concrete validator type
        validators.replaceAll(validator -> validator instanceof FieldVisitorValidatorWrapper wrapper
            ? unwrap(wrapper) : validator);
        return validators;
    }

    /**
     * A field has one validated object only when every validator found for it came through the same
     * visitor: two {@code appendPrefix="false"} visitors declaring the same field, or a visitor-nested
     * validator next to a direct one, leave nothing sensible to hand the provider.
     */
    private void recordVisitedPath(String name, List<Validator> validators) {
        Set<String> paths = new HashSet<>();
        for (Validator validator : validators) {
            paths.add(validator instanceof FieldVisitorValidatorWrapper wrapper ? wrapper.getVisitedPath() : null);
        }
        visitedObjects.remove(name);
        if (paths.size() == 1 && !paths.contains(null)) {
            visitedPaths.put(name, paths.iterator().next());
        } else {
            visitedPaths.remove(name);
        }
    }

    /**
     * The object a field's validators run against: the visited object for a field reached through a
     * {@code visitor} validator, otherwise null — the caller then uses the action, as validation does.
     * Resolved by {@link #getFieldValidators(String)}, which must run first.
     *
     * @since 7.4.0
     */
    public Object getValidatedObject(String name) {
        String path = visitedPaths.get(name);
        if (path == null) {
            return null;
        }
        List<Object> chain = getVisitedObjects(name);
        // a partial chain (user set, user.address still null) has no object of the right type
        return chain.size() == path.split("\\.").length ? chain.get(chain.size() - 1) : null;
    }

    /**
     * The visited objects on the way to a field, outermost first — {@code [user, user.address]} for
     * {@code user.address.street} — as far as they exist. Validation has every one of them on the
     * value stack when it resolves the innermost message, so rendering pushes them too.
     */
    List<Object> getVisitedObjects(String name) {
        String path = visitedPaths.get(name);
        if (path == null) {
            return Collections.emptyList();
        }
        return visitedObjects.computeIfAbsent(name, key -> resolveChain(path));
    }

    private List<Object> resolveChain(String path) {
        List<Object> chain = new ArrayList<>();
        StringBuilder prefix = new StringBuilder();
        for (String segment : path.split("\\.")) {
            prefix.append(prefix.isEmpty() ? "" : ".").append(segment);
            Object visited = findAsValidation(prefix.toString());
            if (visited == null) {
                break;
            }
            chain.add(visited);
        }
        return chain;
    }

    private Object currentAction() {
        ActionInvocation invocation = getStack().getActionContext().getActionInvocation();
        return invocation == null ? null : invocation.getAction();
    }

    /**
     * Validation reads the visited object off the stack as the interceptors left it: the action, with
     * the model {@code ModelDrivenInterceptor} pushed directly above it. At render time an
     * {@code <s:iterator>} or {@code <s:push>} frame above those may expose the same property, so
     * that validation-time pair is put back on top for the lookup.
     */
    private Object findAsValidation(String path) {
        Object invoked = currentAction();
        if (invoked == null) {
            return getStack().findValue(path);
        }
        CompoundRoot root = getStack().getRoot();
        int depth = root.size();
        try {
            Object model = modelPushedAbove(invoked, root);
            getStack().push(invoked);
            if (model != null) {
                getStack().push(model);
            }
            return getStack().findValue(path);
        } finally {
            while (root.size() > depth) {
                getStack().pop();
            }
        }
    }

    /**
     * The model the interceptor pushed: the frame directly above the action, and only when it is the
     * action's current model — anything else there is a page frame. The interceptor is in the default
     * stack, so its being configured alone proves nothing; it is still checked because a stack without
     * it never pushes, however the model looks now.
     */
    private Object modelPushedAbove(Object action, CompoundRoot root) {
        if (!(action instanceof ModelDriven<?> modelDriven) || modelDriven.getModel() == null) {
            return null;
        }
        int actionIndex = root.indexOf(action);
        if (actionIndex <= 0 || !modelDrivenInterceptorConfigured()) {
            return null;
        }
        Object above = root.get(actionIndex - 1);
        return above == modelDriven.getModel() ? above : null;
    }

    private boolean modelDrivenInterceptorConfigured() {
        if (modelDrivenConfigured == null) {
            modelDrivenConfigured = false;
            ActionInvocation invocation = getStack().getActionContext().getActionInvocation();
            ActionConfig actionConfig = invocation == null || invocation.getProxy() == null
                ? null : invocation.getProxy().getConfig();
            if (actionConfig != null) {
                for (InterceptorMapping interceptorMapping : actionConfig.getInterceptors()) {
                    if (interceptorMapping.getInterceptor() instanceof ModelDrivenInterceptor) {
                        modelDrivenConfigured = true;
                    }
                }
            }
        }
        return modelDrivenConfigured;
    }

    /**
     * Gives the nested validator the text provider {@code VisitorFieldValidator.validateObject} gives
     * it: each visited level's bundle, innermost first, then the action's. An instance is only needed
     * when it is itself a {@code TextProvider}; the class-based providers work for a form rendered
     * before the visited objects exist.
     */
    private FieldValidator unwrap(FieldVisitorValidatorWrapper wrapper) {
        FieldValidator validator = wrapper.getFieldValidator();
        Object invoked = currentAction();
        if (wrapper.getVisitedClasses().isEmpty() || invoked == null || textProviderFactory == null) {
            return validator;
        }
        DelegatingValidatorContext parent = new DelegatingValidatorContext(invoked, textProviderFactory);
        List<TextProvider> providers = new ArrayList<>();
        List<Class<?>> classes = wrapper.getVisitedClasses();
        List<String> paths = wrapper.getVisitedPaths();
        for (int level = classes.size() - 1; level >= 0; level--) {
            providers.add(visitedTextProvider(findAsValidation(paths.get(level)), classes.get(level)));
        }
        providers.add(parent);
        validator.setValidatorContext(new DelegatingValidatorContext(parent, new CompositeTextProvider(providers), parent));
        return validator;
    }

    /**
     * What {@code VisitorFieldValidator.createTextProvider} builds: the instance itself when it is a
     * TextProvider, else a provider for its runtime class — or for the declared class when the instance
     * does not exist yet.
     */
    private TextProvider visitedTextProvider(Object visited, Class<?> declared) {
        if (visited instanceof TextProvider textProvider) {
            return textProvider;
        }
        return textProviderFactory.createInstance(visited != null ? visited.getClass() : declared);
    }

    private void resolveActionValidators() {
        if (actionValidatorsResolved) {
            return;
        }
        actionValidatorsResolved = true;
        cachedActionValidators = Collections.emptyList();

        Class actionClass = (Class) getAttributes().get(ATTR_ACTION_CLASS);
        if (actionClass == null) {
            return;
        }
        ActionMapping mapping = actionMapper.getMappingFromActionName(findString(action));
        if (mapping == null) {
            mapping = actionMapper.getMappingFromActionName((String) getAttributes().get("actionName"));
        }
        if (mapping == null) {
            return;
        }
        cachedActionName = mapping.getName();
        String methodName = isValidateAnnotatedMethodOnly(cachedActionName) ? mapping.getMethod() : null;
        cachedActionValidators =
            actionValidatorManager.getValidators(actionClass, cachedActionName, methodName);
    }

    private boolean isValidateAnnotatedMethodOnly(String actionName) {
        RuntimeConfiguration runtimeConfiguration = configuration.getRuntimeConfiguration();
        String actionNamespace = getNamespace(stack);
        ActionConfig actionConfig = runtimeConfiguration.getActionConfig(actionNamespace, actionName);

        if (actionConfig != null) {
            List<InterceptorMapping> interceptors = actionConfig.getInterceptors();
            for (InterceptorMapping interceptorMapping : interceptors) {
                if (interceptorMapping.getInterceptor() instanceof ValidationInterceptor validationInterceptor) {
                    return validationInterceptor.isValidateAnnotatedMethodOnly();
                }
            }
        }
        return false;
    }

    private void findFieldValidators(String name, Class actionClass, String actionName,
                                     List<Validator> validatorList, List<Validator> resultValidators, String prefix) {
        findFieldValidators(name, actionClass, actionName, validatorList, resultValidators, prefix, Collections.emptyList());
    }

    /** One level of visitor nesting: the class it validates and the OGNL path of that object from the action. */
    private record Visit(Class<?> clazz, String path) {
    }

    private void findFieldValidators(String name, Class actionClass, String actionName,
                                     List<Validator> validatorList, List<Validator> resultValidators, String prefix,
                                     List<Visit> visits) {

        for (Validator validator : validatorList) {
            if (validator instanceof FieldValidator fieldValidator) {

                if (validator instanceof VisitorFieldValidator) {
                    VisitorFieldValidator vfValidator = (VisitorFieldValidator) fieldValidator;
                    Class clazz = getVisitorReturnType(actionClass, vfValidator.getFieldName());
                    if (clazz == null) {
                        continue;
                    }

                    String vPrefix = prefix + (vfValidator.isAppendPrefix() ? vfValidator.getFieldName() + "." : "");
                    String vPath = visits.isEmpty() ? vfValidator.getFieldName()
                        : visits.get(visits.size() - 1).path() + "." + vfValidator.getFieldName();
                    // per visitor, not per class: unwrap() sets a context on these instances, and two
                    // visitors over one class must not overwrite each other's
                    List<Validator> visitorValidators = cachedVisitorValidators.computeIfAbsent(vPath,
                        path -> actionValidatorManager.getValidators(clazz, actionName));
                    findFieldValidators(name, clazz, actionName, visitorValidators, resultValidators, vPrefix,
                        append(visits, new Visit(clazz, vPath)));
                } else if ((prefix + fieldValidator.getFieldName()).equals(name)) {
                    if (visits.isEmpty()) {
                        resultValidators.add(fieldValidator);
                    } else {
                        //fixing field name for js side
                        resultValidators.add(new FieldVisitorValidatorWrapper(fieldValidator, prefix,
                            visits.stream().map(Visit::clazz).toList(), visits.stream().map(Visit::path).toList()));
                    }
                }
            }
        }
    }

    private static <T> List<T> append(List<T> list, T element) {
        List<T> appended = new ArrayList<>(list);
        appended.add(element);
        return appended;
    }

    /**
     * Wrap field validator, add visitor's field prefix to the field name.
     * Javascript side is not aware of the visitor validators
     * and does not know how to prefix the fields.
     */
    /*
     * Class is public because Freemarker has problems accessing properties.
     */
    public static class FieldVisitorValidatorWrapper implements FieldValidator {
        private FieldValidator fieldValidator;
        private String namePrefix;
        private final List<Class<?>> visitedClasses;
        private final List<String> visitedPaths;

        public FieldVisitorValidatorWrapper(FieldValidator fv, String namePrefix) {
            this(fv, namePrefix, Collections.emptyList(), Collections.emptyList());
        }

        /**
         * @param visitedClasses the classes visited on the way to the field, outermost first; their
         *                       bundles are where the field's messages resolve
         * @param visitedPaths   the OGNL paths of those objects from the action, e.g.
         *                       {@code [user, user.address]}
         * @since 7.4.0
         */
        public FieldVisitorValidatorWrapper(FieldValidator fv, String namePrefix,
                                            List<Class<?>> visitedClasses, List<String> visitedPaths) {
            this.fieldValidator = fv;
            this.namePrefix = namePrefix;
            this.visitedClasses = List.copyOf(visitedClasses);
            this.visitedPaths = List.copyOf(visitedPaths);
        }

        List<Class<?>> getVisitedClasses() {
            return visitedClasses;
        }

        List<String> getVisitedPaths() {
            return visitedPaths;
        }

        /** The innermost visited path, or null when the wrapper carries none. */
        public String getVisitedPath() {
            return visitedPaths.isEmpty() ? null : visitedPaths.get(visitedPaths.size() - 1);
        }

        public String getValidatorType() {
            return "field-visitor";
        }

        public String getFieldName() {
            return namePrefix + fieldValidator.getFieldName();
        }

        public FieldValidator getFieldValidator() {
            return fieldValidator;
        }

        public void setFieldValidator(FieldValidator fieldValidator) {
            this.fieldValidator = fieldValidator;
        }

        public String getDefaultMessage() {
            return fieldValidator.getDefaultMessage();
        }

        public String getMessage(Object object) {
            return fieldValidator.getMessage(object);
        }

        public String getMessageKey() {
            return fieldValidator.getMessageKey();
        }

        public String[] getMessageParameters() {
            return fieldValidator.getMessageParameters();
        }

        public ValidatorContext getValidatorContext() {
            return fieldValidator.getValidatorContext();
        }

        public void setDefaultMessage(String message) {
            fieldValidator.setDefaultMessage(message);
        }

        public void setFieldName(String fieldName) {
            fieldValidator.setFieldName(fieldName);
        }

        public void setMessageKey(String key) {
            fieldValidator.setMessageKey(key);
        }

        public void setMessageParameters(String[] messageParameters) {
            fieldValidator.setMessageParameters(messageParameters);
        }

        public void setValidatorContext(ValidatorContext validatorContext) {
            fieldValidator.setValidatorContext(validatorContext);
        }

        public void setValidatorType(String type) {
            fieldValidator.setValidatorType(type);
        }

        public void setValueStack(ValueStack stack) {
            fieldValidator.setValueStack(stack);
        }

        public void validate(Object object) throws ValidationException {
            fieldValidator.validate(object);
        }

        public String getNamePrefix() {
            return namePrefix;
        }

        public void setNamePrefix(String namePrefix) {
            this.namePrefix = namePrefix;
        }
    }

    /**
     * Return type of visited object.
     *
     * @param actionClass      action class
     * @param visitorFieldName field name
     * @return type of visited object
     */
    @SuppressWarnings("unchecked")
    protected Class getVisitorReturnType(Class actionClass, String visitorFieldName) {
        if (visitorFieldName == null) {
            return null;
        }
        String methodName = "get" + StringUtils.capitalize(visitorFieldName);
        try {
            Method method = actionClass.getMethod(methodName);
            return method.getReturnType();
        } catch (NoSuchMethodException e) {
            return null;
        }
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

    @StrutsTagAttribute(description = "HTML onsubmit attribute")
    public void setOnsubmit(String onsubmit) {
        this.onsubmit = onsubmit;
    }

    @StrutsTagAttribute(description = "HTML onreset attribute")
    public void setOnreset(String onreset) {
        this.onreset = onreset;
    }

    @StrutsTagAttribute(description = "Set action name to submit to, without .action suffix", defaultValue = "current action")
    public void setAction(String action) {
        this.action = action;
    }

    @StrutsTagAttribute(description = "HTML form target attribute")
    public void setTarget(String target) {
        this.target = target;
    }

    @StrutsTagAttribute(description = "HTML form enctype attribute")
    public void setEnctype(String enctype) {
        this.enctype = enctype;
    }

    @StrutsTagAttribute(description = "HTML form method attribute")
    public void setMethod(String method) {
        this.method = method;
    }

    @StrutsTagAttribute(description = "Namespace for action to submit to", defaultValue = "current namespace")
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * @deprecated since 7.4.0, for removal in 8.0.0. The generated client-side validator only ever
     * covered fields rendered by a nested Struts tag (WW-2975). Use the {@code html5} theme with
     * {@code struts.ui.html5.constraints=true} instead.
     */
    @StrutsTagAttribute(description = "Whether client side/remote validation should be performed. Only" +
        " useful with theme xhtml/ajax", type = "Boolean", defaultValue = "false")
    @Deprecated(since = "7.4.0", forRemoval = true)
    public void setValidate(String validate) {
        this.validate = validate;
    }

    @StrutsTagAttribute(description = "The portlet mode to display after the form submit")
    public void setPortletMode(String portletMode) {
        this.portletMode = portletMode;
    }

    @StrutsTagAttribute(description = "The window state to display after the form submit")
    public void setWindowState(String windowState) {
        this.windowState = windowState;
    }

    @StrutsTagAttribute(description = "The accepted charsets for this form. The values may be comma or blank delimited.")
    public void setAcceptcharset(String acceptcharset) {
        this.acceptcharset = acceptcharset;
    }

    @StrutsTagAttribute(description = "Id of element that will receive the focus when page loads.")
    public void setFocusElement(String focusElement) {
        this.focusElement = focusElement;
    }

    @StrutsTagAttribute(description = "Whether actual context should be included in URL", type = "Boolean", defaultValue = "true")
    public void setIncludeContext(boolean includeContext) {
        this.includeContext = includeContext;
    }
}
