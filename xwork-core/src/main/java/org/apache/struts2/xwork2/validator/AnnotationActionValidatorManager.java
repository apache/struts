/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.validator;


import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.FileManager;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.validator.validators.VisitorFieldValidator;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * AnnotationActionValidatorManager is the entry point into XWork's annotations-based validator framework.
 * Validation rules are specified as annotations within the source files.
 *
 * @author Rainer Hermanns
 * @author jepjep
 */
public class AnnotationActionValidatorManager implements ActionValidatorManager {

    /**
     * The file suffix for any validation file.
     */
    protected static final String VALIDATION_CONFIG_SUFFIX = "-validation.xml";

    private final Map<String, List<ValidatorConfig>> validatorCache = Collections.synchronizedMap(new HashMap<String, List<ValidatorConfig>>());
    private final Map<String, List<ValidatorConfig>> validatorFileCache = Collections.synchronizedMap(new HashMap<String, List<ValidatorConfig>>());
    private static final Logger LOG = LoggerFactory.getLogger(AnnotationActionValidatorManager.class);

    private ValidatorFactory validatorFactory;
    private ValidatorFileParser validatorFileParser;

    @Inject
    public void setValidatorFactory(ValidatorFactory fac) {
        this.validatorFactory = fac;
    }

    @Inject
    public void setValidatorFileParser(ValidatorFileParser parser) {
        this.validatorFileParser = parser;
    }

    public List<Validator> getValidators(Class clazz, String context) {
        return getValidators(clazz, context, null);
    }

    public List<Validator> getValidators(Class clazz, String context, String method) {
        final String validatorKey = buildValidatorKey(clazz);
        final List<ValidatorConfig> cfgs;

        if (validatorCache.containsKey(validatorKey)) {
            if (FileManager.isReloadingConfigs()) {
                validatorCache.put(validatorKey, buildValidatorConfigs(clazz, context, true, null));
            }
        } else {
            validatorCache.put(validatorKey, buildValidatorConfigs(clazz, context, false, null));
        }

        // get the set of validator configs
        cfgs = new ArrayList<ValidatorConfig>(validatorCache.get(validatorKey));

        ValueStack stack = ActionContext.getContext().getValueStack();

        // create clean instances of the validators for the caller's use
        ArrayList<Validator> validators = new ArrayList<Validator>(cfgs.size());
        for (ValidatorConfig cfg : cfgs) {
            if (method == null || method.equals(cfg.getParams().get("methodName"))) {
                Validator validator = validatorFactory.getValidator(
                        new ValidatorConfig.Builder(cfg)
                                .removeParam("methodName")
                                .build());
                validator.setValidatorType(cfg.getType());
                validator.setValueStack(stack);
                validators.add(validator);
            }
        }

        return validators;
    }

    public void validate(Object object, String context) throws ValidationException {
        validate(object, context, (String) null);
    }

    public void validate(Object object, String context, String method) throws ValidationException {
        ValidatorContext validatorContext = new DelegatingValidatorContext(object);
        validate(object, context, validatorContext, method);
    }

    public void validate(Object object, String context, ValidatorContext validatorContext) throws ValidationException {
        validate(object, context, validatorContext, null);
    }

