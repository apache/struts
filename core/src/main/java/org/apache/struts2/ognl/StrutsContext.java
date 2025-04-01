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
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.TypeConverter;

import java.util.Map;

/**
 * StrutsContext is Struts specific implementation of {@link OgnlContext}
 *
 * @since 7.1.0
 */
public class StrutsContext extends OgnlContext<StrutsContext> {

    private StrutsContext(
            MemberAccess<StrutsContext> memberAccess,
            ClassResolver<StrutsContext> classResolver,
            TypeConverter<StrutsContext> typeConverter,
            StrutsContext initialContext
    ) {
        super(memberAccess, classResolver, typeConverter, initialContext);
    }

    public static StrutsContext of(StrutsContext context) {
        return new StrutsContext(context.getMemberAccess(), context.getClassResolver(), context.getTypeConverter(), context);
    }

    public static StrutsContext of(Map<String, Object> context) {
        return StrutsContext.of(Ognl.createDefaultContext(null, context));
    }

    public static StrutsContext empty() {
        return StrutsContext.of(Ognl.createDefaultContext(null));
    }
}
