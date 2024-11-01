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
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.ActionProxy;
import org.apache.struts2.config.entities.ActionConfig;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * AnnotationActionValidatorManager is the entry point into XWork's annotations-based validator framework.
 * Validation rules are specified as annotations within the source files.
 *
 * @author Rainer Hermanns
 * @author jepjep
 */
public class AnnotationActionValidatorManager extends DefaultActionValidatorManager {

    @Override
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
        // WW-2996: key needs to use the name of the action from the config file, instead of the url,
        // so wildcard actions will have the same validator
        // WW-3753: Using the config name instead of the context only for wildcard actions to keep the flexibility
        // provided by the original design (such as mapping different contexts to the same action and method if desired)
        // WW-4536: Using NamedVariablePatternMatcher allows defines actions with patterns enclosed with '{}'
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