    public void validate(Object object, String context, ValidatorContext validatorContext, String method) throws ValidationException {
        List<Validator> validators = getValidators(object.getClass(), context, method);
        Set<String> shortcircuitedFields = null;

        for (final Validator validator : validators) {
            try {
                validator.setValidatorContext(validatorContext);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Running validator: " + validator + " for object " + object + " and method " + method);
                }

                FieldValidator fValidator = null;
                String fullFieldName = null;

                if (validator instanceof FieldValidator) {
                    fValidator = (FieldValidator) validator;
                    fullFieldName = new InternalValidatorContextWrapper(fValidator.getValidatorContext()).getFullFieldName(fValidator.getFieldName());

                    if ((shortcircuitedFields != null) && shortcircuitedFields.contains(fullFieldName)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Short-circuited, skipping");
                        }

                        continue;
                    }
                }

                if (validator instanceof ShortCircuitableValidator && ((ShortCircuitableValidator) validator).isShortCircuit()) {
                    // get number of existing errors
                    List<String> errs = null;

                    if (fValidator != null) {
                        if (validatorContext.hasFieldErrors()) {
                            Collection<String> fieldErrors = validatorContext.getFieldErrors().get(fullFieldName);

                            if (fieldErrors != null) {
                                errs = new ArrayList<String>(fieldErrors);
                            }
                        }
                    } else if (validatorContext.hasActionErrors()) {
                        Collection<String> actionErrors = validatorContext.getActionErrors();

                        if (actionErrors != null) {
                            errs = new ArrayList<String>(actionErrors);
                        }
                    }

                    validator.validate(object);

                    if (fValidator != null) {
                        if (validatorContext.hasFieldErrors()) {
                            Collection<String> errCol = validatorContext.getFieldErrors().get(fullFieldName);

                            if ((errCol != null) && !errCol.equals(errs)) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Short-circuiting on field validation");
                                }

                                if (shortcircuitedFields == null) {
                                    shortcircuitedFields = new TreeSet<String>();
                                }

                                shortcircuitedFields.add(fullFieldName);
                            }
                        }
                    } else if (validatorContext.hasActionErrors()) {
                        Collection<String> errCol = validatorContext.getActionErrors();

                        if ((errCol != null) && !errCol.equals(errs)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Short-circuiting");
                            }

                            break;
                        }
                    }

                    continue;
                }

                validator.validate(object);
            } finally {
                validator.setValidatorContext(null);
            }

        }
    }

    /**
     * Builds a key for validators - used when caching validators.
     *
     * @param clazz the action.
     * @return a validator key which is the class name plus context.
     */
    protected static String buildValidatorKey(Class clazz) {
        ActionInvocation invocation = ActionContext.getContext().getActionInvocation();
        ActionProxy proxy = invocation.getProxy();
        ActionConfig config = proxy.getConfig();

        //the key needs to use the name of the action from the config file,
        //instead of the url, so wild card actions will have the same validator
        //see WW-2996
        StringBuilder sb = new StringBuilder(clazz.getName());
        sb.append("/");
        if (StringUtils.isNotBlank(config.getPackageName())) {
            sb.append(config.getPackageName());
            sb.append("/");
        }
        sb.append(config.getName());
        sb.append("|");
        sb.append(proxy.getMethod());
        return sb.toString();
    }

    private List<ValidatorConfig> buildAliasValidatorConfigs(Class aClass, String context, boolean checkFile) {
        String fileName = aClass.getName().replace('.', '/') + "-" + context.replace('/', '-') + VALIDATION_CONFIG_SUFFIX;

        return loadFile(fileName, aClass, checkFile);
    }


    protected List<ValidatorConfig> buildClassValidatorConfigs(Class aClass, boolean checkFile) {

        String fileName = aClass.getName().replace('.', '/') + VALIDATION_CONFIG_SUFFIX;

        List<ValidatorConfig> result = new ArrayList<ValidatorConfig>(loadFile(fileName, aClass, checkFile));

        AnnotationValidationConfigurationBuilder builder = new AnnotationValidationConfigurationBuilder(validatorFactory);

        List<ValidatorConfig> annotationResult = new ArrayList<ValidatorConfig>(builder.buildAnnotationClassValidatorConfigs(aClass));

        result.addAll(annotationResult);

        return result;

    }

    /**
     * <p>This method 'collects' all the validator configurations for a given
     * action invocation.</p>
     * <p/>
     * <p>It will traverse up the class hierarchy looking for validators for every super class
     * and directly implemented interface of the current action, as well as adding validators for
     * any alias of this invocation. Nifty!</p>
     * <p/>
     * <p>Given the following class structure:
     * <pre>
     *   interface Thing;
     *   interface Animal extends Thing;
     *   interface Quadraped extends Animal;
     *   class AnimalImpl implements Animal;
     *   class QuadrapedImpl extends AnimalImpl implements Quadraped;
     *   class Dog extends QuadrapedImpl;
     * </pre></p>
     * <p/>
     * <p>This method will look for the following config files for Dog:
     * <pre>
     *   Animal
     *   Animal-context
     *   AnimalImpl
     *   AnimalImpl-context
     *   Quadraped
     *   Quadraped-context
     *   QuadrapedImpl
     *   QuadrapedImpl-context
     *   Dog
     *   Dog-context
     * </pre></p>
     * <p/>
     * <p>Note that the validation rules for Thing is never looked for because no class in the
     * hierarchy directly implements Thing.</p>
     *
     * @param clazz     the Class to look up validators for.
     * @param context   the context to use when looking up validators.
     * @param checkFile true if the validation config file should be checked to see if it has been
     *                  updated.
     * @param checked   the set of previously checked class-contexts, null if none have been checked
     * @return a list of validator configs for the given class and context.
     */
    private List<ValidatorConfig> buildValidatorConfigs(Class clazz, String context, boolean checkFile, Set<String> checked) {
        List<ValidatorConfig> validatorConfigs = new ArrayList<ValidatorConfig>();

        if (checked == null) {
            checked = new TreeSet<String>();
        } else if (checked.contains(clazz.getName())) {
            return validatorConfigs;
        }

        if (clazz.isInterface()) {
            Class[] interfaces = clazz.getInterfaces();

            for (Class anInterface : interfaces) {
                validatorConfigs.addAll(buildValidatorConfigs(anInterface, context, checkFile, checked));
            }
        } else {
            if (!clazz.equals(Object.class)) {
                validatorConfigs.addAll(buildValidatorConfigs(clazz.getSuperclass(), context, checkFile, checked));
            }
        }

        // look for validators for implemented interfaces
        Class[] interfaces = clazz.getInterfaces();

        for (Class anInterface1 : interfaces) {
            if (checked.contains(anInterface1.getName())) {
                continue;
            }

            validatorConfigs.addAll(buildClassValidatorConfigs(anInterface1, checkFile));

            if (context != null) {
                validatorConfigs.addAll(buildAliasValidatorConfigs(anInterface1, context, checkFile));
            }

            checked.add(anInterface1.getName());
        }

        validatorConfigs.addAll(buildClassValidatorConfigs(clazz, checkFile));

        if (context != null) {
            validatorConfigs.addAll(buildAliasValidatorConfigs(clazz, context, checkFile));
        }

        checked.add(clazz.getName());

        return validatorConfigs;
    }

    private List<ValidatorConfig> loadFile(String fileName, Class clazz, boolean checkFile) {
        List<ValidatorConfig> retList = Collections.emptyList();

        if ((checkFile && FileManager.fileNeedsReloading(fileName, clazz)) || !validatorFileCache.containsKey(fileName)) {
            InputStream is = null;

            try {
                is = FileManager.loadFile(fileName, clazz);

                if (is != null) {
                    retList = new ArrayList<ValidatorConfig>(validatorFileParser.parseActionValidatorConfigs(validatorFactory, is, fileName));
                }
            } catch (Exception e) {
                LOG.error("Caught exception while loading file " + fileName, e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        LOG.error("Unable to close input stream for " + fileName, e);
                    }
                }
            }

            validatorFileCache.put(fileName, retList);
        } else {
            retList = validatorFileCache.get(fileName);
        }

        return retList;
    }


    /**
     * An {@link com.opensymphony.xwork2.validator.ValidatorContext} wrapper that
     * returns the full field name
     * {@link AnnotationActionValidatorManager.InternalValidatorContextWrapper#getFullFieldName(String)}
     * by consulting it's parent if its an {@link com.opensymphony.xwork2.validator.validators.VisitorFieldValidator.AppendingValidatorContext}.
     * <p/>
     * Eg. if we have nested Visitor
     * AddressVisitor nested inside PersonVisitor, when using the normal #getFullFieldName, we will get
     * "address.somefield", we lost the parent, with this wrapper, we will get "person.address.somefield".
     * This is so that the key is used to register errors, so that we don't screw up short-curcuit feature
     * when using nested visitor. See XW-571 (nested visitor validators break short-circuit functionality)
     * at http://jira.opensymphony.com/browse/XW-571
     */
    protected class InternalValidatorContextWrapper {
        private ValidatorContext validatorContext = null;

        InternalValidatorContextWrapper(ValidatorContext validatorContext) {
            this.validatorContext = validatorContext;
        }

        /**
         * Get the full field name by consulting the parent, so that when we are using nested visitors (
         * visitor nested inside visitor etc.) we still get the full field name including its parents.
         * See XW-571 for more details.
         *
         * @param field
         * @return String
         */
        public String getFullFieldName(String field) {
            if (validatorContext instanceof VisitorFieldValidator.AppendingValidatorContext) {
                VisitorFieldValidator.AppendingValidatorContext appendingValidatorContext =
                        (VisitorFieldValidator.AppendingValidatorContext) validatorContext;
                return appendingValidatorContext.getFullFieldNameFromParent(field);
            }
            return validatorContext.getFullFieldName(field);
        }

    }
}
