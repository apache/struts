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
package org.apache.struts2.views.jsp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Set;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * @see Set
 */
public class SetTag extends ContextBeanTag {

    private static final long serialVersionUID = -5074213926790716974L;

    protected String scope;
    protected String value;
    protected boolean trimBody = true;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Set(stack);
    }

    @Override
    protected void populateParams() {
        super.populateParams();

        Set set = (Set) component;
        set.setScope(scope);
        set.setValue(value);
    }

    public void setName(String name) {
       setVar(name);
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setTrimBody(boolean trimBody) {
        this.trimBody = trimBody;
}

    @Override
    protected String getBody() {
        if (trimBody) {
            if (bodyContent == null) {
                return null;
            } else {
                return bodyContent.getString().trim();
            }
        } else {
            return (bodyContent == null ? null : bodyContent.getString());
        }
    }

    @Override
    /**
     * Must declare the setter at the descendant Tag class level in order for the tag handler to locate the method.
     */
    public void setPerformClearTagStateForTagPoolingServers(boolean performClearTagStateForTagPoolingServers) {
        super.setPerformClearTagStateForTagPoolingServers(performClearTagStateForTagPoolingServers);
    }

    @Override
    protected void clearTagStateForTagPoolingServers() {
       if (getPerformClearTagStateForTagPoolingServers() == false) {
            return;  // If flag is false (default setting), do not perform any state clearing.
        }
        super.clearTagStateForTagPoolingServers();
        this.scope = null;
        this.value = null;
        this.trimBody = true;
     }

}
