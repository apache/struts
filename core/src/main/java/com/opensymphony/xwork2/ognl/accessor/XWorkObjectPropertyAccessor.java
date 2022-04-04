/*
 * Copyright 2002-2006,2009 The Apache Software Foundation.
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
package com.opensymphony.xwork2.ognl.accessor;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;

import java.util.Map;

/**
 * @author Gabe
 */
public class XWorkObjectPropertyAccessor extends ObjectPropertyAccessor {
    @Override
    public Object getProperty(Map context, Object target, Object oname)
            throws OgnlException {
        //set the last set objects in the context
        //so if the next objects accessed are
        //Maps or Collections they can use the information
        //to determine conversion types
        context.put(XWorkConverter.LAST_BEAN_CLASS_ACCESSED, target.getClass());
        context.put(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED, oname.toString());
        ReflectionContextState.updateCurrentPropertyPath(context, oname);
        return super.getProperty(context, target, oname);
    }
}
