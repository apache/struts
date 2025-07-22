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

/**
 * StrutsContext is Struts specific implementation of {@link OgnlContext}
 *
 * @since 7.1.0
 */
public class StrutsContext extends OgnlContext<StrutsContext> {

    private StrutsContext(
            MemberAccess<StrutsContext> memberAccess,
            ClassResolver<StrutsContext> classResolver,
            TypeConverter<StrutsContext> typeConverter
    ) {
        super(memberAccess, classResolver, typeConverter);
    }

    public static StrutsContext of(StrutsContext context) {
        StrutsContext strutsContext = new Builder()
                .withMemberAccess(context.getMemberAccess())
                .withClassResolver(context.getClassResolver())
                .withTypeConverter(context.getTypeConverter())
                .build();
        strutsContext.setValues(context.getValues());
        return strutsContext;
    }

    public static StrutsContext of(Object root, MemberAccess<StrutsContext> memberAccess) {
        return new Builder()
                .withMemberAccess(memberAccess)
                .withRoot(root)
                .build();
    }

    public static StrutsContext of(Object root, ClassResolver<StrutsContext> resolver, TypeConverter<StrutsContext> converter, MemberAccess<StrutsContext> memberAccess) {
        return new StrutsContext.Builder()
                .withMemberAccess(memberAccess)
                .withClassResolver(resolver)
                .withTypeConverter(converter)
                .withRoot(root)
                .build();
    }

    public static class Builder extends OgnlContext.Builder<StrutsContext> {

        private Builder() {
            super(b -> new StrutsContext(b.getMemberAccess(), b.getClassResolver(), b.getTypeConverter()));
            Ognl.withBuilderProvider(this);
        }

    }
}
