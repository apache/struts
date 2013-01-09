/*
 * Copyright 2002-2003,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionChainResult;
import com.opensymphony.xwork2.ModelDrivenAction;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.SimpleAction;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.ModelDrivenInterceptor;
import com.opensymphony.xwork2.interceptor.ParametersInterceptor;
import com.opensymphony.xwork2.interceptor.StaticParametersInterceptor;
import com.opensymphony.xwork2.mock.MockResult;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.validator.ValidationInterceptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * MockConfigurationProvider provides a simple configuration class without the need for xml files, etc. for simple testing.
 *
 * @author $author$
 * @version $Revision$
 */
public class MockConfigurationProvider implements ConfigurationProvider {

    public static final String FOO_ACTION_NAME = "foo";
    public static final String MODEL_DRIVEN_PARAM_TEST = "modelParamTest";
    public static final String MODEL_DRIVEN_PARAM_FILTER_TEST  = "modelParamFilterTest";
    public static final String PARAM_INTERCEPTOR_ACTION_NAME = "parametersInterceptorTest";
    public static final String VALIDATION_ACTION_NAME = "validationInterceptorTest";
    public static final String VALIDATION_ALIAS_NAME = "validationAlias";
    public static final String VALIDATION_SUBPROPERTY_NAME = "subproperty";
    public static final String EXPRESSION_VALIDATION_ACTION = "expressionValidationAction";

    private static final Map<String,String> EMPTY_STRING_MAP = Collections.emptyMap();

    private Configuration configuration;
    private Map<String,String> params;
    private ObjectFactory objectFactory;

    public MockConfigurationProvider() {}
    public MockConfigurationProvider(Map<String,String> params) {
        this.params = params;
    }

    /**
     * Allows the configuration to clean up any resources used
     */
    public void destroy() {
    }
    
    public void init(Configuration config) {
        this.configuration = config;
    }
    
    @Inject
    public void setObjectFactory(ObjectFactory fac) {
        this.objectFactory = fac;
    }

