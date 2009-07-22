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

import java.io.Serializable;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import ognl.OgnlException;
import ognl.PropertyAccessor;
import ognl.OgnlContext;

import java.util.Map;

/**
 * Provides access to DynaBean properties in OGNL
 */
public class DynaBeanPropertyAccessor implements PropertyAccessor {
    /**
     * Used by OGNL to generate bytecode
     */
    public String getSourceAccessor(OgnlContext ognlContext, Object o, Object o1) {
        return null;
    }

    /**
     * Used by OGNL to generate bytecode
     */
    public String getSourceSetter(OgnlContext ognlContext, Object o, Object o1) {
        return null;  
    }

    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        
        if (target instanceof DynaBean && name != null) {
            DynaBean bean = (DynaBean)target;
            DynaClass cls = bean.getDynaClass();
            String key = name.toString();
            if (cls.getDynaProperty(key) != null) {
                return bean.get(key);
            }
        }
        return null;
    }    

    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        
        if (target instanceof DynaBean && name != null) {
            DynaBean bean = (DynaBean)target;
            String key = name.toString();
            bean.set(key, value);
        }
    }    

}
