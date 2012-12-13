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

package org.apache.struts2.config_browser;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.validator.ActionValidatorManager;
import com.opensymphony.xwork2.validator.Validator;

import java.util.Collections;
import java.util.List;

/**
 * ListValidatorsAction loads the validations for a given class and context
 *
 */
public class ListValidatorsAction extends ActionSupport {

    private static final long serialVersionUID = 1L;

    private String clazz;
    private String context;
    List<Validator> validators = Collections.emptyList();
    private ActionValidatorManager actionValidatorManager;

    
    @Inject
    public void setActionValidatorManager(ActionValidatorManager mgr) {
        this.actionValidatorManager = mgr;
    }
    
    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String stripPackage(Class clazz) {
        return clazz.getName().substring(clazz.getName().lastIndexOf('.') + 1);
    }

    public String stripPackage(String clazz) {
        return clazz.substring(clazz.lastIndexOf('.') + 1);
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public List getValidators() {
        return validators;
    }

    public String execute() throws Exception {
        loadValidators();
        return super.execute();
    }

    /**
     * Index action to support cooperation with REST plugin
     *
     * @return action result
     * @throws Exception
     */
    public String index() throws Exception {
        return execute();
    }

    protected void loadValidators() {
        Class value = getClassInstance();
        if ( value != null ) {
            validators = actionValidatorManager.getValidators(value, context);
        }
    }

    private Class getClassInstance() {
        try {
            return ClassLoaderUtil.loadClass(clazz, ActionContext.getContext().getClass());
        } catch (Exception e) {
            LOG.error("Class '" + clazz + "' not found...",e);
        }
        return null;
    }
}
