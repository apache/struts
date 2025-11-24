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
package org.apache.struts2.ognl;

import ognl.ClassResolver;
import ognl.MemberAccess;
import ognl.OgnlContext;
import ognl.TypeConverter;

/**
 * Struts-specific extension of OgnlContext providing type-safe context operations.
 * This class serves as a compatibility layer for OGNL 3.4.8+ which requires OgnlContext
 * instead of raw Map for context parameters.
 *
 * @since 7.2.0
 */
public class StrutsContext extends OgnlContext {

    /**
     * Creates a new StrutsContext with the specified configuration.
     *
     * @param classResolver the class resolver
     * @param typeConverter the type converter
     * @param memberAccess  the member access policy
     */
    public StrutsContext(ClassResolver classResolver, TypeConverter typeConverter, MemberAccess memberAccess) {
        super(classResolver, typeConverter, memberAccess);
    }

    /**
     * Wraps an existing OgnlContext as a StrutsContext.
     *
     * @param context the OgnlContext to wrap
     * @return a StrutsContext instance
     */
    public static StrutsContext wrap(OgnlContext context) {
        if (context instanceof StrutsContext) {
            return (StrutsContext) context;
        }
        StrutsContext strutsContext = new StrutsContext(
                context.getClassResolver(),
                context.getTypeConverter(),
                context.getMemberAccess()
        );
        strutsContext.setRoot(context.getRoot());
        strutsContext.putAll(context);
        return strutsContext;
    }
}
