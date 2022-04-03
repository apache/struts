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
package com.opensymphony.xwork2.conversion;

import java.lang.reflect.Member;
import java.util.Map;

/**
 * Interface for accessing the type conversion facilities within a context.
 *
 * This interface was copied from OGNL's TypeConverter
 *
 * @author Luke Blanshard (blanshlu@netscape.net)
 * @author Drew Davidson (drew@ognl.org)
 */
public interface TypeConverter {
    /**
     * Converts the given value to a given type.  The OGNL context, target, member and
     * name of property being set are given.  This method should be able to handle
     * conversion in general without any context, target, member or property name specified.
     *
     * @param context      context under which the conversion is being done
     * @param target       target object in which the property is being set
     * @param member       member (Constructor, Method or Field) being set
     * @param propertyName property name being set
     * @param value        value to be converted
     * @param toType       type to which value is converted
     * @return Converted value of type toType or TypeConverter.NoConversionPossible to indicate that the
     * conversion was not possible.
     */
    Object convertValue(Map<String, Object> context, Object target, Member member, String propertyName, Object value, Class toType);

    Object NO_CONVERSION_POSSIBLE = "ognl.NoConversionPossible";

    @Deprecated
    String TYPE_CONVERTER_CONTEXT_KEY = "_typeConverter";

}
