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
package org.apache.struts2.factory;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.factory.ResultFactory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.reflection.ReflectionException;
import com.opensymphony.xwork2.util.reflection.ReflectionExceptionHandler;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import com.opensymphony.xwork2.result.ParamNameAwareResult;

import java.util.Map;

/**
 * Default implementation which uses {@link com.opensymphony.xwork2.result.ParamNameAwareResult} to accept or throw away parameters
 */
public class StrutsResultFactory implements ResultFactory {

    protected ObjectFactory objectFactory;
    protected ReflectionProvider reflectionProvider;

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Inject
    public void setReflectionProvider(ReflectionProvider provider) {
        this.reflectionProvider = provider;
    }

    public Result buildResult(ResultConfig resultConfig, Map<String, Object> extraContext) throws Exception {
        String resultClassName = resultConfig.getClassName();
        Result result = null;

        if (resultClassName != null) {
            result = (Result) objectFactory.buildBean(resultClassName, extraContext);
            Map<String, String> params = resultConfig.getParams();
            if (params != null) {
                setParameters(extraContext, result, params);
            }
        }
        return result;
    }

    protected void setParameters(Map<String, Object> extraContext, Result result, Map<String, String> params) {
        for (Map.Entry<String, String> paramEntry : params.entrySet()) {
            try {
                String name = paramEntry.getKey();
                String value = paramEntry.getValue();
                setParameter(result, name, value, extraContext);
            } catch (ReflectionException ex) {
                if (result instanceof ReflectionExceptionHandler) {
                    ((ReflectionExceptionHandler) result).handle(ex);
                }
            }
        }
    }

    protected void setParameter(Result result, String name, String value, Map<String, Object> extraContext) {
        if (result instanceof ParamNameAwareResult) {
            if (((ParamNameAwareResult) result).acceptableParameterName(name, value)) {
                reflectionProvider.setProperty(name, value, result, extraContext, true);
            }
        } else {
            reflectionProvider.setProperty(name, value, result, extraContext, true);
        }
    }

}
