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
package org.apache.struts2.validator.validators;

import org.apache.struts2.conversion.TypeConversionException;
import org.apache.struts2.conversion.impl.DefaultTypeConverter;

import java.lang.reflect.Member;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

/**
 * Converts the {@code min} / {@code max} params of {@link LocalDateRangeFieldValidator} strictly as ISO dates,
 * mapped in {@code LocalDateRangeFieldValidator-conversion.properties}, so a bound does not depend on the
 * application's {@code struts.date.format}.
 *
 * @since 7.5.0
 */
public final class LocalDateRangeBoundConverter extends DefaultTypeConverter {

    @Override
    public Object convertValue(Map<String, Object> context, Object target, Member member, String propertyName,
                               Object value, Class toType) {
        if (value instanceof String bound) {
            try {
                return LocalDate.parse(bound.trim());
            } catch (DateTimeParseException e) {
                throw new TypeConversionException("Could not parse " + propertyName + " as an ISO date (yyyy-MM-dd)", e);
            }
        }
        return value;
    }

}
