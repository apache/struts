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
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

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
    private static final Logger LOG = LogManager.getLogger(AnnotationActionValidatorManager.class);

    private ValidatorFactory validatorFactory;
    private ValidatorFileParser validatorFileParser;
    private FileManager fileManager;
    private boolean reloadingConfigs;
    private TextProviderFactory textProviderFactory;

    @Inject
    public void setValidatorFactory(ValidatorFactory fac) {
        this.validatorFactory = fac;
    }

    @Inject
    public void setValidatorFileParser(ValidatorFileParser parser) {
        this.validatorFileParser = parser;
    }

    @Inject
    public void setFileManagerFactory(FileManagerFactory fileManagerFactory) {
        this.fileManager = fileManagerFactory.getFileManager();
    }

    @Inject(value = XWorkConstants.RELOAD_XML_CONFIGURATION, required = false)
    public void setReloadingConfigs(String reloadingConfigs) {
        this.reloadingConfigs = Boolean.parseBoolean(reloadingConfigs);
    }

    @Inject
    public void setTextProviderFactory(TextProviderFactory textProviderFactory) {
        this.textProviderFactory = textProviderFactory;
    }

    public List<Validator> getValidators(Class clazz, String context) {
        return getValidators(clazz, context, null);
    }

    public List<Validator> getValidators(Class clazz, String context, String method) {
        final String validatorKey = buildValidatorKey(clazz, context);
        final List<ValidatorConfig> cfgs;

        if (validatorCache.containsKey(validatorKey)) {
            if (reloadingConfigs) {
                validatorCache.put(validatorKey, buildValidatorConfigs(clazz, context, true, null));
            }
        } else {
            validatorCache.put(validatorKey, buildValidatorConfigs(clazz, context, false, null));
        }

        // get the set of validator configs
        cfgs = new ArrayList<ValidatorConfig>(validatorCache.get(validatorKey));

        ValueStack stack = ActionContext.getContext().getValueStack();

        // create clean instances of the validators for the caller's use
        ArrayList<Validator> validators = new ArrayList<>(cfgs.size());
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
        ValidatorContext validatorContext = new DelegatingValidatorContext(object, textProviderFactory);
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

                LOG.debug("Running validator: {} for object {} and method {}", validator, object, method);

                FieldValidator fValidator = null;
                String fullFieldName = null;

                if (validator instanceof FieldValidator) {
                    fValidator = (FieldValidator) validator;
                    fullFieldName = fValidator.getValidatorContext().getFullFieldName(fValidator.getFieldName());

                    if ((shortcircuitedFields != null) && shortcircuitedFields.contains(fullFieldName)) {
                        LOG.debug("Short-circuited, skipping");
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
                                errs = new ArrayList<>(fieldErrors);
                            }
                        }
                    } else if (validatorContext.hasActionErrors()) {
                        Collection<String> actionErrors = validatorContext.getActionErrors();

                        if (actionErrors != null) {
                            errs = new ArrayList<>(actionErrors);
                        }
                    }

                    validator.validate(object);

                    if (fValidator != null) {
                        if (validatorContext.hasFieldErrors()) {
                            Collection<String> errCol = validatorContext.getFieldErrors().get(fullFieldName);

                            if ((errCol != null) && !errCol.equals(errs)) {
                                LOG.debug("Short-circuiting on field validation");

                                if (shortcircuitedFields == null) {
                                    shortcircuitedFields = new TreeSet<String>();
                                }

                                shortcircuitedFields.add(fullFieldName);
                            }
                        }
                    } else if (validatorContext.hasActionErrors()) {
                        Collection<String> errCol = validatorContext.getActionErrors();

                        if ((errCol != null) && !errCol.equals(errs)) {
                            LOG.debug("Short-circuiting");
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
     * @param context context
     * @return a validator key which is the class name plus context.
     */
    protected String buildValidatorKey(Class clazz, String context) {
        ActionInvocation invocation = ActionContext.getContext().getActionInvocation();
        ActionProxy proxy = invocation.getProxy();
        ActionConfig config = proxy.getConfig();

        StringBuilder sb = new StringBuilder(clazz.getName());
        sb.append("/");
        if (StringUtils.isNotBlank(config.getPackageName())) {
            sb.append(config.getPackageName());
            sb.append("/");
        }

        // the key needs to use the name of the action from the config file,
        // instead of the url, so wild card actions will have the same validator
        // see WW-2996

        // UPDATE:
        // WW-3753 Using the config name instead of the context only for
        // wild card actions to keep the flexibility provided
        // by the original design (such as mapping different contexts
        // to the same action and method if desired)

        // UPDATE:
        // WW-4536 Using NameVariablePatternMatcher allows defines actions
        // with patterns enclosed with '{}', it's similar case to WW-3753
        String configName = config.getName();
        if (configName.contains(ActionConfig.WILDCARD) || (configName.contains("{") && configName.contains("}"))) {
            sb.append(configName);
            sb.append("|");
            sb.append(proxy.getMethod());
        } else {
            sb.append(context);
        }
        
        return sb.toString();
    }

    private List<ValidatorConfig> buildAliasValidatorConfigs(Class aClass, String context, boolean checkFile) {
        String fileName = aClass.getName().replace('.', '/') + "-" + context.replace('/', '-') + VALIDATION_CONFIG_SUFFIX;

        return loadFile(fileName, aClass, checkFile);
    }


    protected List<ValidatorConfig> buildClassValidatorConfigs(Class aClass, boolean checkFile) {

        String fileName = aClass.getName().replace('.', '/') + VALIDATION_CONFIG_SUFFIX;

        List<ValidatorConfig> result = new ArrayList<>(loadFile(fileName, aClass, checkFile));

        AnnotationValidationConfigurationBuilder builder = new AnnotationValidationConfigurationBuilder(validatorFactory);

        List<ValidatorConfig> annotationResult = new ArrayList<>(builder.buildAnnotationClassValidatorConfigs(aClass));

        result.addAll(annotationResult);

        return result;

    }

    /**
     * <p>This method 'collects' all the validator configurations for a given
     * action invocation.</p>
     *
     * <p>It will traverse up the class hierarchy looking for validators for every super class
     * and directly implemented interface of the current action, as well as adding validators for
     * any alias of this invocation. Nifty!</p>
     *
     * <p>Given the following class structure:</p>
     * <pre>
     *   interface Thing;
     *   interface Animal extends Thing;
     *   interface Quadraped extends Animal;
     *   class AnimalImpl implements Animal;
     *   class QuadrapedImpl extends AnimalImpl implements Quadraped;
     *   class Dog extends QuadrapedImpl;
     * </pre>
     *
     * <p>This method will look for the following config files for Dog:</p>
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
     * </pre>
     *
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
        List<ValidatorConfig> validatorConfigs = new ArrayList<>();

        if (checked == null) {
            checked = new TreeSet<>();
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

        URL fileUrl = ClassLoaderUtil.getResource(fileName, clazz);

        if ((checkFile && fileManager.fileNeedsReloading(fileUrl)) || !validatorFileCache.containsKey(fileName)) {
            try (InputStream is = fileManager.loadFile(fileUrl)) {
                if (is != null) {
                    retList = new ArrayList<>(validatorFileParser.parseActionValidatorConfigs(validatorFactory, is, fileName));
                }
            } catch (IOException e) {
                LOG.error("Caught exception while loading file {}", fileName, e);
            }

            validatorFileCache.put(fileName, retList);
        } else {
            retList = validatorFileCache.get(fileName);
        }

        return retList;
    }
}
