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
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

/**
 * Base class for control and data tags
 */
public abstract class ContextBean extends Component {
    protected String var;
    
    public ContextBean(ValueStack stack) {
        super(stack);
    }

    protected void putInContext(Object value) {
        if (StringUtils.isNotBlank(var)) {
            stack.getContext().put(var, value);
        }
    }
    
    @StrutsTagAttribute(description="Name used to reference the value pushed into the Value Stack")
    public void setVar(String var) {
        if (var != null) {
            this.var = findString(var);
        }
    }
    
    protected String getVar() {
        return this.var;
    }
}
