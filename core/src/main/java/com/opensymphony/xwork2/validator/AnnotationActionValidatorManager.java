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
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * AnnotationActionValidatorManager is the entry point into XWork's annotations-based validator framework.
 * Validation rules are specified as annotations within the source files.
 *
 * @author Rainer Hermanns
 * @author jepjep
 */
public class AnnotationActionValidatorManager extends AbstractActionValidatorManager {

    private static final Logger LOG = LogManager.getLogger(AnnotationActionValidatorManager.class);

    @Override
    public List<Validator> getValidators(Class clazz, String context) {
        return getValidators(clazz, context, null);
    }

    @Override
    public List<Validator> getValidators(Class clazz, String context, String method) {
        String validatorKey = buildValidatorKey(clazz, context);

        if (validatorCache.containsKey(validatorKey)) {
            if (reloadingConfigs) {
                validatorCache.put(validatorKey, buildValidatorConfigs(clazz, context, true, null));
            }
        } else {
            validatorCache.put(validatorKey, buildValidatorConfigs(clazz, context, false, null));
        }

        // get the set of validator configs
        List<ValidatorConfig> cfgs = new ArrayList<>(validatorCache.get(validatorKey));

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

    @Override
    protected List<ValidatorConfig> buildAliasValidatorConfigs(Class aClass, String context, boolean checkFile) {
        String fileName = aClass.getName().replace('.', '/') + "-" + context.replace('/', '-') + VALIDATION_CONFIG_SUFFIX;
        return loadFile(fileName, aClass, checkFile);
    }

    @Override
    protected List<ValidatorConfig> buildClassValidatorConfigs(Class aClass, boolean checkFile) {
        String fileName = aClass.getName().replace('.', '/') + VALIDATION_CONFIG_SUFFIX;
        List<ValidatorConfig> result = new ArrayList<>(loadFile(fileName, aClass, checkFile));
        AnnotationValidationConfigurationBuilder builder = new AnnotationValidationConfigurationBuilder(validatorFactory);
        List<ValidatorConfig> annotationResult = new ArrayList<>(builder.buildAnnotationClassValidatorConfigs(aClass));
        result.addAll(annotationResult);
        return result;
    }
}
