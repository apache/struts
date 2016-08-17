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
package org.apache.struts2.convention;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultTypeConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.AnnotationUtils;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import org.apache.struts2.convention.annotation.ResultPath;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * <p>
 * This class is the implementation of the {@link ConventionsService}
 * interface and provides all of the defaults and annotation handling.
 * </p>
 */
public class ConventionsServiceImpl implements ConventionsService {
    private String resultPath;

    /**
     * Constructs a new instance.
     *
     * @param   resultPath The result path that is configured in the Struts configuration files using
     *          the constant name of <strong>struts.convention.result.path</strong>.
     */
    @Inject
    public ConventionsServiceImpl(@Inject("struts.convention.result.path") String resultPath) {
        this.resultPath = resultPath;
    }

    /**
     * {@inheritDoc}
     */
    public String determineResultPath(Class<?> actionClass) {
        String localResultPath = resultPath;
        ResultPath resultPathAnnotation = AnnotationUtils.findAnnotation(actionClass, ResultPath.class);
        if (resultPathAnnotation != null) {
            if (resultPathAnnotation.value().equals("") && resultPathAnnotation.property().equals("")) {
                throw new ConfigurationException("The ResultPath annotation must have either" +
                    " a value or property specified.");
            }

            String property = resultPathAnnotation.property();
            if (property.equals("")) {
                localResultPath = resultPathAnnotation.value();
            } else {
                try {
                    ResourceBundle strutsBundle = ResourceBundle.getBundle("struts");
                    localResultPath = strutsBundle.getString(property);
                } catch (Exception e) {
                    throw new ConfigurationException("The action class [" + actionClass + "] defines" +
                        " a @ResultPath annotation and a property definition however the" +
                        " struts.properties could not be found in the classpath using ResourceBundle" +
                        " OR the bundle exists but the property [" + property + "] is not defined" +
                        " in the file.", e);
                }
            }
        }

        return localResultPath;
    }

    /**
     * {@inheritDoc}
     */
    public String determineResultPath(ActionConfig actionConfig) {
        if (actionConfig == null) {
            return resultPath;
        }

        try {
            return  determineResultPath(ClassLoaderUtil.loadClass(actionConfig.getClassName(), this.getClass()));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Invalid action class configuration that references an unknown " +
                "class named [" + actionConfig.getClassName() + "]", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, ResultTypeConfig> getResultTypesByExtension(PackageConfig packageConfig) {
        Map<String, ResultTypeConfig> results = packageConfig.getAllResultTypeConfigs();

        ResultTypeConfig dispatcher = disableParse(results.get("dispatcher"));
        ResultTypeConfig velocity = disableParse(results.get("velocity"));
        ResultTypeConfig freemarker = disableParse(results.get("freemarker"));

        Map<String, ResultTypeConfig> resultsByExtension = new HashMap<String, ResultTypeConfig>();
        resultsByExtension.put("jsp", dispatcher);
        resultsByExtension.put("jspf", dispatcher);
        resultsByExtension.put("jspx", dispatcher);
        resultsByExtension.put("vm", velocity);
        resultsByExtension.put("ftl", freemarker);
        resultsByExtension.put("html", dispatcher);
        resultsByExtension.put("htm", dispatcher);
        return resultsByExtension;
    }

    private ResultTypeConfig disableParse(ResultTypeConfig resultConfig) {
        if (resultConfig != null) {
            return new ResultTypeConfig.Builder(resultConfig).addParam("parse", "false").build();
        }
        return null;
    }

}
