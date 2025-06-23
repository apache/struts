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

import org.apache.struts2.conversion.TypeConverter;

import java.lang.reflect.Member;

/**
 * Wraps an OGNL TypeConverter as an XWork TypeConverter
 */
public class XWorkTypeConverterWrapper implements TypeConverter<StrutsContext> {

    private final ognl.TypeConverter<StrutsContext> typeConverter;

    public XWorkTypeConverterWrapper(ognl.TypeConverter<StrutsContext> conv) {
        this.typeConverter = conv;
    }

    @Override
    public Object convertValue(StrutsContext context, Object target, Member member, String propertyName, Object value, Class<?> toType) {
        return typeConverter.convertValue(context, target, member, propertyName, value, toType);
    }
}
