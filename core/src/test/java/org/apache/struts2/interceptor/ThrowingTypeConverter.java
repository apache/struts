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
package org.apache.struts2.interceptor;

import org.apache.struts2.conversion.TypeConversionException;
import org.apache.struts2.conversion.impl.DefaultTypeConverter;

import java.util.Map;

/**
 * Test converter that always fails, used to reproduce WW-3427 (conversion errors on aliased
 * properties must still be reported).
 */
public class ThrowingTypeConverter extends DefaultTypeConverter {

    @Override
    public Object convertValue(Map<String, Object> context, Object value, Class toType) {
        throw new TypeConversionException("intentional conversion failure for value: " + value);
    }
}