    public void loadPackages() {
        
        PackageConfig.Builder defaultPackageContext = new PackageConfig.Builder("defaultPackage");
        Map<String, String> params = new HashMap<String, String>();
        params.put("bar", "5");

        Map<String, ResultConfig> results = new HashMap<String, ResultConfig>();
        Map<String, String> successParams = new HashMap<String, String>();
        successParams.put("actionName", "bar");
        results.put("success", new ResultConfig.Builder("success", ActionChainResult.class.getName()).addParams(successParams).build());

        ActionConfig fooActionConfig = new ActionConfig.Builder("defaultPackage", FOO_ACTION_NAME, SimpleAction.class.getName())
            .addResultConfig(new ResultConfig.Builder(Action.ERROR, MockResult.class.getName()).build())
            .build();
        defaultPackageContext.addActionConfig(FOO_ACTION_NAME, fooActionConfig);

        results = new HashMap<String, ResultConfig>();
        successParams = new HashMap<String, String>();
        successParams.put("actionName", "bar");
        results.put("success", new ResultConfig.Builder("success", ActionChainResult.class.getName()).addParams(successParams).build());

        List<InterceptorMapping> interceptors = new ArrayList<InterceptorMapping>();
        interceptors.add(new InterceptorMapping("params", new ParametersInterceptor()));

        ActionConfig paramInterceptorActionConfig = new ActionConfig.Builder("defaultPackage", PARAM_INTERCEPTOR_ACTION_NAME, SimpleAction.class.getName())
            .addResultConfig(new ResultConfig.Builder(Action.ERROR, MockResult.class.getName()).build())
            .addInterceptors(interceptors)
            .build();
        defaultPackageContext.addActionConfig(PARAM_INTERCEPTOR_ACTION_NAME, paramInterceptorActionConfig);

        interceptors = new ArrayList<InterceptorMapping>();
        interceptors.add(new InterceptorMapping("model", 
                objectFactory.buildInterceptor(new InterceptorConfig.Builder("model", ModelDrivenInterceptor.class.getName()).build(), EMPTY_STRING_MAP)));
        interceptors.add(new InterceptorMapping("params",
                objectFactory.buildInterceptor(new InterceptorConfig.Builder("model", ParametersInterceptor.class.getName()).build(), EMPTY_STRING_MAP)));

        ActionConfig modelParamActionConfig = new ActionConfig.Builder("defaultPackage", MODEL_DRIVEN_PARAM_TEST, ModelDrivenAction.class.getName())
            .addInterceptors(interceptors)
            .addResultConfig(new ResultConfig.Builder(Action.SUCCESS, MockResult.class.getName()).build())
            .build();
        defaultPackageContext.addActionConfig(MODEL_DRIVEN_PARAM_TEST, modelParamActionConfig);
        
        //List paramFilterInterceptor=new ArrayList();
        //paramFilterInterceptor.add(new ParameterFilterInterC)
        //ActionConfig modelParamFilterActionConfig = new ActionConfig(null, ModelDrivenAction.class, null, null, interceptors);
        

        results = new HashMap<String, ResultConfig>();
        successParams = new HashMap<String, String>();
        successParams.put("actionName", "bar");
        results.put("success", new ResultConfig.Builder("success", ActionChainResult.class.getName()).addParams(successParams).build());
        results.put(Action.ERROR, new ResultConfig.Builder(Action.ERROR, MockResult.class.getName()).build());

        interceptors = new ArrayList<InterceptorMapping>();
        interceptors.add(new InterceptorMapping("staticParams", 
                objectFactory.buildInterceptor(new InterceptorConfig.Builder("model", StaticParametersInterceptor.class.getName()).build(), EMPTY_STRING_MAP)));
        interceptors.add(new InterceptorMapping("model", 
                objectFactory.buildInterceptor(new InterceptorConfig.Builder("model", ModelDrivenInterceptor.class.getName()).build(), EMPTY_STRING_MAP)));
        interceptors.add(new InterceptorMapping("params", 
                objectFactory.buildInterceptor(new InterceptorConfig.Builder("model", ParametersInterceptor.class.getName()).build(), EMPTY_STRING_MAP)));
        interceptors.add(new InterceptorMapping("validation", 
                objectFactory.buildInterceptor(new InterceptorConfig.Builder("model", ValidationInterceptor.class.getName()).build(), EMPTY_STRING_MAP)));

        //Explicitly set an out-of-range date for DateRangeValidatorTest
        params = new HashMap<String, String>();
        ActionConfig validationActionConfig = new ActionConfig.Builder("defaultPackage", VALIDATION_ACTION_NAME, SimpleAction.class.getName())
            .addInterceptors(interceptors)
            .addParams(params)
            .addResultConfigs(results)
            .build();
        defaultPackageContext.addActionConfig(VALIDATION_ACTION_NAME, validationActionConfig);
        defaultPackageContext.addActionConfig(VALIDATION_ALIAS_NAME,
                new ActionConfig.Builder(validationActionConfig).name(VALIDATION_ALIAS_NAME).build());
        defaultPackageContext.addActionConfig(VALIDATION_SUBPROPERTY_NAME,
                new ActionConfig.Builder(validationActionConfig).name(VALIDATION_SUBPROPERTY_NAME).build());


        params = new HashMap<String, String>();
        ActionConfig percentageActionConfig = new ActionConfig.Builder("defaultPackage", "percentage", SimpleAction.class.getName())
                .addParams(params)
                .addResultConfigs(results)
                .addInterceptors(interceptors)
                .build();
        defaultPackageContext.addActionConfig(percentageActionConfig.getName(), percentageActionConfig);

        // We need this actionconfig to be the final destination for action chaining
        ActionConfig barActionConfig = new ActionConfig.Builder("defaultPackage", "bar", SimpleAction.class.getName())
                .addResultConfig(new ResultConfig.Builder(Action.ERROR, MockResult.class.getName()).build())
                .build();
        defaultPackageContext.addActionConfig(barActionConfig.getName(), barActionConfig);

        ActionConfig expressionValidationActionConfig = new ActionConfig.Builder("defaultPackage", EXPRESSION_VALIDATION_ACTION, SimpleAction.class.getName())
                .addInterceptors(interceptors)
                .addResultConfigs(results)
                .build();
        defaultPackageContext.addActionConfig(EXPRESSION_VALIDATION_ACTION, expressionValidationActionConfig);

        configuration.addPackageConfig("defaultPackage", defaultPackageContext.build());
    }

    /**
     * Tells whether the ConfigurationProvider should reload its configuration
     *
     * @return false
     */
    public boolean needsReload() {
        return false;
    }

    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
        if (params != null) {
            for (String key : params.keySet()) {
                props.setProperty(key, params.get(key));
            }
        }
    }
}
