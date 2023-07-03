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

package org.apache.struts2.tiles.el;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import jakarta.el.ELContext;
import jakarta.el.ELResolver;
import jakarta.el.FunctionMapper;
import jakarta.el.ValueExpression;
import jakarta.el.VariableMapper;

public class ELContextImpl extends ELContext {
    private static final FunctionMapper NULL_FUNCTION_MAPPER = new FunctionMapper() {
        public Method resolveFunction(String prefix, String localName) {
            return null;
        }
    };
    private final ELResolver resolver;
    private FunctionMapper functionMapper;
    private VariableMapper variableMapper;

    public ELContextImpl(ELResolver resolver) {
        this.functionMapper = NULL_FUNCTION_MAPPER;
        this.resolver = resolver;
    }

    public ELResolver getELResolver() {
        return this.resolver;
    }

    public FunctionMapper getFunctionMapper() {
        return this.functionMapper;
    }

    public VariableMapper getVariableMapper() {
        if (this.variableMapper == null) {
            this.variableMapper = new ELContextImpl.VariableMapperImpl();
        }

        return this.variableMapper;
    }

    public void setFunctionMapper(FunctionMapper functionMapper) {
        this.functionMapper = functionMapper;
    }

    public void setVariableMapper(VariableMapper variableMapper) {
        this.variableMapper = variableMapper;
    }

    private static final class VariableMapperImpl extends VariableMapper {
        private Map<String, ValueExpression> vars;

        private VariableMapperImpl() {
        }

        public ValueExpression resolveVariable(String variable) {
            return this.vars == null ? null : (ValueExpression)this.vars.get(variable);
        }

        public ValueExpression setVariable(String variable, ValueExpression expression) {
            if (this.vars == null) {
                this.vars = new HashMap();
            }

            return (ValueExpression)this.vars.put(variable, expression);
        }
    }
}

