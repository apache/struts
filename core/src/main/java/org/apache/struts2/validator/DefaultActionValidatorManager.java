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
package org.apache.struts2.validator;

import org.apache.struts2.ActionContext;
import org.apache.struts2.FileManager;
import org.apache.struts2.FileManagerFactory;
import org.apache.struts2.text.TextProviderFactory;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.util.ClassLoaderUtil;
import org.apache.struts2.util.ValueStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Collections.synchronizedMap;

/**
 * <p>
 * This is the entry point into XWork's rule-based validation framework.
 * </p>
 *
 * <p>
 * Validation rules are specified in XML configuration files named <code>className-contextName-validation.xml</code> where
 * className is the name of the class the configuration is for and -contextName is optional
 * (contextName is an arbitrary key that is used to look up additional validation rules for a
 * specific context).
 * </p>
 *
 * @author Jason Carreira
 * @author Mark Woon
 * @author James House
 * @author Rainer Hermanns
 */
public class DefaultActionValidatorManager implements ActionValidatorManager {

    /**
     * The file suffix for any validation file.
     */
    protected static final String VALIDATION_CONFIG_SUFFIX = "-validation.xml";

    protected final Map<String, List<ValidatorConfig>> validatorCache = synchronizedMap(new HashMap<>());
    protected final Map<String, List<ValidatorConfig>> validatorFileCache = synchronizedMap(new HashMap<>());
    private static final Logger LOG = LogManager.getLogger(DefaultActionValidatorManager.class);

    protected ValidatorFactory validatorFactory;
    protected ValidatorFileParser validatorFileParser;
    protected FileManager fileManager;
    protected boolean reloadingConfigs;
    protected TextProviderFactory textProviderFactory;

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

    @Inject(value = StrutsConstants.STRUTS_CONFIGURATION_XML_RELOAD, required = false)
    public void setReloadingConfigs(String reloadingConfigs) {
        this.reloadingConfigs = Boolean.parseBoolean(reloadingConfigs);
    }

    @Inject
    public void setTextProviderFactory(TextProviderFactory textProviderFactory) {
        this.textProviderFactory = textProviderFactory;
    }

    @Override
    public void validate(Object object, String context) throws ValidationException {
        validate(object, context, (String) null);
    }

    @Override
    public void validate(Object object, String context, String method) throws ValidationException {
        ValidatorContext validatorContext = new DelegatingValidatorContext(object, textProviderFactory);
        validate(object, context, validatorContext, method);
    }

    @Override
    public void validate(Object object, String context, ValidatorContext validatorContext) throws ValidationException {
        validate(object, context, validatorContext, null);
    }

    /**
     * Builds a key for validators - used when caching validators.
     *
     * @param clazz the action.
     * @param context context
     * @return a validator key which is the class name plus context.
     */
    protected String buildValidatorKey(Class<?> clazz, String context) {
        return clazz.getName() + "/" + context;
    }

    protected Validator getValidatorFromValidatorConfig(ValidatorConfig config, ValueStack stack) {
        Validator validator = validatorFactory.getValidator(config);
        validator.setValidatorType(config.getType());
        validator.setValueStack(stack);
        return validator;
    }

    @Override
    public synchronized List<Validator> getValidators(Class<?> clazz, String context, String method) {
        String validatorKey = buildValidatorKey(clazz, context);

        if (!validatorCache.containsKey(validatorKey)) {
            validatorCache.put(validatorKey, buildValidatorConfigs(clazz, context, false, null));
        } else if (reloadingConfigs) {
            validatorCache.put(validatorKey, buildValidatorConfigs(clazz, context, true, null));
        }

        ValueStack stack = ActionContext.getContext().getValueStack();
        List<ValidatorConfig> configs = validatorCache.get(validatorKey);
        List<Validator> validators = new ArrayList<>();
        for (ValidatorConfig config : configs) {
            if (method == null || method.equals(config.getParams().get("methodName"))) {
                validators.add(getValidatorFromValidatorConfig(config, stack));
            }
        }
        return validators;
    }

    @Override
    public synchronized List<Validator> getValidators(Class<?> clazz, String context) {
        return getValidators(clazz, context, null);
    }

    @Override
    public void validate(Object object, String context, ValidatorContext validatorContext, String method) throws ValidationException {
        List<Validator> validators = getValidators(object.getClass(), context, method);
        Set<String> shortcircuitedFields = null;

        for (Validator validator : validators) {
            validator.setValidatorContext(validatorContext);

            LOG.debug("Running validator: {} for object {} and method {}", validator, object, method);

            FieldValidator fValidator = null;
            String fullFieldName = null;

            if (validator instanceof FieldValidator) {
                fValidator = (FieldValidator) validator;
                fullFieldName = validatorContext.getFullFieldName(fValidator.getFieldName());

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
                                shortcircuitedFields = new TreeSet<>();
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
        }
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
    protected List<ValidatorConfig> buildValidatorConfigs(Class<?> clazz, String context, boolean checkFile, Set<String> checked) {
        List<ValidatorConfig> validatorConfigs = new ArrayList<>();

        if (checked == null) {
            checked = new TreeSet<>();
        } else if (checked.contains(clazz.getName())) {
            return validatorConfigs;
        }

        if (clazz.isInterface()) {
            for (Class<?> anInterface : clazz.getInterfaces()) {
                validatorConfigs.addAll(buildValidatorConfigs(anInterface, context, checkFile, checked));
            }
        } else {
            if (!clazz.equals(Object.class)) {
                validatorConfigs.addAll(buildValidatorConfigs(clazz.getSuperclass(), context, checkFile, checked));
            }
        }

        // look for validators for implemented interfaces
        for (Class<?> anInterface1 : clazz.getInterfaces()) {
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

    protected List<ValidatorConfig> buildAliasValidatorConfigs(Class<?> aClass, String context, boolean checkFile) {
        String fileName = aClass.getName().replace('.', '/') + "-" + context + VALIDATION_CONFIG_SUFFIX;
        return loadFile(fileName, aClass, checkFile);
    }

    protected List<ValidatorConfig> buildClassValidatorConfigs(Class<?> aClass, boolean checkFile) {
        String fileName = aClass.getName().replace('.', '/') + VALIDATION_CONFIG_SUFFIX;
        return loadFile(fileName, aClass, checkFile);
    }

    protected List<ValidatorConfig> loadFile(String fileName, Class<?> clazz, boolean checkFile) {
        List<ValidatorConfig> retList = Collections.emptyList();

        URL fileUrl = ClassLoaderUtil.getResource(fileName, clazz);

        if ((checkFile && fileManager.fileNeedsReloading(fileUrl)) || !validatorFileCache.containsKey(fileName)) {
            try (InputStream is = fileManager.loadFile(fileUrl)) {
                if (is != null) {
                    retList = new ArrayList<>(validatorFileParser.parseActionValidatorConfigs(validatorFactory, is, fileName));
                }
            } catch (IOException e) {
                LOG.error("Caught exception while closing file {}", fileName, e);
            }

            validatorFileCache.put(fileName, retList);
        } else {
            retList = validatorFileCache.get(fileName);
        }

        return retList;
    }
}
