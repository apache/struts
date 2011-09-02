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

package org.apache.struts2.s1;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForward;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.config.ExceptionConfig;
import org.apache.struts.config.ForwardConfig;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;

import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

/**
 * Wrapper for a Struts 1.x ActionMapping based on an XWork ActionConfig.  Using a wrapper object
 * allows us to be explicit about what is and isn't implemented.
 */
class WrapperActionMapping extends ActionMapping {

    private ActionConfig delegate;
    private String actionPath;
    private String attribute;
    private Struts1Factory strutsFactory;

    public WrapperActionMapping(Struts1Factory factory, ActionConfig delegate) {
        this.delegate = delegate;
        this.strutsFactory = factory;
        forwards = null;
        exceptions = null;
    }

    public WrapperActionMapping(Struts1Factory factory, ActionConfig delegate, String actionPath, ModuleConfig moduleConfig) {
        this(factory, delegate);
        this.moduleConfig = moduleConfig;
        this.actionPath = actionPath;
    }

    /**
     * Add Struts ForwardConfigs (from XWork ResultConfigs).
     */
    private void initActionForwards() {
        if (forwards == null) {
            forwards = new HashMap();
            Map results = delegate.getResults();
            for (Iterator i = results.entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry) i.next();
                ActionForward wrapper = strutsFactory.createActionForward((ResultConfig) entry.getValue());
                forwards.put(wrapper.getName(), wrapper);
            }
        }
    }
                
    /**
     * Add XWork ExceptionConfigs (from XWork ExceptionMappingConfigs)
     */
    private void initExceptionConfigs() {
        if (exceptions == null) {
            exceptions = new HashMap();
            List exceptionMappings = delegate.getExceptionMappings();
            for (Iterator i = exceptionMappings.iterator(); i.hasNext();) {
                ExceptionConfig wrapper = strutsFactory.createExceptionConfig((ExceptionMappingConfig) i.next());
                exceptions.put(wrapper.getType(), wrapper);
            }
        }
    }

    public ActionForward findForward(String name) {
        initActionForwards();
        return super.findForward(name);
    }

    public String[] findForwards() {
        initActionForwards();
        return super.findForwards();
    }

    public ForwardConfig findForwardConfig(String name) {
        initActionForwards();
        return super.findForwardConfig(name);
    }

    public ForwardConfig[] findForwardConfigs() {
        initActionForwards();
        return super.findForwardConfigs();
    }

    public ExceptionConfig findExceptionConfig(String type) {
        initExceptionConfigs();
        return super.findExceptionConfig(type);
    }

    public ExceptionConfig[] findExceptionConfigs() {
        initExceptionConfigs();
        return super.findExceptionConfigs();
    }

    public ExceptionConfig findException(Class type) {
        initExceptionConfigs();
        return super.findException(type);
    }

    public ActionForward getInputForward() {
        throw new UnsupportedOperationException("NYI");
    }

    public ModuleConfig getModuleConfig() {
        if (moduleConfig == null) {
            moduleConfig = strutsFactory.createModuleConfig(delegate.getPackageName());
        }

        return moduleConfig;
    }

    public void setModuleConfig(ModuleConfig moduleConfig) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getForward() {
        throw new UnsupportedOperationException("NYI");
    }

    public void setForward(String forward) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public String getInclude() {
        throw new UnsupportedOperationException("NYI");
    }

    public void setInclude(String include) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public String getInput() {
        throw new UnsupportedOperationException("NYI");
    }

    public void setInput(String input) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public String getMultipartClass() {
        throw new UnsupportedOperationException("NYI");
    }

    public void setMultipartClass(String multipartClass) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public String getName() {
        // Note: in Struts, this is a name reference to a form bean defined in the config file.
        throw new UnsupportedOperationException("NYI");
    }

    public void setName(String name) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public String getParameter() {
        throw new UnsupportedOperationException("NYI");
    }

    public void setParameter(String parameter) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public String getPath() {
        return actionPath;
    }

    public void setPath(String path) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public String getPrefix() {
        throw new UnsupportedOperationException("NYI");
    }

    public void setPrefix(String prefix) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public String getRoles() {
        throw new UnsupportedOperationException("NYI");
    }

    public void setRoles(String roles) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public String[] getRoleNames() {
        throw new UnsupportedOperationException("NYI");
    }

    public String getScope() {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public void setScope(String scope) {
        throw new UnsupportedOperationException("NYI");
    }

    public String getSuffix() {
        throw new UnsupportedOperationException("NYI");
    }

    public void setSuffix(String suffix) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public String getType() {
        throw new UnsupportedOperationException("NYI");
    }

    public void setType(String type) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public boolean getUnknown() {
        throw new UnsupportedOperationException("NYI");
    }

    public void setUnknown(boolean unknown) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public boolean getValidate() {
        throw new UnsupportedOperationException("NYI");
    }

    public void setValidate(boolean validate) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public void removeExceptionConfig(ExceptionConfig config) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public void removeForwardConfig(ForwardConfig config) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public void addExceptionConfig(ExceptionConfig config) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public void addForwardConfig(ForwardConfig config) {
        throw new UnsupportedOperationException("Not implemented - immutable");
    }

    public String toString() {
        return "wrapper -> " + delegate.toString();
    }
}
