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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

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
public class DefaultActionValidatorManager extends AbstractActionValidatorManager {

    private final static Logger LOG = LogManager.getLogger(DefaultActionValidatorManager.class);

    @Override
    public synchronized List<Validator> getValidators(Class clazz, String context) {
        return getValidators(clazz, context, null);
    }

    @Override
    public synchronized List<Validator> getValidators(Class clazz, String context, String method) {
        String validatorKey = buildValidatorKey(clazz, context);

        if (validatorCache.containsKey(validatorKey)) {
            if (reloadingConfigs) {
                validatorCache.put(validatorKey, buildValidatorConfigs(clazz, context, true, null));
            }
        } else {
            validatorCache.put(validatorKey, buildValidatorConfigs(clazz, context, false, null));
        }
        ValueStack stack = ActionContext.getContext().getValueStack();

        // get the set of validator configs
        List<ValidatorConfig> cfgs = validatorCache.get(validatorKey);

        // create clean instances of the validators for the caller's use
        ArrayList<Validator> validators = new ArrayList<>(cfgs.size());
        for (ValidatorConfig cfg : cfgs) {
            if (method == null || method.equals(cfg.getParams().get("methodName"))) {
                Validator validator = validatorFactory.getValidator(cfg);
                validator.setValidatorType(cfg.getType());
                validator.setValueStack(stack);
                validators.add(validator);
            }
        }
        return validators;
    }

    /**
     * Builds a key for validators - used when caching validators.
     *
     * @param clazz the action.
     * @param context the action's context.
     * @return a validator key which is the class name plus context.
     */
    protected static String buildValidatorKey(Class clazz, String context) {
        return clazz.getName() + "/" + context;
    }

    @Override
    protected List<ValidatorConfig> buildAliasValidatorConfigs(Class aClass, String context, boolean checkFile) {
        String fileName = aClass.getName().replace('.', '/') + "-" + context + VALIDATION_CONFIG_SUFFIX;
        return loadFile(fileName, aClass, checkFile);
    }

    @Override
    protected List<ValidatorConfig> buildClassValidatorConfigs(Class aClass, boolean checkFile) {
        String fileName = aClass.getName().replace('.', '/') + VALIDATION_CONFIG_SUFFIX;
        return loadFile(fileName, aClass, checkFile);
    }
}
